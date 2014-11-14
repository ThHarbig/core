package mayday.core.pluginrunner;

import java.util.LinkedList;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import mayday.core.MaydayDefaults;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.plugins.MetaInfoPlugin;
import mayday.core.pluma.PluginInfo;

public class MetaInfoPluginRunner {

	protected MIGroupSelection<MIType> miGroups;
	protected PluginInfo pli;
	protected MIManager mimanager;

	public MetaInfoPluginRunner(PluginInfo pli, MIGroupSelection<MIType> miGroups, MIManager mimanager) {
		this.miGroups=miGroups;
		this.pli = pli;
		this.mimanager=mimanager;
	}

	public MetaInfoPluginRunner(PluginInfo pli) {
		this.pli=pli;			
	}

	public void execute() {
		prepare();
		if (miGroups==null || mimanager==null) 
			inferInput();
		if (miGroups==null || mimanager==null)
			return;

		Thread RunPluginThread = new Thread("PluginRunner")	{
			public void run() {	    			
				try {
					runPlugin();
				} catch ( final Exception exception ) {
					exception.printStackTrace();	  
					SwingUtilities.invokeLater(new Runnable(){
						public void run() {
							JOptionPane.showMessageDialog( null,
									exception.getMessage(),
									MaydayDefaults.Messages.ERROR_TITLE,
									JOptionPane.ERROR_MESSAGE );

						}
					});
				}
			}
		};

		RunPluginThread.start();
	}


	protected void prepare() {	    	
//		System.runFinalization();
//		System.gc();
	}

	protected void inferInput() {
		return; }

	@SuppressWarnings("unchecked")
	protected void runPlugin() {    	
		MIGroupSelection<MIType> mgs = miGroups;
		MetaInfoPlugin mip = (MetaInfoPlugin)pli.getInstance();

		// first find out if the plugin is applicable
		LinkedList<Class<? extends MIType>> acceptableClasses = 
			(LinkedList<Class<? extends MIType>>)pli.getProperties().get(MetaInfoPlugin.ACCEPTABLE_CLASSES);
		String discardedGroups = "";
		if (acceptableClasses!=null) {
			for (MIGroup mg : mgs) {
				boolean anyMatch = false; 
				for (Class<? extends MIType> c : acceptableClasses)
					anyMatch |= c.isAssignableFrom(mg.getMIOClass());
				if (!anyMatch)
					discardedGroups+=mg.getName()+"\n";
			}				
		} // no definition => all classes accepted

		if (discardedGroups.length()>0) {
			JOptionPane.showMessageDialog(null, "The following MIGroups are of the wrong type for this plugin:\n"+discardedGroups, 
					"Plugin not applicable", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// ok, types are correct. Now check if more than one list was selected, and how to handle that case
		String multiseltype = (String)pli.getProperties().get(MetaInfoPlugin.MULTISELECT_HANDLING);

		if (multiseltype==null)
			multiseltype = MetaInfoPlugin.MULTISELECT_HANDLE_DEFAULT;

		if (mgs.size()<2) 
			multiseltype = MetaInfoPlugin.MULTISELECT_HANDLE_INTERNAL;

		if (multiseltype.equals(MetaInfoPlugin.MULTISELECT_HANDLE_ASK_USER)) {
			multiseltype=(String)JOptionPane.showInputDialog(null,
					"You have selected several MIO groups. \nHow do you want to apply the selected plugin?",
					"Multiple MIO groups selected",
					JOptionPane.QUESTION_MESSAGE, null,
					new String[]{MetaInfoPlugin.MULTISELECT_HANDLE_BY_REPEAT,MetaInfoPlugin.MULTISELECT_HANDLE_INTERNAL}, MetaInfoPlugin.MULTISELECT_HANDLE_INTERNAL);
		}

		if (multiseltype.equals(MetaInfoPlugin.MULTISELECT_HANDLE_INTERNAL)) {
			mip.run(mgs, mimanager);
		} else
			if (multiseltype.equals(MetaInfoPlugin.MULTISELECT_HANDLE_BY_REPEAT)) {
				for (MIGroup mg : mgs) {
					MIGroupSelection<MIType> mgss = new MIGroupSelection<MIType>();
					mgss.add(mg);
					mip.run(mgss, mimanager);
				}
			}

	}


}