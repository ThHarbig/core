package mayday.vis3.plots.heatmap2.headers.column;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.ClassSelectionModel;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.typed.ClassSelectionSetting;
import mayday.vis3.categorical.ClassSelectionColoring;
import mayday.vis3.plots.heatmap2.columns.HeatmapColumn;
import mayday.vis3.plots.heatmap2.columns.plugins.AbstractColumnGroupPlugin;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.headers.AbstractCellBasedColumnHeader;
import mayday.vis3.plots.heatmap2.headers.ColumnHeaderElement;
import mayday.vis3.plots.heatmap2.interaction.UpdateEvent;

public class ClassSelectionHeader extends AbstractCellBasedColumnHeader implements SettingChangeListener{

	protected HeatmapStructure data;
	protected ClassSelectionSetting cs;
	protected ClassSelectionColoring csc;
	
	@Override
	public int getSize() {
		return 3;
	}

	@Override
	public void render(Graphics2D graphics, AbstractColumnGroupPlugin group) {
		renderCells(graphics, data, group);
	}

	@Override
	public void renderCell(Graphics2D g, int column, AbstractColumnGroupPlugin group) {
		HeatmapColumn theCol = data.getColumn(column);
		if (group.getColumns().contains(theCol)) {
			String cn = theCol.getName();
			Color c = csc.getColorForObject(cn);
			if (c!=null) {
				Rectangle2D rect = g.getClipBounds();
				g.setColor(c);
				g.fill(rect);
			}
		}
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				getClass(),
				"PAS.Heatmap.ColumnHeader.ClassSelection",
				null,
				MC_COL,
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Add a class selection as column headers",
				"Class Selection"
				);
	}

	@Override
	public ColumnHeaderElement init(HeatmapStructure struct, AbstractColumnGroupPlugin group) {
		data=struct;
		
		LinkedList<String> colnames = new LinkedList<String>();
		for (HeatmapColumn col : group.getColumns())
			colnames.add(col.getName());
		
		ClassSelectionModel csm = new ClassSelectionModel(colnames);
		cs = new ClassSelectionSetting("Class Selection",null,csm, 1, colnames.size(), struct.getViewModel().getDataSet());
		cs.addChangeListener(this);
		updateMapping();
		
		return this;
	}

	protected void updateMapping() {
		if (csc!=null)
			csc.hideColorAssignmentWindow();
		ClassSelectionModel csm = cs.getModel();
		csc = new ClassSelectionColoring(csm, data.getViewModel(), "Class Labels");
		csc.addListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				fireChange(UpdateEvent.REPAINT);
			}
		});
		csc.showColorAssignmentWindow();
	}
	
	@Override
	public void stateChanged(SettingChangeEvent e) {
		updateMapping();
		fireChange(UpdateEvent.REPAINT);		
	}
	
	public Setting getSetting() {
		return cs;
	}

	@Override
	public void dispose() {
		if (csc!=null)
			csc.hideColorAssignmentWindow();
    }

}
