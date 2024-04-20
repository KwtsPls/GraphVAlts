package gr.uoa.di.entities.graph;

import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForGraph;

public class GraphVS extends AbstractionForGraph<NodeVS, TripleVS, GraphVS> {

	public GraphVS() {
		super();
	}

	@Override
	public GraphVS getThis() {
		return this;
	}

}
