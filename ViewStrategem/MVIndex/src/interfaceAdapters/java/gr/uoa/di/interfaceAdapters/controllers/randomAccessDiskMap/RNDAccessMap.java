package gr.uoa.di.interfaceAdapters.controllers.randomAccessDiskMap;

import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public abstract class RNDAccessMap<K, V> implements Closeable {

	protected GsonBuilder builder;
	private Gson gson;

	private Map<K, Long[]> map2Disk;
	private String rightParenthesis;
	private RandomAccessFile dataFile;
	private RandomAccessFile indexFile;
	//
	private Type keyToLongType;
	private Type valueType;

	protected RNDAccessMap() {

	}

	public void initialize(String indexFile, String dataFile, Type keyType, Type valueType) throws IOException {

		this.builder = new GsonBuilder().enableComplexMapKeySerialization();

		registerTypeAdapters();

		this.gson = getBuilder().create();
		// Sets the appropriate types
		this.keyToLongType = TypeToken.getParameterized(HashMap.class, keyType, new TypeToken<Long[]>() {
		}.getType()).getType();
		this.valueType = valueType;
		String tmpMap = new HashMap<K, Long>(0).toString();
		rightParenthesis = tmpMap.substring(tmpMap.length() - 1, tmpMap.length());
		if (new File(indexFile).exists()) {
			map2Disk = gson.fromJson(new FileReader(indexFile), keyToLongType);
			this.indexFile = new RandomAccessFile(indexFile, "rw");
		} else {
			this.indexFile = new RandomAccessFile(indexFile, "rw");
			map2Disk = new HashMap<K, Long[]>();
			this.indexFile.write(tmpMap.getBytes());
		}
		this.dataFile = new RandomAccessFile(dataFile, "rw");
	}

	public Set<K> keySet(){
		return map2Disk.keySet();
	}
	
	public abstract void registerTypeAdapters();

	public V put(K key, V value) {
		if (map2Disk.containsKey(key)) {
			return null;
		} else {
			try {
				// Writes Data File
				long objectStart = dataFile.length();
				dataFile.seek(objectStart);

				String str = gson.toJson(value);
				byte[] bytes = str.getBytes();
				dataFile.write(bytes);
				// Writes map2Sidk and indexFile
				Long[] position = new Long[] { objectStart, dataFile.length() - objectStart };
				map2Disk.put(key, position);
				indexFile.seek(indexFile.length() - 1);
				if (map2Disk.size() != 1) {
					indexFile.write(",".getBytes());
				}

				Map<K, Long[]> singleton = Collections.singletonMap(key, position);
				str = gson.toJson(singleton);
				str = str.substring(1, str.length() - 1);
				indexFile.write(str.getBytes());
				indexFile.write(rightParenthesis.getBytes());
				dataFile.write("\n".getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	@Override
	public void close() throws IOException {
		indexFile.close();
		dataFile.close();
	}

	public V get(K key) {
		Long[] position = map2Disk.get(key);
		if (position == null)
			return null;
		try {
			return getValue(position);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private V getValue(Long[] position) throws IOException {
		int len = position[1].intValue();
		byte[] bytes = new byte[len];
		dataFile.seek(position[0]);
		dataFile.read(bytes, 0, len);
		String str = new String(bytes);
		V output = gson.fromJson(str, valueType);
		return output;
	}

	public void forEach(BiConsumer<K, V> consumer) {
		map2Disk.forEach((k, position) -> {
			V v = null;
			try {
				v = getValue(position);
			} catch (IOException e) {
				e.printStackTrace();
			}
			consumer.accept(k, v);
		});
	}
	
	public int size() {
		return map2Disk.size();
	}

	public GsonBuilder getBuilder() {
		return builder;
	}
	
	public Iterator<V> valueIterator() {
		return new Iterator<V>(){
			
			Iterator<Entry<K, Long[]>> iter = map2Disk.entrySet().iterator();

			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public V next() {
				V v = null;
				try {
					v = getValue(iter.next().getValue());
				} catch (IOException e) {
					e.printStackTrace();
				}
				return v;
			}
		};
	}

}
