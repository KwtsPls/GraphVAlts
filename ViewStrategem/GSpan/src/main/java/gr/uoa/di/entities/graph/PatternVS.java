package gr.uoa.di.entities.graph;

import java.util.Arrays;
import java.util.TreeSet;
import java.util.function.Function;

import gr.uoa.di.entities.dictionary.Dictionary;

public class PatternVS implements Printable {
	private int id;
	private GraphVS graph;
	private int support = 1;
	private TreeSet<Integer> where;

	public static PatternVS create(GraphVS graph, int id, int support, Integer[] where) {
		return new PatternVS(graph, id, support, where);
	}

	private PatternVS(GraphVS graph, int patternId, int support, Integer[] where) {
		this.id = patternId;
		this.graph = graph;
		this.support = support;
		this.where = new TreeSet<>(Arrays.asList(where));
	}

	@Override
	public String toString() {
		return toCompactString();
	}

	public boolean isOrdered() {
		int[] varCounter = { Dictionary.firstVarId - 1 };
		for (TripleVS triple : this.getGraph()) {
			if (!validSequence(varCounter, triple.getSubject()))
				return false;
			if (!validSequence(varCounter, triple.getObject()))
				return false;
		}
		return true;
	}

	private boolean validSequence(int[] varCounter, NodeVS node) {
		if (node.isConstant()) {
			return true;
		}
		if (node.getLabel() <= varCounter[0]) {
			return true;
		}
		if (node.getLabel() == varCounter[0] + 1) {
			varCounter[0]++;
			return true;
		}
		return false;

	}

	public int getId() {
		return id;
	}

	public int getSupport() {
		return support;
	}

	public void setSupport(int support) {
		this.support = support;
	}

	public void setId(int id2) {
		id = id2;
	}

	public TreeSet<Integer> getWhere() {
		return where;
	}

	public void setWhere(Integer[] where) {
		this.where = new TreeSet<>(Arrays.asList(where));
		;
	}

	public GraphVS getGraph() {
		return graph;
	}

	@Override
	public String print(Function<Printable, String> function) {
		return new StringBuffer(function.apply(getGraph())).append(" [supp: ").append(this.getSupport())
				.append(", id: ").append(this.getId()).append("]").toString();
	}

}
