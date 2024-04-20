package gr.uoa.di.entities.graph.serialization;

import java.util.Iterator;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForGraph;
import gr.uoa.di.entities.trie.graphAbstraction.InterfaceForNode;
import gr.uoa.di.entities.trie.graphAbstraction.InterfaceForTriple;


public class LinearRewriter {

	public static LinearizedQuery linearize(AbstractionForGraph<?,?,?> graphSimple) {
		AbstractionForGraph<?,?,?> query = graphSimple;
		query.resetForSerialization();
		LinearizedQuery serializedForm = new LinearizedQuery();
		// int varCounter = 0;
		Iterator<Integer> iter = Dictionary.getVarIdIterator();
		//
		InterfaceForNode anchor = query.getAnchorNode();
		anchor.setVarEnumeration(iter);
		serializedForm.add(anchor.getLabel());
		visit(anchor, serializedForm, iter);
		graphSimple.reArrangeVarNodes();
		return serializedForm;
	}

	private static void visit(InterfaceForNode node, LinearizedQuery serializedForm, Iterator<Integer> iter) {
		node.visit();
		for (InterfaceForTriple edge : node.getOutgoingTriples()) {
			if (edge.isVisited())
				continue;
			//
			edge.visit();
			edge.getPredicate().setVarEnumeration(iter);
			edge.getObject().setVarEnumeration(iter);
			//
			serializedForm.add(edge.getPredicate().getLabel());
			serializedForm.add(edge.getObject().getLabel());
			if (!edge.getObject().isVisited())
				visit(edge.getObject(), serializedForm, iter);
		}
		boolean noIncomingEdge = true;
		for (InterfaceForTriple edge : node.getIncomingTriples()) {
			if (edge.isVisited())
				continue;
			//
			if (noIncomingEdge) {
				serializedForm.add(Dictionary.invEdgeMark);
				noIncomingEdge = false;
			}
			edge.visit();
			edge.getPredicate().setVarEnumeration(iter);
			edge.getSubject().setVarEnumeration(iter);
			//
			serializedForm.add(edge.getPredicate().getLabel());
			serializedForm.add(edge.getSubject().getLabel());
			//
			if (!edge.getSubject().isVisited())
				visit(edge.getSubject(), serializedForm, iter);

		}
		serializedForm.add(Dictionary.endEdgeMark);
	}

}
