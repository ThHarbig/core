package mayday.dynamicpl.dataprocessor;

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
public class StringInInterval extends AbstractDataProcessor<String,Boolean> 
	implements OptionPanelProvider, StorageNodeStorable {

	private String l_string = "";
	private String u_string = "";
	private JTextField lTF = new JTextField(10);
	private JTextField uTF = new JTextField(10);
	
	public void composeOptionPanel(JPanel optionPanel) {
		optionPanel.add(new JLabel("Lower bound"));
		
		lTF.setText(l_string);
		lTF.getDocument().addDocumentListener(new DocumentListener() {

			public void actionPerformed() {
				if (l_string != lTF.getText()) {
					l_string = lTF.getText();
					fireChanged();
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
					fireChanged();
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
		
	}
	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return (String.class.isAssignableFrom(inputClass[0]));
	}

	@Override
	public String toString() {
		return " in ["+l_string+","+u_string+"]";
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.filter.StringInInterval",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"String falls into interval",
				"String falls into interval"
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
		l_string = sn.getChild("lower").Value;
		u_string = sn.getChild("upper").Value;
		lTF.setText(l_string);
		uTF.setText(u_string);
	}
	
	@Override
	protected Boolean convert(String value) {
		return value.compareTo(l_string)>=0 && value.compareTo(u_string)<=0;
	}
	
	@Override
	public Class<?>[] getDataClass() {
		return new Class[]{Boolean.class};
	}
	public JPanel getOptionPanel() {
		JPanel pnl  = new  JPanel();
		composeOptionPanel(pnl);
		return pnl;
	}

	}
