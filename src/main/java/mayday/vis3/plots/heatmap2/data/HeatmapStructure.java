package mayday.vis3.plots.heatmap2.data;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.DelayedUpdateTask;
import mayday.core.Probe;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.SortedExtendableConfigurableObjectListSetting;
import mayday.vis3.HierarchicalSortedProbeList;
import mayday.vis3.gui.PlotComponent;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.plots.heatmap2.columns.HeatmapColumn;
import mayday.vis3.plots.heatmap2.columns.plugins.AbstractColumnGroupPlugin;
import mayday.vis3.plots.heatmap2.columns.plugins.expression.MultiExpressionPlugin;
import mayday.vis3.plots.heatmap2.component.ColumnHeaderStack;
import mayday.vis3.plots.heatmap2.component.HeatmapCentralComponent;
import mayday.vis3.plots.heatmap2.component.HeatmapOuterComponent;
import mayday.vis3.plots.heatmap2.component.RowHeaderStack;
import mayday.vis3.plots.heatmap2.headers.ColumnHeaderElement;
import mayday.vis3.plots.heatmap2.headers.RowHeaderElement;
import mayday.vis3.plots.heatmap2.headers.row.ClusterTreeHeader;
import mayday.vis3.plots.heatmap2.headers.row.ProbeNameHeader;
import mayday.vis3.plots.heatmap2.headers.row.SelectionIndicationHeader;
import mayday.vis3.plots.heatmap2.headers.row.TopLevelProbeListHeader;
import mayday.vis3.plots.heatmap2.interaction.UpdateEvent;
import mayday.vis3.plots.heatmap2.interaction.UpdateListener;

public class HeatmapStructure implements ViewModelListener, UpdateListener {
	
	protected LinkedList<RowHeaderElement> rowHeaders = new LinkedList<RowHeaderElement>();	
	protected LinkedList<HeatmapColumn> columns = new LinkedList<HeatmapColumn>();
	protected LinkedList<AbstractColumnGroupPlugin> columnGroups = new LinkedList<AbstractColumnGroupPlugin>();
	
	protected ScalingInfo rowScaling = new ScalingInfo(0,8); 
	protected ScalingInfo colScaling = new ScalingInfo(0,8);
	protected DelayedUpdateTask resizeUpdater = new DelayedUpdateTask("Heatmap resizing", 50) {
		protected boolean needsUpdating() {
			return true;
		}
		protected void performUpdate() {
			repaint();			
		}
	};
	
	protected ColumnHeaderStack compColHeader;
	protected RowHeaderStack compRowHeader;
	protected HeatmapCentralComponent compCentral;
	protected HeatmapOuterComponent compOuter;
	
	protected HierarchicalSortedProbeList probeOrder;	
	protected RowHeightEnhancementSetting rowHeights;
	
	protected ViewModel vm;
	
	protected SortedExtendableConfigurableObjectListSetting<AbstractColumnGroupPlugin> columnTypes;
	protected SortedExtendableConfigurableObjectListSetting<RowHeaderElement> rowHeaderTypes;
	
	public HeatmapStructure(HeatmapOuterComponent hmoc) {
		compOuter = hmoc;
	}
	

	public List<RowHeaderElement> getRowHeaderElements() {
		return Collections.unmodifiableList(rowHeaders);
	}

	public List<HeatmapColumn> getColumns() {
		return Collections.unmodifiableList(columns);
	}
	

	public List<AbstractColumnGroupPlugin> getColumnGroups() {
		return Collections.unmodifiableList(columnGroups);
	}
	
	public HeatmapColumn getColumn(int index) {
		return columns.get(index);
	}
	
	
	public Probe getProbe(int row) {
		return probeOrder.get(row);
	}
	
	public boolean isSelected(int row) {
		return vm.isSelected(getProbe(row));
	}
	
	public double getRowHeight(int row) {
		return rowScaling.getSize(row);
	}

	public double getColWidth(int col) {
		return colScaling.getSize(col);
	}
	
	public double getRowStart(int row) {
		return rowScaling.getStart(row);
	}

	public double getColStart(int col) {
		return colScaling.getStart(col);
	}
	
	public long getColumnScalingModificationCount() {
		return colScaling.getModificationCount();
	}
	
	public long getRowScalingModificationCount() {
		return rowScaling.getModificationCount();
	}

	public Component getColumnHeaderComponent() {
		if (compColHeader==null)
			compColHeader = new ColumnHeaderStack(this,2);
		return compColHeader;
	}
	
	public Component getRowHeaderComponent() {
		if (compRowHeader==null)
			compRowHeader = new RowHeaderStack(this,2);
		return compRowHeader;
	}
	
	public Component getHeatmapComponent() {
		if (compCentral==null)
			compCentral = new HeatmapCentralComponent(this);
		return compCentral;
	}
	
	public int[] getRowsInView(Graphics2D g) {
		Rectangle2D clipRect = g.getClipBounds();
		int[] res = new int[2];
		res[0] = Math.max(0, rowScaling.indexAtPosition((int)clipRect.getY())-1);
		res[1] = Math.min(nrow()-1, rowScaling.indexAtPosition((int)(clipRect.getY()+clipRect.getHeight())));
		return res;
	}
	
	public int[] getColumnsInView(Graphics2D g) {
		Rectangle2D clipRect = g.getClipBounds();
		int[] res = new int[2];
		res[0] = Math.max(0, colScaling.indexAtPosition((int)clipRect.getX())-1);
		res[1] = Math.min(ncol()-1, colScaling.indexAtPosition((int)(clipRect.getX()+clipRect.getWidth())));
		return res;
	}
	
	public int getRowAtPosition(int position) {
		return rowScaling.indexAtPosition(position); 
	}
	
	public int getColumnAtPosition(int position) {
		return colScaling.indexAtPosition(position); 
	}
	
	public void getCellRectangle(int row, int col, Rectangle2D dest) {
		dest.setFrame(
				colScaling.getStart(col), 
				rowScaling.getStart(row), 
				colScaling.getSize(col), 
				rowScaling.getSize(row)
		);
	}
	
	public double getTotalColumnWidth() {
		if (ncol()==0)
			return 0;
		return colScaling.getEnd(ncol()-1);
	}
	
	public double getTotalRowHeight() {
		if (nrow()==0)
			return 0;
		return rowScaling.getEnd(nrow()-1);
	}
	
	public int ncol() {
		return colScaling.size();
	}
	
	public int nrow() {
		return rowScaling.size();
	}
	
	public void scale(double boxX, double boxY) {
		colScaling.setScale(boxX);
		rowScaling.setScale(boxY);
		triggerInvalidate();
	}
	
	// return the component that is linked to all settings in the menus
	public PlotComponent getResponsibleComponent() {
		return (PlotComponent)getHeatmapComponent();
	}

	public void setData(PlotContainer pc) {
		vm = pc.getViewModel();
		vm.addViewModelListener(this);
		
		probeOrder = new HierarchicalSortedProbeList(vm, vm.getProbes());
		probeOrder.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				triggerInvalidate();
			}
		});
		
		rowHeights = new RowHeightEnhancementSetting(vm.getDataSet().getMIManager());
		rowHeights.addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				updateRowHeights();
			}
		});
		
		rowScaling.setNumberOfElements(probeOrder.size(),15);
		
		
		// COLUMNS
		columns.clear();
		
		LinkedList<AbstractColumnGroupPlugin> ctypes = new LinkedList<AbstractColumnGroupPlugin>();
		ctypes.add(new MultiExpressionPlugin().init(this));
		
		columnTypes = new SortedExtendableConfigurableObjectListSetting<AbstractColumnGroupPlugin>("Columns", null, new HeatmapColumnBridge(this));
		columnTypes.setTopMost(true);
		columnTypes.addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				refreshColumnsFromSetting();
			}
		});
		columnTypes.setElements(ctypes);
		

		// ROW HEADERS
		rowHeaders.clear();
		
		LinkedList<RowHeaderElement> rhtypes = new LinkedList<RowHeaderElement>();
		
		rhtypes.add(new ClusterTreeHeader().init(this));
		rhtypes.add(new ProbeNameHeader().init(this));
		rhtypes.add(new SelectionIndicationHeader().init(this));
		rhtypes.add(new TopLevelProbeListHeader().init(this));

		rowHeaderTypes = new SortedExtendableConfigurableObjectListSetting<RowHeaderElement>("Row headers", null, new RowHeaderBridge(this));
		rowHeaderTypes.addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				refreshRowHeadersFromSetting();
			}
		});
		rowHeaderTypes.setElements(rhtypes);
		

			
		pc.addViewSetting(columnTypes, getResponsibleComponent());
		pc.addViewSetting(rowHeaderTypes, getResponsibleComponent());
		pc.addViewSetting(probeOrder.getSetting(), getResponsibleComponent());
		pc.addViewSetting(rowHeights, getResponsibleComponent());
		
//		columnHeaders.add(new ColumnNameHeader(this));
		
				
		triggerInvalidate();
	}

	protected void refreshRowHeadersFromSetting() {		
		LinkedList<RowHeaderElement> oldHeaders = new LinkedList<RowHeaderElement>(rowHeaders);
		
		for (RowHeaderElement he : rowHeaders) {
			he.removeUpdateListener(this);
		}
		rowHeaders.clear();
		for (RowHeaderElement he : rowHeaderTypes.getElements()) {
			rowHeaders.add(he);
			he.addUpdateListener(this);
		}
		
		for (RowHeaderElement rhe : oldHeaders)
			if (!rowHeaders.contains(rhe))
				rhe.dispose();

		triggerInvalidate();
	}
	
	protected void refreshColumnsFromSetting() {
		LinkedList<HeatmapColumn> oldCols = new LinkedList<HeatmapColumn>(columns);
		LinkedList<AbstractColumnGroupPlugin> oldColGroups = new LinkedList<AbstractColumnGroupPlugin>(columnGroups);

		for (HeatmapColumn c : columns)
			c.removeUpdateListener(compCentral);
		columns.clear();
		columnGroups.clear();
		addColumnGroups(columnTypes.getElements());
		
		for (HeatmapColumn hc : oldCols)
			if (!columns.contains(hc))
				hc.dispose();
		for (AbstractColumnGroupPlugin hc : oldColGroups)
			for (ColumnHeaderElement che : hc.getColumnHeaderElements())
				che.dispose();
		
		triggerInvalidate();
	}
	
	/** add Columns to the heatmap. 
	 * Does not trigger repaint
	 * @param col column(s) to add
	 */
	private void addColumns(Collection<HeatmapColumn> col) {
		for (HeatmapColumn c : col)
			columns.add(c);
		
		colScaling.setNumberOfElements(columns.size(), 15);
		
		for (int i=0; i!=columns.size(); ++i) {
			HeatmapColumn c = columns.get(i);
			colScaling.setSize(i, (int)Math.abs(c.getDesiredWidth()), c.getDesiredWidth()<0);
			c.addUpdateListener(compCentral);
			c.addUpdateListener(this);
		}		
	}
	
	public void addColumnGroups(Collection<AbstractColumnGroupPlugin> hcgs) {
		LinkedList<HeatmapColumn> cols = new LinkedList<HeatmapColumn>();
		for (AbstractColumnGroupPlugin hcg : hcgs) {
			cols.addAll(hcg.getColumns());
		}
		addColumns(cols);
		columnGroups.addAll(hcgs);
	}
	
	public void addColumnGroups(AbstractColumnGroupPlugin... hcgs) {
		addColumnGroups(Arrays.asList(hcgs));		
	}
	
	public double getScaleX() {
		return colScaling.scaleFactor;
	}
	
	public double getScaleY() {
		return rowScaling.scaleFactor;
	}

	protected void updateRowHeights() {
//		System.out.println("HMS updateRowHeight");
		int i=0;
		for (Probe pb : probeOrder) {
			rowScaling.setSize(i, rowHeights.rowHeight(pb), false);
			++i;
		}
//		System.out.println("HMS updateRowHeight DONE");
		triggerInvalidate();
	}
	
	private void repaint() {
//		System.out.println("HMS repaint");
		compCentral.invalidate();
		compColHeader.invalidate();
		compRowHeader.invalidate();
		compOuter.invalidate();
		compOuter.revalidate();
		compOuter.repaint();
		compColHeader.updatePlot();
		compRowHeader.updatePlot();
	}
	
	public void triggerInvalidate() {
		resizeUpdater.trigger();
	}

	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		switch (vme.getChange()) {
		case ViewModelEvent.DATA_MANIPULATION_CHANGED:
			getHeatmapComponent().repaint();
			//color gradient hsa to be updated, too!
			compColHeader.updatePlot();
			break;
		case ViewModelEvent.PROBE_SELECTION_CHANGED:
			getHeatmapComponent().repaint();
//			getRowHeaderComponent().repaint();
			break;			
		case ViewModelEvent.TOTAL_PROBES_CHANGED:
			rowScaling.setNumberOfElements(((ViewModel)vme.getSource()).getProbes().size(), 5);
			triggerInvalidate();
			break;			
		case ViewModelEvent.PROBELIST_ORDERING_CHANGED: // fallthrough
		case ViewModelEvent.PROBELIST_SELECTION_CHANGED:
			probeOrder.clear();
			probeOrder.addAll(vm.getProbes());			
			triggerInvalidate();
			break;
		}		
	}
	
	public ViewModel getViewModel() {
		return vm;
	}

	public HierarchicalSortedProbeList getSortedProbeList() {
		return probeOrder;
	}
	
	@Override
	public void elementNeedsUpdating(UpdateEvent evt) {
		if (evt.getChange()==UpdateEvent.SIZE_CHANGE) {
			Object o = evt.getSource();
			if (o instanceof HeatmapColumn) {
				HeatmapColumn c = (HeatmapColumn)o;
				int i = columns.indexOf(c);
				colScaling.setSize(i, (int)Math.abs(c.getDesiredWidth()), c.getDesiredWidth()<0);
			}
			triggerInvalidate();
		}
		
		if (evt.getChange()==UpdateEvent.REPAINT) { 
			compColHeader.updatePlot();
			compRowHeader.updatePlot();
		}		
		
		if (evt.getChange()==UpdateEvent.COLUMNS_CHANGE)
			refreshColumnsFromSetting();
	}
	
	public void dispose() {
		// remove all listeners
		rowHeaderTypes.setElements(Collections.<RowHeaderElement>emptyList());
		columnTypes.setElements(Collections.<AbstractColumnGroupPlugin>emptyList());
		vm.removeViewModelListener(this);
		probeOrder.dispose();
	}
}
