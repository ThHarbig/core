package mayday.core.settings;

import java.awt.Component;

import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import mayday.core.MaydayDefaults;
import mayday.core.settings.events.SettingChangeListener;

public abstract class AbstractSettingComponent<T extends Setting> implements SettingComponent, SettingChangeListener {

	protected T mySetting;
	protected JPanel editorComponent;
	protected boolean hideLabelOverride = false;

	public AbstractSettingComponent(T s) {
		mySetting = s;
		mySetting.addChangeListener(this);
	}
	
	public JComponent getEditorComponent() {
		if (editorComponent==null) {
			editorComponent = new JPanel();
			editorComponent.setLayout(new GroupLayout(editorComponent));
			GroupLayout layout = ((GroupLayout)editorComponent.getLayout());
			layout.setAutoCreateContainerGaps(false);
			layout.setAutoCreateGaps(true);
			GroupLayout.Group seqG, parG;
			layout.setHorizontalGroup(seqG = layout.createSequentialGroup());
			layout.setVerticalGroup(parG = layout.createParallelGroup());
			if (needsLabel() && !hideLabelOverride) {
				JLabel lbl = new JLabel(mySetting.getName());
				seqG.addComponent(lbl);
				parG.addComponent(lbl);
			}
			Component sc = getSettingComponent();
			seqG.addComponent(sc);
			parG.addComponent(sc);
			if (mySetting.getDescription()!=null) {
				HelpButton btn = new HelpButton(mySetting.getName(), mySetting.getDescription());
				seqG.addComponent(btn);
				parG.addComponent(btn);
			}
		}
		return editorComponent;
	}
	
	public void hideLabel(boolean h) {
		hideLabelOverride = h;
	}
	
	protected abstract Component getSettingComponent();
	
	/** returns null if no GUI is editing on this setting */
	protected abstract String getCurrentValueFromGUI();
	
	protected boolean needsLabel() {
		return true;
	}

	public boolean updateSettingFromEditor(boolean failSilently) {
		String newVal = getCurrentValueFromGUI();
		if (newVal==null)
			return true; //nothing to change
		if (mySetting.isValidValue(newVal)) {
			mySetting.setValueString(newVal);
			return true;
		} else {
			if (!failSilently) {
				JOptionPane.showMessageDialog(null, mySetting.getValidityHint()
						+ "\nPlease correct your input."
						,
						MaydayDefaults.Messages.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
			}
			return false;
		}
	}
	
	public T getCorrespondingSetting() {
		return mySetting;
	}
	

}
