package mayday.core.structures.natives.inmemory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unchecked")
public class AbstractLinkedArray<ArrayType> {
	 
	protected ArrayType first_block; // for small lists
	protected List<ArrayType> linked_arrays;
	protected int blocksize;
	protected long length;
	
	public AbstractLinkedArray(int blocksize) {
		this.blocksize=blocksize;		
		first_block = makeBlock();
		linked_arrays = makeList();
	}
	
	public AbstractLinkedArray(AbstractLinkedArray<ArrayType> other) {
		cloneFrom(other);
	}
	
	public void cloneFrom(AbstractLinkedArray<ArrayType> other) {
		blocksize = other.blocksize;
		
		first_block = makeBlock();
		System.arraycopy(other.first_block, 0, first_block, 0, blocksize);
		
		linked_arrays.clear();
		for (int i=0; i!=other.linked_arrays.size(); ++i) {
			ArrayType ll = other.linked_arrays.get(i);
			ArrayType lc = makeBlock();
			System.arraycopy(ll, 0, lc, 0, blocksize);
			linked_arrays.add(lc);
		}		
		length= other.length;
	}
	
	public long size() {
		return length;
	}
	
	public void removeLast() {
		removeLast(1);		
	}

	public void removeLast(int n) {
		trimToSize(length-n);		
	}
	
	public ArrayType makeBlock() {
		return (ArrayType)new Object[blocksize];
	}
	
	public List<ArrayType> makeList() {
		return new ArrayList<ArrayType>();
	}
	
	protected String toString(ArrayType part) {
		return Arrays.toString((Object[])part);
	}
	
	public String toString() {
		String s = "{";
		s+=toString(first_block);
		for (ArrayType la : linked_arrays)
			s = s+toString(la);
		s+="}";
		return s;
	}

	protected int blockCount() {
		return linked_arrays.size()+1;
	}
	
	public void increaseSizeBy(long len) {
		length+=len;		
		while(blockCount()*blocksize < length)
			linked_arrays.add(makeBlock());
	}
	
	public void trimToSize(long size) {
		int lastBlock = (int)(size/blocksize);
		while (blockCount()>(lastBlock+1) && !linked_arrays.isEmpty())
			linked_arrays.remove(lastBlock+1);
	}
	
	public void ensureSize(long size) {
		long missing = size-length;
		if (missing>0)
			increaseSizeBy(missing);
	}

}
