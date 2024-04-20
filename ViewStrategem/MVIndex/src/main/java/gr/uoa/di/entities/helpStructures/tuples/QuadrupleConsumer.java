package gr.uoa.di.entities.helpStructures.tuples;

public interface QuadrupleConsumer<U,V,W,Z> {
	void accept(U u,V v, W w, Z z);
}
