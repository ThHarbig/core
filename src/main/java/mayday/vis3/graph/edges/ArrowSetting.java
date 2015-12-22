package mayday.vis3.graph.edges;

import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.vis3.graph.arrows.ArrowPainter;
import mayday.vis3.graph.arrows.BarAndSolidArrowPainter;
import mayday.vis3.graph.arrows.BarArrowPainter;
import mayday.vis3.graph.arrows.BoxArrowPainter;
import mayday.vis3.graph.arrows.CircleArrowPainter;
import mayday.vis3.graph.arrows.DiamondArrowPainter;
import mayday.vis3.graph.arrows.OpenArrowPainter;
import mayday.vis3.graph.arrows.SolidArrowPainter;

public class ArrowSetting extends BooleanHierarchicalSetting 
{
	private ObjectSelectionSetting<ArrowPainter> arrowStyle;	
	private DoubleSetting arrowAngle;	
	private IntSetting arrowSize;
	private BooleanSetting fillArrow;
	
	public static final int SOLID=0;
	public static final int OPEN=1;
	public static final int CIRCLE=2;
	public static final int DIAMOND=3;
	public static final int BAR=4;
	public static final int BAR_AND_ARROW=5;
	public static final int BOX=6;
	
	
	public static final ArrowPainter[] arrowPainters={new SolidArrowPainter(),
		new OpenArrowPainter(),
		new CircleArrowPainter(), 
		new DiamondArrowPainter(),
		new BarArrowPainter(),
		new BarAndSolidArrowPainter(),
		new BoxArrowPainter()}; 
	
	
	public ArrowSetting(String name, boolean def) 
	{
		super(name,null,def);
		arrowStyle=new ObjectSelectionSetting<ArrowPainter>("Arrow Style", "the way the arrow is to be rendererd", 0, arrowPainters);
		arrowAngle=new DoubleSetting("Angle", "The angle of the arrow", (Math.PI * 30.0 /180.0));
		arrowSize=new IntSetting("Arrow Length","The length of the arrow head",10);
		fillArrow=new BooleanSetting("Fill", null, false);
		
		addSetting(arrowStyle).addSetting(arrowAngle).addSetting(arrowSize).addSetting(fillArrow);	
	}
	
	@Override
	public ArrowSetting clone() 
	{
		ArrowSetting clone=new ArrowSetting(getName(), getBooleanValue());
		clone.fromPrefNode(this.toPrefNode());
		return clone;
	}
	
	public ArrowPainter getArrowPainter()
	{
		return arrowStyle.getObjectValue();
	}
	
	public double getAngle()
	{
		return arrowAngle.getDoubleValue();
	}
	
	public int getSize()
	{
		return arrowSize.getIntValue();
	}
	
	public boolean isFill()
	{
		return fillArrow.getBooleanValue();
	}
	
	public void setArrowStyle(ArrowPainter p)
	{
		arrowStyle.setObjectValue(p);
	}
	
	public void setArrowStyle(int i)
	{
		arrowStyle.setSelectedIndex(i);
	}
	
	public void setArrowAngle(double d)
	{
		arrowAngle.setDoubleValue(d);
	}
	
	public void setArrowSize(int i)
	{
		arrowSize.setIntValue(i);
	}
	
	public void setFillArrow(boolean b)
	{
		fillArrow.setBooleanValue(b);
	}
	
	
}
