package mayday.vis3.gui.actions;

import java.awt.event.ActionEvent;
import java.util.LinkedList;

import javax.swing.AbstractAction;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.vis3.model.ViewModel;

@SuppressWarnings("serial")
public class ProbeSelectionCreateVisualizerAction extends AbstractAction {

	private ViewModel viewModel;
	
	public ProbeSelectionCreateVisualizerAction(ViewModel viewModel) {
		super("Plot in separate visualizer");
		this.viewModel = viewModel;
	}
	
	public void actionPerformed(ActionEvent e) {
		ProbeList tmpList = new ProbeList(viewModel.getDataSet(), true);
		for (Probe pb : viewModel.getSelectedProbes())
			tmpList.addProbe(pb);
		tmpList.setName("Selected Probes from Visualizer "+viewModel.getVisualizer().getID());

		PluginInfo pli = PluginManager.getInstance().getPluginFromID("PAS.incubator.ProfilePlot");
		if (pli!=null) {
			LinkedList<ProbeList> lpl   = new LinkedList<ProbeList>();
			lpl.add(tmpList);
			((ProbelistPlugin)pli.newInstance()).run(lpl,viewModel.getDataSet().getMasterTable());
		}
		
	}

}
