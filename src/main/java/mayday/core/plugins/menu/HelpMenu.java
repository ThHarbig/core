package mayday.core.plugins.menu;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import mayday.core.Mayday;
import mayday.core.gui.AboutDialog;
import mayday.core.gui.PluginMenu;
import mayday.core.pluginrunner.GenericPluginRunner;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.MenuPlugin;

public class HelpMenu extends AbstractPlugin implements MenuPlugin {

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.HelpMenu",
				new String[0],
				Constants.MC_MENUBAR,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Builds the \"Help\" menu.",
				"Help Menu"
				);
		return pli;
	}
	
	public void init() {
	}

	@SuppressWarnings({ "unchecked", "serial" })
	public JMenu getMenu() {
	
		PluginMenu<?> theMenu = new PluginMenu("Help", Constants.MC_HELP) {

			@Override
			public void callPlugin(PluginInfo pli, List selection) {
				runGenericPlugin(pli);				
			}
			
		};
	    theMenu.setMnemonic( KeyEvent.VK_H );		
	    
	    JMenuItem l_aboutItem = new JMenuItem( new ShowAboutDialogAction() );
	    l_aboutItem.setMnemonic( KeyEvent.VK_A );
	    theMenu.add( l_aboutItem , true);
	    theMenu.addSeparator();
	    // plugins here
	    
	    theMenu.fill();
	   
       	theMenu.addSeparator();
        
	    //MZ 2005-06-30
	    JMenuItem l_messageWindowItem = new JMenuItem( new AbstractAction("Show Message Window") {
	    	public void actionPerformed(ActionEvent e) {
	    		Mayday.getMessageWindow().setVisible(true);				
			}
	    });

	    theMenu.add( l_messageWindowItem, true);
        
		return theMenu;
	}

	public int getPreferredPosition() {
		return 5; 
	}
	
	@SuppressWarnings("serial")
	protected class ShowAboutDialogAction extends AbstractAction {
	    public ShowAboutDialogAction() {
	      super( "About ..." );
	    }

	    public void actionPerformed( ActionEvent event )
	    {
	    	new AboutDialog().setVisible(true);
	    }
	  }


	public static void runGenericPlugin(final PluginInfo pli)  {
		GenericPluginRunner gpl = new GenericPluginRunner(pli);
		gpl.execute();
	}
		
}
