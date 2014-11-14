package mayday.core.gui.classes;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import mayday.core.ClassSelectionModel;
import mayday.core.DataSet;
import mayday.core.Experiment;
import mayday.core.gui.components.ExcellentBoxLayout;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.meta.types.StringMIO;

@SuppressWarnings("serial")
public class PanelLoadStoreClasses extends JPanel {

	protected JComboBox storedSettings;
	protected JButton addCurrent;
	protected JButton removeSelected;
	protected ClassSelectionPanel panel;
	protected DataSet ds;
	
	protected final static String MIPATH = "/Experiment Classes";

	protected MIGroupSelection<MIType> getStoredLabellings() {
		return ds.getMIManager().getGroupsForPath(MIPATH, true).filterByType("PAS.MIO.String");
	}
	
	protected void removeStoredSetting(MIGroup s) {
		ds.getMIManager().removeGroup(s);
	}
	
	protected Experiment getExperimentFromName(String name) {
		int index = ds.getMasterTable().getExperimentNames().indexOf(name);
		if (index>=0) 
			return ds.getMasterTable().getExperiment(index);
		return null;
	}
	
	protected void storeCurrentAs(String name) {
		MIGroup mg = ds.getMIManager().newGroup("PAS.MIO.String", name, MIPATH);
		ClassSelectionModel m = panel.getClassPartition();
		for (int i=0; i!=m.getNumObjects(); ++i) {			
			Experiment e = getExperimentFromName(m.getObjectName(i));			
			if (e!=null) {
				((StringMIO)mg.add(e)).setValue(m.getObjectClass(i));
			}					
		}
	}
	
	protected void setFromMIGroup(MIGroup s) {
		ClassSelectionModel m = panel.getClassPartition();
		for (int i=0; i!=m.getNumObjects(); ++i) {
			Experiment e = getExperimentFromName(m.getObjectName(i));
			if (e!=null) {
				StringMIO cnamem = ((StringMIO)s.getMIO(e));
				if (cnamem!=null) {
					 String cname = cnamem.getValue();
					 if (cname!=null) {
						 m.setClass(i, cname);
					 }
				}
			}					
		}
		panel.setModel(m);
	}
	
	public PanelLoadStoreClasses(ClassSelectionPanel aPanel, DataSet ds) {
		this.panel = aPanel;
		this.ds = ds;
		setBorder(BorderFactory.createTitledBorder("Stored labellings"));
		setLayout(new ExcellentBoxLayout(false, 5));
		
		storedSettings = new JComboBox(); 
		updateStored();
		
		storedSettings.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange()==ItemEvent.SELECTED) {
					Object sel = storedSettings.getSelectedItem();
					if (sel!=null) {
						if (sel instanceof MIGroup) {
							MIGroup p = (MIGroup)sel;
							setFromMIGroup(p);
						}						
					}					
				}
			}
		});
		add(storedSettings);

		removeSelected = new JButton(new AbstractAction("Remove") {
			public void actionPerformed(ActionEvent e) {
				Object sel = storedSettings.getSelectedItem();
				if (sel!=null && sel instanceof MIGroup) {
					MIGroup p = (MIGroup)sel;	
					removeStoredSetting(p);
					updateStored();
				}
			}
		});
		add(removeSelected);
		
		addCurrent = new JButton(new AbstractAction("Add") {
			public void actionPerformed(ActionEvent e) {		
				String name = (String)JOptionPane.showInputDialog( null,
						"Please enter a name for the stored class labelling",
						"Store labelling",
						JOptionPane.QUESTION_MESSAGE,
						null,
						null,
						"");
				if (name==null)
					return;
				name = name.trim();
				if (name.length()==0)
					return;
				storeCurrentAs(name);
				updateStored();
			}			
		});
		add(addCurrent);
		
		setMaximumSize(getPreferredSize());
	}

	protected void updateStored() {
		storedSettings.removeAllItems();
		storedSettings.addItem("-- select a labelling --");
		for (MIGroup p : getStoredLabellings())
			storedSettings.addItem(p);
	}


}
