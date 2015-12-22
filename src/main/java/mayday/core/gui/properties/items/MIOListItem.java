package mayday.core.gui.properties.items;

import java.util.HashSet;
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
import mayday.core.meta.WrappedMIO;
import mayday.core.meta.gui.MIOCellRenderer;

@SuppressWarnings("serial")
public class MIOListItem extends AbstractListItem implements MIGroupListener {

	//private ArrayList<WrappedMIO> mioList = new ArrayList<WrappedMIO>();
	private MIGroup miGroup;
	private DeleteObjectAction deleteObjectAction = new DeleteObjectAction();
	private boolean deaf=false;
//	private boolean initialized=false;
	
	public MIOListItem(MIGroup migroup) {		
		super("Meta Information Objects");
		this.miGroup=migroup;
		this.listField.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				deleteObjectAction.setEnabled(listField.getSelectedIndex()>=-1);
			}
		});
//		if (migroup.size()>0)
//			listField.setPrototypeCellValue(migroup.getMIOs().iterator().next().getKey());
		// speed up via prototypecellvalue is impossible because mios can rendered in different sizes
		// we'll do it anyways, because currently all mio renderers are about the same size
		//GJ: don't do it, because sizes are not rendered correctly!
		initList((DefaultListModel)getValue());
//		((DefaultListModel)getValue()).add(0, "Computing...");
//		((TitledBorder)this.getBorder()).setTitle(
//				"Meta Information Objects (computing)");
		initButtons();
		migroup.addMIGroupListener(this);
	}
	
	protected JButton[] getButtons() {
		JButton EditButton = new JButton(editObjectAction);
		JButton DeleteButton = new JButton(deleteObjectAction);
		return new JButton[]{EditButton,DeleteButton};
	}
	
//	@Override
//	public void paint(Graphics g) {		
//		super.paint(g);
////		if (!initialized) {		
////			new Thread("Fill list") {
////				public void run() {
////					initList((DefaultListModel)getValue());
////				}
////			}.start();
////		}
////		initialized=true;
//	}
//			
	
	protected class DeleteObjectAction extends AbstractListItem.DeleteObjectAction {
		
		protected DefaultListModel getListModel() {
			return (DefaultListModel)MIOListItem.this.getValue();
		}
		@SuppressWarnings("deprecation")
		protected void removeFromContainer(LinkedList<Object> obj) {
			for (Object o : obj)
				for (Object o2 : miGroup.getObjectsForMIO(((WrappedMIO)o).getMio()))
					miGroup.remove(o2);
		}
		protected void setDeaf(boolean b) {
			MIOListItem.this.setDeaf(b);
		}		
		
	}
	
	
	protected void initList(DefaultListModel lm) {
		HashSet<MIType> uniqueMIOs = new HashSet<MIType>();
		int position=0;
		lm.clear();
		for (Entry<Object,MIType> mt: miGroup.getMIOs()) 
			uniqueMIOs.add(mt.getValue());
		for (MIType mt : uniqueMIOs) {
			lm.add(position++, new WrappedMIO(mt,miGroup,null));

		}		

	}

	@Override
	protected ListCellRenderer getCellRenderer() {
		return new MIOCellRenderer();
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
