package mayday.core.plugins.dataset;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.meta.gui.MIGroupSelectionDialog;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.DatasetPlugin;

public class SetExperimentDisplayNames extends AbstractPlugin implements DatasetPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.DatasetSetExperimentDisplayNames",
				new String[0],
				Constants.MC_DATASET,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Set the Experiment display names from a MIO group.",
				"Set Display Names"
				);
		pli.addCategory("Experiment Names");
		pli.setMenuName("Set Experiment Display Names...");
		return pli;
	}

	public List<DataSet> run(List<DataSet> datasets) {
		DataSet l_selectedValue = datasets.get(0);
		
        MIGroupSelectionDialog l_mioGroupSelectionDialog 
          = new MIGroupSelectionDialog( l_selectedValue.getMIManager(),"PAS.MIO.String");
        l_mioGroupSelectionDialog.setVisible( true );
        
        MIGroupSelection<MIType> l_mioSelection = l_mioGroupSelectionDialog.getSelection();
        
        if ( l_mioSelection != null ) {
        	//ArrayList< MIOGroup > l_mioGroups = l_mioSelection.getMIOGroups();
        	if (l_mioSelection.size() >0) {
        		l_selectedValue.setExperimentDisplayNames( l_mioSelection.get( 0 ) );
        	}
        }
		
		return new LinkedList<DataSet>();
    }


}
