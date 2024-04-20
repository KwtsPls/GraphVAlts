package gr.uoa.di.interfaceAdapters.controllers.formatters.gSpan.serialization;

import java.io.IOException;
import java.util.Iterator;

import gr.uoa.di.entities.graph.PatternVS;

public interface GSpanSerializer {

	static GSpanSerializer create(String filename) {
		return new _GSpanSerializer(filename);
	}

	void serializeCollection(Iterator<PatternVS> valueIterator) throws IOException;

}
