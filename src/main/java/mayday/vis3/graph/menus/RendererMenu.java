package mayday.vis3.graph.menus;

import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.vis3.graph.renderer.RendererAcceptor;
import mayday.vis3.graph.renderer.primary.PrimaryComponentRenderer;
import mayday.vis3.graph.vis3.SuperColorProvider;

@SuppressWarnings("serial")
public class RendererMenu extends JMenu
{
	protected RendererAcceptor acceptor;
	private ButtonGroup rendererGroup;
	private SuperColorProvider coloring;
	
	public RendererMenu(SuperColorProvider coloring, RendererAcceptor acceptor)
	{
		super("Renderer");
		this.acceptor=acceptor;
		this.coloring=coloring;
		rendererGroup=new ButtonGroup();
		
		Set<PluginInfo> plis=PluginManager.getInstance().getPluginsFor(PrimaryComponentRenderer.MC);
		
		for(PluginInfo pli: plis)
		{
			RendererAction action=null;
			if(pli.getIcon()!=null)
			{
				action=new RendererAction(pli, pli.getName(),pli.getIcon());
			}else
			{
				action=new RendererAction(pli, pli.getName());
			}
			JRadioButtonMenuItem item=new JRadioButtonMenuItem(action);
			add(item);
			rendererGroup.add(item);
			
		}
	}
	
	private class RendererAction extends AbstractAction
	{
		private PluginInfo renderer;
		
		public RendererAction(PluginInfo renderer, String title, Icon icon)
		{
			super(title,icon);
			this.renderer=renderer;
		}
		
		public RendererAction(PluginInfo renderer, String title)
		{
			super(title);
			this.renderer=renderer;
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			PrimaryComponentRenderer rendererInstance=(PrimaryComponentRenderer)renderer.getInstance();
			rendererInstance.setColorProvider(coloring);
			acceptor.setRenderer(rendererInstance);			
		}		
	}	
	
	
	
}
