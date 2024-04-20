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

import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ShortcutView extends View{

    List<String> path;
    double benefit;
    double cost;

    public ShortcutView(Translator translator, Connector connector, PatternVS queryPattern, int viewName) {
        super(translator, connector, queryPattern, viewName);
        this.path=null;
        this.benefit=-1.0;
        this.cost=-1.0;
    }

    public ShortcutView(Translator translator, Connector connector, PatternVS queryPattern, int viewName, String edge){
        super(translator,connector,queryPattern,viewName);
        List<String> triple = CypherQuery.createEdge(edge);
        if(triple.size()!=3) this.path=null;
        else this.path=triple;
        this.benefit=-1.0;
    }

    public ShortcutView(Translator translator, Connector connector, PatternVS queryPattern, int viewName, String edge, double cost){
        super(translator,connector,queryPattern,viewName);
        List<String> triple = CypherQuery.createEdge(edge);
        if(triple.size()!=3) this.path=null;
        else this.path=triple;
        this.benefit=-1.0;
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
        if(this.path!=null) {
            Query q = translator.convert(queryPattern);
            System.out.println(q.edgeLabelMaterialization(this.path, this.viewName));
            Long r = connector.write(q.edgeLabelMaterialization(this.path, this.viewName));
            this.cost = r*View.RELATIONSHIP_COST;
            return this.cost;
        }
        return -1.0;
    }


    public Double materializeThroughFile(String importPath){
        if(this.path!=null){
            String systemPath = importPath.substring(1).replaceAll("\\\\",File.separator);
            BufferedReader rels = null;
            BufferedWriter view = null;
            try {
                rels = new BufferedReader(new FileReader(systemPath+"rels_View_"+viewName+".csv"));
                view = new BufferedWriter(new FileWriter(systemPath+"path_View_"+viewName+".csv"));
                view.write("start|end\n");

                //Get the headers
                String line = rels.readLine();
                String headers[] = line.split("\\|",-1);
                String view_edge = this.path.get(0).replaceAll("`","");

                line = rels.readLine();
                while (line != null) {
                    String[] columns = line.split("\\|",-1);
                    if(columns[2].equals(view_edge)){
                        view.write(columns[0]+"|"+columns[1]+"\n");
                    }
                    line = rels.readLine();
                }

                rels.close();
                view.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            StringBuilder relsQuery = new StringBuilder();
            relsQuery.append("LOAD CSV WITH HEADERS FROM 'file:///").append("path_View_"+viewName+".csv' AS line\n");
            relsQuery.append("FIELDTERMINATOR '|'\n");
            relsQuery.append("MATCH (n1),(n2)\n");
            relsQuery.append("WHERE ID(n1)=toInteger(line.start) AND ID(n2)=toInteger(line.end)\n");
            relsQuery.append("WITH n1,n2,line\n");
            relsQuery.append("CALL apoc.create.relationship(n1, \"View_"+viewName+"\", {},n2)\n");
            relsQuery.append("YIELD rel RETURN rel\n");
            Long r = connector.write(relsQuery.toString());
            this.cost = r*View.RELATIONSHIP_COST;
            return this.cost;
        }
        return -1.0;
    }

    @Override
    public void delete() {
        connector.delete(translator.convert(queryPattern).shortcutDeletion(viewName));
    }

    @Override
    public Query rewrite(PatternVS pattern) {
        //Get the containment mapping for the given pattern
        List<ContainmentMapping<NodeVS, TripleVS, TrieMetadata<View>>> contains= trie.contains(pattern.getGraph());

        //Pattern is contained by the view - do the rewriting
        if(!contains.isEmpty()){
            for (ContainmentMapping<NodeVS, TripleVS, TrieMetadata<View>> containmentMapping : contains) {
                TripleMap tripleMap = TripleMap.create(this.queryPattern,containmentMapping);
                String replacedEdgeString = "(" + this.path.get(1) + "," + this.path.get(0) + "," + this.path.get(2) + ")";
                String viewEdgeString = "(?" + this.path.get(1) + ",View_" + this.viewName + ",?" + this.path.get(2) + ")";
                StringBuilder builder = new StringBuilder();
                AtomicInteger count= new AtomicInteger();

                tripleMap.forEachTripleRewriting((TripleVS key, TripleVS value)->{
                    Pair<String,Integer> pair = translator.parse(key, count.get());
                    count.set(pair.getRight());
                    if(pair.getLeft().equals(replacedEdgeString))
                        builder.append(viewEdgeString);
                    else
                        builder.append(value.toFullString());
                });
                return this.translator.convert(builder.toString());
            }
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
        System.out.println("SHORTCUT_"+viewName+" -> " + pair.getRight().getLeft());
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
