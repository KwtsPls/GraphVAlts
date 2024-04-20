package gr.uoa.di.interfaceAdapters.sparqlParser;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.jena.base.Sys;
import org.apache.jena.graph.Factory;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.core.TriplePath;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForGraph;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForNode;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForTriple;
import gr.uoa.di.entities.graph.regular.factory.AbstractGraphFactory;
import gr.uoa.di.entities.graph.regular.factory.GraphConstructor;

public class _SparqlToFacetedQuery<N extends AbstractionForNode<N, T>, T extends AbstractionForTriple<N, T>, G extends AbstractionForGraph<N, T, G>>
		implements SparqlToFacetedQuery<G> {

	private AbstractGraphFactory<N, T, G> factory;

	public _SparqlToFacetedQuery(AbstractGraphFactory<N, T, G> factory2) {
		factory = factory2;
	}

	/**
	 * This function transforms the string of a SPARQL query (possibly containing
	 * unions etc.) to a list of Faceted Queries.
	 * 
	 * @param queryString the string of the SPARQL query.
	 * @param dictionary  the dictionary that will make the transformation.
	 * @return The list of Faceted queries that are contained in the initial
	 *         queryString. E.g. for a union of conjunctive queries, each of them
	 *         will be returned to the list.
	 */

	@Override
	public LinkedList<G> getGraphQuery(String queryString, Dictionary dictionary) {
		LinkedList<G> output = new LinkedList<G>();
		List<Set<TriplePath>> uCQs = getCQs(queryString);
		for (Set<TriplePath> cq : uCQs) {
			output.add(getCQAsInternalQuery(cq, dictionary));
		}
		return output;
	}

	private List<Set<TriplePath>> getCQs(String queryString) {
		try {
			Query query = new Query();
			QueryFactory.parse(query, queryString, (String) null, Syntax.defaultQuerySyntax);
			return _GetConjunctiveQueries.getUCQ(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new LinkedList<Set<TriplePath>>();
	}

	private G getCQAsInternalQuery(Set<TriplePath> cq, Dictionary dictionary) {
		GraphConstructor<N, T, G> graphConstructor = factory.getGraphConstructor(dictionary);

		if (cq.size() == 0)
			return null;
		for (TriplePath triplePath : cq) {
			Node s = triplePath.getSubject();
			Node o = triplePath.getObject();


			if (s == null || o == null || (!s.isURI() && !s.isVariable())
					|| (!o.isURI() && !o.isVariable() && !o.isLiteral())) {
				return null;
			} else {
				Node p = triplePath.getPredicate();
				Object r = triplePath.getPredicate();
				if(p==null) {
					r = triplePath.getPath();
					String rs = r.toString();
					if(rs.charAt(0)!='(' || rs.charAt(rs.length()-1)!='*') return null;
					r = rs.substring(2,rs.length()-3)+"_star_p_path_42";
					r = NodeFactory.createURI(r.toString());
				}
				else if (!p.isURI()) return null;

				if(r==null) return null;
				graphConstructor.addTripleFromJObjects(s, r, o);
			}
		}
		G query = graphConstructor.getGraphQuery();
		return query;
	}

}
