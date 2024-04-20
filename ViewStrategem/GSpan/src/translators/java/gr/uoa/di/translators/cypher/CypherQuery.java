package gr.uoa.di.translators.cypher;

import gr.uoa.di.translators.Query;
import org.apache.jena.atlas.lib.Pair;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CypherQuery implements Query {

    List<String> cypherPatterns;
    LinkedHashSet<String> headVars;
    LinkedHashSet<String> whereClauseComponents;
    HashMap<String,String> constantNodeMap;
    HashMap<String,List<String>> edgesMap;
    List<String> propertyPathVarList;
    boolean containsPropertyPath;
    int isCyclic;
    int relCounter;
    int varCounter;

    public CypherQuery(){
        cypherPatterns = new LinkedList<>();
        headVars = new LinkedHashSet<>();
        whereClauseComponents = new LinkedHashSet<>();
        constantNodeMap = new HashMap<>();
        edgesMap = new HashMap<>();
        containsPropertyPath = false;
        propertyPathVarList = new LinkedList<>();
        isCyclic = -1;
        varCounter=-1;
        relCounter=-1;
    }

    public void addTriple(Pair<String,String> subject, Pair<String,String> predicate, Pair<String,String> object){
        String strSubject = subject.getLeft();
        String subjectType = subject.getRight();
        String strPredicate = predicate.getLeft();
        String predicateType = predicate.getRight();
        String strObject = object.getLeft();
        String objectType = object.getRight();

        //Subject of triple (s,p,o) is a variable
        if(Objects.equals(subjectType,"VAR")){
            if (!cypherPatterns.contains(makeNode(strSubject)))
                cypherPatterns.add(makeNode(strSubject));
            headVars.add(strSubject);

            //Predicate is a relationship create an edge
            if(Objects.equals(predicateType,"RELATIONSHIP") || Objects.equals(predicateType,"PATH")){
                relCounter++;
                if(Objects.equals(predicateType,"PATH")) {
                    containsPropertyPath = true;
                    propertyPathVarList.add("r"+relCounter);
                }

                if(Objects.equals(objectType,"URI") || Objects.equals(objectType,"VALUE")){
                    varCounter++;
                    cypherPatterns.add(makeEdge(strSubject, "c" + varCounter, strObject,strPredicate,objectType.toLowerCase()));
                    //Add the second node to the return clause
                    headVars.add("c" + varCounter);
                }
                //Object is a var, create a simple edge
                else{
                    cypherPatterns.add(makeEdge(strSubject, strObject, strPredicate));
                    headVars.add(strObject);
                }
            }
            //Predicate is type - ask for subject's labels
            else{
                if(Objects.equals(objectType,"VAR"))
                    headVars.add(makeLabel(strSubject));
                else
                    cypherPatterns.add(makeLabel(strSubject,strObject));
            }

        }
        //Subject is a uri
        else{
            varCounter++;
            String uriVar = "c" + varCounter;
            headVars.add(uriVar);

            //Predicate is a relationship create an edge
            if(Objects.equals(predicateType,"RELATIONSHIP") || Objects.equals(predicateType,"PATH")){
                relCounter++;
                if(Objects.equals(predicateType,"PATH")) {
                    containsPropertyPath = true;
                    propertyPathVarList.add("r"+relCounter);
                }

                if(Objects.equals(objectType,"URI") || Objects.equals(objectType,"VALUE")){
                    varCounter++;
                    cypherPatterns.add(makeEdgeURI(uriVar,strSubject, "c" + varCounter, strObject,strPredicate,objectType.toLowerCase()));
                    //Add the second node to the return clause
                    headVars.add("c" + varCounter);
                }
                //Object is a var, create a simple edge
                else{
                    cypherPatterns.add(makeEdgeURI(uriVar,strObject,strSubject,strPredicate));
                    headVars.add(strObject);
                }
            }
            //Predicate is type - ask for subject's labels
            else{
                headVars.add(makeLabel(strSubject));
            }

        }

    }


    //Helper method to add an edge between a var node and a uri node
    String makeEdgeURI(String uriVar,String var,String uri,String edge){
        constantNodeMap.put(uri,uriVar);
        addEdge(uriVar,var);
        return "("+uriVar+":Resource {uri:"+uri+"})"+"-[:"+edge+"]->"+ "("+var+")";
    }

    //Helper method to add an edge between a var node and a uri/value node
    String makeEdgeURI(String uriVar1,String uri,String uriVar2,String value,String edge,String type){
        constantNodeMap.put(uri,uriVar1);
        constantNodeMap.put(value,uriVar2);
        addEdge(uriVar1,uriVar2);
        return "("+uriVar1+":Resource {uri:"+uri+"})"+"-[:"+edge+"]->"+"("+uriVar2+":Resource {"+type+":"+value+"})";
    }

    //Helper method to add an edge between a var node and a uri/value node
    String makeEdge(String var,String uriVar,String value,String edge,String type){
        constantNodeMap.put(value,uriVar);
        addEdge(var,uriVar);
        return "("+var+")"+"-[:"+edge+"]->"+"("+uriVar+":Resource {"+type+":"+value+"})";
    }

    //Helper method to add an edge between two var nodes
    String makeEdge(String var1,String var2,String edge){
        addEdge(var1,var2);
        return "("+var1+")"+"-[:"+edge+"]->"+"("+var2+")";
    }

    //Helper method to add a node to MATCH clause
    String makeNode(String var){
        return "("+var+")";
    }

    //Helper method to add a node with a uri to MATCH clause
    String makeNode(String var,String uri){
        return "("+var+" {uri:"+uri+"})";
    }

    //Helper method to create a label query
    String makeLabel(String var){
        return "labels("+var+")";
    }

    //Helper method to create a specific label query
    String makeLabel(String var,String label) {
        if(!label.startsWith("\"View_"))
            return "("+var+":"+label.replaceAll("\"","`")+")";
        else{
            return "("+var+":"+label.replaceAll("\"","")+")";
        }
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("MATCH ");
        output.append(String.join(",", cypherPatterns));
        if(!whereClauseComponents.isEmpty()){
            output.append(" WHERE ");
            output.append(String.join(" AND \n",whereClauseComponents));
        }
        output.append(" RETURN ");
        output.append(String.join(",", headVars));
        return output.toString();
    }

    //Method to break an edge pattern into a list of arguments
    public static List<String> createEdge(String e){
        List<String> triple =  new ArrayList<String>();

        //Get the name of the relationship
        Matcher m1 = Pattern.compile("\\[(.*?)\\]").matcher(e);
        String relName=null;
        while (m1.find()) {
            relName = m1.group(1);
        }
        if(relName!=null) {
            String[] arrRelName = relName.split(":", 2);
            triple.add(arrRelName[1]);
        }

        //Get the two nodes of the pattern
        Matcher m = Pattern.compile("\\((.*?)\\)").matcher(e);
        while (m.find()) {
            triple.add(m.group(1));
        }

        return triple;
    }

    /**************** QUERY STATISTICS METHODS **************/
    private void addEdge(String a,String b){
        if(edgesMap.get(a)==null){
            List<String> list = new ArrayList<>();
            list.add(b);
            edgesMap.put(a,list);
        }
        else{
            List<String> list = edgesMap.get(a);
            if(!list.contains(b)) list.add(b);
            edgesMap.put(a,list);
        }

        edgesMap.computeIfAbsent(b, k -> new ArrayList<>());
    }

    @Override
    public boolean containsPropertyPath(){
        return containsPropertyPath;
    }

    @Override
    public LinkedHashSet<String> getHeadVars() {
        return this.headVars;
    }

    @Override
    public boolean isCyclic(){
        if(this.isCyclic==0) return false;
        else if(this.isCyclic==1) return true;

        int V = edgesMap.keySet().stream().toList().size();

        // Mark all the vertices as not visited and not part of recursion stack
        boolean[] visited = new boolean[V];
        boolean[] recStack = new boolean[V];

        // Call the recursive helper function to
        // detect cycle in different DFS trees
        int i=0;
        for (String v:edgesMap.keySet().stream().toList()) {
            if (dfsCycle(i , v, visited, recStack)) {
                this.isCyclic = 1;
                return true;
            }
            i++;
        }

        this.isCyclic=0;
        return false;
    }

    public boolean dfsCycle(int i, String v, boolean[] visited, boolean[] recStack){
        // Mark the current node as visited and
        // part of recursion stack
        if (recStack[i])
            return true;

        if (visited[i])
            return false;

        visited[i] = true;

        recStack[i] = true;
        List<String> children = edgesMap.get(v);

        for (String c: children) {
            if (dfsCycle(edgesMap.keySet().stream().toList().indexOf(c),c,visited,recStack))
                return true;
        }

        recStack[i] = false;

        return false;
    }

    public String getPropertyPath(){
        for(String pattern:this.cypherPatterns){
            if(pattern.contains("*"))
                return pattern;
        }
        return null;
    }

    public void printEdges(){
        for (Map.Entry<String, List<String>> set : edgesMap.entrySet()) {
            System.out.print(set.getKey()+"->[");
            for(String c:set.getValue())
                System.out.print(c+",");
            System.out.println("]");
        }
    }

    /*------------ VIEW METHODS ----------------------------*/
    public String edgeLabelEstimationQuery(String edge,int viewName){
        List<String> triple = createEdge(edge);
        if(triple.size()!=3) return null;

        String replacedEdge = "(" + triple.get(1) + ")-[:"+triple.get(0)+"]->("+triple.get(2)+")";
        String viewEdge = "(" + triple.get(1) + ")-[r:View_"+viewName+"]->("+triple.get(2)+")";
        String anonViewEdge = "(" + triple.get(1) + ")-[:View_"+viewName+"]->("+triple.get(2)+")";

        StringBuilder output = new StringBuilder("MATCH ");
        output.append(String.join(",", cypherPatterns));
        if(!whereClauseComponents.isEmpty()){
            output.append(" WHERE ");
            output.append(String.join(" AND \n",whereClauseComponents));
        }
        output.append(" MERGE " + viewEdge + "\n");
        output.append("WITH r\n");
        output.append("MATCH ");
        output.append(String.join(",", cypherPatterns).replace(replacedEdge,anonViewEdge));
        output.append(" RETURN r");
        return output.toString();
    }

    /*------------------------------------------------------------------------------------*/

    /************************* VIEW MATERIALIZATION QUERIES *******************************/
    @Override
    public String edgeLabelMaterialization(List<String> triple, int viewName){
        String s = triple.get(1);
        if(triple.size()==3 && triple.get(1).contains(":")){
            String[] tokens = triple.get(1).split(":",-1);
            s = tokens[0];
        }

        String o = triple.get(2);
        if(triple.size()==3 && triple.get(2).contains(":")){
            String[] tokens = triple.get(2).split(":",-1);
            o = tokens[0];
        }

        String viewEdge = "("+s+")-[r:View_"+viewName+"]->("+o+")";
        StringBuilder output = new StringBuilder("MATCH ");
        output.append(String.join(",", cypherPatterns));
        if(!whereClauseComponents.isEmpty()){
            output.append(" WHERE ");
            output.append(String.join(" AND \n",whereClauseComponents));
        }
        output.append(" MERGE " + viewEdge);
        output.append(" RETURN r");
        return output.toString();
    }

    @Override
    public String nodeLabelMaterialization(String node, int viewName){
        StringBuilder output = new StringBuilder("MATCH ");
        output.append(String.join(",", cypherPatterns));
        if(!whereClauseComponents.isEmpty()){
            output.append(" WHERE ");
            output.append(String.join(" AND \n",whereClauseComponents));
        }
        output.append(" SET "+node+":View_" + viewName);
        output.append(" RETURN " + node);
        return output.toString();
    }

    @Override
    public String shortcutMaterialization(List<String> triple, int viewName){
        return edgeLabelMaterialization(triple,viewName);
    }

    @Override
    public String reificationMaterialization(int viewName){
        StringBuilder output = new StringBuilder("CREATE (n:View_"+viewName+"_NODE)\n");
        output.append("WITH n\n");
        output.append(" MATCH ");
        output.append(String.join(",", cypherPatterns));
        if(!whereClauseComponents.isEmpty()){
            output.append(" WHERE ");
            output.append(String.join(" AND \n",whereClauseComponents));
        }
        int count=0;
        for(String var:headVars.stream().toList()) {
            if(!var.contains("labels")) {
                output.append(" MERGE (n)-[:View_").append(viewName).append("_").append(count).append("]->(").append(var).append(")\n");
                count++;
            }
        }
        output.append(" RETURN n");
        return output.toString();
    }

    @Override
    public String subgraphMaterialization(int viewName){
        StringBuilder output = new StringBuilder("MATCH ");
        int count=0;
        int count_else=0;
        for(String pattern:cypherPatterns.stream().toList()){
            if(pattern.contains("[")) {
                if (count_else==0 && count == 0)
                    output.append(pattern.replace("[:", "[r" + count + ":"));
                else
                    output.append(",").append(pattern.replace("[:", "[r" + count + ":"));
                count++;
            }
            else if(pattern.contains(":`")){
                if(count_else == 0 && count==0)
                    output.append(pattern);
                else
                    output.append(",").append(pattern);
                count_else++;
            }
        }
        if(!whereClauseComponents.isEmpty()){
            output.append(" WHERE ");
            output.append(String.join(" AND \n",whereClauseComponents));
        }

        //Collect all used variables
        StringBuilder varGroup = new StringBuilder();
        output.append(" WITH ");

        int index=0;
        for(String var:headVars.stream().toList()) {
            if(!var.contains("labels")) {
                if(index==0) {
                    varGroup.append("_"+var);
                    output.append("collect( distinct ").append(var).append(") AS _").append(var);
                } else {
                    varGroup.append("+_").append(var);
                    output.append(",collect( distinct ").append(var).append(") AS _").append(var);
                }
                index++;
            }
        }
        StringBuilder relGroup = new StringBuilder();
        for(int i=0;i<count;i++) {
            if(i==0) relGroup.append("_r").append(i); else relGroup.append("+_r").append(i);
            output.append(",collect( distinct r").append(i).append(") AS _r").append(i);
        }

        output.append("\n CALL apoc.export.csv.data("+varGroup.toString()+", "+relGroup.toString()+", \"View_"+viewName+".csv\", " +
                "{delim:\"|\",quotes:'ifNeeded',timeoutSeconds:86400,batchSize:32768})\n");
        output.append(" YIELD file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data\n");
        output.append(" RETURN file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data\n");

        return output.toString();
    }

    public String materializeSubgraphForPaths(int viewName){
        StringBuilder output = new StringBuilder("MATCH ");
        int count=0;
        for(String pattern:cypherPatterns.stream().toList()){
            if(pattern.contains("[")) {
                if (count == 0)
                    output.append(pattern.replace("[:", "[r" + count + ":"));
                else
                    output.append(",").append(pattern.replace("[:", "[r" + count + ":"));
                count++;
            }
        }
        if(!whereClauseComponents.isEmpty()){
            output.append(" WHERE ");
            output.append(String.join(" AND \n",whereClauseComponents));
        }

        //Collect all used variables
        StringBuilder varGroup = new StringBuilder();
        output.append(" WITH ");

        int index=0;
        for(String var:headVars.stream().toList()) {
            if(!var.contains("labels")) {
                if(index==0) {
                    varGroup.append("_"+var);
                    output.append("collect( distinct ").append(var).append(") AS _").append(var);
                } else {
                    varGroup.append("+_").append(var);
                    output.append(",collect( distinct ").append(var).append(") AS _").append(var);
                }
                index++;
            }
        }
        StringBuilder relGroup = new StringBuilder();
        for(int i=0;i<count;i++) {
            if(i==0) relGroup.append("_r").append(i); else relGroup.append("+_r").append(i);
            if(propertyPathVarList.contains("r"+i)) output.append(",apoc.coll.flatten(collect( distinct r").append(i).append(")) AS _r").append(i);
            else output.append(",collect( distinct r").append(i).append(") AS _r").append(i);
        }

        output.append("\n CALL apoc.export.csv.data("+varGroup.toString()+", "+relGroup.toString()+", \"View_"+viewName+".csv\", " +
                "{delim:\"|\",quotes:'ifNeeded',timeoutSeconds:86400,batchSize:16384})\n");
        output.append(" YIELD file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data\n");
        output.append(" RETURN file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data\n");

        return output.toString();
    }

    /************************* VIEW DELETION QUERIES *******************************/
    @Override
    public String edgeLabelDeletion(int viewName){
        return "MATCH (n)-[r:View_" + viewName + "]->(m) DELETE r";
    }

    @Override
    public String nodeLabelDeletion(int viewName){
        return "MATCH (n:View_" + viewName + ") REMOVE n:View_" + viewName;
    }

    @Override
    public String reificationDeletion(int viewName){
        return "MATCH (n:View_"+viewName+"_NODE)-[r]->() DETACH DELETE r,n";
    }

    @Override
    public String shortcutDeletion(int viewName){
        return edgeLabelDeletion(viewName);
    }

    @Override
    public String subgraphDeletion(int viewName){
        return "MATCH (n:View_"+viewName+")-[r]->(m:View_"+viewName+") DETACH DELETE r,n,m";
    }

    /************************ UTILITY FUNCTIONS ************************************/

    public HashMap<String,List<Pair<Integer, Integer>>> getReificationMappings(){
        HashMap<String,List<Pair<Integer, Integer>>> mappings = new HashMap<>();
        HashMap<String,Integer> vars = new HashMap<>();

        //Collect vars
        int count=0;
        for(String var:headVars.stream().toList()) {
            if(!var.contains("labels")) {
                vars.put(var,count);
                count++;
            }
        }

        count=0;
        for(String pattern:cypherPatterns){
            if(pattern.contains("->")) {
                List<String> triple = createEdge(pattern);
                Integer left = vars.get(triple.get(1));
                String leftVar = triple.get(1);
                if(left==null){
                    String[] tokens = triple.get(1).split(":",-1);
                    left = vars.get(tokens[0]);
                    leftVar = tokens[0];
                }
                Integer right = vars.get(triple.get(2));
                String rightVar = triple.get(2);
                if(right==null){
                    String[] tokens = triple.get(2).split(":",-1);
                    right = vars.get(tokens[0]);
                    rightVar = triple.get(2);
                }
                this.addToMappings(mappings,triple.get(0).replaceAll("`",""),new Pair<>(left,right));
                count++;
            }
        }

        System.out.println(mappings);
        return mappings;
    }

    private HashMap<String,List<Pair<Integer, Integer>>> addToMappings(HashMap<String,List<Pair<Integer, Integer>>> map,String key, Pair<Integer,Integer> pair){
        if(map.get(key)==null){
            List<Pair<Integer, Integer>> l = new ArrayList<>();
            l.add(pair);
            map.put(key,l);
        }
        else{
            List<Pair<Integer, Integer>> l = map.get(key);
            if(!l.contains(pair))
                l.add(pair);
            map.put(key,l);
        }
        return map;
    }

    public int varCount(){
        return headVars.size();
    }
}

