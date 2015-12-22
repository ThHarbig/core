package mayday.core.settings.generic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import mayday.core.gui.components.CheckBoxTitledBorder;
import mayday.core.gui.components.ComponentTitledBorder;
import mayday.core.settings.AbstractSettingComponent;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.SettingsPanel;
import mayday.core.settings.events.SettingChangeEvent;

public class BooleanHierarchicalSettingComponent_Panel extends AbstractSettingComponent<BooleanHierarchicalSetting> {
	
	protected boolean verticalLayout;
	protected boolean topMost;
	protected JCheckBox cb;
	protected JPanel pnl;
	protected SettingsPanel innerpnl;
	protected SettingComponent childSC; // if only one child and not topmost

	public BooleanHierarchicalSettingComponent_Panel(BooleanHierarchicalSetting s, boolean VerticalLayout, boolean isTopMost) {
		super(s);
		verticalLayout = VerticalLayout;
		topMost = isTopMost;
	}
	
	protected boolean needsLabel() {
		return false;
	}
	
	public boolean updateSettingFromEditor(boolean failSilently) {
		if (!super.updateSettingFromEditor(failSilently))
			return false;
		if (innerpnl!=null) {
			for (SettingComponent sc : innerpnl.getSettingComponents()) {
				boolean b = sc.updateSettingFromEditor(failSilently || !mySetting.getBooleanValue());
				if (mySetting.getBooleanValue() && !b)
					return false;
			}
		}
		if (childSC!=null) {
			boolean b = childSC.updateSettingFromEditor(failSilently || !mySetting.getBooleanValue());
			if (mySetting.getBooleanValue() && !b)
				return false;
		}
		return true;
	}

	public void setSelected() {
		if (cb!=null)
			cb.setSelected(mySetting.getBooleanValue());
	}
	
	public void stateChanged(SettingChangeEvent e) {
		setSelected();
	}
	
	protected String getCurrentValueFromGUI() {
		if (cb!=null)
			return Boolean.toString(cb.isSelected());
		return null;
	}

	@SuppressWarnings("unchecked")
	protected Component getSettingComponent() {
		if (cb==null) {
			cb = new JCheckBox(mySetting.getName());
			pnl = new JPanel();
			pnl.setLayout(new BorderLayout());
			if (!topMost && mySetting.children.size()==1) {
				SettingComponent sc = mySetting.children.get(0).getGUIElement();
				childSC = sc;
				if (sc instanceof AbstractSettingComponent) {
					((AbstractSettingComponent)sc).hideLabel(true);
				} else {
					cb.setText("");
				}
				JPanel justifier = new JPanel(new BorderLayout());
				justifier.add(cb, BorderLayout.NORTH);
				pnl.add(justifier, BorderLayout.WEST);
				pnl.add(sc.getEditorComponent());
				
				// MIN SIZE
				int delta = pnl.getInsets().bottom + pnl.getInsets().top;
				Dimension s = sc.getEditorComponent().getMinimumSize();
				pnl.setMinimumSize(s);
				s = sc.getEditorComponent().getMaximumSize();
				s.width += cb.getPreferredSize().width+10;
				s.height = Math.max(pnl.getPreferredSize().height, s.height+delta);
				pnl.setMaximumSize(s);
				
			} else {
				if (topMost)
					pnl.setBorder(new ComponentTitledBorder(cb, pnl, BorderFactory.createEmptyBorder(5,5,5,5)));
				else
					pnl.setBorder(new CheckBoxTitledBorder(cb,pnl));
				pnl.add( innerpnl=new SettingsPanel(mySetting.children,verticalLayout), BorderLayout.CENTER );
			}
			setSelected();
		}
		return pnl;
	}
	

}
