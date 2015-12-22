package mayday.dynamicpl.dataprocessor;

import java.util.HashMap;
import java.util.regex.Pattern;

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
public class StringMatchesPattern extends AbstractDataProcessor<String, Boolean> 
	implements OptionPanelProvider, StorageNodeStorable {

	private String pattern = "";
	private Pattern RegexPattern = Pattern.compile("");
	
	public void composeOptionPanel(JPanel optionPanel) {
		final JLabel mistake = new JLabel("Please check your input.");
		final JTextField jtf = new JTextField(20);
		jtf.setText(pattern);
		
		optionPanel.add(new JLabel("Regular Expression:"));
		jtf.getDocument().addDocumentListener(new DocumentListener() {

			public void actionPerformed() {
				if (pattern != jtf.getText()) {
					if (!setPattern(jtf.getText())) {
						mistake.setVisible(true);
					} else {
						mistake.setVisible(false);
						fireChanged();
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
		optionPanel.add(jtf);
		optionPanel.add(mistake);
	}
	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return (String.class.isAssignableFrom(inputClass[0]));
	}

	@Override
	public String toString() {
		return " matches the pattern \""+pattern+"\"";
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.filter.StringMatchesPattern",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"String matches a regular expression",
				"String matches a regular expression"
		);
		return pli;
	}
	
	// use this to load/save your options	
	public StorageNode toStorageNode() {
		return new StorageNode("Substring",pattern);
	}
	
	public void fromStorageNode(StorageNode sn) {
		setPattern(sn.Value);
	}
	@Override
	protected Boolean convert(String value) {
		if (value==null)
			return true;
		return RegexPattern.matcher(value).matches();
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
	
	private boolean setPattern(String s) {
		pattern = s;
		try {
			RegexPattern = Pattern.compile(s); 
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
