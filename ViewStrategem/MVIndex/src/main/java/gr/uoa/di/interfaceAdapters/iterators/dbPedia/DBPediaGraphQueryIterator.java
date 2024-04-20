package gr.uoa.di.interfaceAdapters.iterators.dbPedia;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.dictionary.Stage;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForGraph;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForNode;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForTriple;
import gr.uoa.di.entities.graph.regular.factory.AbstractGraphFactory;
import gr.uoa.di.interfaceAdapters.workloads.JenaGraphQueryIterator;

public class DBPediaGraphQueryIterator {

	public static <N extends AbstractionForNode<N, T>, T extends AbstractionForTriple<N, T>, G extends AbstractionForGraph<N, T, G>> JenaGraphQueryIterator<G> create(
			Dictionary dictionary, AbstractGraphFactory<N, T, G> factory) {
		DBPediaQueryIterator queryIter = new DBPediaQueryIterator(false);
		return JenaGraphQueryIterator.create(queryIter, dictionary, Stage.INSERTION_STAGE, factory);
	}

	public static <N extends AbstractionForNode<N, T>, T extends AbstractionForTriple<N, T>, G extends AbstractionForGraph<N, T, G>> JenaGraphQueryIterator<G> createLowerCase(
			Dictionary dictionary, AbstractGraphFactory<N, T, G> factory) {
		DBPediaQueryIterator queryIter = new DBPediaQueryIterator(true);
		return JenaGraphQueryIterator.create(queryIter, dictionary, Stage.INSERTION_STAGE, factory);
	}

	public static <N extends AbstractionForNode<N, T>, T extends AbstractionForTriple<N, T>, G extends AbstractionForGraph<N, T, G>> JenaGraphQueryIterator<G> createSecondary(
			Dictionary dictionary, AbstractGraphFactory<N, T, G> factory) {
		DBPediaSecondaryQueryIterator queryIter = new DBPediaSecondaryQueryIterator();
		return JenaGraphQueryIterator.create(queryIter, dictionary, Stage.INSERTION_STAGE, factory);
	}

}
