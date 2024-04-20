package gr.uoa.di.usecases.constants.dbpedia;

import java.io.File;
import java.io.IOException;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.viewSelection.materialization.TrieIndexForMaterialization;
import gr.uoa.di.interfaceAdapters.Resources;
import gr.uoa.di.interfaceAdapters.controllers.dataVault.object.trie.TrieVault;
import gr.uoa.di.interfaceAdapters.controllers.randomAccessDiskMap.patterns.RNDAccessPatterns;
import gr.uoa.di.interfaceAdapters.gateways.csv.CsvWriter;
import gr.uoa.di.usecases.constants.experiments.ConstantForExperiments;
import gr.uoa.di.usecases.constants.materialization.Constants;
import gr.uoa.di.usecases.constants.materialization.LSQAbstractSharableConstants;

public class LSQPrimordialConstants extends LSQAbstractSharableConstants implements Constants, ConstantForExperiments {
    //
    private String availableSizeString;
    final private String supportString;
    final private int support;
    private static final String CACHE_COUNT = "lsq-constants/cacheCount";
    private final String ROOT;
    private final String STATISTICS = "lsq-statistics";

    public static LSQPrimordialConstants create(String rootLocation, int support) {
        return new LSQPrimordialConstants(rootLocation, support);
    }

    //
    private LSQPrimordialConstants(String rootLocation, int support) {
        this.supportString = "lsq-support" + Integer.toString(support);
        this.support = support;
        this.ROOT = rootLocation;
    }

    private static final String CLEANED_PATTERNS = "2.lsqCleanedPatterns";

    private static final String TRIE_FILE = "lsq-primordial/4.trieIndex";

    @Override
    public RNDAccessPatterns getCleanedPatternMap(Dictionary dict) throws IOException {
        return RNDAccessPatterns.create(index(CLEANED_PATTERNS, supportString), data(CLEANED_PATTERNS, supportString),
                dict);
    }

    @Override
    public void serializeTrie(TrieIndexForMaterialization trie, Dictionary dict) throws IOException {
        try (TrieVault vault = TrieVault.create(file(TRIE_FILE, supportString, availableSizeString), dict)) {
            vault.serialize(trie);
        }

    }

    // Materialized Views index
    @Override
    public TrieIndexForMaterialization deserializeTrie(Dictionary dict) throws IOException {
        try (TrieVault vault = TrieVault.create(file(TRIE_FILE, supportString, availableSizeString), dict)) {
            return vault.deserialize();
        }
    }

    @Override
    public boolean trieIndexExists() {
        return new File(file(TRIE_FILE, supportString, availableSizeString)).exists();
    }

    @Override
    public boolean allowsOnlyPrimordialViews() {
        return true;
    }

    @Override
    public int getMinimumSupport() {
        return support;
    }

    @Override
    public String getFileWithCycles() {
        return Resources.fileWithLsqCycles;
    }

    @Override
    public String getFileWithPaths() {
        return Resources.fileWithLsqPaths;
    }

    @Override
    public String getFileForExtractedQueries() {
        return Resources.fileWithLsqLogExtractedQueries;
    }

    @Override
    public String getDatabaseMainURI() {
        return Resources.uriNeo4jMain;
    }

    @Override
    public String getDatabaseViewURI() {
        return Resources.uriNeo4jView;
    }

    @Override
    public String getUsernameMain() {
        return Resources.usernameNeo4jLSQMain;
    }

    @Override
    public String getPasswordMain() {
        return Resources.passwordNeo4jLSQMain;
    }

    @Override
    public String getUsernameView() {
        return Resources.usernameNeo4jLSQView;
    }

    @Override
    public String getPasswordView() {
        return Resources.passwordNeo4jLSQView;
    }

    @Override
    public String getImportDirectory() {
        return Resources.fileWithCsvViewsLSQ;
    }

    @Override
    public String getWorkloadFile() {
        return Resources.fileWithLsqLog;
    }

    @Override
    public String getSupportString() {
        return supportString;
    }

    @Override
    protected String getCacheCountFile() {
        return CACHE_COUNT;
    }

    @Override
    public String getRoot() {
        return ROOT;
    }

    @Override
    public CsvWriter getCsvWriter() throws IOException {
        return CsvWriter.create(getCsvFile());
    }

    @Override
    public CsvWriter getCsvWriter(boolean append) throws IOException {
        return CsvWriter.create(getCsvFile(), append);
    }

    @Override
    public boolean existsCsvFile() {
        return new File(getCsvFile()).exists();
    }

    @Override
    public String getCsvFile() {
        return file("lsq-primordial/" + STATISTICS, supportString, availableSizeString) + ".csv";
    }
}
