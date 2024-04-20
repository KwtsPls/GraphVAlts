package gr.uoa.di.interfaceAdapters.sparqlParser;

//import java.util.ArrayList;
import java.util.LinkedList;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForGraph;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForNode;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForTriple;
import gr.uoa.di.entities.graph.regular.factory.AbstractGraphFactory;

/**
 * This class is used to transform a query in SPARQL format, to a Faceted Query.
 *
 */
public interface SparqlToFacetedQuery<G extends AbstractionForGraph<?, ?, G>> {

	public LinkedList<G> getGraphQuery(String queryString, Dictionary dictionary);

	public static <N extends AbstractionForNode<N, T>, T extends AbstractionForTriple<N, T>, G extends AbstractionForGraph<N, T, G>> SparqlToFacetedQuery<G> create(
			AbstractGraphFactory<N, T, G> factory) {
		return new _SparqlToFacetedQuery<N, T, G>(factory);
	}

	

}
