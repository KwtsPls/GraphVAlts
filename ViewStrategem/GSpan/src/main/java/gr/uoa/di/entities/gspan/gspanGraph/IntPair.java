package gr.uoa.di.entities.gspan.gspanGraph;

public class IntPair implements Comparable<IntPair>{
	private int left;
	private int right;
	
	public int getLeft() {
		return left;
	}
	
	public int getRight() {
		return right;
	}
	
	public static IntPair of(int left, int right) {
		IntPair out=new IntPair();
		out.left=left;
		out.right=right;
		return out;
	}

	@Override
	public String toString() {
		return "("+left+", "+right+")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + left;
		result = prime * result + right;
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
		IntPair other = (IntPair) obj;
		if (left != other.left)
			return false;
		if (right != other.right)
			return false;
		return true;
	}
	
	@Override
	public int compareTo(IntPair o) {
		if(left>o.left) return 1;
		if(left<o.left) return -1;
		if(right>o.right) return 1;
		if(right<o.right) return -1;
		return 0;
	}

}
