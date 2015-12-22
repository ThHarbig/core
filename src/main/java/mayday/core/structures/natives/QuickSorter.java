package mayday.core.structures.natives;



public abstract class QuickSorter<Container, Element> {

	Container x;

	public QuickSorter(Container objectToSort) {
		x = objectToSort;		
	}

	public Container sort() {
		sort1(0, size(x));
		return x;
	}

	protected abstract long size(Container container);
	protected abstract int compareElements(Element e1, Element e2);
	protected abstract Element getElement(Container container, long index);
	protected abstract void swapElements(Container container, long i1, long i2);

	protected final int compareElements(Container container, long i, Element e2) {
		return compareElements(getElement(container, i), e2);
	}
	
	protected final int compareElements(Container container, long i1, long i2) {
		return compareElements(getElement(container, i1), getElement(container, i2));
	}

	// partly taken from 
	
	
	private void sort1(long off, long len) {
//Arrays.sort()
		if (len < 7) {
		    for (long i=off; i<len+off; i++)
			for (long j=i; j>off && compareElements(x, j-1, j)>0; j--)
			    swapElements(x, j, j-1);
		    return;
		}
		
		// Choose a partition element, v
		long m = off + (len >> 1);       // Small arrays, middle element
		if (len > 7) {
			long l = off;
			long n = off + len - 1;
			if (len > 40) {        // Big arrays, pseudomedian of 9
				long s = len/8;
				l = med3(l,     l+s, l+2*s);
				m = med3(m-s,   m,   m+s);
				n = med3(n-2*s, n-s, n);
			}
			m = med3(l, m, n); // Mid-size, med of 3
		}
		Element v = getElement(x,m);

		// Establish Invariant: v* (<v)* (>v)* v*
		long a = off, b = a, c = off + len - 1, d = c;
		while(true) {
			while (b <= c && compareElements(x,b,v)<=0) {
				if (compareElements(x,b,v)==0)
					swapElements(x,a++, b);
				b++;
			}
			while (c >= b && compareElements(x,c,v)>=0) {
				if (compareElements(x,c,v)==0)
					swapElements(x,c, d--);
				c--;
			}
			if (b > c)
				break;
			swapElements(x,b++, c--);
		}

		// Swap partition elements back to middle
		long s, n = off + len;
		s = Math.min(a-off, b-a  );  vecswap(off, b-s, s);
		s = Math.min(d-c,   n-d-1);  vecswap(b,   n-s, s);

		// Recursively sort non-partition-elements
		if ((s = b-a) > 1)
			sort1(off, s);
		if ((s = d-c) > 1)
			sort1(n-s, s);
	}

	/**
	 * Swaps x[a .. (a+n-1)] with x[b .. (b+n-1)].
	 */
	private void vecswap(long a, long b, long n) {
		for (int i=0; i<n; i++, a++, b++)
			swapElements(x,a, b);
	}

	/**
	 * Returns the index of the median of the three indexed elements.
	 */
	private long med3(long a, long b, long c) {		
		boolean a_less_b = compareElements(x,a,b)<0;
		if (a_less_b) {
			boolean b_less_c = compareElements(x,b,c)<0;
			if (b_less_c) 
				return b;
			else {
				boolean a_less_c = compareElements(x,a, c)<0;
				if (a_less_c)
					return c;
				else
					return a;
			}
		} else {
			boolean b_greater_c = compareElements(x,b, c)>0;
			if (b_greater_c)
				return b;
			else {
				boolean a_greater_c = compareElements(x,a, c)>0;
				if (a_greater_c)
					return c;
				else
					return a;
			}
		}		
		
//		return (x[a] < x[b] ?
//				(x[b] < x[c] ? b : x[a] < x[c] ? c : a) :
//					(x[b] > x[c] ? b : x[a] > x[c] ? c : a));
	}

	
//	public static void main(String[] args) {
//		DoubleVector d = new DoubleVector(new ConstantIndexVector(100000,0).toArrayUnpermuted());
//		DoubleVector c = d.clone();
//		d.permute(new Random());
//		
//		long mbefore = System.currentTimeMillis();
//		new QuickSorter<DoubleVector, Double>(d) {
//
//			@Override
//			protected long size(DoubleVector x) {
//				return x.size();
//			}
//
//			@Override
//			protected void swapElements(DoubleVector x, long i1, long i2) {
//				Double d1 = x.get((int)i1);
//				x.set((int)i1, x.get((int)i2));
//				x.set((int)i2, d1);
//			}
//
//			@Override
//			protected Double getElement(DoubleVector container, long index) {
//				return container.get((int)index);
//			}
//
//			@Override
//			protected int compareElements(Double e1, Double e2) {
//				return e1.compareTo(e2);
//			}			
//		}.sort();
//		long mafter = System.currentTimeMillis();
//		System.out.println("QuickSorter took "+(mafter-mbefore)+" ms");
//		
//		
//		
//		System.out.println(c.allValuesEqual(d));
//		
//	}

}
