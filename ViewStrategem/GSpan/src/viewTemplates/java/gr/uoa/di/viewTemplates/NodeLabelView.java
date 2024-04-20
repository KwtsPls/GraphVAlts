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
import gr.uoa.di.interfaceAdapters.Resources;
import gr.uoa.di.translators.Query;
import gr.uoa.di.translators.Translator;
import gr.uoa.di.translators.cypher.CypherQuery;
import org.apache.jena.atlas.lib.Pair;

import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class NodeLabelView extends View{

    String node;
    String edge;
    double benefit;
    double cost;

    public NodeLabelView(Translator translator, Connector connector, PatternVS queryPattern, int viewName){
        super(translator,connector,queryPattern,viewName);
        node=null;
        edge=null;
        benefit=-1.0;
        cost=-1.0;
    }

    public NodeLabelView(Translator translator, Connector connector, PatternVS queryPattern, int viewName, String edge){
        super(translator,connector,queryPattern,viewName);
        List<String> triple = CypherQuery.createEdge(edge);
        if(triple.size()!=3) node=null;
        else {
            node = triple.get(1);
            this.edge = triple.get(0);
        }
        benefit=-1.0;
    }

    public NodeLabelView(Translator translator, Connector connector, PatternVS queryPattern, int viewName, String edge, double cost){
        super(translator,connector,queryPattern,viewName);
        List<String> triple = CypherQuery.createEdge(edge);
        if(triple.size()!=3) node=null;
        else {
            node = triple.get(1);
            this.edge = triple.get(0);
        }
        benefit=-1.0;
        this.cost = cost;
    }

    @Override
    public double getBenefit() {
        if(benefit==-1.0) {
            Pair<Double, String> p = connector.nodeLabelBenefit(this.translator.convert(this.queryPattern), this.viewName);
            if(p!=null) {
                this.node = p.getRight();
                this.benefit = p.getLeft();
            }
        }
        return this.benefit;
    }

    @Override
    public PatternVS getQueryPattern() {
        return this.queryPattern;
    }

    @Override
    public Double materialize() {
        if(this.node!=null) {
            Query q = translator.convert(queryPattern);
            Long r = connector.write(q.nodeLabelMaterialization(this.node,this.viewName));
            this.cost = r*View.NODE_COST + 128;
            return this.cost;
        }
        return -1.0;
    }

    public Double materializeThroughFile(String importPath){
        if(this.edge!=null){
            String systemPath = importPath.substring(1).replaceAll("\\\\",File.separator);
            BufferedReader rels = null;
            BufferedWriter view = null;
            try {
                rels = new BufferedReader(new FileReader(systemPath+"rels_View_"+viewName+".csv"));
                view = new BufferedWriter(new FileWriter(systemPath+"node_View_"+viewName+".csv"));
                view.write("id\n");

                //Get the headers
                String line = rels.readLine();
                String headers[] = line.split("\\|",-1);
                String view_edge = this.edge.replaceAll("`","");

                line = rels.readLine();
                while (line != null) {
                    String[] columns = line.split("\\|",-1);
                    if(columns[2].equals(view_edge)){
                        view.write(columns[0]+"\n");
                    }
                    line = rels.readLine();
                }

                rels.close();
                view.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            StringBuilder relsQuery = new StringBuilder();
            relsQuery.append("LOAD CSV WITH HEADERS FROM 'file:///").append("node_View_"+viewName+".csv' AS line\n");
            relsQuery.append("FIELDTERMINATOR '|'\n");
            relsQuery.append("MATCH (n)\n");
            relsQuery.append("WHERE ID(n)=toInteger(line.id)\n");
            relsQuery.append("SET n:View_"+viewName+"\n");
            relsQuery.append("RETURN n\n");
            Long r = connector.write(relsQuery.toString());
            this.cost = r*View.NODE_COST + 128;
            return this.cost;
        }
        return -1.0;
    }

    @Override
    public void delete() {
        connector.delete(translator.convert(queryPattern).nodeLabelDeletion(viewName));
    }

    @Override
    public Query rewrite(PatternVS pattern) {
        //Get the containment mapping for the given pattern
        List<ContainmentMapping<NodeVS, TripleVS, TrieMetadata<View>>> contains= trie.contains(pattern.getGraph());

        //Pattern is contained by the view - do the rewriting
        if(!contains.isEmpty()){
            for (ContainmentMapping<NodeVS, TripleVS, TrieMetadata<View>> containmentMapping : contains) {
                TripleMap tripleMap = TripleMap.create(this.queryPattern,containmentMapping);
                StringBuilder builder = new StringBuilder();
                AtomicReference<String> mappedNode = new AtomicReference<>();

                tripleMap.forEachTripleRewriting((TripleVS key, TripleVS value)->{
                    List<String> k = translator.parse(key);
                    List<String> v = translator.parse(value);

                    if(k.size()==3){
                        if(k.get(0).equals(node))
                            mappedNode.set(v.get(0));
                        else if(k.get(2).equals(node))
                            mappedNode.set(v.get(2));
                    }
                    builder.append(value.toFullString());
                });

                builder.append("(?").append(mappedNode.get()).append(",").append("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
                        .append(",View_").append(viewName).append(")");
                return this.translator.convert(builder.toString());
            }
        }
        if(translator.convert(pattern).isCyclic()){
            String view = "(?"+this.node+","+"http://www.w3.org/1999/02/22-rdf-syntax-ns#type"+",View_"+this.viewName+")";
            return this.translator.convert(view+ pattern.toFullString());
        }

        return null;
    }

    @Override
    public double executeThroughView(PatternVS query) {
        Query rewritten = this.rewrite(query);
        if(rewritten==null){
            return 0;
        }
        Pair<String, Pair<Double, Long>> pair = this.connector.getBasicStats(rewritten.toString());
        System.out.println("NODE_"+viewName+" -> " + pair.getRight().getLeft());
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
