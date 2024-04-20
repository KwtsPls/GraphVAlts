package gr.uoa.di.interfaceAdapters.controllers.dataVault.object.trie;

import java.io.IOException;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.GraphVsSerializer;
import gr.uoa.di.entities.graph.GraphVS;
import gr.uoa.di.entities.graph.GraphVsDeserializer;
import gr.uoa.di.entities.viewSelection.materialization.Deserializer_TrieIdxForMaterialization;
import gr.uoa.di.entities.viewSelection.materialization.TrieIndexForMaterialization;
import gr.uoa.di.entities.viewSelection.queryRewriting.ViewForRewriting;
import gr.uoa.di.entities.viewSelection.queryRewriting.ViewForRewriting_Serialization;
import gr.uoa.di.interfaceAdapters.controllers.dataVault.object.ObjectVault;

//TrieIndex<Map<MaterializedView, Set<IntTriple>>>
public class TrieVault extends ObjectVault<TrieIndexForMaterialization> {

	private GraphVsDeserializer graphDeserializer;

	public static TrieVault create(String dataFile, Dictionary dict) throws IOException {
		return new TrieVault(dataFile, dict);
	}

	private TrieVault(String dataFile, Dictionary dict) throws IOException {
		graphDeserializer = GraphVsDeserializer.create(dict);
		initialize(dataFile, TrieIndexForMaterialization.class);
	}

	@Override
	public void addSerializersDeserializers() {
		builder.registerTypeAdapter(GraphVS.class, graphDeserializer);
		builder.registerTypeAdapter(GraphVS.class, GraphVsSerializer.create());
		builder.registerTypeAdapter(ViewForRewriting.class, new ViewForRewriting_Serialization());
		builder.registerTypeAdapter(TrieIndexForMaterialization.class, new Deserializer_TrieIdxForMaterialization());
	}
}
