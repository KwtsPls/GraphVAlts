package gr.uoa.di.entities.trie.graphAbstraction;

import java.util.Iterator;
import java.util.List;

import gr.uoa.di.entities.graph.Printable;
import gr.uoa.di.entities.graph.regular.abstractions.Term;

public interface InterfaceForNode extends Term,Printable{

	/**
	 * Sorts the edges of the corresponding Node
	 */
	public void sortEdges();

	/**
	 * @return True if the node has be examined
	 */
	public boolean isVisited();

	/**
	 * @return Says that the node has been examined
	 */
	public void visit();

	/**
	 * @return True if the corresponding Node has no variable-predicates 
	 */
	public boolean hasOnlyConstantPredicates();

	@Override
	public boolean isVariable();

	@Override
	public boolean isConstant();

	@Override
	public int getLabel();

	@Override
	public Term getTerm();

	@Override
	public void setVarEnumeration(Iterator<Integer> iter);


	/**
	 * Resets the triple regarding if it has been visited or not
	 */
	public void resetForSerialization();

	

	
	/**
	 * @return The incoming triples to the node
	 */
	public List<? extends InterfaceForTriple> getIncomingTriples();

	
	/**
	 * @return The outgoing triples to the node
	 */
	public List<? extends InterfaceForTriple> getOutgoingTriples();

	/**
	 * @return The position of the node within the graph
	 */
	public int getPositionInGraph();

	/**
	 * @param positionInGraph Sets the position of the node within the graph
	 */
	public void setPositionInGraph(int positionInGraph);

}
