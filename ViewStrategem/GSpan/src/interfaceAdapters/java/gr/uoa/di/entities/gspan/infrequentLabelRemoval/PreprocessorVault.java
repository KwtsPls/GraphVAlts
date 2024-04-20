package gr.uoa.di.entities.gspan.infrequentLabelRemoval;

import java.io.IOException;

import gr.uoa.di.interfaceAdapters.controllers.dataVault.object.ObjectVault;

public class PreprocessorVault extends ObjectVault<GSPanPreprocessor> {

	public static PreprocessorVault create(String dataFile) throws IOException {
		return new PreprocessorVault(dataFile);
	}

	private PreprocessorVault(String dataFile) throws IOException {
		super.initialize(dataFile, GSPanPreprocessor.class);
	}

	@Override
	public void addSerializersDeserializers() {
		builder.registerTypeAdapter(GSPanPreprocessor.class, new _PreprocessorDeserializer());
	}
}
