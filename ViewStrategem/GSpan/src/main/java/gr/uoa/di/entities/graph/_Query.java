package gr.uoa.di.entities.graph;

import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForQuery;

class _Query extends AbstractionForQuery<NodeVS, GraphVS> implements QueryVS {
	private int support;
	private static int meter = 0;
	private int id;

	//
	_Query(GraphVS graph2, int support) {
		super(graph2);
		this.support = support;
		id = meter++;
	}

	@Override
	public int getSupport() {
		return support;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return new StringBuffer(super.toString()).append("[Support: ").append(support).append(", id: ").append(id)
				.append("]").toString();
	}
}
