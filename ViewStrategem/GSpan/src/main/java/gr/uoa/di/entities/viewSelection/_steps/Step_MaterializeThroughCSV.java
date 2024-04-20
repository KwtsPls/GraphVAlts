package gr.uoa.di.entities.viewSelection._steps;

import gr.uoa.di.databaseConnectors.Neo4jConnector;
import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.PatternVS;
import gr.uoa.di.entities.viewSelection.hierarchy.PatternHierarchy;
import gr.uoa.di.entities.viewSelection.queryRewriting.PatternMetadata;
import gr.uoa.di.translators.cypher.CypherQuery;
import gr.uoa.di.translators.cypher.CypherTranslator;
import gr.uoa.di.usecases.constants.experiments.ConstantForExperiments;
import gr.uoa.di.usecases.constants.materialization.ConstantsSharable;
import gr.uoa.di.viewTemplates.*;
import org.apache.jena.atlas.lib.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Step_MaterializeThroughCSV {
    public static <C extends ConstantsSharable & ConstantForExperiments> void materializePatterns(C constants) throws IOException {
        Dictionary dict = constants.deserializeDictionary();
        PatternHierarchy hierarchy = constants.deserializeHierarchy(dict);
        AtomicInteger count = new AtomicInteger();

        //Create a converter to convert patterns to cypher queries
        CypherTranslator translator = new CypherTranslator();

        //Create the database connectors
        Neo4jConnector connector = new Neo4jConnector(constants.getDatabaseMainURI(), constants.getUsernameMain(), constants.getPasswordMain());
        Neo4jConnector viewConnector = new Neo4jConnector(constants.getDatabaseViewURI(), constants.getUsernameView(), constants.getPasswordView());

        //Load all pattern metadata
        List<PatternMetadata> patternMetadata = new ArrayList<>();
        File f = new File("tmp\\stats.csv");
        if(f.exists() && !f.isDirectory()) {
            try {
                BufferedReader reader;
                reader = new BufferedReader(new FileReader("tmp\\stats.csv"));
                //skip headers
                reader.readLine();
                String line = reader.readLine();

                while (line != null) {
                    PatternMetadata metadata = new PatternMetadata(line);
                    metadata.initialize(hierarchy,translator,connector,viewConnector,constants.getImportDirectory());
                    patternMetadata.add(metadata);
                    // read next line
                    line = reader.readLine();
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for(PatternMetadata pattern:patternMetadata){
            if(pattern.getId()>0) {
                System.out.println(pattern.getEntry());

                if (pattern.getSubgraphView() != null) {
                    pattern.getSubgraphView().materialize();
                    pattern.getSubgraphView().delete();
                }

                if (pattern.getEdgeLabelView() != null)
                    pattern.getEdgeLabelView().materializeThroughFile(constants.getImportDirectory());

                if (pattern.getNodeLabelView() != null)
                    pattern.getNodeLabelView().materializeThroughFile(constants.getImportDirectory());

                if (pattern.getShortcutView() != null)
                    pattern.getShortcutView().materialize();

                if (pattern.getReificationView() != null)
                    pattern.getReificationView().materializeThroughFile(constants.getImportDirectory());

            }
        }
    }
}