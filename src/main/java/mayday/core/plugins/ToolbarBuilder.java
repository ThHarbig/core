package mayday.core.plugins;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JToolBar;

import mayday.core.Mayday;
import mayday.core.MaydayDefaults;
import mayday.core.gui.components.ToolbarOverflowLayout;
import mayday.core.pluginrunner.DataSetPluginRunner;
import mayday.core.pluginrunner.ProbeListPluginRunner;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.CorePlugin;
import mayday.core.pluma.prototypes.DatasetPlugin;
import mayday.core.pluma.prototypes.GenericPlugin;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.pluma.prototypes.SupportPlugin;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.PluginTypeListSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.vis3.tables.TablePlugin;

@SuppressWarnings("serial")
public class ToolbarBuilder extends AbstractPlugin implements CorePlugin, SettingChangeListener {

	protected final static String[] allowedMC = new String[]{
		Constants.MC_SESSION,
		Constants.MC_DATASET,
		Constants.MC_PROBELIST,
		TablePlugin.MC,
		MaydayDefaults.Plugins.CATEGORY_PLOT,
		Constants.MC_SUPPORT
	};
	
	protected static HierarchicalSetting toolbarSetting;
	protected static PluginTypeListSetting elements;
	protected static IntSetting iconsize;
	
	protected static JToolBar toolbar;
	
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.core.ButtonBarBuilder",
				new String[0],
				Constants.MC_CORE,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Builds Mayday's Quick Access button bar using a predefined list of plugins.",
				"Main Window Toolbar"
				);
		return pli;
	}
	
	public Setting getSetting() {
		if (toolbarSetting==null) {
			elements = new PluginTypeListSetting("Toolbar Plugins", null, null, allowedMC);
			iconsize = new IntSetting("Icon size", null, 16, 8, 128, true, true);
			toolbarSetting = new HierarchicalSetting("Toolbar")			
			.addSetting(iconsize)
			.addSetting(elements);

			toolbarSetting.addChangeListener(this);
		}
		return toolbarSetting;
	}
	
	public void run() {
    	
		if (toolbar!=null)
			return;
		
        boolean useTB=MaydayDefaults.Prefs.showMainToolbar.getBooleanValue();

        if (!useTB)
        	return;

        PluginInfo.loadDefaultSettings(getSetting(), "PAS.core.ButtonBarBuilder");
        
        
		toolbar = new JToolBar(JToolBar.HORIZONTAL);
		toolbar.setLayout(new ToolbarOverflowLayout());
		Mayday.sharedInstance.add(toolbar, BorderLayout.PAGE_START);

		fillToolbar();
	}
	
	protected void fillToolbar() {
		if (toolbar==null)
			return; 
			
		toolbar.removeAll();
		
		for (PluginInfo pli : elements.getPluginList()) {
			toolbar.add(new RunPluginAction(pli));
		}
		
	}

	protected class RunPluginAction extends AbstractAction {
		protected PluginInfo plugin;
		public RunPluginAction(PluginInfo pli) {
			super(
					pli.getName().length()>12?pli.getName().substring(0,9)+"...":pli.getName()
			);
			ImageIcon ico = pli.getIcon();
			if (ico!=null) {
				ImageIcon sico = new ImageIcon(ico.getImage().getScaledInstance(iconsize.getIntValue(), iconsize.getIntValue(), Image.SCALE_SMOOTH));		
				this.putValue(AbstractAction.SMALL_ICON, sico);
				this.putValue(AbstractAction.SHORT_DESCRIPTION, pli.getName());
			}
			plugin = pli;		
		}
		public void actionPerformed(ActionEvent evt) {
			new Thread("RunPluginFromButtonBar") {
				public void run() {
					AbstractPlugin apl = plugin.getInstance();
					if (apl instanceof GenericPlugin) {
						((GenericPlugin)apl).run();
					} else if (apl instanceof SupportPlugin) {
						((SupportPlugin)apl).run(null);
					} else if (apl instanceof ProbelistPlugin) {
						new ProbeListPluginRunner(plugin).execute();
					} else if (apl instanceof DatasetPlugin) {
						new DataSetPluginRunner(plugin).execute();
					}
				}
			}.start();
		
		}
	}
	
	
	public void init() {
	}

	public void stateChanged(SettingChangeEvent e) {
		fillToolbar();
	}
}
