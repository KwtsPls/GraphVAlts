package gr.uoa.di.interfaceAdapters.controllers.formatters.gSpan.translators;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.FactoryVS;
import gr.uoa.di.entities.graph.GraphVS;
import gr.uoa.di.entities.graph.NodeVS;
import gr.uoa.di.entities.graph.PatternVS;
import gr.uoa.di.entities.graph.TripleVS;
import gr.uoa.di.entities.graph.VarType;
import gr.uoa.di.entities.graph.regular.factory.GraphConstructor;
import gr.uoa.di.entities.gspan.gspanGraph.GSpanGraph;
import gr.uoa.di.entities.gspan.gspanGraph.IntTriple;

class _GspanInvTranslator implements GSpanInvTranslator {
	private static final FactoryVS factory = new FactoryVS();
	private static final int constantLabel = Dictionary.constantLabel;
	private static final int variableLabel = Dictionary.variableLabel;
	private static final int subjConjunct = Dictionary.subjConjunct;
	private static final int objConjunct = Dictionary.objConjunct;
	private static final int conjunctionLabel = Dictionary.conjunctionLabel;
	private Dictionary dict;

	_GspanInvTranslator(Dictionary dict) {
		this.dict = dict;
	}

	@Override
	public PatternVS translate(GSpanGraph gSpan) {
		if (gSpan == null)
			return null;
		return new GraphCreater().deSerialize(gSpan);
	}

	private class GraphCreater {

		private GraphConstructor<NodeVS, TripleVS, GraphVS> graphConstructor;

		private PatternVS deSerialize(GSpanGraph gspan) {

			TIntObjectMap<VarType> idToVarType = new TIntObjectHashMap<>(); // if it's a constant then
																			// {1,0}, if it's a
																			// conjuncted than {0,1}
			HashMap<Integer, IntTriple> predicate2IntTriple = new HashMap<>();
			List<Integer> nodeLabels = gspan.nodeLabels;
			List<IntTriple> intTriples = gspan.tripleLabels;
			// Filling the predicate2SubjectObject map that maps each predicate to its
			// corresponding subject and object position
			for (int cursor = 0; cursor < intTriples.size(); cursor++) {
				int subjPosition = intTriples.get(cursor).getLeft();
				int objPosition = intTriples.get(cursor).getMiddle();
				int edgeLabel = intTriples.get(cursor).getRight();
				switch (edgeLabel) {
				case subjConjunct:
					if (Dictionary.isConstant(nodeLabels.get(subjPosition))) {
						IntTriple triple = predicate2IntTriple.computeIfAbsent(subjPosition,
								x -> IntTriple.of(-1, nodeLabels.get(subjPosition), -1));
						triple.setLeft(objPosition);
					} else {
						IntTriple triple = predicate2IntTriple.computeIfAbsent(objPosition,
								x -> IntTriple.of(-1, nodeLabels.get(objPosition), -1));
						triple.setLeft(subjPosition);
					}
					break;
				case objConjunct:
					if (Dictionary.isConstant(nodeLabels.get(subjPosition))) {
						IntTriple triple = predicate2IntTriple.computeIfAbsent(subjPosition,
								x -> IntTriple.of(-1, nodeLabels.get(subjPosition), -1));
						triple.setRight(objPosition);
					} else {
						IntTriple triple = predicate2IntTriple.computeIfAbsent(objPosition,
								x -> IntTriple.of(-1, nodeLabels.get(objPosition), -1));
						triple.setRight(subjPosition);
					}
					break;
				default:
					if (subjPosition == objPosition) {
						if (edgeLabel == conjunctionLabel) {
							idToVarType.put(subjPosition, VarType.Conjucted);
						} else {
							nodeLabels.set(subjPosition, edgeLabel);
						}
					}
				}
			}
			// Naming Variables
			Iterator<Integer> varIter = Dictionary.getVarIdIterator();
			// Adding triples to the graph
			this.graphConstructor = factory.getGraphConstructor(dict);
			predicate2IntTriple.forEach((_k, triple) -> {
				int subjectId = getLabel(idToVarType, nodeLabels, triple.getLeft(), varIter);
				int objectId = getLabel(idToVarType, nodeLabels, triple.getRight(), varIter);
				int predicateId = triple.getMiddle();
				VarType subType = idToVarType.get(triple.getLeft());
				VarType objType = idToVarType.get(triple.getRight());
				TripleVS tripleVS = graphConstructor.addTripleFromInt(subjectId, predicateId, objectId);
				setConstantOrConjunction(tripleVS.getSubject(), subjectId, subType);
				setConstantOrConjunction(tripleVS.getObject(), objectId, objType);
			});
			// Getting the graph
			//
			return PatternVS.create(graphConstructor.getGraphQuery(), gspan.id, gspan.support, gspan.where);
		}

		private int getLabel(TIntObjectMap<VarType> idToVarType, List<Integer> nodeLabels, int cursor,
				Iterator<Integer> varIter) {
			if (cursor == -1) {
				return varIter.next();
			}
			int out = nodeLabels.get(cursor);
			if (out == variableLabel || out == constantLabel) {
				if (out == constantLabel)
					idToVarType.put(cursor, VarType.VarConstant);
				out = varIter.next();
				nodeLabels.set(cursor, out);
			}
			return out;
		}

		private void setConstantOrConjunction(NodeVS x, int id, VarType varType) {
			if (x.isConstant() || varType == null) {
				return;
			}
			x.setVarType(varType);
		}
	}

}
