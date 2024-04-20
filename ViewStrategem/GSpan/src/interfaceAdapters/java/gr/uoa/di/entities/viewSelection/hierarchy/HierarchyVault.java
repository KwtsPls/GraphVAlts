package gr.uoa.di.entities.viewSelection.hierarchy;

import java.io.IOException;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.GraphVS;
import gr.uoa.di.entities.graph.GraphVsDeserializer;
import gr.uoa.di.entities.graph.GraphVsSerializer;
import gr.uoa.di.interfaceAdapters.controllers.dataVault.object.ObjectVault;

public class HierarchyVault extends ObjectVault<PatternHierarchy> {
	private GraphVsDeserializer graphDeserializer;

	public static HierarchyVault create(String dataFile, Dictionary dict) throws IOException {
		return new HierarchyVault(dataFile, dict);
	}

	private HierarchyVault(String dataFile, Dictionary dict) throws IOException {
		graphDeserializer = GraphVsDeserializer.create(dict);
		initialize(dataFile, PatternHierarchy.class);
	}

	@Override
	public void addSerializersDeserializers() {
		builder.registerTypeAdapter(GraphVS.class, graphDeserializer);
		builder.registerTypeAdapter(GraphVS.class, GraphVsSerializer.create());
		_PatternHierarchySerialization serialization = new _PatternHierarchySerialization();
		builder.registerTypeAdapter(PatternHierarchy.class, serialization);
		builder.registerTypeAdapter(_PatternHierarchy.class, serialization);
	}

}
