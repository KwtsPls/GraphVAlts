package gr.uoa.di.entities.gspan.infrequentLabelRemoval;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import gr.uoa.di.entities.graph.GraphVS;
import gr.uoa.di.entities.graph.PatternVS;
import gr.uoa.di.entities.gspan.gspanGraph.GSpanGraph;

/**
 * @author theofilos a LabelRemover preprocesses a GSpanGraph workload in order
 *         to accelerate the execution of the GSPan algorithm on it. It is
 *         performed in two steps:
 * 
 *         1) It parses all graphs to find frequent label triples. 2) It removes
 *         infrequent labels triples.
 *
 */

public interface GSPanPreprocessor {

	static GSPanPreprocessor create() {
		return new _GSPanPreprocessor();
	}

	void createLabelStatistics(GraphVS graph);

	/**
	 * @param gSpanGraphIterator an iterator on a collection of GSPan graphs.
	 * 
	 * @param queries
	 * @return a set of GSpanGraphs along with the # appearances of each GSPanGraph
	 *         and the original queries in which the merged graph appears
	 */

	/**
	 * The specific method takes as input a collection of queries (in GSPan form)
	 * and summarizes it to a collection of GSpan graphs along with their
	 * multiplicities and the original queries that contained them.
	 * 
	 * @param iterator an iterator on a collection of GSPan graphs.
	 * @param minSup   the corresponding support for the GSpan algorithm. Labels
	 *                 under this support are ignored.
	 * @return A set of GSpanGraphs along with the # appearances of each GSPanGraph
	 *         and the original queries in which the merged graph appeared.
	 */
	HashMap<GSpanGraph, List<Integer>> mergeGraphs(Iterator<PatternVS> iterator, int minSup);

}
