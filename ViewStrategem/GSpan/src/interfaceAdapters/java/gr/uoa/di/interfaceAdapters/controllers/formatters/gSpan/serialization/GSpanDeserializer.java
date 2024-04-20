package gr.uoa.di.interfaceAdapters.controllers.formatters.gSpan.serialization;

import java.io.IOException;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.PatternVS;
import gr.uoa.di.entities.helpStructures.iterators.ClosableIterator;

public interface GSpanDeserializer {

	static GSpanDeserializer create(Dictionary dict) {
		return new _GspanDeserializer(dict);
	}

	ClosableIterator<PatternVS> deSerializeCollection(String patternLocation) throws IOException;
	
}
