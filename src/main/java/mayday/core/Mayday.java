package mayday.core;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.BackingStoreException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.metal.MetalLookAndFeel;

import mayday.core.datasetmanager.gui.DataSetManagerView;
import mayday.core.gui.MaydayFrame;
import mayday.core.gui.MessageFrame;
import mayday.core.gui.PluggableViewElementContainer;
import mayday.core.gui.PreferencesDialog;
import mayday.core.gui.StatusBarMemoryInfo;
import mayday.core.io.dataset.ZippedSnapshot.Import;
import mayday.core.io.nativeformat.FileRepository;
import mayday.core.parameters.MaydayParameters;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.prototypes.GenericPlugin;
import mayday.core.tasks.AbstractTask;
import mayday.core.tasks.TaskManager;
import mayday.core.tasks.gui.StatusBar;
import mayday.core.tasks.gui.TaskManagerStatusBarItem;

/**
 * @author Nils Gehlenborg
 * @author Janko Dietzsch
 * @version 0.2
 */
@SuppressWarnings("serial")
public final class Mayday extends MaydayFrame implements WindowListener
{
	public static Mayday sharedInstance;
	
	protected static final TaskManager TASK_MANAGER = TaskManager.sharedInstance;
	protected static final StatusBar STATUS_BAR = new StatusBar( new double[] { 7, 1, 1 } );
	protected static MessageFrame MESSAGE_WINDOW;

	public static ImageIcon Mayday_Icon = null;
	public static MaydayParameters parameters;
	
	public static boolean firstTimeRun = false;
	
	protected static JPanel mainBox;
	protected static JSplitPane splitPane;
	protected PluggableViewElementContainer plugContainer;

	private Mayday() throws Exception {
		if (sharedInstance!=null) {
			throw new RuntimeException("Only one instance of Mayday can be started in one process.");
		}
		sharedInstance = this;
		setTitle( MaydayDefaults.PROGRAM_NAME );
		addWindowListener(this);
		init();
	}
	
	/**
	 * The main function creates an instance of Mayday and brings up the
	 * main window.
	 * 
	 * @throws Exception All exceptions that are not caught at other locations are
	 *                   passed to the runtime environment.  
	 */
	public static void main( String... args ) {
		
		System.out.println("Mayday starting...");

		if (MaydayDefaults.isWebstartApplication()) {
			// work around Mac Java's deficiency in correctly handling JNLP <argument> tags
			String prop = System.getProperty(MaydayDefaults.WEBSTART_ARGUMENTS_KEY);
			if (prop!=null) { // these properties override the webstart arguments.
				args = prop.split("[\\s]+");
			}
		}
		
		parameters = new MaydayParameters(args);
		
		if (parameters.isDebugMode())
			MaydayDefaults.debugMode=true;
		
		
		// inform users about the possibility to use DEBUG mode. This goes to the console
		if (MaydayDefaults.debugMode==null) {
			System.out.println("To start Mayday in DEBUG mode, use \"-debug\" or set the environment variable \"MAYDAY_DEBUG\" to \"TRUE\"");
		}
		
		// LAF needs to be set before any window is created, even before the message window!
		// However, then the output in Webstart is not captured, so the messagewindow will have the wrong l&f, oh well.
		getMessageWindow();
		
		System.out.println("Command line arguments: "+Arrays.toString(args));
		
		// inform users about the possibility to use DEBUG mode. This goes to the message window
		if (!MaydayDefaults.debugMode) {
			System.out.println("To start Mayday in DEBUG mode, use \"-debug\" or set the environment variable \"MAYDAY_DEBUG\" to \"TRUE\"");
		}

		// calling setLookAndFeel() implicitely starts up the plugin manager.
		setLookAndFeel(MaydayDefaults.isDebugMode());

		if (MaydayDefaults.isDebugMode()) {
			System.out.println("// System Properties:");
			String s = System.getProperties().toString().replaceAll(", ", ", \n\t");
			if (s.length()>2)
				s = s.substring(1, s.length()-1);
			System.out.print("\t");
			System.out.println(s);
			System.out.println("// End of System Properties");
		}

		// Create the main MAYDAY instance and show the window
		try	{
			Mayday l_mayday = new Mayday();			
			PluginManager.getInstance().startCore();
			if (parameters.showMain()) {
				l_mayday.setVisible( true );
				// go full-screen
				if (MaydayDefaults.Prefs.fullScreen.getBooleanValue()) {
					l_mayday.setExtendedState(JFrame.MAXIMIZED_BOTH);
				}
			}
		}
		catch ( Exception exception )
		{
			exception.printStackTrace();      
			JOptionPane.showMessageDialog( null,
					MaydayDefaults.Messages.GENERAL_EXCEPTION + "\n\n"
					+ exception.getMessage(), MaydayDefaults.Messages.ERROR_TITLE,
					JOptionPane.ERROR_MESSAGE );
		}    
		
		
		// load files from the command line
		List<String> loadFiles = parameters.getFiles();
		// open MRU?
		if (parameters.openMRU()) {
			String f = FileRepository.getMostRecentFile();
			if (f!=null)
				loadFiles.add(f);		
		}
		if (loadFiles.size()>0)
			Import.loadFilesAtStartup(loadFiles);		
				
		// run plugins specified in command line
		String[] plugID = parameters.getPluginsToRun();
		if (plugID!=null) {
			for (String s : plugID) {
				PluginInfo pli = PluginManager.getInstance().getPluginFromID(s);
				if (pli!=null) {
					AbstractPlugin ap = pli.getInstance();
					if (ap instanceof GenericPlugin) {
						System.out.println("Executing plugin "+s);
						GenericPlugin gp = (GenericPlugin)ap;
						try {
							gp.run();
						} catch (Throwable ex) {
							System.err.println("Executing plugin "+s+" failed, because of:");
							System.err.println(ex.getMessage());
							ex.printStackTrace();
						}
					} else {
						System.err.println("Executing plugin "+s+" failed, not a GenericPlugin");
					}
				} else {
					System.err.println("Executing plugin "+s+" failed, no such plugin found");
				}
			}
		}
		
		
		// some debugging help for webstart applications (only works with env var MAYDAY_DEBUG=TRUE)
		if (MaydayDefaults.isDebugMode() && MaydayDefaults.isWebstartApplication()) {
			// dump plugin list
			try {
				PluginManager.getInstance().dumpToFile(File.createTempFile("MaydayWS", ".pluginlist"));
			} catch (IOException e) {
				// too bad
			}
		}
		
		// if run for the first time, ask user to set plugin root
		if (firstTimeRun) {
			if (JOptionPane.showConfirmDialog(sharedInstance, 
					"You seem to be running Mayday for the first time.\n" +
					"It is likely that you need to configure the plugin path.\n" +
					"Would you like to do so now?\n\n" +
					"(You can do this later by selecting Mayday->Preferences->Plugins)",
					"Configure plugin path",
					JOptionPane.YES_NO_OPTION
				) == JOptionPane.YES_OPTION) 
			{
				PreferencesDialog pd = new PreferencesDialog();
				pd.selectPluginPrefPane();
				pd.setVisible(true);
			}
		}
	}
	
	/**
	 * Set up the look and feel for Mayday. 
	 */
	@SuppressWarnings("unchecked")
	private static void setLookAndFeel(boolean verbose)	{
		
		if (verbose)	{
			System.out.println("Using Look and Feel: "+UIManager.getLookAndFeel());
			System.out.println("Available lool&feel implementations:");
			for (LookAndFeelInfo lafi : UIManager.getInstalledLookAndFeels()) 
				System.out.println("- "+lafi.getName()+" "+lafi.getClassName());
		}
		
		try {
			/* this will start the plugin manager, because:
			 * 1. The MaydayDefaults class is accessed for the first time, thus all members are initialized
			 * 2. Some of the pref members are plugintypesettings (ManipulationMethodSetting is responsible here)
			 * 3. PluginTypeSetting asks the PluginManager for all Plugins it needs to fill its own members
			 * 4. PluginManager is called for the first time (getInstance()) and starts to work
			 */			
			if (!MaydayDefaults.Prefs.useNativeLAF.getBooleanValue()) {


				// FB 060313: Windows and MacOS L&F do NOT correctly display JTrees with custom cellrenderers. 
				// From now on we will use the metal L&F which is working.
				//			
				// SY 090320: Metal L&F sucks. Allow fearless users to override this and use the native L&F. 
				// No one will take an application with a nonstandard L&F seriously! 

				//use ugly metal l&f
				UIManager.setLookAndFeel( MetalLookAndFeel.class.getCanonicalName() );

				//setup font for metal
				int fsize = MaydayDefaults.Prefs.fontSize.getIntValue();

				Font f = new Font("Sans", Font.PLAIN, fsize);
				java.util.Enumeration keys = UIManager.getDefaults().keys();
				while (keys.hasMoreElements()) {
					Object key = keys.nextElement();
					Object value = UIManager.get (key);
					if (value instanceof javax.swing.plaf.FontUIResource)
						UIManager.put (key, f);
				}

			} else {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}		
		} catch ( Exception e ) {
			System.err.println("Could not set Look&Feel: "+e.getMessage());			
		}
		
		if (verbose)
			System.out.println("Now using Look and Feel: "+UIManager.getLookAndFeel());
		
		
	}


	/**
	 * the messages window
	 */
	
	public static synchronized MessageFrame getMessageWindow()
	{
		if(MESSAGE_WINDOW == null) //initially
		{
			/*
			 * keep the original output streams
			 */
			PrintStream out = System.out;
			PrintStream err = System.err;

			/*
			 * Note: this will set the system stream to MessageFrame.MessageOutputStream
			 */
			MESSAGE_WINDOW = new MessageFrame();

			/*
			 * If we are in debug mode, it is better to reset the streams to the defaults so
			 * we can enjoy all the facilities our preferred IDE provides :)
			 */
			if( MaydayDefaults.isDebugMode() )
			{
				System.out.println("Mayday is running in debug mode, all output is directed to the default streams.");
				System.setErr(err);
				System.setOut(out);
				
				if (MaydayDefaults.isWebstartApplication()) {
					try {
						System.setErr(new PrintStream(new FileOutputStream(File.createTempFile("MaydayWS", ".err"))));
						System.setOut(new PrintStream(new FileOutputStream(File.createTempFile("MaydayWS", ".log"))));
					} catch (Exception e) {
						// well, sucks :-(
					}
				}
			}
		}
		return MESSAGE_WINDOW;
	}

	protected void fillMainBox() {
		mainBox.add (splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true), BorderLayout.CENTER );
		splitPane.setRightComponent( plugContainer = new PluggableViewElementContainer() ); 
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(1);
		mainBox.add( STATUS_BAR, BorderLayout.SOUTH );
		splitPane.setDividerLocation(1d);
		replaceDataSetManagerView();
	}

	public void addPluggableViewElement( Component element, String title ) {
		plugContainer.addElement( element, title );		
	}
	
	public void replaceDataSetManagerView( ) {
		splitPane.setLeftComponent( DataSetManagerView.getInstance().getGUIComponent() );		
	}

	
	/**
	 * Initializes the main window.

	 * @throws Exception Passes all exceptions that are not caught at other locations
	 *                   forward to the main function.
	 */
	protected void init( ) throws Exception
	{
		// create the menu bar, so that pluma can write into it
		JMenuBar l_menuBar = new JMenuBar();
		setJMenuBar( l_menuBar );

		// start Plugin Scanner now and have it run in a separate thread
		AbstractTask plumaTask = startPluginScanner();

		// create core data structures: 
		// - DataSetManager holds all DataSets
//		DataSetManager l_dataSetManager = DataSetManager.singleInstance;
		// - DataSetManagerView displays the content of DataSetManager

		// create main window components
		Dimension l_screen = Toolkit.getDefaultToolkit().getScreenSize();

		mainBox = new JPanel(new BorderLayout());
		fillMainBox();
		getContentPane().add( mainBox );

		// Set window position and size
		setSize( DataSetManagerView.getInstance().getGUIComponent().getPreferredSize().width + 50, (int) ( l_screen.getHeight() * 0.8 ) );

		//setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );

		// Build status bar
		STATUS_BAR.setStatusItemAt(0, new StatusBarMemoryInfo());
		STATUS_BAR.setStatusItemAt(2, TaskManagerStatusBarItem.singleInstance);
		TaskManagerStatusBarItem.singleInstance.addChangeListener( STATUS_BAR );

		// Wait for the Plugin Scanner to finish before displaying the Main window
		plumaTask.waitFor();
		
		// add runtime shutdown hook for proper deinit of all plugins
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				PluginManager.getInstance().shutdown();
			}
		});

	}

	private AbstractTask startPluginScanner() {
		AbstractTask l_task = new AbstractTask("Loading plugins ...")
		{
			public void initialize()  {   
				isHidden=true;
			}                
			public void doWork()  {        	
				mayday.core.pluma.PluginManager.getInstance(); // init is called automatically
			}
		};            
		l_task.start();
		return l_task;
	}

	public JMenu getMenu(String menuName) {
		for (Component c : this.getJMenuBar().getComponents())
			if (c instanceof JMenu) {
				String name = ((JMenu)c).getText();
				if (name!=null && name.equals(menuName))
					return ((JMenu)c);
			}
		return null;
	}


	public void windowActivated(WindowEvent e) {}

	public void windowClosed(WindowEvent e) {
	}
	
	public void setVisible(boolean vis) {
		super.setVisible(vis);
		int l_width = getWidth();
		int l_height = getHeight();
		Dimension l_screen = Toolkit.getDefaultToolkit().getScreenSize();
		int l_x = (int) ( ( l_screen.width - l_width ) * 0.15 );
		int l_y = (int) ( ( l_screen.height - l_height ) * 0.5 );
		setBounds( l_x, l_y, l_width, l_height );
	}

	public void windowClosing(WindowEvent e) {
		if (JOptionPane.showConfirmDialog(this, "Exit Mayday?", "Mayday: Confirm exit", 
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
			
			List<AbstractTask> tasks = new ArrayList<AbstractTask>(TaskManager.sharedInstance.getTasks());
			
			// warn if tasks are still active, e.g. saving a dataset
			if (tasks.size()!=0) {
				if (JOptionPane.showConfirmDialog(this, 
						"You still have running tasks. Are you _SURE_ you want to cancel them?\n" +
						"If Mayday is currently saving, YOU WILL LOSE YOUR DATA!", 
						"Cancel running tasks?", 
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE)!=JOptionPane.YES_OPTION)
					return;
				System.out.println("Mayday closing with "+tasks.size()+" tasks:");
				for (AbstractTask at : tasks)
					System.out.println("- "+at.getName());
			} 
			
			PluginManager.getInstance().shutdown();
			try {
				Preferences.userRoot().flush();
			} catch (BackingStoreException e1) {
				e1.printStackTrace();
			}
			setVisible(false);
			dispose();
			System.out.println("Thank you for using Mayday.");
			System.exit(0);
		}		
	}

	public void windowDeactivated(WindowEvent e) {}

	public void windowDeiconified(WindowEvent e) {}

	public void windowIconified(WindowEvent e) {}

	public void windowOpened(WindowEvent e) {}
}
