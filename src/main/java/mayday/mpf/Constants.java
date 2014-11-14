package mayday.mpf;

import java.io.File;

import mayday.core.pluma.PluginManager;

/**
 * A few constants that are used throughout the MPF and that we might want to change without going through
 * all source files
 * @author Florian Battke
 */
public class Constants {
	
	public final static String FILEEXT = ".mpd";
	public final static String FILEEXT_REGEX = ".*[.]mpd";
	public final static String masterComponent = "MPF";
	
	public final static String FILTERPATH = 
		PluginManager.getInstance().getPluginRoot()
		+File.separator
		+"mpf_modules"
		+File.separator;
	
	public final static String NOT_IN_MENU="MPF-hideFromMenu";
	
	public final static String ComplexFilterID = "PAS.mpf.complexfilter";

	public final static class Applicator {
		public static final String WINDOWCAPTION = "MPF Applicator";	
	}
	
	public final static class Designer {
		public static final String WINDOWCAPTION = "MPF Designer";
	}
	
}
