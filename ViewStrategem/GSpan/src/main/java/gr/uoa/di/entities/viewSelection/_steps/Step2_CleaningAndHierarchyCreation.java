package gr.uoa.di.entities.viewSelection._steps;

import java.io.IOException;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.PatternVS;
import gr.uoa.di.entities.helpStructures.iterators.ClosableIterator;
import gr.uoa.di.entities.viewSelection.hierarchy.PatternHierarchy;
import gr.uoa.di.entities.viewSelection.hierarchy.__PatTrie;
import gr.uoa.di.interfaceAdapters.controllers.randomAccessDiskMap.patterns.RNDAccessPatterns;
import gr.uoa.di.interfaceAdapters.debug.MyDebug;
import gr.uoa.di.usecases.constants.experiments.ConstantForExperiments;
import gr.uoa.di.usecases.constants.materialization.ConstantsSharable;

public class Step2_CleaningAndHierarchyCreation {

	public static <C extends ConstantsSharable & ConstantForExperiments> void cleanPatternsAndCreateHierarchy(
			C constants, double tolerancePercentage) throws IOException {
		if (constants.existHieararchy()) {
			MyDebug.println("Skipping...");
			return;
		}
		// Creates a dictionary from the dictionary file
		Dictionary dict = constants.deserializeDictionary();
		int minSup = constants.getMinimumSupport();
		double tolerance = (int) (tolerancePercentage * minSup);
		try (RNDAccessPatterns groupedQueries = constants.getGroupedQueryMap(dict);
				ClosableIterator<PatternVS> patterns = constants.getPatternMap(dict);) {
			// Step 2 Create Trie
			__PatTrie trie = PatternHierarchy.create(patterns, groupedQueries);

			// Step 3: Create Pattern Hierarchy
			PatternHierarchy hierarchy = PatternHierarchy.create(trie);
			// Removing Automorphisms
			hierarchy.removeAutomorphisms();
			// Removing Non-ClosedPatterns
			hierarchy.removeNonClosedPatterns(tolerance);
			// Normalizing Support
			hierarchy.normalizeSupport();

			hierarchy.forEachPatternContainedPatterns((x, y) -> {
				System.out.println(x + "\t->\t" + y);

			});

			constants.serializeHierarchy(hierarchy, dict);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
