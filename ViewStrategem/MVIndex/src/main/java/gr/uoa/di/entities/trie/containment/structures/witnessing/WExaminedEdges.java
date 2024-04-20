package gr.uoa.di.entities.trie.containment.structures.witnessing;


import gr.uoa.di.entities.graph.witness.TripleWitness;
import gr.uoa.di.entities.helpStructures.tuples.IntTriple;

public interface WExaminedEdges<TW extends TripleWitness<?,?>> {
	
	public static<TN extends TripleWitness<?,?>> WExaminedEdges<TN> createD(){
		return new _WExaminedEdgesD<TN>();
	}
	
	public WExaminedEdges<TW> push(TW entry, int subjectID, int predicateID, int objectID);
	
	public TW getEdge();

	public WExaminedEdges<TW> pop();	
	
	public boolean isND();
	
	public int getPosition();
	
	public IntTriple getEdgeTriple();

	public int getSubjectID();

	public int getObjectID();

	public int getPredicateID();
		
}
