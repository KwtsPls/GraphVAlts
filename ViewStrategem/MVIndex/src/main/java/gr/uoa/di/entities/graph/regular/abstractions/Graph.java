package gr.uoa.di.entities.graph.regular.abstractions;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import gr.uoa.di.entities.graph.Printable;

public interface Graph<N extends Node<N, T>, T extends Triple<N, T>, G extends Graph<N, T, G>> {

	G getThis();

	@Override
	String toString();

	void forEachNode(Consumer<N> action);

	void forEachVarNode(Consumer<N> action);

	void forEachTriple(Consumer<T> action);

	void sortGraph();

	List<N> getVariables();

	int getNodeCount();

	N getAnchorNode();

	int getVarNodeCount();

	int getEdgeCount();

	void resetForSerialization();

	Iterator<T> iterator();

	String print(Function<Printable, String> function);

	void reArrangeVarNodes();

}