package mayday.dynamicpl.dataprocessor;

import java.util.HashMap;
import java.util.Map;

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
public class KeyValuePair extends AbstractDataProcessor<Map, Boolean> 
implements StorageNodeStorable, OptionPanelProvider
{

	private String _key,_value;
	private JTextField keyTF = new JTextField(20);
	private JTextField valueTF = new JTextField(20);
	
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return (Map.class.isAssignableFrom(inputClass[0]));
	}

	@Override
	public String toString() {
		return ", "+_key+"="+_value;
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.filter.KeyValuePair",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Check for a Key=Value pair",
				"Check for a Key=Value pair"
		);
		return pli;
	}
	
	@Override
	protected Boolean convert(Map value) {
		Object o = value.get(_key);
		if (o!=null) 
			return o.toString().equals(_value);
		else return true;
	}
	@Override
	public Class<?>[] getDataClass() {
		return new Class[]{Boolean.class}; 
	}

	public void fromStorageNode(StorageNode storageNode) {
		_key = storageNode.getChild("Key").Value;
		_value = storageNode.getChild("Value").Value;
		keyTF.setText(_key);
		valueTF.setText(_value);
		fireChanged();
	}

	public StorageNode toStorageNode() {
		StorageNode parent = new StorageNode("KeyValuePair","");
		parent.addChild("Key", _key);
		parent.addChild("Value", _value);
		return parent;
	}

	public JPanel getOptionPanel() {
		JPanel optionPanel = new JPanel();
		keyTF.setText(_key);
		valueTF.setText(_value);
		optionPanel.add(new JLabel("Key:"));
		
		keyTF.getDocument().addDocumentListener(new DocumentListener() {

			public void actionPerformed() {
				if (_key != keyTF.getText()) {
					_key = keyTF.getText();
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
		optionPanel.add(keyTF);
		
		optionPanel.add(new JLabel("Value:"));
		valueTF.getDocument().addDocumentListener(new DocumentListener() {

			public void actionPerformed() {
				if (_value != valueTF.getText()) {
					_value = valueTF.getText();
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
		optionPanel.add(valueTF);
		return optionPanel;
	};
	
}
