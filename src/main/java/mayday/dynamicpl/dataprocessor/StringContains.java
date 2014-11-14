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
public class StringContains extends AbstractDataProcessor<String, Boolean> 
	implements OptionPanelProvider, StorageNodeStorable {

	private String substring = "";
	
	public void composeOptionPanel(JPanel optionPanel) {
		final JTextField jtf = new JTextField(20);
		jtf.setText(substring);
		optionPanel.add(new JLabel("Substring:"));
		jtf.getDocument().addDocumentListener(new DocumentListener() {

			public void actionPerformed() {
				if (substring != jtf.getText()) {
					substring = jtf.getText();
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
		optionPanel.add(jtf);
		
	}
	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return (String.class.isAssignableFrom(inputClass[0]));
	}

	@Override
	public String toString() {
		return " contains the substring \""+substring+"\"";
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.filter.StringContains",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"String contains a substring",
				"String contains a substring"
		);
		return pli;
	}
	
	// use this to load/save your options	
	public StorageNode toStorageNode() {
		return new StorageNode("Substring",substring);
	}
	
	public void fromStorageNode(StorageNode sn) {
		substring = sn.Value;
	}
	@Override
	protected Boolean convert(String value) {
		if (value==null)
			return true;
		return (substring.length()==0) || (value.contains(substring));
	}
	@Override
	public Class<?>[] getDataClass() {
		return new Class[]{Boolean.class};
	};

	public JPanel getOptionPanel() {
		JPanel pnl  = new  JPanel();
		composeOptionPanel(pnl);
		return pnl;
	}
}
