package gr.uoa.di.entities.trie.containment.mapping;

import java.util.function.BiConsumer;

import gr.uoa.di.entities.graph.Printable;
import gr.uoa.di.entities.helpStructures.tuples.IntTriple;
import gr.uoa.di.entities.trie.graphAbstraction.InterfaceForNode;
import gr.uoa.di.entities.trie.graphAbstraction.InterfaceForTriple;

/**
 * @author theofilos A containment mapping is used to represent containment info
 *         between a view (or cached answer) and a graph query.
 * @param <M> The parameter <M> is used to store the metadata of the containment
 *            mapping.
 */
public interface ContainmentMapping<N extends InterfaceForNode,T extends InterfaceForTriple,M> extends Cloneable,Printable{
	
	public static <BasicNode extends InterfaceForNode,BasicTriple extends InterfaceForTriple,M> ContainmentMapping<BasicNode,BasicTriple,M> create(int answerSize, int examinedEdgesSize) {
		return new _ContainmentMapping<BasicNode,BasicTriple,M>(answerSize, examinedEdgesSize);
	}

	/**
	 * Adds an edge of the graph query that has been examined.
	 * 
	 * @param edge The corresponding edge.
	 */
	public void addEdge(IntTriple edgeTriple, T edge);

	public ContainmentMapping<N,T,M> clone();
	

	/**
	 * This function parses mappings between EdgeTriples within the MvIndex and Edges within a graph. 
	 * @param action The input of the function is the BiConsumer that indicates how a mapping pair will be processed.
	 */
	public void forEachTripleMapping(BiConsumer<? super IntTriple, ? super T> action);

	/**
	 * @param triple Takes as input an EdgeTriple located within the MvIndex. 
	 * @return The edge to which it is mapped.
	 */
	public T getContainmentMapping(IntTriple triple);



	public int getId();

	/**
	 * For a containment mapping from a view (or cached query) to a graph query, it
	 * takes as input the id of a node of the view and returns the node of the graph
	 * query to which the view node is mapped.
	 * 
	 * @param nodeId the corresponding id.
	 * @return The node of the graph query to which it is mapped.
	 */
	public N getMappedNode(int nodeId);
	
	public N getMappedNode(N node);

	/**
	 * @return The metadata related to the corresponding containment mapping.
	 */
	public M getMetadata();
	
	/**
	 * Examines if a containment mapping is an isomorphism.
	 * 
	 * @return true if it is.
	 */
	public boolean isIsomorphic();
	
	/**
	 * Maps a variable of a view (or cached answer) to a corresponding node of a
	 * graph query.
	 * 
	 * @param varId the corresponding variable id.
	 * @param node  the corresponding node.
	 */
	public void mapVarId2Node(int varId, N node);
		
	/**
	 * Sets the metadata related to the corresponding containment mapping.
	 * 
	 * @param metadata the aforementioned metadata.
	 */
	public void setMetadata(M metadata);

//	public String toCompactString(Dictionary dict);
//	
//	public String toStringHead();
}
