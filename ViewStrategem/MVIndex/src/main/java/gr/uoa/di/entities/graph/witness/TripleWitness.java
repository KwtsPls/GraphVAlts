package gr.uoa.di.entities.graph.witness;

import java.util.function.Function;

import gr.uoa.di.entities.graph.Printable;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForNode;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForTriple;
import gr.uoa.di.entities.graph.regular.abstractions.Term;

public class TripleWitness<N extends AbstractionForNode<N, T>, T extends AbstractionForTriple<N, T>>
		implements Printable {
	public NodeWitness<N, T> subject;
	public int predicateLabel;
	private Term varOrConstantObject;
	public NodeWitness<N, T> object;
	public T edgeSimple = null;

	TripleWitness(NodeWitness<N, T> subject, NodeWitness<N, T> object, T edgeSimple) {
		if (!(subject.isND || object.isND)) {
			this.edgeSimple = edgeSimple;
		}
		this.subject = subject;
		this.object = object;
		predicateLabel = edgeSimple.getPredicate().getLabel();
		varOrConstantObject = edgeSimple.getPredicate();
	}

	@Override
	public String toString() {
		return toCompactString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		result = prime * result + predicateLabel;
		result = prime * result + ((varOrConstantObject == null) ? 0 : varOrConstantObject.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		TripleWitness<N, T> other = (TripleWitness<N, T>) obj;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		if (predicateLabel != other.predicateLabel)
			return false;
		if (varOrConstantObject == null) {
			if (other.varOrConstantObject != null)
				return false;
		} else if (!varOrConstantObject.equals(other.varOrConstantObject))
			return false;
		return true;
	}

	public boolean isND() {
		return subject.isND || object.isND;
	}

	@Override
	public String print(Function<Printable, String> function) {
		return new StringBuilder("(").append(function.apply(subject)).append(',')
				.append(function.apply(varOrConstantObject)).append(',').append(function.apply(object)).append(')')
				.toString();
	}

}
