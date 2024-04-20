package gr.uoa.di.entities.graph.regular.implementations;

import gr.uoa.di.entities.graph.regular.abstractions.Term;
import gr.uoa.di.entities.graph.regular.factory.AbstractGraphFactory;

public class BasicGraphFactory extends AbstractGraphFactory<BasicNode,BasicTriple,BasicGraph>{
			
	@Override
	public BasicGraph createGraph() {
		return new BasicGraph() ;
	}
	
	@Override
	public BasicTriple createTriple(BasicNode subject, Term predicate, BasicNode object) {
		return new BasicTriple( subject, predicate, object);
	}
	
	@Override
	public BasicNode createVarNode(Object var) {
		return new BasicNode(Term.getVarTerm(var));
	}

	@Override
	public BasicNode createConstantNode(int objectID, Object var) {		
		return new BasicNode(Term.getConstantTerm(objectID, var));
	}

}
