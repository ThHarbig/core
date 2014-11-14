package mayday.genetics.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

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

import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.Species;
import mayday.genetics.basic.chromosome.Chromosome;

@SuppressWarnings("serial")
public class ChromosomePanel extends JPanel {
	
	protected ChromosomeSetContainer csc;
	protected Species s;
	protected DefaultListModel dlm;
	protected ListSelectionModel sel;
	protected TreeSet<Chromosome> chromes;
	protected boolean editable = true;
	
	public ChromosomePanel(ChromosomeSetContainer csc, Species s) {
		this.csc = csc;
		this.s = s;
		dlm = new DefaultListModel();
		JList l = new JList(dlm);
		l.setCellRenderer(new CellRenderer());
		sel = l.getSelectionModel();
		
		setLayout(new BorderLayout());
		add(new JScrollPane(l), BorderLayout.CENTER);
		
		l.addMouseListener(new SMouseListener());
		
		add(new JLabel("Chromosomes"), BorderLayout.NORTH);

		update();
	}
	
	public void setEditable(boolean ed) {
		editable = ed;
	}

	
	protected void update() {
		
		chromes  = new TreeSet<Chromosome>(new Comparator<Chromosome>() {

			@Override
			public int compare(Chromosome o1, Chromosome o2) {
				int internal = -o1.compareToBySize(o2);
				if (internal==0)
					return o1==o2?0:-1;
				else return internal;
			}
			
		});
		if (csc!=null && s!=null)
			for (Chromosome c : csc.getAllChromosomes()) {
				if (c.getSpecies().equals(s))
					chromes.add(c);
			}
		
		dlm.clear();

		for (Chromosome c : chromes)
			dlm.addElement(c);
				
		if (dlm.size()>0)
			sel.setSelectionInterval(0, 0);
	}
	
	public void addListSelectionLister(ListSelectionListener sell) {
		sel.addListSelectionListener(sell);
	}

	
	protected class RenameAction extends AbstractAction {
		List<Chromosome> l;
		public RenameAction(List<Chromosome> ls) {
			super("Rename...");
			l = ls;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			String fail="";
			for (Chromosome c : l) {
				String newName = (String)JOptionPane.showInputDialog(ChromosomePanel.this, 
						"New name for "+c.getId(), "Change chromosome name", JOptionPane.QUESTION_MESSAGE, null, null, c.getId());
				if (newName==null)
					continue;
				boolean unused=true;
				for (int i=0; i!=dlm.size(); ++i) {
					if (((Chromosome)dlm.getElementAt(i)).getId().equals(newName) && dlm.getElementAt(i)!=c)
						unused=false;
				}
				if (unused) {
					c.setId(newName);
				} else {
					fail+="\n"+c.getId()+" could not be renamed to "+newName+" - name exists already.";
				}
			}
			if (fail.length()>0) {
				JOptionPane.showMessageDialog(ChromosomePanel.this, fail, "Renaming failed", JOptionPane.ERROR_MESSAGE, null);
			}
			update();
		}
	}
	
	
	protected class SetLengthAction extends AbstractAction {
		List<Chromosome> l;
		public SetLengthAction(List<Chromosome> ls) {
			super("Change length...");
			l = ls;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			String fail="";
			for (Chromosome c : l) {
				String newLen = (String)JOptionPane.showInputDialog(ChromosomePanel.this, 
						"New length for "+c.getId(), "Change chromosome length", JOptionPane.QUESTION_MESSAGE, null, null, c.getLength());
				if (newLen==null)
					continue;
				try {
					long newL = Long.parseLong(newLen);
					c.setLength(newL);
				} catch (Exception exc) {
					fail+="Invalid length specified for "+c.getId()+": \'"+newLen+"\'";
				}
			}
			if (fail.length()>0) {
				JOptionPane.showMessageDialog(ChromosomePanel.this, fail, "Renaming failed", JOptionPane.ERROR_MESSAGE, null);
			}
			update();
		}
	}
	
	protected class CellRenderer extends DefaultListCellRenderer {
		
	    public Component getListCellRendererComponent(
	            JList list,
	    	Object value,
	            int index,
	            boolean isSelected,
	            boolean cellHasFocus)
	        {
	    	
	    	long len = ((Chromosome)value).getLength();
	    	
	    	NumberFormatter nf = new NumberFormatter(NumberFormat.getInstance());
	    	String ll=""+len;
			try {
				ll = nf.valueToString(len);
			} catch (ParseException e) {
				// nothing to do
			}
	    	
	    	String s = "<html>"+((Chromosome)value).getId()+"&nbsp;&nbsp;<small><font color=#888888>["+ll+" bp]";
	    	
	    	return super.getListCellRendererComponent(list, s, index, isSelected, cellHasFocus);

	        }
		
	}
	
	public void setParent(SpeciesPanel parent) {
		parent.addListSelectionLister(new SSelectionListener(parent));
		updateFromParent(parent);
	}
	
	protected class SMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent me) {
			List<Chromosome> selected = getSelectedChromosome();
			if (selected.size()==0)
				return;
			if (me.getButton()==MouseEvent.BUTTON3) {
				JPopupMenu jpm = new JPopupMenu();
				jpm.add("Class: "+selected.get(0).getClass().getSimpleName());
				if (editable) {					
					jpm.add(new RenameAction(selected));
					jpm.add(new SetLengthAction(selected));
				}						
				JScrollPane jsp = ((JScrollPane)getComponent(0));
				Component parent = jsp.getViewport().getView(); 
				jpm.show(parent, me.getX(), me.getY());				
			}
		}
	}
	
	public class SSelectionListener implements ListSelectionListener {

		protected SpeciesPanel sp;
		
		public SSelectionListener(SpeciesPanel p) {
			sp=p;
		}
		
		public void valueChanged(ListSelectionEvent e) {
			updateFromParent(sp);
		}
		
	}
	
	protected void updateFromParent(SpeciesPanel sp) {
		List<Species> ls = sp.getSelectedSpecies();
		ChromosomePanel.this.csc = sp.csc;
		if (ls.size()>0)
			ChromosomePanel.this.s = ls.get(0);
		else 
			ChromosomePanel.this.s = null;
		update();
	}


	public List<Chromosome> getSelectedChromosome() {
		if (sel.getMinSelectionIndex()==-1)
			return Collections.emptyList();
		List<Chromosome> selected = new LinkedList<Chromosome>();
		for (int i=sel.getMinSelectionIndex(); i<=sel.getMaxSelectionIndex(); ++i) {
			selected.add((Chromosome)dlm.getElementAt(i));
		}
		return selected;
	}

}
