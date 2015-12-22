package mayday.genetics.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

import mayday.genetics.basic.ChromosomeSetContainer;

@SuppressWarnings("serial")
public class CSCPanel extends JPanel {
	
	protected ChromosomeSetContainer[] cscs;
	protected DefaultListModel dlm;
	protected ListSelectionModel sel;
	protected boolean editable = true;
	
	public CSCPanel(ChromosomeSetContainer... csc) {
		this.cscs = csc;	
		init();
	}
	
	public CSCPanel(Collection<ChromosomeSetContainer> csc) {
		this.cscs = csc.toArray(new ChromosomeSetContainer[0]);	
		init();
	}
	
	public void setEditable(boolean ed) {
		editable = ed;
	}


	
	protected void init() {
		dlm = new DefaultListModel();
		JList l = new JList(dlm);
		l.setCellRenderer(new CellRenderer());
		sel = l.getSelectionModel();

		for (ChromosomeSetContainer csc : cscs)
			dlm.addElement(csc);
		
		
		if (dlm.size()>0)
			sel.setSelectionInterval(0, 0);

		
		setLayout(new BorderLayout());
		add(new JScrollPane(l), BorderLayout.CENTER);
		
//		l.addMouseListener(new SMouseListener());
		
		add(new JLabel("Chromosome Set Containers"), BorderLayout.NORTH);
	}
	
	protected class CellRenderer extends DefaultListCellRenderer {
		
	    public Component getListCellRendererComponent(
	            JList list,
	    	Object value,
	            int index,
	            boolean isSelected,
	            boolean cellHasFocus)
	        {
	    	
	    	int size = ((ChromosomeSetContainer)value).keySet().size();
	    	
	    	String name = value.getClass().getSimpleName();
	    	if (value==ChromosomeSetContainer.getDefault()) {
	    		name = "** Mayday Default CSC **";
	    	}
	    	
	    	String s = "<html>"+name+"&nbsp;&nbsp;<small><font color=#888888>["+size+" species]";
	    	
	    	return super.getListCellRendererComponent(list, s, index, isSelected, cellHasFocus);

	        }
		
	}
	
	public void addListSelectionLister(ListSelectionListener sell) {
		sel.addListSelectionListener(sell);
	}
	
	protected class SMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent me) {
			// nothing to do so far
			
			/*
			List<ChromosomeSetContainer> selected = getSelectedCSC();
			if (selected.size()==0)
				return;
			if (me.getButton()==MouseEvent.BUTTON3) {
				JPopupMenu jpm = new JPopupMenu();
				jpm.show(CSCPanel.this, me.getX(), me.getY()+20);
				
			}
			*/
		}
	}

	public List<ChromosomeSetContainer> getSelectedCSC() {
		if (sel.getMinSelectionIndex()==-1)
			return Collections.emptyList();
		List<ChromosomeSetContainer> selected = new LinkedList<ChromosomeSetContainer>();
		for (int i=sel.getMinSelectionIndex(); i<=sel.getMaxSelectionIndex(); ++i) {
			selected.add((ChromosomeSetContainer)dlm.getElementAt(i));
		}
		return selected;
	}

}
