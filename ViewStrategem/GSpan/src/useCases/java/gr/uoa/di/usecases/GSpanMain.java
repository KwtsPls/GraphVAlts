package gr.uoa.di.usecases;

import java.io.File;
import java.io.IOException;

import gr.uoa.di.entities.viewSelection._steps.*;
import gr.uoa.di.interfaceAdapters.debug.MyDebug;
import gr.uoa.di.usecases.constants.dbpedia.ColoursPrimordialConstants;
import gr.uoa.di.usecases.constants.dbpedia.DBPediaPrimordialConstants;
import gr.uoa.di.usecases.constants.dbpedia.LSQPrimordialConstants;
import gr.uoa.di.usecases.constants.dbpedia.ROADNETPrimordialConstants;
import gr.uoa.di.usecases.constants.experiments.ConstantForExperiments;
import gr.uoa.di.usecases.constants.materialization.Constants;

class GSpanMain { // NO_UCD (unused code)

	static double tolerancePercentage = 0.1;
	///////////////

	public static void main(String[] args) throws Exception, IOException {
		if(args.length==2 && args[0].equals("views")){
			if(args[1].equals("dbpedia"))
				viewSelection(DBPediaPrimordialConstants.create("tmp/", 500));
			if(args[1].equals("bio2rdf"))
				viewSelection(LSQPrimordialConstants.create("lsq-tmp/", 150));
			if(args[1].equals("colours"))
				viewSelection(ColoursPrimordialConstants.create("colours-tmp/", 100));
		}
		else if(args.length==2 && args[0].equals("clean")){
			if(args[1].equals("dbpedia"))
				deleteViews(DBPediaPrimordialConstants.create("tmp/", 500));
			if(args[1].equals("bio2rdf"))
				deleteViews(LSQPrimordialConstants.create("lsq-tmp/", 150));
			if(args[1].equals("colours"))
				deleteViews(ColoursPrimordialConstants.create("colours-tmp/", 100));
		}
		else{
			System.err.println("Incorrect arguments\n");
		}
	}

	static <C extends Constants & ConstantForExperiments> void viewSelection(C constants)
			throws Exception, IOException {

		//
		Step_CreateCyclesAndPaths.materializePatterns(constants,4000,0.01);
		//
//		System.gc();
		System.out.println("0st Step: Creating Statistics");
		MyDebug.printAndLog(() -> "0st Step: Creating Statistics");
		Step_Preprocessing.createLabelStatistics(constants);
		//

		MyDebug.printAndLog(() -> "0st Step: Create Test Set");
		Step_TestSet_Creation.create(constants, 10000);
		System.out.println("1st Step: Transforming Graph to gspan form and mining frequent patterns");
		MyDebug.printAndLog(() -> "1st Step: Transforming Graph to gspan form and mining frequent patterns");
		Step1_Mining.mineFrequentPatterns(constants);
		//
		System.out.println("2nd Step: Transform frequent patterns, create hierarchy, and clean non-benefiting patterns");
		MyDebug.printAndLog(
				() -> "2nd Step: Transform frequent patterns, create hierarchy, and clean non-benefiting patterns");
		Step2_CleaningAndHierarchyCreation.cleanPatternsAndCreateHierarchy(constants, tolerancePercentage);

		//
		//
		Step_MaterializePatterns.materializePatterns(constants);
		//

		Step3_IndexSelection.selectViews(constants, tolerancePercentage);
	}

	static <C extends Constants & ConstantForExperiments> void deleteViews(C constants)
			throws Exception, IOException {
		Step_DeleteViews.deleteViews(constants);
	}

}
