package mayday.dynamicpl.dataprocessor;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.AbstractDataProcessor;
import mayday.dynamicpl.gui.OptionPanelProvider;
import mayday.vis3.gui.VisualizerSelectionDialog;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.model.Visualizer;
import mayday.vis3.model.VisualizerMember;

public class ContainedInSelection extends AbstractDataProcessor<Probe, Boolean> 
implements OptionPanelProvider, ViewModelListener, VisualizerMember
{
	
	private Visualizer visualizer;
	private JTextField selectedVis = new JTextField(30);

	@Override
	public Class<?>[] getDataClass() {
		if (visualizer==null)
			return null;	
		return new Class[]{Boolean.class};
	}
	
	@Override
	public Boolean convert(Probe pb) {
		return visualizer.getViewModel().getSelectedProbes().contains(pb);
	}

	@Override
	public String toString() {
		return (visualizer==null?"":"Probe contained in Visualizer selection &lt;"+visualizer.getID()+"&gt");
	}

	@SuppressWarnings("serial")
	public void composeOptionPanel(JPanel optionPanel) {		
		selectedVis.setEditable(false);
		JButton selectVis = new JButton(new AbstractAction("Select") {
			public void actionPerformed(ActionEvent e) {
				VisualizerSelectionDialog vsd = new VisualizerSelectionDialog(
						getDynamicProbeList().getDataSet()
						);
				vsd.setModal(true);
				vsd.setVisible(true);
				List<Visualizer> vss = vsd.getSelection();
				if (visualizer!=null)
					visualizer.getViewModel().removeViewModelListener(ContainedInSelection.this);
				if (vss.size()>0) {
					visualizer = vss.get(0);
					selectedVis.setText(visualizer.getName());
					visualizer.getViewModel().addViewModelListener(ContainedInSelection.this);
				} else {
					visualizer = null;
					selectedVis.setText("");
				}
				fireChanged();
			}
		});
		optionPanel.add(selectedVis);
		optionPanel.add(selectVis);
	}
		
	
	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.source.ContainedInVisualizerSelection",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Returns true if the probe is contained in the set of selected probes in a visualizer.",
				"Contained in Visualizer selection"
		);
		return pli;
	}

	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return Probe.class.isAssignableFrom(inputClass[0]);
	}

	public JPanel getOptionPanel() {
		JPanel p = new JPanel();
		composeOptionPanel(p);
		return p;
	}

	public void viewModelChanged(ViewModelEvent vme) {
		if (vme.getChange()==ViewModelEvent.PROBE_SELECTION_CHANGED)
			fireChanged();		
	}

	public void closePlot() {
		visualizer=null;
		fireChanged();
	}

	public String getPreferredTitle() {
		return "Dynamic ProbeList \""+getDynamicProbeList()+"\"is listening...";
	}

	public JMenu getVisualizerMenu() {
		return new JMenu();
	}

	public void requestFocus() {
	}

	public void setTitle(String title) {
	}

	public void toFront() {
	}

	public String getTitle() {
		return getPreferredTitle();
	}
	
	public void dispose() {
		if (visualizer!=null)
			visualizer.getViewModel().removeViewModelListener(this);
	}

}

