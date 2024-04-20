package gr.uoa.di.entities.trie;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.regular.abstractions.Term;

public class Branch<I, M extends TrieMetadata<I>> {
	public int[] labels;
	public TrieVertex<I,M> vertex;
	
	static <I, V extends TrieMetadata<I>> Branch<I,V> of(int[] key,TrieVertex<I,V> value) {
		Branch<I,V> output=new Branch<I,V>();
		output.labels=key;
		output.vertex=value;
		return output;
	}

	public void setEdgeLabels(int[] newKey) {
		labels=newKey;
	}

	public void setTargetVertex(TrieVertex<I,M> newValue) {
		vertex=newValue;
	}
	
//	@Override
//	public String toString() {
//		return Arrays.toString(labels)+" --> ("+vertex.id+")";
//	}
//
	
	@Override
	public String toString() {
		return toIdString();
	}

	private String toString(Function<Integer, String> intTranslator) {
		StringBuilder builder = new StringBuilder();		
		TrieVertex<I, M> targetVertex = vertex;
		String sequence = IntStream.of(labels).mapToObj(z -> {
			if (z == Dictionary.endEdgeMark)
				return ".";
			if (z == Dictionary.invEdgeMark)
				return ",";
			if (Dictionary.isVariable(z))
				return Dictionary.getVarStringOfId(z);
			return intTranslator.apply(z);
		}).collect(Collectors.joining(" "));
		builder.append(sequence).append("-->").append("(").append(targetVertex.id);
		if (targetVertex.isInserted) {
			builder.append("  ⓘ");
		}
		builder.append(") ");
		return builder.toString();
	}

	private String toIdString() {// NO_UCD (unused code)
		return toString(x -> Integer.toString(x));
	}

	public String toCompactString(Dictionary dict) { // NO_UCD (unused code)
		return toString(x -> Term.getConstantTerm(x, dict.getContantOfId(x)).toCompactString());
	}

	public String toFullString(Dictionary dict) {// NO_UCD (unused code)
		return toString(x -> Term.getConstantTerm(x, dict.getContantOfId(x)).toFullString());
	}

	


}
