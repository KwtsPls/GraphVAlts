package gr.uoa.di.interfaceAdapters.sparqlParser;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementBind;
import org.apache.jena.sparql.syntax.ElementData;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementNamedGraph;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementSubQuery;
import org.apache.jena.sparql.syntax.ElementUnion;
import org.apache.jena.atlas.io.Printable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by vaggelis on 7/8/2017.
 */
class _GetConjunctiveQueries {
	
	
	private static List<Set<TriplePath>> dummyList=null;
	static {
		dummyList=new ArrayList<Set<TriplePath>>(1);
		dummyList.add(new HashSet<TriplePath>(0));
	}
	
	/**Takes as input an arbitrary Jena query and returns it as a Union of conjunctive queries, in the form of triple paths. 
	 * @param query The corresponding jena query. 
	 * @return The list of triple paths within the query.
	 */
	static List<Set<TriplePath>> getUCQ(Query query) {
		List<Set<TriplePath>> subQueries=new ArrayList<Set<TriplePath>>();

		for(Set<TriplePath> triplePath:visit(query.getQueryPattern(),subQueries)) {
			if(triplePath.size()>0) {
				subQueries.add(triplePath);
			}
		}
		return subQueries;
	}
	
	private static List<Set<TriplePath>> visit(Element element,List<Set<TriplePath>> subQueries) {		
		if(ElementGroup.class.isInstance(element)) 
			return visit((ElementGroup)element,subQueries);			
		//else if(ElementTriplesBlock.class.isInstance(element)) visit((ElementTriplesBlock)element);
		else if(ElementFilter.class.isInstance(element)) 
			return dummyList;
		/*else if(ElementAssign.class.isInstance(element)) visit((ElementAssign)element);*/
		else if(ElementBind.class.isInstance(element)) 
			return dummyList;
		else if(ElementData.class.isInstance(element)) 
			return dummyList;
		else if(ElementUnion.class.isInstance(element)) 
			return visit((ElementUnion)element,subQueries);
		else if(ElementOptional.class.isInstance(element)) 
			return visit((ElementOptional)element,subQueries);
		else if(ElementPathBlock.class.isInstance(element)) 
			return visit((ElementPathBlock)element,subQueries);
		else if(ElementSubQuery.class.isInstance(element)) 
			return visit((ElementSubQuery)element,subQueries);
		else if(ElementNamedGraph.class.isInstance(element)) 
			return visit((ElementNamedGraph)element,subQueries);
		/*else if(ElementDataset.class.isInstance(element)) visit((ElementDataset)element);		
		else if(ElementExists.class.isInstance(element)) visit((ElementExists)element);
		else if(ElementNotExists.class.isInstance(element)) visit((ElementNotExists)element);
		else if(ElementMinus.class.isInstance(element)) visit((ElementMinus)element);
		else if(ElementService.class.isInstance(element)) visit((ElementService)element);
		*/
		//ElementNamedGraph
		else {
			System.err.println("The class "+element.getClass()+" is not checked");
			return null;
		}
		
	}
	
	private static List<Set<TriplePath>> visit(ElementNamedGraph graphElement,List<Set<TriplePath>> subQueries) {
		subQueries.addAll(visit(graphElement.getElement(),subQueries));
		return dummyList;
	}
	
	private static List<Set<TriplePath>> visit(ElementSubQuery subQuery,List<Set<TriplePath>> subQueries) {
		subQueries.addAll(getUCQ(subQuery.getQuery()));
		return dummyList;
	}
	
 	private static List<Set<TriplePath>> visit(ElementPathBlock elementPathBlock,List<Set<TriplePath>> subQueries) {
		List<Set<TriplePath>> output=new ArrayList<Set<TriplePath>>();
		Set<TriplePath> triples_set=new HashSet<TriplePath>();
		Iterator<TriplePath> triples = elementPathBlock.patternElts();
		while (triples.hasNext()) {
			triples_set.add(triples.next());
		}
		output.add(triples_set);			
		return output;		
	}
	

	private static List<Set<TriplePath>> visit(ElementUnion elementUnion,List<Set<TriplePath>> subQueries) {
		List<Set<TriplePath>> output=new ArrayList<Set<TriplePath>>();
		for(Element element:elementUnion.getElements()){
			output.addAll(visit(element,subQueries));
		}
		return output;
	}

	private static List<Set<TriplePath>> visit(ElementOptional elementOptional,List<Set<TriplePath>> subQueries) {
		List<Set<TriplePath>> output=new ArrayList<Set<TriplePath>>(); 
		Element element=elementOptional.getOptionalElement();
		output.addAll(visit(element,subQueries));
		return output;
	}

	private static List<Set<TriplePath>> visit(ElementGroup elementGroup,List<Set<TriplePath>> subQueries) {
		List<List<Set<TriplePath>>> subgraphs=new ArrayList<List<Set<TriplePath>>>();
		for(Element element:elementGroup.getElements()){
			subgraphs.add(visit(element,subQueries));
		}
		return merge(subgraphs);
	}

	private static List<Set<TriplePath>> merge(List<List<Set<TriplePath>>> disjunctiveQueries) {		
		List<Set<TriplePath>> output=new ArrayList<Set<TriplePath>>();
		int[] dimensions=new int[disjunctiveQueries.size()];
		for(int i=0;i<disjunctiveQueries.size();i++) 
			dimensions[i]=disjunctiveQueries.get(i).size();			
		
		int[][] combinations=getCombinations(dimensions);
		
		for(int i=0;i<combinations.length;i++) {
			HashSet<TriplePath> tmp=new HashSet<TriplePath>(); 
			for(int j=0;j<combinations[i].length;j++) {
				int index=combinations[i][j];
				tmp.addAll(disjunctiveQueries.get(j).get(index));
			}
			output.add(tmp);
		}
		
		return output;		
	}
	
	private static int[][] getCombinations(int... dimensions) {
		int total=1;
		for(int dim:dimensions) {
			total=total*dim;
		}
		int[][] output=new int[total][dimensions.length];				
		int totalDiairetis=1;
		int ypoloipo=1;
		int diairetis=1;
		for(int dimension=0;dimension<dimensions.length;dimension++) {
			ypoloipo=dimensions[dimension];
			totalDiairetis=totalDiairetis*dimensions[dimension];
			diairetis=totalDiairetis/dimensions[dimension];
			for(int i=0;i<total;i++) {			
				int value=(i/diairetis)%ypoloipo;
				output[i][dimension]=value;
			}
		}
		return output;
	}
	
	

}
