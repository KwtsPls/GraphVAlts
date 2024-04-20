package gr.uoa.di.entities.viewSelection.hierarchy;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import gr.uoa.di.entities.graph.GraphVS;
import gr.uoa.di.entities.graph.PatternVS;
import gr.uoa.di.entities.graph.QueryVS;
import gr.uoa.di.entities.viewSelection.edgeRewriting.TripleMap;

public class _PatternWrapper {
	PatternVS pattern;
	private QueryVS query = null;
	HashMap<_PatternWrapper, List<TripleMap>> patternsContained = new HashMap<>();
	private HashMap<_PatternWrapper, List<TripleMap>> patternsContaining = new HashMap<>();

	_PatternWrapper(PatternVS pattern) {
		this.pattern = pattern;
	}

	public PatternVS getPattern() {
		return pattern;
	}

	public void addContains(_PatternWrapper gpMetadata, TripleMap mapping) {
		patternsContained.computeIfAbsent(gpMetadata, edgemapping -> new LinkedList<>()).add(mapping);

	}

	public void addIsContainedIn(_PatternWrapper gpMetadata, TripleMap mapping) {
		patternsContaining.computeIfAbsent(gpMetadata, edgemapping -> new LinkedList<>()).add(mapping);
	}

	public String toString() {
		StringBuilder builder = new StringBuilder(pattern.getGraph().toCompactString())
				.append("\nContained Patterns:\n");
		patternsContained.forEach((pattern, mappingList) -> {
			builder.append("\t").append(pattern.getGraph().toCompactString()).append("\n");
			mappingList.forEach(mapping -> builder.append("\t\t").append(mapping).append("\n"));
		});
		return builder.toString();
	}

	public boolean hasManyAutomorphisms() {
		List<TripleMap> allMappings = patternsContained.get(this);
		if (allMappings==null || allMappings.size() == 1) {
			return false;
		} else {
			int isoCounter = 0;
			for (TripleMap mapping : allMappings) {
				if (mapping.isIsomorphic()) {
					isoCounter++;
				}
			}
			if (isoCounter > 1) {
				return true;
			}
			return false;
		}
	}

	public void totallyRemove() {
		patternsContained.forEach((metadata, mapList) -> {
			metadata.removeContainingPattern(this);
		});
		patternsContaining.forEach((metadata, mapList) -> {
			metadata.removeContainedPattern(this);
		});

	}

	private void removeContainingPattern(_PatternWrapper patternMetadata) {
		patternsContaining.remove(patternMetadata);
	}

	private void removeContainedPattern(_PatternWrapper patternMetadata) {
		patternsContained.remove(patternMetadata);
	}

	/**
	 * @param tolerance the tolerance for considering a pattern as closed
	 * @return the pattern that causes the current pattern not to be closed 
	 */
	public PatternVS isNotClosed(double tolerance) {
		for (_PatternWrapper containedPattern : patternsContained.keySet()) {
			PatternVS pattern1 = this.getPattern();
			PatternVS pattern2 = containedPattern.getPattern();
			if (pattern1 == pattern2)
				continue;
			int diff = pattern1.getSupport() - pattern2.getSupport();
			if(diff<0) {
				System.err.println(pattern1 +" \n\t"+pattern2);
			}
			if (diff < tolerance) {
				return pattern2;
			}
		}
		return null;
	}

	public int getContainedCount() {
		return patternsContained.size();
	}

	public void forEachContainedPattern(Consumer<_PatternWrapper> consumer) {
		patternsContained.forEach((meta, mapping) -> {
			consumer.accept(meta);
		});

	}

	public boolean contains(_PatternWrapper patternMeta) {
		return patternsContained.containsKey(patternMeta);
	}

	public void forEachWrapperContainment(BiConsumer<_PatternWrapper, List<TripleMap>> consumer) {
		patternsContained.forEach((containedPattern, mappings) -> {
			consumer.accept(containedPattern, mappings);
		});

	}

	public GraphVS getGraph() {
		return pattern.getGraph();
	}

	
	public QueryVS getQueryVS() {
		if (query == null) {
			query = QueryVS.create(pattern);
		}
		return query;
	}

}
