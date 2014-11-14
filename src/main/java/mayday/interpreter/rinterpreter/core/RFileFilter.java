package mayday.interpreter.rinterpreter.core;

import javax.swing.filechooser.*;
import java.io.File;

/**
 * The FileFilter for R source files.<br>
 * Accept any file with the extention ".R" or ".r"
 * and directories to display them in the 
 * <tt>JFileChooser</tt> dialog.
 * 
 * 
 * @author Matthias
 *
 */
public final class RFileFilter extends FileFilter
{
	public boolean accept(File f)
	{
		return (f.isFile() && 
				(f.getName().endsWith(".R")||f.getName().endsWith(".r")))
				|| f.isDirectory();
	}
	
	public String getDescription()
	{
		return new String("R Source Files (*.R, *.r)");
	}
}