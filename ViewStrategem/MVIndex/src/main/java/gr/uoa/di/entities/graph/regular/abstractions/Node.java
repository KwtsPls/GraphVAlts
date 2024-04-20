package gr.uoa.di.entities.graph.regular.abstractions;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import gr.uoa.di.entities.graph.Printable;
import gr.uoa.di.entities.trie.graphAbstraction.InterfaceForNode;

public interface Node<N extends Node<N, T>, T extends Triple<N, T>> extends InterfaceForNode {

	N getThis();

	@Override
	void sortEdges();

	@Override
	boolean isVisited();

	@Override
	void visit();

	@Override
	boolean hasOnlyConstantPredicates();

	@Override
	boolean isVariable();

	@Override
	boolean isLabeledVariable();

	@Override
	boolean isConstant();

	@Override
	int getLabel();

	@Override
	Term getTerm();

	@Override
	void setVarEnumeration(Iterator<Integer> iter);

	@Override
	void resetForSerialization();



	@Override
	List<? extends T> getIncomingTriples();

	@Override
	List<? extends T> getOutgoingTriples();

	@Override
	int getPositionInGraph();

	@Override
	void setPositionInGraph(int positionInGraph);

	void addTriple(Triple<N, T> triple2);

	@Override
	String print(Function<Printable, String> function);

	@Override
	String toString();

	@Override
	void setLabel(int nodeId);

	int countEdges();

}