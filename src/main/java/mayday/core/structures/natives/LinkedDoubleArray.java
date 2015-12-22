package mayday.core.structures.natives;

public class LinkedDoubleArray extends mayday.core.structures.natives.mmap.MMDoubleArray {
//public class LinkedDoubleArray extends mayday.core.structures.natives.inmemory.LinkedDoubleArray {

	public LinkedDoubleArray(int blocksize) {
		super(blocksize);
	}

	public LinkedDoubleArray(LinkedDoubleArray other) {
		super(other);
	}

}
