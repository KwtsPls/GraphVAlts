package gr.uoa.di.entities.graph.regular.factory;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForGraph;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForNode;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForTriple;
import gr.uoa.di.entities.graph.regular.abstractions.Term;

public interface Factory<N extends AbstractionForNode<N, T>, T extends AbstractionForTriple<N, T>, G extends AbstractionForGraph<N, T, G>> {

	G createGraph();

	T createTriple(N subject, Term predicate, N object);

	N createVarNode(Object var);

	N createConstantNode(int objectID, Object var);

	GraphConstructor<N,T,G> getGraphConstructor(Dictionary dict2);

}