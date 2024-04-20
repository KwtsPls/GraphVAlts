package gr.uoa.di.usecases;

import java.io.IOException;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.interfaceAdapters.controllers.randomAccessDiskMap.patterns.RNDAccessPatterns;
import gr.uoa.di.interfaceAdapters.debug.MyDebug;
import gr.uoa.di.usecases.constants.dbpedia.DBPediaPrimordialConstants;
import gr.uoa.di.usecases.constants.experiments.ConstantForExperiments;
import gr.uoa.di.usecases.constants.materialization.Constants;

class CheckTransformations { // NO_UCD (unused code)

	static double tolerancePercentage = 0.1;
	///////////////

	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
		for (int minSup : new int[] { 500, 1000 }) {
			for (int availableSize : new int[] { 1000, 10000 }) {
				for (boolean primordial : new boolean[] { false, true }) {
					checkTransformations(DBPediaPrimordialConstants.create("tmp/", minSup), minSup, availableSize,
							primordial);

				}
			}
		}
	}

	static <C extends Constants & ConstantForExperiments> void checkTransformations(C constants, int minSup,
			int availableSize, boolean primordial) throws JsonGenerationException, JsonMappingException, IOException {
		Dictionary dict = constants.deserializeDictionary();
		try (RNDAccessPatterns tmp = constants.getGroupedQueryMap(dict);
				RNDAccessPatterns queries = constants.getQueryMap(dict);) {
			tmp.forEach((id, patt) -> {
				if (patt.getGraph().getEdgeCount() > 1)
					return;
				MyDebug.println("-----------------> " + id);
				MyDebug.println(patt);
				MyDebug.println("*------------------*");
				Set<Integer> where = patt.getWhere();
				MyDebug.println(where);
			});
		}

	}

}
