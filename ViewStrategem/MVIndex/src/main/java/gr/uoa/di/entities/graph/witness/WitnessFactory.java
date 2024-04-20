package gr.uoa.di.entities.graph.witness;

import java.util.ArrayList;
import java.util.List;

import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForGraph;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForNode;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForTriple;
import gr.uoa.di.entities.graph.regular.abstractions.Term;

public class WitnessFactory {

	private static <N extends AbstractionForNode<N, T>, T extends AbstractionForTriple<N, T>> void merge(N node1,
			N node2, List<NodeWitness<N, T>> equivalences) {
		equivalences.get(node1.getPositionInGraph()).addAll(equivalences.get(node2.getPositionInGraph()));
		equivalences.set(node2.getPositionInGraph(), equivalences.get(node1.getPositionInGraph()));

	}

	public static <N extends AbstractionForNode<N, T>, T extends AbstractionForTriple<N, T>> GraphWitness<N, T> witnessOf(
			AbstractionForGraph<N, T, ?> graph) {
		List<NodeWitness<N, T>> equivalences = new ArrayList<NodeWitness<N, T>>(graph.getNodeCount());
		graph.forEachNode(node -> {
			NodeWitness<N, T> witness = new NodeWitness<N, T>(node);
			equivalences.add(witness);
		});
		//
		boolean[] hasEqSet = new boolean[] { false };
		graph.forEachNode(node -> {
			N prevTarget = null;
			Term prevPredicate = null;
			for (T edge : node.getOutgoingTriples()) {
				if (prevPredicate != null && prevPredicate.getLabel() == edge.getPredicate().getLabel()) {
					hasEqSet[0] = true;
					merge(prevTarget, edge.getObject(), equivalences);
				}
				prevPredicate = edge.getPredicate();
				prevTarget = edge.getObject();
			}
			prevTarget = null;
			prevPredicate = null;
			for (T edge : node.getIncomingTriples()) {
				if (prevPredicate != null && prevPredicate.getLabel() == edge.getPredicate().getLabel()) {
					hasEqSet[0] = true;
					merge(prevTarget, edge.getSubject(), equivalences);
				}
				prevPredicate = edge.getPredicate();
				prevTarget = edge.getSubject();
			}
		});

		if (hasEqSet[0]) {
			GraphWitness<N, T> graphWitness = new GraphWitness<N, T>();
			for (NodeWitness<N, T> witness : equivalences) {
				if (witness == null)
					continue;
				witness.forEachNode(node -> {
					for (T edge : node.getOutgoingTriples()) {
						NodeWitness<N, T> object = equivalences.get(edge.getObject().getPositionInGraph());
						if (object == null)
							continue;
						TripleWitness<N, T> edgeWitness = new TripleWitness<N, T>(witness, object, edge);
						//
						if (witness.outgoingEdges == null)
							witness.outgoingEdges = new ArrayList<TripleWitness<N, T>>();
						witness.outgoingEdges.add(edgeWitness);
						if (object.incomingEdges == null)
							object.incomingEdges = new ArrayList<TripleWitness<N, T>>();
						object.incomingEdges.add(edgeWitness);
					}
					for (T edge : node.getIncomingTriples()) {
						NodeWitness<N, T> subject = equivalences.get(edge.getSubject().getPositionInGraph());
						if (subject == null)
							continue;
						TripleWitness<N, T> edgeWitness = new TripleWitness<N, T>(subject, witness, edge);
						if (subject.outgoingEdges == null)
							subject.outgoingEdges = new ArrayList<TripleWitness<N, T>>();
						subject.outgoingEdges.add(edgeWitness);
						if (witness.incomingEdges == null)
							witness.incomingEdges = new ArrayList<TripleWitness<N, T>>();
						witness.incomingEdges.add(edgeWitness);
					}
					equivalences.set(node.getPositionInGraph(), null);
				});
				graphWitness.nodes.add(witness);
			}
			graphWitness.sortAndRemoveDublicateEdges();
			return graphWitness;
		} else {
			return null;
		}
	}

}
