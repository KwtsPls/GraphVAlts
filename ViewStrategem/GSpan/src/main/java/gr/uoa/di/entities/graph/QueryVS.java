package gr.uoa.di.entities.graph;

import gr.uoa.di.entities.trie.graphAbstraction.InterfaceForQuery;

public interface QueryVS extends InterfaceForQuery<NodeVS, GraphVS> {

	static QueryVS create(PatternVS pattern) {
		return new _Query(pattern.getGraph(), pattern.getSupport());
	}

	public int getSupport();

	public int getId();

}
