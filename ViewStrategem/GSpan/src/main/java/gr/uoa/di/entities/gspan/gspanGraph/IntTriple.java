package gr.uoa.di.entities.gspan.gspanGraph;

public class IntTriple implements Comparable<IntTriple> {
	private int left;
	private int middle;
	private int right;

	public int getLeft() {
		return left;
	}

	public int getMiddle() {
		return middle;
	}

	public int getRight() {
		return right;
	}

	public static IntTriple of(int left, int middle, int right) {
		IntTriple out = new IntTriple();
		out.left = left;
		out.middle = middle;
		out.right = right;
		return out;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + left;
		result = prime * result + middle;
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
		IntTriple other = (IntTriple) obj;
		if (left != other.left)
			return false;
		if (middle != other.middle)
			return false;
		if (right != other.right)
			return false;
		return true;
	}

	@Override
	public int compareTo(IntTriple o) {
		if (left > o.left)
			return 1;
		if (left < o.left)
			return -1;
		if (middle > o.middle)
			return 1;
		if (middle < o.middle)
			return -1;
		if (right > o.right)
			return 1;
		if (right < o.right)
			return -1;
		return 0;
	}

	@Override
	public String toString() {
		return "(" + left + ", " + middle + ", " + right + ")";
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public void setMiddle(int middle) {
		this.middle = middle;

	}

	public void setRight(int right) {
		this.right = right;
	}

}
