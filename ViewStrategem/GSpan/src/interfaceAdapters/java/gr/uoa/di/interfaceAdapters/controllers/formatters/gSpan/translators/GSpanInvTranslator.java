package gr.uoa.di.interfaceAdapters.controllers.formatters.gSpan.translators;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.PatternVS;
import gr.uoa.di.entities.gspan.gspanGraph.GSpanGraph;

public interface GSpanInvTranslator {

	static GSpanInvTranslator create(Dictionary dict) {
		return new _GspanInvTranslator(dict);
	}

	PatternVS translate(GSpanGraph gSpan);

}
