package mayday.core.gui.properties.items;

import java.awt.event.ActionEvent;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupEvent;
import mayday.core.meta.MIGroupListener;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.WrappedMIO;
import mayday.core.meta.gui.MIOCellRenderer;

@SuppressWarnings("serial")
public class MIOTableItem extends AbstractTableItem implements MIGroupListener {

	//private ArrayList<WrappedMIO> mioList = new ArrayList<WrappedMIO>();
	private Object mioExtendable;
	private MIManager miManager;
	private AddAnnotationAction addAnnotationAction = new AddAnnotationAction();
	private MIOTableModel miotm;
	private DeleteObjectAction deleteObjectAction = new DeleteObjectAction();
	
	private boolean isDeaf=false; 
	
	public MIOTableItem(Object mioExtendable, MIManager miManager) {		
		super("Meta Information for this object",new MIOTableModel());
		miotm = (MIOTableModel)this.getValue();
		tableField.setDefaultRenderer(Object.class, new MIOCellRenderer());
		this.mioExtendable = mioExtendable;
		this.miManager = miManager;
		tableField.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				deleteObjectAction.setEnabled(tableField.getSelectedRow()!=-1);
			}
		});
		initList(miotm);
		addAnnotationAction.setEnabled(!hasAnnotationMIO());
		initButtons();
		miManager.addListenerForObject(this);
	}
	
	private boolean hasAnnotationMIO(){
		for(Object o : getTableItems()) {
			if (((WrappedMIO)o).getGroup().getMIOType().equals("PAS.MIO.Annotation"))
				return true;			
		}
		return false;
	}

	protected JButton[] getButtons() {
		JButton EditButton = new JButton(editObjectAction);
		JButton DeleteButton = new JButton(deleteObjectAction);
		JButton AddAnnotationButton = new JButton(addAnnotationAction);
		return new JButton[]{AddAnnotationButton,EditButton,DeleteButton};
	}
	

	
	
	protected class DeleteObjectAction extends AbstractTableItem.DeleteObjectAction {
		protected void removeFromContainer(LinkedList<Object> obj) {
			for (Object o : obj) {
				WrappedMIO wm = (WrappedMIO)o;
				wm.getGroup().remove(wm.getMioExtendable());
			}						
		}
		
	}
	
	protected class AddAnnotationAction extends AbstractAction {
		public AddAnnotationAction() {
			super("Add Annotation");	
			setEnabled(!hasAnnotationMIO());
		}
		public void actionPerformed(ActionEvent arg0) {
			if (!hasAnnotationMIO()) {
				MIGroupSelection<MIType> mgs = miManager.getGroupsForType("PAS.MIO.Annotation");
				MIGroup annotationGroup;
				if (mgs.size()==0)
					annotationGroup = miManager.newGroup("PAS.MIO.Annotation", "Annotations");
				else 
					annotationGroup = mgs.get(0);
				setDeaf(true);
				MIType annotationMIO = annotationGroup.add(mioExtendable);
				setDeaf(false);
				MIOTableModel mtm = (MIOTableModel)MIOTableItem.this.getValue();
				mtm.setRowCount(mtm.getRowCount()+1);
				mtm.setValueAt(annotationGroup.getName(), mtm.getRowCount()-1, 0);
				mtm.setValueAt(new WrappedMIO(annotationMIO,annotationGroup,mioExtendable), mtm.getRowCount()-1, 1);
				getSelection().clearSelection();
				getSelection().setSelectionInterval(mtm.getRowCount()-1, mtm.getRowCount()-1);				
				//Open the new MIO for editing;
				editObjectAction.actionPerformed(null);
			}
			setEnabled(false);
		}
		
	}
	
	
	protected void initList(MIOTableModel lm) {
		MIGroupSelection<MIType> mgs = miManager.getGroupsForObject(mioExtendable);
		lm.setRowCount(mgs.size());
		lm.setColumnCount(2);
		int position=0;
		for (MIGroup mg : mgs) { 
			lm.setValueAt(mg.getName()+(mg.getPath().length()>0?" ("+mg.getPath()+")":""),position,0);
			lm.setValueAt(new WrappedMIO(mg.getMIO(mioExtendable),mg,mioExtendable),position++,1);
		}
	}
	
	private static class MIOTableModel extends DefaultTableModel {
		
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}
		
		public String getColumnName(int column) {
			switch(column) {
			case 0: return "MIO Group";
			case 1: return "MIO Value";	
			}
			return null;
		}		
				
//		@SuppressWarnings("unchecked")
//		public void removeRows(List<Integer> rows) {
//			Vector v = this.getDataVector();
//			Vector w = new Vector();
//			for (int i=0; i!=v.size(); ++i)
//				if (!rows.contains(i))  
//					w.add(v);
//			this.setDataVector(w, this.columnIdentifiers);
//		}
		
	}
	
	public Object getWatchedObject() {
		return this.mioExtendable;
	}

	public void miGroupChanged(MIGroupEvent event) {
		if (isDeaf) 
			return;
		MIOTableModel lm = (MIOTableModel)this.getValue();
		//remove or add elements
		switch (event.getChange()) {
		case MIGroupEvent.MIO_ADDED:
			int position = lm.getRowCount();
			lm.setRowCount(position+1);
			MIGroup mg = (MIGroup)event.getSource();
			lm.setValueAt(mg.getName()+(mg.getPath().length()>0?" ("+mg.getPath()+")":""),position,0);
			lm.setValueAt(new WrappedMIO(mg.getMIO(mioExtendable),mg,mioExtendable),position,1);
			break;
		case MIGroupEvent.MIO_REMOVED:
			mg = (MIGroup)event.getSource();
			position=-1;
			for (int i=0; i!=lm.getRowCount(); ++i)
				if (((WrappedMIO)lm.getValueAt(i, 1)).getGroup()==mg)
					position = i;
			if (position>-1)
				lm.removeRow(position);
			break;
		case MIGroupEvent.MIO_REPLACED:
			mg = (MIGroup)event.getSource();
			position=-1;
			for (int i=0; i!=lm.getRowCount(); ++i)
				if (((WrappedMIO)lm.getValueAt(i, 1)).getGroup()==mg)
					position = i;
			if (position>-1) {
				lm.setValueAt(mg.getName()+(mg.getPath().length()>0?" ("+mg.getPath()+")":""),position,0);
				lm.setValueAt(new WrappedMIO(mg.getMIO(mioExtendable),mg,mioExtendable),position,1);
			}
			break;
		};
		addAnnotationAction.setEnabled(!hasAnnotationMIO());
	}
	
	  
	private void setDeaf(boolean deaf) {
		isDeaf = deaf;
	}
	
	public void removeNotify() {
		miManager.removeListenerForObject(this);
	    super.removeNotify();
	  }
	  
	
}
