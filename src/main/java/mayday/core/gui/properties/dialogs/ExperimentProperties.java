package mayday.core.gui.properties.dialogs;

import java.util.HashMap;

import mayday.core.Experiment;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.items.MIOTableItem;
import mayday.core.gui.properties.items.NameItem;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class ExperimentProperties extends AbstractPlugin {

	
	public void init() {}

	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.properties.experiment",
				new String[0],
				Constants.MC_PROPERTYDIALOG,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Experiment Properties Dialog",
				"Experiment"
				);
		pli.getProperties().put(PropertiesDialogFactory.PropertyKey1, Experiment.class);
		pli.getProperties().put(PropertiesDialogFactory.PropertyKey2, Dialog.class);
		return pli;
	}
	
	@SuppressWarnings("serial")
	public static class Dialog extends AbstractPropertiesDialog {

		public Dialog() {
			super();
			setTitle("Experiment Properties");
		}
		
		private Experiment e;
		private NameItem ni;
		
		@Override
		public void assignObject(Object o) {
			e = (Experiment)o;
			ni = new NameItem(e.getName());
			addDialogItem( ni );
			addDialogItem( new MIOTableItem(e,e.getMasterTable().getDataSet().getMIManager()), 1.0);
		}

		@Override
		protected void doOKAction() {
			// Change DataSet name
			String newName = (String)ni.getValue();
			if (newName.equals(e.getName()))
				return;
			
//			while (e.getMasterTable().getExperimentNames().contains(newName)) {
//				newName = JOptionPane.showInputDialog(null, "Enter a unique Experiment name: ",newName); 
//			}
				
			if (newName != null) {
				e.setName(newName);				
			}			
		}
		
	}

}
