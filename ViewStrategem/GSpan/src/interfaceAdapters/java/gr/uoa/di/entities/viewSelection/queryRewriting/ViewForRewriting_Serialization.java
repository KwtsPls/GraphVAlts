package gr.uoa.di.entities.viewSelection.queryRewriting;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ViewForRewriting_Serialization
		implements JsonDeserializer<ViewForRewriting>, JsonSerializer<ViewForRewriting> {

	@Override
	public ViewForRewriting deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		return context.deserialize(json, _ViewForRewriting.class);
	}

	@Override
	public JsonElement serialize(ViewForRewriting src, Type typeOfSrc, JsonSerializationContext context) {
		return context.serialize(ViewForRewriting.create(src), _ViewForRewriting.class);
	}

}
