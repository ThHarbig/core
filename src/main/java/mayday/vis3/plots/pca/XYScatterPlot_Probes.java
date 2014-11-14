package mayday.vis3.plots.pca;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.math.JamaSubset.Matrix;
import mayday.core.plugins.probe.ProbeMenu;
import mayday.vis3.ColorProvider;
import mayday.vis3.ColorProviderSetting;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.vis2base.DataSeries;
import wsi.ra.chart2d.GraphicsModifier;

@SuppressWarnings("serial")
public class XYScatterPlot_Probes extends XYScatterPlot<Probe> {

	private ColorProvider coloring;
	private ProbeColoring probeColorSetter;

	public XYScatterPlot_Probes(Matrix pcaData, int dim1, int dim2, List<Probe> probesOnDisplay) {
		super(pcaData, dim1, dim2, probesOnDisplay);

		probeColorSetter = new ProbeColoring();
		
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				double[] clicked = getPoint(e.getX(), e.getY());
				switch (e.getButton()) {
				case MouseEvent.BUTTON1:
					Probe pb = (Probe)szb.getObject(clicked[0], clicked[1]);
					if (pb!=null) {
						if (e.getClickCount()==2) {
							PropertiesDialogFactory.createDialog(pb).setVisible(true);
						} else {
							int CONTROLMASK = getToolkit().getMenuShortcutKeyMask();
							if ((e.getModifiers()&CONTROLMASK) == CONTROLMASK) {
								// 			toggle selection of the clicked probe
								viewModel.toggleProbeSelected(pb);
							} else {
								// 			select only one probe
								viewModel.setProbeSelection(pb);
							}
						}
					}
					break;
				case MouseEvent.BUTTON3:
					ProbeMenu pm = new ProbeMenu(viewModel.getSelectedProbes(), viewModel.getDataSet().getMasterTable());
					pm.getPopupMenu().show(XYScatterPlot_Probes.this, e.getX(), e.getY());
					break;
				}
			}		
		});
		
		
	}

	protected void updateSelection(Set<Probe> newSelection, boolean control, boolean alt) {
		
		Set<Probe> previousSelection = viewModel.getSelectedProbes();
		if (control && alt) {
			previousSelection = new HashSet<Probe>(previousSelection);
			previousSelection.removeAll(newSelection);
			newSelection = previousSelection;
		} else if (control) {
			newSelection.addAll(previousSelection);
		} else if (alt) {
			newSelection.retainAll(previousSelection);
		} else {
			// nothing to do with prev selection
		}

		viewModel.setProbeSelection(newSelection);
	}
	
	public DataSeries getPlotComponent(int j) {
		DataSeries ds = super.getPlotComponent(j);
		ds.setAfterJumpModifier(probeColorSetter);
		return ds;
	}
		

	public void select(Color selection_color)
	{
		if (selectionLayer!=null)
			removeDataSeries(selectionLayer);
		
		Set<Probe> s = new HashSet<Probe>();
				
		for(ProbeList probe_list : viewModel.getProbeLists(true))
			s.addAll(probe_list.getAllProbes());
		
		s.retainAll(viewModel.getSelectedProbes());
		
		selectionLayer = doSelect1(s);
		if (selectionLayer!=null) {
			selectionLayer.setColor(Color.RED);
			addDataSeries(selectionLayer);
		}
		clearBuffer();
		repaint();
	}
	
	public void setup0(PlotContainer plotContainer) {

		if (coloring==null) {
			coloring = new ColorProvider(viewModel);		
			coloring.setMode(ColorProviderSetting.COLOR_BY_TOP_PRIORITY_PROBELIST);
			coloring.addChangeListener(new ChangeListener(){
				
				public void stateChanged(ChangeEvent e) {
					clearBuffer();  // remove antialiased image
					repaint(); // redraw plot with new coloring 
				}
				
			});
			plotContainer.addViewSetting(coloring.getSetting(), this);
		}
	}
	
	private class ProbeColoring implements GraphicsModifier {
		public void modify(Graphics2D g, Object o) {
			if (o instanceof Probe)
				g.setColor(coloring.getColor((Probe)o));
		}		
	}

	
}
