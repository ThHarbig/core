package mayday.core.settings.generic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import mayday.core.settings.AbstractSettingComponent;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.SettingsPanel;
import mayday.core.settings.events.SettingChangeEvent;

public class BooleanHierarchicalSettingComponent_Foldup extends AbstractSettingComponent<BooleanHierarchicalSetting> {

	protected JCheckBox cb;
	protected JPanel pnl;
	protected JPanel subsetpnl;
	protected SettingsPanel innerpnl;
	protected SettingComponent childSC; // if only one child and not topmost

	public BooleanHierarchicalSettingComponent_Foldup(BooleanHierarchicalSetting s) {
		super(s);
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
		boolean sel = mySetting.getBooleanValue();
		if (cb!=null)
			cb.setSelected(sel);
		doFoldup();
	}

	protected void doFoldup() {
		if (cb==null)
			return;
		boolean sel = cb.isSelected();
		
		if (subsetpnl!=null) {
			subsetpnl.setVisible(sel);
			subsetpnl.invalidate();
			subsetpnl.revalidate();
		}
		
		if (pnl!=null) {
			pnl.invalidate();
			pnl.revalidate();
		}
	}


	public void stateChanged(SettingChangeEvent e) {
		setSelected();
	}

	protected String getCurrentValueFromGUI() {
		if (cb!=null)
			return Boolean.toString(cb.isSelected());
		return null;
	}
	
	public JComponent getEditorComponent() {
		JComponent c = super.getEditorComponent();
		c.setBackground(Color.WHITE);
		c.setOpaque(true);
		return c;
	}

	protected Component getSettingComponent() {

		if (pnl==null) {
			pnl = new JPanel();
			pnl.setLayout(new BorderLayout());
			pnl.setBackground(Color.WHITE);
			pnl.setOpaque(true);

			cb = new JCheckBox(mySetting.getName());
			cb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					doFoldup();
				}
			});
			cb.setBackground(Color.WHITE);
			cb.setOpaque(true);
			pnl.add(cb, BorderLayout.NORTH);
			
			subsetpnl = new JPanel(new BorderLayout());
			subsetpnl.setBackground(Color.WHITE);
			subsetpnl.setOpaque(true);

			JComponent inner = null;

			if (mySetting.children.size()==1) {
				SettingComponent sc = mySetting.children.get(0).getGUIElement();
				childSC = sc;
				if (sc instanceof HierarchicalSettingComponent_Panel) {
					((HierarchicalSettingComponent_Panel)sc).setTopMost(true);
				} else {
//					if (sc instanceof AbstractSettingComponent) {
//						((AbstractSettingComponent)sc).hideLabel(true);
//					} 
				}

				// MIN SIZE
				int delta = subsetpnl.getInsets().bottom + subsetpnl.getInsets().top;
				Dimension s = sc.getEditorComponent().getMinimumSize();
				subsetpnl.setMinimumSize(s);
				s = sc.getEditorComponent().getMaximumSize();
				s.width += cb.getPreferredSize().width+10;
				s.height = Math.max(subsetpnl.getPreferredSize().height, s.height+delta);
				subsetpnl.setMaximumSize(s);
				
				inner = sc.getEditorComponent();
			} else if (mySetting.children.size()>1) {
				innerpnl=new SettingsPanel(mySetting.children,true);
				inner = innerpnl;
			} 
			
			if (inner!=null) {
				subsetpnl.add(Box.createHorizontalStrut(new JCheckBox("").getPreferredSize().width), BorderLayout.WEST);
				subsetpnl.add(inner, BorderLayout.CENTER);
				pnl.add(subsetpnl, BorderLayout.CENTER);
			}

			setSelected();
		}
		return pnl;
	}


}
