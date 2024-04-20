package gr.uoa.di.entities.graph.regular.factory;

import java.util.HashMap;
import java.util.function.Consumer;


import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForGraph;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForNode;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForTriple;
import gr.uoa.di.entities.graph.regular.abstractions.Term;

public abstract class AbstractGraphFactory<N extends AbstractionForNode<N,T>,T extends AbstractionForTriple<N,T>,G extends AbstractionForGraph<N,T,G>> implements Factory<N, T, G> {
	
	@Override
	abstract public G createGraph();
		
	@Override
	abstract public T createTriple(N subject, Term predicate, N object);
	
	@Override
	abstract public N createVarNode(Object var);

	@Override
	abstract public N createConstantNode(int objectID, Object var);
	
	@Override
	public GraphConstructor<N,T,G> getGraphConstructor(Dictionary dict2) {
		return new _GraphConstructor(dict2);
	}
	
	private class _GraphConstructor implements GraphConstructor<N,T,G>  {
		private Dictionary dict;
		private HashMap<Object, N> nodeIndex=new HashMap<Object, N> ();
		private G graph;

		private _GraphConstructor(Dictionary dict2) {
			this.dict = dict2;
			this.graph= createGraph();
		}
		
		private N getNodeFromJObject(Object term) {
			return nodeIndex.compute(term, (x,y)->{
				if(y!=null) return y;
				if(term.toString().startsWith("?")) {
					N node=createVarNode(term);
					graph.addNode(node);
					return node;
				}else {
					N node=createConstantNode(dict.getIdOfConstant(term), term);
					graph.addNode(node);
					return node;
				}
			});
		}
		
		private N getNodeFromInt(int nodeId) {
			return getNodeFromInt(nodeId,null);
		}
		
		private N getNodeFromInt(int nodeId,Consumer<N> consumer) {
			Object term = dict.getContantOfId(nodeId);
			return nodeIndex.compute(term, (x,y)->{
				if(y!=null) return y;
				N node;
				if(term.toString().startsWith("?")) {
					node=createVarNode(term);
					node.setLabel(nodeId);					
				}else {
					node=createConstantNode(nodeId, term);
				}
				if(consumer!=null)
					consumer.accept(node);
				graph.addNode(node);
				return node;
			});
		}
		
		
		
		private Term getTermFromJObject(Object term) {
			return nodeIndex.compute(term, (x,y)->{
				if(y!=null) return y;
				if(term.toString().startsWith("?")) {
					return createVarNode(term);
				}else {
					return createConstantNode(dict.getIdOfConstant(term), term);
				}
			}).getTerm();
		}
		
		private Term getTermFromInt(int termId) {
			Object term = dict.getContantOfId(termId);
			return nodeIndex.compute(term, (x,y)->{
				if(y!=null) return y;
				if(term.toString().startsWith("?")) {
					return createVarNode(term);
				}else {
					return createConstantNode(termId, term);
				}
			}).getTerm();
		}

		@Override
		public void addTripleFromJObjects(Object subject, Object predicate, Object object) {
			addTriple(getNodeFromJObject(subject),getTermFromJObject(predicate),getNodeFromJObject(object));
		}
		
		@Override
		public T addTripleFromInt(int subjectId, int predicateId, int objectId) {			
			return addTriple(getNodeFromInt(subjectId),getTermFromInt(predicateId),getNodeFromInt(objectId));
		}
		
		private T addTriple(N subject, Term predicate, N object) {
			T triple=createTriple(subject,  predicate, object);
			subject.addTriple(triple);
			object.addTriple(triple);
			return triple;
		}

		@Override
		public G getGraphQuery() {
			graph.sortGraph();
			return graph;
		}
	}

}
