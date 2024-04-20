package gr.uoa.di.usecases;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import gr.uoa.di.databaseConnectors.Connector;
import gr.uoa.di.databaseConnectors.Neo4jConnector;
import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.GraphVS;
import gr.uoa.di.entities.graph.PatternVS;
import gr.uoa.di.entities.gspan.infrequentLabelRemoval.GSPanPreprocessor;
import gr.uoa.di.entities.viewSelection._steps.Step_DeleteViews;
import gr.uoa.di.entities.viewSelection.hierarchy.PatternHierarchy;
import gr.uoa.di.interfaceAdapters.Resources;
import gr.uoa.di.interfaceAdapters.controllers.randomAccessDiskMap.patterns.RNDAccessPatterns;
import gr.uoa.di.interfaceAdapters.debug.MyDebug;
import gr.uoa.di.interfaceAdapters.workloads.JenaGraphQueryIterator;
import gr.uoa.di.translators.Translator;
import gr.uoa.di.translators.cypher.CypherTranslator;
import gr.uoa.di.usecases.constants.dbpedia.ColoursPrimordialConstants;
import gr.uoa.di.usecases.constants.dbpedia.DBPediaPrimordialConstants;
import gr.uoa.di.usecases.constants.experiments.ConstantForExperiments;
import gr.uoa.di.usecases.constants.materialization.Constants;
import gr.uoa.di.viewTemplates.*;
import org.apache.jena.atlas.lib.Pair;

import java.io.*;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class ColoursPatternCreator {

    public static void main(String[] args) throws Exception, IOException {
        //materializeColourPatterns(ColoursPrimordialConstants.create("colours-tmp/", 0));
        executeAll();
    }

    static <C extends Constants & ConstantForExperiments> void materializeColourPatterns(C constants)
            throws Exception, IOException {

        String[] patterns = {
                "(?x0,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/blue)(?x1,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/red)(?x2,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/green)(?x0,http://dummy.com/r,?x1)(?x1,http://dummy.com/r,?x2)(?x2,http://dummy.com/r,?x0)",
                "(?x0,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/blue)(?x1,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/blue)(?x2,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/green)(?x0,http://dummy.com/r,?x1)(?x1,http://dummy.com/r,?x2)(?x2,http://dummy.com/r,?x0)",
                "(?x0,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/blue)(?x1,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/red)(?x2,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/blue)(?x0,http://dummy.com/r,?x1)(?x1,http://dummy.com/r,?x2)(?x2,http://dummy.com/r,?x0)",
                "(?x0,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/blue)(?x1,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/blue)(?x2,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/blue)(?x0,http://dummy.com/r,?x1)(?x1,http://dummy.com/r,?x2)(?x2,http://dummy.com/r,?x0)",
                "(?x0,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/blue)(?x1,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/green)(?x2,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/green)(?x0,http://dummy.com/r,?x1)(?x1,http://dummy.com/r,?x2)(?x2,http://dummy.com/r,?x0)",
                "(?x0,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/blue)(?x1,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/green)(?x2,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/red)(?x0,http://dummy.com/r,?x1)(?x1,http://dummy.com/r,?x2)(?x2,http://dummy.com/r,?x0)",
                "(?x0,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/green)(?x1,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/red)(?x2,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/green)(?x0,http://dummy.com/r,?x1)(?x1,http://dummy.com/r,?x2)(?x2,http://dummy.com/r,?x0)",
                "" + "(?x0,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/blue)(?x1,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/green)(?x0,http://dummy.com/r,?x1)(?x1,http://dummy.com/r,?x2)(?x1,http://dummy.com/r,?x3)",
                "(?x0,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/red)(?x1,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/blue)(?x0,http://dummy.com/r,?x1)",
                "(?x0,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/green)(?x1,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/blue)(?x2,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/red)(?x0,http://dummy.com/r,?x1)(?x0,http://dummy.com/r,?x2)",
                "(?x0,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/green)(?x0,http://dummy.com/r,?x1)(?x1,http://dummy.com/r,?x2)(?x2,http://dummy.com/r,?x3)",
                "(?x0,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/red)(?x0,http://dummy.com/r,?x1)(?x1,http://dummy.com/r,?x2)(?x2,http://dummy.com/r,?x3)(?x3,http://dummy.com/r,?x4)",
                "(?x0,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/blue)(?x3,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/blue)(?x0,http://dummy.com/r,?x1)(?x1,http://dummy.com/r,?x2)(?x2,http://dummy.com/r,?x3)",
                "" + "(?x0,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/blue)(?x1,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/red)(?x0,http://dummy.com/r*,?x1)",
                "(?x0,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/green)(?x0,http://dummy.com/r*,?x1)",
                "(?x0,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/red)(?x0,http://dummy.com/r*,?x1)",
                "(?x0,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/green)(?x1,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/red)(?x2,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/blue)(?x0,http://dummy.com/r,?x1)(?x1,http://dummy.com/r,?x2)(?x2,http://dummy.com/r*,?x3)",
                "(?x0,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/blue)(?x1,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/green)(?x2,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/red)(?x0,http://dummy.com/r,?x1)(?x1,http://dummy.com/r,?x2)(?x2,http://dummy.com/r*,?x3)",
                "(?x0,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/red)(?x1,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/green)(?x2,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://dummy.com/blue)(?x0,http://dummy.com/r,?x1)(?x1,http://dummy.com/r,?x2)(?x2,http://dummy.com/r*,?x3)"
        };

        String[] sparql = {
                "PREFIX dummy :<http://dummy.com/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * WHERE { ?x0 rdf:type dummy:blue . ?x1 rdf:type dummy:red . ?x2 rdf:type dummy:green . ?x0 dummy:r ?x1 . ?x1 dummy:r ?x2 . ?x2 dummy:r ?x0 .}",
                "PREFIX dummy :<http://dummy.com/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * WHERE { ?x0 rdf:type dummy:blue . ?x1 rdf:type dummy:blue . ?x2 rdf:type dummy:green . ?x0 dummy:r ?x1 . ?x1 dummy:r ?x2 . ?x2 dummy:r ?x0 .}",
                "PREFIX dummy :<http://dummy.com/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * WHERE { ?x0 rdf:type dummy:blue . ?x1 rdf:type dummy:red . ?x2 rdf:type dummy:blue . ?x0 dummy:r ?x1 . ?x1 dummy:r ?x2 . ?x2 dummy:r ?x0 .}",
                "PREFIX dummy :<http://dummy.com/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * WHERE { ?x0 rdf:type dummy:blue . ?x1 rdf:type dummy:blue . ?x2 rdf:type dummy:blue . ?x0 dummy:r ?x1 . ?x1 dummy:r ?x2 . ?x2 dummy:r ?x0 .}",
                "PREFIX dummy :<http://dummy.com/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * WHERE { ?x0 rdf:type dummy:blue . ?x1 rdf:type dummy:green . ?x2 rdf:type dummy:green . ?x0 dummy:r ?x1 . ?x1 dummy:r ?x2 . ?x2 dummy:r ?x0 .}",
                "PREFIX dummy :<http://dummy.com/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * WHERE { ?x0 rdf:type dummy:blue . ?x1 rdf:type dummy:green . ?x2 rdf:type dummy:red . ?x0 dummy:r ?x1 . ?x1 dummy:r ?x2 . ?x2 dummy:r ?x0 .}",
                "PREFIX dummy :<http://dummy.com/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * WHERE { ?x0 rdf:type dummy:green . ?x1 rdf:type dummy:red . ?x2 rdf:type dummy:blue . ?x0 dummy:r ?x1 . ?x1 dummy:r ?x2 . ?x2 dummy:r ?x0 .}",
                "" + "PREFIX dummy :<http://dummy.com/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * WHERE { ?x0 rdf:type dummy:blue . ?x1 rdf:type dummy:green . ?x0 dummy:r ?x1 . ?x1 dummy:r ?x2 . ?x1 dummy:r ?x3 .}",
                "PREFIX dummy :<http://dummy.com/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * WHERE { ?x0 rdf:type dummy:red . ?x1 rdf:type dummy:blue . ?x0 dummy:r ?x1 . }",
                "PREFIX dummy :<http://dummy.com/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * WHERE { ?x0 rdf:type dummy:green . ?x1 rdf:type dummy:blue . ?x2 rdf:type dummy:red . ?x0 dummy:r ?x1 . ?x0 dummy:r ?x2 . }",
                "PREFIX dummy :<http://dummy.com/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * WHERE { ?x0 rdf:type dummy:green . ?x0 dummy:r ?x1 . ?x1 dummy:r ?x2 . ?x2 dummy:r ?x3 . }",
                "PREFIX dummy :<http://dummy.com/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * WHERE { ?x0 rdf:type dummy:red . ?x0 dummy:r ?x1 . ?x1 dummy:r ?x2 . ?x2 dummy:r ?x3 . ?x3 dummy:r ?x4 . }",
                "PREFIX dummy :<http://dummy.com/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * WHERE { ?x0 rdf:type dummy:blue . ?x1 rdf:type dummy:blue . ?x0 dummy:r ?x1 . ?x1 dummy:r ?x2 . ?x2 dummy:r ?x3 . }",
                "" + "PREFIX dummy :<http://dummy.com/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * WHERE { ?x0 rdf:type dummy:blue . ?x1 rdf:type dummy:red . ?x0 dummy:r* ?x1 . }",
                "PREFIX dummy :<http://dummy.com/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * WHERE { ?x0 rdf:type dummy:green . ?x0 dummy:r* ?x1 . }",
                "PREFIX dummy :<http://dummy.com/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * WHERE { ?x0 rdf:type dummy:red . ?x0 dummy:r* ?x1 . }",
                "PREFIX dummy :<http://dummy.com/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * WHERE { ?x0 rdf:type dummy:green . ?x1 rdf:type dummy:red . ?x2 rdf:type dummy:blue . ?x0 dummy:r ?x1 . ?x1 dummy:r ?x2 . ?x2 dummy:r* ?x3 . }",
                "PREFIX dummy :<http://dummy.com/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * WHERE { ?x0 rdf:type dummy:blue . ?x1 rdf:type dummy:green . ?x2 rdf:type dummy:red . ?x0 dummy:r ?x1 . ?x1 dummy:r ?x2 . ?x2 dummy:r* ?x3 . }",
                "PREFIX dummy :<http://dummy.com/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT * WHERE { ?x0 rdf:type dummy:red . ?x1 rdf:type dummy:green . ?x2 rdf:type dummy:blue . ?x0 dummy:r ?x1 . ?x1 dummy:r ?x2 . ?x2 dummy:r* ?x3 . }"
        };

        GSPanPreprocessor preprocessor = GSPanPreprocessor.create();
        Dictionary dict = Dictionary.create();
        // Scans the file and keeps statistics about frequent triples
        try (JenaGraphQueryIterator<GraphVS> iter = constants.getJenaQueryIterator(dict);
             RNDAccessPatterns queries = constants.getQueryMap(dict);) {

            int[] meter = { 0 };
            while (iter.hasNext()) {
                GraphVS graph = iter.next();
                if (graph == null)
                    continue;
                preprocessor.createLabelStatistics(graph);
                PatternVS pattern = PatternVS.create(graph, meter[0], 1, new Integer[] { meter[0] });
                queries.put(meter[0], pattern);
                pattern.setSupport(1);
                meter[0]++;
            }

            MyDebug.printAndLog(() -> "# Queries Within the workload:" + queries.size());
            constants.serializeGSPanPreprocessor(preprocessor);
            constants.serializeDictionary(dict);
        } catch (IOException e) {
            e.printStackTrace();
        }

        dict = constants.deserializeDictionary();

        BufferedWriter stats = new BufferedWriter(new FileWriter(constants.getRoot()+ File.separator+"stats.csv"));
        stats.write("ViewName|Pattern|Edge|Cost|StorageE|StorageN|StorageP|StorageR|StorageS\n");

        CypherTranslator translator = new CypherTranslator();
        Neo4jConnector connector = new Neo4jConnector(Resources.uriNeo4jMain,Resources.usernameNeo4jCOLOURSMain,Resources.passwordNeo4jCOLOURSMain);
        Neo4jConnector viewConnector = new Neo4jConnector(Resources.uriNeo4jView,Resources.usernameNeo4jCOLOURSView,Resources.passwordNeo4jCOLOURSView);

        RNDAccessPatterns queries = null;
        try {
            queries = constants.getQueryMap(dict);
            Iterator<PatternVS> iter = queries.valueIterator();

            int count=0;
            while(iter.hasNext()){
                PatternVS pattern = iter.next();
                System.out.println(pattern.toFullString());
                Pair<String, Pair<Double, Long>> pair = connector.getBasicStats(translator.convert(pattern).toString());
                System.out.println(translator.convert(pattern)+"\n");

                Long result = pair.getRight().getRight();
                Double storage_edge = -1.0;
                Double storage_node = -1.0;
                Double storage_shortcut = -1.0;
                Double storage_reification = -1.0;
                Double storage_subgraph = -1.0;


                //Query pattern does not have an empty result set
                if (result != 0) {
                    View eView = new EdgeLabelView(translator, connector, pattern, count, pair.getLeft());
                    View nView = new NodeLabelView(translator, connector, pattern, count, pair.getLeft());
                    View rView = new ReificationView(translator, connector, pattern, count);
                    View sView = new SubgraphView(translator, connector, viewConnector, pattern, count, constants.getImportDirectory());

                    storage_subgraph = sView.materialize();
                    //sView.delete();
                    //storage_reification = rView.materialize();

                    //No property paths - check for candidate edge
                    if (!translator.convert(pattern).containsPropertyPath()) {
                           // storage_edge = eView.materialize();
                            //storage_node = nView.materialize();
                    }
                    //Materialize shortcut view
                    else {
                        //String path = connector.getVarLengthPath(translator.convert(pattern).toString());
                        //View shortcutView = new ShortcutView(translator, connector, pattern, count, path);
                        //storage_shortcut = shortcutView.materialize();
                    }

                }

                //stats.write(count + "|" + pattern + "|" + pair.getLeft() + "|" + pair.getRight().getLeft() + "|" + storage_edge + "|" + storage_node +
                           // "|" + storage_shortcut + "|" + storage_reification + "|" + storage_subgraph + "\n");

                //System.out.println(count + "|" + pattern + "|" + pair.getLeft() + "|" + pair.getRight().getLeft() + "|" + storage_edge + "|" + storage_node +
                  //      "|" + storage_shortcut + "|" + storage_reification + "|" + storage_subgraph + "\n");
                count++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void executeSome(){
        Neo4jConnector neo4jConnector = new Neo4jConnector(Resources.uriNeo4jMain,Resources.usernameNeo4jCOLOURSMain,Resources.passwordNeo4jCOLOURSMain);
        Long result = neo4jConnector.write("MATCH (x0),(x0)-[:`http://dummy.com/r`]->(x1),(x0:`http://dummy.com/green`),(x1),(x1)-[:`http://dummy.com/r`]->(x2),(x2),(x2)-[:`http://dummy.com/r`]->(x3) MERGE (x0)-[:View_10]->(x1) RETURN x0,x1,x2,x3");
        System.out.println("10 :"+result);
        result = neo4jConnector.write("MATCH (x0),(x0)-[:`http://dummy.com/r`]->(x1),(x0:`http://dummy.com/green`),(x1),(x1)-[:`http://dummy.com/r`]->(x2),(x2),(x2)-[:`http://dummy.com/r`]->(x3) SET x0:View_10 RETURN x0,x1,x2,x3");
        System.out.println("10_l :"+result);
        System.out.println("--------------------------------");

        result = neo4jConnector.write("MATCH (x0),(x0)-[:`http://dummy.com/r`]->(x1),(x0:`http://dummy.com/red`),(x1),(x1)-[:`http://dummy.com/r`]->(x2),(x2),(x2)-[:`http://dummy.com/r`]->(x3),(x3),(x3)-[:`http://dummy.com/r`]->(x4) MERGE (x0)-[:View_11]->(x1) RETURN x0,x1,x2,x3,x4");
        System.out.println("11 :"+result);
        result = neo4jConnector.write("MATCH (x0),(x0)-[:`http://dummy.com/r`]->(x1),(x0:`http://dummy.com/red`),(x1),(x1)-[:`http://dummy.com/r`]->(x2),(x2),(x2)-[:`http://dummy.com/r`]->(x3),(x3),(x3)-[:`http://dummy.com/r`]->(x4) SET x0:View_11 RETURN x0,x1,x2,x3,x4");
        System.out.println("11_l :"+result);
        System.out.println("--------------------------------");

        result = neo4jConnector.write("MATCH (x0),(x0)-[:`http://dummy.com/r`]->(x1),(x0:`http://dummy.com/blue`),(x1),(x1)-[:`http://dummy.com/r`]->(x2),(x1:`http://dummy.com/blue`),(x2),(x2)-[:`http://dummy.com/r`]->(x3) MERGE (x0)-[:View_12]->(x1) RETURN x0,x1,x2,x3");
        System.out.println("12 :"+result);
        result = neo4jConnector.write("MATCH (x0),(x0)-[:`http://dummy.com/r`]->(x1),(x0:`http://dummy.com/blue`),(x1),(x1)-[:`http://dummy.com/r`]->(x2),(x1:`http://dummy.com/blue`),(x2),(x2)-[:`http://dummy.com/r`]->(x3) SET x0:View_12 RETURN x0,x1,x2,x3");
        System.out.println("12_l :"+result);
        System.out.println("--------------------------------");

        result = neo4jConnector.write("MATCH (x0),(x0)-[:`http://dummy.com/r`]->(x1),(x1),(x1)-[:`http://dummy.com/r`]->(x2),(x1)-[:`http://dummy.com/r`]->(x3) MERGE (x0)-[:View_7]->(x1) RETURN x0,x1,x2,x3");
        System.out.println("7 :"+result);
        result = neo4jConnector.write("MATCH (x0),(x0)-[:`http://dummy.com/r`]->(x1),(x1),(x1)-[:`http://dummy.com/r`]->(x2),(x1)-[:`http://dummy.com/r`]->(x3) SET x0:View_7 RETURN x0,x1,x2,x3");
        System.out.println("7_l :"+result);
        System.out.println("--------------------------------");
    }


    public static void executeAll(){
        Neo4jConnector neo4jConnector = new Neo4jConnector(Resources.uriNeo4jMain,Resources.usernameNeo4jCOLOURSMain,Resources.passwordNeo4jCOLOURSMain);
        Neo4jConnector viewConnector = new Neo4jConnector(Resources.uriNeo4jView,Resources.usernameNeo4jCOLOURSView,Resources.passwordNeo4jCOLOURSView);
        CypherTranslator translator = new CypherTranslator();

        String edge_query = "MATCH (x0)-[:View_12]->(x1),(x0)-[:`http://dummy.com/r`]->(x1),(x0:`http://dummy.com/blue`),(x1),(x1)-[:`http://dummy.com/r`]->(x2),(x1:`http://dummy.com/blue`),(x2),(x2)-[:`http://dummy.com/r`]->(x3) RETURN x0,x1,x2,x3";
        String node_query = "MATCH (x0:View_12),(x0)-[:`http://dummy.com/r`]->(x1),(x0:`http://dummy.com/blue`),(x1),(x1)-[:`http://dummy.com/r`]->(x2),(x1:`http://dummy.com/blue`),(x2),(x2)-[:`http://dummy.com/r`]->(x3) RETURN x0,x1,x2,x3";
        String reification_query = "MATCH (n:View_12_NODE)-[:View_12_0]->(x0),(n)-[:View_12_1]->(x1),(x0)-[:`http://dummy.com/r`]->(x1),(x0:`http://dummy.com/blue`),(x1),(x1)-[:`http://dummy.com/r`]->(x2),(x1:`http://dummy.com/blue`),(x2),(x2)-[:`http://dummy.com/r`]->(x3) RETURN x0,x1,x2,x3";
        String subgraph = "MATCH (x0:View_12),(x0)-[:`http://dummy.com/r`]->(x1),(x0:`http://dummy.com/blue`),(x1:View_12),(x1)-[:`http://dummy.com/r`]->(x2),(x1:`http://dummy.com/blue`),(x2:View_12),(x2)-[:`http://dummy.com/r`]->(x3:View_12) RETURN x0,x1,x2,x3";

        System.out.println(neo4jConnector.getBasicStats(edge_query));
        System.out.println(neo4jConnector.getBasicStats(node_query));
        System.out.println(neo4jConnector.getBasicStats(reification_query));
        System.out.println(viewConnector.getBasicStats(subgraph));
    }
}
