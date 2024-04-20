package gr.uoa.di.entities.trie;

import java.util.Iterator;
import java.util.List;

class FindPath {


	static<I, M extends TrieMetadata<I>> List<Branch<I, M>> find(TrieVertex<I, M> vertex, int vertexId, List<Branch<I,M>>  list) {
		if(vertexId==vertex.id) {
			return list;
		}else {
			Iterator<Branch<I, M>> iter = vertex.getBranchIterator();
			while(iter.hasNext()) {
				Branch<I, M> branch = iter.next();
				list.add(branch);
				List<Branch<I, M>> output = find(branch.vertex,vertexId,list);
				if(output == null) {
					list.remove(list.size()-1);
				}else {
					return list; 
				}
			}			
			return null;
		}
		
		
		
	}

}
