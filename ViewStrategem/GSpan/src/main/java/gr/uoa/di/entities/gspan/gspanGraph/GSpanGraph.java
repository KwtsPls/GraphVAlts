package gr.uoa.di.entities.gspan.gspanGraph;

import java.util.List;

import java.util.ArrayList;
import java.util.LinkedList;

public class GSpanGraph {
	private static final Integer[] emptyWhere = new Integer[] {};

	public int id = -1;
	public List<Integer> nodeLabels;
	public List<IntTriple> tripleLabels;
	public int support;
	public Integer[] where = emptyWhere;

	public static GSpanGraph create(List<Integer> nodeLabels, List<IntTriple> edgeTriples) {
		return new GSpanGraph(nodeLabels, edgeTriples);
	}

	public GSpanGraph() {
		nodeLabels = new ArrayList<Integer>();
		tripleLabels = new ArrayList<IntTriple>();
	}

	public GSpanGraph(List<Integer> nodeLabels2, List<IntTriple> edgeTriples2) {
		nodeLabels = nodeLabels2;
		tripleLabels = edgeTriples2;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("t # ").append(id).append(" * ").append(support).append("\n");
		int i = 0;
		for (Integer nodeId : nodeLabels) {
			buffer.append("v ").append(i++).append(" ").append(nodeId).append("\n");
		}
		
		tripleLabels.forEach(label->{
			buffer.append("e ").append(label.getLeft()).append(" ").append(label.getMiddle()).append(" ").append(label.getRight())
			.append("\n");
		});


		buffer.append("x ");
		for (int occId : where) {
			buffer.append(occId).append(' ');
		}
		buffer.deleteCharAt(buffer.length() - 1);
		return buffer.toString();
	}

	public List<Integer> getNodeLabeling() {
		return nodeLabels;
	}

	public List<IntTriple> getEdgeLabeling() {
		return tripleLabels;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tripleLabels == null) ? 0 : tripleLabels.hashCode());
		result = prime * result + ((nodeLabels == null) ? 0 : nodeLabels.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GSpanGraph other = (GSpanGraph) obj;
		if (tripleLabels == null) {
			if (other.tripleLabels != null)
				return false;
		} else if (!tripleLabels.equals(other.tripleLabels))
			return false;
		if (nodeLabels == null) {
			if (other.nodeLabels != null)
				return false;
		} else if (!nodeLabels.equals(other.nodeLabels))
			return false;
		return true;
	}

	public boolean isEmpty() {
		if (tripleLabels.size() == 0) {
			return true;
		}
		return false;
	}
	
	public static GSpanGraph create(String graphString) {
		List<Integer> nodeLabels = new ArrayList<>(); 
		List<IntTriple> edgeTriples = new LinkedList<>();
		//
		String[] parsedLines2 = graphString.split("\n");
		//
		int id = -1;
		int support = -1;
		Integer[] where = null;
		//
		for(String line:parsedLines2) {
			if(line.startsWith("t")) {
				String[] termString = line.split(" ");
				id = Integer.valueOf(termString[2]);
				support = Integer.valueOf(termString[4]);
			}else if(line.startsWith("v")) {
				String[] termString = line.split(" ");
				int constantId = Integer.valueOf(termString[2]);
				nodeLabels.add(constantId);
			}else if(line.startsWith("e")) {
				String[] termString = line.split(" ");			
				int subjId = Integer.valueOf(termString[1]);
				int objId = Integer.valueOf(termString[2]);
				int predicateId = Integer.valueOf(termString[3]);
				edgeTriples.add(IntTriple.of(subjId, objId, predicateId));
			}else if(line.startsWith("x")) {
				String[] termString = line.split(" ");
				where = new Integer[termString.length-1];
				for(int i=1;i<termString.length;i++) {
					where[i-1] = Integer.valueOf(termString[i]);
				}	
			
			}
			
		}
		if(nodeLabels.isEmpty()||edgeTriples.isEmpty()) {
			return null;
		}
		GSpanGraph gspanGraph =new GSpanGraph(nodeLabels,edgeTriples);
		gspanGraph.id = id;
		gspanGraph.support = support; 
		gspanGraph.where = where;
		return gspanGraph;
	}

}
