package gr.uoa.di.interfaceAdapters.controllers.dataVault.object;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

public abstract class ObjectVault<V> implements AutoCloseable {

	protected GsonBuilder builder;
	private Gson gson;

	private Long position = null;

	private RandomAccessFile dataFile;
	//
	private Type valueType;

	
	protected ObjectVault() {
	}


	protected void initialize(String dataFile, Type type) throws IOException {
		this.builder = new GsonBuilder().enableComplexMapKeySerialization();
		addSerializersDeserializers();
		this.gson = builder.create();
		// Sets the appropriate types
		this.valueType = type;
		File file = new File(dataFile);
		if (file.exists()) {
			position = file.length();
		}
		this.dataFile = new RandomAccessFile(dataFile, "rw");
	}

	abstract public void addSerializersDeserializers();

	public V serialize(V value) {		
		try {
			dataFile.seek(0);
			// Writes Data File
			String str = gson.toJson(value);
			byte[] bytes = str.getBytes();
			dataFile.write(bytes);
			// Writes map2Sidk and indexFile
			position =  dataFile.length();			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}

	@Override
	public void close() throws IOException {
		dataFile.close();
	}

	public V deserialize() {
		if (position == null)
			return null;
		try {
			return getValue(position);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private V getValue(Long position) throws IOException {
		int len = position.intValue();
		byte[] bytes = new byte[len];
		dataFile.seek(0);
		dataFile.read(bytes, 0, len);
		String str = new String(bytes);
		return gson.fromJson(str, valueType);
	}
}
