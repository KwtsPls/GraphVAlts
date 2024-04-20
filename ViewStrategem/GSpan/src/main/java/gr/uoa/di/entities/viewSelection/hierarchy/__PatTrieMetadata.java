package gr.uoa.di.entities.viewSelection.hierarchy;

import java.util.Iterator;
import java.util.TreeSet;

import gr.uoa.di.entities.graph.NodeVS;
import gr.uoa.di.entities.graph.PatternVS;
import gr.uoa.di.entities.graph.VarType;
import gr.uoa.di.entities.trie.TrieMetadata;
import gr.uoa.di.interfaceAdapters.controllers.randomAccessDiskMap.patterns.RNDAccessPatterns;

class __PatTrieMetadata implements TrieMetadata<PatternVS> {
	private PatternVS pattern = null;
	private boolean toUpdateSupport = false;

	@Override
	public void updateOn(PatternVS newPattern) {
		if (this.pattern == null) {
			this.pattern = newPattern;
		} else {
			// Updates all the appearances of the two patterns
			int previousWhere = pattern.getWhere().size();
			pattern.getWhere().addAll(newPattern.getWhere());
			if (previousWhere != pattern.getWhere().size()) {
				toUpdateSupport = true;
			}
			// Sets variable-constants and conjuncted nodes
			Iterator<NodeVS> previousVarIter = pattern.getGraph().getVariables().iterator();
			Iterator<NodeVS> newVarIter = newPattern.getGraph().getVariables().iterator();
			while (previousVarIter.hasNext()) {
				NodeVS pVar = previousVarIter.next();
				NodeVS nVar = newVarIter.next();
				if (pVar.getLabel() != nVar.getLabel()) {
					System.err.println("There is no match between variables");
					System.exit(1);
				}
				if (nVar.isVarConstant()) {
					pVar.setVarType(VarType.VarConstant);
				} else if (nVar.isConjuncted()) {
					pVar.setVarType(VarType.Conjucted);
				}
			}
		}
	}

	public PatternVS getPattern() {
		return pattern;
	}

	void updateSupport(RNDAccessPatterns groupedQueries) {
		if (toUpdateSupport)
			pattern.setSupport(getSupport(groupedQueries, pattern.getWhere()));
		toUpdateSupport = false;
	}

	static private int getSupport(RNDAccessPatterns groupedQueries, TreeSet<Integer> where) {
		int queryMeter = 0;
		for (int gId : where) {
			TreeSet<Integer> actualWhere = groupedQueries.get(gId).getWhere();
			queryMeter += actualWhere.size();
		}
		return queryMeter;
	}
}
