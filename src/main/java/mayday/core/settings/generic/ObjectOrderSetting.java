package mayday.core.settings.generic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import mayday.core.gui.components.ReorderableJList;
import mayday.core.settings.AbstractSettingComponent;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.typed.IntListSetting;

/** This setting can be used to set the ORDER of a number of distinct objects.
	WARNING: Serialization of this class saves the ordering as permutation of indices, not the list of ordered objects itself! */
public class ObjectOrderSetting<T> extends IntListSetting {
	
	public static enum LayoutStyle {
		LIST_WITH_BUTTONS,
		LIST_ONLY
	}
	
	protected List<T> theObjects;
	protected LayoutStyle layoutStyle = LayoutStyle.LIST_ONLY;
	
	public ObjectOrderSetting(String Name, String Description, List<T> objects) {
		super(Name, Description, null);
		if (objects!=null)
			setOrderedElements(objects);
	}
	
	public List<T> getOrderedElements() {
		LinkedList<T> ret = new LinkedList<T>();
		for (int i : getIntegerListValue()) {
			ret.add(theObjects.get(i));
		}
		return ret;
	}
	
	/** set the objects and their ordering. overwrites previous content */
	public void setOrderedElements(List<T> objects) {
		theObjects = new ArrayList<T>();
		theObjects.addAll(objects);
		LinkedList<Integer> ret = new LinkedList<Integer>();
		for (int i=0; i!=objects.size(); ++i)
			ret.add(i); // identity ordering
		setIntegerListValue(ret);				
	}
	
	public ObjectOrderSetting<T> setLayoutStyle(LayoutStyle style) {
		layoutStyle = style;
		return this;
	}
	
	public ObjectOrderSetting<T> clone() {
		ObjectOrderSetting<T> oos = new ObjectOrderSetting<T>(getName(),getDescription(),theObjects);
		oos.layoutStyle=layoutStyle;
		oos.fromPrefNode(toPrefNode());
		return oos;
	}
	
	
	public SettingComponent getGUIElement() {
		ObjectOrderSettingComponent oosc = new ObjectOrderSettingComponent(this);
		return oosc;
	}
	
	protected class ObjectOrderSettingComponent extends AbstractSettingComponent<ObjectOrderSetting<T>> {

		JPanel pnl;
		ReorderableJList theList;
		JButton upBut, dnBut;
		
		public ObjectOrderSettingComponent(
				ObjectOrderSetting<T> s) {
			super(s);
		}

		@Override
		protected String getCurrentValueFromGUI() {
			// create an ordering on the original settings' content
			List<T> original = mySetting.theObjects;
			LinkedList<Integer> ret = new LinkedList<Integer>();
			for (int i=0; i!=theList.getModel().getSize(); ++i)
				ret.add(original.indexOf(theList.getModel().getElementAt(i)));
			IntListSetting ils = new IntListSetting("tmp",null,ret);			
			return ils.getValueString();
		}

		@SuppressWarnings("serial")
		@Override
		protected Component getSettingComponent() {
			if (pnl==null) {
				theList = new ReorderableJList();
				theList.setSelectionModel(new DefaultListSelectionModel());
				theList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				theList.setVisibleRowCount(10);
				theList.setEnabled(true);

				pnl = new JPanel(new BorderLayout());
				pnl.add(new JScrollPane(theList), BorderLayout.CENTER);

				stateChanged(null);		
				if (mySetting.layoutStyle==LayoutStyle.LIST_WITH_BUTTONS) {
					upBut = new JButton(new AbstractAction("Move up") {
						public void actionPerformed(ActionEvent e) {
							moveUpElements();
						}
					});
					dnBut = new JButton(new AbstractAction("Move down") {
						public void actionPerformed(ActionEvent e) {
							moveDownElements();
						}					
					});
					JPanel p2 = new JPanel();
					p2.setLayout(new BoxLayout(p2, BoxLayout.LINE_AXIS));
					p2.add(Box.createHorizontalGlue());
					p2.add(upBut);
					p2.add(Box.createHorizontalStrut(5));
					p2.add(dnBut);
					pnl.add(p2, BorderLayout.SOUTH);
				}
			}
			return pnl;
		}
		
		protected void moveUpElements() {
			int[] selectedItems = theList.getSelectedIndices();
			DefaultListModel model = (DefaultListModel)theList.getModel();
			
			for ( int i = 0; i < selectedItems.length; ++i )	{
				int index = selectedItems[i];
				if (index==0)
					continue;
				Object o = model.remove(index-1);
				model.add(index, o);

				if ( selectedItems[i] > 0 )
					--selectedItems[i]; // move selection
				else
					selectedItems[i] = -1;
			}
			theList.setSelectedIndices( selectedItems );
		}
		
		protected void moveDownElements() {
			int[] selectedItems = theList.getSelectedIndices();
			DefaultListModel model = (DefaultListModel)theList.getModel();
			
			for ( int i = selectedItems.length-1; i >= 0 ; --i )	{
				int index = selectedItems[i];
				if (index==model.getSize()-1)
					continue;
				Object o = model.remove(index+1);
				model.add(index, o);

				if ( selectedItems[i] < model.getSize() - 1 )
					++selectedItems[i]; // move selection
				else
					selectedItems[i] = -1;
			}
			theList.setSelectedIndices( selectedItems );
		}

		public void stateChanged(SettingChangeEvent e) {			
			if (theList!=null) {
				DefaultListModel m = ((DefaultListModel)theList.getModel());
				m.clear();
				for (T le : mySetting.getOrderedElements())
					m.addElement(le);					
			}
		}


	
		
	}
	
}

