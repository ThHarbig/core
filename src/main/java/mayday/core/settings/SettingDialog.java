package mayday.core.settings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mayday.core.gui.MaydayDialog;

@SuppressWarnings("serial")
public class SettingDialog extends MaydayDialog {
	
	protected JButton okButton;
	protected JButton applyButton;
	protected JButton cancelButton;
	protected SettingComponent settingComponent;
	protected boolean closedWithOK;
	
	@SuppressWarnings("unchecked")
	public SettingDialog(Window owner, String title, Setting setting) {
		super(owner, title);
		if (setting!=null) {
			if (setting instanceof TopMostSettable)
				((TopMostSettable) setting).setTopMost(true);
			settingComponent = setting.getGUIElement();
			init();
		}
	}
	
	/** Shows the dialog without an apply button (only OK and Cancel) and makes it modal */
	public SettingDialog showAsInputDialog() {
		getApplyButton().setVisible(false);
		setModal(true);
		setVisible(true);
		return this;
	}
	
	@Override
	public void setVisible(boolean visible) {
		closedWithOK = false;
		super.setVisible(visible);
	}

	protected void init() {
//		setLayout(new BorderLayout());	
		
		Component editor = settingComponent.getEditorComponent();		
		
		JScrollPane sp = new JScrollPane(editor);
		sp.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		
		add(sp);//, BorderLayout.CENTER);

		applyButton = new JButton(new AbstractAction("Apply") {
			public void actionPerformed(ActionEvent e) {
				apply();
			}
		});		
		
		okButton = new JButton(new AbstractAction("OK") {
			public void actionPerformed(ActionEvent e) {
				if (applyAndSave()) {
					closedWithOK = true;
					dispose();
				}				
			}
		});
		
		cancelButton = new JButton(new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent e) {
				dispose();				
			}
		});	
		
		JPanel buttons = new JPanel();
		
		additionalButtons(buttons);
		buttons.add(Box.createHorizontalGlue());
		buttons.add(Box.createHorizontalStrut(10));
		buttons.add(cancelButton);
		buttons.add(Box.createHorizontalStrut(5));
		buttons.add(applyButton);
		buttons.add(okButton);
		add(buttons, BorderLayout.SOUTH);
		
		getRootPane().setDefaultButton(okButton);
		
		pack();
		
		Dimension d = getPreferredSize();
		d.width = Math.min(d.width, 800);
		d.height= Math.min(d.height, 600);		
		setMinimumSize(d);
		
	}
	
	public void additionalButtons(JPanel buttons) {}
	
	public boolean apply() {
		return settingComponent.updateSettingFromEditor(false);
	}

	public boolean applyAndSave() {
		return apply(); // no saving here.
	}
	
	public boolean closedWithOK() {
		return closedWithOK;
	}
	
	public boolean canceled() {
		return !closedWithOK;
	}
	
	protected JButton getApplyButton() {
		return applyButton;
	}
	
	public JButton getOKButton() {
		return okButton;
	}
	
	public JButton getCancelButton() {
		return cancelButton;
	}
	

}
