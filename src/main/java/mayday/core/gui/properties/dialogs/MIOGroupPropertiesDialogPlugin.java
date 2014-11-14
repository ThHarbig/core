package mayday.core.gui.properties.dialogs;

import java.util.HashMap;

import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.items.InfoItem;
import mayday.core.gui.properties.items.MIOExtendableListItem;
import mayday.core.gui.properties.items.MIOListItem;
import mayday.core.gui.properties.items.MIOTableItem;
import mayday.core.gui.properties.items.NameItem;
import mayday.core.meta.MIGroup;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class MIOGroupPropertiesDialogPlugin extends AbstractPlugin {

	
	public void init() {}

	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.properties.miogroup",
				new String[0],
				Constants.MC_PROPERTYDIALOG,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Meta Information Group Properties Dialog",
				"MIO Group"
				);
		pli.getProperties().put(PropertiesDialogFactory.PropertyKey1, MIGroup.class);
		pli.getProperties().put(PropertiesDialogFactory.PropertyKey2, Dialog.class);
		return pli;
	}
	
	@SuppressWarnings("serial")
	public static class Dialog extends AbstractPropertiesDialog {

		private NameItem ni;
		private MIGroup mg;
		
		public Dialog() {
			super();
			setTitle("Meta Information Group Properties");
		}
		
		@Override
		public void assignObject(Object o) {
			mg = (MIGroup)o;
			ni = new NameItem(mg.getName());
			addDialogItem( ni );
			addDialogItem( new InfoItem( "Path", mg.getPath()) );
			addDialogItem( new InfoItem( "MIO Type", mg.getMIOType()) );			
			addDialogItem( new MIOExtendableListItem(mg) , 1.0);
			addDialogItem( new MIOListItem(mg) ,1.0 );
			addDialogItem( new MIOTableItem(mg,mg.getMIManager()), 1.0);
		}

		@Override
		protected void doOKAction() {
			String newName = (String)ni.getValue();
			if (newName.equals(mg.getName()))
				return;
			
			mg.setName(newName);
		}
		
	}
		

}
