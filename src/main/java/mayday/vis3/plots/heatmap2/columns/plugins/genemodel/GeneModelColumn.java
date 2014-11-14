package mayday.vis3.plots.heatmap2.columns.plugins.genemodel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.genetics.LocusMIO;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.genetics.coordinatemodel.GBAtom;
import mayday.vis3.plots.heatmap2.columns.AbstractProbeSelectionColumn;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.interaction.UpdateEvent;

public class GeneModelColumn extends AbstractProbeSelectionColumn implements SettingChangeListener {

	protected HierarchicalSetting setting;
	
	protected IntSetting pxwidth;
	protected DoubleSetting relwidth;
	protected SelectableHierarchicalSetting wtype;	
	
	protected MIGroupSetting locusMI;
	protected BooleanSetting showSelection;
	
	protected final static double INTRONSIZE=0.025; // 2.5% per intron
	
	protected TreeMap<Long, Double> positionMap; // map base position to percentage in plot
	
	public GeneModelColumn(HeatmapStructure struct) {
		super(struct);
		
		pxwidth = new IntSetting("With in pixels",null, 300, 1, null, true, false);
		relwidth= new DoubleSetting("Relative width",null, 1d, .1, null, true, false);
		wtype = new SelectableHierarchicalSetting("Column width", null, 0, new Object[]{pxwidth, relwidth});
		
		locusMI = new MIGroupSetting("Gene Models", null, null, struct.getViewModel().getDataSet().getMIManager(), false)
							.setAcceptableClass(LocusMIO.class);
		showSelection = new BooleanSetting("Indicate selection", null, true);
		
		setting = new HierarchicalSetting("Gene Model Column").addSetting(wtype).addSetting(locusMI).addSetting(showSelection);
		setting.addChangeListener(this);
	}
	
	@Override
	public String getName() {
		return "Gene Models"+(locusMI.getMIGroup()==null?"":" from "+locusMI.getMIGroup().getName());
	}

	@Override
	public void render(Graphics2D graphics, int row, int col, boolean isSelected) {		
		graphics.setColor(Color.white);
		Rectangle bounds = graphics.getClipBounds();
		graphics.fill(bounds);
		
		if (positionMap==null)
			recomputeMaxLen();
		
		if (positionMap==null)
			return;
		
		Probe pb = data.getProbe(row);
		MIGroup lmg = locusMI.getMIGroup();
		if (lmg==null)
			return;
		LocusMIO lm = (LocusMIO)lmg.getMIO(pb);
		if (lm==null)
			return;
		
		List<GBAtom> atoms = lm.getValue().getCoordinate().getModel().getCoordinateAtoms();
		int width = bounds.width-10;

		int lastEnd=-1;

		graphics.setColor(Color.LIGHT_GRAY);
		graphics.drawLine(5, bounds.height/2, bounds.width-5, bounds.height/2);
		
		Color color = Color.black;
		if (isSelected && showSelection.getBooleanValue())
			color = Color.red;
		graphics.setColor(color);
		int linePos = bounds.height/2;
		int boxStart = 2;
		int boxEnd = bounds.height-2;
		int boxHeight = boxEnd-boxStart;
		if (boxHeight<2) {
			boxHeight=1;
			boxStart = linePos;
		}
		
		for (GBAtom atom : atoms) {
			Double fromPerc = positionMap.get(atom.from);
			Double toPerc = positionMap.get(atom.to);
			
			int fromPix = (int)(width*fromPerc) +5;
			int toPix = (int)(width*toPerc) +5; 
			
			graphics.fillRect(fromPix, boxStart, Math.max(toPix-fromPix,1), boxHeight);
			if (lastEnd>-1 && boxHeight>1) 
				graphics.drawLine(lastEnd, linePos, fromPix, linePos);
			lastEnd = toPix;
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
		if (e.hasSource(wtype))
			fireChange(UpdateEvent.SIZE_CHANGE);
		else
			fireChange(UpdateEvent.REPAINT);
		
		if (e.hasSource(locusMI) ) {
			recomputeMaxLen();
		}
	}
	
	public void recomputeMaxLen() {
		MIGroup lmg = locusMI.getMIGroup();

		if (lmg!=null) {
			long maxLengthOfExons=0;
			long maxNumberOfIntrons=0;
			positionMap = new TreeMap<Long, Double>();
			TreeMap<Long, Integer> positionType = new TreeMap<Long, Integer>(); // 1=from, 2=to, 3=both
			
			for (Probe pb : data.getViewModel().getProbes()) {
				LocusMIO lm = (LocusMIO)lmg.getMIO(pb);
				if (lm!=null) {
					AbstractGeneticCoordinate agc = lm.getValue().getCoordinate();
					long exonlength=0;
					long introns=0;
					for (GBAtom gba : agc.getModel().getCoordinateAtoms()) {
						exonlength+=gba.to-gba.from;
						++introns;
						Integer i = positionType.get(gba.from);
						if (i==null)
							i=0;
						i|=1;
						positionType.put(gba.from, i);
						i = positionType.get(gba.to);
						if (i==null)
							i=0;
						i|=2;
						positionType.put(gba.to, i);
					}
					--introns;
					maxLengthOfExons = Math.max(maxLengthOfExons,exonlength);
					maxNumberOfIntrons = Math.max(maxNumberOfIntrons,introns);
				}
			}
			
			// compute the range
			double exonPercentage = 1.0-INTRONSIZE*maxNumberOfIntrons;
			double percentPerBaseExon = exonPercentage / (double)maxLengthOfExons;
			
			// produce the mapping
			long lastPosition=-1;
			boolean lastPositionWasStart=true;
			double percentage=0;
			for (Long currentPosition : positionType.keySet()) {
				Integer currentMode = positionType.get(currentPosition);
				if (lastPosition<0)
					lastPosition = currentPosition;
				boolean currentPositionIsStart = ((currentMode&1)!=0);
				if (currentPositionIsStart && !lastPositionWasStart)
					percentage += INTRONSIZE;
				else {
					percentage += ((double)(currentPosition-lastPosition))*percentPerBaseExon;
				}
				positionMap.put(currentPosition, percentage);
				lastPositionWasStart = currentPositionIsStart;
				lastPosition = currentPosition;
			}
			
			if (percentage>1) {
				double factor = 1/percentage; 
				for (Entry<Long, Double> entry : positionMap.entrySet()) {
					entry.setValue(entry.getValue()*factor);
				}
			}
			
		}
	}

	@Override
	public void dispose() { /*nada*/ }

}
