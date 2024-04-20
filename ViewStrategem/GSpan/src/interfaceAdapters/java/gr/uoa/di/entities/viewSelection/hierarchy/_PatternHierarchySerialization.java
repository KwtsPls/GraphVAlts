package gr.uoa.di.entities.viewSelection.hierarchy;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import gr.uoa.di.entities.viewSelection.edgeRewriting.TripleMap;

class _PatternHierarchySerialization implements JsonDeserializer<PatternHierarchy>, JsonSerializer<_PatternHierarchy> {

	@Override
	public PatternHierarchy deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		_PatternHierarchy output = new _PatternHierarchy();
		IntermediateObjects imported = context.deserialize(json, IntermediateObjects.class);
		//
		HashMap<Integer, _PatternWrapper> idsToPatterns = imported.idsToPatterns;
		HashMap<Integer, HashMap<Integer, List<_TmpEdgeRewriting>>> containmentsRewritings = imported.containmentsRewritings;
		//
		containmentsRewritings.forEach((id1, mapping) -> {
			_PatternWrapper wrapper1 = idsToPatterns.get(id1);
			mapping.forEach((id2, listOfRewritings) -> {
				_PatternWrapper wrapper2 = idsToPatterns.get(id2);
				List<TripleMap> tripleMappings = _TmpEdgeRewriting.createTripleMapping(wrapper1.getPattern().getGraph(),
						wrapper2.getPattern().getGraph(), listOfRewritings);
				tripleMappings.forEach(tripleMapping -> {
					wrapper1.addContains(wrapper2, tripleMapping);
					wrapper2.addIsContainedIn(wrapper1, tripleMapping);
				});
			});
		});
		output.patternWrappers = new LinkedList<>(idsToPatterns.values());
		return output;
	}

	@Override
	public JsonElement serialize(_PatternHierarchy src, Type typeOfSrc, JsonSerializationContext context) {
		HashMap<Integer, _PatternWrapper> idsToPatterns = new HashMap<>();
		HashMap<Integer, HashMap<Integer, List<_TmpEdgeRewriting>>> containmentsRewritings = new HashMap<>();
		src.patternWrappers.forEach(p -> {
			idsToPatterns.put(p.pattern.getId(), new _PatternWrapper(p.pattern));
			p.patternsContained.forEach((containedPattern, listOfMaps) -> {
				List<_TmpEdgeRewriting> tmpList = new LinkedList<>();
				listOfMaps.forEach(map -> {
					tmpList.add(new _TmpEdgeRewriting(map));
				});
				containmentsRewritings.computeIfAbsent(p.pattern.getId(), x -> new HashMap<>())
						.put(containedPattern.pattern.getId(), tmpList);
			});

		});
		return context.serialize(new IntermediateObjects(idsToPatterns, containmentsRewritings));
	}

	private static class IntermediateObjects {
		private IntermediateObjects(HashMap<Integer, _PatternWrapper> idsToPatterns,
				HashMap<Integer, HashMap<Integer, List<_TmpEdgeRewriting>>> containmentsRewritings) {
			super();
			this.idsToPatterns = idsToPatterns;
			this.containmentsRewritings = containmentsRewritings;
		}

		private HashMap<Integer, _PatternWrapper> idsToPatterns;
		private HashMap<Integer, HashMap<Integer, List<_TmpEdgeRewriting>>> containmentsRewritings;
	}

}
