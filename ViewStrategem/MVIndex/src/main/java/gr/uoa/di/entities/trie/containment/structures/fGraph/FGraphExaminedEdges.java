package gr.uoa.di.entities.trie.containment.structures.fGraph;


import gr.uoa.di.entities.graph.Printable;
import gr.uoa.di.entities.helpStructures.tuples.IntTriple;

public interface FGraphExaminedEdges<T extends Printable> {
	
	static public<T extends Printable> FGraphExaminedEdges<T> create() {
		return new _FGraphExaminedEdges<T>();
	}
	
	public FGraphExaminedEdges<T> push(T entry, int subjectID, int predicateID, int objectID);
	
	public T getEdge();
	
	public IntTriple getEdgeTriple();

	public FGraphExaminedEdges<T> pop();	
	
	public boolean isND();
	
	public int getPosition();
	
}
