package mayday.core.pluginrunner;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import mayday.core.DataSet;
import mayday.core.MaydayDefaults;
import mayday.core.datasetmanager.gui.DataSetManagerView;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.prototypes.DatasetPlugin;

public class DataSetPluginRunner {

	protected List<DataSet> dataSets;
	protected PluginInfo pli;

	public DataSetPluginRunner(PluginInfo pli, List<DataSet> dataSets) {
		this.dataSets=dataSets;
		this.pli = pli;
	}

	public DataSetPluginRunner(PluginInfo pli) {
		this.pli=pli;			
	}

	public void execute() {
		prepare();
		if (dataSets==null) 
			inferInput();
		if (dataSets==null)
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

	public static void insertIntoDataSetManager(List<DataSet> results) {
    	if (results==null)
    		return;
    	for (DataSet ds : results) {
    		DataSetManagerView.getInstance().addDataSet(ds);
    		// add global probe list if no other pl exists	    		
    		if (!ds.getProbeListManager().contains("global"))
    			ds.getProbeListManager().addObjectAtTop(ds.getMasterTable().createGlobalProbeList(true));	    		
    	}	    	
    	DataSetManagerView.getInstance().setSelectedDataSets(results);	    	
    }

	protected void prepare() {	    	
//		System.runFinalization();
//		System.gc();
	}

	protected void inferInput() {
		dataSets = DataSetManagerView.getInstance().getSelectedDataSets();
	}

	protected void runPlugin() {    	
        DatasetPlugin dpl = (DatasetPlugin)(pli.getInstance());	    	    	
        List<DataSet> results = dpl.run( dataSets );
        insertIntoDataSetManager(results); 
	}


}