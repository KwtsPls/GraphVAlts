package gr.uoa.di.entities.graph.regular.helpers;

import org.apache.jena.graph.Node_Literal;

public class CompactString{

	
	public static String apply(Object object) {
		if(org.apache.jena.graph.Node_URI.class.isInstance(object)) {
			String output=object.toString();
			String[] outputs=output.split("/");			
			output=outputs[outputs.length-1];
			outputs=output.split("#");
			output=outputs[outputs.length-1];
			return output;			
		}else if(Node_Literal.class.isInstance(object)){			
			Node_Literal literal=((Node_Literal)object);
			String literalString = literal.getLiteralValue().toString();
			if(literalString.length()<12) {
				return "'"+literalString+"'";
			}else {
				return "'"+literalString.substring(0, 10)+"..'";
			}
			
		}else {
			
			return object.toString();
		}
		
	}

}
