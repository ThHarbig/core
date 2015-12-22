package mayday.vis3.graph.edges;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.generic.PluginInstanceSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.vis3.graph.arrows.ArrowPainter;
import mayday.vis3.graph.edges.router.AbstractEdgeRouter;
import mayday.vis3.graph.edges.router.SimpleEdgeRouter;
import mayday.vis3.graph.edges.strokes.BackwardSlashStroke;
import mayday.vis3.graph.edges.strokes.ContiguousArrowStroke;
import mayday.vis3.graph.edges.strokes.EdgeStroke;
import mayday.vis3.graph.edges.strokes.ForwardSlashStroke;
import mayday.vis3.graph.edges.strokes.SeparateArrowStroke;
import mayday.vis3.graph.edges.strokes.TaperedStroke;
import mayday.vis3.graph.edges.strokes.VerticalSlashStroke;
import mayday.vis3.graph.edges.strokes.WobbleStroke;
import mayday.vis3.graph.edges.strokes.ZigzagStroke;
import mayday.vis3.graph.edges.strokes.PipeStroke.Type;

public class EdgeSetting extends HierarchicalSetting 
{
	private ArrowSetting sourceArrow;
	private ArrowSetting targetArrow;
	
	private ColorSetting edgeColor;
	
	private BooleanHierarchicalSetting showLabelSetting;
	private RestrictedStringSetting labelPlacementSetting;
	private RestrictedStringSetting	weightSetting; 
	
	private BooleanHierarchicalSetting useWeight;
	private ObjectSelectionSetting<Stroke> stroke;
	private PluginInstanceSetting<AbstractEdgeRouter> router; 
	
	private String targetRole="";
	
	public static final Stroke[] strokes={
		EdgeStroke.createEdgeStroke(2.0f),
		EdgeStroke.createDashedEdgeStroke(2.0f),
		EdgeStroke.createDottedEdgeStroke(2.0f),
//		new SineWaveStroke(2.0f, 10, 5),
		new ZigzagStroke(new BasicStroke(2.0f), 10, 5),
		new WobbleStroke(5, 2.5f),
		new BackwardSlashStroke(2.0f,Type.BACKWARD),
		new ForwardSlashStroke(2.0f,Type.FORWARD),
		new VerticalSlashStroke(2.0f, Type.VERTICAL),
		new ContiguousArrowStroke(2.0f),
		new SeparateArrowStroke(2.0f),
		new TaperedStroke()
	
	};
	
	public static final String[] labelpos={"Begin","Center","End"};
	public static final String[] weightStrokes={"Width",
//		"Sine Frequency",
		"Wobble Frequency","Zigzag Frequency"};
	
	
	public static final int SOLID_STROKE=0;
	public static final int DASHED_STROKE=1;
	public static final int DOTTED_STROKE=2;
	
	
	public static final int BEGIN=0;
	public static final int CENTER=1;
	public static final int END=2;
	
	public EdgeSetting(String name) 
	{
		super(name);	
		sourceArrow=new ArrowSetting("Source Arrow", false);
		targetArrow=new ArrowSetting("Target Arrow", true);
		edgeColor=new ColorSetting("Color", null, Color.black);
					
		stroke=new ObjectSelectionSetting<Stroke>("Stroke",null,0,strokes);		
		showLabelSetting=new BooleanHierarchicalSetting("Show label", null, true);
		
		String[] labelpos={"Begin","Center","End"};
		labelPlacementSetting=new RestrictedStringSetting("Label Placement", null, 0, labelpos);
		showLabelSetting.addSetting(labelPlacementSetting);

		useWeight=new BooleanHierarchicalSetting("Display Weights",null,true);
		weightSetting=new RestrictedStringSetting("Display Weight", null, 0, weightStrokes);
		useWeight.addSetting(weightSetting);
		
		router=new PluginInstanceSetting<AbstractEdgeRouter>("Edge Router", null, AbstractEdgeRouter.MC);
		router.setInstance(new SimpleEdgeRouter());
		
		addSetting(sourceArrow).addSetting(targetArrow).
		addSetting(edgeColor).
		addSetting(stroke).
		addSetting(useWeight).
		addSetting(showLabelSetting);	
		addSetting(router);
		
	}
	
	public EdgeSetting() 
	{
		this("Edge drawing");
	}
	
	@Override
	public EdgeSetting clone() 
	{
		EdgeSetting res=new EdgeSetting();
		res.fromPrefNode(toPrefNode());
		return res;
	}
	
	public boolean isUseWeight()
	{
		return useWeight.getBooleanValue();
	}
	
	public boolean isShowLabel()
	{
		return showLabelSetting.getBooleanValue();
	}
	
	public Stroke getStroke()
	{
		return stroke.getObjectValue();
	}
	
	public boolean isDrawSourceArrow()
	{
		return sourceArrow.getBooleanValue();
	}
	
	public boolean isDrawTargetArrow()
	{
		return targetArrow.getBooleanValue();
	}
	
	public boolean isFillSourceArrow()
	{
		return sourceArrow.isFill();
	}
	
	public boolean isFillTargetArrow()
	{
		return targetArrow.isFill();
	}
	
	public ArrowSetting getSourceArrow()
	{
		return sourceArrow;
	}
	
	public ArrowSetting getTargetArrow()
	{
		return targetArrow;
	}
	
	public Color getColor()
	{
		return edgeColor.getColorValue();
	}
	
	public int getLabelPlacement()
	{
		return labelPlacementSetting.getSelectedIndex();
	}
	
	
	
	
	public void setSourceArrowStyle(int i)
	{
		sourceArrow.setArrowStyle(i);
	}
	
	public void setSourceArrowStyle(ArrowPainter p)
	{
		sourceArrow.setArrowStyle(p);
	}
	
	public void setSourceArrowAngle(double d)
	{
		sourceArrow.setArrowAngle(d);
	}
	
	public void setSourceArrowSize(int i)
	{
		sourceArrow.setArrowSize(i);
	}
	
	public void setSourceFillArrow(boolean b)
	{
		sourceArrow.setFillArrow(b);
	}
	
	public void setTargetArrowStyle(int i)
	{
		targetArrow.setArrowStyle(i);
	}	
	
	public void setTargetArrowStyle(ArrowPainter p)
	{
		targetArrow.setArrowStyle(p);
	}
	
	public void setTargetArrowAngle(double d)
	{
		targetArrow.setArrowAngle(d);
	}
	
	public void setTargetArrowSize(int i)
	{
		targetArrow.setArrowSize(i);
	}
	
	public void setTargetFillArrow(boolean b)
	{
		targetArrow.setFillArrow(b);
	}
	
	public void setPaintSourceArrow(boolean b)
	{
		sourceArrow.setBooleanValue(b);
	}
	
	public void setPaintTargetArrow(boolean b)
	{
		targetArrow.setBooleanValue(b);
	}
	
	public void setEdgeColor(Color c)
	{
		edgeColor.setColorValue(c);
	}
	
	public void setShowLabelSetting(boolean b)
	{
		showLabelSetting.setBooleanValue(b);
	}
	
	public void setLabelPlacementSetting(int i)
	{
		labelPlacementSetting.setSelectedIndex(i);
	}
	
	public void setUniformStroke(boolean b)
	{
		useWeight.setBooleanValue(b);
	}
	
	public void setStroke(int i)
	{
		stroke.setSelectedIndex(i);
	}
	
	public Stroke getWidthStroke(double w)
	{
		switch(weightSetting.getSelectedIndex())
		{
		case 0:	return new BasicStroke((float)w);
//		case 1:	return new SineWaveStroke(2, (float)(1.0/w), 5);
		case 1:	return new WobbleStroke(2,(float)(1.0/w) );
		case 2:	return new ZigzagStroke(new BasicStroke(2),(float)(1.0/w), 5f);
			
		}
		return new BasicStroke((float)w);
	}
	
	public AbstractEdgeRouter getRouter()
	{
		return router.getInstance();
	}
	
	public void setRouter(AbstractEdgeRouter router)
	{
		this.router.setInstance(router);
	}
	
	public void setTargetRole(String targetRole) {
		this.targetRole = targetRole;
	}
	
	public String getTargetRole() {
		return targetRole;
	}
	
	
}
