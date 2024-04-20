package gr.uoa.di.interfaceAdapters.workloads;

import java.io.IOException;
import java.util.LinkedList;


import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.dictionary.Stage;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForGraph;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForNode;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForTriple;
import gr.uoa.di.entities.graph.regular.factory.AbstractGraphFactory;
import gr.uoa.di.interfaceAdapters.sparqlParser.SparqlToFacetedQuery;

public class JenaGraphQueryIterator<G extends AbstractionForGraph<?, ?, G>>
		implements GraphQueryIterator<G>, AutoCloseable {

	private QueryIterator iter;
	private Dictionary translator;
	private LinkedList<G> graphs = new LinkedList<G>();
	private String name;
	private SparqlToFacetedQuery<G> sparqlToFacetedQuery;

	public static <N extends AbstractionForNode<N, T>, T extends AbstractionForTriple<N, T>, G extends AbstractionForGraph<N, T, G>> JenaGraphQueryIterator<G> create(
			QueryIterator iter, Dictionary dictionary, Stage stage, AbstractGraphFactory<N, T, G> factory) {
		JenaGraphQueryIterator<G> out = new JenaGraphQueryIterator<G>(iter, dictionary, stage);
		out.sparqlToFacetedQuery = SparqlToFacetedQuery.create(factory);
		return out;
	}

	@Override
	public boolean hasNext() {
		if (!graphs.isEmpty()) {
			return true;
		} else {
			while (iter.hasNext()) {
				String queryString = iter.next();
				graphs.addAll(sparqlToFacetedQuery.getGraphQuery(queryString, translator));
				if (!graphs.isEmpty())
					return true;
			}
			return false;
		}
	}

	private JenaGraphQueryIterator(QueryIterator iter, Dictionary dictionary, Stage stage) {
		this.iter = iter;
		this.translator = dictionary;
		dictionary.setStage(stage);
	}

	@Override
	public G next() {
		return graphs.removeFirst();
	}

	@Override
	public void close() throws IOException {
		iter.close();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Dictionary getDictionary() {
		return translator;
	}

}
