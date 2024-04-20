package gr.uoa.di.translators;

import gr.uoa.di.entities.graph.PatternVS;
import gr.uoa.di.entities.graph.TripleVS;
import gr.uoa.di.translators.cypher.CypherQuery;
import org.apache.jena.atlas.lib.Pair;

import java.util.List;

public interface Translator {
    public Query convert(PatternVS pattern);
    public Query convert(String patternString);
    public Pair<String,Integer> parse(TripleVS tripleVS, int varCount);
    public List<String> parse(TripleVS tripleVS);
}
