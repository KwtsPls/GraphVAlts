package gr.uoa.di.interfaceAdapters.controllers.dataVault.map;


public interface MapVaultInterface<K, E> extends AutoCloseable{

	E get(K key);

	E putIfAbsent(K key, E value);

	int size();

}