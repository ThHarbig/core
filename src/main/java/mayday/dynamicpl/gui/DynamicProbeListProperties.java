package mayday.dynamicpl.gui;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;

import javax.swing.JOptionPane;

import mayday.core.DataSetEvent;
import mayday.core.DataSetListener;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;
import mayday.core.gui.properties.items.ColorItem;
import mayday.core.gui.properties.items.MIOTableItem;
import mayday.core.gui.properties.items.NameItem;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.DynamicProbeList;

public class DynamicProbeListProperties extends AbstractPlugin {

	
	public void init() {}

	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.properties.dynamicprobelist",
				new String[0],
				Constants.MC_PROPERTYDIALOG,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Dynamic Probe List Properties Dialog",
				"Dynamic ProbeList"
				);
		pli.getProperties().put(PropertiesDialogFactory.PropertyKey1, DynamicProbeList.class);
		pli.getProperties().put(PropertiesDialogFactory.PropertyKey2, Dialog.class);
		return pli;
	}
	
	@SuppressWarnings("serial")
	public static class Dialog extends AbstractPropertiesDialog {

		public Dialog() {
			super();
			setTitle("Dynamic Probelist Properties");
		}
		
		private DynamicProbeList pl;
		private NameItem ni;
		private ColorItem ci;
		private RuleSetEditorItem rep;
		
		@Override
		public void assignObject(Object o) {
			pl = (DynamicProbeList)o;
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
			
			rep = new RuleSetEditorItem(pl);
			
			addDialogItem( ni );
			addDialogItem( ci );
			//addDialogItem( new ProbeListItem(pl.getDataSet().getMasterTable(),pl), 1.0);
			addDialogItem(rep, .5);
//			addDialogItem(new RuleSetEditorOpenItem(pl));
			addDialogItem( new MIOTableItem(pl,pl.getDataSet().getMIManager()), .5);
		}

		@Override
		protected void doOKAction() {
			String newName = (String)ni.getValue();
			Color newColor = (Color)ci.getValue();
			
			pl.setColor(newColor);
			
			// apply rule set changes
			rep.apply();
			
			// Change DataSet name
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
