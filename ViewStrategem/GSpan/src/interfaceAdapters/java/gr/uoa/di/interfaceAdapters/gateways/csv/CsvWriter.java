package gr.uoa.di.interfaceAdapters.gateways.csv;

import java.io.IOException;

public interface CsvWriter extends AutoCloseable {

	public static CsvWriter create(String filename) throws IOException {
		return new _CsvWriter(filename, false);
	}

	public static CsvWriter create(String filename, boolean append) throws IOException {
		return new _CsvWriter(filename, append);
	}

// TODO Remove unused code found by UCDetector
// 	public static CsvWriter create(String filename, boolean append, CsvReader csvReader) throws IOException {
// 		return new _CsvWriter(filename, append, csvReader);
// 	}

	void write(String value) throws IOException;

	void writeln(String value) throws IOException;

	void write(double... values) throws IOException;

	void write(int... values) throws IOException;

	void writeln(int... values) throws IOException;

	void flush() throws IOException;

	@Override
	public void close() throws IOException;

	void write(String... values) throws IOException;

}