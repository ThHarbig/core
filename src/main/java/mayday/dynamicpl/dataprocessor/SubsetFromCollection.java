package mayday.dynamicpl.dataprocessor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
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
public class SubsetFromCollection extends AbstractDataProcessor<Collection,Collection> 
	implements OptionPanelProvider, StorageNodeStorable {

	private int[] indices=new int[0];
	private String iString="";
	
	private Class[] innerClass;
	
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		if (inputClass[0]==Collection.class) {
			if (inputClass.length>1) {
				innerClass  = new Class[inputClass.length];
				System.arraycopy(inputClass, 0, innerClass, 0, inputClass.length);
			}				
			return true;
		}
		return false;
	}
	
	private JTextField numberTF = new JTextField(10);
	
	public void composeOptionPanel(JPanel optionPanel) {
		optionPanel.add(new JLabel("Extract items with indices (comma separated)"));
		numberTF.setText(iString);
		final JLabel mistake = new JLabel("Please check your input.");
		numberTF.getDocument().addDocumentListener(new DocumentListener() {

			public void actionPerformed() {
				if (iString != numberTF.getText()) {
					iString = numberTF.getText();					
					try {
						String[] parts = iString.split(",");
						indices=new int[parts.length];
						for (int i=0;i!=parts.length;++i)
							indices[i]=Integer.parseInt(parts[i]);						
						iString = Arrays.toString(indices).replaceAll("\\[", "").replaceAll("\\]", "");
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
				this.getClass(),
				"PAS.dynamicPL.filter.SubsetFromCollection",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Subset matches",
				"Subset matches"
		);
		return pli;
	}
	
	
	// use this to load/save your options	
	public StorageNode toStorageNode() {
		StorageNode parent = new StorageNode("SubsetFromCollection","");
		parent.addChild("indices",iString);
		return parent;
	}
	
	public void fromStorageNode(StorageNode sn) {
		numberTF.setText(sn.getChild("index").Value); // will hopefully emit a document action event
	}
	
	@Override
	protected Collection convert(Collection c) {
		List<Object> lo = new ArrayList<Object>();

		if (!(c instanceof List)) {
			c = new ArrayList(c);
		}

		for (int index : indices)
			lo.add(lo.get(index));

		return lo;
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
