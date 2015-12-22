package mayday.core.gui.properties.items;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;

import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;

@SuppressWarnings("serial")
public abstract class AbstractListItem extends AbstractPropertiesItem {

	protected JList listField = new JList(new DefaultListModel());
	protected EditObjectAction editObjectAction = new EditObjectAction();
	private String Caption; 

	public AbstractListItem(String caption) {
		super(caption);
		Caption=caption;
		JScrollPane jsp = new JScrollPane(listField);		
		this.add(jsp, BorderLayout.CENTER);
		listField.setCellRenderer(getCellRenderer());
		listField.addMouseListener(getMouseListener());
		listField.getModel().addListDataListener(new ListDataListener() {
			public void contentsChanged(ListDataEvent arg0) {
				((TitledBorder)AbstractListItem.this.getBorder()).setTitle(
						Caption+" ("+listField.getModel().getSize()+")");
				AbstractListItem.this.repaint();
			}
			public void intervalAdded(ListDataEvent arg0) {
				contentsChanged(arg0);			}
			public void intervalRemoved(ListDataEvent arg0) {
				contentsChanged(arg0);
			}
		});
		this.listField.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				editObjectAction.setEnabled(listField.getSelectedIndex()>-1);
			}
		});
	}

	protected void initButtons() {
		JButton[] listButtons = getButtons();
		if (listButtons!=null) {
			JPanel buttonPanel = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,0,0,
					GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0,0);
			for (JButton b : listButtons) {
				buttonPanel.add(b, gbc);
				gbc.gridy++;
			}
			gbc.weighty=1.0;
			buttonPanel.add(Box.createVerticalGlue(),gbc);
			this.add(buttonPanel, BorderLayout.EAST);
		}
	}


	protected MouseListener getMouseListener() {
		return new ObjectListMouseListener();
	}

	protected abstract ListCellRenderer getCellRenderer();

	protected JButton[] getButtons() {
		return null;
	}

	protected Object[] getListItems() {
		return ((DefaultListModel)listField.getModel()).toArray();
	}

	public Object getValue() {
		return listField.getModel();
	}	

	@Override
	public boolean hasChanged() {
		return false; // no use here
	}

	@Override
	public void setValue(Object value) {
		listField.setModel((ListModel)value);
	}

	public ListSelectionModel getSelection() {
		return listField.getSelectionModel();
	}

	protected class EditObjectAction extends AbstractAction {
		public EditObjectAction() {			
			super("Edit");		
			setEnabled(false);
		}
		public void actionPerformed(ActionEvent arg0) {
			AbstractPropertiesDialog dlg;
			Object[] selected = listField.getSelectedValues();
			dlg = PropertiesDialogFactory.createDialog(selected);
			if (parent!=null)
				dlg.setModal(parent.isModal());
			dlg.setVisible(true);
		}		
	}

	protected class ObjectListMouseListener extends MouseInputAdapter {      
		public void mouseClicked( MouseEvent e ) {       
			if ( e.getButton() == MouseEvent.BUTTON1 ) 
				if ( e.getClickCount() == 2 ) 
					if ( listField.getSelectedValue() != null )
						editObjectAction.actionPerformed(null);
		}
	}  
	
	protected abstract class DeleteObjectAction extends AbstractAction {
		public DeleteObjectAction() {
			super("Delete");	
			setEnabled(false);
		}
		protected abstract DefaultListModel getListModel();
		protected abstract void setDeaf(boolean b);
		protected abstract void removeFromContainer(LinkedList<Object> obj);
		
		public void actionPerformed(ActionEvent arg0) {
			ListSelectionModel lsm = getSelection();
			Object[] listItems = getListItems();
			LinkedList<Object> objectsToDelete = new LinkedList<Object>();
			for (int i = 0; i!= listItems.length; ++i)
				if (lsm.isSelectedIndex(i))
					objectsToDelete.add(listItems[i]);
						
			DefaultListModel lm = getListModel(); 
			setDeaf(true);
			removeFromContainer(objectsToDelete);
			// update the model
			for (Object o : objectsToDelete)
				lm.removeElement(o);
			setDeaf(false);
		}		
		
	}
}
