package gr.uoa.di.entities.gspan.infrequentLabelRemoval;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import gr.uoa.di.entities.graph.GraphVS;
import gr.uoa.di.entities.graph.PatternVS;
import gr.uoa.di.entities.gspan.gspanGraph.GSpanGraph;
import gr.uoa.di.entities.gspan.gspanGraph.IntPair;
import gr.uoa.di.entities.gspan.gspanGraph.IntTriple;
import gr.uoa.di.interfaceAdapters.controllers.formatters.gSpan.translators.GSpanTranslator;
import gr.uoa.di.interfaceAdapters.debug.MyDebug;

/**
 * @author theofilos a LabelRemover preprocesses a GSpanGraph workload in order
 *         to accelerate the execution of the GSPan algorithm on it. It is
 *         performed in two steps:
 * 
 *         1) It parses all graphs to find frequent label triples. 2) It removes
 *         infrequent labels triples.
 *
 */

class _GSPanPreprocessor implements GSPanPreprocessor {
	private HashMap<IntTriple, Integer> tripleFrequency = new HashMap<IntTriple, Integer>();

	/**
	 * @param gSpanGraphIterator an iterator on a collection of GSPan graphs. the
	 *                           specific method internally keeps statistics on the
	 *                           triple occurrences of the GSPan collection.
	 * 
	 */
	@Override
	public void createLabelStatistics(GraphVS graph) {
		// Add label statistics for triple
		if (graph == null)
			return;
		parseGraph(GSpanTranslator.getGSpanGraph(graph));
	}

	@Override
	public HashMap<GSpanGraph, List<Integer>> mergeGraphs(Iterator<PatternVS> iterator, int minSup) {
		clearInfrequentTriples(minSup);
		HashMap<GSpanGraph, List<Integer>> out = new HashMap<>();
		while (iterator.hasNext()) {
			PatternVS pattern = iterator.next();
			GraphVS graph = pattern.getGraph();
			if (graph == null)
				continue;
			GSpanGraph gspan = GSpanTranslator.getGSpanGraph(graph);
			for (GSpanGraph cleanedGraph : clearedGRaphs(gspan)) {
				out.computeIfAbsent(cleanedGraph, key -> new ArrayList<>()).add(pattern.getId());
			}
		}
		MyDebug.println("Merged Graphs:" + out.size());
		return out;
	}

	private Collection<GSpanGraph> clearedGRaphs(GSpanGraph graph) {
		List<IntTriple> triples = graph.getEdgeLabeling();
		List<Integer> nodeLabels = graph.getNodeLabeling();
		Collection<GSpanGraph> output = new LinkedList<GSpanGraph>();
		_ConnectedComponents connComponents = new _ConnectedComponents(nodeLabels.size());
		// Clears the triples that are not frequent
		List<Pair<IntTriple, IntPair>> cleanTriples = triples.stream()
				.map(x -> Pair.of(
						IntTriple.of(nodeLabels.get(x.getLeft()), nodeLabels.get(x.getMiddle()), x.getRight()),
						IntPair.of(x.getLeft(), x.getMiddle())))
				.filter(pair -> tripleFrequency.containsKey(pair.getLeft())).peek(x -> {// Finds the connected
																						// components
					int from = x.getRight().getLeft();
					int to = x.getRight().getRight();
					connComponents.addEdge(from, to);
				}).sorted().collect(Collectors.toList());
		int compNum = connComponents.findConComp();
		// Fills the list for each connected component
		List<ArrayList<Pair<IntTriple, IntPair>>> connComponentsTriples = Collections.nCopies(compNum,
				new ArrayList<Pair<IntTriple, IntPair>>());
		cleanTriples.stream().forEach(x -> {
			int component = connComponents.get(x.getRight().getLeft()).getRight();
			connComponentsTriples.get(component).add(x);
		});
		// Return seperate graphs
		for (ArrayList<Pair<IntTriple, IntPair>> connComponentTriples : connComponentsTriples) {
			HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
			PseudoMeter meter = new PseudoMeter();
			ArrayList<IntTriple> triples2 = new ArrayList<IntTriple>();
			ArrayList<Integer> nodeLabels2 = new ArrayList<Integer>();
			connComponentTriples.stream().sorted().forEach(x -> {
				IntPair pair = x.getRight();
				int from = pair.getLeft();
				int to = pair.getRight();
				from = map.compute(from, (key, value) -> {
					if (value == null) {
						nodeLabels2.add(x.getLeft().getLeft());
						return meter.getValue();
					} else {
						return value;
					}
				});
				to = map.compute(to, (key, value) -> {
					if (value == null) {
						nodeLabels2.add(x.getLeft().getMiddle());
						return meter.getValue();
					} else {
						return value;
					}
				});
				triples2.add(IntTriple.of(from, to, x.getLeft().getRight()));
			});
			if (triples2.size() != 0)
				output.add(new GSpanGraph(nodeLabels2, triples2));
		}
		return output;
	}

	private static class PseudoMeter {
		private int meter = 0;

		private int getValue() {
			return meter++;
		}
	}

	private void parseGraph(GSpanGraph gSpanGraph) {
		List<Integer> nodeLabels = gSpanGraph.nodeLabels;
		for (IntTriple triple : gSpanGraph.tripleLabels) {
			IntTriple labelTriple = IntTriple.of(nodeLabels.get(triple.getLeft()), nodeLabels.get(triple.getMiddle()),
					triple.getRight());
			tripleFrequency.compute(labelTriple, (key, value) -> value == null ? 1 : value + 1);
		}
	}

	private void clearInfrequentTriples(int support) {
		Iterator<Entry<IntTriple, Integer>> iter = tripleFrequency.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<IntTriple, Integer> entry = iter.next();
			if (entry.getValue() < support) {
				iter.remove();
			}
		}
	}

}
