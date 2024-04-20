package gr.uoa.di.databaseConnectors;

import gr.uoa.di.entities.graph.TripleVS;
import gr.uoa.di.translators.Query;
import gr.uoa.di.translators.cypher.CypherQuery;
import org.apache.jena.atlas.lib.Pair;

import java.util.List;

public interface Connector{
    public Long write(String query);
    public Long read(String query);
    public Long writeUsingPeriodicCommits(String query);
    public String delete(String query);
    public Pair<Double, List<String>> edgeLabelBenefit(Query query, int viewName);
    public Pair<Double,String> nodeLabelBenefit(Query query, int viewName);
    public Pair<String, Pair<Double, Long>> getBasicStats(String query);
}
