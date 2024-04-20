package gr.uoa.di.entities.viewSelection._steps;

import gr.uoa.di.databaseConnectors.Neo4jConnector;
import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.viewSelection.hierarchy.PatternHierarchy;
import gr.uoa.di.translators.cypher.CypherQuery;
import gr.uoa.di.translators.cypher.CypherTranslator;
import gr.uoa.di.usecases.constants.experiments.ConstantForExperiments;
import gr.uoa.di.usecases.constants.materialization.ConstantsSharable;
import gr.uoa.di.viewTemplates.*;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class Step_DeleteViews {
    public static <C extends ConstantsSharable & ConstantForExperiments> void deleteViews(C constants) throws IOException {
        Dictionary dict = constants.deserializeDictionary();
        PatternHierarchy hierarchy = constants.deserializeHierarchy(dict);
        AtomicInteger count = new AtomicInteger();

        //Create a converter to convert patterns to cypher queries
        CypherTranslator translator = new CypherTranslator();
        //Create the database connectors
        Neo4jConnector connector = new Neo4jConnector(constants.getDatabaseMainURI(), constants.getUsernameMain(), constants.getPasswordMain());
        Neo4jConnector viewConnector = new Neo4jConnector(constants.getDatabaseViewURI(), constants.getUsernameView(), constants.getPasswordView());

        hierarchy.forEachPatternContainedPatterns((pattern, containments) -> {
            count.getAndIncrement();
            System.out.println("Deleting : "+ count.get() +" "+ pattern);
            View eView = new EdgeLabelView(translator,connector,pattern,count.get());
            View nView = new NodeLabelView(translator,connector,pattern,count.get());
            View scView = new ShortcutView(translator,connector,pattern,count.get());
            View rView = new ReificationView(translator,connector,pattern,count.get());
            View subView = new SubgraphView(translator,connector,viewConnector,pattern,count.get(),constants.getImportDirectory());

            eView.delete();
            nView.delete();
            scView.delete();
            rView.delete();
            subView.delete();

        });
    }
}
