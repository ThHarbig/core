package mayday.dynamicpl.dataprocessor;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
public class SpecificFromCollection extends AbstractDataProcessor<Collection,Object> 
	implements OptionPanelProvider, StorageNodeStorable {

	private int index=0;
	private String iString=""+index;
	
	private Class[] innerClass;
	
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		if (inputClass[0]==Collection.class) {
			if (inputClass.length>1) {
				innerClass  = new Class[inputClass.length-1];
				System.arraycopy(inputClass, 1, innerClass, 0, inputClass.length-1);
			}				
			return true;
		}
		return false;
	}
	
	private JTextField numberTF = new JTextField(10);
	
	public void composeOptionPanel(JPanel optionPanel) {
		optionPanel.add(new JLabel("Extract item with index "));
		numberTF.setText(iString);
		final JLabel mistake = new JLabel("Please check your input.");
		numberTF.getDocument().addDocumentListener(new DocumentListener() {

			public void actionPerformed() {
				if (iString != numberTF.getText()) {
					iString = numberTF.getText();
					try {
						index = Integer.parseInt(iString);
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

	@Override
	public String toString() {
		return "["+iString+"] ";
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.filter.SpecificFromCollection",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Specific element matches",
				"Specific element matches"
		);
		return pli;
	}
	
	
	// use this to load/save your options	
	public StorageNode toStorageNode() {
		StorageNode parent = new StorageNode("SpecificFromCollection","");
		parent.addChild("index",iString);
		return parent;
	}
	
	public void fromStorageNode(StorageNode sn) {
		index = Integer.parseInt(sn.getChild("index").Value);
		iString = ""+index;;
		numberTF.setText(iString);
	}
	
	@Override
	protected Object convert(Collection c) {
		if (c instanceof List)  // fast access
			return ((List)c).get(index);
		Object ret=null;
		Iterator it = c.iterator(); // slow access
		for (int i=0; i<=index && it.hasNext(); ++i)
			ret = it.next();
		return ret;
	}
	@Override
	public Class<?>[] getDataClass() {
		return innerClass;
	}
	public JPanel getOptionPanel() {
		JPanel pnl  = new  JPanel();
		composeOptionPanel(pnl);
		return pnl;
	}

	}
