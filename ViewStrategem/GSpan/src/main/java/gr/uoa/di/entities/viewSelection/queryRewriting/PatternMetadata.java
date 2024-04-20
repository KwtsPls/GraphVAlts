package gr.uoa.di.entities.viewSelection.queryRewriting;

import gr.uoa.di.databaseConnectors.Connector;
import gr.uoa.di.entities.graph.PatternVS;
import gr.uoa.di.entities.viewSelection.hierarchy.PatternHierarchy;
import gr.uoa.di.entities.viewSelection.hierarchy._PatternWrapper;
import gr.uoa.di.translators.Translator;
import gr.uoa.di.translators.cypher.CypherQuery;
import gr.uoa.di.viewTemplates.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PatternMetadata {
    String entry;
    int id;
    double patternCost;
    PatternVS patternVS;
    String replacedEdge;
    EdgeLabelView edgeLabelView;
    NodeLabelView nodeLabelView;
    ShortcutView shortcutView;
    ReificationView reificationView;
    SubgraphView subgraphView;

    public PatternMetadata(String entry){
        this.entry = entry;
    }

    //Method to parse the given line from the stats.csv file
    public void initialize(PatternHierarchy patternHierarchy, Translator translator, Connector connector, Connector viewConnector, String path){
        String[] tokens = this.entry.split("\\|",-1);
        //First entry is the viewName
        int viewName = Integer.parseInt(tokens[0]);
        this.id = viewName;
        //Second entry is the frequent pattern
        String patternString = tokens[1];
        String[] patternTokens = patternString.split("\\[",-1);
        patternTokens = patternTokens[1].split(",",-1);
        String stringId = patternTokens[1].replace(" id: ","").replace("]","");
        int patternId = Integer.parseInt(stringId);

        //Third entry is the replaced edge - if it exists
        this.replacedEdge = null;
        if(!tokens[2].contains("UNIQUE c0:Resource(uri)")){
            this.replacedEdge = tokens[2];
        }

        this.patternCost = Double.parseDouble(tokens[3]);
        double storageEdge = Double.parseDouble(tokens[4]);
        double storageNode = Double.parseDouble(tokens[5]);
        double storagePath = Double.parseDouble(tokens[6]);
        double storageReification = Double.parseDouble(tokens[7]);
        double storageSubgraph = Double.parseDouble(tokens[8]);

        List<PatternVS> patternVSList = new ArrayList<>();
        patternHierarchy.forEachPatternContainedPatterns((patternVS, myPairIterator) -> {
            patternVSList.add(patternVS);
        });

        PatternVS p=null;
        for(PatternVS patternVS:patternVSList){
            if(patternVS.getId()==patternId){
                p = patternVS;
                this.patternVS = patternVS;
                break;
            }
        }

        System.out.println(p);
        if(storageEdge!=-1)
            this.edgeLabelView = new EdgeLabelView(translator,connector,p,viewName,replacedEdge,storageEdge);
        else
            this.edgeLabelView = null;

        if(storageNode!=-1)
            this.nodeLabelView = new NodeLabelView(translator,connector,p,viewName,replacedEdge,storageNode);
        else
            this.nodeLabelView = null;

        if(storagePath!=-1) {
            String replacedPropertyPath = ((CypherQuery)translator.convert(p)).getPropertyPath();
            System.out.println(replacedPropertyPath);
            this.shortcutView = new ShortcutView(translator, connector, p, viewName, replacedPropertyPath, storagePath);
        }
        else
            this.shortcutView = null;

        if(storageReification!=-1)
            this.reificationView = new ReificationView(translator,connector,p,viewName,storageReification);
        else
            this.reificationView = null;

        if(storageSubgraph!=-1)
            this.subgraphView = new SubgraphView(translator,connector,viewConnector,p,viewName,storageSubgraph, path);
        else
            this.subgraphView = null;
    }


    public EdgeLabelView getEdgeLabelView() {
        return edgeLabelView;
    }

    public NodeLabelView getNodeLabelView() {
        return nodeLabelView;
    }

    public ShortcutView getShortcutView() {
        return shortcutView;
    }

    public ReificationView getReificationView() {
        return reificationView;
    }

    public SubgraphView getSubgraphView() {
        return subgraphView;
    }

    public String getEntry() {
        return entry;
    }

    public int getId(){
        return id;
    }

    public PatternVS getPatternVS() {
        return patternVS;
    }

    @Override
    public String toString() {
        return "PatternMetadata{" +
                "id=" + id +
                ", patternCost=" + patternCost +
                ", replacedEdge='" + replacedEdge + '\'' +
                ", edgeLabelView=" + edgeLabelView +
                ", nodeLabelView=" + nodeLabelView +
                ", shortcutView=" + shortcutView +
                ", reificationView=" + reificationView +
                ", subgraphView=" + subgraphView +
                '}';
    }
}
