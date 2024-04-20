package gr.uoa.di.databaseConnectors;

import gr.uoa.di.entities.graph.FactoryVS;
import gr.uoa.di.entities.graph.TripleVS;
import gr.uoa.di.translators.Query;
import gr.uoa.di.translators.cypher.CypherQuery;
import gr.uoa.di.viewTemplates.View;
import org.apache.jena.atlas.lib.Pair;
import org.apache.jena.base.Sys;
import org.neo4j.driver.*;
import org.neo4j.driver.exceptions.ClientException;
import org.neo4j.driver.summary.Plan;
import org.neo4j.driver.summary.ResultSummary;

import org.neo4j.driver.Record;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Neo4jConnector implements AutoCloseable,Connector {

    private final Driver driver;

    public Neo4jConnector( String uri, String user, String password )
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    /********************* GENERIC METHODS ********************************/
    @Override
    public Long writeUsingPeriodicCommits(String query){
        try ( Session session = driver.session() ){
            String commit = "CALL apoc.periodic.commit($statement, $params)";
            Map<String, Object> params = new HashMap<>();
            params.put("statement", query);
            params.put("params", Map.of("limit", 10000));
            Result r = session.run(commit, params);
            System.out.println(r);
            Record record = r.single();
            System.out.println(record);
            return record.get("updates").asLong();
        }
    }


    //Method to get the initial edge to create the correct index
    @Override
    public Long write(String query){
        try ( Session session = driver.session() )
        {
            Long s = session.writeTransaction( tx ->
            {
                Result result = tx.run(query);
                return result.stream().count();
            });
            session.close();
            return s;
        }
    }

    @Override
    public Long read(String query){
        try ( Session session = driver.session() )
        {
            Long s = session.readTransaction( tx ->
            {
                Result result = tx.run(query);
                return result.stream().count();
            });
            session.close();
            return s;
        }
    }

    //Method to calculate the execution cost of a given query based on the first action of the planner
    @Override
    public String delete(String query){
        try ( Session session = driver.session() )
        {
            String res = session.writeTransaction( tx ->
            {
                ResultSummary result = tx.run(query).consume();
                return result.toString();
            });

            return res;
        }
        catch (ClientException ce){
            System.out.println("Bad input");
            return "Bad input";
        }
    }


    /********************* EDGE LABEL VIEW METHODS ********************************/

    //Method to calculate the benefit of creating an edge label view
    @Override
    public Pair<Double, List<String>> edgeLabelBenefit(Query q, int viewName){

        //Get the pattern which will be replaced by the edge label view
        CypherQuery query = (CypherQuery)q;
        List<String> triple = CypherQuery.createEdge(getIndexEdge(query.toString()));
        if(triple.size()!=3) return null;
        String rel = triple.get(0);

        //Get a constructed query to find the estimated benefit of the view
        String estimationQuery = query.edgeLabelEstimationQuery(rel,viewName);
        if(estimationQuery==null) return null;

        double view_rows = costEdgeLabelView(estimationQuery);
        double edge_rows = costEdge(query.toString());

        double benefit;
        if(view_rows!=0.0)
            benefit = (edge_rows - view_rows)/(view_rows* View.RELATIONSHIP_COST);
        else
            benefit = 0.0;

        return new Pair<>(benefit,triple);
    }

    //Method to calculate the execution cost of a given query
    public double costEdgeLabelView(String query){
        try ( Session session = driver.session() )
        {
            Double cost = session.readTransaction( tx ->
            {
                ResultSummary result = tx.run("EXPLAIN " + query).consume();
                return edgeLabelEstimation(result.plan());
            });

            return cost;
        }
    }

    //Method to calculate the execution cost of a given query based on the first action of the planner
    public double costEdge(String query){
        try ( Session session = driver.session() )
        {
            Double cost = session.readTransaction( tx ->
            {
                ResultSummary result = tx.run("EXPLAIN " + query).consume();
                return initEdgeEstimation(result.plan());
            });

            return cost;
        }
    }

    //Method to calculate the total estimated rows from the given plan of the query recursively
    double initEdgeEstimation(Plan plan){
        double result = plan.arguments().get("EstimatedRows").asDouble();
        List<? extends Plan> l = plan.children();
        if (!l.isEmpty()) {
            for (Plan p : l) {
                return initEdgeEstimation(p);
            }
        }
        return result;
    }

    //Method to get the first part of the query that the planner calculated
    String getInitialEdge(Plan plan){
        Value result = plan.arguments().get("Details");
        List<? extends Plan> l = plan.children();
        if (!l.isEmpty()) {
            for (Plan p : l) {
                return getInitialEdge(p);
            }
        }
        return (result==null)?null:result.asString();
    }

    //Method to get the initial edge to create the correct index
    public String getIndexEdge(String query){
        try ( Session session = driver.session() )
        {
            String s = session.readTransaction( tx ->
            {
                ResultSummary result = tx.run("EXPLAIN " + query).consume();
                return getInitialEdge(result.plan());
            });

            return s;
        }
    }

    //Method to calculate the total estimated rows from the given plan of the query recursively
    double edgeLabelEstimation(Plan plan){
        double result = 0;
        List<? extends Plan> l = plan.children();
        if (!l.isEmpty()) {
            for (Plan p : l) {
                if(Objects.equals(p.operatorType(), "DirectedRelationshipTypeScan@neo4j") &&
                        p.arguments().get("Details").asString().contains("View"))
                    result += p.arguments().get("EstimatedRows").asDouble() + edgeLabelEstimation(p);
                else
                    result += edgeLabelEstimation(p);
            }
        }
        return result;
    }

    //Method to get the var length path edge
    //Method to get the first part of the query that the planner calculated
    String getVarLengthPathUtil(Plan plan){
        if(plan.operatorType().equals("VarLengthExpand(All)@neo4j"))
            return plan.arguments().get("Details").toString();
        Value result = plan.arguments().get("Details");
        List<? extends Plan> l = plan.children();
        if (!l.isEmpty()) {
            for (Plan p : l) {
                if(p.operatorType().equals("VarLengthExpand(All)@neo4j"))
                    return p.arguments().get("Details").toString();
                return getVarLengthPathUtil(p);
            }
        }
        return null;
    }

    //Method to get the initial edge to create the correct index
    public String getVarLengthPath(String query){
        try ( Session session = driver.session() )
        {
            String s = session.readTransaction( tx ->
            {
                ResultSummary result = tx.run("EXPLAIN " + query).consume();
                return getVarLengthPathUtil(result.plan());
            });

            return s;
        }
    }

    //Method to get the estimated result size of the query
    public Double estimatedResultSize(String query){
        try ( Session session = driver.session() )
        {
            Double records = session.readTransaction( tx ->
            {
                ResultSummary result = tx.run("EXPLAIN " + query).consume();
                return result.plan().arguments().get("EstimatedRows").asDouble();
            });

            return records;
        }
    }


    /********************* NODE LABEL VIEW METHODS ********************************/

    //Method to calculate the benefit of creating an edge label view
    @Override
    public Pair<Double,String> nodeLabelBenefit(Query q, int viewName){

        //Get the pattern which will be replaced by the edge label view
        CypherQuery query = (CypherQuery) q;
        List<String> triple = CypherQuery.createEdge(getIndexEdge(query.toString()));
        if(triple.size()!=3) return null;
        String node = triple.get(1);

        //Get a constructed query to find the estimated benefit of the view
        double view_rows = costNodeLabelView(query.toString());
        double edge_rows = costEdge(query.toString());

        double benefit;
        if(view_rows!=0.0)
            benefit = (edge_rows - view_rows)/(view_rows*View.LABEL_COST + 128);
        else
            benefit = 0.0;

        return new Pair<>(benefit,node);
    }

    //Method to get the estimated result of a query
    public double costNodeLabelView(String query){
        try ( Session session = driver.session() )
        {
            double cost = session.readTransaction( tx ->
            {
                ResultSummary result = tx.run("EXPLAIN " + query).consume();
                return result.plan().arguments().get("EstimatedRows").asDouble();
            });

            return cost;
        }
    }

    /********************* REIFICATION VIEW METHODS ********************************/



    /********************* PROFILING QUERIES ***************************************/
    //Method to get the first part of the query that the planner calculated
    String getInitialEdgeUsingProfile(Plan plan){
        Value result = plan.arguments().get("Details");
        List<? extends Plan> l = plan.children();
        if (!l.isEmpty()) {
            for (Plan p : l) {
                return getInitialEdgeUsingProfile(p);
            }
        }
        return (result==null)?null:result.asString();
    }

    //Method to get the initial edge to create the correct index
    public String getIndexEdgeUsingProfile(String query){
        try ( Session session = driver.session() )
        {
            String s = session.readTransaction( tx ->
            {
                ResultSummary result = tx.run("PROFILE " + query).consume();
                return getInitialEdgeUsingProfile(result.plan());
            });

            return s;
        }
    }

    //Method to calculate the total estimated rows from the given plan of the query recursively
    double initEdgeEstimationUsingProfile(Plan plan){
        double result = 0;
        if(plan.arguments().get("Rows")!=null)
            result = plan.arguments().get("Rows").asDouble();
        List<? extends Plan> l = plan.children();
        if (!l.isEmpty()) {
            for (Plan p : l) {
                return result+initEdgeEstimation(p);
            }
        }
        return result;
    }

    public Pair<String, Pair<Double, Long>> getBasicStats(String query){
        try ( Session session = driver.session() )
        {
            Pair<String, Pair<Double, Long>> res =  session.readTransaction( tx ->
            {
                ResultSummary result = tx.run("PROFILE " + query).consume();
                String edge = getInitialEdgeUsingProfile(result.plan());
                Double cost = initEdgeEstimationUsingProfile(result.plan());
                if(result.profile()!=null) {
                    return new Pair<>(edge, new Pair<>(cost, result.profile().records()));
                }
                else {
                    Result r = tx.run("PROFILE " + query);
                    Long records = r.stream().count();
                    ResultSummary summary = r.consume();
                    edge = getInitialEdgeUsingProfile(summary.plan());
                    cost = initEdgeEstimationUsingProfile(summary.plan());
                    return new Pair<>(edge, new Pair<>(cost, records));
                }
            });
            session.close();
            return res;
        }
    }

    public double resultSize(Plan plan){
        double result = 0;
        List<? extends Plan> l = plan.children();
        if (!l.isEmpty()) {
            for (Plan p : l) {
                if(Objects.equals(p.operatorType(), "ProduceResults@neo4j"))
                    result = p.arguments().get("Rows").asDouble();
                else
                    result = 0;
            }
        }
        return result;
    }

    @Override
    public void close() throws Exception
    {
        driver.close();
    }

}
