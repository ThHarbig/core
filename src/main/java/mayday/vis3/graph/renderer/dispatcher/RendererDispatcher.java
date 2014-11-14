package mayday.vis3.graph.renderer.dispatcher;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamWriter;

import mayday.core.DataSet;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.Nodes;
import mayday.core.structures.maps.DefaultValueMap;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.NodeComponent;
import mayday.vis3.graph.model.ProbeGraphModel;
import mayday.vis3.graph.renderer.ComponentRenderer;
import mayday.vis3.graph.renderer.RendererDecorator;
import mayday.vis3.graph.renderer.primary.MinimalRenderer;
import mayday.vis3.graph.vis3.SuperColorProvider;

public class RendererDispatcher implements SettingChangeListener
{
	protected Map<CanvasComponent, RendererPluginSetting> individualRenderers=new HashMap<CanvasComponent, RendererPluginSetting>();
	protected DefaultValueMap<String, AssignedRendererSetting> roleRenderers;
	protected AssignedRendererSetting defaultRenderer;
	protected DecoratorListSetting overallDecorators;
	
	protected BooleanSetting renderSmallSettings=new BooleanSetting("Simplyfy small nodes", "Draw nodes smaller than 20x20 pixels as boxes\\" +
			"without decoration and label.", true);
	//	private Map<CanvasComponent, BufferedImage> cachedComponents=new HashMap<CanvasComponent, BufferedImage>();	
	//	private RendererDispatcherSetting setting=new RendererDispatcherSetting();

	protected AssignedRendererListSetting roleRenderersSetting;

	public RendererDispatcher(DataSet ds, SuperColorProvider coloring) 
	{
		defaultRenderer=new AssignedRendererSetting("Default Renderer",ds, coloring);
		defaultRenderer.setPrimaryRenderer("PAS.GraphViewer.Renderer.Box");
		inititalizeRenderers(ds, coloring);
		overallDecorators=new DecoratorListSetting("Additional Information",ds);	
		
	}
	
	public void clearRoles()
	{
		roleRenderersSetting.clear();	
		
	}
	
	public void clearIndividualRenderers()
	{
		individualRenderers.clear();
	}

	protected void inititalizeRenderers(DataSet ds, SuperColorProvider coloring)
	{
		Map<String, AssignedRendererSetting> baseMap=new HashMap<String, AssignedRendererSetting>();
		roleRenderers=new DefaultValueMap<String, AssignedRendererSetting>(baseMap, defaultRenderer);

		AssignedRendererSetting probeRenderer=new AssignedRendererSetting(Nodes.Roles.PROBE_ROLE,ds, coloring);
		probeRenderer.setPrimaryRenderer("PAS.GraphViewer.Renderer.Chromogram");
		roleRenderers.put(Nodes.Roles.PROBE_ROLE, probeRenderer);

		AssignedRendererSetting probesRenderer=new AssignedRendererSetting(Nodes.Roles.PROBES_ROLE,ds, coloring);
		probesRenderer.setPrimaryRenderer("PAS.GraphViewer.Renderer.Heatmap");
		roleRenderers.put(Nodes.Roles.PROBES_ROLE, probesRenderer);

		AssignedRendererSetting profileRenderer=new AssignedRendererSetting(Nodes.Roles.PROBELIST_ROLE, ds, coloring);
		profileRenderer.setPrimaryRenderer("PAS.GraphViewer.Renderer.Profile");
		roleRenderers.put(Nodes.Roles.PROBELIST_ROLE, profileRenderer);

		AssignedRendererSetting noteRenderer=new AssignedRendererSetting(Nodes.Roles.NOTE_ROLE,ds, coloring);
		noteRenderer.setPrimaryRenderer("PAS.GraphViewer.Renderer.Note");
		roleRenderers.put(Nodes.Roles.NOTE_ROLE, noteRenderer);

		AssignedRendererSetting nodeRenderer=new AssignedRendererSetting(Nodes.Roles.NODE_ROLE,ds, coloring);
		nodeRenderer.setPrimaryRenderer("PAS.GraphViewer.Renderer.Default");	
		roleRenderers.put(Nodes.Roles.NODE_ROLE, nodeRenderer);

		AssignedRendererSetting mioRenderer=new AssignedRendererSetting(Nodes.Roles.MIO_ROLE,ds, coloring);
		mioRenderer.setPrimaryRenderer("PAS.GraphViewer.Renderer.MIO");	
		roleRenderers.put(Nodes.Roles.MIO_ROLE, mioRenderer);
		
		roleRenderersSetting=new AssignedRendererListSetting(ds, coloring, baseMap.values());
		roleRenderersSetting.addChangeListener(this);
	}

	public void addRoleRenderer(String role, AssignedRendererSetting renderer)
	{
		roleRenderersSetting.addRenderer(renderer);		
	}

	public void addIndividualRenderer(CanvasComponent comp, RendererPluginSetting renderer)
	{
		individualRenderers.put(comp, renderer); 
		clearCache(comp);
		comp.resetRendererMenu();		
	}
	
	public void clearIndividualRenderer(CanvasComponent comp)
	{
		individualRenderers.remove(comp); 
		clearCache(comp);
		comp.resetRendererMenu();		
	}

	public Map<CanvasComponent, RendererPluginSetting> getIndividualRenderers()
	{
		return individualRenderers; 
	}

	public void render(Graphics2D g, Node node, Object value, boolean paintLabel,CanvasComponent comp)
	{
		//0: check if component is small:
		if(renderSmallSettings.getBooleanValue() && (comp.getWidth() <20 || comp.getHeight() < 20) )
		{				
			MinimalRenderer.sharedInstance.draw(g, node, new Rectangle(comp.getSize()), value, "", comp.isSelected());	
			return;			
		}
		
		//1. check if component is cached
		if(renderCachedComponent(g, node, value, comp))
			return;
		//2. not cached: 
		if(individualRenderers.containsKey(comp))
		{
			renderComponentWithRenderer(g, node, value, comp, individualRenderers.get(comp),paintLabel);
			return;
		}
		//3. not an individualist component
		if(roleRenderers.containsKey(node.getRole()))
		{
			renderComponentWithRenderer(g, node, value, comp, roleRenderers.get(node.getRole()),paintLabel);
			return;
		}
		renderComponentWithRenderer(g, node, value, comp, defaultRenderer,paintLabel);
		//		//4. recover: 
		//		renderComponentWithRenderer(g, node, value, comp, defaultRenderer);
	}

//	private void renderComponentWithRenderer(Graphics2D g, Node node, Object value, CanvasComponent comp,RendererPluginSetting rendererPluginSetting)
//	{
//		ComponentRenderer renderer=rendererPluginSetting.getRenderer();
//		for(RendererDecorator dec: overallDecorators.getSelection())
//		{
//			dec.setRenderer(renderer);
//			renderer=dec;
//		}	
//		renderer.draw(g, node, new Rectangle(comp.getSize()), value, comp.getLabel(), comp.isSelected());	
////		rendererPluginSetting.getRenderer().draw(g, node, new Rectangle(comp.getSize()), value, comp.getLabel(), comp.isSelected());		
//	}
	
	protected void renderComponentWithRenderer(Graphics2D g, Node node, Object value, CanvasComponent comp, RendererPluginSetting rendererPluginSetting, boolean paintLabel)
	{
		ComponentRenderer renderer=rendererPluginSetting.getRenderer();
		for(RendererDecorator dec: overallDecorators.getSelection())
		{
			dec.setRenderer(renderer);
			renderer=dec;
		}	
		renderer.draw(g, node, new Rectangle(comp.getSize()), value, paintLabel?comp.getLabel():"", comp.isSelected());	
//		rendererPluginSetting.getRenderer().draw(g, node, new Rectangle(comp.getSize()), value, comp.getLabel(), comp.isSelected());		
	}

	protected boolean renderCachedComponent(Graphics2D g, Node node, Object value, CanvasComponent comp)
	{
		return false;
	}

	@Override
	public void stateChanged(SettingChangeEvent e) 
	{
		roleRenderers.clear();
		for(AssignedRendererSetting s:roleRenderersSetting.getSelection())
		{
			roleRenderers.put(s.getTarget().getStringValue(), s);
		}		
	}

	public AssignedRendererListSetting getRoleRenderersSetting() {
		return roleRenderersSetting;
	}

	public ComponentRenderer getRenderer(CanvasComponent comp, Node node) 
	{	//0: check if component is small:
		if(renderSmallSettings.getBooleanValue() && (comp.getWidth() <10 || comp.getHeight() < 10) )
		{				
			return MinimalRenderer.sharedInstance;				
		}		
		return getRendererSetting(comp, node).getRenderer();
	}
	
	public RendererPluginSetting getRendererSetting(CanvasComponent comp, Node node) 
	{
		if(individualRenderers.containsKey(comp))
		{
			return individualRenderers.get(comp);
		}	
		if(roleRenderers.containsKey(node.getRole()))
		{
			return roleRenderers.get(node.getRole());			
		}
		return defaultRenderer;
	}

	public AssignedRendererSetting getDefaultRenderer() {
		return defaultRenderer;
	}
	
	public void setDefaultRenderer(AssignedRendererSetting defaultRenderer) {
		this.defaultRenderer = defaultRenderer;
		
	}
	
	public void updateDefaultRenderer(AssignedRendererSetting newDefaultRenderer) 
	{
		defaultRenderer.setTarget(newDefaultRenderer.getTarget());
		defaultRenderer.setPrimaryRenderer(newDefaultRenderer.getPrimaryRenderer().getStringValue());
		defaultRenderer.getDecorators().clear();
		for(RendererDecorator r: newDefaultRenderer.getDecorators().getSelection())
		{
			defaultRenderer.getDecorators().add(r);
		}
		
	}

	public DecoratorListSetting getOverallDecorators() {
		return overallDecorators;
	}

	public DefaultValueMap<String, AssignedRendererSetting> getRoleRenderers() {
		return roleRenderers;
	}

	public static void copyIndividualRenderers(RendererDispatcher source, RendererDispatcher target, 
			ProbeGraphModel sourceModel, ProbeGraphModel targetModel)
	{		
		for(CanvasComponent cc: source.getIndividualRenderers().keySet())
		{
			if(targetModel.getComponent(sourceModel.getNode(cc))==null)
				continue;
			target.addIndividualRenderer(targetModel.getComponent(sourceModel.getNode(cc)), source.getIndividualRenderers().get(cc));
		}		
	}
	
	public static final String RENDERER_DISPATCHER="rendererDispatcher";
	public static final String DEFAULT_RENDERER="defaultRenderer";
	public static final String ROLE_RENDERER="roleRenderers";
	public static final String INDIVIDUAL_RENDERER_LIST="individualRendererList";
	public static final String INDIVIDUAL_RENDERER="individualRenderer";
	public static final String TARGET_COMPONENT="targetComponent";
	public static final String OVERALL_DECORATORS="overallDecorators";
	public static final String SETTINGS="settings";
	
	public void exportXML(XMLStreamWriter writer)throws Exception
	{
		writer.writeStartElement(RENDERER_DISPATCHER);		
		// write default renderer
		writer.writeStartElement(DEFAULT_RENDERER);
		getDefaultRenderer().exportXML(writer);
		writer.writeEndElement();
		
		// write role renderers;		
		writer.writeStartElement(ROLE_RENDERER);
		for(AssignedRendererSetting ars: getRoleRenderers().values())
		{
			ars.exportXML(writer);
		}
		writer.writeEndElement();		
		// individual renderers
		writer.writeStartElement(INDIVIDUAL_RENDERER_LIST);
		for(CanvasComponent cc: getIndividualRenderers().keySet())				
		{
			if(cc instanceof NodeComponent)
			{
				writer.writeStartElement(INDIVIDUAL_RENDERER);
				writer.writeAttribute(TARGET_COMPONENT, ((NodeComponent) cc).getNode().getXMLExportId());				
				getIndividualRenderers().get(cc).exportXML(writer);
				writer.writeEndElement();					
			}			
		}
		writer.writeEndElement();
		// overall decorators
		if(!overallDecorators.getSelection().isEmpty())
		{
			writer.writeStartElement(OVERALL_DECORATORS);
			overallDecorators.exportXML(writer);			
			writer.writeEndElement();
			
		}
		
		
	}
	
	public BooleanSetting getRenderSmallSettings() {
		return renderSmallSettings;
	}
	
	public void clearCache(){
		// for implementation in subclasses
	}
	
	public void clearCache(CanvasComponent comp) {
		// for implementation in subclasses
	}
	

}
