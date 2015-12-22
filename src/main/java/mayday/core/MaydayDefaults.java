package mayday.core;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.prefs.BackingStoreException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import mayday.core.gui.ProbeListImage;
import mayday.core.pluma.PluginInfo;
import mayday.core.settings.Settings;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.settings.typed.StringSetting;


/*
 * Created on Apr 12, 2003
 *
 */

/**
 * @author neil
 * @version 
 */
public final class MaydayDefaults
{
	
	  // program information  
    public static final String PROGRAM_NAME = "Mayday"; 
    public static final String PROGRAM_DESCRIPTION = "A microarray data analysis workbench."; 
    public static final int RELEASE_MAJOR = 2; 
    public static final int RELEASE_MINOR = 14; 
    public static final String RELEASE_SUPPLEMENT = "";
    public static final String PROGRAM_FULL_NAME = PROGRAM_NAME +
    " " +
    RELEASE_MAJOR + 
    "." +
    RELEASE_MINOR +
    " " +
    RELEASE_SUPPLEMENT;
    
    
    // preferences
    public static File WORKINGDIR = new File(System.getProperty("user.home") + "/.mayday"); 
    
    public static class Prefs
    {
        public static Preferences NODE_PREFS = Preferences.userRoot().node(
            MaydayDefaults.PROGRAM_NAME+(isWebstartApplication()?"-webstart":"") 
        );

		public static final String PLUGINROOT_UNCONFIGURED = "@firstTimeUse";
        
        public static final String NODE_PLUGINS = "Plug-ins";
        
        /**
         * Libraries that needs to be loaded for plugins.
         */
<<<<<<< HEAD
        private static final String NODE_LIBRARIES = "libraries";
=======
        private static final String NODE_LIBRARIES = "libraries";        
>>>>>>> bd8805447b59c9475dfbcdf6f975397ad3c2209e
        /**
         * Replaces NODE_LIBRARIES in the case of webstart
         */
        private static final String NODE_LIBRARIES_WEBSTART_MODE = "librariesjws";

        private static final String NODE_MOST_RECENT_FILES = "MRU";

        
        private static final String PLUGIN_DIRECTORY = "directory";
        private static final String WEBSTART_ADDITIONAL_PLUGIN_DIRECTORY = "additionalWebstartDirectory";
        public static final String DEFAULT_PLUGIN_DIRECTORY = System.getProperty("user.home","") + "/mayday/plugins" + PLUGINROOT_UNCONFIGURED;
        public static final String DEFAULT_WEBSTART_ADDITIONAL_PLUGIN_DIRECTORY = System.getProperty("user.home","") + "/mayday/additionalPlugins";
        
       
        public final static PathSetting EditorPath;
        
        public final static Settings EditorSettings = new Settings(
        		new HierarchicalSetting("Text Editor")
        		.addSetting(EditorPath = new PathSetting("Path",null,"editor",false,false,true)),
        		NODE_PREFS.node("Editor")
        );        

        public final static StringSetting ProxyHost;
        public final static IntSetting ProxyPort;
        public final static BooleanSetting ProxyActive;

        public final static Settings InternetSettings = new Settings(
        		new HierarchicalSetting("Internet")
        		.addSetting(new HierarchicalSetting("HTTP Proxy")
        			.addSetting(ProxyActive = new BooleanSetting("Use proxy",null,false))
        			.addSetting(ProxyHost = new StringSetting("Proxy host URL",null,"",true))
        			.addSetting(ProxyPort = new IntSetting("Port",null,3128))
        		),
        		NODE_PREFS.node("Internet")
        );

        
        public final static BooleanSetting useNativeLAF, centerDialogs, fullScreen, showMainToolbar, useNewLayout,
        							showPLToolTips, showTaskManager;
        public final static IntSetting fontSize;
        public final static ObjectSelectionSetting<String> showOverviewDialog;
        
        public final static Settings AppearanceSettings = new Settings(
        		new HierarchicalSetting("Appearance")
        		.addSetting(new HierarchicalSetting("General")
        			.addSetting(useNativeLAF = new BooleanSetting("Use native look&feel (requires restart)",
        					"Native l&f might not work correctly on Windows systems", false))
        			.addSetting(fontSize = new IntSetting("Font size for non-native l&f",null,10,0,null,false,false))
        			.addSetting(fullScreen = new BooleanSetting("Start in full-screen mode",null,true))
        			.addSetting(centerDialogs = new BooleanSetting("Center dialogs on screen",null,true))
        		)
        		.addSetting(new HierarchicalSetting("Main window")
        			.addSetting(showMainToolbar = new BooleanSetting("Show main window toolbar",null,false))
        			.addSetting(useNewLayout = new BooleanSetting("Use new main window layout",null,true))        			
        			.addSetting(showPLToolTips = new BooleanSetting("Show tooltips for ProbeLists",null,true))
        		)
        		.addSetting(ProbeListImage.setting)        		
        		.addSetting(new HierarchicalSetting("DataSet overview")
        			.addSetting(showOverviewDialog = new ObjectSelectionSetting<String>(
        					"Display overview on DataSet import",null,1,
        					new String[]{"no","new style","old style"}))
        		)
        		.addSetting(new HierarchicalSetting("TaskManager display")
        			.addSetting(showTaskManager = new BooleanSetting("Automatically open the Task Manager when tasks start",null,true))
        		)        		
        		,
        		NODE_PREFS.node("Appearance")
        );
        
        public static final String KEY_LASTOPENDIR="LastOpenDir";
        public static final String DEFAULT_LASTOPENDIR=System.getProperty("user.home","");
        
        public static final String KEY_LASTSAVEDIR="LastSaveDir";
        public static final String DEFAULT_LASTSAVEDIR=System.getProperty("user.home","");

        
        /**
         * For plugins: Get the Preferences plugin node as your root node
         * for your preferences.
         * 
         * @return The Preferences plugin node.
         */
        public static Preferences getPluginPrefs()
        {
            Preferences prefs=NODE_PREFS.node(NODE_PLUGINS);
            return prefs;
        }
        
        public static Preferences getLibrariesPrefs()
        {
            return NODE_PREFS.node(
                isWebstartApplication() ? 
                        NODE_LIBRARIES_WEBSTART_MODE
                        :NODE_LIBRARIES
            );
        }
        
        /**
         * Get the plugin directory where the plugins can be found.
         * This returns either the "normal" plugin directory or the
         * webstart additional plugin directory if Mayday was started via webstart.
         * In the latter case the plugin directory will be created if necessary.
         * 
         * @return
         */
        public static String getPluginDirectory()
        {
            Preferences p = getPluginPrefs();
            //String dir = null;
            
            File f = isWebstartApplication()                  
                ? new File( p.get(
                    WEBSTART_ADDITIONAL_PLUGIN_DIRECTORY, 
                    DEFAULT_WEBSTART_ADDITIONAL_PLUGIN_DIRECTORY))
                : new File( p.get(
                    PLUGIN_DIRECTORY, 
                    DEFAULT_PLUGIN_DIRECTORY));
            
            if (f.getName().endsWith(PLUGINROOT_UNCONFIGURED)) {
        		f = new File(f.getPath().replace(PLUGINROOT_UNCONFIGURED,""));
        		Mayday.firstTimeRun = true;
            }
              
            if(!f.exists() && !f.mkdirs()) {
            	throw new RuntimeException("The plugin directory '" + f.getAbsolutePath() + "' does not exist" +
            	" and could not be created.\n(Do you have write permissions in the parent folder?)");
            }
            	
            return f.getAbsolutePath();
        }
        
        public static void storePluginDirectory(File path)
        {
            Preferences p = getPluginPrefs();
            p.put( isWebstartApplication() 
                    ? WEBSTART_ADDITIONAL_PLUGIN_DIRECTORY
                    : PLUGIN_DIRECTORY, 
                path.getAbsolutePath()
            );            
        }
        
        public static Preferences getSubNode(String node)
        {
            Preferences prefs=NODE_PREFS.node(node);
            return prefs;
        }
        
        public static void save()
        {
            try
            {
              NODE_PREFS.flush();              
//              NODE_PREFS.exportSubtree(new FileOutputStream(PREFS_FILE));
//            }catch ( IOException exception )
//            {
//                JOptionPane.showMessageDialog( null,
//                    exception.getMessage() + "\n\n" +
//                    "No preferences were saved.",
//                    MaydayDefaults.Messages.ERROR_TITLE,
//                    JOptionPane.ERROR_MESSAGE ); 
            }catch ( BackingStoreException exception )
            {
                JOptionPane.showMessageDialog( null,
                    exception.getMessage() + "\n\n" +
                    "No preferences were saved.",
                    MaydayDefaults.Messages.ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE ); 
            }
        }
    }  
    
    
    /**
     * Load images. This is a wrapper for PluginInfo.getIcon()
     * @param relativePath The image path relative to the mayday root, e.g mayday/core/icons/mayday.png
     * @return
     */    
    public static BufferedImage createImage( String relativePath )
    {
    	ImageIcon img = PluginInfo.getIcon(relativePath);
    	if (img!=null)
    		return (BufferedImage)img.getImage();
    	else
    		return new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB);
    }
    
   
    public static final String SPLASH_SCREEN_IMAGE = "mayday/images/splash.png";	 
    public static final String PROGRAM_ICON_IMAGE = "mayday/images/icon32.gif";
    public static final String PLUGIN_ICON_IMAGE_16 = "mayday/images/plugin16.gif";
    
    public static ImageIcon SPLASH_SCREEN_IMAGEICON = new ImageIcon();
    // fb: this is set by pluma.gui.ProgressSplash.loadImage()
    
    //a more common way to get the file:    
    public static final String PROBELIST_DTD="probelist.dtd";
    public static final String PROBELIST_DTD_FILE="mayday/core/"+PROBELIST_DTD;    
    
    //end MZ

    // FB 2011-11-30: Mac Java does not parse <argument> JNLP tags, so we need to pass arguments in an extra variable
<<<<<<< HEAD
    // AG 2015-11-25: In newer java versions, parameters passed via jnlp files should have 'jnlp.' prefix
    public static final String WEBSTART_ARGUMENTS_KEY = "jnlp.mayday.webstart.arguments";
    //MZ 2006-06-25
    //webstart application
    public static final String WEBSTART_PROPERTIES_KEY = "jnlp.mayday.webstart";
=======
    public static final String WEBSTART_ARGUMENTS_KEY = "mayday.webstart.arguments";
    
    //MZ 2006-06-25
    //webstart application
    public static final String WEBSTART_PROPERTIES_KEY = "mayday.webstart";
>>>>>>> bd8805447b59c9475dfbcdf6f975397ad3c2209e
    public static boolean isWebstartApplication() {
        return System.getProperty(WEBSTART_PROPERTIES_KEY)!=null &&
        Boolean.parseBoolean(System.getProperty(WEBSTART_PROPERTIES_KEY));
    }
    
    
    
    //Riester 2004/09/06 unicode
    // NG: add authors Riester, Symons and Zschunke
    public static final String AUTHORS = "Nils Gehlenborg, Janko Dietzsch and Kay Nieselt.";
    
    public static final String AUTHORS_INSTITUTION =
    "Junior Research Group Integrative Transcriptomics<br>" +
    "Eberhard-Karls-Universit\u00E4t, T\u00FCbingen, Germany<br><br>" +
    "http://it.informatik.uni-tuebingen.de";
    
    public static final String AUTHORS_CORE = 
    	"Nils Gehlenborg, Janko Dietzsch, Matthias Zschunke, <br>Stephan Symons, Florian Battke, G\u00FCnter J\u00E4ger";
    
    // help files
    public static final String MANUAL_PATH="doc/manual/";
    public static final String MANUAL_HTML="html/node1.html";
    
    // probes
    public static final boolean REMOVE_DANGLING_IMPLICIT_PROBES = true;  
    
    // probe lists
    public static final String NEW_PROBE_LIST_NAME = "new";
    public static final String GLOBAL_PROBE_LIST_NAME = "global";
    public static final String PROBE_LIST_EXTENSION = "pls"; 
    public static final String DEFAULT_SVG_EXTENSION = "svg"; 
    public static final String DEFAULT_PNG_EXTENSION = "png"; 
    public static final String DEFAULT_JPEG_EXTENSION = "jpg"; 
    public static final String DEFAULT_TIFF_EXTENSION = "tif"; 
    public static final String DEFAULT_TABULAR_EXPORT_EXTENSION = "dat";
    //KD: 07.03.2006 added new FileFormat Extensions 
    public static final String DEFAULT_GMX_EXTENSION = "gmx";
    public static final String DEFAULT_GMT_EXTENSION = "gmt";
    public static final String DEFAULT_GRP_EXTENSION = "grp";
    //end KD
    
    public static final int SVG_FORMAT = 0x01;
    public static final int PNG_FORMAT = 0x02;
    public static final int JPEG_FORMAT = 0x03;
    public static final int TIFF_FORMAT = 0x04;
    
    // global counters
    public static int s_newProbeListCounter = 0;
    public static int s_globalProbeListCounter = 0;
    public static int s_visualizerCounter = 0;
    
    // global paths  //MZ 26.01.04: deprecated!
    public static String s_lastExportPath = "";
    
    
    // probe list layout
    public static final Color DEFAULT_PROBE_LIST_COLOR = Color.black;
    
    // dialogs
    public static final int DEFAULT_VSPACE = 10;
    public static final EmptyBorder DIALOG_DEFAULT_BORDER = (EmptyBorder)BorderFactory.createEmptyBorder( 10, 10, 10, 10 );
    
    
    // plots
    public static final double VERTICAL_IN_PLOT_SPACER = 0.25;
    public static final int PLOTS_PER_SCREEN_WIDTH = 3;
    public static final double PLOT_ASPECT_RATIO = 4.0/3.0;
    
    // fb: 071213: Code changed to allow running without display
    // Why?, you may wonder. The reason is that the Manifest Scanner needs this class
    // and is run from a console process (Ant)
    public static int DEFAULT_PLOT_WIDTH = 800;
    
    static {
    	try {
    		GraphicsDevice[] sd = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
    		//Use first/primary screen 
    		Rectangle primaryScreen = sd[0].getDefaultConfiguration().getBounds();
    		DEFAULT_PLOT_WIDTH=primaryScreen.width/PLOTS_PER_SCREEN_WIDTH;
    	} catch (HeadlessException e) {
    		System.err.println("Can't get screen size? Running on Linux without X11?");
    	}
    }
    
    
    public static final int DEFAULT_PLOT_HEIGHT = (int)((double)DEFAULT_PLOT_WIDTH/PLOT_ASPECT_RATIO);
    
    
    public static final double DEFAULT_LOOKUP_AREA = 7.5;
    
    public static final boolean TEXT_ANTIALIASING = true;
    public static final int DEFAULT_PLOT_FONT_SIZE = 10;
    public static final int DEFAULT_PLOT_MAJOR_SCALE_FONT_SIZE = 9; // 11
    public static final int DEFAULT_PLOT_MINOR_SCALE_FONT_SIZE = 9;
    public static final int DEFAULT_PLOT_LARGE_LEGEND_FONT_SIZE = 9; // 11
    public static final int DEFAULT_PLOT_SMALL_LEGEND_FONT_SIZE = 9;
    public static final int DEFAULT_PLOT_CAPTION_FONT_SIZE = 16;
    public static final Font DEFAULT_PLOT_FONT = new Font( "Helvetica", Font.PLAIN, MaydayDefaults.DEFAULT_PLOT_FONT_SIZE ); 
    public static final Font DEFAULT_PLOT_MAJOR_SCALE_FONT = new Font( "Lucida Sans",
        Font.BOLD,
        MaydayDefaults.DEFAULT_PLOT_MAJOR_SCALE_FONT_SIZE ); 
    public static final Font DEFAULT_PLOT_SMALL_LEGEND_FONT = new Font( "Lucida Sans",
        Font.PLAIN,
        MaydayDefaults.DEFAULT_PLOT_SMALL_LEGEND_FONT_SIZE ); 
    public static final Font DEFAULT_PLOT_LARGE_LEGEND_FONT = new Font( "Lucida Sans",
        Font.BOLD,
        MaydayDefaults.DEFAULT_PLOT_LARGE_LEGEND_FONT_SIZE ); 
    public static final Font DEFAULT_PLOT_MINOR_SCALE_FONT = new Font( "Lucida Sans",
        Font.PLAIN,
        MaydayDefaults.DEFAULT_PLOT_MINOR_SCALE_FONT_SIZE ); 
    public static final Font DEFAULT_PLOT_CAPTION_FONT = new Font( "Lucida Sans",
        Font.BOLD,
        MaydayDefaults.DEFAULT_PLOT_CAPTION_FONT_SIZE );
    
    public static final int DEFAULT_PLOT_COLOR_SCALE_HEIGHT = 16;
    
    
    public static final FontRenderContext DEFAULT_FONT_RENDER_CONTEXT = new FontRenderContext( null,
        MaydayDefaults.TEXT_ANTIALIASING,
        false );
    public static final int PLOT_ANNOTATION_DISTANCE = MaydayDefaults.DEFAULT_PLOT_FONT_SIZE / 2;
    
    public static final double DEFAULT_EXPRESSION_IMAGE_BOX_WIDTH = 1.5*DEFAULT_PLOT_LARGE_LEGEND_FONT_SIZE;
    public static final double DEFAULT_EXPRESSION_IMAGE_BOX_HEIGHT = DEFAULT_PLOT_LARGE_LEGEND_FONT_SIZE;
    
    public static final int X_MAJOR_TICK_EXPONENT = 0; 
    public static final int X_MINOR_TICK_EXPONENT = 2;
    public static final int Y_MAJOR_TICK_EXPONENT = 0; 
    public static final int Y_MINOR_TICK_EXPONENT = 2;
    
    // plot export
    public static final float DEFAULT_JPEG_QUALITY = 1.0f;
    
    // colors
    public static class Colors
    {
        public static final Color PLOTTING_AREA = Color.white;
        public static final Color Y_MAJOR_GRID_LINE = Color.lightGray;
        public static final Color Y_MINOR_GRID_LINE = new Color( 0xEEEEEE );
        public static final Color Y_MAJOR_GRID_SCALE = Color.black;
        public static final Color Y_MINOR_GRID_SCALE = Color.black;
        public static final Color X_MAJOR_GRID_LINE = Color.lightGray;
        public static final Color X_MINOR_GRID_LINE = new Color( 0xEEEEEE );
        public static final Color X_MAJOR_GRID_SCALE = Color.black;
        public static final Color X_MINOR_GRID_SCALE = Color.black;
        public static final Color MODE_LEGEND = Color.black;
        public static final Color PROBE_LIST_LEGEND = Color.black;
        public static final Color PROBE_LIST_LEGEND_HIDDEN = Color.gray;
        public static final Color CAPTION = Color.black;
        public static final Color DEFAULT_SELECTION_COLOR = Color.red;
    }
    
    
    
    // fall-back values (used when a value cannot be calculated because of missing information) 
    public static final double FALLBACK_SEGMENT_WIDTH = 4.0; // assume about 10 experiments
    public static final double FALLBACK_RANGE = 30.0;
    public static final double FALLBACK_MAX_VALUE = 15.0;
    public static final double FALLBACK_MIN_VALUE = -15.0;
    
    
    // strings
    public static final String DEFAULT_ON_TEXT = "On";
    public static final String DEFAULT_OFF_TEXT = "Off";
    public static final String DEFAULT_HIDE_TEXT = "Hide";
    public static final String DEFAULT_SHOW_TEXT = "Show";
    public static final String IDENTIFIER_NAME = "Identifier";
    
    
    // keys
    public static final KeyStroke DEFAULT_ZOOM_IN_ACCELERATOR_KEY = KeyStroke.getKeyStroke( KeyEvent.VK_PLUS,
        KeyEvent.CTRL_DOWN_MASK,
        false );
    public static final KeyStroke DEFAULT_ZOOM_OUT_ACCELERATOR_KEY = KeyStroke.getKeyStroke( KeyEvent.VK_MINUS,
        KeyEvent.CTRL_DOWN_MASK,
        false );
    
    public static final KeyStroke DEFAULT_FIT_FRAME_ACCELERATOR_KEY = KeyStroke.getKeyStroke( KeyEvent.VK_F,
        KeyEvent.CTRL_DOWN_MASK,
        false );
    
    public static final KeyStroke DEFAULT_FIRST_PAGE_ACCELERATOR_KEY = KeyStroke.getKeyStroke( KeyEvent.VK_HOME,
        KeyEvent.CTRL_DOWN_MASK,
        false );
    
    public static final KeyStroke DEFAULT_LAST_PAGE_ACCELERATOR_KEY = KeyStroke.getKeyStroke( KeyEvent.VK_END,
        KeyEvent.CTRL_DOWN_MASK,
        false );
    
    public static final KeyStroke DEFAULT_NEXT_PAGE_ACCELERATOR_KEY = KeyStroke.getKeyStroke( KeyEvent.VK_PAGE_DOWN,
        KeyEvent.CTRL_DOWN_MASK,
        false );
    
    public static final KeyStroke DEFAULT_PREVIOUS_PAGE_ACCELERATOR_KEY = KeyStroke.getKeyStroke( KeyEvent.VK_PAGE_UP,
        KeyEvent.CTRL_DOWN_MASK,
        false );

    public static String JAR_LIST_SEPARATOR = System.getProperty( "path.separator" );
    
   
    
    // messages
    public static class Messages	
    {
        public static final String REPLACEMENT = "%REPLACE";
        public static final String ERROR_TITLE = "Error";
        public static final String WARNING_TITLE = "Warning";
        public static final String INFORMATION_TITLE = "Information";
        public static final String QUESTION_TITLE = "Question";
        public static final String WRONG_FILE_FORMAT = "File \"" +
        REPLACEMENT +
        "\" contains illegal data.";
        public static final String FILE_NOT_FOUND = "File \"" +
        REPLACEMENT +
        "\" not found.";
        public static final String PROBE_LIST_NOT_UNIQUE = "A probe list named \"" +
        REPLACEMENT +
        "\" already exists.";
        public static final String FILE_NAME_NOT_UNIQUE = "File \"" +
        REPLACEMENT +
        "\" already exists. Would you like to overwrite this file?";
        public static final String DATA_SET_NOT_UNIQUE = "A data set named \"" +
        REPLACEMENT +
        "\" already exists.";
        public static final String ENTER_NEW_NAME = "Enter a new name.";
        public static final String ENTER_OTHER_NAME = "Enter another name.";
        public static final String ENTER_PROBES_PER_PAGE = "Enter number of probes per page. This must be greater than 0.";
        public static final String WRONG_INPUT_FORMAT = "Wrong input format.";
        public static final String INTEGER_EXPECTED = "Expected input is an integer number.";
        public static final String NOT_IMPLEMENTED = "Not implemented.";
        public static final String GENERAL_EXCEPTION = "An exception occurred.\nPlease restart " + PROGRAM_NAME + ".";
        public static final String UNABLE_TO_OPEN_DATA_SET = MaydayDefaults.PROGRAM_NAME + " is unable to open the requested data set.";
        public static final String UNABLE_TO_OPEN_PROBE_LIST = MaydayDefaults.PROGRAM_NAME + " is unable to open the requested data set.";
        public static final String UNABLE_TO_ZOOM = MaydayDefaults.PROGRAM_NAME + " is unable to zoom.";
        public static final String UNABLE_TO_PAINT = MaydayDefaults.PROGRAM_NAME + " is unable repaint the plot.";
        public static final String UNABLE_TO_CHANGE_SCALES = MaydayDefaults.PROGRAM_NAME + " is unable to toggle the state of the\nrequested scale.";
        public static final String UNABLE_TO_CHANGE_CAPTION = MaydayDefaults.PROGRAM_NAME + " is unable to toggle the state of the\ncaption.";
        public static final String UNABLE_TO_CHANGE_LEGEND = MaydayDefaults.PROGRAM_NAME + " is unable to toggle the state of the\nlegend.";
        public static final String UNABLE_TO_EXPORT = MaydayDefaults.PROGRAM_NAME + " is unable to export this " +
        "view\nto the requested file format.";
        public static final String OUT_OF_MEMORY = "The operation you are trying to perform exceeds\nthe " +
        "memory available to the Java Virtual Machine\nrunning " +
        "this application.\nSee the manual of your Java installation " +
        "to find\nout how to provide more memory for the\nJava Virtual Machine.\n\n" +
        "You are strongly encouraged to restart " + MaydayDefaults.PROGRAM_NAME + " now.";
        public static final String UNABLE_TO_FIND_BATIK = "Unable to find Apache Project Batik libraries required for image export.";
        public static final String SETUP_BATIK = "Either you have not installed the Apache Project Batik libraries or you did\n" +
        "not setup the class path correctly. See the documentation that came\n" +
        "with this version of " + MaydayDefaults.PROGRAM_NAME + " on how to setup " +
        "the class path correctly.\nThere you will also find information about where to " +
        "obtain the required\nlibraries.";
        public static final String GRID_TO_LARGE = MaydayDefaults.PROGRAM_NAME + " is unable to create a grid with the\ndimensions you entered.";                                               
        
        public static final String NO_PLUGIN_SELECTED = "Please select a plug-in first.";                                           
    }
    
    // plugins
    public static class Plugins  
    {
        /*
         * Categories
         */
        public static final String CATEGORY_DATAMINING = "Data Mining";
        public static final String CATEGORY_FILTERING = "Filtering";
        public static final String CATEGORY_INTERPRETER="Interpreter";
       
        // SY: 21.04.04 Adding Category Data Import
        public static final String CATEGORY_DATAIMPORT="Import"; 
        // end SY

        // fb: 070718: added to allow classification of import pathways
        public static final String CATEGORY_DATAIMPORT_FILE="Import from file";

        public static final String CATEGORY_PROBELIST_IMPORT="Import Probelist";
        
        // NG: 2005-10-24: category for data set import
        // fb: 070721 - removed this to find all plugins still using that category
        public static final String CATEGORY_DATASETIMPORT="Data Set Import"; // FB: 070717 - removed because of duplication (DATAIMPORT)
        // end NG
        
        // NG: 2005-10-24: category for export
        public static final String CATEGORY_EXPORT="Export"; // deprecated
        // end NG
        // fb: 070809: Added to do for export what has been done for import before
        public static final String CATEGORY_DATAEXPORT="Data Set Export";

        public static final String CATEGORY_DATAEXPORT_FILE="Export to file";
        
        public static final String CATEGORY_PROBELIST_EXPORT="Export Probelist";
        
        
        // SY: 14.07.04 Adding Category Database Connectivity
        public static final String CATEGORY_DATABASE="Database";
        // end SY
        // NG: 2004-12-08: category for statistics
        public static final String CATEGORY_STATISTICS="Statistics";
        // end NG

        public static final String CATEGORY_CLUSTERING = "Clustering";
        public static final String SUBCATEGORY_CLUSTERINGEXTRAS = "Quality Assessment, Co-clustering, etc.";

        
        // NG: 2005-01-24: category for scoring functions
        public static final String CATEGORY_SCORING="Scoring Functions";
        // end NG
        
        // NG: 2005-02-11: category for test plugins 
        public static final String CATEGORY_TEST="Test & Debug";
        // end NG
        
        public static final String CATEGORY_SETOPERATIONS = "Set operations";

        
        // NG: 2005-12-08: category for visualization
        public static final String CATEGORY_VISUALIZATION="Visualization";
        // end NG

        // NG: 2005-12-08: category for meta information
        public static final String CATEGORY_METAINFORMATION="Meta Information";
        // end NG

        // NG: 2005-12-08: category for utilities
        public static final String CATEGORY_UTILITIES="Utilities";
        // end NG
        
        //KD: 2006-03-20: category for 
        //Functional Analysis of GeneExpression (FAGE)
        public static final String CATEGORY_FAGE="FAGE";
        //end KD
        
        //fb: 20060803: category for plugins that users should not see
        public static final String CATEGORY_INVISIBLE = "Invisible Plugins";
        //end fb
        
        // fb: 20071108: category for nastasja
        public static final String CATEGORY_RAWDATA = "Raw Data";
        // end 
        
        // fb: 20071108: category for new plotting framework
        public static final String CATEGORY_PLOT = "Visualization Plot";
        // end 
        
        /*
         * Subcategories
         */
        public static final String SUBCATEGORY_STATISTICS = "Statistics";
        public static final String SUBCATEGORY_RELEVANCE = "Scoring";
        public static final String SUBCATEGORY_PROBELISTS = "Probe Lists";
        public static final String SUBCATEGORY_IMPORT = "Import";
    }
    
    protected static Boolean debugMode;
    
    public static boolean isDebugMode() {
    	if (debugMode==null) {
    		String DEBUG = System.getenv("MAYDAY_DEBUG");
    		debugMode = ( DEBUG!=null && DEBUG.trim().toLowerCase().equals("true") );
    	}
	    return debugMode;
    }
    
    @Deprecated // deprecated because it is called automatically from MaydayDialog and MaydayFrame
    public static void centerWindowOnScreen(java.awt.Window window) {
    	if (MaydayDefaults.Prefs.centerDialogs.getBooleanValue()) {
        	int w = window.getWidth();
        	int h = window.getHeight();
        	GraphicsDevice[] sd = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        	//Use first/primary screen 
        	Rectangle primaryScreen = sd[0].getDefaultConfiguration().getBounds();

        	window.setLocation(
        			new Point(
        					((primaryScreen.width-w)/2)+primaryScreen.x,
        					((primaryScreen.height-h)/2)+primaryScreen.y			
        			)
        	);
        }
    }
    
    
    public static Preferences getMostRecentFiles() {
    	return Prefs.NODE_PREFS.node(Prefs.NODE_MOST_RECENT_FILES);
    }
    
}

