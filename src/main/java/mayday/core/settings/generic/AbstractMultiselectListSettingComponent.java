package mayday.core.settings.generic;

import java.awt.Component;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import mayday.core.meta.MIType;
import mayday.core.meta.types.StringListMIO;
import mayday.core.settings.AbstractSettingComponent;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;

public abstract class AbstractMultiselectListSettingComponent<T extends Setting, ListElement>  extends AbstractSettingComponent<T> {
	
	protected JList theList;
	protected JScrollPane thePane;
	
	protected abstract String renderListElement(ListElement element);
	protected abstract String renderToolTip(ListElement element);
	protected abstract Iterable<ListElement> elementsFromSetting(T mySetting);
	protected abstract Iterable<Integer> selectedElementsFromSetting(T mySetting);
	
	public AbstractMultiselectListSettingComponent(T s) {
		super(s);
	}

	@Override
	protected String getCurrentValueFromGUI() {
		List<String> fr = new LinkedList<String>();
		for (Integer i=0; i!=theList.getModel().getSize(); ++i)
			if (theList.getSelectionModel().isSelectedIndex(i))
				fr.add(i.toString());
		StringListMIO slm = new StringListMIO();
		slm.setValue(fr);
		return slm.serialize(MIType.SERIAL_TEXT);
	}
	
	@SuppressWarnings("unchecked")
	protected List<ListElement> modelToList(ListModel dlm) {
		List<ListElement> ret = new LinkedList<ListElement>();
		for (int i=0; i!=dlm.getSize(); ++i)
			ret.add((ListElement)dlm.getElementAt(i));
		return ret;
	}
	
	

	@Override
	protected Component getSettingComponent() {
		if (thePane==null) {
			theList = new JList(new DefaultListModel());
			theList.setSelectionModel(new DefaultListSelectionModel());
			theList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			theList.setVisibleRowCount(10);
			theList.setEnabled(true);
			stateChanged(null);		
			// replace the renderer
			final ListCellRenderer prevRenderer = theList.getCellRenderer();			
			theList.setCellRenderer(new ListCellRenderer() {
				@SuppressWarnings("unchecked")
				public Component getListCellRendererComponent(JList list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					if (prevRenderer instanceof JComponent)
						((JComponent)prevRenderer).setToolTipText(renderToolTip((ListElement)value));
					return prevRenderer.getListCellRendererComponent(
							list, 
							renderListElement((ListElement)value), 
							index, 
							isSelected, 
							cellHasFocus
							);
				}
			});
			thePane = new JScrollPane(theList);
		}
		return thePane;
	}

	public void stateChanged(SettingChangeEvent e) {
		if (theList!=null) {
			DefaultListModel m = ((DefaultListModel)theList.getModel());
			m.clear();
			for (ListElement le : elementsFromSetting(mySetting))
				m.addElement(le);
			DefaultListSelectionModel sm = ((DefaultListSelectionModel)theList.getSelectionModel());
			sm.clearSelection();
			for (int sel : selectedElementsFromSetting(mySetting))
				sm.addSelectionInterval(sel,sel);
		}
	}

}
