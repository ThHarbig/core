package mayday.interpreter.rinterpreter;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Window;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

import mayday.core.Mayday;
import mayday.core.MaydayDefaults;
import mayday.core.Preferences;
import mayday.core.pluma.PluginInfo;

/**
 * This class contains constants for the user interface - like 
 * names of buttons or labels -, constant Strings - like file identifier
 * or R source code components ...
 * 
 * @author Matthias Zschunke 
 */
public final class RDefaults
{
	public static final boolean DEBUG = ( System.getenv("MAYDAY_DEBUG")==null ? 
        false : Boolean.parseBoolean(System.getenv("MAYDAY_DEBUG"))
    );
	
	public static final String REPLACE="%REPLACE";
	public static final String FILE_REPLACE="%file";
	public static final String FIELD_REPLACE="%field";
	public static final String TIME_REPLACE="%time";
	
//    public static final ClassLoader THIS_CLASSLOADER=RPlugin.class.getClassLoader();

    
	/**
	 * Get the path to the rinterpreter plugin, that means
	 * the path to the RDefaults class.
	 */
	public static final String PATH="mayday/interpreter/rinterpreter/";

//	public static final Frame MAYDAY_FRAME=Mayday.getFrames()[1];
	public static final Window MAYDAY_FRAME() {
		return Mayday.sharedInstance;
	}
	
	public static Dialog RForMaydayDialog=null;
    
    public static final String FULLNAME="R Interpreter";
    
	public static final String RSPLASH = "mayday/interpreter/rinterpreter/images/RfMSplash0.gif";    
    
    public static final String ICON_WARNING = "mayday/interpreter/rinterpreter/images/warning12.gif";    
    
    public static final String RLOGO_ICON = "mayday/interpreter/rinterpreter/images/RLogo.png";

	public static final String RESULTPREFIX="output";
	public static final String RESULTSUFFIX=".out";
	public static final String ERRORPREFIX="error";
	public static final String ERRORSUFFIX=".err";
	public static final String INPUTFILEPREFIX="DATAobj";
	public static final String TEMPFILESUFFIX=".dat";
	public static final String RSUFFIX=".R";
	public static final String BATCHPREFIX="script";

	public static final String OS_NAME=System.getProperty("os.name");
	private static String i_SYSTEM;
	/**
	 *  define the os class:
	 *  WinNT (=NT,XP,2000)
	 *  Other (=all, that are not WinNT) 
	 */
	static
	{
		if(OS_NAME.equals("Windows NT") 
			|| OS_NAME.equals("Windows XP")
			|| OS_NAME.equals("Windows 2000"))
		{
			i_SYSTEM="WinNT";
		}else
		{
			i_SYSTEM="Other";
		}
	}
	public static final String SYSTEM=i_SYSTEM;
	public static final String REDIRECTOR_StdIn="<";
	public static final String REPLACE_StdIn="%StdIn";
	public static final String REDIRECTOR_StdOut="1>";
	public static final String REPLACE_StdOut="%StdOut";
	public static final String REDIRECTOR_StdErr="2>";
	public static final String REPLACE_StdErr="%StdErr";
	public static final String RHOME="RHOME";
	public static final String R_HOME="R_HOME";
	public static final String REPLACE_RHOME="%RHOME";
	public static final String REPLACE_RBINARY="%RBINARY";
	public static final String REPLACE_RARGS="%RARGS";
	
	/**
	 * System specific stuff:<br>
	 * For WinNT systems:<br>
	 * <pre>
	 *      COMMAND_INTERPRETER="cmd.exe /C";
	 *      BATCH_FIRSTLINE="@echo off";
	 *      BATCH_ENVSET="SET";
	 * 		BATCH_SUFFIX=".bat";
	 * 		
	 * </pre>
	 * For other systems:<br>
	 * <pre>
	 *      COMMAND_INTERPRETER="sh";
	 *      BATCH_FIRSTLINE="#! sh";
	 *      BATCH_ENVSET="export";
	 * 		BATCH_SUBBIX=".sh";
	 * </pre>
	 * 
	 */
	//private static String i_RArgs;
	private static String i_COMMAND_INTERPRETER;
	private static String i_COMMAND_INTERPRETER_SWITCH;
	private static String i_BATCH_FIRSTLINE;
	private static String i_BATCH_ENVSET;
	private static String i_BATCH_SUFFIX;
	private static String i_KILL_PROCESS;
	static
	{
		if(SYSTEM.equals("WinNT"))
		{
			i_COMMAND_INTERPRETER="cmd.exe";
			i_COMMAND_INTERPRETER_SWITCH="/C";
			i_BATCH_FIRSTLINE="@echo off";
			i_BATCH_ENVSET="SET";
			i_BATCH_SUFFIX=".bat";
			i_KILL_PROCESS="tskill ";
		}else
		{
			i_COMMAND_INTERPRETER="sh";
			i_COMMAND_INTERPRETER_SWITCH="";
			i_BATCH_FIRSTLINE="#! sh";
			i_BATCH_ENVSET="export";
			i_BATCH_SUFFIX=".sh";
			i_KILL_PROCESS="kill -9 ";
		}
	}
	public static final String COMMAND_INTERPRETER=i_COMMAND_INTERPRETER;
	public static final String COMMAND_INTERPRETER_SWITCH=i_COMMAND_INTERPRETER_SWITCH;
	public static final String BATCHSUFFIX=i_BATCH_SUFFIX;
	public static final String BATCH_CONTENT=
		i_BATCH_FIRSTLINE+"\n"+
		i_BATCH_ENVSET+" "+RHOME +"="+REPLACE_RHOME+"\n"+
		i_BATCH_ENVSET+" "+R_HOME+"="+REPLACE_RHOME+"\n"+
		REPLACE_RBINARY+" "+REPLACE_RARGS+" "+
			REDIRECTOR_StdIn +" "+REPLACE_StdIn +" "+
			REDIRECTOR_StdOut+" "+REPLACE_StdOut+" "+
			REDIRECTOR_StdErr+" "+REPLACE_StdErr+"\n";
	
	public static final String R_Args="--no-save --silent"; //"--vanilla", " --gui=none";
	public static final String Rterm_Args="--no-save --silent --internet2";//"--vanilla"
	public static final String KILL_PROCESS=i_KILL_PROCESS;
	public static final String PID_FILENAME="rfm_R.pid";

	/**
	 * Examine which form of R is installed.
	 * 
	 * @param binary, the binary name of the R interpreter to use
	 * @return the arguments for the R interpreter;
	 * if the binary is an exe file, than it is assumed, that the 
	 * Window-like Version of R is used, which does not know the
	 * "--gui=none"-argument.
	 */
	public static String getRArgs(String binary)
	{
		return (binary.trim().endsWith(".exe"))? Rterm_Args :R_Args;		
	}

	public static final String R_LOGFILE_HEADER="# RForMayday Logfile";

    public static final String ABOUT_TEXT = 
        "<html><body>"
        + "Matthias Zschunke, Janko Dietzsch, Kay Nieselt<br>"
        + "Zentrum f\u00FCr Bioinformatik T\u00FCbingen (ZBIT), T\u00FCbingen, Germany<br>" 
        + "Junior Research Group Proteomics Algorithms and Simulations<br><br>" 
        + "Eberhard-Karls-Universit\u00E4t, T\u00FCbingen, Germany<br>" 
        + "Information- and Cognitive Sciences Department<br><br>" 
        + "http://www.zbit.uni-tuebingen.de/pas<br>"
        +"</body></html>";
    
        
	
	/**
	 * This function fixes a problem with the function String.replaceAll().
	 * You cannot replace Filenames under Windows because the path
	 * separators disappear in the result!
	 * <br><br>
	 * The fix is to replace first all occurances of '\\' with '/' wich
	 * works under Windows systems. (The question is, if it works too 
	 * under other Windows/DOS-like systems, like OS/2 etc.)<br>
	 * If your system uses '\\' as file separator this fix is applied.
	 * 
	 * @param target, String whithin the replacement takes place
	 * @param key, sequence to replace
	 * @param value, String to put in
	 * @return target, where all occurances of key are replaced by value
	 */
	public static String replaceFileName(String target, String key, String value)
	{
		if(System.getProperty("file.separator").equals("\\"))
		{
			return target.replaceAll(key,value.replace('\\','/'));
		}
		return target.replaceAll(key,value);
	}
	
	public static final class TempFiles
	{
		public static final int DEL_NO=0;
		public static final int DEL_YES=1;
		public static final int DEL_ASK=2;
		
		public static final int JPEG_PLOT=0;
		public static final int JPG_PLOT=1;
		public static final int PDF_PLOT=2;
		public static final int PNG_PLOT=3;
		public static final int PS_PLOT=4;
		public static final int SVG_PLOT=5;
        public static final int BMP_PLOT=6;
		
		public static final String[] GRAPHICS_EXTENTIONS=
			{"JPEG","JPG","PDF","PNG","PS","SVG","BMP"};
	}
	

	/**
	 * This class contains keys and default-values for the RInterpreter
	 * Preferences tree.
	 *
	 */
	public static final class Prefs
	{
		public static final String PLOT_TYPE_KEY = "plottype";
		public static final int PLOT_TYPE_DEFAULT = TempFiles.JPEG_PLOT;
		
		public static final String SHOW_PLOTS_KEY="showplots";
		public static final boolean SHOW_PLOTS_DEFAULT=true;

        public final static String BINARY_KEY="binary";

        private static String fetchRBinary()
        {
            try
            {
                String rhome = System.getenv("RHOME");
                String binary = "/bin/R" + (OS_NAME.startsWith("Windows")?".exe":"");
                File f = new File(rhome + binary);
                
                if(rhome!=null && f.exists())
                {
                    return f.getCanonicalPath();
                }
                
                rhome = System.getenv("R_HOME");
                f = new File(rhome + binary);
                if(rhome!=null && f.exists())
                {
                    return f.getCanonicalPath();
                }
            }catch(Throwable t)
            {}
            
            return "";
        }
		public final static String BINARY_DEFAULT = fetchRBinary();

		public final static String WORKINGDIR_KEY="workingdir";
		public final static String WORKINGDIR_DEFAULT=System.getProperty("user.dir","");

		public final static String LOGFILE_KEY="logfile";
		public final static String LOGFILE_DEFAULT="";

		public final static String SOURCES_NODE="sources";
        public final static String MIOTYPE_NODE="miotypes";

		public final static String LASTSRCDIR_KEY="lastSourceDir";
		public final static String LASTSRCDIR_DEFAULT=System.getProperty("user.dir","");
		
		public final static String LASTSOURCESELECTIONINDEX_KEY="lastsourceselection";
		public final static int LASTSOURCESELECTIONINDEX_DEFAULT=0;
		
		public final static String DELETEOUTPUTFILES_KEY="deleteOutputFiles";
		public final static int DELETEOUTPUTFILES=RDefaults.TempFiles.DEL_ASK;
		public final static String DELETEINPUTFILES_KEY="deleteInputFiles";
		public final static int DELETEINPUTFILES=RDefaults.TempFiles.DEL_ASK;
	}
	
	
	/**
	 * Get the Preferences plugin node from the Mayday Preferences tree
	 * and open the node for this plugin.
	 * 
	 * @return Root node of the RInterpreter Preferences tree.
	 */
	public static Preferences getPrefs()
	{
        return PluginInfo.getPreferences("PAS.Rinterpreter");
	}
	
	/**
	 * Get the Preferences node <tt>nodeKey</tt> from the RInterpreter Preferences.
	 * 
	 * @param nodeKey
	 * @return Preferences
	 */
	public static Preferences getPrefsSubNode(String nodeKey)
	{
		return getPrefs().node(nodeKey);
    }
    
    
	/**
	 * This class contains the key-strings for the 
	 * RSourceDescription dtd and the filename of the 
	 * dtd file.<br>
	 * The dtd file is assumed to be located in the 
	 * same directory like <tt>RDefaults.class</tt>.
	 */
	public static final class RSDesc
	{
		public final static String DTD="RSourceDescription.dtd";
		public final static String ROOT="RSourceDescription";
        public final static String FUNCNAME_ELEM="functionname";
		public final static String QUICKINFO_ELEM="quickinfo";
		public final static String PARLIST_ELEM="paramlist";
		public final static String PARAM_ELEM="param";
        public final static String REQUIRES_ELEM="requires";
		public final static String FUNCATTRIB_ID="id";
		public final static String FUNCATTRIB_DESC="descriptor";
		public final static String PARAMATTRIB_NAME="name";
		public final static String PARAMATTRIB_DEFAULT="default";
		public final static String PARAMATTRIB_DESC="description";
        public final static String TYPE_ELEM="type";
        public final static String TYPEATTRIB_NAME="name";
        public final static String TYPEATTRIB_NOEDIT="noedit";
        public final static String TYPENAME_BOOLEAN="boolean";
        public final static String TYPENAME_STRICTBOOLEAN="strictboolean";
        public final static String TYPENAME_DEFAULT="default";
        public final static String TYPENAME_LIST="selection";
        public final static String TYPENAME_FILENAME="filename";
        public final static String TYPENAME_FILENAMES="filenames";
        //added 05.10.08 to ensure that strings are interpreteted as Strings nt
        public final static String TYPENAME_STRING="string";
        public final static String MIO_ELEM="mio";
        public final static String MIOATTRIB_id = "id";
        public final static String MIOATTRIB_classname="classname";
        public final static String MIOATTRIB_direction="direction";
        
        public final static String ENTRY_ELEM="entry";
        public final static String ENTRYATTRIB_VALUE="value";
		public final static String QUICKINFO_DEFAULT="Description file automatically created";	
		public final static String HEADER="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n"+ 
										  "<!DOCTYPE RSourceDescription SYSTEM '"+DTD+"'>\n";

		public final static String PARAMDESC_FIRST_DEFAULT="Mayday's data structures";										  

	}
	
	/**
	 * This class encapsulates some R source strings and parts of
	 * such strings used for the creation of the temporary source file. 
	 * 
	 * @author Matthias
	 *
	 */
	public static final class RSrcComponents
	{	    
		public static final String REPLACE=RDefaults.REPLACE;
		public static final String FILE_REPLACE=RDefaults.FILE_REPLACE;
		public static final String FIELD_REPLACE=RDefaults.FIELD_REPLACE;
		public static final String TIME_REPLACE=RDefaults.TIME_REPLACE;
		public static final String PID_REPLACE="%pidreplace";

	    public static final String[] PRESETS=
	    {
	        
	    };

		
		public static final String SOURCE="source(\""+FILE_REPLACE+"\");\n";
		public static final String SETWD="setwd(\""+FILE_REPLACE+"\");\n";
		public static final String CAT_PID="cat(Sys.getpid(),file=\""+PID_REPLACE+"\",append=FALSE);\n";
		public static final String PRINT="print("+FIELD_REPLACE+");\n";
		public static final String RESULT_COMMENT="# result starts:";
		public static final String CREATE_OUTPUT="create.output("+FIELD_REPLACE+")";
		
		public static final String INITIAL_HINT=
			"# ----------------------------------------------\n"+
			"# created by "+RDefaults.FULLNAME+"\n"+
			"# "+TIME_REPLACE+"\n"+
			"# ----------------------------------------------\n"
		;
		
		public static final String DATASTRUCTURES_FIELD="DATA";
		public static final String MT_FIELD="mastertab.field";
		public static final String PL_FIELD="probelists.field";
		public static final String PR_FIELD="probes.field";
		public static final String RESULT_FIELD="result.field";
		
		public static final String DATASTRUCTURES_READ="datastructures.read(\""+FILE_REPLACE+"\");\n";
		public static final String LIBRARY="library(RForMayday);";
		public static final String COM_AREA="COMAREA<-\""+PID_REPLACE +"\";\n";
		
		public static final String TRUE="TRUE";
		public static final String FALSE="FALSE";
		
		//public static final String[] GRAPHICS_EXTENTIONS=
		//{"JPEG","JPG","PDF","PNG","PS","SVG"};
		public static final String[] SET_GRAPHICS_DEVICES=
		{
		        "jpeg100",
		        "jpeg100",
		        "pdf",
		        "png",
		        "postscript",
		        "devSVGMulti",
                "bmp"
		};
		
		public static final String DEVICE_REPLACE="%DEVICE";
		public static final String SET_DEFAULT_DEVICE=
		    "set.default.plot.device(\""+
		    DEVICE_REPLACE+
		    "\");";
	}
	
	public static final class RResults
	{
		public static final String JOB_REPLACE="%job";
		public static final String SETTINGS_REPLACE="%settings";
		
		public static final String DS="%dataset";
		public static final String DS_QINFO_NEW=
			"Created by "+RDefaults.FULLNAME+"\n"+
			RDefaults.TIME_REPLACE+"\n"+
			"SETTINGS: \n"+
			SETTINGS_REPLACE			
			;
		
		public static final String MT="%mastertable";
        public static final String MI="%miotypes";
        public static final String MG="%miogroups";
		public static final String PL="%probelists";
		public static final String PR="%probes";
        public static final String MIOS = "%mios";
		
	}
	
	/**
	 * The names of the buttons.
	 * 
	 * @author Matthias
	 *
	 */
	//maybe change the values acording to your favorite language
	public static final class ActionNames
	{
		public final static String BROWSE=		"Browse ...";
		public final static String RUN=			"Run";
		public final static String NEXT=		"Next >";
		public final static String CANCEL=		"Cancel";
		public final static String SAVE=		"Save";
		public final static String EDIT=		"Edit ...";
		public final static String REMOVE=		"Remove";
		public final static String ADD=			"Add ...";
        public final static String ADD_SHORT=   "Add";
		public final static String SELECT=		"Select";
		public final static String DELETE=		"Delete";
		public final static String SKIP=		"Skip";
		public final static String OK=			"Ok";
		public final static String SELECTFUN=   "Select Function ...";
		public final static String OPEN=        "Open ...";
		public final static String REFRESH= 	"Refresh";
		public final static String CLEANLOG= 	"Clean up";
		public final static String SORT=		"Sort";
        public final static String RESTORE=     "Restore defaults";
        public final static String ABOUT = "About ...";
	}
	
	/**
	 * Values for the Actions described in 
	 * <tt>RDefaults.ActionNames</tt>.
	 * @author Matthias
	 *
	 */
	public static final class Actions
	{
		public final static int DEFAULT=	0; 
		public final static int BROWSE=		1;
		public final static int RUN=		2;
		public final static int NEXT=		3;
		public final static int CANCEL=		4; //CANCEL must not be 0
		public final static int SAVE=		5;
		public final static int EDIT=		6;
		public final static int REMOVE=		7;
		public final static int ADD=		8;
		public final static int SELECT=		9;
		public final static int DELETE=		10;
		public final static int SKIP=		11;
		public final static int OK=         12;
		public final static int SELECTFUN=	13; 
		public final static int OPEN=		14;
		public final static int CLEANLOG=   15;
		public final static int SORT=		16;
        public final static int RESTORE=    17;
	}
	
	/**
	 * Labels for GUI-elements.
	 * 
	 * @author Matthias
	 *
	 */
	public static final class Labels
	{
		public static final String RBINARY="R binary";
		public static final String RWORKING="Working Directory";
		public static final String RLOGFILE="Log File";
		public static final String SOURCES="Sources";
		public static final String INPUT_FILES="Input Files";
		public static final String OUTPUT_FILES="Output Files";
		public static final String ASK_FOR="ask for deletion";
		public static final String ALWAYS="always delete";
		public static final String NEVER="never delete";
		public static final String TEMPFILE_INFORMATION=
			"What should be done with the temporary files which\n"+
			"are created by "+FULLNAME+"?";
		public static final String SHOW_PLOTS_CHECKBOX="Show RPlots after execution?";
	}
	
	/**
	 * Titles of dialog boxes and tabbedPanes.
	 * 
	 * @author Matthias
	 *
	 */
	public static final class Titles
	{
		public static final String FUNCTIONCHOOSER="Function Chooser";
		public static final String FUNCTIONPARAMCHOOSER="Parameter Chooser";
		public static final String XMLPARSEEXCEPTIONDIALOG="XMLParser exception";
		public static final String SOURCES="Sources";
		public static final String SETTINGS="Settings";
		public static final String FILE_SETTINGS="File "+SETTINGS;
		public static final String TEMPFILES="Temporay Files";
		public static final String RDIALOG=FULLNAME+" - "+SETTINGS;
		public static final String PARAMCHOOSERCOLUMN1="Parameter id";
		public static final String PARAMCHOOSERCOLUMN2="Value";
		public static final String DESCRIPTIONEDITORDIALOG="Description Editor";
		public static final String PROGRESSDIALOG="R is executing - Please Wait!";
		public static final String RPLOTS_FRAME="R Plots";
		public static final String RPLOTS="RPlots";
		public static final String PLOT_NR="Rplot #";
		
	}
	
	/**
	 * Messages for error and information dialogs.
	 * @author Matthias
	 *
	 */

	public static final class Messages
	{
		public static final String INFORMATION="Information";
		public static final String ERROR="Error";
		public static final String WARNING="Warning";
		public static final String QUESTION="Question";
		public static final String R_EXEC_ERROR="The following errors occured during the execution of R.\n\n";
        public static final String R_EXEC_WARNINGS="The following warnings occured during the execution of R.\n\n";
		public static final String EXIT_STATE="The exit state of R was ";
		public static final String JOB_CANCELED="The job has been canceled.";
		public static final String FILE_COULD_NOT_WRITE_TO="Could not write to file: ";
		
		
		public static final class Type
		{
			public static final int INFO=JOptionPane.INFORMATION_MESSAGE;
			public static final int ERROR=JOptionPane.ERROR_MESSAGE;
			public static final int WARNING=JOptionPane.WARNING_MESSAGE;
			public static final int QUESTION=JOptionPane.QUESTION_MESSAGE;			
		}
		
		public static final class RResultParser
		{
			public static final String PARSING_ERROR="RResultParser: parsing error";
			public static final String IN_FILE="File: ";
			public static final String AT_LINE="Line: ";
		}
	}
	
	/**
	 * Tooltip texts.
	 * 
	 * @author Matthias
	 *
	 */
	public static final class ToolTips
	{
		public static final String DESCRIPTIONEDIT="Edit the R Description File";
		public static final String DELETEXMLFILE="Delete this R Description File";
		public static final String SELECT_NEW_FUNCTION="Select another function";
		public static final String READQUICKINFO="Read the quickinfo from the source file";
		public static final String PLOT_TYPE_COMBOBOX="Choose a default graphics type";
		public static final String SHOW_PLOTS_CHECKBOX=
		    	"Determine whether to show resulting RPlots in a frame if any.\n" +
		    	"plot has been created.\n" +
		    	"Plots that cannot be shown, e.g. postscript plots, are ignored.";
		public static final String CLEANUP_LOG="Clear the content of the log-file.";
	}

	
	/**
	 * Start the editor specified in the Mayday Preferences
	 * with the given file.
	 * 
	 * @param f, file to edit.
	 */
	public static void startEditor(File f)
	{
		Runtime rt=Runtime.getRuntime();
		String[] args=new String[2];
		args[0]=MaydayDefaults.Prefs.EditorPath.getStringValue();			
		args[1]=f.getAbsolutePath();
		try
		{
			//Process p=
				rt.exec(args);
		}catch (IOException ex)
		{
			RDefaults.messageGUI("Could not start editor.\n"+ex.getMessage(),RDefaults.Messages.Type.ERROR);
		}
		
	}
	
	/**
	 * Simply prints the given message to StdErr.
	 * deprecated!
	 * @param msg
	 */
	public static void message(String msg)
	{
		System.err.println(msg);
	}
	
	/**
	 * Shows a MessageDialog with the given message and
	 * the message type.
	 * 
	 * @param msg, message
	 * @param type, message type 
	 */
	public static void messageGUI(String msg, int type)
	{
		JOptionPane.showMessageDialog(
			RDefaults.RForMaydayDialog,
			msg,
			messageTitle(type),
			type);
	}
	
	// usefull functions:
	public static void debugPrint(Object o, String function, String message)
	{
		if(DEBUG)
		{
			String ret=o.getClass().getName()+": "+function+": "+message;
			System.err.println(ret);
		}
	}
	
	
	public static void debugInfo(String message, int type)
	{
		if(DEBUG)
		JOptionPane.showMessageDialog( null,
				 message,
				 messageTitle(type),
				 type ); 
	}
	
	/**
	 * Creating a message dialog title.
	 * 
	 * @param type
	 * @return the title
	 */
	public static String messageTitle(int type)
	{
		switch(type)
		{
			case Messages.Type.INFO: return Messages.INFORMATION;
			case Messages.Type.ERROR: return Messages.ERROR;
			case Messages.Type.WARNING: return Messages.WARNING;
			case Messages.Type.QUESTION: return Messages.QUESTION;
			default: return "";
		}
	}
	

	
	/**
	 * Produce a String of the form "#ffffff" from
	 * the given color.
	 * 
	 * @param c
	 * @return String representation of the rgb color
	 */
	public static String RString(Color c)
	{
		return "#"+
			fillHex(Integer.toHexString(c.getRed()))+
			fillHex(Integer.toHexString(c.getGreen()))+
			fillHex(Integer.toHexString(c.getBlue()));
	}
	
	private static String fillHex(String hex)
	{
		return (hex.length()==1)?"0"+hex:hex;
	}
	
	public static String fillFront(String s, int len, char fill)
	{
	    StringBuffer buf=new StringBuffer();
	    for(int i=0; i<len-s.length(); ++i)
	    {
	        buf.append(fill);
	    }
	    
	    return buf.toString()+s;
	}
	
	
	public static String RString(boolean b)
	{
		return b ? 
			RSrcComponents.TRUE :
			RSrcComponents.FALSE;		
	}
	
	/**
	 * Produce a Color from the given String.<br>
	 * This method simply invokes the method 
	 * <tt>java.awt.Color.decode(String)</tt>.
	 * If the Color.decode()-method stops with
	 * a NumberFormatException, Color.BLACK
	 * will be returned.
	 * 
	 * @param s, rgb color string of the form "#FFFFFF"
	 * @return Color object with the given color or 
	 * Color.Black, if an exception occured.
	 */
	public static Color stringToColor(String s)
	{
		Color c=Color.BLACK;
		try
		{
			c=Color.decode(s);
		}catch(NumberFormatException ex)
		{;}
		
		return c;
	}
	
	public static boolean parseBoolean(String s, boolean defaultBool)
	{
		boolean b=defaultBool;
		try
		{
			b=Boolean.valueOf(s).booleanValue();
		}catch(Exception ex)
		{;}
		return b;
	}

	public static void printResultArrays(Object[] s)
	{
		if(s==null)
		{
			System.out.println("**NULL**");
		}else
		{
			for(int i=0; i!=s.length;++i)
			{
				System.out.println(""+i+": "+s[i]);
			}
		}
	}
    
    public static String toString(Object[] list, String sep)
    {
        if(list==null || list.length==0) return null;
        if(sep==null) sep=", ";
                
        StringBuffer buf=new StringBuffer();
        for(int i=0; i<list.length-1; ++i)
        {
            buf.append(((String)list[i])+sep);
        }
        buf.append(list[list.length-1]);        
        
        return buf.toString();
    }
	
	public static String getTimeString(long time)
	{
	    String[] s={"","",""};
	    
	    long millis=time%1000;
	    long sec=time/1000;
	    long min=sec/60;
	    sec=sec%60;
	    s[0]=String.valueOf(min);
	    s[1]=String.valueOf(sec);
	    s[2]=String.valueOf(millis);
	    
	    return s[0]+" min "+s[1]+"."+s[2]+" sec.";
	}
    
    public static void log(Throwable t)
    {
        String logfile = getPrefs().get(
                Prefs.LOGFILE_KEY,
                Prefs.LOGFILE_DEFAULT
        );
        
        PrintWriter pw;
        try
        {
            pw = ( logfile.length()==0 ?
                    new PrintWriter(System.err) :
                    new PrintWriter(new FileWriter(logfile, true))
        );
        }catch(Exception ex)
        {
            pw = new PrintWriter(System.err);
        }
        
        t.printStackTrace(pw);
    }
    
//    static BufferedImage loadImage(String path)
//    {
//        return loadImage(path, null);
//    }
    
//    static BufferedImage loadImage(String path, BufferedImage defaultImage)
//    {
//        try
//        {
//            defaultImage = ImageIO.read(
//                THIS_CLASSLOADER.getResourceAsStream( path ));
//            
//        }catch(IOException ex)
//        {
//            ex.printStackTrace();
//        }   
//        return defaultImage;
//    }
}