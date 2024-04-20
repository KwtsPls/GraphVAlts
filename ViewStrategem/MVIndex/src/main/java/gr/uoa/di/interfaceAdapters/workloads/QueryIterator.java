package gr.uoa.di.interfaceAdapters.workloads;

import java.io.IOException;
import java.util.Iterator;


public abstract interface QueryIterator extends Iterator<String>,AutoCloseable{

	@Override
	abstract void close() throws IOException;
	
	abstract String getName();

}
