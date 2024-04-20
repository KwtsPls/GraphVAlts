package gr.uoa.di.entities.trie;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.collect.Iterators;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.regular.abstractions.Term;

public class TrieVertex<I, M extends TrieMetadata<I>> {

	private static int meter = 0;
	public int id;
	public M metadata = null;
	public Map<Integer, Branch<I, M>> constantBranches = null;
	public Map<Integer, Branch<I, M>> varBranches = null;
	public boolean isInserted = false;

	public TrieVertex() {
		id = (meter++);
	}

	@Override
	public String toString() {
		return toIdString();
	}

	private String toString(Function<Integer, String> intTranslator) {
		StringBuilder builder = new StringBuilder();
		toString(builder, constantBranches, intTranslator);
		toString(builder, varBranches, intTranslator);
		return builder.toString();
	}

	String toIdString() {
		return toString(x -> Integer.toString(x));
	}

	String toCompactString(Dictionary dict) {
		return toString(x -> Term.getConstantTerm(x, dict.getContantOfId(x)).toCompactString());
	}

	String toFullString(Dictionary dict) {
		return toString(x -> Term.getConstantTerm(x, dict.getContantOfId(x)).toFullString());
	}

	private void toString(StringBuilder builder, Map<Integer, Branch<I, M>> branches,
			Function<Integer, String> intTranslator) {
		if (branches == null)
			return;
		branches.forEach((x, y) -> {
			builder.append("(").append(id).append(") -- ");
			//
			TrieVertex<I, M> targetVertex = y.vertex;
			String sequence = IntStream.of(y.labels).mapToObj(z -> {
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
			builder.append(")\n");
			builder.append(targetVertex.toString(intTranslator));
		});
	}

	void put(int key, Branch<I, M> branch) {
		if (Dictionary.isConstant(key)) {
			if (constantBranches == null)
				constantBranches = new HashMap<Integer, Branch<I, M>>();
			constantBranches.put(key, branch);
		} else {
			if (varBranches == null)
				varBranches = new HashMap<Integer, Branch<I, M>>();
			varBranches.put(key, branch);
		}
	}

	public Branch<I, M> get(int key) {
		if (Dictionary.isConstant(key)) {
			return constantBranches == null ? null : constantBranches.get(key);
		} else {
			return varBranches == null ? null : varBranches.get(key);
		}

	}

	public Iterator<Branch<I, M>> getBranchIterator() {
		if (constantBranches == null && varBranches == null) {
			return Collections.emptyIterator();
		} else if (constantBranches == null) {
			return varBranches.values().iterator();
		} else if (varBranches == null) {
			return constantBranches.values().iterator();
		}
		return Iterators.concat(constantBranches.values().iterator(), varBranches.values().iterator());
	}

	void forEachMetadataEntry(Consumer<M> metadataConsumer) {
		if(this.metadata!=null) {
			metadataConsumer.accept(metadata);
		}
		Iterator<Branch<I, M>> branchIter = getBranchIterator();
		while(branchIter.hasNext()) {
			Branch<I, M> branch = branchIter.next();
			branch.vertex.forEachMetadataEntry(metadataConsumer);
		}
	}

	public M getMetadata() {
		return metadata;		
	}

}
