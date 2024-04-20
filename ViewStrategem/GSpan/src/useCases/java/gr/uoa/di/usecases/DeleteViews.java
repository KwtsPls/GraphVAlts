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

public class DeleteViews {

    public static void main(String[] args) throws Exception, IOException {
        deleteViews(DBPediaPrimordialConstants.create("tmp/", 500));
        deleteViews(LSQPrimordialConstants.create("lsq-tmp/", 150));
        deleteViews(ColoursPrimordialConstants.create("colours-tmp/", 100));
    }

    static <C extends Constants & ConstantForExperiments> void deleteViews(C constants)
            throws Exception, IOException {
            Step_DeleteViews.deleteViews(constants);
    }
}
