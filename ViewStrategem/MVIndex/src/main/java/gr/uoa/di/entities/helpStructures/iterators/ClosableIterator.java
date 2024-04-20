package gr.uoa.di.entities.helpStructures.iterators;

import java.util.Iterator;


public interface ClosableIterator<E> extends  Iterator<E>, AutoCloseable{
	

}
