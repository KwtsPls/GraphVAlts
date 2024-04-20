package gr.uoa.di.translators;

import org.apache.jena.atlas.lib.Pair;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

public interface Query {
    //View materialization queries
    public String edgeLabelMaterialization(List<String> triple, int viewName);
    public String nodeLabelMaterialization(String node, int viewName);
    public String shortcutMaterialization(List<String> triple, int viewName);
    public String reificationMaterialization(int viewName);
    public String subgraphMaterialization(int viewName);

    //View deletion queries
    public String edgeLabelDeletion(int viewName);
    public String nodeLabelDeletion(int viewName);
    public String reificationDeletion(int viewName);
    public String shortcutDeletion(int viewName);
    public String subgraphDeletion(int viewName);

    //Misc methods
    public boolean isCyclic();
    public boolean containsPropertyPath();
    public LinkedHashSet<String> getHeadVars();
    public HashMap<String, List<Pair<Integer, Integer>>> getReificationMappings();
}
