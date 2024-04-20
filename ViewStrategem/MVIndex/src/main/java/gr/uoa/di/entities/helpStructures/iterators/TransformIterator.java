package gr.uoa.di.entities.helpStructures.iterators;

import java.util.Iterator;
import java.util.function.Function;

public interface TransformIterator<S, T> extends Iterator<T> {
	
	static public <S,T>  TransformIterator<S, T>  create(Iterator<S> iterator, Function<S,T> func){
		return new _TransformIterator<S, T>(iterator,func);
	}

	@Override
	boolean hasNext();

	@Override
	T next();

}