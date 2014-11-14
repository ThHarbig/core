package mayday.core.structures.natives;

public class LinkedIntArray extends mayday.core.structures.natives.mmap.MMIntArray {
//public class LinkedIntArray extends mayday.core.structures.natives.inmemory.LinkedIntArray {

	public LinkedIntArray(int blocksize) {
		super(blocksize);
	}

	public LinkedIntArray(LinkedIntArray other) {
		super(other);
	}

}
