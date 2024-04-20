package gr.uoa.di.entities.trie.containment;

import java.util.LinkedList;
import java.util.List;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.witness.TripleWitness;
import gr.uoa.di.entities.helpStructures.tuples.IntTriple;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForNode;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForTriple;
import gr.uoa.di.entities.graph.regular.helpers.EdgeMatcher;
import gr.uoa.di.entities.graph.witness.NodeWitness;
import gr.uoa.di.entities.trie.containment.mapping.ContainmentMapping;
import gr.uoa.di.entities.trie.containment.structures.shared.ConstainmentStack;
import gr.uoa.di.entities.trie.containment.structures.witnessing.WExaminedEdges;

abstract class WitnessDisambiguate<N extends AbstractionForNode<N, T>, T extends AbstractionForTriple<N, T>, W> {

	static final int firstVarId = Dictionary.firstVarId;

	List<ContainmentMapping<N, T, W>> disambiguate(WExaminedEdges<TripleWitness<N, T>> examinedEdges, //
			ConstainmentStack<NodeWitness<N, T>> containmentMapping, W metadata) {
		int answerSize = containmentMapping.getPosition() - firstVarId + 1;
		ContainmentMapping<N, T, W> deterministicMap = ContainmentMapping.create(answerSize,
				examinedEdges.getPosition() + 1);
		while (containmentMapping.pop() != null) {
			NodeWitness<N, T> witness = containmentMapping.getCurrentEntry();
			int position = containmentMapping.getPosition();
			if (witness.size() == 1) {
				witness.forEachNode(x -> deterministicMap.mapVarId2Node(position, x));
			}
			containmentMapping = containmentMapping.pop();
		}
		List<ContainmentMapping<N, T, W>> containmentMappings = new LinkedList<ContainmentMapping<N, T, W>>();
		disambiguate(deterministicMap, containmentMappings, examinedEdges, metadata);
		return containmentMappings;
	}

	private void disambiguate(ContainmentMapping<N, T, W> deterministicMap,
			List<ContainmentMapping<N, T, W>> containmentMappings, WExaminedEdges<TripleWitness<N, T>> ndEdges,
			W metadata) {
		if (ndEdges.pop() != null) {
			TripleWitness<N, T> edge = ndEdges.getEdge();
			IntTriple edgeTriple = ndEdges.getEdgeTriple();
			if (ndEdges.isND()) {
				int subjectId = ndEdges.getSubjectID();
				int objectId = ndEdges.getObjectID();
				int predicateId = ndEdges.getPredicateID();
				ndEdges = ndEdges.pop();
				//
				N subject = Dictionary.isConstant(subjectId) ? edge.subject.getConstantLabel(subjectId)
						: deterministicMap.getMappedNode(subjectId);
				N object = Dictionary.isConstant(objectId) ? edge.object.getConstantLabel(objectId)
						: deterministicMap.getMappedNode(objectId);

				if (subject != null) {
					if (object != null) {
						T simpleEdge;
						if ((simpleEdge = EdgeMatcher.matchOutgoingEdges(subject, predicateId, object)
//								subject.matchOutgoingEdges(predicateId, object)
						) != null) {
							deterministicMap.addEdge(edgeTriple, simpleEdge);
							disambiguate(deterministicMap, containmentMappings, ndEdges, metadata);
							return;
						}
					} else {
						List<T> matchedEdges = EdgeMatcher.matchOutgoingEdges(subject, predicateId);
//								subject.matchOutgoingEdges(predicateId);
						int matchedCount = matchedEdges.size();
						if (matchedCount != 0) {
							WExaminedEdges<TripleWitness<N, T>> ndEdges2 = ndEdges;
							matchedEdges.forEach(simpleEdge -> {
								ContainmentMapping<N, T, W> newMap = deterministicMap.clone();
								newMap.mapVarId2Node(objectId, simpleEdge.getObject());
								newMap.addEdge(edgeTriple, simpleEdge);
								disambiguate(newMap, containmentMappings, ndEdges2, metadata);
							});
						}
					}
				} else if (object != null) {
					List<T> matchedEdges = EdgeMatcher.matchIncomingEdges(object, predicateId);
//							object.matchIncomingEdges(predicateId);
					int matchedCount = matchedEdges.size();
					if (matchedCount != 0) {
						WExaminedEdges<TripleWitness<N, T>> ndEdges2 = ndEdges;
						matchedEdges.forEach(simpleEdge -> {
							ContainmentMapping<N, T, W> newMap = deterministicMap.clone();
							newMap.mapVarId2Node(subjectId, simpleEdge.getSubject());
							newMap.addEdge(edgeTriple, simpleEdge);
							disambiguate(newMap, containmentMappings, ndEdges2, metadata);
						});
					}
				} else {
					WExaminedEdges<TripleWitness<N, T>> ndEdges2 = ndEdges;
					edge.subject.forEachNode(subject2 -> {
						EdgeMatcher.matchOutgoingEdges(subject2, predicateId)
//						subject2.matchOutgoingEdges(predicateId)
								.forEach(simpleEdge -> {
									ContainmentMapping<N, T, W> newMap = deterministicMap.clone();
									newMap.mapVarId2Node(subjectId, subject2);
									newMap.mapVarId2Node(objectId, simpleEdge.getObject());
									newMap.addEdge(edgeTriple, simpleEdge);
									disambiguate(newMap, containmentMappings, ndEdges2, metadata);
								});
					});
				}
			} else {
				deterministicMap.addEdge(edgeTriple, edge.edgeSimple);
				ndEdges = ndEdges.pop();
				disambiguate(deterministicMap, containmentMappings, ndEdges, metadata);
			}
		} else {
			deterministicMap.setMetadata(metadata);
			containmentMappings.add(deterministicMap);
		}
	}
}
