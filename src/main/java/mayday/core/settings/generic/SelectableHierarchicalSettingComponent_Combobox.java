package mayday.core.settings.generic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import mayday.core.settings.AbstractSettingComponent;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.TopMostSettable;
import mayday.core.settings.events.SettingChangeEvent;

public class SelectableHierarchicalSettingComponent_Combobox extends AbstractSettingComponent<SelectableHierarchicalSetting>  {

	protected JComboBox cb;
	protected JPanel pnl;
	protected boolean topMost = false;
	List<SettingComponent> settingComponents = new ArrayList<SettingComponent>();
	protected Setting lastSelected;
	protected Component lastComponent;
	protected Dimension minDim = new Dimension();

	@SuppressWarnings("unchecked")
	public SelectableHierarchicalSettingComponent_Combobox(SelectableHierarchicalSetting s, boolean TopMost) {
		super(s);
		topMost = TopMost;
		for (Object o : s.getPredefinedValues())
			if (o instanceof TopMostSettable)
				settingComponents.add(((TopMostSettable)o).setTopMost(true).getGUIElement());
			else if (o instanceof Setting)
				settingComponents.add(((Setting)o).getGUIElement());
			else 
				settingComponents.add(null);
	}


	public void setTopMost(boolean TopMost) {
		topMost = TopMost;
	}
	
	public void stateChanged(SettingChangeEvent e) {
		setSelected();
	}

	public void setSelected() {
		String newVal = mySetting.getStringValue();
		int i;
		for (i=0; i!=cb.getItemCount(); ++i) {
			if (cb.getItemAt(i).toString().equals(newVal)) {
				cb.setSelectedIndex(i);
				break;
			}
		}
		updateChild();
	}
	
	public boolean updateSettingFromEditor(boolean failSilently) {
		if (!super.updateSettingFromEditor(failSilently))
			return false;
		for (int i=0; i!=settingComponents.size(); ++i) {
			SettingComponent sc = settingComponents.get(i);
			if (sc!=null && !sc.updateSettingFromEditor(i!=cb.getSelectedIndex())) // only warn on active child
				// only break if a subsetting is false AND selected
				if (i==cb.getSelectedIndex())
					return false;
		}
		return true;
	} 
	
	protected String getCurrentValueFromGUI() {
		if (cb==null)
			return null;
		return cb.getSelectedItem().toString();
	}

	protected Component getSettingComponent() {
		if (cb==null) {
			cb = new JComboBox(mySetting.getPredefinedValues());
			pnl = new JPanel(new BorderLayout());
			cb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					updateChild();
				}
			});
			cb.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
			
			for (Object o : mySetting.predef) {
				if (o instanceof Setting) {
					Setting subs = (Setting)o;
					Dimension d = subs.getGUIElement().getEditorComponent().getPreferredSize();
					minDim.width = Math.max(minDim.width, d.width);
					minDim.height = Math.max(minDim.height, d.height);
				}
			}
			minDim.height += cb.getPreferredSize().getHeight()+3;
			
			pnl.add(cb, BorderLayout.NORTH);
		}		
		setSelected();
		return pnl;
	}
	
	protected void updateChild() {
		if (lastSelected==null || lastSelected!=cb.getSelectedItem()) {
			Object o = cb.getSelectedItem();
			if (lastComponent!=null)
				pnl.remove(lastComponent);
			if (o instanceof Setting) {
				lastSelected = (Setting)cb.getSelectedItem();
				lastComponent = settingComponents.get(cb.getSelectedIndex()).getEditorComponent();
				pnl.add( lastComponent , BorderLayout.CENTER);
			} else 
				lastComponent = null;
			pnl.setPreferredSize(minDim);				
			pnl.invalidate();
			pnl.revalidate();
		}
	}

}
