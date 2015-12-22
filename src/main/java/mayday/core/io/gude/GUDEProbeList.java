package mayday.core.io.gude;

import java.util.ArrayList;
import java.util.HashMap;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.datasetmanager.gui.DataSetManagerView;
import mayday.core.io.gude.prototypes.ProbelistExportPlugin;
import mayday.core.io.gude.prototypes.ProbelistFileExportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.probelistmanager.ProbeListManager;

public class GUDEProbeList extends GUDEBase {

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.GUDE.ProbeList",
				new String[0],
				Constants.MC_CORE,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Manages all ProbeList export plugins",
				"ProbeList Export"
				);		
		return pli;
	}

	@SuppressWarnings("serial")
	protected class RunPluginAction extends GUDEBase.RunPluginAction {

		public RunPluginAction(PluginInfo pli) {
			super(pli);
		}
		public void actionPerformed(final GUDEEvent arg0) {

			new Thread() {
				public void run() {
					
		           	DataSet ds = DataSetManagerView.getInstance().getSelectedDataSets().get(0);
		        	ProbeListManager probeListManager = ds.getProbeListManager();
		        	
		        	ArrayList<ProbeList> probeLists = new ArrayList<ProbeList>();
		            for (Object p:probeListManager.getProbeListManagerView().getSelectedValues())
		            {
		                probeLists.add( (ProbeList)p );
		            }

					AbstractPlugin apl = getPlugin().getInstance();
					
					if (apl instanceof ProbelistFileExportPlugin) {
						//JFileChooser fc = (JFileChooser)(arg0.getSource());
						ProbelistFileExportPlugin dsep = (ProbelistFileExportPlugin)apl;
						dsep.exportTo(probeLists, arg0.getFileName());			
					} else if (apl instanceof ProbelistExportPlugin){
						ProbelistExportPlugin dsep = (ProbelistExportPlugin)apl;
						dsep.run(probeLists);
					}
				}
			}.start();
		}
		
	}

	protected RunPluginAction createAction(PluginInfo pli) {
		return new RunPluginAction(pli);
	}
	

	@Override
	protected String objectType() {
		return "ProbeList";
	}

	@Override
	protected String providedMasterComponent() {
		return Constants.MC_PROBELIST_EXPORT;
	}

	@Override
	public void init() {	
	}

	
}
