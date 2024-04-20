package gr.uoa.di.entities.graph.regular.abstractions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import gr.uoa.di.entities.graph.Printable;
import gr.uoa.di.entities.trie.graphAbstraction.InterfaceForQuery;

public abstract class AbstractionForQuery<N extends AbstractionForNode<N,?>,G extends AbstractionForGraph<N,?,G>> implements InterfaceForQuery<N, G>{
	
	private G graph;
	private List<N> variables;
	
	public AbstractionForQuery(G graph2) {
		this.graph=graph2;
		variables = new ArrayList<>(graph2.getVarNodeCount());
		graph2.forEachVarNode(var->{
			variables.add(var);
		});
	}
		
	@Override
	public G getGraph() {
		return graph;
	}
	
	@Override
	public List<N> getHeadVars(){
		return variables;
	}
	
	@Override
	public String toString() {
		return toCompactString();
	}

		
	@Override
	public String print(Function<Printable,String> function) {
		StringBuffer buffer = new StringBuffer(getHeadVars().toString());		
		buffer.append("<-");
		buffer.append(function.apply(getGraph()));
		return buffer.toString();
	}
}
