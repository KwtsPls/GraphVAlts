package gr.uoa.di.entities.trie.containment.structures.fGraph;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.Printable;
import gr.uoa.di.entities.helpStructures.tuples.IntTriple;

class _FGraphExaminedEdges<T extends Printable> implements FGraphExaminedEdges<T> {

	private FGraphExaminedEdges<T> pointer;
	private T entry;
	private IntTriple edgeTriple;
	private int position=-1;
	

	public _FGraphExaminedEdges() {
	}

	@Override
	public FGraphExaminedEdges<T> push(T entry, int subjectId, int predicateId, int objectId) {
		_FGraphExaminedEdges<T> output = new _FGraphExaminedEdges<T>();
		output.entry = entry;
		output.pointer = this;
		output.position=position+1;
		output.edgeTriple=IntTriple.of(subjectId, predicateId, objectId);
		return output;
	}

	@Override
	public T getEdge() {
		return entry;
	}

	@Override
	public FGraphExaminedEdges<T> pop() {
		return this.pointer;

	}

	@Override
	public String toString() {
		if (pointer == null)
			return "";
		String previous = pop().toString();
		StringBuilder buffer = new StringBuilder(previous).append(previous.equals("") ? "" : ", ");
		buffer.append('(')
				.append(Dictionary.isConstant(edgeTriple.s) ? edgeTriple.s : "?x" + (edgeTriple.s - Dictionary.firstVarId))
				.append(',')
				.append(Dictionary.isConstant(edgeTriple.p) ? edgeTriple.p : "?x" + (edgeTriple.p - Dictionary.firstVarId))
				.append(',')
				.append(Dictionary.isConstant(edgeTriple.o) ? edgeTriple.o : "?x" + (edgeTriple.o - Dictionary.firstVarId))
				.append(") -> ");
		buffer.append(entry.toIdString());
		return buffer.toString();
	}
	
	@Override
	public boolean isND() {
		return true;
	}

	public int getSubjectID() {
		return edgeTriple.s;
		
	}

	public int getPredicateID() {
		
		return edgeTriple.p;
		
		
	}

	public int getObjectID() {
		return edgeTriple.o;		
	}


	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public IntTriple getEdgeTriple() {
		return edgeTriple;
	}

}
