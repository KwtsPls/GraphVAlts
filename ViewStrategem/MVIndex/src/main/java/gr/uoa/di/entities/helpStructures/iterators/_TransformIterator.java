package gr.uoa.di.entities.helpStructures.iterators;

import java.util.Iterator;
import java.util.function.Function;

public class _TransformIterator<S,T> implements TransformIterator<S, T>{
	
	Iterator<S> iter; 
	Function<S,T> func;
	
	_TransformIterator(Iterator<S> iter, Function<S,T> func){
		this.iter=iter;
		this.func=func;
	}

	@Override
	public boolean hasNext() {
		return iter.hasNext();
	}

	@Override
	public T next() {
		return func.apply(iter.next());
	}

}
