package mayday.vis3.plots.heatmap2.headers.column;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;

import mayday.core.gui.components.VerticalLabel;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.typed.StringSetting;
import mayday.vis3.plots.heatmap2.columns.plugins.AbstractColumnGroupPlugin;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.headers.AbstractCellBasedColumnHeader;
import mayday.vis3.plots.heatmap2.headers.ColumnHeaderElement;
import mayday.vis3.plots.heatmap2.interaction.UpdateEvent;

public class ManualTextHeader extends AbstractCellBasedColumnHeader implements SettingChangeListener {

	protected VerticalLabel label = new VerticalLabel();
	protected JLabel horizontalLabel = new JLabel();
	protected HeatmapStructure data;
	protected int hsize;
	protected int maxHeight;
	protected StringSetting columnName;

	@Override
	public int getSize() {		
		return hsize;
	}

	@Override
	public void render(Graphics2D graphics, AbstractColumnGroupPlugin group) {
		maxHeight = 0;
		renderCells(graphics, data, group);
		if (maxHeight!=hsize) {
			hsize = maxHeight;
			fireChange(UpdateEvent.SIZE_CHANGE);	
		}
	}

	@Override
	public void renderCell(Graphics2D g, int column, AbstractColumnGroupPlugin group) {
		Rectangle2D rect = g.getClipBounds();
		
		horizontalLabel.setText(columnName.getStringValue());
		horizontalLabel.setSize(horizontalLabel.getPreferredSize());
		
		label.setText(columnName.getStringValue());		
		label.setSize(label.getPreferredSize());
		
		double colWidth = data.getColWidth(column);
		// first try to label the column in normal orientation
		
		if (!placeLabel(g, rect, (int)colWidth, horizontalLabel))
			placeLabel(g, rect, (int)colWidth, label);
	}
	
	protected boolean placeLabel(Graphics2D g, Rectangle2D rect, int columnWidth, JLabel label) {
		if (columnWidth<label.getWidth()) {
			// no rendering here
			return false;
		} else {
			int xdelta = (int)(columnWidth-label.getWidth())/2;
			int ydelta = (int)(rect.getHeight()-label.getHeight());
			g.translate(xdelta, ydelta);
			label.paint(g);
			g.translate(-xdelta, -ydelta);
			maxHeight = Math.max(maxHeight, label.getHeight());
			return true;
		}
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				getClass(),
				"PAS.Heatmap.ColumnHeader.ManualText",
				null,
				MC_COL,
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Add column names as column headers",
				"Manually defined header"
				);
	}

	@Override
	public ColumnHeaderElement init(HeatmapStructure struct, AbstractColumnGroupPlugin group) {
		data=struct;
		columnName = new StringSetting("Column header",null,"");
		columnName.addChangeListener(this);
		return this;
	}
	
	public void dispose() { 
		columnName.removeChangeListener(this);
	}

	@Override
	public void stateChanged(SettingChangeEvent e) {
		fireChange(UpdateEvent.REPAINT);
	}
	
	public Setting getSetting() {
		return columnName;
	}

}
