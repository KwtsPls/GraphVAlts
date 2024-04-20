package gr.uoa.di.entities.graph;

import gr.uoa.di.entities.graph.regular.abstractions.Term;
import gr.uoa.di.entities.graph.regular.factory.AbstractGraphFactory;
import gr.uoa.di.entities.graph.regular.factory.Factory;

public class FactoryVS extends AbstractGraphFactory<NodeVS, TripleVS, GraphVS>
		implements Factory<NodeVS, TripleVS, GraphVS> {

	@Override
	public GraphVS createGraph() {
		return new GraphVS();
	}

	@Override
	public TripleVS createTriple(NodeVS subject, Term predicate, NodeVS object) {
		return new TripleVS(subject, predicate, object);
	}

	@Override
	public NodeVS createVarNode(Object var) {
		return new NodeVS(Term.getVarTerm(var));
	}

	@Override
	public NodeVS createConstantNode(int objectID, Object var) {
		return new NodeVS(Term.getConstantTerm(objectID, var));
	}
}
