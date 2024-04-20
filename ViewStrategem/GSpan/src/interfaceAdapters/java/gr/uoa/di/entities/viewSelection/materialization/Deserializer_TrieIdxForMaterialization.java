package gr.uoa.di.entities.viewSelection.materialization;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import gr.uoa.di.entities.trie.TrieVertex;
import gr.uoa.di.entities.viewSelection.queryRewriting.ViewForRewriting;

public class Deserializer_TrieIdxForMaterialization implements JsonDeserializer<TrieIndexForMaterialization> {

	@Override
	public TrieIndexForMaterialization deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		TrieIndexForMaterialization trieIndex = new TrieIndexForMaterialization();
		JsonElement vertexJson = json.getAsJsonObject().get("vertex");
		Type type = new TypeToken<TrieVertex<ViewForRewriting, MetadataForMaterialization>>() {
		}.getType();
		trieIndex.vertex = context.deserialize(vertexJson, type);
		return trieIndex;
	}
}
