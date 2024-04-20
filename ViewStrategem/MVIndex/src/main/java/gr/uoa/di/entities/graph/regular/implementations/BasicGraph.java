package gr.uoa.di.entities.graph.regular.implementations;

import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForGraph;
import gr.uoa.di.entities.graph.regular.abstractions.Graph;

public class BasicGraph extends AbstractionForGraph<BasicNode,BasicTriple,BasicGraph> implements Graph<BasicNode,BasicTriple,BasicGraph>{

	public BasicGraph() {
		super();
	}

	@Override
	public BasicGraph getThis() {
		return this;
	}
}
