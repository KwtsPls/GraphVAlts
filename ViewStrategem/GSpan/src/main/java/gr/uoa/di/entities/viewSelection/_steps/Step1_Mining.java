package gr.uoa.di.entities.viewSelection._steps;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.gspan.gspanGraph.GSpanGraph;
import gr.uoa.di.entities.gspan.gspanImplementation.GSpanWeightedExecutor;
import gr.uoa.di.entities.gspan.infrequentLabelRemoval.GSPanPreprocessor;
import gr.uoa.di.interfaceAdapters.controllers.formatters.gSpan.serialization.GSpanSerializer;
import gr.uoa.di.interfaceAdapters.controllers.formatters.gSpan.translators.GSpanInvTranslator;
import gr.uoa.di.interfaceAdapters.controllers.randomAccessDiskMap.patterns.RNDAccessPatterns;
import gr.uoa.di.interfaceAdapters.debug.MyDebug;
import gr.uoa.di.usecases.constants.experiments.ConstantForExperiments;
import gr.uoa.di.usecases.constants.materialization.ConstantsSharable;

public class Step1_Mining {

	public static <C extends ConstantsSharable & ConstantForExperiments> void mineFrequentPatterns(C constants)
			throws Exception, IOException {
		new Step1_Mining().mineFrequentPatterns2(constants);
	}

	private <C extends ConstantsSharable & ConstantForExperiments> void mineFrequentPatterns2(C constants)
			throws JsonGenerationException, JsonMappingException, IOException {
		if (constants.frequentPatternsExist()) {
			MyDebug.println("Skipping...");
			return;
		}
		Dictionary dict = constants.deserializeDictionary();
		//
		GSPanPreprocessor preprocessor = constants.deserializeGSPanPreprocessor();
		int minSup = constants.getMinimumSupport();
		/**
		 * The freqGraphs HashMap will be used to store simplified graphs along with the
		 * frequency they appear
		 */
		HashMap<GSpanGraph, List<Integer>> freqGraphs = null;
		try (RNDAccessPatterns queries = constants.getQueryMap(dict);) {
			freqGraphs = preprocessor.mergeGraphs(queries.valueIterator(), minSup);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/** Mining is performed on grouped queries */
		try (RNDAccessPatterns groupedQueries = constants.getGroupedQueryMap(dict);) {
			int[] counter = new int[] { 0 };
			GSpanInvTranslator deserializer = GSpanInvTranslator.create(dict);
			freqGraphs.forEach((gSpan, listOfQueryIds) -> {
				// Step1: writes graphs to rndAccessIds
				gSpan.support = listOfQueryIds.size();
				Integer[] intArray = new Integer[listOfQueryIds.size()];
				intArray = listOfQueryIds.toArray(intArray);
				gSpan.where = intArray;
				gSpan.id = counter[0];
				groupedQueries.put(counter[0]++, deserializer.translate(gSpan));
			});
			/** Printing grouped patterns with frequency */
			MyDebug.logHeader(() -> "Grouped Queries with frequencies");
			groupedQueries.forEach((id, pattern) -> {
				MyDebug.log(() -> "\t" + pattern.toCompactString());
			});

			/** Writes Gspan graphs to the corresponding Gspan format */
			GSpanSerializer serializer = GSpanSerializer.create(constants.getGSpanFormatter().toString());
			serializer.serializeCollection(groupedQueries.valueIterator());
			/**
			 * The GSpan algorithm is executed on the dbpediaGraphFile and the execution
			 * results are stored to the dbpediaPatternFile
			 */
			GSpanWeightedExecutor.run(constants.getGSpanFormatter(), new File(constants.getPatternLocation()), minSup,
					3, 10);
		}

	}

}
