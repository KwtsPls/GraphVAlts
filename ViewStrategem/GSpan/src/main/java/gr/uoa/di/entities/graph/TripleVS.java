package gr.uoa.di.entities.graph;

import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForNode;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForTriple;
import gr.uoa.di.entities.graph.regular.abstractions.Term;

public class TripleVS extends AbstractionForTriple<NodeVS, TripleVS> {

	private int cost = -1;
	private int rowCount = Integer.MAX_VALUE;

	TripleVS(AbstractionForNode<NodeVS, TripleVS> subject, Term predicate,
			AbstractionForNode<NodeVS, TripleVS> object) {
		super(subject, predicate, object);
	}

	public int getCost() {
		return cost;
	}

	@Override
	public TripleVS getThis() {
		return this;
	}

	@Override
	public String toString() {
		return toIdString();
	}

	public int getRowCount() {
		return rowCount;
	}

}
