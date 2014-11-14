package mayday.vis3.plots.pca;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JMenu;

import mayday.core.Experiment;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.math.JamaSubset.Matrix;
import mayday.vis3.components.MultiPlotPanel;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;


@SuppressWarnings("serial")
public class PCAPlotComponent extends MultiPlotPanel {

	public Matrix PCAData;
	public double[] EigenValues;
	public ViewModel viewModel;
	protected ExportPCAAction exportPCA = new ExportPCAAction();

	protected boolean transpose_first;
	protected int number_of_components;
	
	public PCAPlotComponent( boolean transpose, int numberOfComponents ) {
		transpose_first = transpose;
		number_of_components = numberOfComponents;
	}
	
	@SuppressWarnings("deprecation") // accessing the file menu is allowed
	public void setup(PlotContainer plotContainer) {
		super.setup(plotContainer);
		viewModel = plotContainer.getViewModel();
		plotContainer.setPreferredTitle("PCA Plot"+(transpose_first?" (transposed)":""), null);
		// free the swing thread (we're in it right now!)
		setPlots(new Component[]{new JLabel("PCA is being computed, please wait")});
		zoomController.setActive(true);
		JMenu m = plotContainer.getMenu(PlotContainer.FILE_MENU, this);
		m.add(exportPCA);
		new PCAComputer(this);
	}
	
	public void updateWithPCAResult(PCAComputer pcc) {
		if (PCAData == null) { // computation failed
			setPlots(new Component[]{new JLabel("PCA computation failed.")});
		} else {
			
			number_of_components = Math.min(number_of_components, EigenValues.length);
			
			ArrayList<Component> components = new ArrayList<Component>();
			
			if (transpose_first) {
				ArrayList<Experiment> allexperiments = new ArrayList<Experiment>();
				allexperiments.addAll(viewModel.getDataSet().getMasterTable().getExperiments());
				exportPCA.setData(PCAData,allexperiments);

				for (int i=0; i!=number_of_components-1; ++i) {
					for (int j=i+1; j!=number_of_components; ++j) {
						components.add(new XYScatterPlot_Experiments(PCAData,i,j,allexperiments));						
					}
				}

			} else {
				ArrayList<Probe> allprobes = new ArrayList<Probe>();
				List<ProbeList> pls = viewModel.getProbeLists(true);
				for(int i = pls.size(); i!=0; --i) {
					allprobes.addAll(pls.get(i-1).toCollection());
				}
				exportPCA.setData(PCAData,allprobes);

				for (int i=0; i!=number_of_components-1; ++i) {
					for (int j=i+1; j!=number_of_components; ++j) {
						components.add(new XYScatterPlot_Probes(PCAData,i,j,allprobes));						
					}
				}
			}
			
			double[] percentEigenvalues = new double[EigenValues.length];
			double sum = sum(EigenValues);
			
			for(int i = 0; i < EigenValues.length; i++)
				percentEigenvalues[i] = (EigenValues[i]/sum) * 100.;

			components.add(new PCBarPlot(percentEigenvalues));
			
			setPlots(components.toArray(new Component[components.size()]));
		}		
	}
	
	private double sum(double[] values) {
		double sum = 0;
		for(double d : values)
			sum += d;
		return sum;
	}
}
