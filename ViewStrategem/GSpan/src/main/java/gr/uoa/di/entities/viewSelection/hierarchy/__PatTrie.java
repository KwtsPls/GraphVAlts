package gr.uoa.di.entities.viewSelection.hierarchy;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import gr.uoa.di.entities.graph.PatternVS;
import gr.uoa.di.entities.trie.TrieIndex;
import gr.uoa.di.interfaceAdapters.controllers.randomAccessDiskMap.patterns.RNDAccessPatterns;

public class __PatTrie extends TrieIndex<PatternVS, __PatTrieMetadata> {
	// Benefited Queries
	private Set<Integer> benefitedQueries = new HashSet<>();
	private int size = 0;

	__PatTrie(Iterator<PatternVS> patterns, RNDAccessPatterns groupedQueries) {
		TreeSet<Integer> usedGroupQueries = new TreeSet<>();
		int meter = 0;
		while (patterns.hasNext()) {
			PatternVS pattern = patterns.next();
			/**
			 * Ignore patterns that contain no variables; ignore patterns that contain no
			 * edges; ignore patterns that contain one edge and two variables - since they
			 * correspond to the initial database relations.
			 */
			if (pattern.getGraph().getVarNodeCount() == 0 || pattern.getGraph().getEdgeCount() == 0
					|| pattern.getGraph().getEdgeCount() == 1) {
				continue;
			}
			meter++;
			/** Logging out all patterns */
			this.insertQuery(pattern.getGraph(), pattern);
			usedGroupQueries.addAll(pattern.getWhere());
		}
		System.out.println("Initially the Patterns are:" + meter);
		forEachMetadataEntry(entry -> {
			entry.updateSupport(groupedQueries);
			size++;
		});
		usedGroupQueries.forEach(gId -> {
			benefitedQueries.addAll(groupedQueries.get(gId).getWhere());
		});
	}

	@Override
	public __PatTrieMetadata createMetadataInstance() {
		return new __PatTrieMetadata();
	}

	public Set<Integer> getBenefitedQueries() {
		Set<Integer> output = benefitedQueries;
		benefitedQueries = null;
		return output;
	}

	public int getPatternCount() {
		return size;
	}
}
