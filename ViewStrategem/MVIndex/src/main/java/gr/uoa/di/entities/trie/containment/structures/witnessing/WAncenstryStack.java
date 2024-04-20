package gr.uoa.di.entities.trie.containment.structures.witnessing;

import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForNode;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForTriple;
import gr.uoa.di.entities.graph.witness.NodeWitness;

public interface WAncenstryStack<N extends AbstractionForNode<N, T>, T extends AbstractionForTriple<N, T>> {
	
	public static <N extends AbstractionForNode<N, T>, T extends AbstractionForTriple<N, T>> WAncenstryStack<N,T> create() {
		return new _WAncenstryStack<N,T>();
	}

	_WAncenstryStack<N, T> push(NodeWitness<N, T> fatherNode, int edgeCursor, boolean outGoingExamination,
			int currentNodeID);

	_WAncenstryStack<N, T> pop();

	NodeWitness<N, T> getNode();

	int getCursor();

	boolean getOutgoing();

	int getExaminedNodeID();

	@Override
	String toString();

}