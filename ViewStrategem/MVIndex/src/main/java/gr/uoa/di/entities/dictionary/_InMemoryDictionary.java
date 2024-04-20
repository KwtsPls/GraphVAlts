package gr.uoa.di.entities.dictionary;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;


 class _InMemoryDictionary implements Dictionary {

	public BiMap<Object, Integer> entries = HashBiMap.create();
	private int counter = Integer.MIN_VALUE;
	private boolean insertionStage = true;

	public _InMemoryDictionary() {
		// Creating Marks
		entries.put(new _Mark("isVariable"), Dictionary.variableLabel);
		entries.put(new _Mark("->"), Dictionary.directionLabel);
		entries.put(new _Mark("isA"), Dictionary.isALabel);
		entries.put(new _Mark("edgeSeperator"), Dictionary.invEdgeMark);
		entries.put(new _Mark("edgeEndMark"), Dictionary.endEdgeMark);
		// Creating 100 Variables
		entries.putAll(_DictionaryHelper.varEntries);

	}

	@Override
	public Object getContantOfId(int id) {
		return entries.inverse().get(id);
	}

	@Override
	public Integer getIdOfConstant(Object constant) {
		return entries.compute(constant,
				(key, value) -> value == null ? (insertionStage ? counter++ : Dictionary.unexaminedConstant) : value);

	}

	@Override
	public void setStage(Stage stage) {
		switch (stage) {
		case INSERTION_STAGE:
			insertionStage = true;
			break;
		case CONTAINMENT_STAGE:
			insertionStage = false;
			break;
		}
	}

}
