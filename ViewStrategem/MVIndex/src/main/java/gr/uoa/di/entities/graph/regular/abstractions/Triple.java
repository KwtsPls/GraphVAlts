package gr.uoa.di.entities.graph.regular.abstractions;

import java.util.function.Function;

import gr.uoa.di.entities.graph.Printable;
import gr.uoa.di.entities.trie.graphAbstraction.InterfaceForTriple;

public interface Triple<N extends Node<N, T>, T extends Triple<N, T>> extends InterfaceForTriple {



	@Override
	N getSubject();

	/**
	 * @return The actual triple in its type
	 */
	T getThis();

	@Override
	Term getPredicate();

	@Override
	N getObject();

	@Override
	String toString();

	@Override
	boolean isVisited();

	@Override
	void visit();

	@Override
	int getPredicateLabel();



	@Override
	void resetForSerialization();

	@Override
	String print(Function<Printable, String> function);

}