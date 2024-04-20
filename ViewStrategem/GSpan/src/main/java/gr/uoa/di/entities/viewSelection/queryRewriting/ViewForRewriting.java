package gr.uoa.di.entities.viewSelection.queryRewriting;

import java.util.Set;

import gr.uoa.di.entities.graph.GraphVS;
import gr.uoa.di.entities.helpStructures.tuples.IntTriple;

public interface ViewForRewriting {
	
	public static ViewForRewriting create(ViewForRewriting view) {
		return new _ViewForRewriting(view);
	}

	GraphVS getGraph();

	int getRowCount();

	String getTableName();
	
	Set<IntTriple> getViewTriples();

	int getVarCount();

}
