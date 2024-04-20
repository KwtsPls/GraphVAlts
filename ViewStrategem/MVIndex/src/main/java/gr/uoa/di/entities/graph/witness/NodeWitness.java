package gr.uoa.di.entities.graph.witness;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import gr.uoa.di.entities.graph.Printable;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForNode;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForTriple;

public class NodeWitness<N extends AbstractionForNode<N, T>, T extends AbstractionForTriple<N, T>>
		implements Printable {
	private Map<Integer, N> constantNodes = null;
	private Set<N> varNodes = null;
	boolean isND = false;

	public int size() {
		return (constantNodes == null ? 0 : constantNodes.size()) + (varNodes == null ? 0 : varNodes.size());
	}

	NodeWitness(N node) {
		if (node.isConstant()) {
			constantNodes = new HashMap<Integer, N>();
			constantNodes.put(node.getLabel(), node);
		} else {
			varNodes = new HashSet<N>();
			varNodes.add(node);
		}
	}

	public List<TripleWitness<N, T>> incomingEdges = null;
	public List<TripleWitness<N, T>> outgoingEdges = null;

	@Override
	public String toString() {
		return toCompactString();
	}

	protected void sortEdges() {
		if (outgoingEdges != null) {
			Collections.sort(outgoingEdges, WitnessComparator.edgeWitnessComparator);
			removeDublicates(outgoingEdges.listIterator());
		}
		if (incomingEdges != null) {
			Collections.sort(incomingEdges, WitnessComparator.edgeWitnessComparator);
			removeDublicates(incomingEdges.listIterator());
		}
	}

	private static <N extends AbstractionForNode<N, T>, T extends AbstractionForTriple<N, T>> void removeDublicates(
			ListIterator<TripleWitness<N, T>> iter) {
		Integer previousLabel = null;
		while (iter.hasNext()) {
			Integer currentLabel = iter.next().predicateLabel;
			if (currentLabel.equals(previousLabel)) {
				iter.remove();
			} else {
				previousLabel = currentLabel;
			}
		}
	}

	public boolean containsConstantLabel(int label) {
		if (constantNodes == null)
			return false;
		return constantNodes.containsKey(label);
	}

	public void forEachConstant(Consumer<N> consumer) {
		if (constantNodes != null)
			constantNodes.forEach((x, y) -> consumer.accept(y));
	}

	public void forEachNode(Consumer<N> action) {
		if (constantNodes != null) {
			constantNodes.forEach((x, y) -> action.accept(y));
		}
		if (varNodes != null) {
			varNodes.forEach(action);
		}
	}

	void addAll(NodeWitness<N, T> nodeWitness) {
		isND = true;
		if (constantNodes == null) {
			constantNodes = nodeWitness.constantNodes;
		} else {
			if (nodeWitness.constantNodes != null) {
				constantNodes.putAll(nodeWitness.constantNodes);
			}
		}
		if (varNodes == null) {
			varNodes = nodeWitness.varNodes;
		} else {
			if (nodeWitness.varNodes != null) {
				varNodes.addAll(nodeWitness.varNodes);
			}
		}
	}

	public Set<N> getNodeSet() {
		if (constantNodes == null) {
			return new HashSet<N>(varNodes);
		} else {
			HashSet<N> output = new HashSet<N>(constantNodes.values());
			if (varNodes == null) {
				return output;
			}
			output.addAll(varNodes);
			return output;
		}
	}

	public N getConstantLabel(int subjectId) {
		if (constantNodes == null)
			return null;
		return constantNodes.get(subjectId);
	}
	
	@Override
	public String print(Function<Printable, String> function) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");

		if (constantNodes != null) {
			constantNodes.forEach((x, y) -> builder.append(function.apply(y)).append(","));
		}
		if (varNodes != null) {
			varNodes.forEach(x -> builder.append(function.apply(x)).append(","));
		}
		builder.setCharAt(builder.length() - 1, ']');

		return builder.toString();
	}

}
