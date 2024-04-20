package gr.uoa.di.usecases.constants.materialization;

import java.io.File;
import java.io.IOException;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.PatternVS;
import gr.uoa.di.entities.helpStructures.iterators.ClosableIterator;
import gr.uoa.di.entities.viewSelection.hierarchy.PatternHierarchy;
import gr.uoa.di.interfaceAdapters.controllers.randomAccessDiskMap.patterns.RNDAccessPatterns;

public interface ConstantsSharable extends ConstantsGlobal {

	boolean frequentPatternsExist();

	int getMinimumSupport();

	RNDAccessPatterns getGroupedQueryMap(Dictionary dict) throws IOException;

	File getGSpanFormatter();

	String getPatternLocation();

	ClosableIterator<PatternVS> getPatternMap(Dictionary dict) throws IOException;

	// Hierarchy Related Constants
	void serializeHierarchy(PatternHierarchy hierarchy, Dictionary dict) throws IOException;

	boolean existHieararchy();

	PatternHierarchy deserializeHierarchy(Dictionary dict) throws IOException;

	public String getRoot();

	//cycles and paths
	public String getFileWithCycles();
	public String getFileWithPaths();
	public String getFileForExtractedQueries();

	//database connection stats
	public String getDatabaseMainURI();
	public String getDatabaseViewURI();
	public String getUsernameMain();
	public String getPasswordMain();
	public String getUsernameView();
	public String getPasswordView();
	public String getImportDirectory();
	public String getWorkloadFile();

}
