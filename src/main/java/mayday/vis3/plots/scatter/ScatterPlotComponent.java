package mayday.vis3.plots.scatter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.plugins.probe.ProbeMenu;
import mayday.vis3.SparseZBuffer;
import mayday.vis3.ValueProvider;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;
import mayday.vis3.vis2base.DataSeries;
import mayday.vis3.vis2base.Shape;

@SuppressWarnings("serial")
public class ScatterPlotComponent extends AbstractProbeListScatterPlotComponent {

	protected ValueProvider X;
	protected ValueProvider Y;
	protected SparseZBuffer szb;

	protected Rectangle selRect;


	public ScatterPlotComponent() {
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
					pm.getPopupMenu().show(ScatterPlotComponent.this, e.getX(), e.getY());
					break;
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (selRect!=null) {
					System.out.println("Selecting profiles intersecting: "+selRect);
					Graphics2D g = ((Graphics2D)farea.getGraphics());
					drawSelectionRectangle(g);
					int CONTROLMASK = getToolkit().getMenuShortcutKeyMask();
					boolean control = ((e.getModifiers()&CONTROLMASK) == CONTROLMASK);
					boolean alt = e.isAltDown();
					selectByRectangle(selRect, control, alt);
					selRect = null;
				}
			}
		});

		farea.addMouseMotionListener(new MouseMotionListener() {

			protected Point dragPoint;
			protected Point targPoint;

			public void mouseDragged(MouseEvent e) {					
				Graphics2D g = ((Graphics2D)farea.getGraphics());
				if (selRect==null) {
					dragPoint = e.getPoint();
				} else {
					drawSelectionRectangle(g);
				}
				targPoint = e.getPoint();
				selRect = new Rectangle(dragPoint, new Dimension(1,1));
				selRect.add(targPoint);					
				drawSelectionRectangle(g);					
			}

			public void mouseMoved(MouseEvent e) {
			}
		});
	}

	protected void drawSelectionRectangle(Graphics2D g) {
		if (selRect==null)
			return;
		g.setXORMode(getBackground());
		g.setColor(Color.RED);
		Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(2));
		g.draw(selRect);
		g.setStroke(oldStroke);
		g.setPaintMode();
	}

	protected void selectByRectangle(Rectangle r, boolean control, boolean alt) {
		Set<Probe> newSelection = new HashSet<Probe>();
		double[] clicked1 = getPoint(r.x, r.y);
		double[] clicked2 = getPoint(r.x+r.width, r.y+r.height);
		for (Probe pb : viewModel.getProbes()) {
			double xval = X.getValue(pb);
			double yval = Y.getValue(pb);
			boolean inX = (xval>=clicked1[0] && xval<clicked2[0]);
			boolean inY = (yval<clicked1[1] && yval>clicked2[1]);
			if (inX && inY)
				newSelection.add(pb);
		}
		/*
		 * selection modes: 
		 * - no modifier: replace
		 * - ctrl: union with previous selection
		 * - alt: intersect with previous selection
		 * - ctrl+alt: remove from previous selection
		 */

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

	@Override
	public DataSeries viewProbes(Collection<Probe> probes) {
		DataSeries ds = new DataSeries();
		if (X!=null && Y!=null) {
			for (Probe pb : probes) {
				double xx = X.getValue(pb);
				double yy = Y.getValue(pb);
				ds.addPoint(xx, yy, pb);
				szb.setObject(xx, yy, pb);
			}
		}
		return ds;
	}

	@Override
	public DataSeries doSelect(Collection<Probe> probes) {
		DataSeries ds = viewProbes(probes);
		ds.setShape(new Shape() {
			@Override
			public void paint(Graphics2D g) {
				g.fillOval(-3,-3,6,6);
			}
			public boolean wantDeviceCoordinates() {
				return true;
			}
		});
		return ds;
	}

	@Override
	public int getNumberOfComponents() {
		return viewModel.getProbeLists(true).size();
	}

	@Override
	public DataSeries getPlotComponent(int i) {
		int index = getNumberOfComponents()-i-1;
		ProbeList pl = viewModel.getProbeLists(true).get(index);
		DataSeries res = viewProbes(pl.getAllProbes());
		res.setAfterJumpModifier(probeColorSetter);
		return res;
	}

	@Override
	public String getPreferredTitle() {
		return "Scatter Plot";
	}

	public void setup(PlotContainer plotContainer) {
		if (firstTime) {
			szb = new SparseZBuffer();
			setXLabeling(null);
			setYLabeling(null);
		}
		super.setup(plotContainer);
		initValueProviders(viewModel, plotContainer);
	}

	protected void initValueProviders(ViewModel vm, PlotContainer plotContainer) {

		if (X==null || Y==null) {
			ChangeListener cl = new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					updatePlot();
				}
			};	

			X = new ValueProvider(viewModel,"X axis");
			Y = new ValueProvider(viewModel,"Y axis");
			X.addChangeListener(cl);
			Y.addChangeListener(cl);
			if (vm.getDataSet().getMasterTable().getNumberOfExperiments()>1)
				Y.setProvider(Y.new ExperimentProvider(1));
		}
		plotContainer.addViewSetting(X.getSetting(), this);
		plotContainer.addViewSetting(Y.getSetting(), this);
	}

	@Override
	public String getAutoTitleY(String ytitle) {
		if (Y!=null)
			return  Y.getSourceName();
		return ytitle;
	}

	@Override
	public String getAutoTitleX(String xtitle) {
		if (X!=null)
			return  X.getSourceName();
		return xtitle;
	}

}
