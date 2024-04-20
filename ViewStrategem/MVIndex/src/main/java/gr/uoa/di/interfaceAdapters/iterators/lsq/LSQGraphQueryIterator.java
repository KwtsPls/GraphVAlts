package gr.uoa.di.interfaceAdapters.iterators.lsq;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.dictionary.Stage;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForGraph;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForNode;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForTriple;
import gr.uoa.di.entities.graph.regular.factory.AbstractGraphFactory;
import gr.uoa.di.interfaceAdapters.workloads.JenaGraphQueryIterator;

public class LSQGraphQueryIterator {

    public static <N extends AbstractionForNode<N, T>, T extends AbstractionForTriple<N, T>, G extends AbstractionForGraph<N, T, G>> JenaGraphQueryIterator<G> create(
            Dictionary dictionary, AbstractGraphFactory<N, T, G> factory) {
        LSQQueryIterator queryIter = new LSQQueryIterator(false);
        return JenaGraphQueryIterator.create(queryIter, dictionary, Stage.INSERTION_STAGE, factory);
    }

    public static <N extends AbstractionForNode<N, T>, T extends AbstractionForTriple<N, T>, G extends AbstractionForGraph<N, T, G>> JenaGraphQueryIterator<G> createLowerCase(
            Dictionary dictionary, AbstractGraphFactory<N, T, G> factory) {
        LSQQueryIterator queryIter = new LSQQueryIterator(true);
        return JenaGraphQueryIterator.create(queryIter, dictionary, Stage.INSERTION_STAGE, factory);
    }

    public static <N extends AbstractionForNode<N, T>, T extends AbstractionForTriple<N, T>, G extends AbstractionForGraph<N, T, G>> JenaGraphQueryIterator<G> createSecondary(
            Dictionary dictionary, AbstractGraphFactory<N, T, G> factory) {
        LSQSecondaryQueryIterator queryIter = new LSQSecondaryQueryIterator();
        return JenaGraphQueryIterator.create(queryIter, dictionary, Stage.INSERTION_STAGE, factory);
    }

}
