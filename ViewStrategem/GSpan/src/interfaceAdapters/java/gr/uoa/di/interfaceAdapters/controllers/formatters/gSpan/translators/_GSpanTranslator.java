package gr.uoa.di.interfaceAdapters.controllers.formatters.gSpan.translators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.GraphVS;
import gr.uoa.di.entities.graph.NodeVS;
import gr.uoa.di.entities.graph.TripleVS;
import gr.uoa.di.entities.gspan.gspanGraph.GSpanGraph;
import gr.uoa.di.entities.gspan.gspanGraph.IntTriple;

class _GSpanTranslator {
	private static final int constantLabel = Dictionary.constantLabel;
	private static final int conjunctionLabel = Dictionary.conjunctionLabel;
	private static final int variableLabel = Dictionary.variableLabel;
	private static final int subjConjunct = Dictionary.subjConjunct;
	private static final int objConjunct = Dictionary.objConjunct;
	private HashMap<NodeVS,Integer> nodeMap = new HashMap<>();
	private List<IntTriple> tripleLabels = new ArrayList<>();
	private List<Integer> nodeLabels = new ArrayList<Integer>();
	
	GSpanGraph translate(GraphVS graph, int id, TreeSet<Integer> where) {
		GSpanGraph gspanGraph = getGSpanGraph(graph);
		gspanGraph.id = id;		
		gspanGraph.where = where.toArray(new Integer[where.size()]);
		gspanGraph.support = where.size();			
		return gspanGraph;
	}

	GSpanGraph getGSpanGraph(GraphVS graph){
		for (TripleVS triple : graph) {
			int tableId = nodeLabels.size();
			nodeLabels.add(triple.getPredicateLabel());
			int subjectId = getNode(triple.getSubject());
			int objectId = getNode(triple.getObject());
			tripleLabels.add(IntTriple.of(tableId, subjectId, subjConjunct));
			tripleLabels.add(IntTriple.of(tableId, objectId, objConjunct));
		}
		return GSpanGraph.create(nodeLabels, tripleLabels);
	}
	
	private int getNode(NodeVS node) {	
		int out = nodeMap.computeIfAbsent(node, x->{
			int nodeId;
			if (x.isConstant()) {
				nodeId = nodeLabels.size();
				nodeLabels.add(constantLabel);
				appendTriple(nodeId, nodeId, x.getLabel());
			} else {
				nodeId = nodeLabels.size();
				nodeLabels.add(variableLabel);
				if(x.countEdges()>1) {
					appendTriple(nodeId, nodeId, conjunctionLabel);
				}
			}
			return nodeId;
		});
		return out;
	}

	private void appendTriple(int subject, int object, int predicate) {
		tripleLabels.add(IntTriple.of(subject, object, predicate));
	}
}
