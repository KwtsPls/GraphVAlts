package gr.uoa.di.entities.helpStructures.tuples;

public class MyTriple<K,M,R> { // NO_UCD (unused code)
	

	private K l;
	private M m;
	private R r;
	
	public static <K,M,R> MyTriple<K,M,R> of(K left,M middle, R right){
		MyTriple<K,M,R>out=new MyTriple<K,M,R>();
		out.l=left;
		out.m=middle;
		out.r=right;
		return out;
	}

	public R getRight() {
		return r;
	}
	
	public M getMiddle() {
		return m;
	}
	
	public K getLeft() {
		return l;
	}

	public void setLeft(K left) {
		this.l=left;
	}
	
	public void setMiddle(M middle) {
		this.m = middle;
	}
	
	public void setRight(R right) {
		this.r=right;
	}
	
	@Override
	public String toString() {
		return new StringBuffer("[").append(l.toString()).append(",").append(m.toString()).append(",").append(r.toString()).append("]").toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((l == null) ? 0 : l.hashCode());
		result = prime * result + ((m == null) ? 0 : m.hashCode());
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
		MyTriple other = (MyTriple) obj;
		if (l == null) {
			if (other.l != null)
				return false;
		} else if (!l.equals(other.l))
			return false;
		if (m == null) {
			if (other.m != null)
				return false;
		} else if (!m.equals(other.m))
			return false;
		if (r == null) {
			if (other.r != null)
				return false;
		} else if (!r.equals(other.r))
			return false;
		return true;
	}
	
}
