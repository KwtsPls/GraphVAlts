package gr.uoa.di.usecases.constants.materialization;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.dictionary.DictionaryVault;
import gr.uoa.di.entities.graph.FactoryVS;
import gr.uoa.di.entities.graph.GraphVS;
import gr.uoa.di.entities.gspan.infrequentLabelRemoval.GSPanPreprocessor;
import gr.uoa.di.entities.gspan.infrequentLabelRemoval.PreprocessorVault;
import gr.uoa.di.interfaceAdapters.controllers.dataVault.object.ObjectVault;
import gr.uoa.di.interfaceAdapters.controllers.randomAccessDiskMap.patterns.RNDAccessPatterns;
import gr.uoa.di.interfaceAdapters.iterators.roadnet.ROADNETGraphQueryIterator;
import gr.uoa.di.interfaceAdapters.workloads.JenaGraphQueryIterator;

abstract class ROADNETAbstractGlobalConstants implements ConstantsGlobal {

    private static final String DICTIONARY_FILE = "0.roadnet-dictionary";
    private static final String GSPAN_PREPROCESSOR = "0.roadnet-preprocessor";
    private static final String GSPAN_QUERY_WORKLOAD = "0.roadnetQuery";
    private static final String BENEFITED_QUERIES = "0.roadnetTestingQueries";

    abstract protected String getRoot();

    @Override
    final public boolean dictionaryExists() {
        return new File(file(DICTIONARY_FILE)).exists();
    }

    @Override
    final public JenaGraphQueryIterator<GraphVS> getJenaQueryIterator(Dictionary dict) {
        return ROADNETGraphQueryIterator.createLowerCase(dict, new FactoryVS());
    }

    @Override
    final public JenaGraphQueryIterator<GraphVS> getSecondaryJenaQueryIterator(Dictionary dict) {
        return ROADNETGraphQueryIterator.createSecondary(dict, new FactoryVS());
    }

    @Override
    final public RNDAccessPatterns getQueryMap(Dictionary dict) throws IOException {
        return RNDAccessPatterns.create(index(GSPAN_QUERY_WORKLOAD), data(GSPAN_QUERY_WORKLOAD), dict);
    }

    @Override
    final public void serializeDictionary(Dictionary dict) throws IOException {
        try (DictionaryVault vault = new DictionaryVault(file(DICTIONARY_FILE))) {
            vault.serialize(dict);
        }
    }

    @Override
    final public Dictionary deserializeDictionary() throws IOException {
        try (DictionaryVault vault = new DictionaryVault(file(DICTIONARY_FILE))) {
            return vault.deserialize();
        }
    }

    // Preprocessor
    @Override
    final public GSPanPreprocessor deserializeGSPanPreprocessor() throws IOException {
        try (ObjectVault<GSPanPreprocessor> vault = PreprocessorVault.create(file(GSPAN_PREPROCESSOR))) {
            return vault.deserialize();
        }
    }

    @Override
    final public void serializeGSPanPreprocessor(GSPanPreprocessor preprocessor) throws IOException {
        try (ObjectVault<GSPanPreprocessor> vault = PreprocessorVault.create(file(GSPAN_PREPROCESSOR))) {
            vault.serialize(preprocessor);
        }
    }

    @Override
    final public RNDAccessPatterns getQueriesForTesting(Dictionary dict) throws IOException {
        return RNDAccessPatterns.create(index(BENEFITED_QUERIES), data(BENEFITED_QUERIES), dict);
    }

    @Override
    final public void deleteQueriesForTesting() {
        new File(index(BENEFITED_QUERIES)).delete();
    }

    @Override
    final public boolean queriesForTestingExist() {
        return new File(index(BENEFITED_QUERIES)).exists();
    }

    private final StringBuilder createDirectory(String fileString, String... attributes) {
        StringBuilder builder = new StringBuilder(getRoot());
        if (attributes.length > 0)
            builder.append(attributes[0]).append("/");
        File file = new File(fileString);
        String parent = file.getParent();
        builder.append(parent == null ? "" : parent);
        String name = file.getName() + ((attributes.length == 2) ? attributes[1] : "");

        Path path = Paths.get(builder.toString());
        if (!Files.exists(path))
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        builder.append("/").append(name);
        return builder;
    }

    protected final String index(String fileString, String... attributes) {
        StringBuilder builder = createDirectory(fileString, attributes);
        builder.append("Index.json");
        return builder.toString();
    }

    protected final String file(String fileString, String... attributes) {
        StringBuilder builder = createDirectory(fileString, attributes);
        builder.append(".json");
        return builder.toString();
    }

    protected final String data(String fileString, String... attributes) {
        StringBuilder builder = createDirectory(fileString, attributes);
        builder.append("Data.json");
        return builder.toString();
    }

}