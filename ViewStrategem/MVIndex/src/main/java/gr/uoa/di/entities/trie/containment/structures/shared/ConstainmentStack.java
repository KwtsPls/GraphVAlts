package gr.uoa.di.entities.trie.containment.structures.shared;

public interface ConstainmentStack<M> {
	
	public static <M>  ConstainmentStack<M> create(){
		return new _ContainmentStack<M>();
	}

	_ContainmentStack<M> pushMap(int varPosition, M value);

	_ContainmentStack<M> pop();

	int getPosition();

	M getCurrentEntry();

}