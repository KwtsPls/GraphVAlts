package gr.uoa.di.entities.viewSelection.edgeRewriting;

import java.util.List;
import java.util.function.BiConsumer;

import gr.uoa.di.entities.graph.PatternVS;
import gr.uoa.di.entities.graph.Printable;
import gr.uoa.di.entities.graph.TripleVS;
import gr.uoa.di.entities.trie.containment.mapping.ContainmentMapping;
import gr.uoa.di.entities.helpStructures.tuples.MyPair;

public interface TripleMap extends Printable {

	public static <M> TripleMap create(
			PatternVS fromPattern, ContainmentMapping<?, TripleVS, M> mapping) {
		return new _TripleMap(fromPattern, mapping);
	}
	
	public static < M> TripleMap 
	create(boolean isomorphic,List<MyPair<TripleVS,TripleVS>> pairs) {
		return new _TripleMap(isomorphic, pairs);
	}

	public TripleVS getMappedTriple(TripleVS edge);

	boolean isIsomorphic();

	void forEachTripleRewriting(BiConsumer<TripleVS, TripleVS> consumer);

}
