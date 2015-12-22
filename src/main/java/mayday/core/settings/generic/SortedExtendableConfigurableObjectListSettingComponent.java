package mayday.core.settings.generic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import mayday.core.gui.components.ReorderableJList;
import mayday.core.settings.AbstractSettingComponent;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.typed.RestrictedStringSetting;

public class SortedExtendableConfigurableObjectListSettingComponent<T> 
	extends AbstractSettingComponent<SortedExtendableConfigurableObjectListSetting<T>> {

	protected JList theList;
	protected JButton addB, removeB;
	protected JPanel pnl_inner;
	protected JSplitPane pnl;
	protected JPanel empty_panel;
	protected Dimension minDim = new Dimension();

	
	protected boolean topMost;
	
	List<SettingComponent> settingComponents = new ArrayList<SettingComponent>();
	HashMap<Setting, SettingComponent> settingComponentsMap = new HashMap<Setting, SettingComponent>();

	public SortedExtendableConfigurableObjectListSettingComponent(SortedExtendableConfigurableObjectListSetting<T> s, boolean topMost) {
		super(s);
		this.topMost = topMost;
	}
	
	
	protected String renderListElement(T element) {
		return mySetting.getBridge().getDisplayName(element);
	}
	
	protected String renderToolTip(T element) {
		return mySetting.getBridge().getTooltip(element);
	}
	
	protected T getElementToAdd(Collection<T> alreadyPresent) {
		LinkedList<T> available = new LinkedList<T>();
		available.addAll(mySetting.getBridge().availableElementsForAddition(alreadyPresent));
		
		if (available.size()==1) {
			// add right away
			return available.getFirst();
		}
		
		TreeMap<String, T> elements = new TreeMap<String, T>();
		TreeMap<String, String> tooltips = new TreeMap<String, String>();
		
		for (T elem: available) {
			elements.put(mySetting.getBridge().getDisplayName(elem), elem);
			tooltips.put(mySetting.getBridge().getDisplayName(elem), mySetting.getBridge().getTooltip(elem));
		}
		
		RestrictedStringSetting selection = new RestrictedStringSetting("Select element to add",null,0,elements.keySet().toArray(new String[0]));
		selection.setToolTips(tooltips.values().toArray(new String[0]));
		
		selection.setLayoutStyle(ObjectSelectionSetting.LayoutStyle.LIST);
		
		SettingDialog sd = new SettingDialog(null, "Select elements to add", selection);
		sd.showAsInputDialog();
		
		if (!sd.canceled()) {
			T elem = elements.get(selection.getObjectValue());
			for (T unneeded : elements.values())
				if (unneeded!=elem)
					mySetting.getBridge().disposeElement(unneeded);
			return elem;
		}

		for (T unneeded : elements.values())
			mySetting.getBridge().disposeElement(unneeded);
		
		return null;				
	}
	
	protected Iterable<T> elementsFromSetting(SortedExtendableConfigurableObjectListSetting<T> mySetting) {
		return mySetting.getElements();
	}
	
	protected Setting elementToSetting(T element) {
		return mySetting.getBridge().getSettingForElement(element);
	}

	public boolean needsLabel() {
		return !topMost;
	}
	
	@Override
	protected String getCurrentValueFromGUI() {
		throw new UnsupportedOperationException("How did you call this function?");
	}

	@SuppressWarnings("unchecked")
	protected List<T> modelToList(ListModel dlm) {
		List<T> ret = new LinkedList<T>();
		for (int i=0; i!=dlm.getSize(); ++i)
			ret.add((T)dlm.getElementAt(i));
		return ret;
	}

	protected void addElement(T element) {
		Setting s = elementToSetting(element);
		if (s!=null){
			SettingComponent sc = s.getGUIElement();
			settingComponents.add(sc);
			settingComponentsMap.put(s,sc);
		}
		((DefaultListModel)theList.getModel()).addElement(element);
	}
	
	@SuppressWarnings("unchecked")
	protected void removeElement(T element) {
		Setting s = elementToSetting(element);
		if (s!=null) {
			SettingComponent sc = s.getGUIElement();
			settingComponents.remove(sc);
			settingComponentsMap.remove(s);
		}
		((DefaultListModel)theList.getModel()).removeElement(element);
		selectionChanged((T)theList.getSelectedValue());
	}

	@SuppressWarnings({ "serial", "unchecked" })
	@Override
	protected Component getSettingComponent() {
		if (pnl==null) {
			theList = new ReorderableJList();
			theList.setSelectionModel(new DefaultListSelectionModel());
			theList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			theList.setVisibleRowCount(10);
			theList.setEnabled(true);
			
			for (T elem : elementsFromSetting(mySetting))
				addElement(elem);
			
			for (SettingComponent sc : settingComponents) {
				Dimension d = sc.getEditorComponent().getPreferredSize();
				minDim.width = Math.max(minDim.width, d.width);
				minDim.height = Math.max(minDim.height, d.height);
			}
			minDim.width += theList.getPreferredSize().width + 20;
			minDim.height = Math.max(theList.getPreferredSize().height, minDim.height) + 50;
			
			// replace the renderer			
			theList.setCellRenderer(new RemoveableElementCellRenderer(theList));
			theList.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					if (evt.getClickCount()==2 && evt.getButton()==MouseEvent.BUTTON1) {
						handleDoubleClickOnElement((T)theList.getModel().getElementAt(theList.getSelectedIndex()));
					}
					if ( evt.getButton() == MouseEvent.BUTTON1 &&  evt.getClickCount() == 1 ) {
						if ( theList.getSelectedValue() != null ) {
							// map coordinates
							int x = evt.getX();
							int y = evt.getY();
							Rectangle r = theList.getCellBounds(theList.getSelectedIndex(),theList.getSelectedIndex());
							x-= r.x;
							y-= r.y;
							// account for the inset        
							Rectangle image = ((RemoveableElementCellRenderer)theList.getCellRenderer()).closer.getBounds();
							if (image.contains(x,y)) { 
								// remove the item
								((DefaultListModel)theList.getModel()).removeElement(theList.getSelectedValue());
								selectionChanged((T)theList.getSelectedValue());
							} else {
								// update the subview
								selectionChanged((T)theList.getSelectedValue());
							}
						}
					}
				}
			});
			addB = new JButton(new AbstractAction("Add") {
				public void actionPerformed(ActionEvent e) {
					T le = getElementToAdd(modelToList(theList.getModel()));
					if (le!=null)
						addElement(le);
				}
			});
			removeB = new JButton(new AbstractAction("Remove selected") {
				public void actionPerformed(ActionEvent e) {
					for (Object o : theList.getSelectedValues())
						removeElement((T)o);
				}					
			});
			pnl_inner = new JPanel(new BorderLayout());
			pnl_inner.add(new JScrollPane(theList), BorderLayout.CENTER);
			JPanel p2 = new JPanel();
			p2.setLayout(new BoxLayout(p2, BoxLayout.LINE_AXIS));
			p2.add(Box.createHorizontalGlue());
			p2.add(removeB);
			p2.add(Box.createHorizontalStrut(5));
			p2.add(addB);
			pnl_inner.add(p2, BorderLayout.SOUTH);
			
			empty_panel = new JPanel(new BorderLayout());
			empty_panel.add(new JLabel("No configurable element is selected"), BorderLayout.CENTER);
			pnl  = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnl_inner, empty_panel);
			
			pnl.setPreferredSize(minDim);
			
			if (theList.getModel().getSize()>0) {
				theList.setSelectedIndex(0);
				selectionChanged((T)theList.getSelectedValue());
			}

		}
		return pnl;
	}

	public void stateChanged(SettingChangeEvent e) {
		if (theList!=null) {
			DefaultListModel m = ((DefaultListModel)theList.getModel());
			m.clear();
			for (T le : elementsFromSetting(mySetting))
				addElement(le);					
		}
	}
	
	protected void selectionChanged(T selected) {
		pnl.setRightComponent(empty_panel);
		if (selected!=null) {
			Setting s = elementToSetting(selected);
			if (s !=null )  {
				SettingComponent sc = settingComponentsMap.get(s);
				if (sc!=null) {
					Component comp = sc.getEditorComponent();
					JScrollPane jsp = new JScrollPane(comp);
					pnl.setRightComponent(jsp);
//					comp.setPreferredSize(minDim);
					comp.invalidate();
					comp.repaint();									
				}
			}
		}
	}

	protected void handleDoubleClickOnElement(T le) {
		// void for overriders
	}

	
	public boolean updateSettingFromEditor(boolean failSilently) {
		if (pnl!=null) {
			// update all element settings
			for (SettingComponent sc : settingComponents) 
				if (!sc.updateSettingFromEditor(failSilently))
					return false;		
			// update list of elements
			mySetting.setElements(modelToList(theList.getModel()));
		}
		return true;
	}

	@SuppressWarnings("serial")
	public class RemoveableElementCellRenderer extends JPanel implements ListCellRenderer {

		ListCellRenderer plcr;
		JLabel closer;

		public RemoveableElementCellRenderer(JList list) {
			plcr = list.getCellRenderer();
			closer = new JLabel("x");
			closer.setMaximumSize(closer.getMinimumSize());
			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			add((Component)plcr);
			add(Box.createHorizontalGlue());
			add(closer);			
		}

		@SuppressWarnings("unchecked")
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {

			plcr.getListCellRendererComponent(list, renderListElement((T)value), index, isSelected, cellHasFocus);
			closer.setText("<html><small><b><font face=Arial color=#333333>x&nbsp;");

			if ( isSelected )
			{
				setBackground( list.getSelectionBackground() );
			}
			else
			{
				setBackground( list.getBackground() );
			}	

			setEnabled( list.isEnabled() );
			setFont( list.getFont() );
			setOpaque( true );

			return this;
		}



	}

}
