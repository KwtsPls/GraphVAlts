package gr.uoa.di.entities.graph;

import java.lang.reflect.Type;
import java.util.Iterator;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import gr.uoa.di.entities.dictionary.Dictionary;

public class GraphVsSerializer implements JsonSerializer<GraphVS> {

	public static GraphVsSerializer create() {
		return new GraphVsSerializer();
	}

	protected GraphVsSerializer() {

	}

	@Override
	public JsonElement serialize(GraphVS graph, Type typeOfSrc, JsonSerializationContext context) {

		JsonObject output = new JsonObject();
		// Fields
		JsonArray arrayGraph = new JsonArray();
		JsonArray constantGraph = new JsonArray();
		JsonArray conjuctedGraph = new JsonArray();
		//
		BiMap<NodeVS, Integer> varNodesToIds = HashBiMap.create();
		Iterator<Integer> iter = Dictionary.getVarIdIterator();
		// Adding Head
		graph.forEach(triple -> {
			JsonArray tripleArray = new JsonArray();
			int subjId = getNodeId(varNodesToIds, iter, triple.getSubject());
			int predId = triple.getPredicateLabel();
			int objId = getNodeId(varNodesToIds, iter, triple.getObject());
			tripleArray.add(subjId);
			tripleArray.add(predId);
			tripleArray.add(objId);
			arrayGraph.add(tripleArray);
			// Add Meta
			addMeta(subjId, triple.getSubject(), constantGraph, conjuctedGraph);
			addMeta(objId, triple.getObject(), constantGraph, conjuctedGraph);
		});
		output.add("graph", arrayGraph);
		output.add("cons", constantGraph);
		output.add("conj", conjuctedGraph);
		return output;
	}

	private void addMeta(int id, NodeVS node, JsonArray constantGraph, JsonArray conjuctedGraph) {
		if (node.isVarConstant()) {
			constantGraph.add(id);
		} else if (node.isConjuncted()) {
			conjuctedGraph.add(id);
		}

	}

	private int getNodeId(BiMap<NodeVS, Integer> varNodesToIds, Iterator<Integer> iter, NodeVS node) {
		if (node.isConstant())
			return node.getLabel();
		else {
			int nodeId = node.isLabeledVariable() ? varNodesToIds.computeIfAbsent(node, x -> x.getLabel())
					: varNodesToIds.computeIfAbsent(node, x -> iter.next());
			return nodeId;
		}
	}

}
