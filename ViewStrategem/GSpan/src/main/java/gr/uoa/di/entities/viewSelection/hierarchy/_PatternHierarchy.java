package gr.uoa.di.entities.viewSelection.hierarchy;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.BiConsumer;

import gr.uoa.di.entities.graph.NodeVS;
import gr.uoa.di.entities.graph.PatternVS;
import gr.uoa.di.entities.graph.TripleVS;
import gr.uoa.di.entities.helpStructures.iterators.TransformIterator;
import gr.uoa.di.entities.helpStructures.tuples.MyPair;
import gr.uoa.di.entities.trie.containment.mapping.ContainmentMapping;
import gr.uoa.di.entities.viewSelection.edgeRewriting.TripleMap;
import gr.uoa.di.interfaceAdapters.debug.MyDebug;

class _PatternHierarchy implements PatternHierarchy {

	/**
	 * The corresponding table, stores all the containment mappings from the first
	 * GraphPattern (its id is stored 1st) to the second GraphPattern (its id is
	 * stored 2nd). Each value is a list of containment mappings. Each containment
	 * mapping is represented as a list of int[] that represent triple mappings. For
	 * example, if a containment mapping maps the triples (a,b,c)->(d,e,f), the
	 * int[] value (a,b,c,d,e,f) will be stored.
	 */
	List<_PatternWrapper> patternWrappers;

	_PatternHierarchy() {

	}

	_PatternHierarchy(__PatTrie trie) {
		HashMap<PatternVS, _PatternWrapper> patterns2Wrappers = new HashMap<>();

		trie.forEachMetadataEntry(metadata -> {
			_PatternWrapper toWrapper = patterns2Wrappers.computeIfAbsent(metadata.getPattern(),
					x -> new _PatternWrapper(metadata.getPattern()));
			List<ContainmentMapping<NodeVS, TripleVS, __PatTrieMetadata>> output = trie
					.contains(metadata.getPattern().getGraph());
			output.forEach(mapping -> {
				PatternVS pattern = mapping.getMetadata().getPattern();
				_PatternWrapper fromWrapper = patterns2Wrappers.computeIfAbsent(pattern,
						x -> new _PatternWrapper(pattern));

				addContainment(fromWrapper, toWrapper, mapping);
			});
		});

		patternWrappers = new LinkedList<>(patterns2Wrappers.values());
		// Sorting pattern wrappers, the ones with the less edges appear first
		patternWrappers.sort(new Comparator<_PatternWrapper>() {
			@Override
			public int compare(_PatternWrapper o1, _PatternWrapper o2) {
				int output = Integer.compare(o1.getGraph().getEdgeCount(), o2.getGraph().getEdgeCount());
				if (output != 0)
					return output;
				output = -Integer.compare(o1.getGraph().getVarNodeCount(), o2.getGraph().getVarNodeCount());
				if (output != 0)
					return output;
				return output != 0 ? output : Integer.compare(o1.getPattern().getId(), o2.getPattern().getId());
			}
		});
		// Writing containments to log
		MyDebug.logHeader(() -> "PatternHierarchy");
		patternWrappers.forEach(wrapper -> {
			MyDebug.log(() -> "\t" + wrapper.getPattern());
			wrapper.patternsContained.forEach((containedPattern, mappings) -> {
				MyDebug.log(() -> "\t\t" + containedPattern.getPattern());
			});
		});
	}

	/**
	 * Removes form the hierarchy all patterns that contain multiple automorphisms.
	 * These patterns will produce very large views, thus are not examined.
	 */
	@Override
	public void removeAutomorphisms() {
		ListIterator<_PatternWrapper> iter = patternWrappers.listIterator();
		MyDebug.logHeader(() -> "Removing Automorphisms");
		while (iter.hasNext()) {
			_PatternWrapper pattern = iter.next();
			if (pattern.hasManyAutomorphisms()) {
				iter.remove();
				pattern.totallyRemove();
				MyDebug.log(() -> "\t" + pattern.getGraph());
			}
		}
	}

	/**
	 * Removes all patterns that contain a pattern with (almost) the same support.
	 * 
	 * @param tolerance if the difference of the supports between a pattern and its
	 *                  contained pattern is less than tolerance, than the pattern
	 *                  is removed.
	 */
	@Override
	public void removeNonClosedPatterns(double tolerance) {
		ListIterator<_PatternWrapper> iter = patternWrappers.listIterator();
		MyDebug.log(() -> "Removing Non-Closed Patterns");
		while (iter.hasNext()) {
			_PatternWrapper wrapper = iter.next();
			PatternVS pattern2 = wrapper.isNotClosed(tolerance);
			if (pattern2 != null) {
				iter.remove();
				wrapper.totallyRemove();
				MyDebug.log(() -> wrapper.getPattern() + " removed in favor of");
				MyDebug.log(() -> "\t" + pattern2.toCompactString());
			}
		}
	}

	/**
	 * This function normalizes the supports within the hierarchy, such that each
	 * pattern's support is the sum of supports of its contained patterns and its
	 * exclusive support (i.e. corresponding to support of queries that are
	 * contained in this pattern but not in any of its contained patterns).
	 * 
	 * @param sqlHelper
	 */
	@Override
	public void normalizeSupport() {
		NavigableMap<Integer, List<_PatternWrapper>> containmentsPerPattern = new TreeMap<>();
		for (_PatternWrapper patMeta : patternWrappers) {
			int count = patMeta.getContainedCount();
			containmentsPerPattern.computeIfAbsent(count, list -> new LinkedList<>()).add(patMeta);
		}
		containmentsPerPattern.forEach((count, list) -> {
			list.forEach(patternMeta -> {
				patternMeta.forEachContainedPattern(cntPatternMeta -> {
					supportNormalization(patternMeta, cntPatternMeta);
				});
			});

		});
	}

	private void supportNormalization(_PatternWrapper patternMeta, _PatternWrapper cntPatternMeta) {
		if (patternMeta == cntPatternMeta)
			return;
		if (cntPatternMeta.contains(patternMeta)) {
			System.err.println("Cyclic dpendencies are not Handled\n\t" + cntPatternMeta.getGraph().toCompactString());
		} else {
			patternMeta.getPattern().setSupport(
					Math.max(patternMeta.getPattern().getSupport() - cntPatternMeta.getPattern().getSupport(), 0));
		}
	}

	private static void addContainment(_PatternWrapper fromWrapper, _PatternWrapper toWrapper,
			ContainmentMapping<NodeVS, TripleVS, __PatTrieMetadata> mapping) {
		TripleMap edgeMapping = TripleMap.create(fromWrapper.getPattern(), mapping);
		fromWrapper.addContains(toWrapper, edgeMapping);
		toWrapper.addIsContainedIn(fromWrapper, edgeMapping);
	}

	@Override
	public int getPatternCount() {
		return patternWrappers.size();
	}

	@Override
	public void forEachPatternContainedPatterns(
			BiConsumer<PatternVS, Iterator<MyPair<PatternVS, List<TripleMap>>>> consumer) {
		patternWrappers.forEach(wrapper -> {
			PatternVS pattern = wrapper.getPattern();

			Iterator<Entry<_PatternWrapper, List<TripleMap>>> iter = wrapper.patternsContained.entrySet().iterator();
			var newIter = TransformIterator.create(iter, x -> {
				return MyPair.of(x.getKey().getPattern(), x.getValue());
			});
			consumer.accept(pattern, newIter);
		});

	}

}
