package mayday.core.structures.maps;

@SuppressWarnings("unchecked")
public class TwoKey<K1 extends Comparable, K2 extends Comparable> implements Comparable<TwoKey<K1,K2>> {

	protected K1 k1;
	protected K2 k2;

	public TwoKey(K1 k1, K2 k2) {
		this.k1 = k1;
		this.k2 = k2;
	}

	public int compareTo(TwoKey<K1,K2> o) {
		int i = k1.compareTo(o.k1);
		if (i!=0)
			return i;
		else
			return k2.compareTo(o.k2);
	}
	
	public String toString() {
		return "<"+k1.toString()+","+k2.toString()+">";
	}
	
	public boolean equals(Object o) {
		if (o instanceof TwoKey)
			return compareTo((TwoKey)o)==0;
		return super.equals(o);
	}
	
	public int hashCode() {
		return (k1!=null?k1.hashCode():0 )+ (k2!=null?k2.hashCode():0);
	}
	
	public K1 getK1() {
		return k1;
	}
	
	public K2 getK2() {
		return k2;
	}
	
}