package gr.uoa.di.entities.dictionary;

import java.util.Iterator;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

class _DictionaryHelper {
	
	static final BiMap<Object, Integer> varEntries = _DictionaryHelper.initializeVarEntries();
	
	private static BiMap<Object, Integer> initializeVarEntries() {
		HashBiMap<Object, Integer> map = HashBiMap.create(Dictionary.lastVarId-Dictionary.firstVarId+1);
		for (int varID = Dictionary.firstVarId, varPosition = 0; varID<=Dictionary.lastVarId; varID++, varPosition++) {
			map.put(new _DictVar(varPosition,varID), varID);
		}
		return map;
	}
	
	static Iterator<Integer> getVarIdIterator() {
		return new Iterator<Integer>() {
			
			int i=Dictionary.firstVarId;

			@Override
			public boolean hasNext() {
				if(i==Dictionary.lastVarId)
					return false;
				else
					return true;
			}

			@Override
			public Integer next() {
				if(i<=Dictionary.lastVarId)
					return i++;
				else
					return null;
			}
			
		};
	}

}
