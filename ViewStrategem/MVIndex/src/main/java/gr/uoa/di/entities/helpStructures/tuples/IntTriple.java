package gr.uoa.di.entities.helpStructures.tuples;

import java.util.Arrays;
import java.util.Iterator;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.trie.graphAbstraction.InterfaceForTriple;;

public class IntTriple implements Iterable<Integer> {

	public final int s;
	public final int p;
	public final int o;

	public static IntTriple of(int subjectId, int predicateId, int objectId) {
		return new IntTriple(subjectId, predicateId, objectId);
	}

	public static <I extends InterfaceForTriple> IntTriple of(I triple) {
		return new IntTriple(triple.getSubject().getLabel(), triple.getPredicate().getLabel(),
				triple.getObject().getLabel());
	}

	private IntTriple(int subjectId, int predicateId, int objectId) {
		super();
		this.s = subjectId;
		this.p = predicateId;
		this.o = objectId;
	}

	@Override
	public String toString() {

		return new StringBuilder("[").append(getAsVar(s)).append(",").append(getAsVar(p)).append(",")
				.append(getAsVar(o)).append("]").toString();
	}

	private String getAsVar(int id) {
		return Dictionary.isConstant(id) ? Integer.toString(id) : Dictionary.getVarStringOfId(id);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + o;
		result = prime * result + p;
		result = prime * result + s;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof IntTriple))
			return false;
		IntTriple other = (IntTriple) obj;
		if (o != other.o)
			return false;
		if (p != other.p)
			return false;
		if (s != other.s)
			return false;
		return true;
	}

	public int getVarCount() {
		return (Dictionary.isVariable(s) ? 1 : 0) + (Dictionary.isVariable(p) ? 1 : 0)
				+ (Dictionary.isVariable(o) ? 1 : 0);

	}

	public int[] getVars() {
		int[] vars = new int[3];
		int counter = 0;
		if (Dictionary.isVariable(s)) {
			vars[counter++] = s;
		}
		if (Dictionary.isVariable(p)) {
			vars[counter++] = p;
		}
		if (Dictionary.isVariable(o)) {
			vars[counter++] = o;
		}
		return Arrays.copyOfRange(vars, 0, counter);
	}

	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {
			int i = 0;

			@Override
			public boolean hasNext() {
				if (i < 3)
					return true;
				return false;
			}

			@Override
			public Integer next() {
				switch (i++) {
				case 0:
					return s;
				case 1:
					return p;
				case 2:
					return o;
				default:
					return null;
				}
			}

		};
	}

}
