package mayday.core.gui.properties.items;

import java.util.LinkedList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.probelistmanager.ProbeListManagerEvent;
import mayday.core.probelistmanager.ProbeListManagerListener;
import mayday.core.probelistmanager.gui.cellrenderer.ProbeListCellRenderer;

@SuppressWarnings("serial")
public class ProbeListListItem extends AbstractListItem implements ProbeListManagerListener, ProbeListListener {

	//private ArrayList<WrappedMIO> mioList = new ArrayList<WrappedMIO>();
	private DataSet dataSet;
	private Probe probe;
	private DeleteObjectAction deleteObjectAction = new DeleteObjectAction();
	private boolean deaf=false;
	
	public ProbeListListItem(DataSet ds) {		
		super("Probe Lists");
		this.dataSet=ds;
		this.listField.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				deleteObjectAction.setEnabled(listField.getSelectedIndex()>-1);
			}
		});
		//speed up
		if (ds.getProbeListManager().getObjects().size()>0)
			listField.setPrototypeCellValue((ProbeList)ds.getProbeListManager().getObjects().iterator().next());
		initList((DefaultListModel)getValue());
		initButtons();
		ds.getProbeListManager().addProbeListManagerListener(this);
	}
	
	public ProbeListListItem(Probe pb) {
		super("Probe Lists");
		this.probe=pb;
		this.listField.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				deleteObjectAction.setEnabled(listField.getSelectedIndex()>-1);
			}
		});
		// add listeners for each probe list
		for (Object opl : pb.getProbeLists())
			((ProbeList)opl).addProbeListListener(this);
		//speed up
		if (pb.getProbeLists().size()>0)
			listField.setPrototypeCellValue(pb.getProbeLists().get(0));		
		initList((DefaultListModel)getValue());
		initButtons();		
	}
	
	protected JButton[] getButtons() {
		JButton EditButton = new JButton(editObjectAction);
		JButton DeleteButton = new JButton(deleteObjectAction);
		return new JButton[]{EditButton,DeleteButton};
	}
	

	protected class DeleteObjectAction extends AbstractListItem.DeleteObjectAction {
		
		protected DefaultListModel getListModel() {
			return (DefaultListModel)ProbeListListItem.this.getValue();
		}
		protected void removeFromContainer(LinkedList<Object> obj) {
			if (probe==null) {
				for (Object o : obj) {
					dataSet.getProbeListManager().removeObject(o);
				}
			} else {
				for (Object o : obj) {
					ProbeList pl = (ProbeList)o;
					pl.removeProbe(probe);
					pl.removeProbeListListener(ProbeListListItem.this);
				}
			}
		}
		protected void setDeaf(boolean b) {
			ProbeListListItem.this.setDeaf(b);
		}		
		
	}
			
	
	protected void initList(DefaultListModel lm) {
		int position=0;
		lm.clear();
		if (probe==null) { // work on DataSet
			for (Object plo: dataSet.getProbeListManager().getObjects()) {
				lm.add(position++, plo);
			}
		} else {
			for (Object plo: probe.getProbeLists()) {
				lm.add(position++, plo);
			}
		}
	}

	@Override
	protected ListCellRenderer getCellRenderer() {
		return new ProbeListCellRenderer();
	}

	public void probeListManagerChanged(ProbeListManagerEvent event) {
		if (!deaf)
			if (event.getChange()==ProbeListManagerEvent.CONTENT_CHANGE)
				initList((DefaultListModel)getValue());		
	}
	
	public void removeNotify() {
		if (probe==null)
			dataSet.getProbeListManager().removeProbeListManagerListener(this);
		else
			for (Object opl : probe.getProbeLists())
				((ProbeList)opl).removeProbeListListener(this);
	    super.removeNotify();
	  }

	protected void setDeaf(boolean t) {
		deaf=t;
	}

	public void probeListChanged(ProbeListEvent event) {
		if (!deaf) // might have removed us, but then again maybe not
			initList((DefaultListModel)getValue());
	}
	
}
