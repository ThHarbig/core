package mayday.core.structures.natives;




/** Abstract class for O(n log n) in-place sorting of any kind of object. Derived classes must implement accessors 
 * to the object that should be sorted. 
 * @author battke
 * @param <Container> container object type
 * @param <Content> content object type
 */
public abstract class HeapSorter<Container,Content> {

	Container myObject;
	
	public HeapSorter(Container objectToSort) {
		myObject = objectToSort;		
	}
	
	public Container sort() {
		heapsort();
		return myObject;
	}
	
	protected abstract long size();
	
	protected abstract Content getElement(long index);
	protected abstract void setElement(long index, Content content);
	protected abstract int compareElements(long i1, long i2);
	
	public void heapsort() {
		heapify();
		long end = size()-1;
		while (end>0) {
			swapReferences(end, 0);
			shiftDown(0,--end);
		}		
	}
	
	protected void heapify() {
		long start = size()-2/2;
		long count = size();
		while (start>=0) {
			shiftDown(start--, count-1);
		}
	}
	
	protected void shiftDown(long start, long end) {
		long root = start;
		while (leftChild(root)<=end) {
			long child = leftChild(root);
			if (child+1 <= end && compareElements(child, child+1)<0)
				child++;
			if (compareElements(root, child)<0) {
				swapReferences(root, child);
				root = child;
			} else
				return;
		}
	}

	/**
	 * Internal method for heapsort.
	 * @param i the index of an item in the heap.
	 * @return the index of the left child.
	 */
	private static long leftChild( long i ) {
		return 2 * i + 1;
	}




	/**
	 * Method to swap to elements in an array.
	 * @param a an array of objects.
	 * @param index1 the index of the first object.
	 * @param index2 the index of the second object.
	 */
	private final void swapReferences( long index1, long index2 ) {
		Content tmp = getElement(index1);
		setElement(index1, getElement(index2));
		setElement(index2, tmp);	
	}
	
	
//	public static void main(String[] args) {
//		DoubleVector d = new DoubleVector(new ConstantIndexVector(1000000,0).toArrayUnpermuted());
//		DoubleVector c = d.clone();
//		d.permute(new Random());
//		long mbefore = System.currentTimeMillis();
//		new HeapSorter<DoubleVector, Double>(d) {
//
//			@Override
//			protected int compareElements(long i1, long i2) {
//				return getElement(i1).compareTo(getElement(i2));
//			}
//
//			@Override
//			protected Double getElement(long index) {
//				return myObject.get((int)index);
//			}
//
//			@Override
//			protected void setElement(long index, Double content) {
//				myObject.set((int)index, content);
//			}
//
//			@Override
//			protected long size() {
//				return myObject.size();
//			}			
//		}.sort();
//		long mafter = System.currentTimeMillis();
//		System.out.println("HeapSorter took "+(mafter-mbefore)+" ms");
//		
//		System.out.println(c.allValuesEqual(d));
//		
//	}

}
