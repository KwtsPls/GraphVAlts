package gr.uoa.di.entities.graph.regular.factory;

import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForGraph;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForNode;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForTriple;

public interface GraphConstructor<N extends AbstractionForNode<N, T>, T extends AbstractionForTriple<N, T>, G extends AbstractionForGraph<N, T, G>> {

	void addTripleFromJObjects(Object subject, Object predicate, Object object);

	T addTripleFromInt(int subjectId, int predicateId, int objectId);

	G getGraphQuery();

}