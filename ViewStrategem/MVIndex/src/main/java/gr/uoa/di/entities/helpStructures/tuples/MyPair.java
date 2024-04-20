package gr.uoa.di.entities.helpStructures.tuples;

public class MyPair<K,V> {
	

	private K l;
	private V r;
	
	public static <K,V> MyPair<K,V> of(K left, V right){
		MyPair<K,V> out=new MyPair<K,V>();
		out.l=left;
		out.r=right;
		return out;
	}

	public V getRight() {
		return r;
	}
	
	public K getLeft() {
		return l;
	}

	public void setLeft(K left) {
		this.l=left;
	}
	
	public void setRight(V right) {
		this.r=right;
	}
	
	@Override
	public String toString() {
		return new StringBuffer("[").append(l.toString()).append(",").append(r.toString()).append("]").toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((l == null) ? 0 : l.hashCode());
		result = prime * result + ((r == null) ? 0 : r.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		MyPair other = (MyPair) obj;
		if (l == null) {
			if (other.l != null)
				return false;
		} else if (!l.equals(other.l))
			return false;
		if (r == null) {
			if (other.r != null)
				return false;
		} else if (!r.equals(other.r))
			return false;
		return true;
	}
	
}
