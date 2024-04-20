package gr.uoa.di.entities.trie.containment.structures.fGraph;

public interface FAncenstryStack<M> {
	
	static public <M> FAncenstryStack<M> create(){
		return new _FAncenstryStack<M>();
	}

	_FAncenstryStack<M> push(M fatherNode, int edgeCursor, boolean outGoingExamination, int examinedNodeID);

	_FAncenstryStack<M> pop();

	M getNode();

	int getCursor();

	boolean getOutgoing();

	@Override
	String toString();

	int getExaminedNodeID();

}