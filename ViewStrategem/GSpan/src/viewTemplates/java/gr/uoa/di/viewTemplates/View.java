package gr.uoa.di.viewTemplates;

import gr.uoa.di.databaseConnectors.Connector;
import gr.uoa.di.entities.graph.PatternVS;
import gr.uoa.di.entities.trie.TrieMetadata;
import gr.uoa.di.entities.viewSelection.materialization.TrieIndexForMaterialization;
import gr.uoa.di.entities.viewSelection.queryRewriting.ViewForRewriting;
import gr.uoa.di.translators.Query;
import gr.uoa.di.translators.Translator;

public abstract class View implements ViewForRewriting {

    protected Connector connector;
    protected Translator translator;
    protected PatternVS queryPattern;
    TrieIndexForMaterialization trie;
    protected int viewName;

    public static final int RELATIONSHIP_COST=41;
    public static final int NODE_COST=15;
    public static final int LABEL_COST=1;
    public static final int PROPERTY_STORE_COST=41;
    public static final int PROPERTY_VALUE_COST=128;

    public View(Translator translator,Connector connector,PatternVS queryPattern,int viewName){
        this.translator = translator;
        this.connector = connector;
        this.queryPattern = queryPattern;
        this.viewName = viewName;
        this.trie = TrieIndexForMaterialization.create();
        trie.insertQueryPattern(queryPattern.getGraph(), this);
    }

    public abstract double getBenefit();
    public abstract PatternVS getQueryPattern();
    public abstract Double materialize();
    public abstract void delete();
    public abstract Query rewrite(PatternVS pattern);
    public abstract double executeThroughView(PatternVS query);
    public abstract Double materializeThroughFile(String importPath);
}
