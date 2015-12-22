package mayday.genetics.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.NumberFormatter;

import mayday.core.structures.maps.MultiTreeMap;
import mayday.genetics.basic.ChromosomeSet;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.Species;
import mayday.genetics.basic.chromosome.Chromosome;

@SuppressWarnings("serial")
public class SpeciesPanel extends JPanel {
	
	protected ChromosomeSetContainer csc;
	protected DefaultListModel dlm;
	protected ListSelectionModel sel;
	protected MultiTreeMap<Species, Chromosome> species;
	protected boolean editable = true;
	
	public SpeciesPanel(ChromosomeSetContainer csc) {
		this.csc = csc;
		dlm = new DefaultListModel();
		JList l = new JList(dlm);
		l.setCellRenderer(new CellRenderer());
		sel = l.getSelectionModel();
		
		setLayout(new BorderLayout());
		add(new JScrollPane(l), BorderLayout.CENTER);
		
		l.addMouseListener(new SMouseListener());
		
		add(new JLabel("Species"), BorderLayout.NORTH);
		
		update();
	}
	
	public void setEditable(boolean ed) {
		editable = ed;
	}
	
	public void setParent(CSCPanel parent) {
		parent.addListSelectionLister(new SSelectionListener(parent));
		updateFromParent(parent);
	}
	
	protected void update() {
		
		species = new MultiTreeMap<Species, Chromosome>();
		if (csc!=null)
			for (Chromosome s : csc.getAllChromosomes()) {
				species.put(s.getSpecies(), s);
			}
		
		dlm.clear();

		for (Species s : species.keySet())
			dlm.addElement(s);
		
		if (dlm.size()>0)
			sel.setSelectionInterval(0, 0);
		
	}
	
	public void addListSelectionLister(ListSelectionListener sell) {
		sel.addListSelectionListener(sell);
	}

	
	protected class CellRenderer extends DefaultListCellRenderer {
		
	    public Component getListCellRendererComponent(
	            JList list,
	    	Object value,
	            int index,
	            boolean isSelected,
	            boolean cellHasFocus)
	        {
	    	
	    	List<Chromosome> lcr = species.get((Species)value);
	    	
	    	long len=0;
	    	for (Chromosome c : lcr) {
	    		if (len>-1)
	    			len+=c.getLength();
	    	}
	    	if (len==0)
	    		len = -1;
	    	
	    	NumberFormatter nf = new NumberFormatter(NumberFormat.getInstance());
	    	String ll=""+len;
			try {
				ll = nf.valueToString(len);
			} catch (Exception e) {}
	    	
	    	String s = "<html>"+((Species)value).getName()+"&nbsp;&nbsp;<small><font color=#888888>["+lcr.size()+" chromosomes, "+ll+" bp]";
	    	
	    	return super.getListCellRendererComponent(list, s, index, isSelected, cellHasFocus);

	        }
		
	}
	
	protected class SMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent me) {
			if (!editable)
				return;
			List<Species> selected = getSelectedSpecies();
			if (selected.size()==0)
				return;
			if (me.getButton()==MouseEvent.BUTTON3) {
				JPopupMenu jpm = new JPopupMenu();
//				jpm.add("Class: "+selected.get(0).getClass().getSimpleName());
//				jpm.add(new RemoveAction(selected));
				jpm.add(new RenameAction(selected));
				jpm.add(new MergeAction(selected));
				JScrollPane jsp = ((JScrollPane)getComponent(0));
				Component parent = jsp.getViewport().getView(); 
				jpm.show(parent, me.getX(), me.getY());		
			}
		}
	}
	
	protected class RenameAction extends AbstractAction {
		List<Species> l;
		public RenameAction(List<Species> ls) {
			super("Rename...");
			l = ls;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			String fail="";
			for (Species s : l) {
				String newName = (String)JOptionPane.showInputDialog(SpeciesPanel.this, 
						"New name for "+s.getName(), "Change species name", JOptionPane.QUESTION_MESSAGE, null, null, s.getName());
				if (newName==null)
					continue;
				boolean unused=true;
				for (int i=0; i!=dlm.size(); ++i) {
					if (((Species)dlm.getElementAt(i)).getName().equals(newName) && dlm.getElementAt(i)!=s)
						unused=false;
				}
				if (unused) {
					s.setName(newName);
				} else {
					fail+="\n"+s.getName()+" could not be renamed to "+newName+" - name exists already.";
				}
			}
			if (fail.length()>0) {
				JOptionPane.showMessageDialog(SpeciesPanel.this, fail, "Renaming failed", JOptionPane.ERROR_MESSAGE, null);
			}
			update();
		}
	}
	
	
	protected class MergeAction extends AbstractAction {
		List<Species> l;
		public MergeAction(List<Species> ls) {
			super("Merge");
			l = ls;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if (l.size()<2)
				return;
			Species newMain = l.get(0);
			for (int i=1; i<l.size(); ++i) {
				ChromosomeSet cs = csc.getChromosomes(l.get(i));
				for (Chromosome c : cs.getAllChromosomes()) {
					c.setSpecies(newMain);
				}
			}
			update();
		}
	}
	
	
//	protected class RemoveAction extends AbstractAction {
//		List<Species> l;
//		public RemoveAction(List<Species> ls) {
//			super("Remove");
//			l = ls;
//		}
//	}
	
	public class SSelectionListener implements ListSelectionListener {

		protected CSCPanel cscp;
		
		public SSelectionListener(CSCPanel p) {
			cscp=p;
		}
		
		public void valueChanged(ListSelectionEvent e) {
			updateFromParent(cscp);
		}
		
	}
	
	public void updateFromParent(CSCPanel cscp) {
		List<ChromosomeSetContainer> lcsc = cscp.getSelectedCSC();
		if (lcsc.size()>0)
			SpeciesPanel.this.csc = lcsc.get(0);
		else 
			SpeciesPanel.this.csc = null;
		update();
	}


	public List<Species> getSelectedSpecies() {
		if (sel.getMinSelectionIndex()==-1)
			return Collections.emptyList();
		List<Species> selected = new LinkedList<Species>();
		for (int i=sel.getMinSelectionIndex(); i<=sel.getMaxSelectionIndex(); ++i) {
			selected.add((Species)dlm.getElementAt(i));
		}
		return selected;
	}
	
	public ChromosomeSetContainer getCSC() {
		return csc;
	}
	
}
