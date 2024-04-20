package gr.uoa.di.entities.graph.regular.implementations;

import gr.uoa.di.entities.graph.regular.abstractions.Term;
import gr.uoa.di.entities.graph.regular.abstractions.Triple;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForNode;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForTriple;

public class BasicTriple extends AbstractionForTriple<BasicNode,BasicTriple> implements Triple<BasicNode,BasicTriple>{

	BasicTriple(AbstractionForNode<BasicNode, BasicTriple> subject, Term predicate,
			AbstractionForNode<BasicNode, BasicTriple> object) {
		super(subject, predicate, object);
	}

	@Override
	public BasicTriple getThis() {
		return this;
	}

}
