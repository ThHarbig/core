package mayday.core.gui.properties.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import mayday.core.DataSet;
import mayday.core.DataSetEvent;
import mayday.core.DataSetListener;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.datasetmanager.gui.DataSetManagerView;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.items.AbstractPropertiesItem;
import mayday.core.gui.properties.items.ExperimentsItem;
import mayday.core.gui.properties.items.MIOGroupListItem;
import mayday.core.gui.properties.items.MIOTableItem;
import mayday.core.gui.properties.items.NameItem;
import mayday.core.gui.properties.items.ProbeListItem;
import mayday.core.gui.properties.items.ProbeListListItem;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class DataSetProperties extends AbstractPlugin {

	
	public void init() {}

	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.properties.dataset",
				new String[0],
				Constants.MC_PROPERTYDIALOG,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Data Set Properties Dialog",
				"DataSet"
				);
		pli.getProperties().put(PropertiesDialogFactory.PropertyKey1, DataSet.class);
		pli.getProperties().put(PropertiesDialogFactory.PropertyKey2, Dialog.class);
		return pli;
	}
	
	@SuppressWarnings("serial")
	public static class Dialog extends AbstractPropertiesDialog {

		public Dialog() {
			super();
			setTitle("Dataset Properties");
		}
		
		private NameItem ni;
		private DataSet ds;
		
		@Override
		public void assignObject(Object o) {
			// add tabs
			ds = (DataSet)o;
			ni=new NameItem(ds.getName());
			
			final DataSetListener closingListener = new DataSetListener() {
				public void dataSetChanged(DataSetEvent event) {
					if (event.getChange()==DataSetEvent.CLOSING_CHANGE)
						dispose();
					if (event.getChange()==DataSetEvent.CAPTION_CHANGE)
						ni.setValue(ds.getName());
				}
			};
			ds.addDataSetListener(closingListener);
			this.addWindowListener(new WindowListener() {
				public void windowActivated(WindowEvent arg0) {}
				public void windowClosed(WindowEvent arg0) {
					ds.removeDataSetListener(closingListener);
				}
				public void windowClosing(WindowEvent arg0) {}
				public void windowDeactivated(WindowEvent arg0) {}
				public void windowDeiconified(WindowEvent arg0) {}
				public void windowIconified(WindowEvent arg0) {}
				public void windowOpened(WindowEvent arg0) {}				
			});
			
			JTabbedPanePropertiesItem pane = new JTabbedPanePropertiesItem("");
			addDialogItem(pane, 1.0);
			pane.setBorder(BorderFactory.createEmptyBorder());

			JPanel panel = new JPanel(new GridBagLayout());
			pane.add("Overview", panel);
			
			addDialogItem( panel, ni );
			addDialogItem( panel, new ExperimentsItem(ds.getMasterTable()) , 1.0);
			addDialogItem( panel, new ProbeListListItem(ds), 1.0);
			addDialogItem( panel, new ProbeListItem(ds.getMasterTable(),null), 1.0);
			addDialogItem( panel, new MIOGroupListItem(ds.getMIManager()), 1.0);
			addDialogItem( panel, new MIOTableItem(ds,ds.getMIManager()), 1.0);
			
			panel = new JPanel(new GridBagLayout());
			pane.add("Experiments", panel);
			addDialogItem( panel, new ExperimentsItem(ds.getMasterTable()) , 1.0);
			
			panel = new JPanel(new GridBagLayout());
			pane.add("ProbeLists", panel);
			addDialogItem( panel, new ProbeListListItem(ds) , 1.0);

			panel = new JPanel(new GridBagLayout());
			pane.add("Probes", panel);
			addDialogItem( panel, new ProbeListItem(ds.getMasterTable(),null) , 1.0);

			panel = new JPanel(new GridBagLayout());
			pane.add("Meta Information Groups", panel);
			addDialogItem( panel, new MIOGroupListItem(ds.getMIManager()) , 1.0);
			
			panel = new JPanel(new GridBagLayout());
			pane.add("Meta Information Objects", panel);
			addDialogItem( panel, new MIOTableItem(ds,ds.getMIManager()) , 1.0);

		}

		@Override
		protected void doOKAction() {			
			// Change DataSet name
			String newName = (String)ni.getValue();
			if (newName.equals(ds.getName()))
				return;
			
			while (DataSetManager.singleInstance.contains(newName)) {
				newName = JOptionPane.showInputDialog(null, "Enter a unique DataSet name: ",newName); 
			}
				
			if (newName != null) {
				ds.setName(newName);
				DataSetManagerView.getInstance().updateInfo(ds);
			}
		}
		
		
		protected void addDialogItem(Container c, AbstractPropertiesItem ali) {
			c.add(ali,gbc);
			gbc.gridy++;
		}
		
		protected void addDialogItem(Container c, AbstractPropertiesItem ali, double weighty) {
			gbc.weighty=weighty;
			gbc.fill=GridBagConstraints.BOTH;
			addDialogItem(c,ali);
			gbc.fill=GridBagConstraints.HORIZONTAL;
			gbc.weighty=0.0;
			ali.setParentDialog(this);
		}
		
	}
	
	@SuppressWarnings("serial")
	public static class JTabbedPanePropertiesItem extends AbstractPropertiesItem {

		public JTabbedPanePropertiesItem(String caption) {
			super(caption);
			add(tp, BorderLayout.CENTER);
		}

		JTabbedPane tp = new JTabbedPane();
		
		public Component add(String title, Component c) {
			return tp.add(title, c);
		}
		
		@Override
		public Object getValue() {
			return null;
		}

		@Override
		public boolean hasChanged() {
			return false;
		}

		@Override
		public void setValue(Object value) {
		}
		
	}


}
