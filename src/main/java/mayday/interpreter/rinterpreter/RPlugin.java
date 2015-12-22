package mayday.interpreter.rinterpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.BackingStoreException;

import javax.swing.JOptionPane;

import mayday.core.MasterTable;
import mayday.core.ProbeList;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.gui.PreferencePane;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.interpreter.rinterpreter.core.RJob;
import mayday.interpreter.rinterpreter.core.RResultParser;
import mayday.interpreter.rinterpreter.core.RSettings;
import mayday.interpreter.rinterpreter.exceptions.RInternalException;
import mayday.interpreter.rinterpreter.gui.FunctionParameterDialog;
import mayday.interpreter.rinterpreter.gui.GraphicsFileFilter;
import mayday.interpreter.rinterpreter.gui.RPlotFrame;
import mayday.interpreter.rinterpreter.gui.RSettingsDialog;
import mayday.interpreter.rinterpreter.gui.SourceComponent;


/**
 * RPlugin is a Plugin for Mayday
 * following the related specification.
 * It applies R functions to expression data.
 * 
 * This plug-in can be run in two modi:
 * <ul>
 *   <li> Dialog mode: The settings are collected via
 *        specific dialogs. A run of the R interpreter
 *        needs user interaction.
 *   </li>
 *   <li> Internal mode: The settings are computed by a
 *        caller process. No user interaction is needed
 *        within this plugin. No dialogs will be shown
 *        during the run of this plug-in (except the
 *        RProgressDialog2) 
 *   </li> 
 * </ul>
 * 
 * @author Matthias Zschunke
 * @version 1.2
 *
 */
public class RPlugin extends AbstractPlugin implements ProbelistPlugin
{
  /*
   * A package-private instance used in RDefaults.
   */
  static RPlugin sharedInstance = null;
 
  public RPlugin()
  {
      sharedInstance = this;      
  }

    public void init() {
    	new SourceComponent().initializeSources();
    }

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.Rinterpreter",
				new String[]{"LIB.Batik","LIB.Regor"},
				Constants.MC_PROBELIST,
				new HashMap<String, Object>(),
				"Matthias Zschunke, Janko Dietzsch, Kay Nieselt",
				"zschunke@informatik.uni-tuebingen.de",
				"Applying R functions to expression data.<br><br>" +
				"ZBIT (Zentrum fuer Bioinformatik Tuebingen)<br>" +
				"Fakultaet f. Informations- und Kognitionswissenschaften<br>" +
				"Universitaet Tuebingen<br>" +
				"Sand 14<br>" +
				"72076 Tuebingen",
				"R interpreter"
				);		
		pli.setIcon(RDefaults.RLOGO_ICON);
		return pli;
	}




  
  /**
   * Create a
   *//*
  public JMenuItem createMenuItem(
          final String category,
          final PluginManager manager, 
          final List probeLists, 
          final ProbeListManager probeListManager)
  {
      if(!category.equals(MaydayDefaults.Plugins.CATEGORY_DATAIMPORT))
      try
      {
          final RSettings settings = new RSettings();
          settings.setProbeLists(probeLists);
          settings.setMasterTable(probeListManager.getDataSet().getMasterTable());
          settings.inactivate();
          
          JMenu menu = new JMenu("R Functions");
          menu.setIcon(RDefaults.RLOGO_ICON);
          Preferences prefs=RDefaults.getPrefs().node(RDefaults.Prefs.SOURCES_NODE);
          if(prefs.keys()!=null && prefs.keys().length>0)
          {
              String[] keys = prefs.keys();
              Arrays.sort(keys,new Comparator<String>() {
                public int compare(String o1, String o2)
                {
                    return o1.compareToIgnoreCase(o2);
                }});
              for(String k : keys)
              {
                  menu.add(new JMenuItem(new RunPluginAction0(k,manager,RPlugin.this,probeLists,probeListManager) {
                      public void actionPerformed(ActionEvent e)
                      {
                          RSettings l_settings = settings;
                          try
                          {
                              RSource source = new RSource(new File(
                                  RDefaults.getPrefs().node(RDefaults.Prefs.SOURCES_NODE).get(
                                      (String)this.getValue(AbstractAction.NAME), 
                                      null)
                              ));
                              settings.setSource(source);
                              initSettings(settings);
                              
                              settings.inactivate();
                              new FunctionParameterDialog(settings);
                              
                              if(settings.isActive()) 
                              {
                                  execute(settings);
                                  
                                  if(settings.getReturnList()!=null)
                                      this.insertIntoProbeListManager(
                                          new ArrayList<ProbeList>(settings.getReturnList()));
                                  
                              }
                          }catch(FileNotFoundException ex)
                          {
                              RDefaults.getPrefs().node(RDefaults.Prefs.SOURCES_NODE).remove(
                                  (String)this.getValue(AbstractAction.NAME)    
                              );
                              
                          }catch(Exception ex)
                          {
                              JOptionPane.showMessageDialog(
                                  (Component)e.getSource(), 
                                  "<html>Cannot execute this R function due to an error. <br>" +
                                  "<pre>" + ex.getClass().getName() + ": " + ex.getMessage() + "</pre>",
                                  "R: " + (String)this.getValue(AbstractAction.NAME),
                                  JOptionPane.ERROR_MESSAGE
                              );
                          }                    
                      }}));
              }
              menu.addSeparator();
              menu.add(new JMenuItem(new RunPluginAction0("More ...",manager,RPlugin.this,probeLists,probeListManager) {
                  public void actionPerformed(ActionEvent e)
                  {
                      super.actionPerformed(e);
                  }}));
              return menu;
          }//end if length
      }catch(Throwable e)
      {
          e.printStackTrace();
      }
      
      JMenuItem menu = super.createMenuItem(category, manager, probeLists, probeListManager);
      menu.setIcon(RDefaults.RLOGO_ICON);
      return menu; 
  }
*/

	public PreferencePane getPreferencesPanel() {
		try {
			RSettingsDialog rsd = new RSettingsDialog(null);
			return rsd.getAsPane();
		} catch (BackingStoreException e) {
			e.printStackTrace();
			return null; //new JLabel("Could not access backing store: \n"+e.getMessage());
		}
	}
	/*
	  public ArrayList<AbstractStandardDialogComponent> getPreferencesTabs()
	  {
	      return new ArrayList<AbstractStandardDialogComponent>(Arrays.asList(
	              new SourceComponent(),
	              new FileSettingsComponent()
	      ));
	  }
	  */ 
	  public List<ProbeList> run(List<ProbeList> probelists, MasterTable mastertab) {
		// run as probelist plugin
	      try
	      {
	          //collect settings  (modal)
	          RSettings settings=new RSettings();
	          settings.inactivate();
	          settings.setMasterTable(mastertab);
	          settings.setProbeLists(probelists);
	          new RSettingsDialog(settings).setVisible(true);
	          if(!settings.isActive()) {
	        	  return new LinkedList<ProbeList>();
	          }
	          
	          new FunctionParameterDialog(settings); 
	          if(settings.isActive()) execute(settings);
	          
	          //settings.releaseWaiters();
	          //settings.waitFor();
	          
	          return settings.getReturnList();
	          
	      }catch(Throwable t)
	      {
	          RDefaults.messageGUI(t.getMessage(),RDefaults.Messages.Type.ERROR);
	          t.printStackTrace();
	      }finally
	      {
	          System.gc();
	      }
	      
	      return new LinkedList<ProbeList>();
	  }
  
  
  
  /**
   * This method is used to run an R session in internal mode.
   * That means no graphical user interaction is supported.
   * Thus you can use the R interface for data analysis in your
   * specific <i>Mayday</i>-plug-in.
   * <br><br>
   * <u>How to invoke an R session:</u><br>
   * <ol>
   *   <li> Create an instance of RPlugin. 
   *   </li>
   *   <li> Create an RSource object via RSource.createInstance(String src,String funId)
   *     where src is the source file that contains the applicable function named by funId.
   *   </li>
   *   <li> Set the values of the parameters of the RSource object.
   *   </li>
   *   <li> Create an RSettings object via RSettings.createInitializedInstance(RSource src)
   *     where src is the currently created RSource object.<br>
   *     Note that you cannot use RSettings.DEL_ASK for the temporary file
   *     deletion flags. If DEL_ASK is given it will be set to DEL_YES.
   *   </li>
   *   <li> Invoke the RPlugin's runInternal() method.<br>
   *        The changed MasterTable will be returned through the parameter list.
   *        With this MasterTable it is possible to get the corresponding DataSet.  
   *   </li>
   * </ol>
   * <br><br>
   * You can use the RInternalException and subclasses to catch specific
   * exceptions of the R session. 
   * 
   * @param settings an RSettings object created via RSettings.createInitializedInstance()
   * @param probelists see specification of Pluggable.run()  
   * @param mastertable see specification of Pluggable.run(); The MasterTable object after
   *        running the R execution is returned through this parameter.
   * @param warningMsg a String reference to get the warning messages of the R session, will be
   *        null, if no warnings occured.
   * 
   * @return see specification of Pluggable.run()
   */
  public List<ProbeList> runInternal(RSettings settings, List<ProbeList> probelists, MasterTable mastertab, StringBuffer warningMsg)
  {
    //we could maybe return the plot files...
  	
  	//test settings
  	if(settings==null)
  	{
  		throw new RInternalException("RSettings cannot be null!");
  	}
  	
  	if (settings.getBinary()==null) {
  		throw new RInternalException("The R executable path is not defined!");
  	}
  	File exe=new File(settings.getBinary());
  	if(!exe.exists())
  	{
  		throw new RInternalException("This R executable does not exist!");
  	}
  	  	
  	//test tmpfile deletion settings:
  	if(settings.deleteInputFiles()==RSettings.DEL_ASK)
  	{
  		settings.setDeleteInputFiles(RSettings.DEL_YES);
  	}
  	if(settings.deleteOutputFiles()==RSettings.DEL_ASK)
  	{
  		settings.setDeleteOutputFiles(RSettings.DEL_YES);
  	}
  	
  	List<ProbeList> returnList=null;
  	StringBuffer errBuf= new StringBuffer();  // was =null before, so we got a NullPointerException below (fb)
  	settings.setMasterTable(mastertab);
    settings.setProbeLists(probelists);
  	try
  	{
  	  	System.out.println("RPlugin::runInternal creating Job");
  		RJob job=new RJob(settings);
  		System.out.println("RPlugin::runInternal running Job");
  		int exitValue=job.run(); //waits until execution has finished
  		System.out.println("RPlugin::runInternal finished Job");
  		File output=job.getResult();
  		
  		File error=job.getError();
  		if(error!=null)
  		{
  			BufferedReader rd=new BufferedReader(new FileReader(error));
  			String line=null;
  			while((line=rd.readLine())!=null)
  			{
  				errBuf.append(line+"\n");
  			}
  			rd.close();
  		}
  		
  		try
  		{
  		    if(exitValue==0)
  		    {
  		        RResultParser parser=new RResultParser(output, settings);
  		    	returnList=parser.parse();
  		    	mastertab=parser.getMasterTable();  // returning does NOT work! (fb)
  		    	settings.setMasterTable(mastertab); //this does work (fb)
  		    }  			
  			warningMsg.append(errBuf.toString());  // returning via String argument did NOT work! (fb)
  			
  		}catch(Exception ex)
  		{
  			throw new RInternalException(
				"RResultParser: parsing error"+ex.getMessage()+"\n"+
				"R error: \n"+errBuf.toString()			
			);
  		}
  		
  		if(settings.deleteInputFiles()==RDefaults.TempFiles.DEL_YES) // added (fb)
        {
            //Delete without asking
            for(int i=0;i!=job.getInputFiles().length;++i)
            {
                job.getInputFiles()[i].delete();
            }
        }  		
  		
  		 if(settings.deleteOutputFiles()==RDefaults.TempFiles.DEL_YES) // added (fb)
         {
             //Delete without asking
             for(int i=0;i!=job.getOutputFiles().length;++i)
             {
                 //maybe the error file is null
                 if(job.getOutputFiles()[i]!=null)
                 {
                     job.getOutputFiles()[i].delete();
                     job.getOutputFiles()[i].deleteOnExit();
                 }
             }
         }
  		
  	}catch(Exception ex)
  	{
  		throw new RInternalException(ex.getMessage());
  	}
    
  	return returnList;
  }
  
  /*
  private void initSettings(RSettings settings)
  {
      settings.setBinary(RDefaults.getPrefs().get(
              RDefaults.Prefs.BINARY_KEY,
              RDefaults.Prefs.BINARY_DEFAULT
      ));
      
      settings.setWorkingDir(RDefaults.getPrefs().get(
              RDefaults.Prefs.WORKINGDIR_KEY ,
              RDefaults.Prefs.WORKINGDIR_DEFAULT        
     ));
      
      
      settings.setLogFilename(RDefaults.getPrefs().get(
              RDefaults.Prefs.LOGFILE_KEY ,
              RDefaults.Prefs.LOGFILE_DEFAULT        
      ));
      
      
      settings.setDeleteInputFiles(RDefaults.getPrefs().getInt(
          RDefaults.Prefs.DELETEINPUTFILES_KEY,
          RDefaults.Prefs.DELETEINPUTFILES
      ));

      settings.setDeleteOutputFiles(RDefaults.getPrefs().getInt(
          RDefaults.Prefs.DELETEOUTPUTFILES_KEY,
          RDefaults.Prefs.DELETEOUTPUTFILES
      ));

      settings.setPlotType(RDefaults.getPrefs().getInt(
              RDefaults.Prefs.PLOT_TYPE_KEY,
              RDefaults.Prefs.PLOT_TYPE_DEFAULT
      ));
      settings.setShowPlots(RDefaults.getPrefs().getBoolean(
              RDefaults.Prefs.SHOW_PLOTS_KEY,
              RDefaults.Prefs.SHOW_PLOTS_DEFAULT
      ));
  }*/
  
  private void execute(RSettings settings)
  {
      //create an RJob and run it
      File output;
      RJob job;
      try
      {
          job=new RJob(settings);
          int exitValue=job.run();  //this waits until execution has finished
          
          output=job.getResult();
          
          if(RDefaults.DEBUG) System.out.println("Execution finished.");
          
          File err=job.getError();
          if(job.isCanceled())
          {
              JOptionPane.showMessageDialog(
                  RDefaults.RForMaydayDialog,
                  RDefaults.Messages.JOB_CANCELED,
                  RDefaults.messageTitle(RDefaults.Messages.Type.WARNING),
                  JOptionPane.WARNING_MESSAGE
              );
          }else if(err!=null)
          {
              StringBuffer buf=new StringBuffer();
              BufferedReader r=new BufferedReader(new FileReader(err));
              String line;
              while((line=r.readLine())!=null)
              {
                  buf.append(line+"\n");
              }
              r.close();
              
              String[] options=
              {
                      RDefaults.ActionNames.OK,
                      RDefaults.ActionNames.OPEN
              };
              
              int res=JOptionPane.showOptionDialog(
                  RDefaults.RForMaydayDialog,
                  RDefaults.Messages.EXIT_STATE+exitValue+".\n\n"+
                  (exitValue==0  
                          ? RDefaults.Messages.R_EXEC_WARNINGS
                          : RDefaults.Messages.R_EXEC_ERROR
                  )+buf.toString(),
                  RDefaults.messageTitle(RDefaults.Messages.Type.WARNING),
                  JOptionPane.YES_NO_OPTION,
                  JOptionPane.WARNING_MESSAGE,
                  null, //icon
                  (Object[])options,
                  RDefaults.ActionNames.OK
              );
              
              if(res==1) //open the editor
              {
                  RDefaults.startEditor(output);
                  //RDefaults.startEditor(err);                     
              }
          }
          
          //parse the results 
          if(exitValue==0)
          {
              try
              {
                  RResultParser p=new RResultParser(output,settings);
                  settings.setReturnList(p.parse());
                  // if a new dataset was created, add it now
                  if (settings.getReturnList()==null)
                	  DataSetManager.singleInstance.addObjectAtBottom(p.getMasterTable().getDataSet());
              }catch(RuntimeException ex)
              {
                  if(RDefaults.DEBUG) ex.printStackTrace();
                  RDefaults.messageGUI(
                      ex.getMessage(),
                      RDefaults.Messages.Type.ERROR
                  );
              }
          }
          
          //settings.releaseWaiters();
          
          //IMPROVEMENT: don't look if canceled?!
          //look for the pictures:
          GraphicsFileFilter filter = new GraphicsFileFilter(settings);
          File[] images=new File(settings.getWorkingDir()).listFiles(filter);
          RPlotFrame plotsFrame=null;
          if(images!=null && images.length!=0)
          {
              plotsFrame=new RPlotFrame(settings);
          }
          
          if(plotsFrame!=null)
          {
              int numExceptions=plotsFrame.getExceptionCounter();
              if(numExceptions>0)
              {
                  PrintWriter logWriter=new PrintWriter(
                      new FileWriter(
                          new File(settings.getLogFilename()),
                          true //append?
                      ),
                      true //autoflush?
                  );
                  logWriter.println(plotsFrame.getExceptionMessages());
                  
                  RDefaults.messageGUI(
                      ""+numExceptions+" plot"+(numExceptions==1?"":"s")+" ignored.\n" +
                      "For further information see \n" +
                      "  "+settings.getLogFilename()+".",
                      RDefaults.Messages.Type.INFO
                  );                  
              }
          }
          
          
          //delete the temporary files
          if(settings.deleteInputFiles()==RDefaults.TempFiles.DEL_ASK)
          {
              //ASK FOR DELETION
              int res=JOptionPane.showOptionDialog(
                  RDefaults.RForMaydayDialog,
                  "Delete the temporary input files?",
                  null, //title
                  JOptionPane.YES_NO_OPTION,
                  JOptionPane.QUESTION_MESSAGE,
                  null, //icon
                  null, //options
                  null //initial value
              );
              if(res==JOptionPane.YES_OPTION)
              {
                  for(int i=0;i!=job.getInputFiles().length;++i)
                  {
                      job.getInputFiles()[i].delete();
                  }
              }               
          }else if(settings.deleteInputFiles()==RDefaults.TempFiles.DEL_YES)
          {
              //Delete without asking
              for(int i=0;i!=job.getInputFiles().length;++i)
              {
                  job.getInputFiles()[i].delete();
              }
          }
          if(settings.deleteOutputFiles()==RDefaults.TempFiles.DEL_ASK)
          {
              //ASK FOR DELETION
              int res=JOptionPane.showOptionDialog(
                  RDefaults.RForMaydayDialog,
                  "Delete the temporary output files?",
                  null, //title
                  JOptionPane.YES_NO_OPTION,
                  JOptionPane.QUESTION_MESSAGE,
                  null, //icon
                  null, //options
                  null //initial value
              );
              if(res==JOptionPane.YES_OPTION)
              {
                  for(int i=0;i!=job.getOutputFiles().length;++i)
                  {
                      //maybe the error file is null
                      if(job.getOutputFiles()[i]!=null)
                      {
                          job.getOutputFiles()[i].delete();
                          job.getOutputFiles()[i].deleteOnExit();
                      }
                  }
              }                       
          }else if(settings.deleteOutputFiles()==RDefaults.TempFiles.DEL_YES)
          {
              //Delete without asking
              for(int i=0;i!=job.getOutputFiles().length;++i)
              {
                  //maybe the error file is null
                  if(job.getOutputFiles()[i]!=null)
                  {
                      job.getOutputFiles()[i].delete();
                      job.getOutputFiles()[i].deleteOnExit();
                  }
              }
          }
          
      }catch(Exception ex)
      {
          ex.printStackTrace();
      }finally
      {
          System.gc();
      }
  }
  
  /*
  private abstract static class RunPluginAction0
  extends RunPluginAction
  {
    /**
     * @param manager
     * @param plugin
     * @param probeLists
     * @param probeListManager
     *//*
    public RunPluginAction0(String name, PluginManager manager, Pluggable plugin, List probeLists, ProbeListManager probeListManager)
    {
        super(manager, plugin, probeLists, probeListManager);
        putValue(AbstractAction.NAME, name);
    }      
  }*/

}

