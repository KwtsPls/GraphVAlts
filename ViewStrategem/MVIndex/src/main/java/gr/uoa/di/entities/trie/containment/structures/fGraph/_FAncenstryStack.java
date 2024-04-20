package gr.uoa.di.entities.trie.containment.structures.fGraph;

class _FAncenstryStack<M> implements FAncenstryStack<M> {

	/* Contains the parent and metadata of the currently */
	/* examined node */
	private _FAncenstryStack<M> pointer;
	/* Contains the currently examined node of the graph-witness */
	private M currentNode;
	/* Contains the cursor on the examined current node's edges */
	private int edgeCursor;
	/* true (false) if we examine the outgoing (incoming) edges */
	private boolean outgoing;
	/* Corresponds to the trie value of the currentNode */
	private int examinedNodeID;

	public _FAncenstryStack() {
		this.edgeCursor = -1;
	}

	private _FAncenstryStack(M fatherNode, int edgeCursor, boolean outGoingExamination, int examinedNodeID,_FAncenstryStack<M> pointer) {
		this.currentNode = fatherNode;
		this.edgeCursor = edgeCursor;
		this.outgoing = outGoingExamination;
		this.pointer = pointer;
		this.examinedNodeID = examinedNodeID;
	}

	@Override
	public _FAncenstryStack<M> push(M fatherNode, int edgeCursor, boolean outGoingExamination, int examinedNodeID) {
		return new _FAncenstryStack<M>(fatherNode, edgeCursor, outGoingExamination, examinedNodeID,this);
	}

	@Override
	public _FAncenstryStack<M> pop() {
		return this.pointer;
	}

	@Override
	public M getNode() {
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
	public String toString() {
		if (pointer == null)
			return "";
		StringBuilder buffer = new StringBuilder(pop().toString());
		buffer.append(" ");
		buffer.append(currentNode);
		return buffer.toString();
	}

	@Override
	public int getExaminedNodeID() {
		return examinedNodeID;
	}

}
