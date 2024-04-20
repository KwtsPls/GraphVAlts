package gr.uoa.di.entities.trie.containment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.witness.TripleWitness;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForNode;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForTriple;
import gr.uoa.di.entities.graph.witness.GraphWitness;
import gr.uoa.di.entities.graph.witness.NodeWitness;
import gr.uoa.di.entities.trie.Branch;
import gr.uoa.di.entities.trie.TrieMetadata;
import gr.uoa.di.entities.trie.TrieVertex;
import gr.uoa.di.entities.trie.containment.mapping.ContainmentMapping;
import gr.uoa.di.entities.trie.containment.structures.shared.ConstainmentStack;
import gr.uoa.di.entities.trie.containment.structures.witnessing.WAncenstryStack;
import gr.uoa.di.entities.trie.containment.structures.witnessing.WExaminedEdges;

abstract class TrieContainmentForWitness<	N extends AbstractionForNode<N, T>, 
											T extends AbstractionForTriple<N, T>,
											I, M extends TrieMetadata<I>>
		extends WitnessDisambiguate<N, T, M> {

	private List<ContainmentMapping<N, T, M>> contains(TrieVertex<I,M> trieVertex, NodeWitness<N, T> anchor,
			List<ContainmentMapping<N, T, M>> output) {
		for (Branch<I,M> branch : getMatchedAnchorLabel(trieVertex, anchor)) {
			ConstainmentStack<NodeWitness<N, T>> containmentMapping = ConstainmentStack.create();
			if (Dictionary.isVariable(branch.labels[0])) {
				containmentMapping = containmentMapping.pushMap(branch.labels[0], anchor);
			}
			examineBranch(1, branch.labels, branch.vertex, branch.labels[0], 0, anchor, true, true,
					//
					anchor.outgoingEdges, 0, WAncenstryStack.create(),
					//
					WExaminedEdges.createD(), containmentMapping, output);
		}
		return output;

	}

	private void examineBranch(
			/* Contains in which position the cursor of the trie is located */
			int trieCursor,
			/* Contains the edge labels of the examined branch */
			int[] trieLabels,
			/* Contains the destination vertex of the examined branch */
			TrieVertex<I,M> destinationVertex,
			/* Contains the id of the source vertex in the trie structure */
			int examinedNodeID,
			/* Contains the id of the predicate in the trie structure */
			int predicateID,
			/* Contains the currently examined node of the graph-witness */
			NodeWitness<N, T> examinedNode,
			/* true if we examine a predicate */
			/* false if we examine the subject/object of an Edge */
			boolean predicateExamination,
			/* true (false) if we examine the object (subject) of an Edge */
			boolean outGoingExamination,
			/* Contains the currently examined (incoming/outgoing) edges */
			List<TripleWitness<N, T>> edges,
			/* Contains the cursor on the examined current node's edges */
			int edgeCursor,
			/* Contains information on the current node's ancestor and */
			/* the corresponding info on the traversal of the graph witness */
			WAncenstryStack<N, T> ancestorStack,
			/* Contains a sequence of the graph-witness edges */
			/* that have been examined */
			WExaminedEdges<TripleWitness<N, T>> examinedEdges,
			/* Contains the corresponding running mapping */
			ConstainmentStack<NodeWitness<N, T>> containmentMapping,
			/* Contains a collection of containment mappings */
			List<ContainmentMapping<N, T, M>> output) {
		int label;
		TripleWitness<N, T> edge = edges == null ? null : edges.get(edgeCursor);
		while (trieCursor < trieLabels.length) {
			label = trieLabels[trieCursor++];
			// Starts The examination
			if (label == Dictionary.invEdgeMark) {
				edges = examinedNode.incomingEdges;
				outGoingExamination = false;
				edgeCursor = 0;
			} else if (Dictionary.endEdgeMark == label) {
				examinedNode = ancestorStack.getNode();
				edgeCursor = ancestorStack.getCursor();
				outGoingExamination = ancestorStack.getOutgoing();
				examinedNodeID = ancestorStack.getExaminedNodeID();
				if (examinedNode != null) {
					edges = outGoingExamination ? examinedNode.outgoingEdges : examinedNode.incomingEdges;
				}
				ancestorStack = ancestorStack.pop();
			} else if (predicateExamination) {
				if (edges == null)
					return;
				if (Dictionary.isVariable(label)) {
					System.err.println("Den exetazetai!!!!!!");
				} else {
					int size = edges.size();
					while ((edge = edges.get(edgeCursor)).predicateLabel < label) {
						if (++edgeCursor == size)
							return;
					}
					if (edge.predicateLabel > label)
						return;
				}
				predicateID = label;
				predicateExamination = false;
			} else {
				NodeWitness<N, T> target = outGoingExamination ? edge.object : edge.subject;
				if (Dictionary.isVariable(label)) {
					containmentMapping = containmentMapping.pushMap(label, target);
					if (containmentMapping == null)
						return;
				} else {
					if (!target.containsConstantLabel(label))
						return;
				}
				examinedEdges = examinedEdges.push(edge, outGoingExamination ? examinedNodeID : label, predicateID,
						outGoingExamination ? label : examinedNodeID);
				ancestorStack = ancestorStack.push(examinedNode, edgeCursor, outGoingExamination, examinedNodeID);
				//
				edges = target.outgoingEdges;
				outGoingExamination = true;
				edgeCursor = 0;
				examinedNode = target;
				examinedNodeID = label;
				//
				predicateExamination = true;
			}

		}

		if (destinationVertex.isInserted) {
			output.addAll(disambiguate(examinedEdges, containmentMapping, destinationVertex.metadata));
		}

		Collection<Branch<I,M>> matchedBranches;
		if (predicateExamination) {

			matchedBranches = getMatchedEdges(destinationVertex, examinedNode, outGoingExamination, 0);

		} else {
			matchedBranches = getMatchedEdges(destinationVertex, outGoingExamination, edge);
		}
//		Collection<Branch<V>> matchedBranches = predicateExamination
//				? getMatchedEdges(destinationVertex, currentNode, outGoingExamination, 0)
//				: getMatchedEdges(destinationVertex, outGoingExamination, edge);

		for (Branch<I,M> branch : matchedBranches) {
			examineBranch(0, branch.labels, branch.vertex, examinedNodeID, predicateID, examinedNode,
					predicateExamination, outGoingExamination,
					//
					edges, edgeCursor, ancestorStack,
					//
					examinedEdges, containmentMapping, output);
		}

	}

	private Collection<Branch<I,M>> getMatchedAnchorLabel(TrieVertex<I,M> trieVertex, NodeWitness<N, T> anchor) {
		List<Branch<I,M>> output = new ArrayList<Branch<I,M>>();
		// Matches all branches starting with a viarable or with a mark symbol
		Map<Integer, Branch<I,M>> varBranches = trieVertex.varBranches;
		if (varBranches != null) {
			output.addAll(varBranches.values());
		}
		// Matches all branches starting with a constant within the witness
		anchor.forEachConstant(x -> {
			int label = x.getLabel();
			Branch<I,M> answer = trieVertex.get(label);
			if (answer != null)
				output.add(answer);
		});

		return output;
	}

	private Collection<Branch<I,M>> getMatchedEdges(TrieVertex<I,M> trieVertex, boolean objectExamination,
			TripleWitness<N, T> edge) {
		List<Branch<I,M>> output = new ArrayList<Branch<I,M>>();
		Map<Integer, Branch<I,M>> constantBranches = trieVertex.constantBranches;
		Map<Integer, Branch<I,M>> varBranches = trieVertex.varBranches;

		if (varBranches != null) {
			output.addAll(varBranches.values());
		}
		if (constantBranches == null) {
			return output;
		}
		//
		NodeWitness<N, T> target;
		if (objectExamination) {
			target = edge.object;
		} else {
			target = edge.subject;
		}

		target.forEachConstant(node -> {
			Branch<I,M> branch = constantBranches.get(node.getLabel());
			if (branch != null) {
				output.add(branch);
			}
		});

		return output;
	}

	private Collection<Branch<I,M>> getMatchedEdges(TrieVertex<I,M> trieVertex, NodeWitness<N, T> anchor,
			boolean objectExamination, int edgeCursor) {

		List<Branch<I,M>> output = new ArrayList<Branch<I,M>>();
		// Adding all variable-beginning branches
		Map<Integer, Branch<I,M>> varBranches = trieVertex.varBranches;
		if (varBranches != null) {
			output.addAll(varBranches.values());
		}
		// Returns if there are no constant-beginning branches
		Map<Integer, Branch<I,M>> constantBranches = trieVertex.constantBranches;
		if (constantBranches == null) {
			return output;
		}
		//
		List<TripleWitness<N, T>> edges = objectExamination ? anchor.outgoingEdges : anchor.incomingEdges;
		if (edges == null) {
			return output;
		}
		Branch<I,M> branch = null;
		for (; edgeCursor < edges.size(); edgeCursor++) {
			int label = edges.get(edgeCursor).predicateLabel;
			if (!Dictionary.isConstant(label))
				break;
			if ((branch = constantBranches.get(label)) != null) {
				output.add(branch);
			}
		}
		return output;
	}

	List<ContainmentMapping<N, T, M>> contains(TrieVertex<I,M> trieVertex, GraphWitness<N, T> graphWitness) {
		List<ContainmentMapping<N, T, M>> output = new LinkedList<ContainmentMapping<N, T, M>>();
		for (NodeWitness<N, T> anchor : graphWitness.nodes) {
			contains(trieVertex, anchor, output);
		}
		return output;
	}

}
