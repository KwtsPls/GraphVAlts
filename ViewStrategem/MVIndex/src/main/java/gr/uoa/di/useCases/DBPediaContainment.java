package gr.uoa.di.useCases;

import java.util.List;


import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.regular.implementations.BasicGraph;
import gr.uoa.di.entities.graph.regular.implementations.BasicGraphFactory;
import gr.uoa.di.entities.graph.regular.implementations.BasicNode;
import gr.uoa.di.entities.graph.regular.implementations.BasicTriple;
import gr.uoa.di.entities.graph.serialization.LinearizedQuery;
import gr.uoa.di.entities.graph.serialization.LinearRewriter;
import gr.uoa.di.entities.trie.TrieIndex;
import gr.uoa.di.entities.trie.TrieMetadataDefault;
import gr.uoa.di.entities.trie.TrieVertex;
import gr.uoa.di.entities.trie.containment.mapping.ContainmentMapping;
import gr.uoa.di.interfaceAdapters.iterators.dbPedia.DBPediaGraphQueryIterator;
import gr.uoa.di.interfaceAdapters.workloads.JenaGraphQueryIterator;

class DBPediaContainment { // NO_UCD (unused code)

	public static void main(String[] args){

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

		System.out.println(trie);
		System.out.println("******************--------------******************");

//		long totalTime = 0;
//		int counter=0;
		try (JenaGraphQueryIterator<BasicGraph> iter = DBPediaGraphQueryIterator.create(dict,new BasicGraphFactory())) {

//			int i = 0;
			
			while (iter.hasNext()) {
				BasicGraph graph = iter.next();
				if (graph == null)
					continue;
				if(counter++==10000000)
					break;
				counter++;
				List<ContainmentMapping<BasicNode,BasicTriple,TrieMetadataDefault<String>>> output=
						trie.contains(graph);		
				for(ContainmentMapping<BasicNode,BasicTriple,TrieMetadataDefault<String>> mapping:output) {
					System.out.println(mapping.toCompactString());
				};
				if (output == null || output.isEmpty()) {
					System.out.println(counter);
					System.out.println("*************************");
					
					LinearizedQuery ser = LinearRewriter.linearize(graph);
					System.out.println(ser);
					System.out.println(graph.toIdString());
					
					TrieVertex<String,TrieMetadataDefault<String>> lala=trie.insertQuery(graph, "!!!");
					System.out.println(lala.id);
					System.out.println("*************************");
					// *****************************************************

					output = trie.contains(graph);
				} else {

				}				
			}
//			System.out.println(totalTime/counter);
//			System.out.println(TrieContainmentForWitness.totalTime/TrieContainmentForWitness.counter);	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
