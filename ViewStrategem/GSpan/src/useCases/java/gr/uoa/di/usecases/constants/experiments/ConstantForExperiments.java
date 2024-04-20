package gr.uoa.di.usecases.constants.experiments;

import java.io.IOException;

import gr.uoa.di.interfaceAdapters.gateways.csv.CsvWriter;

public interface ConstantForExperiments {

	CsvWriter getCsvWriter() throws IOException;

	CsvWriter getCsvWriter(boolean append) throws IOException;

	String getCsvFile();

	boolean existsCsvFile();

}
