package gr.uoa.di.entities.trie.graphAbstraction;

import java.util.List;

import gr.uoa.di.entities.graph.Printable;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForGraph;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForNode;

public interface InterfaceForQuery<N extends AbstractionForNode<N, ?>, G extends AbstractionForGraph<N, ?, G>> extends Printable {
	
	/**
	 * @return The graph corresponding to the body of the query 
	 */
	G getGraph();

	/**
	 * @return The variables appearing in the head of the query 
	 * 
	 */
	List<N> getHeadVars();

}