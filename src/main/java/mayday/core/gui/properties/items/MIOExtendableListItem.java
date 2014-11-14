package mayday.core.gui.properties.items;

import java.util.LinkedList;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupEvent;
import mayday.core.meta.MIGroupListener;
import mayday.core.meta.MIType;
import mayday.core.meta.gui.MIOExtendableRenderer;

@SuppressWarnings("serial")
public class MIOExtendableListItem extends AbstractListItem implements MIGroupListener {

	//private ArrayList<WrappedMIO> mioList = new ArrayList<WrappedMIO>();
	private MIGroup miGroup;
	private DeleteObjectAction deleteObjectAction = new DeleteObjectAction();
	private boolean deaf=false;
//	private boolean initialized=false;
	
	public MIOExtendableListItem(MIGroup migroup) {		
		super("MIO Extended Objects");
		this.miGroup=migroup;
		this.listField.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				deleteObjectAction.setEnabled(listField.getSelectedIndex()>=-1);
			}
		});
		// speed up via prototypecellvalue is impossible because mios can rendered in different sizes
		// we'll do it anyhow
		//initList((DefaultListModel)getValue());
		if (migroup.size()>0)
			listField.setPrototypeCellValue(migroup.getMIOs().iterator().next().getKey());
		initList((DefaultListModel)getValue());
//		((DefaultListModel)getValue()).add(0, "Computing...");
//		((TitledBorder)this.getBorder()).setTitle(
//				"MIO Extended Objects (computing)");
		initButtons();
		migroup.addMIGroupListener(this);
	}
	
	protected JButton[] getButtons() {
		JButton EditButton = new JButton(editObjectAction);
		JButton DeleteButton = new JButton(deleteObjectAction);
		return new JButton[]{EditButton,DeleteButton};
	}
	
	
	protected class DeleteObjectAction extends AbstractListItem.DeleteObjectAction {
		
		protected DefaultListModel getListModel() {
			return (DefaultListModel)MIOExtendableListItem.this.getValue();
		}
		protected void removeFromContainer(LinkedList<Object> obj) {
			for (Object o : obj)
				miGroup.remove(o);			
		}
		protected void setDeaf(boolean b) {
			MIOExtendableListItem.this.setDeaf(b);
		}		
		
	}
	
//	@Override
//	public void paint(Graphics g) {		
//		super.paint(g);
//		if (!initialized) {		
//			new Thread("Fill list") {
//				public void run() {
//					initList((DefaultListModel)getValue());
//				}
//			}.start();
//		}
//		initialized=true;
//	}
	
	protected void initList(DefaultListModel lm) {
		int position=0;
		lm.clear();
		for (Entry<Object,MIType> mt: miGroup.getMIOs()) { 
			lm.add(position++, mt.getKey());
		}		
	}

	@Override
	protected ListCellRenderer getCellRenderer() {
		return new MIOExtendableRenderer();
	}
	
	public void removeNotify() {
		miGroup.removeMIGroupListener(this);
	    super.removeNotify();
	  }

	protected void setDeaf(boolean d) {
		deaf=d;
	}

	public Object getWatchedObject() {
		return null;
	}

	public void miGroupChanged(MIGroupEvent event) {
		if (!deaf)
			initList((DefaultListModel)getValue());		
	}
}
