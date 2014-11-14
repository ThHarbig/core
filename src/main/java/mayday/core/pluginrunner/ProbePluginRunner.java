package mayday.core.pluginrunner;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import mayday.core.DataSet;
import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.prototypes.ProbePlugin;

public class ProbePluginRunner {

	protected List<Probe> probes;
	protected DataSet ds;
	protected PluginInfo pli;

	public ProbePluginRunner(PluginInfo pli, List<Probe> probes, DataSet ds) {
		this.probes=probes;
		this.ds = ds;
		this.pli = pli;
	}

	public ProbePluginRunner(PluginInfo pli) {
		this.pli=pli;			
	}

	public void execute() {
		prepare();
		if (ds==null || probes==null)
			return;

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
		ProbePlugin ppl = (ProbePlugin)(pli.getInstance());
		ppl.run( probes, ds.getMasterTable() );
		
	}


}