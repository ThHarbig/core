package mayday.core.structures;


public class Pair<One extends Comparable<One>,Two extends Comparable<Two>> implements Comparable<Pair<One, Two>> {

	protected One ONE;
	protected Two TWO;
	
	public Pair(One one, Two two) {
		ONE = one;
		TWO = two;
	}
	
	public String toString() {
		return "<"+ONE+","+TWO+">";
	}
	
	public int compareTo(Pair<One, Two> o) {
		int c1 = 0;
		if (ONE!=null && o.ONE!=null)
			c1 = ONE.compareTo(o.ONE);
		if (c1==0 && TWO!=null && o.TWO!=null)
			c1 = TWO.compareTo(o.TWO);
		
		return c1;
	}
	
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (o instanceof Pair)
			return compareTo((Pair)o)==0;
		else
			return super.equals(o);
	}
	
	public int hashCode() {
		int h  = ONE.hashCode()*37 + TWO.hashCode();
		return h;
	}

	public void set(One one, Two two) {
		ONE = one;
		TWO = two;
	}
	
	public One getFirst() {
		return ONE;
	}
	
	public Two getSecond() {
		return TWO;
	}
	
	
}
