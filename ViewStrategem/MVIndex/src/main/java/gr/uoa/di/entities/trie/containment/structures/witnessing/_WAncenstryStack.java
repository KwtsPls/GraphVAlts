package gr.uoa.di.entities.trie.containment.structures.witnessing;

import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForNode;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForTriple;
import gr.uoa.di.entities.graph.witness.NodeWitness;

class _WAncenstryStack<N extends AbstractionForNode<N, T>, T extends AbstractionForTriple<N, T>>
		implements WAncenstryStack<N, T> {
	protected static final long serialVersionUID = -5036783150411127313L;

	/* Contains the parent and metadata of the currently */
	/* examined node */
	private _WAncenstryStack<N, T> pointer;
	/* Contains the currently examined node of the graph-witness */
	private NodeWitness<N, T> currentNode;
	/* Contains the cursor on the examined current node's edges */
	private int edgeCursor;
	/* true (false) if we examine the outgoing (incoming) edges */
	private boolean outgoing;
	/* Corresponds to the trie value of the currentNode */
	private int currentNodeId;

	public _WAncenstryStack() {
		this.edgeCursor = -1;
	}

	private _WAncenstryStack(NodeWitness<N, T> fatherNode, int edgeCursor, boolean outGoingExamination,
			int currentNodeID) {
		this.currentNode = fatherNode;
		this.edgeCursor = edgeCursor;
		this.outgoing = outGoingExamination;
		this.currentNodeId = currentNodeID;
		this.pointer = this;
	}

	@Override
	public _WAncenstryStack<N, T> push(NodeWitness<N, T> fatherNode, int edgeCursor, boolean outGoingExamination,
			int currentNodeID) {
		return new _WAncenstryStack<N, T>(fatherNode, edgeCursor, outGoingExamination,
				currentNodeID);
	}

	@Override
	public _WAncenstryStack<N, T> pop() {
		return this.pointer;
	}

	@Override
	public NodeWitness<N, T> getNode() {
		return currentNode;
	}

	@Override
	public int getCursor() {
		return edgeCursor;
	}

	@Override
	public boolean getOutgoing() {
		return outgoing;
	}

	@Override
	public int getExaminedNodeID() {
		return currentNodeId;
	}

	@Override
	public String toString() {
		if (pointer == null)
			return "";
		StringBuilder buffer = new StringBuilder(pop().toString());
		buffer.append(" ");
		buffer.append(currentNode);
		return buffer.toString();
	}

}
