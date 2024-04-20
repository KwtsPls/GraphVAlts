package gr.uoa.di.entities.viewSelection.hierarchy;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import gr.uoa.di.entities.graph.GraphVS;
import gr.uoa.di.entities.graph.TripleVS;
import gr.uoa.di.entities.helpStructures.tuples.IntTriple;
import gr.uoa.di.entities.helpStructures.tuples.MyPair;
import gr.uoa.di.entities.viewSelection.edgeRewriting.TripleMap;

class _TmpEdgeRewriting {
	private HashMap<IntTriple, IntTriple> map;
	private boolean isomorphic;
	
	public _TmpEdgeRewriting(TripleMap map) {
		this.map = new HashMap<>();
		map.forEachTripleRewriting((tripleFrom,tripleTo)->{
			this.map.put(IntTriple.of(tripleFrom), IntTriple.of(tripleTo));
		});
		this.isomorphic=map.isIsomorphic();
	}

	public String toString() {
		return map.toString();
	}

	public static List<TripleMap> createTripleMapping(GraphVS graphVS1, GraphVS graphVS2,
			List<_TmpEdgeRewriting> listOfRewritings) {
		List<TripleMap> output = new LinkedList<>();
		//
		HashMap<IntTriple,TripleVS> graph1Triples = new HashMap<>();		
		for(TripleVS triple:graphVS1) {
			graph1Triples.put(IntTriple.of(triple), triple);
		}
		//			
		HashMap<IntTriple,TripleVS> graph2Triples = new HashMap<>();
		for(TripleVS triple:graphVS2) {
			graph2Triples.put(IntTriple.of(triple), triple);
		}
		//			
		listOfRewritings.forEach(rewriting->{
			
			List<MyPair<TripleVS,TripleVS>> pairs = new LinkedList<>();
			rewriting.map.forEach((x,y)->{				
				pairs.add(MyPair.of(graph1Triples.get(x),graph2Triples.get(y)));
			});
			output.add(TripleMap.create(rewriting.isomorphic, pairs));
		});
		return output;
	}
}
