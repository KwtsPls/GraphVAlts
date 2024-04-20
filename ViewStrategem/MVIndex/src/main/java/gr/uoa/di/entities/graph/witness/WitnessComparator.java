package gr.uoa.di.entities.graph.witness;

import java.util.Comparator;

class WitnessComparator {
	
	static Comparator<TripleWitness<?,?>> edgeWitnessComparator = new Comparator<TripleWitness<?,?>>() {
		@Override
		public int compare(TripleWitness<?,?> e1, TripleWitness<?,?> e2) {
			return Integer.compare(e1.predicateLabel, e2.predicateLabel);
		}
	};

}
