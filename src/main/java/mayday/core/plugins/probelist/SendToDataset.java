package mayday.core.plugins.probelist;

import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import mayday.core.DataSet;
import mayday.core.Mayday;
import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.dataset.DataSetSelectionDialog;
import mayday.core.io.gude.GUDEConstants;
import mayday.core.io.gude.prototypes.ProbelistExportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.probelistmanager.UnionProbeList;

public class SendToDataset extends AbstractPlugin implements ProbelistExportPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.SendToDataset",
				new String[0],
				Constants.MC_PROBELIST_EXPORT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Exports a probe list to another dataset.",
				"Send to DataSet"
		);
		pli.setMenuName("Send to DataSet...");
		pli.getProperties().put(GUDEConstants.EXPORTER_TYPE, GUDEConstants.EXPORTERTYPE_OTHER);
		return pli;
	}


	protected String problems;
	protected int problemCount;
	
	public void run(List<ProbeList> probelists) {
//		DataSetManager l_dataSetManager = DataSetManager.singleInstance;
//
//		LinkedList<DataSet> targetCandidates = new LinkedList<DataSet>(l_dataSetManager.getDataSets());
//		// only allow sending somewhere else        
//		targetCandidates.remove(probelists.get(0).getDataSet());
//
//		if (targetCandidates.size()==0) {
//			JOptionPane.showMessageDialog(
//					null,
//					"No dataset available as target",
//					"Target Selection",
//					JOptionPane.ERROR_MESSAGE,
//					null );
//			return;
//		}


//		DataSet l_targetDataSet = (DataSet)JOptionPane.showInputDialog(
//				null,
//				"Select target data set.",
//				"Target Selection",
//				JOptionPane.QUESTION_MESSAGE,
//				null,
//				targetCandidates.toArray(),
//				null );
//
//		if ( l_targetDataSet == null ) { // user cancelled
//			return;
//		}

		DataSetSelectionDialog dssd = new DataSetSelectionDialog();
		dssd.setDialogDescription("Please select the target DataSet(s)");
		dssd.setModal(true);
		dssd.setVisible(true);

		for (DataSet target : dssd.getSelection()) {
			if (target == probelists.get(0).getDataSet())
				continue;
			HashMap<ProbeList,ProbeList> source2target = new HashMap<ProbeList, ProbeList>();

			problems="";
			problemCount=0;
			
			for (ProbeList p : probelists) { 
				transferProbeList(p, target, source2target);         
			}

			if (problemCount>0) {
				JOptionPane.showMessageDialog( Mayday.sharedInstance,
						"There were problems transferring "+problemCount+" of the "+probelists.size()+" probelists\nto DataSet \""+target.getName()+"\":\n"
						+problems,
						MaydayDefaults.Messages.INFORMATION_TITLE,
						JOptionPane.INFORMATION_MESSAGE );
			}
		}
		
		
	}

	protected void transferProbeList(ProbeList p, DataSet target, HashMap<ProbeList, ProbeList> source2target) {
		
		ProbeList newPL;
		
		if (source2target.get(p)!=null)
			return; //already done
		
		if (p instanceof UnionProbeList) {
			newPL = new UnionProbeList(target, null);    
		} else {
			newPL = new ProbeList( target, true );
			for ( Probe pb : p.getAllProbes() ) {
				String l_probeName = pb.getName();
				// try to find probe in target data set set
				Probe l_targetProbe = target.getMasterTable().getProbe( l_probeName );

				if ( l_targetProbe != null )
					newPL.addProbe( l_targetProbe );
			}

			int missing= p.getNumberOfProbes()-newPL.getNumberOfProbes();
			if (missing>0) {
				problems += p.getName() + ": " + missing +" probes could not be found.\n";
				++problemCount;
			}
		}

		newPL.setName( p.getName() );
		newPL.setColor( p.getColor() );
		newPL.getAnnotation().setQuickInfo( p.getAnnotation().getQuickInfo() );
		newPL.getAnnotation().setInfo( "Received from data set \"" +p.getDataSet().getName() + "\"" );
		source2target.put(p,newPL);

		UnionProbeList ppar = p.getParent();
		if (ppar!=null && source2target.containsKey(ppar)) {
			UnionProbeList npar = (UnionProbeList)source2target.get(ppar);
			if (npar==null)
				transferProbeList(ppar, target, source2target);
			newPL.setParent((UnionProbeList)source2target.get(ppar));
		}

		target.getProbeListManager().addObjectAtBottom(newPL);
		
		if (newPL instanceof UnionProbeList) {
			// copy all children
			for (ProbeList pl : p.getDataSet().getProbeListManager().getProbeListsBelow(p))
				transferProbeList(pl, target, source2target);
		}
	}
	
}
