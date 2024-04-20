package gr.uoa.di.viewTemplates;

import gr.uoa.di.databaseConnectors.Connector;
import gr.uoa.di.databaseConnectors.Neo4jConnector;
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
import gr.uoa.di.translators.cypher.CypherTranslator;
import org.apache.jena.atlas.lib.Pair;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class SubgraphView extends View{

    Connector viewConnector;
    String path;
    double benefit;
    double cost;

    public SubgraphView(Translator translator, Connector connector, Connector viewConnector, PatternVS queryPattern, int viewName, String path) {
        super(translator,connector, queryPattern, viewName);
        this.viewConnector = viewConnector;
        this.path = path;
        benefit=-1.0;
        cost=-1.0;
    }

    public SubgraphView(Translator translator, Connector connector, Connector viewConnector, PatternVS queryPattern, int viewName, double cost, String path) {
        super(translator,connector, queryPattern, viewName);
        this.viewConnector = viewConnector;
        this.path = path;
        benefit=-1.0;
        this.cost=cost;
    }

    @Override
    public double getBenefit() {
        return benefit;
    }

    @Override
    public PatternVS getQueryPattern() {
        return queryPattern;
    }

    @Override
    public Double materialize() {
        CypherTranslator cypherTranslator = (CypherTranslator)translator;
        CypherQuery query = cypherTranslator.convert(this.queryPattern);

        //Save the results of the query pattern to a csv file
        String csvQuery;
        if(query.containsPropertyPath()) csvQuery = query.materializeSubgraphForPaths(this.viewName);
        else csvQuery = query.subgraphMaterialization(this.viewName);
        String systemPath = path.substring(1).replaceAll("\\\\",File.separator);
        File f = new File(systemPath+"View_"+viewName+".csv");
        if(!f.exists()) {
            System.out.println(csvQuery);
            connector.write(csvQuery);
        }

        //Open the csv file and create 3 separate csv files
        try {
            //Open the initial csv file
            BufferedReader reader = new BufferedReader(new FileReader(systemPath+"View_"+viewName+".csv"));

            //Open the different file writers
            BufferedWriter uris = new BufferedWriter(new FileWriter(systemPath+"uris_View_"+viewName+".csv"));
            uris.write("id|labels|uri\n");
            BufferedWriter values = new BufferedWriter(new FileWriter(systemPath+"values_View_"+viewName+".csv"));
            values.write("id|labels|value\n");
            BufferedWriter rels = new BufferedWriter(new FileWriter(systemPath+"rels_View_"+viewName+".csv"));
            rels.write("start|end|type\n");

            Long nodes=0L;
            Long relationships=0L;
            Long properties=0L;

            //Get the headers
            String line = reader.readLine();
            String headers[] = line.split("\\|",-1);
            HashSet<String> uniqueRels = new HashSet<>();

            line = reader.readLine();
            while (line != null) {
                String[] columns = line.split("\\|",-1);

                if(columns.length==7) {
                    //Current line is a node
                    if (!columns[0].isEmpty()) {
                        //Uri node
                        if (columns[3].isEmpty()) {
                            String[] labels = columns[1].split(":", -1);
                            StringBuilder labelColumn = new StringBuilder();
                            for (String label : labels) {
                                if (!label.isEmpty()) {
                                    if (label.equals("http"))
                                        labelColumn.append("http:");
                                    else if (label.equals("Resource"))
                                        labelColumn.append("Resource;View_").append(viewName).append(";");
                                    else
                                        labelColumn.append(label).append(";");
                                }
                            }
                            nodes++;
                            properties++;
                            uris.write(columns[0] + "|" + labelColumn.toString().substring(0, labelColumn.length() - 1) + "|" + columns[2] + "\n");
                        }
                        //Value node
                        else {
                            nodes++;
                            properties++;
                            values.write(columns[0] + "|Resource;View_" + viewName + "|" + columns[3] + "\n");
                        }
                    }
                    //Current line is a relationship
                    else {
                        relationships++;
                        if(!uniqueRels.contains(columns[4] + "|" + columns[5] + "|" + columns[6])) {
                            rels.write(columns[4] + "|" + columns[5] + "|" + columns[6] + "\n");
                            uniqueRels.add(columns[4] + "|" + columns[5] + "|" + columns[6]);
                        }
                    }
                }
                else if(columns.length==6){
                    //Current line is a node
                    if (!columns[0].isEmpty()) {
                        //Uri node
                        if (headers[2].equals("uri")) {
                            String[] labels = columns[1].split(":", -1);
                            StringBuilder labelColumn = new StringBuilder();
                            for (String label : labels) {
                                if (!label.isEmpty()) {
                                    if (label.equals("http"))
                                        labelColumn.append("http:");
                                    else if (label.equals("Resource"))
                                        labelColumn.append("Resource;View_").append(viewName).append(";");
                                    else
                                        labelColumn.append(label).append(";");
                                }
                            }
                            nodes++;
                            properties++;
                            uris.write(columns[0] + "|" + labelColumn.toString().substring(0, labelColumn.length() - 1) + "|" + columns[2] + "\n");
                        }
                        //Value node
                        else {
                            nodes++;
                            properties++;
                            values.write(columns[0] + "|Resource;View_" + viewName + "|" + columns[2] + "\n");
                        }
                    }
                    //Current line is a relationship
                    else {
                        relationships++;
                        if(!uniqueRels.contains(columns[3] + "|" + columns[4] + "|" + columns[5])) {
                            rels.write(columns[3] + "|" + columns[4] + "|" + columns[5] + "\n");
                            uniqueRels.add(columns[3] + "|" + columns[4] + "|" + columns[5]);
                        }
                    }
                }

                line = reader.readLine();
            }

            reader.close();
            uris.close();
            values.close();
            rels.close();

            //Insert the data in a different database
            StringBuilder uriQuery = new StringBuilder();
            uriQuery.append("LOAD CSV WITH HEADERS FROM 'file://").append(path+"uris_View_"+viewName+".csv' AS line\n");
            uriQuery.append("FIELDTERMINATOR '|'\n");
            uriQuery.append("CREATE (n {id:line.id,uri:line.uri})\n");
            uriQuery.append("WITH n,line\n");
            uriQuery.append("CALL apoc.create.addLabels(n, split(line.labels,';'))\n");
            uriQuery.append("YIELD node RETURN node, labels(node) AS labels\n");

            StringBuilder valueQuery = new StringBuilder();
            valueQuery.append("LOAD CSV WITH HEADERS FROM 'file://").append(path+"values_View_"+viewName+".csv' AS line\n");
            valueQuery.append("FIELDTERMINATOR '|'\n");
            valueQuery.append("CREATE (n {id:line.id,value:line.value})\n");
            valueQuery.append("WITH n,line\n");
            valueQuery.append("CALL apoc.create.addLabels(n, split(line.labels,';'))\n");
            valueQuery.append("YIELD node RETURN node, labels(node) AS labels\n");

            StringBuilder relsQuery = new StringBuilder();
            relsQuery.append("LOAD CSV WITH HEADERS FROM 'file://").append(path+"rels_View_"+viewName+".csv' AS line\n");
            relsQuery.append("FIELDTERMINATOR '|'\n");
            relsQuery.append("MATCH (n1:View_"+viewName+"{id:line.start}),(n2:View_"+viewName+"{id:line.end})\n");
            relsQuery.append("WITH n1,n2,line\n");
            relsQuery.append("CALL apoc.create.relationship(n1, line.type, {},n2)\n");
            relsQuery.append("YIELD rel RETURN rel\n");

            viewConnector.write("CREATE INDEX unique_view_"+this.viewName+" IF NOT EXISTS\n" +
                    "FOR (n:View_"+this.viewName+") ON (n.id)");
            viewConnector.write(uriQuery.toString());
            viewConnector.write(valueQuery.toString());
            viewConnector.write(relsQuery.toString());

            this.cost = nodes*View.NODE_COST + properties*View.PROPERTY_STORE_COST + relationships*View.RELATIONSHIP_COST;
            return this.cost;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1.0;
    }

    @Override
    public void delete() {
        viewConnector.delete(translator.convert(queryPattern).subgraphDeletion(viewName));
    }

    @Override
    public Query rewrite(PatternVS pattern) {
        //Get the containment mapping for the given pattern
        List<ContainmentMapping<NodeVS, TripleVS, TrieMetadata<View>>> contains= trie.contains(pattern.getGraph());

        //Pattern is contained by the view - do the rewriting
        if(!contains.isEmpty()){
            for (ContainmentMapping<NodeVS, TripleVS, TrieMetadata<View>> containmentMapping : contains) {
                TripleMap tripleMap = TripleMap.create(this.queryPattern,containmentMapping);
                if(tripleMap.isIsomorphic()) {
                    HashSet<String> nodes = new HashSet<>();
                    StringBuilder builder = new StringBuilder();
                    AtomicInteger count = new AtomicInteger();

                    tripleMap.forEachTripleRewriting((TripleVS key, TripleVS value) -> {
                        Pair<String, Integer> pair = translator.parse(value, count.get());

                        String triple = pair.getLeft().substring(1, pair.getLeft().length() - 1);
                        String[] tokens = triple.split(",");

                        count.set(pair.getRight());
                        nodes.add(tokens[0]);
                        nodes.add(tokens[2]);
                        builder.append(value.toFullString());
                    });

                    StringBuilder subgraphBuilder = new StringBuilder();
                    for (String node : nodes) {
                        subgraphBuilder.append("(?").append(node).append(",http://www.w3.org/1999/02/22-rdf-syntax-ns#type,View_").
                                append(viewName).append(")");
                    }

                    return this.translator.convert(subgraphBuilder.toString() + builder.toString());
                }
            }
        }
        if(translator.convert(pattern).isCyclic()){
            LinkedHashSet<String> nodes = translator.convert(pattern).getHeadVars();
            StringBuilder builder = new StringBuilder();
            for(String node:nodes){
                if(!node.contains("labels"))
                    builder.append("(?"+node+","+"http://www.w3.org/1999/02/22-rdf-syntax-ns#type"+",View_"+this.viewName+")");
            }
            return this.translator.convert(builder+ pattern.toFullString());
        }

        return null;
    }

    @Override
    public double executeThroughView(PatternVS query) {
        Query rewritten = this.rewrite(query);
        if(rewritten==null){
            return 0;
        }
        Pair<String, Pair<Double, Long>> pair = this.viewConnector.getBasicStats(translator.convert(this.getQueryPattern()).toString());
        System.out.println("SUBGRAPH_"+viewName+" -> " + pair.getRight().getLeft());
        return pair.getRight().getLeft();
    }

    @Override
    public Double materializeThroughFile(String importPath) {
        return materialize();
    }

    @Override
    public GraphVS getGraph() {
        return queryPattern.getGraph();
    }

    @Override
    public int getRowCount() {
        return 0;
    }

    @Override
    public String getTableName() {
        return "View_"+viewName;
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
