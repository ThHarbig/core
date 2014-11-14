package mayday.core.pluginrunner;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.prototypes.GenericPlugin;

public class GenericPluginRunner {

	protected PluginInfo pli;

	public GenericPluginRunner(PluginInfo pli) {
		this.pli = pli;
	}

	public void execute() {
		prepare();

		Thread RunPluginThread = new Thread("PluginRunner")	{
			public void run() {	    			
				try {
					runPlugin();
				} catch ( final Exception exception ) {
					exception.printStackTrace();	  
					SwingUtilities.invokeLater(new Runnable(){
						public void run() {
							JOptionPane.showMessageDialog( null,
									exception.getMessage(),
									MaydayDefaults.Messages.ERROR_TITLE,
									JOptionPane.ERROR_MESSAGE );

						}
					});
				}
			}
		};

		RunPluginThread.start();
	}

	protected void prepare() {	    	
//		System.runFinalization();
//		System.gc();
	}

	protected void runPlugin() {    	
        GenericPlugin dpl = (GenericPlugin)(pli.getInstance());	    	    	
        dpl.run( );

	}


}