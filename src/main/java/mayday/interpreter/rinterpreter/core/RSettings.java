package mayday.interpreter.rinterpreter.core;

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.MasterTable;
import mayday.core.Preferences;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.interpreter.rinterpreter.RDefaults;
import at.jta.RegistryErrorException;
import at.jta.Regor;
import at.jta.Key;

/**
 * Class that collects the setting.
 * 
 * @author Matthias
 *
 */
public class RSettings 
{
	public static int DEL_ASK=RDefaults.TempFiles.DEL_ASK;
	public static int DEL_NO=RDefaults.TempFiles.DEL_NO;
	public static int DEL_YES=RDefaults.TempFiles.DEL_YES;	
    
    private boolean active = true;
	
    private String rbinary;
    private String workingdir;
    private String logfile;
    private RSource source;
    private int deleteOutputFiles=RDefaults.TempFiles.DEL_ASK;
    private int deleteInputFiles=RDefaults.TempFiles.DEL_ASK;
    private int plotType=RDefaults.TempFiles.PNG_PLOT;
    private boolean showPlots=true;
    private long beginTimeStamp;
    private long endTimeStamp=-1;
    private File statusFile;
    
    public boolean silentRunning = false; // (fb) true if used by RPlugin.runInternal 
    
    private MasterTable masterTable;
    private List<ProbeList> probeLists;
    private List<ProbeList> returnList=null;
    private List<MIGroup> miolist=null;
    private List<MIGroupSelection<MIType>> mioSelection=null;
    private Map<String, Integer> mioTypes=null;
    private Map<Integer, String> mioTypes2=null;
    
    //private Semaphore semaphore=new Semaphore(0);
    
//    public void releaseWaiters()
//    {
//        semaphore.release();
//    }
    
//    public void waitFor()
//    {
//        try
//        {
//            this.semaphore.acquire();
//            
//        }catch(InterruptedException ex)
//        {;}
//    }

    public List<MIGroupSelection<MIType>> getMIOSelection()
    {
        if(this.mioSelection==null)
        {
            this.mioSelection=new ArrayList<MIGroupSelection<MIType>>();
        }
        return this.mioSelection;
    }
    
    public List<MIGroup> getMIOGroups()
    {
        if(this.miolist==null)
        {
            this.miolist=new ArrayList<MIGroup>();
        }
        return this.miolist;
    }

    public long getBeginTimeStamp()
    {
        return beginTimeStamp;
    }
 
    public void setBeginTimeStamp(long beginTimeStamp)
    {
        this.beginTimeStamp = beginTimeStamp;
    }
    public String getBinary()
    {
		// if the binary is not set, try to find it.
		if (rbinary==null || rbinary.equals("") || !(new File(rbinary).exists())) {
			setBinary(findRBinary());
			System.out.println("RSettings: R binary was found at  "+rbinary);		
		}
        return this.rbinary;
    }

    public void setBinary(String filename)
    {
        this.rbinary=filename;
    }

    public String getWorkingDir()
    {
        return this.workingdir;
    }

    public void setWorkingDir(String directory)
    {
        this.workingdir=directory;
    }
    
    public void setLogFilename(String filename)
    {
    	this.logfile=filename;
    }
    
    public String getLogFilename()
    {
    	return this.logfile;
    }

    public void setSource(RSource src)
    {
        this.source=src;
    }

    public RSource getSource()
    {
        return source;
    }
    
    public String toString()
    {
    	return
    	"R-binary:  ............"+this.rbinary+"\n"+
    	"WorkingDir:  .........."+this.workingdir+"\n"+
    	"LogFile:  ............."+this.logfile+"\n"+
    	"Selected Function: ...."+this.source.getDescriptor()+"\n"+
    	"Function call: ........"+this.source.functionString();
    }
    
    /*
    public void setFunction(RFunction fun)
    {
    	this.function=fun;
    }//*/
    
    /*
    public RFunction getFunction()
    {
    	return this.function;
    }//*/
    
	/**
	 * @return
	 */
	public int deleteInputFiles()
	{
		return this.deleteInputFiles;
	}

	/**
	 * @param b
	 */
	public void setDeleteInputFiles(int i)
	{
		this.deleteInputFiles=i;
	}

	/**
	 * @return
	 */
	public int deleteOutputFiles()
	{
		return this.deleteOutputFiles;
	}

	/**
	 * @param b
	 */
	public void setDeleteOutputFiles(int i)
	{
		this.deleteOutputFiles=i;
	}
	
	public static String findRBinary() {
		// Windows: HKEY_LOCAL_MACHINE\Software\R-core\R
		// Linux,MacOS: `which R`
		String os = System.getProperty("os.name");
		if (os==null) return "";
		
		if (os.toLowerCase().contains("windows")) {
			try {
				Regor registry = new Regor();
				Key key = registry.openKey(Regor.HKEY_LOCAL_MACHINE, "Software\\R-core\\R",Regor.KEY_READ);
				String value = Regor.parseValue( registry.readValue(key, "InstallPath"));
				return value+File.separator+"bin"+File.separator+"R.exe";
			} catch (RegistryErrorException e) {
				System.err.println("Could not read R executable path from the registry:\n"+e.getMessage());
				e.printStackTrace();
			}
			
		} else {
			try {
				Process process = Runtime.getRuntime().exec("which R");
				StreamReader reader = new StreamReader(process.getInputStream());
			    reader.start();
			    process.waitFor();
			    reader.join();
			    return reader.getResult().replace("\n", "");
			} catch (Exception e) {
				System.err.println("Could not get R executable path via `which R`:\n"+e.getMessage());
				e.printStackTrace();
			}
		}
		return "";
	}
	
	private static class StreamReader extends Thread {
	    private InputStream is;
	    private StringWriter sw;

	    StreamReader(InputStream is) {
	      this.is = is;
	      sw = new StringWriter();
	    }

	    public void run() {
	      try {
	        int c;
	        while ((c = is.read()) != -1)
	          sw.write(c);
	        }
	        catch (Exception e) { ; }
	      }

	    String getResult() {
	      return sw.toString();
	    }
	  }
	
	/**
	 *  This method is used to create an instance of RSettings to be
	 *  invoked in a quiet session of the <i>R for Mayday</i> interface.
	 *  That means all GUI capabilities of this plug-in are turned off.
	 *  <br>
	 *  This method is needed when the R-interpreter should be invoked
	 *  within other <i>Mayday</i> plug-ins. 
	 * 
	 *  @param  rsource An object of type RSource.
	 *  @return Rettings A new instance of class RSettings initialized by
	 *          the given RSource object and the settings stored in the
	 *          <i>R for Mayday</i> plug-in's preferrences.
	 */
	public static RSettings createInitializedInstance(RSource rsource)
	{
		Preferences prefs=RDefaults.getPrefs();
		RSettings settings=new RSettings();
		
		settings.setBinary(
			prefs.get(RDefaults.Prefs.BINARY_KEY,null)
		);
		settings.setLogFilename(
			prefs.get(RDefaults.Prefs.LOGFILE_KEY,null)
		);
				
		settings.setSource(rsource);  
		
		settings.setDeleteInputFiles(
			prefs.getInt(
				RDefaults.Prefs.DELETEINPUTFILES_KEY,
				RSettings.DEL_YES
			)
		);
		settings.setDeleteOutputFiles(
			prefs.getInt(
				RDefaults.Prefs.DELETEOUTPUTFILES_KEY,
				RSettings.DEL_YES
			)
		);
		
		settings.setWorkingDir( // (fb) this was missing
			prefs.get(
					RDefaults.Prefs.WORKINGDIR_KEY,
					RDefaults.Prefs.WORKINGDIR_DEFAULT)
			);
		
		settings.setPlotType(
		    prefs.getInt(
		        RDefaults.Prefs.PLOT_TYPE_KEY,
		        RDefaults.TempFiles.PNG_PLOT
		    )
		);
		
		return settings;
	}

    /**
     * @return Returns the plotType.
     */
    public int getPlotType()
    {
        return plotType;
    }
    /**
     * @param plotType The plotType to set.
     */
    public void setPlotType(int plotType)
    {
        this.plotType = plotType;
    }
    /**
     * @return Returns the showPlots.
     */
    public boolean isShowPlots()
    {
        return showPlots;
    }
    /**
     * @param showPlots The showPlots to set.
     */
    public void setShowPlots(boolean showPlots)
    {
        this.showPlots = showPlots;
    }
    public File getStatusFile()
    {
        return statusFile;
    }
    public void setStatusFile(File pidFile)
    {
        this.statusFile = pidFile;
    }
    public void setEndTimeStamp(long endTimeStamp)
    {
        this.endTimeStamp = endTimeStamp;
    }
    public long getDuration()
    {
        return this.endTimeStamp<0? 
                -1:
                this.endTimeStamp-this.beginTimeStamp;
    }

    public MasterTable getMasterTable()
    {
        return masterTable;
    }
    public void setMasterTable(MasterTable masterTable)
    {
        this.masterTable = masterTable;
    }
    public List<ProbeList> getProbeLists()
    {
        return probeLists;
    }
    public void setProbeLists(List<ProbeList> probeLists)
    {
        this.probeLists = probeLists;
    }
    public List<ProbeList> getReturnList()
    {
        return returnList;
    }
    public void setReturnList(List<ProbeList> returnList)
    {
        this.returnList = returnList;
    }
    
    
    public Map<String, Integer> getMioTypes()
    {
        return mioTypes;
    }
    synchronized public void setMioTypes(Map<String, Integer> mioTypes)
    {
        this.mioTypes = mioTypes;
        this.mioTypes2=new HashMap<Integer, String>();
        for(String s:mioTypes.keySet())
        {
            this.mioTypes2.put(this.mioTypes.get(s),s);
        }
    }
    public Map<Integer, String> getMioTypes2()
    {
        return this.mioTypes2;
    }
    
    public boolean isActive()
    {
        return active;
    }
    
    public void activate()
    {
        active = true;
    }
    
    public void inactivate()
    {
        active = false;
    }
    
}



