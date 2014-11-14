package mayday.vis3.graph.renderer;

import mayday.vis3.graph.renderer.primary.BoxRenderer;
import mayday.vis3.graph.renderer.primary.ChromogramRenderer;
import mayday.vis3.graph.renderer.primary.CircleRenderer;
import mayday.vis3.graph.renderer.primary.DefaultComponentRenderer;
import mayday.vis3.graph.renderer.primary.DiamondRenderer;
import mayday.vis3.graph.renderer.primary.GradientGeneRenderer;
import mayday.vis3.graph.renderer.primary.HeatMapRenderer;
import mayday.vis3.graph.renderer.primary.ProfilePlotRenderer;
import mayday.vis3.graph.vis3.SuperColorProvider;

public abstract class RendererFactory 
{
	public static final String DEFAULT_RENDERER="Default Renderer";
	public static final String BOX_RENDERER="Box";
	public static final String DIAMOND_RENDERER="Diamond";
	public static final String CIRCLE_RENDERER="Circle";
	
	public static final String GRADIENT_RENDERER="Gradient";
	public static final String CHROMOGRAM_RENDERER="Heat Stream";
	public static final String HEATMAP_RENDERER="Heatmap";
	public static final String PROFILE_RENDERER="Profile";
	
	public static final String[] RENDERERS=
	{
		DEFAULT_RENDERER,
		BOX_RENDERER,
		DIAMOND_RENDERER,
		CIRCLE_RENDERER,
		GRADIENT_RENDERER,
		CHROMOGRAM_RENDERER,
		HEATMAP_RENDERER,
		PROFILE_RENDERER
	};
	
	public static ComponentRenderer createRenderer(String renderer, SuperColorProvider coloring)
	{
		if(renderer.equals(DEFAULT_RENDERER))
			return new DefaultComponentRenderer();
		if(renderer.equals(BOX_RENDERER))
			return new BoxRenderer(coloring);
		if(renderer.equals(DIAMOND_RENDERER))
			return new DiamondRenderer(coloring);
		if(renderer.equals(CIRCLE_RENDERER))
			return new CircleRenderer(coloring);
		if(renderer.equals(GRADIENT_RENDERER))
			return new GradientGeneRenderer(coloring);
		if(renderer.equals(CHROMOGRAM_RENDERER))
			return new ChromogramRenderer(coloring);
		if(renderer.equals(HEATMAP_RENDERER))
			return new HeatMapRenderer(coloring);
		if(renderer.equals(PROFILE_RENDERER))
			return new ProfilePlotRenderer(coloring);
		return new DefaultComponentRenderer();
	}
	
	public static ComponentRenderer createRendererByClass(String rendererclass, SuperColorProvider coloring)
	{
		if(rendererclass.equals(DefaultComponentRenderer.class.getCanonicalName()))
			return new DefaultComponentRenderer();
		if(rendererclass.equals(BoxRenderer.class.getCanonicalName()))
			return new BoxRenderer(coloring);
		if(rendererclass.equals(DiamondRenderer.class.getCanonicalName()))
			return new DiamondRenderer(coloring);
		if(rendererclass.equals(CircleRenderer.class.getCanonicalName()))
			return new CircleRenderer(coloring);
		if(rendererclass.equals(GradientGeneRenderer.class.getCanonicalName()))
			return new GradientGeneRenderer(coloring);
		if(rendererclass.equals(ChromogramRenderer.class.getCanonicalName()))
			return new ChromogramRenderer(coloring);
		if(rendererclass.equals(HeatMapRenderer.class.getCanonicalName()))
			return new HeatMapRenderer(coloring);
		if(rendererclass.equals(ProfilePlotRenderer.class.getCanonicalName()))
			return new ProfilePlotRenderer(coloring);
		return new DefaultComponentRenderer();
	}
	
}
