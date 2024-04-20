package gr.uoa.di.entities.trie;

import java.util.Arrays;

import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForGraph;
import gr.uoa.di.entities.graph.serialization.LinearRewriter;

class _TrieInsertion {

	static <I, M extends TrieMetadata<I>> TrieVertex<I, M> insertQuery(TrieIndex<I,M> index, TrieVertex<I, M> vertex,
			AbstractionForGraph<?, ?, ?> graph, I info) {
		int[] serializedQuery = LinearRewriter.linearize(graph).toArray();
		TrieVertex<I, M> output = insertSerializedQuery(vertex, serializedQuery, info);
		(output.metadata = (output.metadata == null) ? index.createMetadataInstance(): output.metadata).updateOn(info);
		return output;
	}

	static <I, M extends TrieMetadata<I>> TrieVertex<I, M> insertQueryPattern(TrieIndex<I,M> index, TrieVertex<I, M> vertex,
																	   AbstractionForGraph<?, ?, ?> graph, I info) {
		int[] serializedQuery = LinearRewriter.linearize(graph).toArray();
		return insertSerializedQuery(vertex, serializedQuery, info);
	}

	private static <I, M extends TrieMetadata<I>> TrieVertex<I, M> insertSerializedQuery(TrieVertex<I, M> trie,
			int[] serializedQuery, I info) {
		TrieVertex<I, M> output = insertionSerializedQuery(trie, serializedQuery, 0);
		output.isInserted = true;
		return output;
	}

	private static <I, M extends TrieMetadata<I>> TrieVertex<I, M> insertionSerializedQuery(TrieVertex<I, M> trie,
			int[] serializedQuery, int cursor) {
		if (cursor == serializedQuery.length) {
			return trie;
		}
		Branch<I, M> branch = trie.get(serializedQuery[cursor]);
		if (branch == null) {
			return addBranch(trie, serializedQuery, cursor);
		} else {
			int[] branchLabels = branch.labels;
			int i = 0;
			while (true) {
				if (i == branchLabels.length) {
					if (cursor == serializedQuery.length)
						return branch.vertex;
					TrieVertex<I, M> vertex = branch.vertex;
					return insertionSerializedQuery(vertex, serializedQuery, cursor);
				} else if (cursor == serializedQuery.length) {
					int[] branchLabels1 = Arrays.copyOf(branchLabels, i);
					int[] branchLabels2 = Arrays.copyOfRange(branchLabels, i, branchLabels.length);
					//
					TrieVertex<I, M> leafVertex = branch.vertex;
					branch.setEdgeLabels(branchLabels1);
					TrieVertex<I, M> middleNode = new TrieVertex<I, M>();
					branch.setTargetVertex(middleNode);
					//
					middleNode.put(branchLabels[i], Branch.of(branchLabels2, leafVertex));
					return middleNode;
				} else if (branchLabels[i] != serializedQuery[cursor]) {
					int[] branchLabels1 = Arrays.copyOf(branchLabels, i);
					int[] branchLabels2 = Arrays.copyOfRange(branchLabels, i, branchLabels.length);
					//
					TrieVertex<I, M> leafVertex = branch.vertex;
					branch.setEdgeLabels(branchLabels1);
					TrieVertex<I, M> middleNode = new TrieVertex<I, M>();
					branch.setTargetVertex(middleNode);
					//
					middleNode.put(branchLabels[i], Branch.of(branchLabels2, leafVertex));
					//
					return insertionSerializedQuery(middleNode, serializedQuery, cursor);
				} else {
					i++;
					cursor++;
				}
			}
		}

	}

	private static <I, M extends TrieMetadata<I>> TrieVertex<I, M> addBranch(TrieVertex<I, M> trie,
			int[] serializedQuery, int cursor) {
		int[] newEdge = Arrays.copyOfRange(serializedQuery, cursor, serializedQuery.length);
		TrieVertex<I, M> newNode = new TrieVertex<I, M>();
		trie.put(serializedQuery[cursor], Branch.of(newEdge, newNode));
		return newNode;
	}

}
