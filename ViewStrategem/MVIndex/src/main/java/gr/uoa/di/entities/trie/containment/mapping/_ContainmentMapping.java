package gr.uoa.di.entities.trie.containment.mapping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.function.Function;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.Printable;
import gr.uoa.di.entities.helpStructures.tuples.IntTriple;
import gr.uoa.di.entities.trie.graphAbstraction.InterfaceForNode;
import gr.uoa.di.entities.trie.graphAbstraction.InterfaceForTriple;

class _ContainmentMapping<	N extends InterfaceForNode,
							T extends InterfaceForTriple,M> implements ContainmentMapping<N,T,M> {
	static private final int firstVarId = Dictionary.firstVarId;
	private M metadata;
	private _PseudoList<N> mappedNodes;
	private HashMap<IntTriple, T> edgeTripleToEdge;
	private Boolean isIsomorphic = null;
	private int id;//TODO REMOVE ID it may not be nedded
	static private int meter = 0;

	protected _ContainmentMapping(int answerSize, int examinedEdgesSize) {
		mappedNodes = new _PseudoList<N>(answerSize);
		edgeTripleToEdge = new HashMap<>(examinedEdgesSize);
		id = meter++;
	}

	private _ContainmentMapping() {

	}

	@SuppressWarnings("unchecked")
	@Override
	public _ContainmentMapping<N,T,M> clone() {
		_ContainmentMapping<N,T,M> output = new _ContainmentMapping<N,T,M>();
		output.mappedNodes = mappedNodes.clone();
		output.edgeTripleToEdge = (HashMap<IntTriple, T>) edgeTripleToEdge.clone();
		output.id = meter++;
		return output;
	}

	@Override
	public void mapVarId2Node(int nodeId, N x) {
		final int position = nodeId - firstVarId;
		mappedNodes.setObject(position,x);
	}

	@Override
	public N getMappedNode(int nodeId) {
		final int position = nodeId - firstVarId;
		return mappedNodes.get(position);
	}

	@Override
	public N getMappedNode(N node) {
		return getMappedNode(node.getLabel());
	}

	@Override
	public void addEdge(IntTriple edgeTriple, T simpleEdge) {
		edgeTripleToEdge.put(edgeTriple, simpleEdge);
	}

	@Override
	public boolean isIsomorphic() {
		if (isIsomorphic == null) {
			HashSet<N> set = new HashSet<N>(mappedNodes.length);			
			for(N node: mappedNodes) {
				if(node.isConstant()) {
					isIsomorphic = false;
					return false;
				}
				set.add(node);
			}			
			if (set.size() == mappedNodes.length) {
				isIsomorphic = true;
			} else {
				isIsomorphic = false;
			}
		}
		return isIsomorphic;
	}

	@Override
	public String toString() {
		return toCompactString();
	}

	@Override 
	public M getMetadata() {
		return metadata;
	}

	@Override 
	public void setMetadata(M metadata) {
		this.metadata = metadata;
	}

	@Override
	public T getContainmentMapping(IntTriple triple) {
		return edgeTripleToEdge.get(triple);
	}

	
	@Override
	public void forEachTripleMapping(BiConsumer<? super IntTriple, ? super T> action) {
		edgeTripleToEdge.forEach(action);
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String print(Function<Printable, String> function) {
		StringBuilder builder = new StringBuilder();
		//
		builder.append("Node Mapping: (");
		for(int i=0;i<mappedNodes.length;i++) {
			builder.append("?x").append(i).append(", ");
		}
		builder.setCharAt(builder.length()-2, ')');
		builder.append("-> (");
		mappedNodes.forEach(node->{
			builder.append(function.apply(node));
			builder.append(", ");
		});
		builder.setCharAt(builder.length()-2, ')');
		if(metadata!=null)
			builder.append(" ").append(metadata.toString());
		return builder.toString();
	}

}
