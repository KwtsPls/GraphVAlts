package gr.uoa.di.entities.trie.containment.structures.witnessing;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.witness.TripleWitness;
import gr.uoa.di.entities.helpStructures.tuples.IntTriple;

class _WExaminedEdgesND<TW extends TripleWitness<?,?>> implements WExaminedEdges<TW> {

	protected WExaminedEdges<TW> pointer;
	protected TW entry;
	protected IntTriple edgeTriple;
	protected int position=-1;
	

	public _WExaminedEdgesND() {
	}

	@Override
	public WExaminedEdges<TW> push(TW triple, int subjectId, int predicateId, int objectId) {
		if(triple.isND()) {
			_WExaminedEdgesND<TW> output = new _WExaminedEdgesND<TW>();
			output.entry = triple;
			output.pointer = this;
			output.edgeTriple=IntTriple.of(subjectId, predicateId, objectId);	
			output.position=position+1;
			return output;
		}else {
			_WExaminedEdgesD<TW> output = new _WExaminedEdgesD<TW>();
			output.entry = triple;
			output.pointer = this;
			output.position=position+1;
			output.triple=IntTriple.of(subjectId, predicateId, objectId);
			return output;
		}

	}

	@Override
	public TW getEdge() {
		return entry;
	}

	@Override
	public WExaminedEdges<TW> pop() {
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
