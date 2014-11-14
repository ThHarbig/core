package mayday.core.plugins.dataset;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import mayday.core.DataSet;
import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.probelist.ProbeListSelectionDialog;
import mayday.core.gui.probelist.ProbeListSelectionFilter;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.DatasetPlugin;

public class FindProbeLists extends AbstractPlugin implements DatasetPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.FindProbeLists",
				new String[0],
				Constants.MC_DATASET,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Lists all ProbeLists containing a given probe",
				"Find ProbeLists containing a Probe..."
				);
		return pli;
	}

	public List<DataSet> run(List<DataSet> datasets) {
		
		DataSet ds = datasets.get(0);
		
		String l_probeName = new String();

		do {
			l_probeName = (String)JOptionPane.showInputDialog( null,
					"Enter a probe identifier or display name.",
					MaydayDefaults.Messages.INFORMATION_TITLE,
					JOptionPane.INFORMATION_MESSAGE,
					null,
					null,
					"" );
			if ( l_probeName == null )         
				return null;

			if ( l_probeName.trim().equals( "" ) ) {
				JOptionPane.showMessageDialog( null,
						"Empty probe names do not exist.",                                           
						MaydayDefaults.Messages.ERROR_TITLE,
						JOptionPane.ERROR_MESSAGE ); 
			}
		}
		while ( l_probeName.trim().equals( "" ) );

		LinkedList <Probe> probesToFind = new LinkedList<Probe>();
		
		Probe pb = ds.getMasterTable().getProbe(l_probeName);
		
		if (pb==null) {
			for (Probe npb : ds.getMasterTable().getProbes().values())
				if (npb.getDisplayName().equals(l_probeName)) {
					probesToFind.add(npb);					
				}					
		} else {
			probesToFind.add(pb);
		}
		
		final LinkedList<ProbeList> res = new LinkedList<ProbeList>();
		
		for (ProbeList pl : ds.getProbeListManager().getProbeLists()) {
			for (Probe ptf : probesToFind) {
				if (pl.contains(ptf)) {
					res.add(pl);
					break;
				}
			}
		}
		
		ProbeListSelectionDialog plsd = new ProbeListSelectionDialog(ds.getProbeListManager());
		plsd.setFilter(new ProbeListSelectionFilter() {

			public boolean pass(ProbeList pl) {
				return res.contains(pl);
			}
					
		});
		
		plsd.setTitle("ProbeLists containing a probe named "+l_probeName);
		plsd.setDialogDescription("");
	
		
		plsd.setVisible(true);
		
		return null;
		
    }


}
