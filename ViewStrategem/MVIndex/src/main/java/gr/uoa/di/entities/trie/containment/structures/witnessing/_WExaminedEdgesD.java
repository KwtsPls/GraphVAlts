package gr.uoa.di.entities.trie.containment.structures.witnessing;

import gr.uoa.di.entities.graph.witness.TripleWitness;
import gr.uoa.di.entities.helpStructures.tuples.IntTriple;

 class _WExaminedEdgesD<TW extends TripleWitness<?,?>> implements WExaminedEdges<TW> {

	protected WExaminedEdges<TW> pointer;
	protected TW entry;
	protected IntTriple triple;
	protected int position=-1;

	public _WExaminedEdgesD() {
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
		buffer.append(entry.toIdString());

		return buffer.toString();

	}

	@Override
	public boolean isND() {
		return false;
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public IntTriple getEdgeTriple() {
		return triple;
	}

	@Override
	public int getSubjectID() {		
		return triple.s;
	}

	@Override
	public int getObjectID() {
		return triple.o;
	}

	@Override
	public int getPredicateID() {
		return triple.p;
	}

}
