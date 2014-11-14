package mayday.core.gui.properties.items;

import java.util.LinkedList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mayday.core.MasterTable;
import mayday.core.MasterTableEvent;
import mayday.core.MasterTableListener;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.gui.ProbeCellRenderer;

@SuppressWarnings("serial")
public class ProbeListItem extends AbstractListItem implements ProbeListListener, MasterTableListener {

	//private ArrayList<WrappedMIO> mioList = new ArrayList<WrappedMIO>();
	private MasterTable masterTable;
	private ProbeList probeList;
	private DeleteObjectAction deleteObjectAction = new DeleteObjectAction();
	private boolean deaf=false;
	
	public ProbeListItem(MasterTable mt, ProbeList pl) {		
		super("Probes");
		this.masterTable=mt;
		this.probeList=pl;
		this.listField.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				deleteObjectAction.setEnabled(listField.getSelectedIndex()>=-1);
			}
		});
		//speed up
		Probe prototype = null;
		if (pl!=null) {
			if (pl.getNumberOfProbes()>0)
				prototype = pl.getAllProbes().iterator().next();
		} else {
			if (mt.getNumberOfProbes()>0)
				prototype =  (Probe)mt.getProbes().values().iterator().next();
		}
		if (prototype!=null)
			listField.setPrototypeCellValue(prototype);
		// build list
		initList((DefaultListModel)getValue());
		initButtons();
		if (pl!=null)
			pl.addProbeListListener(this);
		else
			mt.addMasterTableListener(this);
	}
	
	protected JButton[] getButtons() {
		JButton EditButton = new JButton(editObjectAction);
		JButton DeleteButton = new JButton(deleteObjectAction);
		return new JButton[]{EditButton,DeleteButton};
	}
	
	

	protected class DeleteObjectAction extends AbstractListItem.DeleteObjectAction {
		
		protected DefaultListModel getListModel() {
			return (DefaultListModel)ProbeListItem.this.getValue();
		}
		protected void removeFromContainer(LinkedList<Object> obj) {
			if (probeList!=null) {
				for (Object o : obj)
					probeList.removeProbe((Probe)o);
			} else {
				for (Object o : obj)
					masterTable.removeProbe(((Probe)o).getName());
			}
		}
		protected void setDeaf(boolean b) {
			ProbeListItem.this.setDeaf(b);
		}		
		
	}		
	
	protected void initList(DefaultListModel lm) {
		int position=0;
		lm.clear();
		if (probeList==null) { // whole mastertable
			for (Object pbo: masterTable.getProbes().values()) {
				lm.add(position++, pbo);
			}
		} else {
			for (Probe pb: probeList.getAllProbes()) {
				lm.add(position++, pb);
			}
		}
	}

	@Override
	protected ListCellRenderer getCellRenderer() {
		return new ProbeCellRenderer();
	}

	public void probeListChanged(ProbeListEvent event) {
		if (!deaf)
			initList((DefaultListModel)getValue());
	}
	
	public void removeNotify() {
		if (probeList!=null)
			probeList.removeProbeListListener(this);
		else
			masterTable.removeMasterTableListener(this);
	    super.removeNotify();
	  }

	public void masterTableChanged(MasterTableEvent event) {
		if (!deaf)
			initList((DefaultListModel)getValue());		
	}
	
	protected void setDeaf(boolean d) {
		deaf=d;
	}
}
