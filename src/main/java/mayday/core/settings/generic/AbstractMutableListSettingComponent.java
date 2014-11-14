package mayday.core.settings.generic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import mayday.core.gui.components.ReorderableJList;
import mayday.core.meta.MIType;
import mayday.core.meta.types.StringListMIO;
import mayday.core.settings.AbstractSettingComponent;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;

public abstract class AbstractMutableListSettingComponent<T extends Setting, ListElement>  extends AbstractSettingComponent<T> {
	
	protected JList theList;
	protected JButton addB, removeB;
	protected JPanel pnl;
	
	protected abstract String elementToString(ListElement element);
	protected abstract String renderListElement(ListElement element);
	protected abstract String renderToolTip(ListElement element);
	protected abstract ListElement getElementToAdd(Collection<ListElement> alreadyPresent);
	protected abstract Iterable<ListElement> elementsFromSetting(T mySetting);
	
	public AbstractMutableListSettingComponent(T s) {
		super(s);
	}

	@Override
	protected String getCurrentValueFromGUI() {
		List<String> fr = new LinkedList<String>();
		for (ListElement le : modelToList(theList.getModel()))
			fr.add(elementToString(le));
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
	
	

	@SuppressWarnings("serial")
	@Override
	protected Component getSettingComponent() {
		if (pnl==null) {
			theList = new ReorderableJList();
			theList.setSelectionModel(new DefaultListSelectionModel());
			theList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			theList.setVisibleRowCount(10);
			theList.setEnabled(true);
			stateChanged(null);		
			// replace the renderer
//			final ListCellRenderer prevRenderer = theList.getCellRenderer();			
			theList.setCellRenderer(new RemoveableElementCellRenderer());
			theList.addMouseListener(new MouseAdapter() {
				@SuppressWarnings("unchecked")
				public void mouseClicked(MouseEvent evt) {
					if (evt.getClickCount()==2 && evt.getButton()==MouseEvent.BUTTON1) {
						handleDoubleClickOnElement((ListElement)theList.getModel().getElementAt(theList.getSelectedIndex()));
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
								((DefaultListModel)theList.getModel()).removeElement(theList.getSelectedValue());
							}
						}
					}
				}
			});
			addB = new JButton(new AbstractAction("Add") {
				public void actionPerformed(ActionEvent e) {
					ListElement le = getElementToAdd(modelToList(theList.getModel()));
					if (le!=null)
						((DefaultListModel)theList.getModel()).addElement(le);
				}
			});
			removeB = new JButton(new AbstractAction("Remove selected") {
				public void actionPerformed(ActionEvent e) {
					for (Object o : theList.getSelectedValues())
						((DefaultListModel)theList.getModel()).removeElement(o);
				}					
			});
			pnl = new JPanel(new BorderLayout());
			pnl.add(new JScrollPane(theList), BorderLayout.CENTER);
			JPanel p2 = new JPanel();
			p2.setLayout(new BoxLayout(p2, BoxLayout.LINE_AXIS));
			p2.add(Box.createHorizontalGlue());
			p2.add(removeB);
			p2.add(Box.createHorizontalStrut(5));
			p2.add(addB);
			pnl.add(p2, BorderLayout.SOUTH);
		}
		return pnl;
	}

	public void stateChanged(SettingChangeEvent e) {
		if (theList!=null) {
			DefaultListModel m = ((DefaultListModel)theList.getModel());
			m.clear();
			for (ListElement le : elementsFromSetting(mySetting))
				m.addElement(le);					
		}
	}
	
	protected void handleDoubleClickOnElement(ListElement le) {
		// void for overriders
	}
	

	
	@SuppressWarnings("serial")
	public class RemoveableElementCellRenderer extends JPanel implements ListCellRenderer {

		DefaultListCellRenderer plcr;
		JLabel closer;
		
		public RemoveableElementCellRenderer() {
			plcr = new DefaultListCellRenderer();
			closer = new JLabel("x");
			closer.setMaximumSize(closer.getMinimumSize());
			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			add(plcr);
			add(Box.createHorizontalGlue());
			add(closer);
		}
		
		@SuppressWarnings("unchecked")
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {

			plcr.getListCellRendererComponent(list, renderListElement((ListElement)value), index, isSelected, cellHasFocus);
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
