package mayday.core.structures;

public class Triple<
	One extends Comparable<One>,
	Two extends Comparable<Two>,
	Three extends Comparable<Three>
> implements Comparable<Triple<One, Two, Three>> {

	protected One ONE;
	protected Two TWO;
	protected Three THREE;
	
	public Triple(One one, Two two, Three three) {
		ONE = one;
		TWO = two;
		THREE = three;
	}
	
	public String toString() {
		return "<"+ONE+","+TWO+","+THREE+">";
	}
	
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (o instanceof Triple)
			return compareTo((Triple)o)==0;
		else
			return super.equals(o);
	}
	
	public int hashCode() {
		return (ONE.hashCode()*37 + TWO.hashCode())*37 + THREE.hashCode();
	}

	public void set(One one, Two two, Three three) {
		ONE = one;
		TWO = two;
		THREE = three;
	}
	
	public One getFirst() {
		return ONE;
	}
	
	public Two getSecond() {
		return TWO;
	}
	
	public Three getThird() {
		return THREE;
	}

	public int compareTo(Triple<One, Two, Three> o) {
		int c1 = 0;
		
		if (ONE!=null && o.ONE!=null)
			c1 = ONE.compareTo(o.ONE);
		if (c1==0 && TWO!=null && o.TWO!=null)
			c1 = TWO.compareTo(o.TWO);
		if (c1==0 && THREE!=null && o.THREE!=null)
			c1 = THREE.compareTo(o.THREE);
		
		return c1;	}
	
	
}
