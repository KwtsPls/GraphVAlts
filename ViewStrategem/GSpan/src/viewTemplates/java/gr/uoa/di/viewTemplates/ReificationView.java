package gr.uoa.di.viewTemplates;

import gr.uoa.di.databaseConnectors.Connector;
import gr.uoa.di.entities.graph.GraphVS;
import gr.uoa.di.entities.graph.NodeVS;
import gr.uoa.di.entities.graph.PatternVS;
import gr.uoa.di.entities.graph.TripleVS;
import gr.uoa.di.entities.helpStructures.tuples.IntTriple;
import gr.uoa.di.entities.trie.TrieMetadata;
import gr.uoa.di.entities.trie.containment.mapping.ContainmentMapping;
import gr.uoa.di.entities.viewSelection.edgeRewriting.TripleMap;
import gr.uoa.di.entities.viewSelection.queryRewriting.ViewForRewriting;
import gr.uoa.di.interfaceAdapters.Resources;
import gr.uoa.di.translators.Query;
import gr.uoa.di.translators.Translator;
import gr.uoa.di.translators.cypher.CypherQuery;
import org.apache.jena.atlas.lib.Pair;
import org.neo4j.cypher.internal.expressions.In;
import scala.Int;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ReificationView extends View{

    List<String> edges;
    String path;
    double benefit;
    double cost;

    public ReificationView(Translator translator, Connector connector, PatternVS queryPattern, int viewName) {
        super(translator, connector, queryPattern, viewName);
        benefit=-1.0;
        this.cost = -1.0;
        this.edges = new ArrayList<>();
    }

    public ReificationView(Translator translator, Connector connector, PatternVS queryPattern, int viewName, double cost) {
        super(translator, connector, queryPattern, viewName);
        benefit=-1.0;
        this.cost = -1.0;
        this.edges = new ArrayList<>();
        this.cost = cost;
    }

    @Override
    public double getBenefit() {
        return 0;
    }

    @Override
    public PatternVS getQueryPattern() {
        return null;
    }

    @Override
    public Double materialize() {
        Query q = translator.convert(queryPattern);
        String query = q.reificationMaterialization(this.viewName);
        Long r = connector.write(query);
        this.cost = View.NODE_COST + r*View.RELATIONSHIP_COST;
        return this.cost;
    }

    public Double materializeThroughFile(String importPath){
        if(this.edges!=null){
            String systemPath = importPath.substring(1).replaceAll("\\\\",File.separator);
            CypherQuery q = (CypherQuery) translator.convert(this.queryPattern);
            HashMap<String,List<Pair<Integer, Integer>>> maps = q.getReificationMappings();

            BufferedReader rels = null;
            HashMap<Integer,BufferedWriter> writers = new HashMap<>();
            HashMap<Integer,HashSet<String>> items = new HashMap<>();
            try {
                rels = new BufferedReader(new FileReader(systemPath+"rels_View_"+viewName+".csv"));

                //Get the headers
                String line = rels.readLine();
                String headers[] = line.split("\\|",-1);

                line = rels.readLine();
                while (line != null) {
                    String[] columns = line.split("\\|",-1);

                    List<Pair<Integer,Integer>> pairs = maps.get(columns[2]);
                    if(pairs!=null) {
                        for(Pair<Integer, Integer> pair:pairs) {
                            if (writers.get(pair.getLeft()) == null) {
                                writers.put(pair.getLeft(), new BufferedWriter(new FileWriter(systemPath + "reif_View_" + viewName + "_" + pair.getLeft() + ".csv")));
                                writers.get(pair.getLeft()).write("id\n");

                                items.put(pair.getLeft(), new HashSet<>());
                            }
                            if (writers.get(pair.getRight()) == null) {
                                writers.put(pair.getRight(), new BufferedWriter(new FileWriter(systemPath + "reif_View_" + viewName + "_" + pair.getRight() + ".csv")));
                                writers.get(pair.getRight()).write("id\n");

                                items.put(pair.getRight(), new HashSet<>());
                            }

                            if (!items.get(pair.getLeft()).contains(columns[0])) {
                                writers.get(pair.getLeft()).write(columns[0] + "\n");
                                items.get(pair.getLeft()).add(columns[0]);
                            }
                            if (!items.get(pair.getRight()).contains(columns[1])) {
                                writers.get(pair.getRight()).write(columns[1] + "\n");
                                items.get(pair.getRight()).add(columns[1]);
                            }
                        }
                    }

                    line = rels.readLine();
                }

                rels.close();
                for (Map.Entry<Integer,BufferedWriter> entry : writers.entrySet())
                    entry.getValue().close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            this.cost=NODE_COST+128;
            connector.write("CREATE (n:View_"+viewName+"_NODE) RETURN n");

            for(int i=0;i<q.varCount();i++) {
                System.out.println(i);
                File f = new File(systemPath + "reif_View_" + viewName + "_" + i + ".csv");
                if(f.exists()) {
                    StringBuilder relsQuery = new StringBuilder();
                    relsQuery.append("LOAD CSV WITH HEADERS FROM 'file:///").append("reif_View_" + viewName + "_" + i + ".csv' AS line\n");
                    relsQuery.append("FIELDTERMINATOR '|'\n");
                    relsQuery.append("MATCH (n1:View_" + viewName + "_NODE),(n2)\n");
                    relsQuery.append("WHERE ID(n2)=toInteger(line.id)\n");
                    relsQuery.append("WITH n1,n2,line\n");
                    relsQuery.append("CALL apoc.create.relationship(n1, \"View_" + viewName + "_" + i + "\", {},n2)\n");
                    relsQuery.append("YIELD rel RETURN rel\n");
                    Long r = connector.write(relsQuery.toString());
                    this.cost += r * View.RELATIONSHIP_COST;
                }
            }

            return this.cost;
        }
        return -1.0;
    }

    @Override
    public void delete() {
        connector.delete(translator.convert(queryPattern).reificationDeletion(viewName));
    }

    @Override
    public Query rewrite(PatternVS pattern) {
        //Get the containment mapping for the given pattern
        List<ContainmentMapping<NodeVS, TripleVS, TrieMetadata<View>>> contains= trie.contains(pattern.getGraph());

        //Pattern is contained by the view - do the rewriting
        if(!contains.isEmpty()){
            for (ContainmentMapping<NodeVS, TripleVS, TrieMetadata<View>> containmentMapping : contains) {
                Query query = this.translator.convert(this.queryPattern);
                HashMap<String,List<Pair<Integer, Integer>>> maps = query.getReificationMappings();
                TripleMap tripleMap = TripleMap.create(this.queryPattern,containmentMapping);
                HashMap<String,Integer> nodes = new HashMap<>();
                StringBuilder builder = new StringBuilder();
                AtomicInteger count = new AtomicInteger();

                tripleMap.forEachTripleRewriting((TripleVS key, TripleVS value)->{
                    Pair<String,Integer> pair = translator.parse(value, count.get());

                    String triple = pair.getLeft().substring(1, pair.getLeft().length() - 1);
                    String[] tokens = triple.split(",");

                    count.set(pair.getRight());
                    if(!tokens[1].replaceAll("`","").equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
                        List<Pair<Integer, Integer>> pairs = maps.get(tokens[1].replaceAll("`", ""));
                        if(!pairs.isEmpty()) {
                            Pair<Integer, Integer> integerPair = pairs.get(0);
                            pairs.remove(0);
                            nodes.put(tokens[0], integerPair.getLeft());
                            nodes.put(tokens[2], integerPair.getRight());
                            maps.put(tokens[1].replaceAll("`", ""),pairs);
                        }
                        builder.append(value.toFullString());
                    }
                });

                StringBuilder reificationBuilder = new StringBuilder("(?n,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,View_"+viewName+"_NODE)");
                for (Map.Entry<String,Integer> entry : nodes.entrySet()){
                    Long result = connector.read("MATCH (n:View_"+viewName+"_NODE)-[r:View_"+viewName+"_"+entry.getValue()+"]->() RETURN r LIMIT 1");
                    if(result!=0) {
                        System.out.println(entry.getKey() + " " + entry.getValue());
                        reificationBuilder.append("(?n,View_").append(viewName).append("_" + entry.getValue()).append(",?").append(entry.getKey()).append(")");
                    }
                }


                return this.translator.convert(reificationBuilder.toString() + builder.toString());
            }
        }
        if(translator.convert(pattern).isCyclic()){
            LinkedHashSet<String> nodes = translator.convert(pattern).getHeadVars();
            int count=0;
            StringBuilder builder = new StringBuilder("(?n,http://www.w3.org/1999/02/22-rdf-syntax-ns#type,View_"+viewName+"_NODE)");
            for(String node:nodes){
                if(!node.contains("labels")) {
                        builder.append("(?n," + "View_"+this.viewName+"_"+count+",?"+node+")");
                    count++;
                }
            }
            return this.translator.convert(builder+pattern.toFullString());
        }

        return null;
    }

    @Override
    public double executeThroughView(PatternVS query) {
        Query rewritten = this.rewrite(query);
        if(rewritten==null){
            return 0;
        }
        System.out.println(rewritten.toString());
        Pair<String, Pair<Double, Long>> pair = this.connector.getBasicStats(rewritten.toString());
        System.out.println("REIFICATION_"+viewName+" -> " + pair.getRight().getLeft());
        return pair.getRight().getLeft();
    }

    @Override
    public GraphVS getGraph() {
        return null;
    }

    @Override
    public int getRowCount() {
        return 0;
    }

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public Set<IntTriple> getViewTriples() {
        return null;
    }

    @Override
    public int getVarCount() {
        return 0;
    }
}
