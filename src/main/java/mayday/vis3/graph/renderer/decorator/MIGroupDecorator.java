//package mayday.vis3.graph.renderer.decorator;
//
//import mayday.core.meta.MIGroup;
//import mayday.vis3.gradient.ColorGradient;
//import mayday.vis3.graph.renderer.ComponentRenderer;
//import mayday.vis3.graph.renderer.MIOColoring;
//import mayday.vis3.graph.renderer.RendererDecorator;
//
//public abstract class MIGroupDecorator extends RendererDecorator
//{
//	private MIGroup miGroup;
//	
//	protected MIOColoring coloring;
//	
//	public MIGroupDecorator(ComponentRenderer renderer, MIGroup group) 
//	{
//		super(renderer);
//		miGroup=group;
//		coloring.setMIGroup(group);
//		coloring=new MIOColoring();
//		
//	}
//	
//	public MIGroupDecorator(ComponentRenderer renderer, MIGroup group, ColorGradient gradient) 
//	{
//		super(renderer);
//		miGroup=group;		
//		coloring=new MIOColoring();
//		coloring.setMIGroup(group);
//		coloring.setColorGradient(gradient);
//		
//	}
//	
//	public MIGroup getMiGroup() {
//		return miGroup;
//	}
//
//	public void setMiGroup(MIGroup miGroup) 
//	{
//		this.miGroup = miGroup;
//		coloring.setMIGroup(miGroup);
//	}
//	
//	protected MIOColoring getColoring() {
//		return coloring;
//	}
//
//	protected void setColoring(MIOColoring coloring) {
//		this.coloring = coloring;
//	}
//
//
//	
//	
//}
