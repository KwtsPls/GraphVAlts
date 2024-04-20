package gr.uoa.di.entities.graph;

import java.util.function.Function;

import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForNode;
import gr.uoa.di.entities.graph.regular.abstractions.Term;

//
public class NodeVS extends AbstractionForNode<NodeVS, TripleVS> {
	private VarType varType = null;

	NodeVS(Term term) {
		super(term);
	}

	@Override
	public NodeVS getThis() {
		return this;
	}

	public void setVarType(VarType varType) {
		this.varType = varType;
	}

	public VarType getVarType() {
		return varType;
	}

	public boolean isVarConstant() {
		return varType == VarType.VarConstant;
	}

	public boolean isConjuncted() {
		return varType == VarType.Conjucted;
	}

	@Override
	public String print(Function<Printable, String> function) {
		return isVarConstant() ? '_' + super.print(function)
				: isConjuncted() ? '.' + super.print(function) : super.print(function);
	}

}
