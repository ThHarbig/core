package mayday.core.io.gudi;

import java.awt.event.ActionEvent;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFileChooser;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.datasetmanager.gui.DataSetManagerView;
import mayday.core.io.gudi.prototypes.DatasetImportPlugin;
import mayday.core.io.gudi.prototypes.ProbelistFileImportPlugin;
import mayday.core.io.gudi.prototypes.ProbelistImportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.probelistmanager.ProbeListManager;
import mayday.core.probelistmanager.UnionProbeList;

public class GUDIProbeList extends GUDIBase {

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.GUDI.ProbeList",
				new String[0],
				Constants.MC_CORE,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Manages all ProbeList import plugins",
				"ProbeList Import"
				);		
		return pli;
	}

	
	@SuppressWarnings("serial")
	protected class RunPluginAction extends GUDIBase.RunPluginAction {
		
		ProbeListManager probeListManager;
		LinkedList<String> files;
		
		public RunPluginAction(PluginInfo pli) {
			super(pli);
		}
		public void actionPerformed(final ActionEvent arg0) {
			
			new Thread() {
				public void run() {
					List<ProbeList> results = null;
					
					DataSet ds = DataSetManagerView.getInstance().getSelectedDataSets().get(0);
					
					AbstractPlugin apl = getPlugin().getInstance();
					if (apl instanceof ProbelistFileImportPlugin) {
						JFileChooser fc = (JFileChooser)(arg0.getSource());
						files = new LinkedList<String>();
						if (fc.isMultiSelectionEnabled())
							for (File selfile : fc.getSelectedFiles())
								files.add(selfile.getAbsolutePath());
						else 
							files.add(fc.getSelectedFile().getAbsolutePath());
						ProbelistFileImportPlugin dsip = (ProbelistFileImportPlugin)apl;
						results = dsip.importFrom(files,ds);			
					} else if (apl instanceof DatasetImportPlugin){
						ProbelistImportPlugin dsip = (ProbelistImportPlugin)apl;
						results = dsip.run(ds);
					}
					if (results!=null) {						
						// close selected probelists
						probeListManager = ds.getProbeListManager();
						insertIntoProbeListManager(results);
					}								

				}
			}.start();
		}
		
		protected void insertIntoProbeListManager(ProbeList pl, UnionProbeList p) {
			ProbeListManager plm = pl.getDataSet().getProbeListManager();
			if (pl.getParent()==null)
				pl.setParent(p);
			plm.addObjectAtTop( pl );
		}
		
		protected void insertIntoProbeListManager(List<ProbeList> results) {
	    	if (results==null || results.size()==0)
	    		return;
	    	ProbeListManager plm = results.get(0).getDataSet().getProbeListManager();	    	
	    	UnionProbeList insertionParent = (UnionProbeList) null;
	    	if (results.size()>1) {
	    		UnionProbeList upl = new UnionProbeList(plm.getDataSet(), null);
	    		String namePrefix = getPlugin().getName();
    			String name = namePrefix;
    			int nameSuffix=0;
	    		if (plm.contains(name)) 
	    			name = namePrefix+" ("+(++nameSuffix)+")";	    		
	    		upl.setName(name);
	    		upl.setParent(insertionParent);
	    		DateFormat df = DateFormat.getDateTimeInstance();
	    		upl.getAnnotation().setQuickInfo("Created "+df.format(new Date())+", from "+files.size()+" input files (see annotation)");
	    		String infoString = "Input Files:";
	    		for (String fn : files)
	    			infoString+="\n- "+new File(fn).getName();
	    		upl.getAnnotation().setInfo(infoString);
	    		plm.addObjectAtTop(upl);
	    		insertionParent=upl;
	    	}
	    		
	    	for (ProbeList pl : results)
	    		insertIntoProbeListManager(pl, insertionParent);
	    }
	    
		
	}

	protected RunPluginAction createAction(PluginInfo pli) {
		return new RunPluginAction(pli);
	}


	protected String objectType() {
		return "ProbeList";
	}


	protected String providedMasterComponent() {
		return Constants.MC_PROBELIST_IMPORT;
	}	

	public void init() {
	}
	
	
}
