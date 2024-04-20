package gr.uoa.di.entities.viewSelection.edgeRewriting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import gr.uoa.di.entities.graph.PatternVS;
import gr.uoa.di.entities.graph.Printable;
import gr.uoa.di.entities.graph.TripleVS;
import gr.uoa.di.entities.helpStructures.tuples.IntTriple;
import gr.uoa.di.entities.helpStructures.tuples.MyPair;
import gr.uoa.di.entities.trie.containment.mapping.ContainmentMapping;

class _TripleMap implements TripleMap {

	private Map<TripleVS, TripleVS> map = new HashMap<>();
	private boolean isomorphic;

	public <M> _TripleMap(PatternVS fromPattern, ContainmentMapping<?, TripleVS, M> mapping) {
		fromPattern.getGraph().forEachTriple(triple -> {
			IntTriple tripleIds = IntTriple.of(triple);
			map.computeIfAbsent(triple, y -> mapping.getContainmentMapping(tripleIds));
		});
		isomorphic = mapping.isIsomorphic();
	}

	public _TripleMap(boolean isomorphic, List<MyPair<TripleVS, TripleVS>> pairs) {
		this.isomorphic = isomorphic;
		pairs.forEach(pair -> {
			if (pair.getLeft() == null) {
				System.err.println(pair);
			}
			map.put(pair.getLeft(), pair.getRight());
		});
	}

	@Override
	public TripleVS getMappedTriple(TripleVS edge) {
		return map.get(edge);
	}

	@Override
	public boolean isIsomorphic() {
		return isomorphic;
	}

	@Override
	public String toString() {
		return toCompactString();
	}

	@Override
	public void forEachTripleRewriting(BiConsumer<TripleVS, TripleVS> consumer) {
		map.forEach(consumer);

	}

	@Override
	public String print(Function<Printable, String> function) {
		StringBuilder out = new StringBuilder();
		map.forEach((edge1, edge2) -> {
			out.append(function.apply(edge1)).append("->");
			out.append(function.apply(edge2)).append(" ");
		});
		return out.toString();
	}

}
