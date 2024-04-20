package gr.uoa.di.interfaceAdapters.controllers.formatters.gSpan.translators;

import gr.uoa.di.entities.graph.GraphVS;
import gr.uoa.di.entities.graph.PatternVS;
import gr.uoa.di.entities.gspan.gspanGraph.GSpanGraph;

public interface GSpanTranslator {

	static GSpanGraph getGSpanGraph(GraphVS graph) {
		return  new _GSpanTranslator().getGSpanGraph(graph);
	}

	static GSpanGraph create(PatternVS pattern) {
		return new _GSpanTranslator().translate(pattern.getGraph(), pattern.getId(), pattern.getWhere());
	}

}
