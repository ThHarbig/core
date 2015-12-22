package mayday.dynamicpl.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.dynamicpl.RuleSet;

@SuppressWarnings("serial")
public class RuleSetGUI extends JPanel implements ChangeListener {

	private RuleSet ruleSet;
	private JComboBox combinationComboBox;
	private JCheckBox activeCheckBox;
	
	private boolean changeInitiatedByMe = false;
	
	public RuleSetGUI(RuleSet rs) {
		ruleSet = rs;		
		init();
		ruleSet.addChangeListener(this);
	}
	
	protected void init() {		
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder("Rule Set Options"));
		
		JPanel subPanel = new JPanel();
		subPanel.setLayout(new BorderLayout());
		
		activeCheckBox = new JCheckBox("Active"); 
		activeCheckBox.setSelected(ruleSet.isActive());
		activeCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeInitiatedByMe=true;
				ruleSet.setActive(activeCheckBox.isSelected());
				changeInitiatedByMe=false;
			}
		});
		subPanel.add(activeCheckBox, BorderLayout.WEST);
				
		combinationComboBox = new JComboBox(new String[]{
				"Probes must fulfill ALL contained rules",
				"Probes must fulfill ANY contained rule",
		});
		combinationComboBox.setSelectedIndex(ruleSet.getCombinationMode());
		combinationComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int newMode = combinationComboBox.getSelectedIndex();
				changeInitiatedByMe=true;
				ruleSet.setCombinationMode(newMode);
				changeInitiatedByMe=false;
			}
		});
		subPanel.add(combinationComboBox, BorderLayout.EAST);
		
		add(subPanel, BorderLayout.NORTH);
	}

	public void stateChanged(ChangeEvent arg0) {
		if (changeInitiatedByMe)
			return;		
		combinationComboBox.setSelectedIndex(ruleSet.getCombinationMode());
		activeCheckBox.setSelected(ruleSet.isActive());		
	}
	
	public void removeNotify() {
		ruleSet.removeChangeListener(this);
		super.removeNotify();
	}
}
