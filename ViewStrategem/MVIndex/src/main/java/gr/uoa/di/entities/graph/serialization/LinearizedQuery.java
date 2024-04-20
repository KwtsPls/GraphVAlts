package gr.uoa.di.entities.graph.serialization;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.linked.TIntLinkedList;
import gr.uoa.di.entities.dictionary.Dictionary;

public class LinearizedQuery extends TIntLinkedList{
	
	@Override
	public String toString() {
		
		StringBuilder builder=new StringBuilder("(");
		TIntIterator iter=this.iterator();
		
		while(iter.hasNext()) {
			int label=iter.next();
			if(label==Dictionary.invEdgeMark) {
				builder.append(", ");
			}else if(label==Dictionary.endEdgeMark) {
				builder.append(". ");
			}else {
				builder.append(getVariable(label));
				builder.append(' ');
			}
			
		}
		builder.setCharAt(builder.length()-1, ')');
		return builder.toString();
	}

	private String getVariable(int label) {
		if(Dictionary.isVariable(label)) {
			return Dictionary.getVarStringOfId(label);
		}
		return label+"";
	}
	
	
	

}
