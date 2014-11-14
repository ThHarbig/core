package mayday.core.io.gude;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.prototypes.CorePlugin;
import mayday.core.pluma.prototypes.MenuMakingPlugin;

public abstract class GUDEBase extends AbstractPlugin implements MenuMakingPlugin,
		CorePlugin {

//	protected String providedMasterComponent;
//	protected String objectType;
	
	protected abstract String providedMasterComponent();
	protected abstract String objectType();
	
	protected List<RunPluginAction> fsplugins = new LinkedList<RunPluginAction>();  
	protected JMenu otherplugins;

	
	public Vector<JMenuItem> createMenu() {
		
		Vector<JMenuItem> theMenu = new Vector<JMenuItem>();
		
		fsplugins.clear();
		otherplugins = new JMenu("Further export options...");
		
		Set<PluginInfo> plis = PluginManager.getInstance().getPluginsFor(providedMasterComponent());
		for (PluginInfo pli : plis) {
			/* find out what kind of plugin this is according to the constants defined in GUDIConstants */
			Integer type = (Integer)(pli.getProperties().get(GUDEConstants.EXPORTER_TYPE));
			if (type==null) {
				System.err.println("GUDE: "+pli.getIdentifier()+" has no valid EXPORTER_TYPE");
			} else {
				switch(type) {
				case GUDEConstants.EXPORTERTYPE_FILESYSTEM:
					fsplugins.add(createAction(pli));
					break;
				case GUDEConstants.EXPORTERTYPE_OTHER:
					otherplugins.add(createAction(pli));
					break;
				}
			}
			
		}
		// Build menu
		if (fsplugins.size()>0)
			theMenu.add(new JMenuItem(new ExportDataSetAction()));
		if (otherplugins.getItemCount()>0)
			theMenu.add(otherplugins);
		return theMenu;
	}
	
	protected abstract RunPluginAction createAction(PluginInfo pli);

	public void run() {
	}

	@SuppressWarnings("serial")
	public abstract class RunPluginAction extends AbstractAction {
		
		protected PluginInfo plugin;
		
		public RunPluginAction(PluginInfo pli) {
			super(pli.getMenuName());
			ImageIcon ico=null;
			try {
				 ico = pli.getIcon();
			} catch (Throwable something) {};
			
			if (ico!=null)
				this.putValue(AbstractAction.SMALL_ICON, ico);
			plugin = pli;			
		}
		
		public PluginInfo getPlugin() {
			return plugin;
		}

		public abstract void actionPerformed(GUDEEvent arg0);
		
		public void actionPerformed(ActionEvent arg0) {
			actionPerformed(new GUDEEvent(arg0.getSource(),""));			
		}

	}
	
	@SuppressWarnings("serial")
	public class ExportDataSetAction extends AbstractAction
    {
        public ExportDataSetAction() {
            super("Export to file...");
        }
            
        public void actionPerformed(ActionEvent arg0) {
        	new GUDEDialog(fsplugins, objectType());        	
        }       
    }
	
}
