package mayday.vis3.plots.heatmap2.columns.plugins.enhance;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JLabel;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.vis3.plots.heatmap2.columns.AbstractProbeSelectionColumn;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.interaction.UpdateEvent;

public class TextualMetaColumn extends AbstractProbeSelectionColumn implements SettingChangeListener {

	protected HierarchicalSetting setting;
	
	protected IntSetting pxwidth;
	protected DoubleSetting relwidth;
	protected SelectableHierarchicalSetting wtype;
	
	protected MIGroupSetting meta;
	protected MIGroup migroup;
	
	protected BooleanSetting showSelection;
	protected JLabel theLabel;
	
	protected double colWidth=0;
	protected double maxColWidth=30;
	protected double lastScale;
	protected double lastReportedColWidth;
	
	
	public TextualMetaColumn(HeatmapStructure struct) {
		super(struct);
		
		pxwidth = new IntSetting("With in pixels",null, (int)maxColWidth, 1, null, true, false);
		relwidth= new DoubleSetting("Relative width",null, 1d, .1, null, true, false);
		wtype = new SelectableHierarchicalSetting("Maximal column width", null, 0, new Object[]{pxwidth, relwidth});
		
		meta = new MIGroupSetting("Meta information",null, null, struct.getViewModel().getDataSet().getMIManager(), false); 
		
		showSelection = new BooleanSetting("Indicate selection",null, true);
		
		setting = new HierarchicalSetting("Textual Meta Column").addSetting(wtype).addSetting(meta).addSetting(showSelection);
		setting.addChangeListener(this);
		
		theLabel = new JLabel();
		theLabel.setHorizontalAlignment(JLabel.LEFT);
		theLabel.setBackground(Color.WHITE);
		theLabel.setOpaque(true);
	}
	
	@Override
	public String getName() {
		return migroup.getName();
	}

	@Override
	public void render(Graphics2D graphics, int row, int col, boolean isSelected) {
		
		MIType mt = migroup.getMIO(data.getProbe(row));
		if (mt==null)
			return;
		
		theLabel.setText(mt.toString());
		theLabel.setSize(theLabel.getPreferredSize());
		if (data.getRowHeight(row)<theLabel.getHeight()) {
			// no rendering here
		} else {
			
			if (data.isSelected(row) && showSelection.getBooleanValue())
				theLabel.setForeground(Color.red);
			else
				theLabel.setForeground(Color.black);
			
			int xdelta = 0;
			int ydelta = (int)(data.getRowHeight(row)-theLabel.getHeight())/2;

			graphics.translate(xdelta, ydelta);
			theLabel.paint(graphics);
			graphics.translate(-xdelta, -ydelta);
			
			colWidth = Math.max(colWidth, theLabel.getWidth());
			updateWidth();
		}
		
	}
	
	

	@Override
	public double getDesiredWidth() {
		if (wtype.getObjectValue()==pxwidth)
			return -pxwidth.getIntValue();
		else
			return relwidth.getDoubleValue();
	}

	@Override
	public void stateChanged(SettingChangeEvent e) {
		if (e.hasSource(meta)) {
			migroup = meta.getMIGroup();
		} 
		if (e.hasSource(wtype)) {
			lastScale=Double.NEGATIVE_INFINITY;
		}
		updateWidth();
		fireChange(UpdateEvent.REPAINT);		
	}

	protected void updateWidth() {
		if (this.lastScale!=data.getScaleX()) {
			if (wtype.getObjectValue()==pxwidth) {
				maxColWidth = pxwidth.getIntValue();
			} else {
				maxColWidth = data.getScaleX() * relwidth.getDoubleValue();
			}
			lastScale=data.getScaleX();
		}
		double ncw = Math.min(maxColWidth, colWidth);
		if (ncw!=lastReportedColWidth) {
			lastReportedColWidth = ncw;
			fireChange(UpdateEvent.SIZE_CHANGE);
		}
			
		
	}
	
	@Override
	public void dispose() { /*nada*/ }

}
