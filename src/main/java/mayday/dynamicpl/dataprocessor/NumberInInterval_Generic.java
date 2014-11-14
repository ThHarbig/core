package mayday.dynamicpl.dataprocessor;

import java.awt.Color;
import java.util.HashMap;

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
public abstract class NumberInInterval_Generic<V extends Number> extends AbstractDataProcessor<V,Boolean> 
	implements OptionPanelProvider, StorageNodeStorable {

	protected V lower;
	protected V upper;
	private String l_string;
	private String u_string;
	private JTextField lTF = new JTextField(10);
	private JTextField uTF = new JTextField(10);

	public NumberInInterval_Generic() {
		initNumbers();
		l_string = ""+lower;
		u_string = ""+upper;
	}
	
	public void composeOptionPanel(JPanel optionPanel) {
		final JLabel mistake = new JLabel("Please check your input.");

		optionPanel.add(new JLabel("Lower bound"));
		
		lTF.setText(l_string);
		lTF.getDocument().addDocumentListener(new DocumentListener() {

			public void actionPerformed() {
				if (l_string != lTF.getText()) {
					l_string = lTF.getText();
					try {
						lower = parseNumber(l_string);
						lTF.setBackground(Color.green);
						mistake.setVisible(false);
						fireChanged();
					} catch (Exception e) {
						lTF.setBackground(Color.red);
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
		optionPanel.add(lTF);
		
		optionPanel.add(new JLabel("Upper bound"));
		
		uTF.setText(u_string);
		uTF.getDocument().addDocumentListener(new DocumentListener() {

			public void actionPerformed() {
				if (u_string != uTF.getText()) {
					u_string = uTF.getText();
					try {
						upper = parseNumber(u_string);
						uTF.setBackground(Color.green);
						mistake.setVisible(false);
						fireChanged();
					} catch (Exception e) {
						uTF.setBackground(Color.red);
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
		optionPanel.add(uTF);
		
		optionPanel.add(mistake);
		mistake.setVisible(false);
		
	}
	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return (lower.getClass().isAssignableFrom(inputClass[0]));
	}

	@Override
	public String toString() {
		return " in ["+l_string+","+u_string+"]";
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.filter.NumberInInterval."+lower.getClass(),
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Falls into interval ("+lower.getClass()+")",
				"Falls into interval ("+lower.getClass()+")"
		);
		return pli;
	}
	
	
	// use this to load/save your options	
	public StorageNode toStorageNode() {
		StorageNode parent = new StorageNode("Interval","");
		parent.addChild("lower",l_string);
		parent.addChild("upper",u_string);
		return parent;
	}
	
	public void fromStorageNode(StorageNode sn) {
		lower = parseNumber(sn.getChild("lower").Value);
		upper = parseNumber(sn.getChild("upper").Value);
		l_string=""+lower;
		u_string=""+upper;
		lTF.setText(l_string);
		uTF.setText(u_string);
	}
	
	@Override
	protected abstract Boolean convert(V value); 
	
	@Override
	public Class<?>[] getDataClass() {
		return new Class[]{Boolean.class};
	}
	
	public JPanel getOptionPanel() {
		JPanel pnl  = new  JPanel();
		composeOptionPanel(pnl);
		return pnl;
	}
	
	public abstract void initNumbers();
	
	public abstract V parseNumber(String s);

}
