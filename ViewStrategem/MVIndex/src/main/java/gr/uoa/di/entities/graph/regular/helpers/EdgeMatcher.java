package gr.uoa.di.entities.graph.regular.helpers;

import java.util.Collections;
import java.util.List;

import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForNode;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForTriple;
import gr.uoa.di.entities.graph.regular.abstractions.Term;
import gr.uoa.di.entities.trie.graphAbstraction.InterfaceForNode;
import gr.uoa.di.entities.trie.graphAbstraction.InterfaceForTriple;

public class EdgeMatcher {
	
	
	public static <N extends AbstractionForNode<N,T>&InterfaceForNode,T extends AbstractionForTriple<N,T>> T matchOutgoingEdges(N subject, int predicateId, N object) {
		List<T> outgoingEdges = subject.getOutgoingTriples();
		int position = Collections.binarySearch(outgoingEdges,//
				InterfaceForTriple.createDummy(subject, Term.getConstantTerm(predicateId, null), object),//
				ComparatorsForGraph.tripleComparator);
		if (position < 0) {
			return null;
		}
		return outgoingEdges.get(position);
	}

	

	
	public static <N extends AbstractionForNode<N,T>&InterfaceForNode,T extends AbstractionForTriple<N,T>> List<T> matchIncomingEdges(N object, int predicateId) {
		List<T> incomingEdges = object.getIncomingTriples();
		int position = Collections.binarySearch(incomingEdges,
				InterfaceForTriple.createDummy(null, Term.getConstantTerm(predicateId, null), null),
				ComparatorsForGraph.tripleComparatorWithNulls);
		if (position < 0) {
			return Collections.emptyList();
		}
		int minPosition = position;
		int maxPosition = position;
		//
		while (minPosition > 0 && incomingEdges.get(minPosition - 1).getPredicateLabel() == predicateId) {
			minPosition--;
		}
		while (maxPosition < incomingEdges.size() - 1
				&& incomingEdges.get(maxPosition + 1).getPredicateLabel() == predicateId) {
			maxPosition++;
		}
		return incomingEdges.subList(minPosition, maxPosition + 1);
	}

	
	public static <N extends AbstractionForNode<N,T>&InterfaceForNode,T extends AbstractionForTriple<N,T>> List<T> matchOutgoingEdges(N subject, int predicateId) {
		List<T> outgoingEdges = subject.getOutgoingTriples();
		int position = Collections.binarySearch(outgoingEdges,
				InterfaceForTriple.createDummy(null, Term.getConstantTerm(predicateId, null), null),
				ComparatorsForGraph.tripleComparatorWithNulls);
		if (position < 0) {
			return Collections.emptyList();
		}
		int minPosition = position;
		int maxPosition = position;
		//
		while (minPosition > 0 && outgoingEdges.get(minPosition - 1).getPredicateLabel() == predicateId) {
			minPosition--;
		}
		while (maxPosition < outgoingEdges.size() - 1
				&& outgoingEdges.get(maxPosition + 1).getPredicateLabel() == predicateId) {
			maxPosition++;
		}
		return outgoingEdges.subList(minPosition, maxPosition + 1);
	}

}
