package gr.uoa.di.entities.graph.witness;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import gr.uoa.di.entities.graph.Printable;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForNode;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForTriple;

public class GraphWitness<N extends AbstractionForNode<N, T>, T extends AbstractionForTriple<N, T>>
		implements Printable {
	public List<NodeWitness<N, T>> nodes = new LinkedList<NodeWitness<N, T>>();

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (NodeWitness<N, T> node : nodes) {
			if (node.outgoingEdges == null)
				continue;
			for (TripleWitness<N, T> edge : node.outgoingEdges) {
				builder.append(edge.toString());
			}
		}
		return builder.toString();
	}
	
	void sortAndRemoveDublicateEdges() {
		for (NodeWitness<N, T> node : nodes) {
			node.sortEdges();
		}

	}

	@Override
	public String print(Function<Printable, String> function) {
		StringBuilder builder = new StringBuilder();
		for (NodeWitness<N, T> node : nodes) {
			if (node.outgoingEdges == null)
				continue;
			for (TripleWitness<N, T> edge : node.outgoingEdges) {
				builder.append(function.apply(edge));
			}
		}
		return builder.toString();
	}

}
