package gr.uoa.di.entities.graph;

import java.util.function.Function;

public interface Printable {
	
	default String toIdString() {
		return print(p->p.toIdString());
	}
	
	default String toCompactString() {
		return print(p->p.toCompactString());
	}
	
	default String toFullString() {
		return print(p->p.toFullString());
	}
	
	String print(Function<Printable,String> function);
	
}
