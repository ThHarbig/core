package mayday.core.gui.properties.dialogs;

import java.util.HashMap;

import mayday.core.Probe;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.items.MIOTableItem;
import mayday.core.gui.properties.items.NameItem;
import mayday.core.gui.properties.items.ProbeListListItem;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class ProbeProperties extends AbstractPlugin {

	
	public void init() {}

	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.properties.probe",
				new String[0],
				Constants.MC_PROPERTYDIALOG,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Probe Properties Dialog",
				"Probe"
				);
		pli.getProperties().put(PropertiesDialogFactory.PropertyKey1, Probe.class);
		pli.getProperties().put(PropertiesDialogFactory.PropertyKey2, Dialog.class);
		return pli;
	}
	
	@SuppressWarnings("serial")
	public static class Dialog extends AbstractPropertiesDialog {

		public Dialog() {
			super();
			setTitle("Probe Properties");
		}
		
		@Override
		public void assignObject(Object o) {
			Probe pb = (Probe)o;
			NameItem ni = new NameItem(pb.getName());
			ni.setEditable(false);
			addDialogItem( ni );
			addDialogItem( new ProbeListListItem(pb), 1.0);
			addDialogItem( new MIOTableItem(pb, 
					pb.getMasterTable().getDataSet().getMIManager()), 1.0);
		}

		@Override
		protected void doOKAction() {
			// nothing to do			
		}
		
	}

}
