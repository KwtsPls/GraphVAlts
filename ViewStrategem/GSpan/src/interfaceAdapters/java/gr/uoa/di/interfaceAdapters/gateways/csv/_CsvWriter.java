package gr.uoa.di.interfaceAdapters.gateways.csv;

import java.io.FileWriter;
import java.io.IOException;

class _CsvWriter implements AutoCloseable, CsvWriter {

	private static String delimeter = "\t";

	private FileWriter myWriter;

	_CsvWriter(String filename, boolean append) throws IOException {
		myWriter = new FileWriter(filename, append);
		if (append == false) {
			myWriter.write("\"sep=" + delimeter + "\"\n");
		}
	}

	@Override
	public void write(String value) throws IOException {
		myWriter.write(value + delimeter);
	}

	@Override
	public void writeln(String value) throws IOException {
		myWriter.write(value + "\n");
	}

	@Override
	public void write(double... values) throws IOException {
		for (double value : values)
			myWriter.write(String.format("%.6f", value) + delimeter);
	}

	@Override
	public void write(String... values) throws IOException {
		for (String value : values)
			myWriter.write(value + delimeter);
	}

	@Override
	public void write(int... values) throws IOException {
		for (int value : values) {
			myWriter.write(value + delimeter);
		}
	}

	@Override
	public void writeln(int... values) throws IOException {
		for (int value : values)
			myWriter.write(value + delimeter);
		myWriter.write("\n");
	}

	@Override
	public void close() throws IOException {
		myWriter.close();
	}

	@Override
	public void flush() throws IOException {
		myWriter.flush();
	}

}
