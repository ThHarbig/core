package mayday.core.gui.properties.dialogs;

import java.util.HashMap;

import javax.swing.JOptionPane;

import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.items.EnclosingGroupItem;
import mayday.core.gui.properties.items.MIORendererItem;
import mayday.core.gui.properties.items.MIOTableItem;
import mayday.core.meta.WrappedMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class MIOProperties extends AbstractPlugin {

	
	public void init() {}

	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.properties.mio",
				new String[0],
				Constants.MC_PROPERTYDIALOG,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Meta Information Object Properties Dialog",
				"MIO"
				);
		pli.getProperties().put(PropertiesDialogFactory.PropertyKey1, WrappedMIO.class);
		pli.getProperties().put(PropertiesDialogFactory.PropertyKey2, Dialog.class);
		return pli;
	}
	
	@SuppressWarnings("serial")
	public static class Dialog extends AbstractPropertiesDialog {

		protected MIORendererItem mri;
		
		public Dialog() {
			super();
			setTitle("Meta Information Object Properties");
		}
		
		@Override
		public void assignObject(Object o) {
			WrappedMIO wm = (WrappedMIO)o;
			mri = new MIORendererItem(wm);
			addDialogItem( mri );
			if (wm.getGroup()!=null)
				addDialogItem( new EnclosingGroupItem(wm));
			
			addDialogItem( new MIOTableItem(wm.getMio(), wm.getGroup().getMIManager() ), 1.0);
		}

		@Override
		protected void doOKAction() {
			if (mri.isEditing()) {
				if (JOptionPane.showConfirmDialog(null, 
						"Changes to the Meta Information have not been applied.\n" +
						"Should they be applied now?\n" +
						"The alternative is to discard all changes.",
		    			"Apply changes?", 
		    			JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
		    			==JOptionPane.YES_OPTION) {
					mri.applyChanges();
				}
			}
			// nothing to do			
		}
		
	}

}
