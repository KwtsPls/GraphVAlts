package gr.uoa.di.entities.graph;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.regular.factory.GraphConstructor;

public class GraphVsDeserializer implements JsonDeserializer<GraphVS> {
	private FactoryVS factory = new FactoryVS();
	private Dictionary dict;

	public static GraphVsDeserializer create(Dictionary dict) {
		return new GraphVsDeserializer(dict);
	}

	private GraphVsDeserializer(Dictionary dict) {
		this.dict = dict;
	}

	@Override
	public GraphVS deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonArray arrayGraph = json.getAsJsonObject().get("graph").getAsJsonArray();
		JsonArray constantGraph = json.getAsJsonObject().get("cons").getAsJsonArray();
		JsonArray conjuctedGraph = json.getAsJsonObject().get("conj").getAsJsonArray();

		Set<Integer> constantInts = new Gson().fromJson(constantGraph, new TypeToken<HashSet<Integer>>() {
		}.getType());
		Set<Integer> conjuctedInts = new Gson().fromJson(conjuctedGraph, new TypeToken<HashSet<Integer>>() {
		}.getType());

		GraphConstructor<NodeVS, TripleVS, GraphVS> constructor = factory.getGraphConstructor(dict);
		arrayGraph.forEach(tripleOfIds -> {
			JsonArray array = tripleOfIds.getAsJsonArray();
			int subjectId = array.get(0).getAsInt();
			int predicateId = array.get(1).getAsInt();
			int objectId = array.get(2).getAsInt();
			TripleVS triple = constructor.addTripleFromInt(subjectId, predicateId, objectId);
			// Set Metadata
			setMeta(subjectId, triple.getSubject(), constantInts, conjuctedInts);
			setMeta(objectId, triple.getObject(), constantInts, conjuctedInts);
		});
		return constructor.getGraphQuery();
	}

	private void setMeta(int id, NodeVS node, Set<Integer> constantInts, Set<Integer> conjuctedInts) {
		if (constantInts.contains(id)) {
			node.setVarType(VarType.VarConstant);
		} else if (conjuctedInts.contains(id)) {
			node.setVarType(VarType.Conjucted);
		}

	}

}
