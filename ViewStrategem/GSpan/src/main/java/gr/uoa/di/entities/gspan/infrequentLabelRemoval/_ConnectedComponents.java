package gr.uoa.di.entities.gspan.infrequentLabelRemoval;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.tuple.MutablePair;

class _ConnectedComponents extends Vector<MutablePair<List<Integer>,Integer>>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	_ConnectedComponents(int size) {
		this.setSize(size);
	}

	void addEdge(int position,int edgeId) {
		addOneEdge(position,edgeId);
		addOneEdge(edgeId,position);
	}
	
	private void addOneEdge(int position,int edgeId) {
		List<Integer> list;
		if(this.get(position)==null) {
			list=new LinkedList<Integer>();
			this.set(position,MutablePair.of(list,null));
			list.add(edgeId);
		}else {
			this.get(position).getLeft().add(edgeId);
		}
	}
	
	int findConComp() {
		int i=0;
		for(MutablePair<List<Integer>,Integer> node:this)
			i=check(node,i);
		return i;
	}
	
	private int check(MutablePair<List<Integer>,Integer> node,int component) {
		if(node==null) return component;
		if(node.getRight()!=null) return component;
		node.setValue(component);
		node.getLeft().forEach(x->check(get(x),component));
		return component+1;
	}
	
}
