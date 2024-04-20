package gr.uoa.di.entities.graph.regular.helpers;

import java.util.Comparator;

import gr.uoa.di.entities.graph.regular.abstractions.Term;
import gr.uoa.di.entities.trie.graphAbstraction.InterfaceForTriple;

public class ComparatorsForGraph {
	
	private static Comparator<Term> termComparator = new Comparator<Term>() {
		@Override
		public int compare(Term t1, Term t2) {			
			return Integer.compare(t1.getLabel(), t2.getLabel());
		}

	};
	
	public static Comparator<InterfaceForTriple> _tripleComparator = new Comparator<InterfaceForTriple>() {
		@Override
		public int compare(InterfaceForTriple e1, InterfaceForTriple e2) {
			int comp = termComparator.compare(e1.getPredicate(), e2.getPredicate());
			if (comp != 0)
				return comp;
			if (e1.getSubject() == e2.getSubject())
				return termComparator.compare(e1.getObject(), e2.getObject());
			return termComparator.compare(e1.getSubject(), e2.getSubject());
		}
	};

	static Comparator<InterfaceForTriple> tripleComparator = new Comparator<InterfaceForTriple>() {
		@Override
		public int compare(InterfaceForTriple e1, InterfaceForTriple e2) {
			int comp = termComparator.compare(e1.getPredicate(), e2.getPredicate());
			if (comp != 0)
				return comp;
			if (e1.getSubject() == e2.getSubject())
				return termComparator.compare(e1.getObject(), e2.getObject());
			return termComparator.compare(e1.getSubject(), e2.getSubject());
		}
	};

	static Comparator<InterfaceForTriple> tripleComparatorWithNulls = new Comparator<InterfaceForTriple>() {
		@Override
		public int compare(InterfaceForTriple e1, InterfaceForTriple e2) {
			return termComparator.compare(e1.getPredicate(), e2.getPredicate());
		}
	};
	
}
