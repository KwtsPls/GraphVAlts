package gr.uoa.di.entities.viewSelection.queryRewriting;

import java.util.Set;
import java.util.TreeSet;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.GraphVS;
import gr.uoa.di.entities.helpStructures.tuples.IntTriple;

class _ViewForRewriting implements ViewForRewriting {
	private GraphVS graph; // TODO is only needed for the materialization phase
	private int rowCount;
	private String tableName = null;
	private Set<IntTriple> coveredTriples;
	private int varCount;

	_ViewForRewriting(ViewForRewriting view) {
		graph = view.getGraph();
		rowCount = view.getRowCount();
		tableName = view.getTableName();
		coveredTriples = view.getViewTriples();
		varCount = view.getVarCount();
	}

	@Override
	public GraphVS getGraph() {
		return graph;
	}

	@Override
	public int getRowCount() {
		return rowCount;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	public Set<IntTriple> getViewTriples() {
		return coveredTriples;
	}

	public String toString() {
		TreeSet<String> vars = new TreeSet<String>();
		coveredTriples.forEach(triple -> {
			for (int tripleId : triple.getVars()) {
				vars.add(Dictionary.getVarStringOfId(tripleId));
			}
		});
		StringBuffer buffer = new StringBuffer(
				"(" + (tableName == null ? " " : tableName + vars+',') + "rows:" + rowCount + ",");
		buffer.append(graph.toCompactString());
		return buffer.toString();
	}

	@Override
	public int getVarCount() {
		return varCount;
	}

}
