package gr.uoa.di.entities.trie;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForGraph;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForNode;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForTriple;
import gr.uoa.di.entities.trie.containment.TrieContainmentForGraphQuery;
import gr.uoa.di.entities.trie.containment.mapping.ContainmentMapping;

public abstract class TrieIndex<I, M extends TrieMetadata<I>> {

	public TrieVertex<I, M> vertex = new TrieVertex<>();

	public static <I, M extends TrieMetadata<I>> TrieIndex<I, M> create(Supplier<M> supplier) {
		return new TrieIndex<I, M>(supplier) {
			@Override
			public M createMetadataInstance() {
				return supplier.get();
			}
		};
	}

	private TrieIndex(Supplier<M> supplier) {
		super();
	}

	protected TrieIndex() {
		super();
	}

	public abstract M createMetadataInstance();

	public TrieVertex<I, M> insertQuery(AbstractionForGraph<?, ?, ?> graph, I metatada) {
		return _TrieInsertion.insertQuery(this, vertex, graph, metatada);
	}

	public TrieVertex<I, M> insertQueryPattern(AbstractionForGraph<?, ?, ?> graph, I metatada) {
		return _TrieInsertion.insertQueryPattern(this, vertex, graph, metatada);
	}

	public <	N extends AbstractionForNode<N, T>, 
				T extends AbstractionForTriple<N, T>, 
				G extends AbstractionForGraph<N, T, G>> 
	List<ContainmentMapping<N, T, M>> contains(
			G graph) {
		TrieContainmentForGraphQuery<N, T, I, M> containmentEngine = new TrieContainmentForGraphQuery<N, T, I, M>();
		return containmentEngine.contains(vertex, graph);
	}

	@Override
	public String toString() {
		return vertex.toIdString();
	}
	
	public void forEachMetadataEntry(Consumer<M> metadataConsumer) {
		vertex.forEachMetadataEntry(metadataConsumer);
	}
	
	public String toIdString() {// NO_UCD (unused code)
		return vertex.toIdString();
	}

	public String toCompactString(Dictionary dict) { // NO_UCD (unused code)
		return vertex.toCompactString(dict);
	}

	public String toFullString(Dictionary dict) { // NO_UCD (unused code)
		return vertex.toFullString(dict);
	}
	
	public List<Branch<I,M>> findPath(int vertexId ){ // NO_UCD (unused code)
		return FindPath.find(vertex,vertexId,new LinkedList<Branch<I,M>>());
	}

}
