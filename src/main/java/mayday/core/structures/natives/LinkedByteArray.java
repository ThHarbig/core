package mayday.core.structures.natives;

public class LinkedByteArray extends mayday.core.structures.natives.mmap.MMIntArray {
//public class LinkedIntArray extends mayday.core.structures.natives.inmemory.LinkedIntArray {

	public LinkedByteArray(int blocksize) {
		super(blocksize);
	}

	public LinkedByteArray(LinkedByteArray other) {
		super(other);
	}

}
