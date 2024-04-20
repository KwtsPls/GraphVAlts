package gr.uoa.di.entities.viewSelection._steps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.interfaceAdapters.controllers.randomAccessDiskMap.patterns.RNDAccessPatterns;
import gr.uoa.di.interfaceAdapters.debug.MyDebug;
import gr.uoa.di.usecases.constants.materialization.ConstantsGlobal;

public class Step_TestSet_Creation {

	public static void create(ConstantsGlobal constants, int outputSize) throws IOException {
		Dictionary dict = constants.deserializeDictionary();
		Random random = new Random(25);
		if (constants.queriesForTestingExist()) {
			try (RNDAccessPatterns queriesForTesting = constants.getQueriesForTesting(dict)) {
				if (queriesForTesting.size() == outputSize) {
					MyDebug.println("Skipping...");
					return;
				} else {
					constants.deleteQueriesForTesting();
				}
			}
		}
		//
		try (RNDAccessPatterns queriesForTesting = constants.getQueriesForTesting(dict);
				RNDAccessPatterns queries = constants.getQueryMap(dict);) {
			// We cannot create a test set larger than the original se of queries
			outputSize = Math.min(queries.size(), outputSize);
			// Creating the queryIds
			List<Integer> queryIds = new ArrayList<>(queries.keySet());
			for (int i = queryIds.size(); queriesForTesting.size() < outputSize && i > 0; i--) {
				int nextRand = random.nextInt(i);
				int id = queryIds.get(nextRand);
				if (queries.get(id).getGraph() != null) {
					queriesForTesting.put(id, queries.get(id));
				}
				queryIds.set(nextRand, queryIds.get(i - 1));
			}
			MyDebug.printAndLog(() -> "Queries for testing: #" + queriesForTesting.size());
		}
	}

}
