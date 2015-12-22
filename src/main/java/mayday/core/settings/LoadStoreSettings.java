package mayday.core.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import mayday.core.Preferences;

@SuppressWarnings("serial")
public class LoadStoreSettings extends JPanel {

	JComboBox storedSettings;
	JButton addCurrent;
	JButton removeSelected;
	protected Settings settings;
	protected SettingsDialog settingsDialog;

	public LoadStoreSettings(SettingsDialog aSettingsDialog) {		
		this.settings = aSettingsDialog.settings;
		this.settingsDialog = aSettingsDialog;

		storedSettings = new JComboBox(); 
		updateStored();
		
		storedSettings.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange()==ItemEvent.SELECTED) {
					Object sel = storedSettings.getSelectedItem();
					if (sel!=null) {
						if (sel instanceof Preferences) {
							Preferences p = (Preferences)sel;
							settingsDialog.apply(); // needs to be done to make sure changes due to loading fire events
							settings.getRoot().fromPrefNode(p.node(p.keys()[0])); // always only ONE child here
						}
						else {
							settingsDialog.apply(); // needs to be done to make sure changes due to loading fire events
							Preferences p = settings.getLastUsedSettings();
							if (p!=null)
								settings.getRoot().fromPrefNode(p);
						}
					}					
				}
			}
		});
		add(storedSettings);

		removeSelected = new JButton(new AbstractAction("Remove") {
			public void actionPerformed(ActionEvent e) {
				Object sel = storedSettings.getSelectedItem();
				if (sel!=null && sel instanceof Preferences) {
					Preferences p = (Preferences)sel;					
					settings.removeStoredSetting(p.Name);
					updateStored();
				}
			}
		});
		add(removeSelected);
		
		addCurrent = new JButton(new AbstractAction("Add") {
			public void actionPerformed(ActionEvent e) {		
				if (!settingsDialog.apply())
					return;
				String name = (String)JOptionPane.showInputDialog( null,
						"Please enter a name for the stored setting",
						"Store setting",
						JOptionPane.QUESTION_MESSAGE,
						null,
						null,
						"");
				if (name==null)
					return;
				name = name.trim();
				if (name.length()==0)
					return;
				settings.storeCurrentAs(name);
				updateStored();
			}			
		});
		add(addCurrent);
		
		
	}

	protected void updateStored() {
		storedSettings.removeAllItems();
		storedSettings.addItem("-- last used settings --");
		for (Preferences p : settings.getStoredSettings())
			storedSettings.addItem(p);
	}


}
