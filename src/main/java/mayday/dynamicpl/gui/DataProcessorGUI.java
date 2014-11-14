package mayday.dynamicpl.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import mayday.core.pluma.PluginManager;
import mayday.dynamicpl.AbstractDataProcessor;
import mayday.dynamicpl.DataProcessors;
import mayday.dynamicpl.Rule;

/* Represents on item in the sources stack of a Rule. Allows to change the item
 * but also presents options of the currently selected item. RuleGUI is responsible
 * for replacing this instance if another source is selected instead of the presently
 * selected source (stored in dataSource).
 */
@SuppressWarnings({"unchecked","serial"})
public class DataProcessorGUI extends JPanel {

	protected Rule rule;
	protected AbstractDataProcessor dataSource;
	protected Set<DataProcessors.Item> nextCandidates;
	protected JComboBox selectedSource = new JComboBox();
	
	public DataProcessorGUI(Rule theRule, AbstractDataProcessor ads, Set<DataProcessors.Item> theNextCandidates) {
		dataSource = ads;
		nextCandidates = theNextCandidates;
		rule=theRule;
		init();
	}
	
	protected void init() {
		setLayout(new BorderLayout());		
		
		if (nextCandidates.size()==0) {
			selectedSource.addItem("-- No matching processors (is the previous processor configured correctly?)");
		} else {
			int selectedIndex=0;
			selectedSource.addItem("-- Please select a data processor");
			
			for (DataProcessors.Item candidate : nextCandidates) {
				selectedSource.addItem(candidate);
				if (dataSource!=null)
					if (candidate.getPluginInfo()==PluginManager.getInstance().getPluginFromClass(dataSource.getClass()))
							selectedIndex = selectedSource.getItemCount()-1;
			}
			selectedSource.setSelectedIndex(selectedIndex);
			selectedSource.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					Object selO = selectedSource.getSelectedItem();
					AbstractDataProcessor selected = null;
					if (selO instanceof DataProcessors.Item) {
						selected = ((DataProcessors.Item)selO).newInstance(rule.getDynamicProbeList());
						if (dataSource==null) {
							// add new source
							rule.addProcessor(selected);
						} else {
							// replace existing
							rule.removeProcessor(dataSource);
							rule.addProcessor(selected);
						}
					} else {
						if (dataSource!=null) //remove existing
							rule.removeProcessor(dataSource);
					}
				
				}
				
			});
		}
				
		add(selectedSource, BorderLayout.NORTH);
		
		if (dataSource!=null && dataSource instanceof OptionPanelProvider) {
			add(((OptionPanelProvider)dataSource).getOptionPanel(), BorderLayout.CENTER);
		}
					
	}
	
	public JComboBox getComboBox() {
		return selectedSource;
	}
	
}
