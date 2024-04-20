package gr.uoa.di.interfaceAdapters.controllers.dataVault.map;

import java.io.IOException;

import com.google.gson.reflect.TypeToken;

import gr.uoa.di.entities.helpStructures.tuples.MyPair;
import gr.uoa.di.interfaceAdapters.controllers.randomAccessDiskMap.RNDAccessMap;

import java.lang.reflect.Type;

abstract class _HelpMap<K,E> extends RNDAccessMap<Integer, MyPair<K,E>>{
	
	_HelpMap(String indexFile, String dataFile, Type keyType, Type valueType)
			throws IOException {
		Type type = TypeToken.getParameterized(MyPair.class, keyType,valueType).getType();
		super.initialize(indexFile, dataFile, Integer.class, type);
	}

	@Override
	abstract public void registerTypeAdapters();
	
}
