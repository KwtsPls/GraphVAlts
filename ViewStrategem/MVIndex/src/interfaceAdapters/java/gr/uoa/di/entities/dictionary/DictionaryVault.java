package gr.uoa.di.entities.dictionary;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Literal;

import com.google.common.collect.BiMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonDeserializer;

import gr.uoa.di.interfaceAdapters.controllers.dataVault.object.ObjectVault;

public class DictionaryVault extends ObjectVault<Dictionary> {

	public DictionaryVault(String dataFile) throws IOException {
		initialize(dataFile, Dictionary.class);
	}

	@Override
	public void addSerializersDeserializers() {
		Type type = new TypeToken<BiMap<Object, Integer>>() {
		}.getType();
		//
		builder.registerTypeAdapter(type, new DictionarySerializer());
		builder.registerTypeAdapter(type, new DictionaryDeserializer());
		//
		builder.registerTypeAdapter(Dictionary.class, new JsonDeserializer<Dictionary>() {

			@Override
			public _InMemoryDictionary deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
					throws JsonParseException {
				return context.deserialize(json, _InMemoryDictionary.class);
			}
		});
	}

	private static class DictionarySerializer implements JsonSerializer<BiMap<Object, Integer>> {
		@Override
		public JsonElement serialize(BiMap<Object, Integer> src, Type typeOfSrc, JsonSerializationContext context) {
			
			JsonArray array = new JsonArray();
			src.forEach((x, y) -> {
				try {
					String classString = x.getClass().getName();

					switch (classString) {
					case "gr.uoa.di.entities.dictionary._Mark": {
						break;
					}
					case "gr.uoa.di.entities.dictionary._DictVar": {
						break;
					}
					case "org.apache.jena.graph.Node_Literal": {
						Node_Literal nodeLiteral = ((Node_Literal) x);
						String uri = nodeLiteral.getLiteralDatatype().getURI();
						String type = uri.split("#")[1];
						String  lexForm= nodeLiteral.getLiteralLexicalForm();
						if(type .equals( "langString") || (type.equals("string")))
							lexForm = URLEncoder.encode(lexForm,"UTF-8");						
						JsonElement element = getJsonForLiteral(context, type,
								lexForm, nodeLiteral.getLiteralLanguage(), y);
						array.add(element);
						break;
					}
					case "org.apache.jena.graph.Node_URI": {
						String  lexForm= x.toString();
						lexForm = URLEncoder.encode(lexForm,"UTF-8");
						JsonElement element = getJsonForURI(context, "Node_URI", lexForm, y);
						array.add(element);
						break;
					}
					default: {
						System.err.println("Objects of type: " + classString + " are ignored");
						break;
					}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			return array;
		}

		private static JsonElement getJsonForURI(JsonSerializationContext context, String type, String objectString,
				int id) throws IOException {
			JsonArray array = new JsonArray();
			array.add(type);
			array.add(id);
			array.add(objectString);
			return array;
		}

		private static JsonElement getJsonForLiteral(JsonSerializationContext context, String type, String objectString,
				String language, int id) throws IOException {
			JsonArray array = new JsonArray();
			array.add(type);
			array.add(id);
			array.add(objectString);
			array.add(language);
			return array;
		}
	}

	private static class DictionaryDeserializer implements JsonDeserializer<BiMap<Object, Integer>> {

		@Override
		public BiMap<Object, Integer> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			_InMemoryDictionary tmp = new _InMemoryDictionary();
			BiMap<Object, Integer> biMap = tmp.entries;
			json.getAsJsonArray().forEach(entry -> {
				JsonArray entryTuple = entry.getAsJsonArray();
				String type = entryTuple.get(0).getAsString();
				int value = entryTuple.get(1).getAsInt();
				Node key = null;
				RDFDatatype dt = null;
				String lexForm =null;
				switch (type) {
				case "Node_URI":
					try {
						lexForm = URLDecoder.decode(entryTuple.get(2).getAsString(),  "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					key = NodeFactory.createURI(lexForm);
					biMap.put(key, value);
					break;
				case "langString":
					try {
						lexForm = URLDecoder.decode(entryTuple.get(2).getAsString(),  "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					dt = NodeFactory.getType("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString");
					key = NodeFactory.createLiteral(lexForm, entryTuple.get(3).getAsString(), dt);
					biMap.put(key, value);
					break;
				case "string":
					try {
						lexForm = URLDecoder.decode(entryTuple.get(2).getAsString(),  "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					dt = NodeFactory.getType("http://www.w3.org/2001/XMLSchema#string");
					key = NodeFactory.createLiteralByValue(lexForm, dt);
					biMap.put(key, value);
					break;
				default:
					System.err.println(type + " type is not supported");
					break;
				}
			});
			return biMap;
		}

	}

}
