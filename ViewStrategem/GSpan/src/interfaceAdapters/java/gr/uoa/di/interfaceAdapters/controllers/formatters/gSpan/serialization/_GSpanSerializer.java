package gr.uoa.di.interfaceAdapters.controllers.formatters.gSpan.serialization;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import gr.uoa.di.entities.graph.PatternVS;
import gr.uoa.di.entities.gspan.gspanGraph.GSpanGraph;
import gr.uoa.di.interfaceAdapters.controllers.formatters.gSpan.translators.GSpanTranslator;

class _GSpanSerializer implements GSpanSerializer{

	private String filename;
	
	_GSpanSerializer(String filename){
		this.filename = filename;
	}
	
	public void serializeCollection(Iterator<PatternVS> patternIter) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));		
		while(patternIter.hasNext()) {
			PatternVS pattern = patternIter.next();
			GSpanGraph gspanGraph = GSpanTranslator.create(pattern);
			writer.write(gspanGraph.toString());
			writer.write("\n");
		}
		writer.close();
	}
	
}
