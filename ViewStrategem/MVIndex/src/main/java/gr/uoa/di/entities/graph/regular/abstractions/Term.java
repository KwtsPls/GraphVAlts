package gr.uoa.di.entities.graph.regular.abstractions;

import java.util.Iterator;

import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.Printable;

public interface Term extends Comparable<Term>, Printable{
	
	/**
	 * Creates a variable term based on a variable object
	 * @param var The object that corresponds to the variable object
	 * @return The corresponding output term
	 */
	public static Term getVarTerm(Object var) {
		return new _Term(var,Dictionary.variableLabel,false);
	}
	
	/**
	 * Creates a constant term based on the object's numerical identifier and the specific object, and  
	 * @param id The object's identifier
	 * @param object The corresponding object
	 * @return The corresponding term for the specific object
	 */
	public static Term getConstantTerm(int id,Object object) {
		return new _Term(object,id,true);
	}
	
	/**
	 * @return True if the term is a variable
	 */
	public boolean isVariable();
	
	/**
	 * @return True if the term is a variable and contains a corresponding variable label
	 */
	public boolean isLabeledVariable();
	
	/**
	 * @return True if the term is a constant
	 */
	public boolean isConstant();
	
	
	/**
	 * @return Returns the numerical identifier corresponding to the label of the term
	 */
	public int getLabel();
	
		
	
	/**
	 * @return The element in its term form
	 */
	public Term getTerm();
	
	
	/**
	 * @param iter Assigns a variable id, if the term is a variable
	 */
	public void setVarEnumeration(Iterator<Integer> iter);


	@Override
	public default int compareTo(Term o) {
		if (this.isVariable() && o.isVariable())
			return this.toString().compareTo(o.toString());
		return Integer.compare(this.getLabel(), o.getLabel());
	}

	/**
	 * @param id Set the identifier of a specific term
	 */
	public void setLabel(int id);

	
	
}
