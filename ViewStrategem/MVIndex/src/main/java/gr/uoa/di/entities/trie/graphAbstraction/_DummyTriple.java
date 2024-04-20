package gr.uoa.di.entities.trie.graphAbstraction;

import java.util.function.Function;

import gr.uoa.di.entities.graph.Printable;
import gr.uoa.di.entities.graph.regular.abstractions.Term;

class _DummyTriple implements InterfaceForTriple{

	private InterfaceForNode subject;
	private Term predicate;
	private InterfaceForNode object;
	
	public _DummyTriple( InterfaceForNode subject, Term predicate,  InterfaceForNode object) {
		this.subject = subject;;
		this.predicate =predicate;
		this.object = object;
	}

	
	@Override
	public InterfaceForNode getSubject() {
		return subject;
	}	

	@Override
	public Term getPredicate() {
		return predicate;
	}

	
	@Override
	public InterfaceForNode getObject() {
		return object;
	}
	
	@Override
	public String toString() {
		return toCompactString();
	}

	@Override
	public boolean isVisited() {
		return false;
	}

	@Override
	public void visit() {
	}
	
	@Override
	public int getPredicateLabel() {
		return predicate.getLabel();
	}
	


	
	@Override
	public void resetForSerialization() {
	}
	

	@Override
	public String print(Function<Printable, String> function) {
		return new StringBuilder("(").append(function.apply(subject)).append(',').append(function.apply(predicate))
				.append(',').append(function.apply(object)).append(')').toString();
	}


}
