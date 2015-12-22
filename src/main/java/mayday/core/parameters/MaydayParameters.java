package mayday.core.parameters;

import java.util.List;

public class MaydayParameters extends Parameters {

	public MaydayParameters(String[] params) {
		super(params);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void init() {
		
		usage="Mayday [options]* [snapshot files]*";
				
		addParameter(new Parameter("debug",
						"-debug                 start Mayday in debug mode (verbose output to stdout)"));				
		
		addParameter(new Parameter("nosplash",
						"-nosplash              do not show the splash screen while starting"));

		addParameter(new Parameter("run",-1,true,
				        "-run <plugID>*         run the plugins with given plugin ids",null,null));
		
		addParameter(new Parameter("last",
		        		"-last                  open the most-recently-used session"));
		
		addParameter(new Parameter("nowin",
						"-nowin                 do not show the main window. Useful in combination with -run"));
		
//		addParameter(new Parameter("pluginpath",1,true,
//						"-pluginpath <path>     use another pluginpath than specified in the settings",null,null));
//		
//		addParameter(new Parameter("basepath",1,true,
//						"-basepath <path>       use another base directory (defaults to user's home directory)",null,null));

	}
	
	public boolean isDebugMode() {
		return getParameter("debug").isPresent();
	}
	
	public boolean noSplash() {
		return getParameter("nosplash").isPresent();		
	}
	
	public List<String> getFiles() {
		return getUnclaimedParameters();
	}
	
	public String[] getPluginsToRun() {
		return getParameter("run").getValues();
	}
	
	public boolean openMRU() {
		return getParameter("last").isPresent();
	}
	
	public boolean showMain() {
		return !getParameter("nowin").isPresent();
	}
	
}
