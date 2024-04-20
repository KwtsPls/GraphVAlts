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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Step_MaterializePatterns {
    public static <C extends ConstantsSharable & ConstantForExperiments> void materializePatterns(C constants) throws IOException {
        Dictionary dict = constants.deserializeDictionary();
        PatternHierarchy hierarchy = constants.deserializeHierarchy(dict);
        AtomicInteger count = new AtomicInteger();

        //Create a converter to convert patterns to cypher queries
        CypherTranslator translator = new CypherTranslator();

        BufferedWriter stats = new BufferedWriter(new FileWriter(constants.getRoot()+File.separator+"stats.csv"));
        stats.write("ViewName|Pattern|Edge|Cost|StorageE|StorageN|StorageP|StorageR|StorageS\n");

        List<PatternVS> patternVSList = new ArrayList<>();
        hierarchy.forEachPatternContainedPatterns((patternVS, myPairIterator) -> {
            patternVSList.add(patternVS);
        });

        //Create the database connectors
        Neo4jConnector connector = new Neo4jConnector(constants.getDatabaseMainURI(), constants.getUsernameMain(), constants.getPasswordMain());
        Neo4jConnector viewConnector = new Neo4jConnector(constants.getDatabaseViewURI(), constants.getUsernameView(), constants.getPasswordView());

        for(PatternVS pattern:patternVSList) {
            count.getAndIncrement();

            if(count.get()!=4 && count.get()!=55) {

                System.out.println("-------------------------------");
                System.out.println(count.get()+" "+pattern.toFullString());
                System.out.println(translator.convert(pattern));

                //Get estimated result - ignore queries with very large result sets
                if (connector.estimatedResultSize(translator.convert(pattern).toString()) <= 2000000.0) {

                    Pair<String, Pair<Double, Long>> pair = connector.getBasicStats(translator.convert(pattern).toString());
                    Long result = pair.getRight().getRight();
                    Double storage_edge = -1.0;
                    Double storage_node = -1.0;
                    Double storage_shortcut = -1.0;
                    Double storage_reification = -1.0;
                    Double storage_subgraph = -1.0;


                    //Query pattern does not have an empty result set
                    if (result != 0 && result <= 2000000.0) {
                        View eView = new EdgeLabelView(translator, connector, pattern, count.get(), pair.getLeft());
                        View nView = new NodeLabelView(translator, connector, pattern, count.get(), pair.getLeft());
                        View rView = new ReificationView(translator, connector, pattern, count.get());
                        View sView = new SubgraphView(translator, connector, viewConnector, pattern, count.get(), constants.getImportDirectory());

                        storage_subgraph = sView.materialize();
                        sView.delete();
                        storage_reification = rView.materialize();

                        //No property paths - check for candidate edge
                        if (!translator.convert(pattern).containsPropertyPath()) {
                            if (pair.getLeft() != null) {
                                storage_edge = eView.materialize();
                                storage_node = nView.materialize();
                            }
                        }
                        //Materialize shortcut view
                        else {
                            String path = connector.getVarLengthPath(translator.convert(pattern).toString());
                            View shortcutView = new ShortcutView(translator, connector, pattern, count.get(), path);
                            storage_shortcut = shortcutView.materialize();
                        }

                    }

                    try {
                        stats.write(count.get() + "|" + pattern.toFullString() + "|" + pair.getLeft() + "|" + pair.getRight().getLeft() + "|" + storage_edge + "|" + storage_node +
                                "|" + storage_shortcut + "|" + storage_reification + "|" + storage_subgraph + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        }


        try {
            connector.close();
            viewConnector.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        stats.close();
    }
}
