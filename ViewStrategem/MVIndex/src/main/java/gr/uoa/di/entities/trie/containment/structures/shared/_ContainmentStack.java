package gr.uoa.di.entities.trie.containment.structures.shared;

import gr.uoa.di.entities.dictionary.Dictionary;

class _ContainmentStack<M> implements ConstainmentStack<M>   {

	private _ContainmentStack<M> pointer;
	private M value;
	private int position;

	public _ContainmentStack() {
		position = Dictionary.firstVarId - 1;
	}


	@Override
	public _ContainmentStack<M> pushMap(int varPosition, M value) {
		if (varPosition > position + 1 || varPosition > Dictionary.lastVarId) {
			return null;
		} else if (varPosition == position + 1) {
			_ContainmentStack<M> output = new _ContainmentStack<M>();
			output.value = value;
			output.pointer = this;
			output.position = position + 1;
			return output;
		} else {
			int moves =  position -varPosition;
			_ContainmentStack<M> cursor = this;
			while ((moves--) != 0) {
				cursor = cursor.pointer;
			}
			if (value != this.value) {
				return null;
			}

			return this;
		}
	}

	public M getCurrentEntry() {
		return value;
	}

	public int getPosition() {
		return position;
	}

	@Override
	public _ContainmentStack<M> pop() {
		return this.pointer;
	}

	@Override
	public String toString() {
		if (pointer == null)
			return "";
		String previous=pop().toString();
		StringBuilder buffer = new StringBuilder(previous).append(previous.equals("")?"":", ");
		buffer.append(position).append("->");
		buffer.append(value);		
		return buffer.toString();

	}

}
