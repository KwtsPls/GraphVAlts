package gr.uoa.di.useCases;


import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.regular.implementations.BasicGraph;
import gr.uoa.di.entities.graph.regular.implementations.BasicGraphFactory;
import gr.uoa.di.entities.trie.TrieIndex;
import gr.uoa.di.entities.trie.TrieMetadataDefault;
import gr.uoa.di.interfaceAdapters.iterators.dbPedia.DBPediaGraphQueryIterator;
import gr.uoa.di.interfaceAdapters.workloads.JenaGraphQueryIterator;


public class DBPediaInsertion { // NO_UCD (unused code)

	public static void main(String[] args){
		// Insertion
		TrieIndex<String,TrieMetadataDefault<String>> trie = TrieIndex.create(()->new TrieMetadataDefault<String>());
		Dictionary dict = Dictionary.create();
		int counter=0;
		try (JenaGraphQueryIterator<BasicGraph> iter = DBPediaGraphQueryIterator.create(dict,new BasicGraphFactory());) {
			long time = System.currentTimeMillis();
			while (iter.hasNext()) {
				BasicGraph query = iter.next();
				if (query == null)
					continue;
				if(counter++==10000000)
					break;
				trie.insertQuery(query,"!!!!");
			}
			System.out.println(trie.toString());
			dict = iter.getDictionary();
			time = System.currentTimeMillis() - time;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
