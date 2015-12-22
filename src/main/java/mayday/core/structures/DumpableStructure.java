package mayday.core.structures;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/** This interface should have been implemented by many classes in the mayday.core.structures.natives.mmap packages.
 * Since debugging was such a drag, I commented all the functions out for the time being. 
 * Find them by grepping for DumpableStructure in the source folder :) 
 * @author battke
 *
 */
public interface DumpableStructure {

	public void writeDump(DataOutputStream dos) throws IOException;	
	public void readDump(DataInputStream dis) throws IOException;

}
