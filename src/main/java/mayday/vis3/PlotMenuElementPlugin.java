package mayday.vis3;

import java.awt.Component;

import mayday.vis3.model.ViewModel;

public interface PlotMenuElementPlugin {

	public final static String MC = "Additional plot export methods";
	
	public void run(ViewModel viewModel, Component plotComponent);
	
}
