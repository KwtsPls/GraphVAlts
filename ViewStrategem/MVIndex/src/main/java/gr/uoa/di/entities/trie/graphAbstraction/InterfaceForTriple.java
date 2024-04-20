package gr.uoa.di.entities.trie.graphAbstraction;

import gr.uoa.di.entities.graph.Printable;
import gr.uoa.di.entities.graph.regular.abstractions.Term;

/**
 * This class is used to represent triples within the MVIndex structure
 *
 */
/**
 * @author theof
 *
 */
public interface InterfaceForTriple extends Printable {
	
	public static InterfaceForTriple createDummy(InterfaceForNode subject, Term predicate,InterfaceForNode object) {
		return new _DummyTriple(subject,predicate,object);
	}

	/**
	 * @return The subject of the triple
	 */
	public InterfaceForNode getSubject();

	/**
	 * @return The term corresponding to the predicate of the triple
	 */
	public Term getPredicate();

	/**
	 * @return The object of the triple 
	 */
	public InterfaceForNode getObject();

	/**
	 * @return True if the triple has be examined
	 */
	public boolean isVisited();

	/**
	 * @return Says that the triple has been examined
	 */
	public void visit();

	/**
	 * @return The id of the corresponding predicate
	 */
	public int getPredicateLabel();

	

	/**
	 * Resets the triple regarding if it has been visited or not
	 */
	public void resetForSerialization();


}
