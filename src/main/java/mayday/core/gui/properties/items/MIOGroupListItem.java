package mayday.core.gui.properties.items;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupEvent;
import mayday.core.meta.MIGroupListener;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIManagerEvent;
import mayday.core.meta.MIManagerListener;
import mayday.core.meta.gui.MIManagerDialog;

@SuppressWarnings("serial")
public class MIOGroupListItem extends AbstractListItem implements MIManagerListener, MIGroupListener {

	private MIManager mimanager;
	List<MIGroup> migroups;
	private DeleteObjectAction deleteObjectAction = new DeleteObjectAction();
	private boolean deaf=false;
	
	public MIOGroupListItem(MIManager miManager) {		
		super("Meta Information Groups");
		mimanager = miManager;
		this.listField.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				deleteObjectAction.setEnabled(listField.getSelectedIndex()>=-1);
			}
		});
		initList((DefaultListModel)getValue());
		initButtons();
		mimanager.addMIManagerListener(this);	
	}
	
	public MIOGroupListItem(List<MIGroup> miGroups) {		
		super("Meta Information Groups");
		migroups = miGroups;
		deleteObjectAction.setEnabled(false);
		initList((DefaultListModel)getValue());
		initButtons();
		for (MIGroup mg : migroups) {
			mg.addMIGroupListener(this);
		}
	}
	
	protected JButton[] getButtons() {
		JButton EditButton = new JButton(editObjectAction);
		JButton DeleteButton = new JButton(deleteObjectAction);
		JButton MIMAButton = new JButton(new AbstractAction("MI Manager...") {

			public void actionPerformed(ActionEvent e) {
				MIManagerDialog mmd = new MIManagerDialog(mimanager);
				if (parent!=null)
					mmd.setModal(parent.isModal());
				mmd.setVisible(true);
			}
			
		});
		return new JButton[]{EditButton,DeleteButton,MIMAButton};
	}
	
	protected class DeleteObjectAction extends AbstractListItem.DeleteObjectAction {
		
		protected DefaultListModel getListModel() {
			return (DefaultListModel)MIOGroupListItem.this.getValue();
		}
		protected void removeFromContainer(LinkedList<Object> obj) {
			for (Object o : obj)
				mimanager.removeGroup((MIGroup)o);			
		}
		protected void setDeaf(boolean b) {
			MIOGroupListItem.this.setDeaf(b);
		}		
		
	}
	
	
	protected void initList(DefaultListModel lm) {
		int position=0;
		lm.clear();
		if (mimanager==null) { // only list
			for (MIGroup mg : migroups) {
				lm.add(position++, mg);
			}
		} else {
			for (MIGroup mg: mimanager.getGroups()) {
				lm.add(position++, mg);
			}
		}
	}

	@Override
	protected ListCellRenderer getCellRenderer() {
		return new DefaultListCellRenderer();
	}

	
	public void removeNotify() {
		if (mimanager!=null)
			mimanager.removeMIManagerListener(this);
		else
			for (MIGroup mg : migroups)
				mg.removeMIGroupListener(this);
	    super.removeNotify();
	  }


	
	protected void setDeaf(boolean d) {
		deaf=d;
	}


	public void miManagerChanged(MIManagerEvent event) {
		if (!deaf)
			initList((DefaultListModel)getValue());
	}

	public Object getWatchedObject() {
		return null;
	}

	public void miGroupChanged(MIGroupEvent event) {
		if (!deaf)
			initList((DefaultListModel)getValue());
	}
	
}
