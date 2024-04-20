package gr.uoa.di.entities.trie.containment.mapping;

import java.util.Arrays;
import java.util.Iterator;

class _PseudoList<V> implements Cloneable,Iterable<V> {
	
	private Object[] objects;
	int length;
	
	public _PseudoList(int length){		
		this.objects = new Object[length];
		this.length = length;
	}
	
	private _PseudoList(Object[] objects){
		this.objects = objects;
		this.length = objects.length;
	}
	
	@SuppressWarnings("unchecked")
	public V get(int i) {
		return (V)objects[i];
	}
	
	public void setObject(int i,V value) {
		objects[i] = value;
	}
	
	@Override
	public _PseudoList<V> clone(){
		return new _PseudoList<V>(this.objects.clone());		
	}
	
	@Override
	public String toString() {
		return Arrays.toString(objects);
	}

	@Override
	public Iterator<V> iterator() {
		
		return new  Iterator<V>() {
			int current = 0;

			@Override
			public boolean hasNext() {
				return current<length;
			}

			@SuppressWarnings("unchecked")
			@Override
			public V next() {
				return (V)objects[current++];
			}
			
		};
	}
}
