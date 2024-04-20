package gr.uoa.di.interfaceAdapters.controllers.formatters.gSpan.serialization;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.PatternVS;
import gr.uoa.di.entities.gspan.gspanGraph.GSpanGraph;
import gr.uoa.di.entities.helpStructures.iterators.ClosableIterator;
import gr.uoa.di.interfaceAdapters.controllers.formatters.gSpan.translators.GSpanInvTranslator;

class _GspanDeserializer implements GSpanDeserializer{
	private GSpanInvTranslator invTranslator;
	
	
	_GspanDeserializer(Dictionary dict) {
		invTranslator= GSpanInvTranslator.create(dict);
	}	

	private PatternVS deSerialize(String patternString) {
		GSpanGraph gSpan = GSpanGraph.create(patternString);
		return invTranslator.translate(gSpan);
	}

	public ClosableIterator<PatternVS>  deSerializeCollection(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		return new _PatternIterator(reader);
	}

	private class _PatternIterator implements ClosableIterator<PatternVS>  {
		private BufferedReader reader;
		private StringBuilder builder = new StringBuilder();
		private String line = null;
		private PatternVS nextGraph = null;

		private _PatternIterator(BufferedReader reader) {
			this.reader = reader;
		}

		@Override
		public boolean hasNext() {
			if (nextGraph == null) {
				try {
					nextGraph = getNext();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (nextGraph != null) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public PatternVS next() {
			if (nextGraph == null) {
				try {
					nextGraph = getNext();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			PatternVS out = nextGraph;
			nextGraph = null;
			return out;
		}

		public PatternVS getNext() throws IOException {
			while ((line = reader.readLine()) != null && (builder.length()==0 || !line.startsWith("t"))) {
				builder.append(line).append("\n");
			}
			String graphString = builder.toString();
			PatternVS out = null;
			if (graphString != null ) {
				out = deSerialize(graphString);
				if(line!=null) {
					builder = new StringBuilder(line).append("\n");
				}else {
					builder = new StringBuilder();
				}
			}
			return out;
		}

		@Override
		public void close() throws Exception {
			reader.close();
		}

	}

}
