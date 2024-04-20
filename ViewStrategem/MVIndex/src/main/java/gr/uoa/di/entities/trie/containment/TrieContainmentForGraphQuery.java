package gr.uoa.di.entities.trie.containment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForGraph;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForNode;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForTriple;
import gr.uoa.di.entities.graph.witness.GraphWitness;
import gr.uoa.di.entities.graph.witness.WitnessFactory;
import gr.uoa.di.entities.trie.Branch;
import gr.uoa.di.entities.trie.TrieMetadata;
import gr.uoa.di.entities.trie.TrieVertex;
import gr.uoa.di.entities.trie.containment.mapping.ContainmentMapping;
import gr.uoa.di.entities.trie.containment.structures.fGraph.FAncenstryStack;
import gr.uoa.di.entities.trie.containment.structures.fGraph.FGraphExaminedEdges;
import gr.uoa.di.entities.trie.containment.structures.shared.ConstainmentStack;

public class TrieContainmentForGraphQuery<	N extends AbstractionForNode<N, T>, 
											T extends AbstractionForTriple<N, T>, 
											I, M extends TrieMetadata<I>>
		extends TrieContainmentForWitness<N, T, I, M> {

	public List<ContainmentMapping<N, T, M>> contains(TrieVertex<I,M> trieVertex, AbstractionForGraph<N, T, ?> graph) {
		GraphWitness<N, T> witness = WitnessFactory.witnessOf(graph);
		if (witness == null) {
			return containsFgraph(trieVertex, graph);
		} else {
			return contains(trieVertex, witness);
		}
	}

	private ContainmentMapping<N, T, M> disambiguate(ConstainmentStack<N> containmentMapping,
			FGraphExaminedEdges<T> examinedEdges, M metadata) {
		int answerSize = containmentMapping.getPosition() - firstVarId + 1;
		ContainmentMapping<N, T, M> answer = ContainmentMapping.create(answerSize, examinedEdges.getPosition() + 1);
		while (containmentMapping.pop() != null) {
			answer.mapVarId2Node(containmentMapping.getPosition(), containmentMapping.getCurrentEntry());
			containmentMapping = containmentMapping.pop();
		}
		while (examinedEdges.pop() != null) {
			answer.addEdge(examinedEdges.getEdgeTriple(), examinedEdges.getEdge());
			examinedEdges = examinedEdges.pop();
		}
		answer.setMetadata(metadata);
		return answer;
	}

	private List<ContainmentMapping<N, T, M>> containsFgraph(TrieVertex<I,M> trieVertex, AbstractionForGraph<N, T, ?> graph) {
		List<ContainmentMapping<N, T, M>> output = new LinkedList<ContainmentMapping<N, T, M>>();
		graph.forEachNode(anchor -> {
					contains(trieVertex, anchor, output);
				});
		return output;
	}

	private List<ContainmentMapping<N, T, M>> contains(TrieVertex<I,M> trieVertex, N anchor,
			List<ContainmentMapping<N, T, M>> output) {
		for (Branch<I,M> branch : getMatchedAnchorLabel(trieVertex, anchor)) {
			ConstainmentStack<N> containmentMapping = ConstainmentStack.create();
			if (Dictionary.isVariable(branch.labels[0])) {
				containmentMapping = containmentMapping.pushMap(branch.labels[0], anchor);
			}
			examineBranch(1, branch.labels, branch.vertex, //
					branch.labels[0], 0, //
					anchor, true, true, //
					anchor.getOutgoingTriples(), 0, FAncenstryStack.create(), //
					FGraphExaminedEdges.create(), containmentMapping, output);
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
			N currentNode,
			/* true if we examine a predicate */
			/* false if we examine the subject/object of an Edge */
			boolean predicateExamination,
			/* true (false) if we examine the object (subject) of an Edge */
			boolean outGoingExamination,
			/* Contains the currently examined (incoming/outgoing) edges */
			List<T> edges,
			/* Contains the cursor on the examined current node's edges */
			int edgeCursor,
			/* Contains information on the current node's ancestor and */
			/* the corresponding info on the traversal of the graph witness */
			FAncenstryStack<N> ancestorStack,
			/* Contains a sequence of the graph-witness edges */
			/* that have been examined */
			FGraphExaminedEdges<T> examinedEdges,
			/* Contains the corresponding running mapping */
			ConstainmentStack<N> containmentMapping,
			/* Contains a collection of containment mappings */
			List<ContainmentMapping<N, T, M>> output) {
		int label;
		T edge = edges.isEmpty() ? null : edges.get(edgeCursor);
		while (trieCursor < trieLabels.length) {
			label = trieLabels[trieCursor++];
			// Starts The examination
			if (label == Dictionary.invEdgeMark) {
				edges = currentNode.getIncomingTriples();
				outGoingExamination = false;
				edgeCursor = 0;
			} else if (Dictionary.endEdgeMark == label) {
				currentNode = ancestorStack.getNode();
				edgeCursor = ancestorStack.getCursor();
				outGoingExamination = ancestorStack.getOutgoing();
				examinedNodeID = ancestorStack.getExaminedNodeID();
				if (currentNode != null) {
					edges = outGoingExamination ? currentNode.getOutgoingTriples() : currentNode.getIncomingTriples();
				}
				ancestorStack = ancestorStack.pop();
			} else if (predicateExamination) {
				predicateID = label;
				predicateExamination = false;
				if (edges.isEmpty())
					return;
				if (Dictionary.isVariable(label)) {
					System.err.println("Den exetazetai!!!!!!");
				} else {
					int size = edges.size();
					while ((edge = edges.get(edgeCursor)).getPredicateLabel() < label) {
						if (++edgeCursor == size)
							return;
					}
					if (edge.getPredicateLabel() > label) {
						return;
					} else {
						int edgeCursorHigh = edgeCursor;
						while (edgeCursorHigh + 1 < edges.size()
								&& edges.get(edgeCursorHigh + 1).getPredicateLabel() == label) {
							edgeCursorHigh++;
						}
						if (edgeCursor != edgeCursorHigh++) {
							while (edgeCursor != edgeCursorHigh) {
								examineBranch(trieCursor, trieLabels, destinationVertex, //
										examinedNodeID, predicateID, //
										currentNode, predicateExamination, outGoingExamination, edges, edgeCursor++,
										ancestorStack, examinedEdges, containmentMapping, output);

							}
							return;
						}

					}
				}
			} else {
				N target = outGoingExamination ? edge.getObject() : edge.getSubject();
				if (Dictionary.isVariable(label)) {
					containmentMapping = containmentMapping.pushMap(label, target);
					if (containmentMapping == null)
						return;
				} else {
					// totalTime=totalTime-System.nanoTime();
					if (target.getLabel() != label)
						return;
					// totalTime=totalTime+System.nanoTime();
				}
				examinedEdges = examinedEdges.push(edge, outGoingExamination ? examinedNodeID : label, predicateID,
						outGoingExamination ? label : examinedNodeID);
				ancestorStack = ancestorStack.push(currentNode, edgeCursor, outGoingExamination, examinedNodeID);
				//
				edges = target.getOutgoingTriples();
				outGoingExamination = true;
				edgeCursor = 0;
				currentNode = target;
				examinedNodeID = label;
				//
				predicateExamination = true;
			}
		}
		if (destinationVertex.isInserted) {
			output.add((disambiguate(containmentMapping, examinedEdges, destinationVertex.metadata)));
		}
		Collection<Branch<I,M>> matchedBranches;
		if (predicateExamination) {
			matchedBranches = getMatchedEdges(destinationVertex, currentNode, outGoingExamination, 0);
		} else {
			matchedBranches = getMatchedEdges(destinationVertex, outGoingExamination, edge);
		}
		for (Branch<I,M> branch : matchedBranches) {
			examineBranch(0, branch.labels, branch.vertex, //
					examinedNodeID, predicateID, //
					currentNode, predicateExamination, //
					outGoingExamination, edges, edgeCursor, //
					ancestorStack, examinedEdges, //
					containmentMapping, output);
		}
	}

	private Collection<Branch<I,M>> getMatchedAnchorLabel(TrieVertex<I,M> trieVertex, N anchor) {
		List<Branch<I,M>> matchedBranches = new LinkedList<Branch<I,M>>();
		// Matches all branches starting with a viarable or with a mark symbol
		Map<Integer, Branch<I,M>> varBranches = trieVertex.varBranches;
		if (varBranches != null) {
			matchedBranches.addAll(varBranches.values());
		}
		// Matches all branches starting with a constant within the witness
		if (anchor.isConstant()) {
			int label = anchor.getLabel();
			Branch<I,M> answer = trieVertex.get(label);
			if (answer != null)
				matchedBranches.add(answer);
		}
		return matchedBranches;
	}

	private Collection<Branch<I,M>> getMatchedEdges(TrieVertex<I,M> trieVertex, boolean objectExamination, T edge) {
		List<Branch<I,M>> matchedBranches = new ArrayList<Branch<I,M>>();
		Map<Integer, Branch<I,M>> constantBranches = trieVertex.constantBranches;
		Map<Integer, Branch<I,M>> varBranches = trieVertex.varBranches;
		if (varBranches != null) {
			matchedBranches.addAll(varBranches.values());
		}
		if (constantBranches == null) {
			return matchedBranches;
		}
		//
		N target;
		if (objectExamination) {
			target = edge.getObject();
		} else {
			target = edge.getSubject();
		}
		Branch<I,M> branch = constantBranches.get(target.getLabel());
		if (branch != null) {
			matchedBranches.add(branch);
		}
		return matchedBranches;
	}

	private Collection<Branch<I,M>> getMatchedEdges(TrieVertex<I,M> trieVertex, N anchor, boolean objectExamination,
			int edgeCursor) {
		List<Branch<I,M>> matchedBranches = new ArrayList<Branch<I,M>>();
		// Adding all variable-beginning branches
		Map<Integer, Branch<I,M>> varBranches = trieVertex.varBranches;
		if (varBranches != null) {
			matchedBranches.addAll(varBranches.values());
		}
		// Returns if there are no constant-beginning branches
		Map<Integer, Branch<I,M>> constantBranches = trieVertex.constantBranches;
		if (constantBranches == null) {
			return matchedBranches;
		}
		//
		List<T> edges = objectExamination ? anchor.getOutgoingTriples() : anchor.getIncomingTriples();
		if (edges.isEmpty()) {
			return matchedBranches;
		}
		Branch<I,M> branch = null;
		Branch<I,M> newBranch = null;
		for (; edgeCursor < edges.size(); edgeCursor++) {
			int label = edges.get(edgeCursor).getPredicateLabel();
			if (!Dictionary.isConstant(label))
				break;
			if ((newBranch = constantBranches.get(label)) != null) {
				if (branch != newBranch) {
					branch = newBranch;
					matchedBranches.add(branch);
				}
			}
		}
		return matchedBranches;
	}

}
