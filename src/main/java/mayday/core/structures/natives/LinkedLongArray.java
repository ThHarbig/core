package mayday.core.structures.natives;

public class LinkedLongArray extends mayday.core.structures.natives.mmap.MMLongArray {
//public class LinkedLongArray extends mayday.core.structures.natives.inmemory.LinkedLongArray {

	public LinkedLongArray(int blocksize) {
		super(blocksize);
	}
	
	public LinkedLongArray(LinkedLongArray other) {
		super(other);
	}

}
