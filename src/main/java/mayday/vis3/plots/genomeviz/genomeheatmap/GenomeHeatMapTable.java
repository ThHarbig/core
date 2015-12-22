package mayday.vis3.plots.genomeviz.genomeheatmap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Window;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.Scrollable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.typed.BooleanSetting;
import mayday.vis3.ColorProviderSetting;
import mayday.vis3.gui.PlotComponent;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.plots.genomeviz.NoLocusMIO_Panel;
import mayday.vis3.plots.genomeviz.Organiser;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.StrandInformation;
import mayday.vis3.plots.genomeviz.genomeheatmap.additional.MasterManager;
import mayday.vis3.plots.genomeviz.genomeheatmap.delegates.TableComputations;
import mayday.vis3.plots.genomeviz.genomeheatmap.delegates.TableMapper;
import mayday.vis3.plots.genomeviz.genomeheatmap.usergestures.UserGestures;
import mayday.vis3.plots.genomeviz.genomeheatmap.view.MyColorProvider;
import mayday.vis3.plots.genomeviz.genomeheatmap.view.cellrenderer.BoxRendererExp;
import mayday.vis3.plots.genomeviz.genomeheatmap.view.cellrenderer.BoxRendererMIO;
import mayday.vis3.plots.genomeviz.genomeheatmap.view.cellrenderer.BoxRendererPbl;
import mayday.vis3.plots.genomeviz.genomeheatmap.view.cellrenderer.CEmptyHeaderRenderer;
import mayday.vis3.plots.genomeviz.genomeheatmap.view.cellrenderer.CStrandNameRenderer;
import mayday.vis3.plots.genomeviz.genomeheatmap.view.cellrenderer.LastCellRenderer;
 
public class GenomeHeatMapTable extends JTable implements PlotComponent, ViewModelListener, ProbeListListener, Scrollable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5031301611122066645L;
	protected MasterManager master;
	protected GenomeHeatMapComponent component;
	protected GenomeHeatMapTableModel model;
	protected Organiser org;
	// access to data set
	protected ViewModel viewModel;

	protected MyColorProvider coloring;
	
	protected BooleanSetting showGrid; 
	
	
	protected boolean tableAligned = true;
	
	GenomeHeatMapTable(MasterManager masterManager, GenomeHeatMapComponent Component) {
		master = masterManager;
		component = Component;
		System.out.println("ChromeHeatMapTable - Constructor ");

	
		
	
		// changes size of whole table (only meaningful in jScrollPane)
		setAutoResizeMode(AUTO_RESIZE_OFF);
		// set grid unvisible
		setIntercellSpacing(new Dimension(0,0));
		// set JTable visible
		setVisible(true);
		
		this.setName("heatMapTable");
		getTableHeader().setReorderingAllowed(false);
		// selection of elements in a row
		setRowSelectionAllowed(true);
		// selection of elements in a column
		setColumnSelectionAllowed(true);
		// defines how values in table can be selected
		getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				//SINGLE_SELECTION only one single cell can be selected
				//SINGLE_INTERVAL_SELECTION whole column/row can be selected
				//MULTIPLE_INTERVAL_SELECTION selection of multiple columns/rows
		
		addMouseWheelListener(master.getController());
		addKeyListener(master.getController());
		
		// adds mouselistener
		addMouseListener(master.getController());
		
		showGrid = new BooleanSetting("Show grid lines",null,false);
		showGrid.addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				model.setGridVisible(showGrid.getBooleanValue());
				if(model.getGridVisible() == true){
					master.setIntercellSpacingFromTable(1);
				}
				else if(model.getGridVisible() == false){
					master.setIntercellSpacingFromTable(0);
				}				
			}
		});
	}

	/**
	 * always increment table visibleRect in three-row-steps.
	 * @param Rectangle visible rect of table
	 * @param int orientation of the scrollbar
	 * @param int direction decrement or increment
	 */
	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {

		int row = TableComputations.getFirstVisibleRow(visibleRect.y, model
				.getTableSettings().getBoxSizeY());
		StrandInformation strand = TableMapper.getStrand(row, model
				.getRowCount());
		int valueUp = 0;
		int valueDown = 0;

		int boxSizeY = this.model.getTableSettings().getBoxSizeY();
		int diff = visibleRect.y - (row * boxSizeY);
		int rest = (boxSizeY - diff);

		if (strand == StrandInformation.MINUS) {
			valueUp = diff;
			valueDown = rest + 2 * boxSizeY;
		} else if (strand == StrandInformation.PLUS) {
			valueUp = diff + boxSizeY;
			valueDown = rest + boxSizeY;
		} else if (strand == StrandInformation.PLACEHOLDER) {
			valueUp = diff + 2 * boxSizeY;
			valueDown = rest;
		}
		
		valueUp = Math.abs(valueUp);
		valueDown = Math.abs(valueDown);
		// up
		if (direction < 0) {
			if (valueUp == 0)return boxSizeY * 3;
			else return valueUp;
		}
		// down
		else {
			if (valueDown == 0)	return boxSizeY * 3;
			else return valueDown;
		}
	}
	
	
	// stes model of table global
	public void setInternalTableModel(TableModel Model){
		model = (GenomeHeatMapTableModel)Model;
		
		switch(model.getStyle()){
		case CLASSIC:
			setBackground(Color.BLACK);
			break;
		case MODERN:
			setBackground(Color.white);
			break;
		}
	}
	
	// apply box size
	public void applyBoxSize() {

			// set height of boxes
			this.setRowHeight(model.getTableSettings().getBoxSizeY());
			
			// Liefert Abstand zwischen zwei Zellen (horizontal/vertikal): 1 falls Grid gesetzt wurde
//			int boxColumnWidth = getIntercellSpacing().width;
		
			// set size for columns here also for the first column
			for(int i = 0; i < getColumnCount(); i++){
				TableColumn tableColumn = getColumnModel().getColumn(i);
				tableColumn.setMinWidth(0);

				tableColumn.setMaxWidth(model.getTableSettings().getBoxSizeX());
				tableColumn.setMinWidth(model.getTableSettings().getBoxSizeX());
				tableColumn.setResizable(false);
				// Komplette Breite der Tabelle jeweils mit Abstand zwischen zwei Zellen
				//boxColumnWidth += boxSizeX + getIntercellSpacing().width;
//				boxColumnWidth += model.getTableSettings().getBoxSizeX() + getIntercellSpacing().width;
			}

			// call invalidate and waits until all components are new computed
			revalidate();

			// repaints the region
			repaint();

	}
		
	
	/**
	 * called if Plot is added to plot container, so called from addNotify().
	 * @param PlotContainer
	 */
	public void setup(PlotContainer plotContainer) {
		plotContainer.setPreferredTitle("Genome HeatStream",this);
		viewModel = plotContainer.getViewModel();
		org = Organiser.getInstance(viewModel);
		org.addPlotComponent(this);

		if (org.isDataSetted() && org.containsLoci()) {
			// probes = new SortedProbeList(viewModel, viewModel.getProbes());
			coloring = new MyColorProvider(viewModel);
			if (org.getColoring() != null) {
				coloring.init(org.getColoring());
			} else {
				coloring.init(0, 1);
			}

			// sets the table model global
			setInternalTableModel(getModel());
			// Initialize the Data
			model.initializeData(viewModel, org, coloring);
			

			editViewMenu(plotContainer);
			editSelectionMenu(plotContainer);

			// adds Controller to colorProvider
			coloring.addChangeListener(master.getController());
			viewModel.addRefreshingListenerToAllProbeLists(this, true);
			viewModel.addViewModelListener(this);
			addListenerToModel();
			updatePlot();

		} else{
			createDefaultPanel();
		}
	}

	private void createDefaultPanel() {
		component.add(new NoLocusMIO_Panel(component),BorderLayout.CENTER);
	}

	private void editSelectionMenu(PlotContainer plotContainer) {
//		JMenu selectionMenu = plotContainer.getMenu(PlotContainer.SELECTION_MENU, this);
//		selectionMenu.add(new JSeparator());
	}

	@SuppressWarnings("deprecation")
	private void editViewMenu(PlotContainer plotContainer) {
		// get menu
//		// set grid visible/unvisible
//		JMenuItem invertGridSettings = invertGridButton();
//		viewMenu.add(invertGridSettings);
		
		
//		plotContainer.addViewSetting(master.getZoomingSetting(),this);
		plotContainer.addViewSetting(master.menuManager.getViewingSetting(), this);
		// for the selection of the right experiment
		plotContainer.addViewSetting(coloring.getSetting(),this);
		plotContainer.addViewSetting(master.menuManager.getStyleSetting(), this);
		plotContainer.addViewSetting(showGrid, this);

		
		JMenu find = master.findProbe_window();
		JMenuItem gotoPosition = new JMenuItem("Find position...");
		gotoPosition.addActionListener(master.getController());
		gotoPosition.setActionCommand(UserGestures.GOTO_POSITION);
		find.add(gotoPosition);
		JMenu viewMenu = plotContainer.getMenu(PlotContainer.VIEW_MENU, this);
		viewMenu.add(master.menuManager.getZoomViewMenu().setZoomMenu());
		viewMenu.add(master.setSpecChrSelMenu());
		viewMenu.add(master.selectRangeOfProbesForNewWindow());
		viewMenu.add(find);				
	}


	/**
	 * adds listener to ChromeHeatMapTableModel, if TableModel fires event,
	 * updatePlot() is called.
	 */
	private void addListenerToModel() {
		model.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				updatePlot();
			}
		});
	}


	/**
	 * 
	 * @return
	 */
//	private JMenuItem invertGridButton() {
//		JMenuItem invertGridSettings = new JMenuItem("Invert Grid Settings");
//		invertGridSettings.setActionCommand(UserGestures.GRID);
//		invertGridSettings.addActionListener(master.getController());
//		return invertGridSettings;
//	}
	
	/**
	 * 
	 */
	public void removeNotify() {
		super.removeNotify();
		if(model!=null){
			model.removeNotify();
		}

		if (coloring!=null)
			coloring.removeNotify();
		
		if(viewModel!=null){
			viewModel.removeViewModelListener(this);
		}

		if(model!=null){
			model.clearData();
			model = null;
		}
		
		if(org!=null){
			org.removePlotComponent(this);
			org = null;
		}
	}
	
	// diese Methode wird aufgerufen um ein Objekt am Bildschirm darzustellen
	// also wenn b.add(f); dann ruft f addNotify() auf
	/* From Component */
	public void addNotify() {

		// calls addNotify() -> always when component is added to container
		super.addNotify();
		Component comp = this;
		
		// get outermost component and call setup for component
		while (comp!=null && !(comp instanceof PlotContainer)) {
			comp=comp.getParent();
		}
			
		if (comp!=null) {
			// call setup for outermost component
			setup((PlotContainer)comp);
		}		
	}
	
	/**
	 * get outermost window.
	 * @return
	 */
	protected Window getOutermostJWindow() {
		System.out.println("ChromeHeatMapTable getOutermostJWindow");
		System.out.println(" ");
		Component comp = this;
		while (comp!=null && !(comp instanceof Window)) {
			comp=comp.getParent();
		}
		return((Window)comp);
	}
	
	/**
	 * is being called at initialization and window resize.
	 */
	public void updatePlot() {
		if (org.isDataSetted() && model.isInitialized() && getColumnModel().getColumnCount()>0) {
			// set cell renderer for first column (strand information)
			{
				// table column represents attributes
				TableColumn tableColumn = getColumnModel().getColumn(0);
				switch (model.getStyle()) {
				case CLASSIC:

					// set cell renderer to draw individual values for each
					// column
					tableColumn.setCellRenderer(new CStrandNameRenderer(master,
							model));
					// sets header to empty header (white)
					tableColumn.setHeaderRenderer(new CEmptyHeaderRenderer(
							model));
					// paint the last column black
					if (model.getNumberOfBackUnusedColumns() > 0) {
						tableColumn = getColumnModel().getColumn(
								getColumnModel().getColumnCount()
										- model.getNumberOfBackUnusedColumns());
						tableColumn
								.setCellRenderer(new LastCellRenderer(model));
						tableColumn.setHeaderRenderer(new CEmptyHeaderRenderer(
								model));
					}
					break;
				case MODERN:
					// set cell renderer to draw individual values for each
					// column
					tableColumn.setCellRenderer(new CStrandNameRenderer(master,
							model));
					// sets header to empty header (white)
					tableColumn.setHeaderRenderer(new CEmptyHeaderRenderer(
							model));
					// paint the last column black
					if (model.getNumberOfBackUnusedColumns() > 0) {
						tableColumn = getColumnModel().getColumn(
								getColumnModel().getColumnCount()
										- model.getNumberOfBackUnusedColumns());
						tableColumn
								.setCellRenderer(new LastCellRenderer(model));
						tableColumn.setHeaderRenderer(new CEmptyHeaderRenderer(
								model));
					}
					break;
				}

				// skip first column and set cell renderer for each column
				// set empty header renderer for all other columns
				for (int i = model.getNumberOfFrontUnusedColumns(); i != getColumnModel()
						.getColumnCount()
						- model.getNumberOfBackUnusedColumns(); ++i) {
					tableColumn = getColumnModel().getColumn(i);
					tableColumn.setHeaderRenderer(new CEmptyHeaderRenderer(
							model));
					if (this.coloring.getColoringMode() == ColorProviderSetting.COLOR_BY_EXPERIMENT_VALUE) {
						tableColumn.setCellRenderer(new BoxRendererExp(
								coloring,  master,
								model));
					} else if (this.coloring.getColoringMode() == ColorProviderSetting.COLOR_BY_MIO_VALUE) {
						tableColumn.setCellRenderer(new BoxRendererMIO(
								coloring,  master,
								model));
					} else if (this.coloring.getColoringMode() == ColorProviderSetting.COLOR_BY_TOP_PRIORITY_PROBELIST) {
						tableColumn.setCellRenderer(new BoxRendererPbl(
								coloring,  master,
								model));
					}
				}
				applyBoxSize();
			}
		}
	}
	

	/**
	 * 
	 */
	public void viewModelChanged(ViewModelEvent vme) {
		if(vme.getChange() == ViewModelEvent.PROBE_SELECTION_CHANGED ){
			repaint();
		}
	}
	
	/**
	 * 
	 */
	public void probeListChanged(ProbeListEvent event) {
		switch (event.getChange()) {
		case ProbeListEvent.OVERALL_CHANGE:
			updatePlot();
			break;
		default:
			//do nothing
		}
	}
}
