package mayday.mpf.filters;

import java.io.File;
import java.io.FilenameFilter;
import mayday.interpreter.rinterpreter.RDefaults;
import mayday.mpf.options.OptString;
import mayday.mpf.FilterBase;

/** @author Florian Battke */
public class MoveRPlots extends FilterBase {

	private OptString targetPrefix = new OptString("Target prefix","The value to put in for zzzzzz","movedRplot");
	
	public MoveRPlots() {
		super(1,0);
		
		pli.setName("Move R Plots");
		pli.setIdentifier("PAS.mpf.moveRplots");
		pli.replaceCategory("R interpreter");
		pli.setAuthor("Florian Battke");
		pli.setEmail("battke@informatik.uni-tuebingen.de");
		pli.setAbout("Moves R plots from RplotXXX.* to INPUTNAME_zzzzzzXXX.* to prevent one R invocation from " +
				"overwriting plots of a previous invocation in processing pipelines, where zzzzzz is a name " +
				"selected by the user. Only the default RPlugin working directory is checked.");
		
		Options.add(targetPrefix);
	}
	

	public void execute() {
		String srcPath = 
			RDefaults.getPrefs().get(
				RDefaults.Prefs.WORKINGDIR_KEY,
				RDefaults.Prefs.WORKINGDIR_DEFAULT
		);

		File srcDir = new File(srcPath);
		
		File[] candidates = srcDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith("Rplot");
				}
			}
		);
		
		for (File f : candidates) {
			String newname = f.getParent()+File.separator
					+InputData[0].getName()+'_'+f.getName().replace("Rplot",targetPrefix.Value);
			f.renameTo(new File(newname));
		}

		
	}

}
