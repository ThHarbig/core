package mayday.vis3.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.vis3.components.PlotScrollPane;
import mayday.vis3.components.PlotWithLegendAndTitle;
import mayday.vis3.gui.PlotWindow;
import mayday.vis3.gui.probemover.MultiProbeMoverComponent;
import mayday.vis3.model.ViewModel;

@SuppressWarnings("serial")
public class ProbeSelectionOpenProbeMover extends AbstractAction {

	private ViewModel viewModel;
	
	public ProbeSelectionOpenProbeMover(ViewModel viewModel) {
		super("Open selection mover...");
		this.viewModel = viewModel;
	}
	
	public void actionPerformed(ActionEvent e) {
		PlotWithLegendAndTitle myComponent;
		MultiProbeMoverComponent mpp = new MultiProbeMoverComponent();
		myComponent = new PlotWithLegendAndTitle(new PlotScrollPane(mpp));
		myComponent.setTitledComponent(null);
		
		PlotWindow newPlot = new PlotWindow(mpp, viewModel.getVisualizer());
		viewModel.getVisualizer().addPlot(newPlot);
		newPlot.setVisible(true);
	}

}
