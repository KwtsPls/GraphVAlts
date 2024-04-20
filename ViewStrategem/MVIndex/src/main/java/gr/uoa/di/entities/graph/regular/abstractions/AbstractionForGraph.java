package gr.uoa.di.entities.graph.regular.abstractions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.Iterators;

import gr.uoa.di.entities.graph.Printable;
import gr.uoa.di.entities.trie.graphAbstraction.InterfaceForNode;

public abstract class AbstractionForGraph<N extends AbstractionForNode<N,T>,T extends AbstractionForTriple<N,T>, G extends AbstractionForGraph<N,T,G>> implements Printable,Iterable<T>, Graph<N, T, G> {

	private List<N> varNodes;
	private List<N> constantNodes;
	
	public AbstractionForGraph() {
		varNodes = new ArrayList<>();
		constantNodes = new ArrayList<>();
	}
	
	@Override
	abstract public G getThis();
	
	@Override
	public String toString() {
		return toCompactString();
	}

	
	

	private void addVarNode(AbstractionForNode<N,T> node) {
		varNodes.add(node.getThis());
	}

	private void addConstantNode(AbstractionForNode<N,T> node) {
		constantNodes.add(node.getThis());
	}

	
	@Override
	public void forEachNode(Consumer<N> action) {
		varNodes.forEach(action);
		constantNodes.forEach(action);
	}

	
	@Override
	public void forEachVarNode(Consumer<N> action) {
		varNodes.forEach(action);
	}

	
	@Override
	public void forEachTriple(Consumer<T> action) {
		forEachNode(node -> {
			for (T triple : node.getOutgoingTriples()) {
				action.accept(triple);
			}
		});
	}

	
	@Override
	public void sortGraph() {
		int[] i = new int[] { 0 };
		forEachNode(node -> {
			node.sortEdges();
			node.setPositionInGraph(i[0]++);
		});
	}

	@Override
	public List<N>  getVariables() {
		return varNodes;
	}
	
	@Override
	public int getNodeCount() {
		return varNodes.size() + constantNodes.size();
	}

	@Override
	public N getAnchorNode() {
		N anchor = null;
		if (constantNodes.size() > 0)
			anchor = constantNodes.get(0);
		else
			anchor = varNodes.get(0);
		for (N node : constantNodes) {
			if (node.hasOnlyConstantPredicates()) {
				anchor = node;
				break;
			}
		}
		return anchor;
	}

	
	@Override
	public int getVarNodeCount() {
		return varNodes.size();
	}

	
	@Override
	public int getEdgeCount() {
		int[] counter = { 0 };
		this.forEachTriple(edge -> counter[0]++);
		return counter[0];
	}

	@Override
	public void resetForSerialization() {
		forEachNode(node -> {
			node.resetForSerialization();
		});
		forEachTriple(edge -> {
			edge.resetForSerialization();
		});
	}

	
	public void addNode(N node) {
		if (node.isConstant()) {
			addConstantNode(node);
		} else {
			addVarNode(node);
		}
	}
	
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			Iterator<N> allNodes = Iterators.concat(varNodes.iterator(),constantNodes.iterator());
			Iterator<T> triplesPerNodeIter = Collections.emptyIterator();
			@Override
			public boolean hasNext() {
				while(!triplesPerNodeIter.hasNext()) {
					if(allNodes.hasNext()) {
						triplesPerNodeIter = allNodes.next().getOutgoingTriples().iterator();
					}else {
						return false;
					}
				}
				return true;
			}

			@Override
			public T next() {
				return triplesPerNodeIter.next();
			}
		};
	}
	
	@Override
	public String print(Function<Printable, String> function) {
		StringBuilder builder = new StringBuilder();
		forEachTriple(edge->builder.append(function.apply(edge)));
		return builder.toString();
	}

	@Override
	public void reArrangeVarNodes() {
		varNodes.sort(varComparator);
		sortGraph();
	}
	
	private static Comparator<InterfaceForNode> varComparator = new Comparator<InterfaceForNode>(){
		@Override
		public int compare(InterfaceForNode o1, InterfaceForNode o2) {
			return Integer.compare(o1.getLabel(), o2.getLabel());
		}
		
	};
}
