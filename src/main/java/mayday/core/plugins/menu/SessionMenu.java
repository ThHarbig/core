package mayday.core.plugins.menu;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import mayday.core.gui.PluginMenu;
import mayday.core.gui.PreferencesDialog;
import mayday.core.pluginrunner.GenericPluginRunner;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.MenuPlugin;

public class SessionMenu extends AbstractPlugin implements MenuPlugin {

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.SessionMenu",
				new String[0],
				Constants.MC_MENUBAR,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Manages all session related plugins and builds the \"Mayday\" menu.",
				"Mayday Menu"
				);
		return pli;
	}
	
	public void init() {
	}

	@SuppressWarnings({ "unchecked", "serial" })
	public JMenu getMenu() {
		
		PluginMenu<?> theMenu = new PluginMenu("Mayday", Constants.MC_SESSION) {

			@Override
			public void callPlugin(PluginInfo pli, List selection) {
				runGenericPlugin(pli);
			}
			
		};
	    theMenu.setMnemonic( KeyEvent.VK_M );
		
	    JMenuItem temp = new JMenuItem(new ShowPreferencesDialogAction());
	    temp.setMnemonic( KeyEvent.VK_P );
	    theMenu.add(temp, true);
	    
	    temp = new JMenuItem(new PluginManagerAction());
	    theMenu.add(temp, true);
	    
	    theMenu.addSeparator();
	    
	    theMenu.fill();	   
        
		return theMenu;
	}

	public int getPreferredPosition() {
		return 1; 
	}
	
	
	@SuppressWarnings("serial")
	protected class ShowPreferencesDialogAction extends AbstractAction
	  {
	    public ShowPreferencesDialogAction() {
	      super( "Preferences ..." );
	    }

	    public void actionPerformed( ActionEvent event )  {
	      new PreferencesDialog().setVisible(true);	      
	    }
	  }
	  
	@SuppressWarnings("serial")
	protected class PluginManagerAction extends AbstractAction
	  {
	      public PluginManagerAction() {
	          super( "Plugins ..." );
	      }
	      	      
	      public void actionPerformed( ActionEvent event ) {
	    	  new mayday.core.pluma.gui.PluginManagerView();
	      }
	  }
	

	public static void runGenericPlugin(final PluginInfo pli)  {
		GenericPluginRunner gpl = new GenericPluginRunner(pli);
		gpl.execute();
	}
}
