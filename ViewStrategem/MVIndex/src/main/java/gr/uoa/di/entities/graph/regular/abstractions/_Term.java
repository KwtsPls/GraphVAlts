package gr.uoa.di.entities.graph.regular.abstractions;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.function.Function;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Literal;
import org.apache.jena.graph.Node_URI;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.Printable;
import gr.uoa.di.entities.graph.regular.helpers.CompactString;

 class _Term implements Term,Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object object;
	private int label;
	private boolean isConstant;
	
	_Term(Object object,int label,boolean isConstant ) {
		this.object= object;
		this.label=label;
		this.isConstant=isConstant;
	}

	@Override
	public boolean isVariable() {
		return !isConstant;
	}
	
	@Override
	public boolean isLabeledVariable() {
		return Dictionary.isLabeledVariable(label);
	}

	@Override
	public boolean isConstant() {
		return isConstant;
	}

	@Override
	public String toIdString() {
		if(isConstant)
		return Integer.toString(label);
		return object.toString();
	}
	
	@Override
	public String toString() {
		if(isConstant)
			return new StringBuffer(object.toString()).append("{").append(Integer.toString(label)).append('}').toString();
		else
			return new StringBuffer(object.toString()).toString();
	}

	@Override
	public int getLabel() {
		return label;
	}

//	@Override
//	public Object getItem() {
//		return object;
//	}

	@Override
	public void setVarEnumeration(Iterator<Integer> iter) {
		if( (!isConstant) && label==Dictionary.variableLabel) 
			label=iter.next();
		
	}

	@Override
	public _Term getTerm() {
		return this;
	}

	@Override
	public String toCompactString() {
		if(isConstant) {
			return CompactString.apply(object);	
		}
		return object.toString();
	}

	@Override
	public String toFullString() {		
		return object.toString();
	}

	@Override
	public String print(Function<Printable, String> function) {
		return function.apply(this);
	}

	@Override
	public void setLabel(int label) {
		this.label = label;
	}

	@Override
	public int hashCode() {
		return label;
	}

	@Override
	public boolean equals(Object obj) {		
		if (getClass() != obj.getClass())
			return false;
		_Term other = (_Term) obj;
		return label == other.label;
	}
		
	private void writeObject(ObjectOutputStream out) throws IOException{
		out.writeInt(label);		
		out.writeBoolean(isConstant);
		if(Node_URI.class.isInstance(object)) {
			out.writeInt(0);
			out.writeObject(((Node_URI)object).getURI());
		}else if(Node_Literal.class.isInstance(object)){
			out.writeInt(1);
			out.writeObject(((Node_Literal)object).getLiteral().getLexicalForm());
			out.writeObject(((Node_Literal)object).getLiteralLanguage());
		}else {
			System.err.println("The specific class cannot be serialized "+object.getClass());
			System.exit(1);
		}
		
	}
	
	private void readObject(ObjectInputStream in) 
	  throws IOException, ClassNotFoundException{		
		this.label = in.readInt();
		this.isConstant = in.readBoolean();
		int typeId= in.readInt();
		if(typeId == 0) {
			String uri = (String) in.readObject();
			this.object = NodeFactory.createURI(uri);
		}else {
			String literal = (String) in.readObject();
			String lang= (String) in.readObject();
			this.object = NodeFactory.createLiteral(literal,lang);
		}
		
	}
}
