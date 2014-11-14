package mayday.dynamicpl.dataprocessor;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.TreeSet;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import mayday.core.io.StorageNode;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.AbstractDataProcessor;
import mayday.dynamicpl.gui.OptionPanelProvider;
import mayday.dynamicpl.miostore.StorageNodeStorable;

@SuppressWarnings("unchecked")
public class StringInStringList extends AbstractDataProcessor<String,Boolean> 
	implements OptionPanelProvider, StorageNodeStorable {

	private TreeSet<String> l_strings = new TreeSet<String>();
	private JTextArea lTF = new JTextArea(10,30);
	
	private String listToString(TreeSet<String> list) {
		String listContent = "";
		for (String s : list)
			listContent+=", "+s;
		if (listContent.length()>0)
			listContent = listContent.substring(2); //remove leading comma
		return listContent;
	}
	
	private TreeSet<String> stringToList(String list) {
		TreeSet<String> ret = new TreeSet<String>(); 
		String[] ret2 = list.split("[,\n]");
		for(String s: ret2) {
			s = s.trim();
			if (s.length()!=0)
				ret.add(s);
		}
		return ret;
	}
	
	public void composeOptionPanel(JPanel optionPanel) {
		optionPanel.setLayout(new BorderLayout());
		optionPanel.add(new JLabel("String list, separated by comma or newline"), BorderLayout.NORTH);		
		
		lTF.setText(listToString(l_strings));
		//lTF.setBorder(BorderFactory.createEtchedBorder());
		lTF.setWrapStyleWord(true);
		lTF.setLineWrap(true);
		
		lTF.getDocument().addDocumentListener(new DocumentListener() {

			public void actionPerformed() {
				
				TreeSet<String> news = stringToList(lTF.getText());
				boolean ch = (news.size()!=l_strings.size());
				if (!ch) { // same length, but also same content?
					TreeSet<String> tmp = new TreeSet<String>(news);
					tmp.retainAll(l_strings);
					ch=(tmp.size()!=news.size());
				}
							
				if (ch) {
					l_strings.clear();
					l_strings.addAll(news);
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
		optionPanel.add(new JScrollPane(lTF), BorderLayout.CENTER);
		
	}
	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return (String.class.isAssignableFrom(inputClass[0]));
	}

	@Override
	public String toString() {
		return " one of "+l_strings.size()+" strings";
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.filter.StringInStringList",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"String occurs in a list",
				"String occurs in a list"
		);
		return pli;
	}
	
	
	// use this to load/save your options	
	public StorageNode toStorageNode() {
		StorageNode parent = new StorageNode("StringList","");
		for (String s : l_strings)
			parent.addChild(s,"");
		return parent;
	}
	
	public void fromStorageNode(StorageNode sn) {
		l_strings.clear();
		for (StorageNode child : sn.getChildren())
			l_strings.add(child.Name);
		lTF.setText(listToString(l_strings));
	}
	
	@Override
	protected Boolean convert(String value) {
		return l_strings.contains(value);
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
