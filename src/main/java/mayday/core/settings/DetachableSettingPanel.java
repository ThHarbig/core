package mayday.core.settings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class DetachableSettingPanel extends JPanel {
	
	protected JButton applyButton;
	protected SettingComponent settingComponent;
	protected Setting setting;
	protected Window parent;
	
	@SuppressWarnings("unchecked")
	public DetachableSettingPanel(final Setting setting, final Window parent, boolean hideLabel) {
//		JPanel borderTitle = new JPanel(new BorderLayout());
//		borderTitle.add(new JLabel(setting.getName()), BorderLayout.CENTER);
		this.setting=setting;
		this.parent=parent;
		settingComponent = setting.getGUIElement();
		if ((settingComponent instanceof AbstractSettingComponent) && hideLabel)
			((AbstractSettingComponent)settingComponent).hideLabel(true);
		init();
	}
	
	public DetachableSettingPanel(final Setting setting, final Window parent) {
		this(setting, parent, false);
	}
	
	protected void init() {
		setLayout(new BorderLayout());	
		
		Component editor = settingComponent.getEditorComponent();		
		
		add(editor, BorderLayout.CENTER);

		applyButton = new JButton(new AbstractAction("Apply") {
			public void actionPerformed(ActionEvent e) {
				apply();
			}
		});		

		JButton detachButton = new JButton(new AbstractAction("Detach") {
			public void actionPerformed(ActionEvent e) {
				final Window sdlg = new SettingDialog(parent, setting.getName(), setting);
				sdlg.setVisible(true);			
				if (parent!=null)
					parent.addWindowListener(new WindowAdapter() {
						public void windowClosing( WindowEvent evt ) {
							sdlg.dispose();
						}
					});
			}
		});		

		
		JPanel buttons = new JPanel();
		
		buttons.add(Box.createHorizontalGlue());
		buttons.add(detachButton);
		buttons.add(Box.createHorizontalStrut(10));
		buttons.add(applyButton);
		add(buttons, BorderLayout.SOUTH);
		
	}
	
	public boolean apply() {
		return settingComponent.updateSettingFromEditor(false);
	}

}
