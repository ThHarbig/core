package mayday.dynamicpl.dataprocessor;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import mayday.core.io.StorageNode;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.AbstractDataProcessor;
import mayday.dynamicpl.gui.OptionPanelProvider;
import mayday.dynamicpl.miostore.StorageNodeStorable;

@SuppressWarnings("unchecked")
public abstract class NumberMatches_Generic<V extends Number> extends AbstractDataProcessor<V,Boolean> 
	implements OptionPanelProvider, StorageNodeStorable {

	protected V number;
	private String substring;
	protected int operation=2;
	private String[] operations = new String[]{
			"<","<=","==",">=",">"
	};
	private String[] operations2 = new String[]{
			"&lt;","&lt;=","==","&gt;=","&gt;"
	};
	private JComboBox operationCB = new JComboBox(operations);
	private JTextField numberTF = new JTextField(10);
	
	public NumberMatches_Generic() {
		initNumbers();
		substring = ""+number;
	}
	
	public void composeOptionPanel(JPanel optionPanel) {
		optionPanel.add(new JLabel("Value should be "));
		operationCB.setSelectedIndex(operation);
		operationCB.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				int newOp = operationCB.getSelectedIndex();
				if (newOp!=operation) {
					operation=newOp;
					fireChanged();
				}
			}
			
		});
		optionPanel.add(operationCB);
		
		numberTF.setText(substring);
		final JLabel mistake = new JLabel("Please check your input.");
		numberTF.getDocument().addDocumentListener(new DocumentListener() {

			public void actionPerformed() {
				if (substring != numberTF.getText()) {
					substring = numberTF.getText();
					try {
						number = parseNumber(substring);
						numberTF.setBackground(Color.green);
						mistake.setVisible(false);
						fireChanged();
					} catch (Exception e) {
						numberTF.setBackground(Color.red);
						mistake.setVisible(true);
					}
				}
			}

			public void changedUpdate(DocumentEvent e) {
				actionPerformed();
			}

			public void insertUpdate(DocumentEvent e) {
				actionPerformed();			}

			public void removeUpdate(DocumentEvent e) {
				actionPerformed();	
			}
		});
		optionPanel.add(numberTF);
		optionPanel.add(mistake);
		mistake.setVisible(false);
		
	}
	
	protected abstract V parseNumber(String string);
	
	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return (number.getClass().isAssignableFrom(inputClass[0]));
	}

	@Override
	public String toString() {
		return " "+operations2[operation]+" "+number;
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.filter.NumberMatches."+number.getClass(),
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Compare a number ("+number.getClass()+")",
				"Compare a number ("+number.getClass()+")"
		);
		return pli;
	}
	
	
	// use this to load/save your options	
	public StorageNode toStorageNode() {
		StorageNode parent = new StorageNode("NumberMatcher","");
		parent.addChild("number",number);
		parent.addChild("operation",operation);
		return parent;
	}
	
	public void fromStorageNode(StorageNode sn) {
		number = parseNumber(sn.getChild("number").Value);
		operation = Integer.parseInt(sn.getChild("operation").Value);
		substring=""+number;
		numberTF.setText(substring);
		operationCB.setSelectedIndex(operation);
	}
	
	@Override
	protected abstract Boolean convert(V v);
	
	@Override
	public Class<?>[] getDataClass() {
		return new Class[]{Boolean.class};
	}
	
	public JPanel getOptionPanel() {
		JPanel pnl  = new  JPanel();
		composeOptionPanel(pnl);
		return pnl;
	}
	
	protected abstract void initNumbers();

}
