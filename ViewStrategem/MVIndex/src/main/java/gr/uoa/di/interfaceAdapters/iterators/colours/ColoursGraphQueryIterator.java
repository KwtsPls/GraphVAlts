package gr.uoa.di.interfaceAdapters.iterators.colours;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.dictionary.Stage;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForGraph;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForNode;
import gr.uoa.di.entities.graph.regular.abstractions.AbstractionForTriple;
import gr.uoa.di.entities.graph.regular.factory.AbstractGraphFactory;
import gr.uoa.di.interfaceAdapters.iterators.roadnet.ROADNETQueryIterator;
import gr.uoa.di.interfaceAdapters.iterators.roadnet.ROADNETSecondaryQueryIterator;
import gr.uoa.di.interfaceAdapters.workloads.JenaGraphQueryIterator;

public class ColoursGraphQueryIterator {

    public static <N extends AbstractionForNode<N, T>, T extends AbstractionForTriple<N, T>, G extends AbstractionForGraph<N, T, G>> JenaGraphQueryIterator<G> create(
            Dictionary dictionary, AbstractGraphFactory<N, T, G> factory) {
        ColoursQueryIterator queryIter = new ColoursQueryIterator(false);
        return JenaGraphQueryIterator.create(queryIter, dictionary, Stage.INSERTION_STAGE, factory);
    }

    public static <N extends AbstractionForNode<N, T>, T extends AbstractionForTriple<N, T>, G extends AbstractionForGraph<N, T, G>> JenaGraphQueryIterator<G> createLowerCase(
            Dictionary dictionary, AbstractGraphFactory<N, T, G> factory) {
        ColoursQueryIterator queryIter = new ColoursQueryIterator(true);
        return JenaGraphQueryIterator.create(queryIter, dictionary, Stage.INSERTION_STAGE, factory);
    }

    public static <N extends AbstractionForNode<N, T>, T extends AbstractionForTriple<N, T>, G extends AbstractionForGraph<N, T, G>> JenaGraphQueryIterator<G> createSecondary(
            Dictionary dictionary, AbstractGraphFactory<N, T, G> factory) {
        ColoursSecondaryQueryIterator queryIter = new ColoursSecondaryQueryIterator();
        return JenaGraphQueryIterator.create(queryIter, dictionary, Stage.INSERTION_STAGE, factory);
    }

}
