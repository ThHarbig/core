package mayday.core.gui.properties.dialogs;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;

import javax.swing.JOptionPane;

import mayday.core.DataSetEvent;
import mayday.core.DataSetListener;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.items.ColorItem;
import mayday.core.gui.properties.items.MIOTableItem;
import mayday.core.gui.properties.items.NameItem;
import mayday.core.gui.properties.items.ProbeListItem;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class ProbeListProperties extends AbstractPlugin {

	
	public void init() {}

	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.properties.probelist",
				new String[0],
				Constants.MC_PROPERTYDIALOG,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Probe List Properties Dialog",
				"ProbeList"
				);
		pli.getProperties().put(PropertiesDialogFactory.PropertyKey1, ProbeList.class);
		pli.getProperties().put(PropertiesDialogFactory.PropertyKey2, Dialog.class);
		return pli;
	}
	
	@SuppressWarnings("serial")
	public static class Dialog extends AbstractPropertiesDialog {

		public Dialog() {
			super();
			setTitle("Probelist Properties");
		}
		
		private ProbeList pl;
		private NameItem ni;
		private ColorItem ci;
		
		@Override
		public void assignObject(Object o) {
			pl = (ProbeList)o;
			ni = new NameItem(pl.getName());
			
			final ProbeListListener closingPLListener = new ProbeListListener() {
				public void probeListChanged(ProbeListEvent event) {
					if (event.getChange()==ProbeListEvent.PROBELIST_CLOSED)
						dispose();
					if (event.getChange()==ProbeListEvent.LAYOUT_CHANGE)
						ni.setValue(pl.getName());
				}
			};
			final DataSetListener closingDSListener = new DataSetListener() {
				public void dataSetChanged(DataSetEvent event) {
					if (event.getChange()==DataSetEvent.CLOSING_CHANGE)
						dispose();
				}
			};
			pl.addProbeListListener(closingPLListener);
			pl.getDataSet().addDataSetListener(closingDSListener);
			this.addWindowListener(new WindowListener() {
				public void windowActivated(WindowEvent arg0) {}
				public void windowClosed(WindowEvent arg0) {
					pl.removeProbeListListener(closingPLListener);
					pl.getDataSet().removeDataSetListener(closingDSListener);
				}
				public void windowClosing(WindowEvent arg0) {}
				public void windowDeactivated(WindowEvent arg0) {}
				public void windowDeiconified(WindowEvent arg0) {}
				public void windowIconified(WindowEvent arg0) {}
				public void windowOpened(WindowEvent arg0) {}				
			});
			
			ci = new ColorItem(pl.getColor());
			addDialogItem( ni );
			addDialogItem( ci );
			addDialogItem( new ProbeListItem(pl.getDataSet().getMasterTable(),pl), 1.0);
			addDialogItem( new MIOTableItem(pl,pl.getDataSet().getMIManager()), 1.0);
		}

		@Override
		protected void doOKAction() {
			String newName = (String)ni.getValue();
			Color newColor = (Color)ci.getValue();
			
			pl.setColor(newColor);
			
			if (newName.equals(pl.getName()))
				return;
			
			while (pl.getDataSet().getProbeListManager().contains(newName)) {
				newName = JOptionPane.showInputDialog(null, "Enter a unique ProbeList name: ",newName); 
			}
				
			if (newName != null) {
				pl.setName(newName);
				pl.getDataSet().getProbeListManager().getProbeListManagerView().getComponent().repaint();
			}			
		}
		
	}

}
