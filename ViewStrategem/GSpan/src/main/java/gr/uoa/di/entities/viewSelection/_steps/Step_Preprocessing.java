package gr.uoa.di.entities.viewSelection._steps;

import java.io.IOException;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.GraphVS;
import gr.uoa.di.entities.graph.PatternVS;
import gr.uoa.di.entities.gspan.infrequentLabelRemoval.GSPanPreprocessor;
import gr.uoa.di.interfaceAdapters.controllers.randomAccessDiskMap.patterns.RNDAccessPatterns;
import gr.uoa.di.interfaceAdapters.debug.MyDebug;
import gr.uoa.di.interfaceAdapters.workloads.JenaGraphQueryIterator;
import gr.uoa.di.translators.cypher.CypherQuery;
import gr.uoa.di.translators.cypher.CypherTranslator;
import gr.uoa.di.usecases.constants.materialization.ConstantsGlobal;

public class Step_Preprocessing {

	public static void createLabelStatistics(ConstantsGlobal constants) {
		new Step_Preprocessing()._createLabelStatistics(constants);
	}

	private void _createLabelStatistics(ConstantsGlobal constants) {
		if (constants.dictionaryExists()) {
			MyDebug.println("Skipping...");
			return;
		}
		GSPanPreprocessor preprocessor = GSPanPreprocessor.create();
		Dictionary dict = Dictionary.create();
		// Scans the file and keeps statistics about frequent triples
		try (JenaGraphQueryIterator<GraphVS> iter = constants.getJenaQueryIterator(dict);
				RNDAccessPatterns queries = constants.getQueryMap(dict);) {

			int[] meter = { 0 };
			while (iter.hasNext()) {
				GraphVS graph = iter.next();
				if (graph == null)
					continue;
				preprocessor.createLabelStatistics(graph);
				PatternVS pattern = PatternVS.create(graph, meter[0], 1, new Integer[] { meter[0] });
				queries.put(meter[0], pattern);
				pattern.setSupport(1);
				meter[0]++;
			}

			JenaGraphQueryIterator<GraphVS> secIter = constants.getSecondaryJenaQueryIterator(dict);
			while (secIter.hasNext()) {
				GraphVS graph = secIter.next();
				if (graph == null)
					continue;
				preprocessor.createLabelStatistics(graph);
				PatternVS pattern = PatternVS.create(graph, meter[0], 1, new Integer[] { meter[0] });
				queries.put(meter[0], pattern);
				pattern.setSupport(1);
				meter[0]++;
			}

			MyDebug.printAndLog(() -> "# Queries Within the workload:" + queries.size());
			constants.serializeGSPanPreprocessor(preprocessor);
			constants.serializeDictionary(dict);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
