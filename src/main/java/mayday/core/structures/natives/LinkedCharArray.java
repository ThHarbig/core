package mayday.core.structures.natives;

public class LinkedCharArray extends mayday.core.structures.natives.mmap.MMCharArray {
//public class LinkedCharArray extends mayday.core.structures.natives.inmemory.LinkedCharArray {
	
	public LinkedCharArray(int blocksize) {
		super(blocksize);
	}
	
	public LinkedCharArray(int blocksize, boolean wideChar) {
		super(blocksize, wideChar);
	}

	
	public LinkedCharArray(LinkedCharArray other) {
		super(other);
	}

}
