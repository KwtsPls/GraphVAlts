package gr.uoa.di.interfaceAdapters.controllers.dataVault.map;

import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.GsonBuilder;

import gr.uoa.di.entities.helpStructures.tuples.MyPair;

abstract public class MapVault<K, E> implements AutoCloseable, MapVaultInterface<K, E> {
	private static final int prime = 31;

	private _HelpMap<K, E> helpMap;

	public static <K, E> MapVaultInterface<K, E> create(String indexFile, String dataFile, Type keyType, Type valueType)
			throws IOException {
		return new MapVault<K, E>(indexFile, dataFile, keyType, valueType) {
			@Override
			public void registerTypeAdapters(GsonBuilder builder2) {
			}
		};
	}

	protected MapVault() {
		
	}
	
	private MapVault(String indexFile, String dataFile, Type keyType, Type valueType) throws IOException {
		initialize(indexFile,dataFile,keyType,valueType);
	}
	
	private void initialize(String indexFile, String dataFile, Type keyType, Type valueType) throws IOException{
		helpMap = new _HelpMap<K, E>(indexFile, dataFile, keyType, valueType) {
			@Override
			public void registerTypeAdapters() {
				MapVault.this.registerTypeAdapters(builder);
			}
		};
	}

	public abstract void registerTypeAdapters(GsonBuilder builder2);

	@Override
	public E get(K key) {
		int hashCode = key.hashCode();
		MyPair<K, E> pair;
		while ((pair = helpMap.get(hashCode)) != null && !key.equals(pair.getLeft())) {
			hashCode = nextHash(hashCode);
		}
		return pair == null ? null : pair.getRight();
	}

	@Override
	public E putIfAbsent(K key, E value) {
		int hashCode = key.hashCode();
		MyPair<K, E> pair;
		while ((pair = helpMap.get(hashCode)) != null && !key.equals(pair.getLeft())) {
			hashCode = nextHash(hashCode);
			System.out.println(key + " vs " + pair.getLeft());
		}
		if (pair == null) {
			helpMap.put(hashCode, MyPair.of(key, value));
			return value;
		} else {
			return pair.getRight();
		}
	}

	private int nextHash(int currentHashCode) {
		int result = currentHashCode;
		result = prime * result + 1;
		return result;
	}

	@Override
	public void close() throws IOException {
		helpMap.close();
	}

	@Override
	public int size() {
		return helpMap.size();
	}

}
