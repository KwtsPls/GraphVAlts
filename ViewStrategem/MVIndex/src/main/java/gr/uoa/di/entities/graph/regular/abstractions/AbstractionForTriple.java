package gr.uoa.di.entities.graph.regular.abstractions;

import java.util.function.Function;

import gr.uoa.di.entities.graph.Printable;
import gr.uoa.di.entities.trie.graphAbstraction.InterfaceForTriple;

public abstract class AbstractionForTriple<N extends AbstractionForNode<N, T>, T extends AbstractionForTriple<N, T>>
		implements InterfaceForTriple, Triple<N, T> {
	private N subject;
	private Term predicate;
	private N object;
	private boolean isExamined = false;
	

	public AbstractionForTriple(AbstractionForNode<N, T> subject, Term predicate, AbstractionForNode<N, T> object) {
		this.subject = subject == null ? null : subject.getThis();
		this.predicate = predicate;
		this.object = object == null ? null : object.getThis();
	}



	@Override
	public N getSubject() {
		return subject;
	}

	@Override
	public abstract T getThis();

	@Override
	public Term getPredicate() {
		return predicate;
	}

	@Override
	public N getObject() {
		return object;
	}

	@Override
	public String toString() {
		return toCompactString();
	}

	@Override
	public boolean isVisited() {
		return isExamined;
	}

	@Override
	public void visit() {
		isExamined = true;
	}

	@Override
	public int getPredicateLabel() {
		return predicate.getLabel();
	}

	

	@Override
	public void resetForSerialization() {
		isExamined = false;
	}
	
	@Override
	public String print(Function<Printable,String> function) {
		return new StringBuilder("(").append(function.apply(subject)).append(',').append(
				function.apply(predicate))
				.append(',').append(function.apply(object)).append(')').toString();
	}

}
