package gr.uoa.di.interfaceAdapters.workloads;

import java.io.IOException;
import java.util.Iterator;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForGraph;


public interface GraphQueryIterator<G extends AbstractionForGraph<?, ?, G>> extends Iterator<G>,AutoCloseable{

	
	@Override
	void close() throws IOException;	
	String getName(); 
	Dictionary getDictionary();

}
