package mayday.core;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import mayday.core.gui.listdialog.BaseFrame;

public class ExperimentNamer {

	protected static HashMap<Experiment, String> askUser = new HashMap<Experiment, String>();

	protected static DelayedUpdateTask userQuery = new DelayedUpdateTask("Experiment Namer", 5000) {

		@Override
		protected void performUpdate() {
			synchronized (ExperimentNamer.class) {
				if (askUser.isEmpty())
					return;
				final NamingFrame nf = new NamingFrame(askUser);				
				askUser.clear();
				nf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				nf.setVisible(true);
			}
		}

		@Override
		protected boolean needsUpdating() {
			return true;
		}
	};

	public static synchronized void ensureNameUniqueness(Experiment... lists) {

		// Find unique names for all lists and insert them right away

		// Lists that needed changing are presented to the user.
		// Choices for suffixes are:
		// - current time
		// - prime ' / any character
		// - number in brackets (x2)


		// default naming scheme is "x (time)"
		NameMapping additionalNames = new NameMapping();
		
		for (Experiment pl : lists) {
			String plname = pl.getName();
			if (plname == null || plname.equals("")) {
				plname = "Unnamed Experiment";
				askUser.put(pl, plname);
			}
			MasterTable manager = getManager(pl);
			if (!nameOK(pl, plname, manager, additionalNames.getSet(manager))) {
				DateFormat df = DateFormat.getTimeInstance();

				String suggestion = plname + " ("+df.format(new Date())+")";
				while (!nameOK(pl, suggestion, manager, additionalNames.getSet(manager))) {
					suggestion+="'";
				}
				plname = suggestion;
				askUser.put(pl, pl.getName());
			}
			pl.setName0(plname);
			additionalNames.getSet(manager).add(plname);
		}

		if (!askUser.isEmpty()) {
			userQuery.trigger();
		}
		// now we come back in a few seconds to ask for proper names

	}

	protected static boolean nameOK(Experiment pl, String plname, MasterTable manager, Set<String> additionalNames) {
		int i = getManager(pl).getExperimentNames().indexOf(plname);
		if (i!=-1) {
			Experiment p2 = getManager(pl).getExperiment(i);
			if (p2!=null && p2!=pl)
				return false;
		}
		return additionalNames==null || !additionalNames.contains(plname);
	}


	@SuppressWarnings("serial")
	protected static class NamingFrame extends BaseFrame {

		protected Experiment[] lists;
		protected String[] newNames;
		protected String[] originalNames;
		protected DefaultTableModel dtm;
		protected JTable theTable;
		protected NameMapping nm = new NameMapping();

		protected NamingFrame(HashMap<Experiment, String> askUser) {
			super("Resolve Experiment Name Conflicts");
			lists = askUser.keySet().toArray(new Experiment[0]);
			originalNames = askUser.values().toArray(new String[0]);
			newNames = new String[originalNames.length];
			for (int i=0; i!=newNames.length; ++i)
				newNames[i] = lists[i].getName();
		}

		@Override 
		public void fillListActions(List<Object> actions) {
			actions.add(new RestoreNameAction());
			actions.add(null);
			actions.add(new AddNumberAction());
			actions.add(new AddPrimeAction());
			actions.add(new AddTimeAction());
			actions.add(null);
			actions.add(new AddPrefixAction());
			actions.add(new AddSuffixAction());
		}

		@Override
		public JComponent getDescription() {
			return new JLabel("Please review the suggested Experiment names. List size: ");
		}

		@Override
		public JComponent getList() {
			dtm = new DefaultTableModel();
			dtm.addColumn("Original Name", originalNames);
			dtm.addColumn("New Name", newNames);
			theTable = new JTable(dtm);
			theTable.getColumnModel().getColumn(0).setCellEditor(null);
			theTable.setDefaultRenderer(Object.class, new UniquenessRenderer());
			theTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			return theTable;
		}

		@Override
		public int getListSize() {
			return dtm!=null?dtm.getRowCount():0;
		}

		public void fillDialogActions(List<Object> actions) {
			actions.add(new CancelAction());
			actions.add(new OKAction());
		}

		public boolean applyNames() {
			NameMapping additionalNames = new NameMapping();
			for (int i=0; i!=dtm.getRowCount(); ++i) {
				String newName = (String)dtm.getValueAt(i, 1);
				Experiment pl = lists[i];
				MasterTable manager = getManager(pl);
				if (nameOK(pl, newName, manager, additionalNames.getSet(manager))) {
					additionalNames.getSet(manager).add(newName);
				} else {
					JOptionPane.showMessageDialog(this, "Please make sure all names are unique before pressing OK.", "Not all names are unique.", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
			for (int i=0; i!=dtm.getRowCount(); ++i) {
				String newName = (String)dtm.getValueAt(i, 1);
				Experiment pl = lists[i];
				pl.setName0(newName);
			}
			return true;
		}

		protected class OKAction extends AbstractAction {
			public OKAction() {
				super("OK");
			}

			public void actionPerformed(ActionEvent e) {
				if (applyNames()) {
					canceled = false;
					dispose();
				}
			}
		}

		protected abstract class ChangeElementsAction extends AbstractAction {

			public ChangeElementsAction(String name) {
				super(name);
			}

			public void actionPerformed(ActionEvent e) {
				int[] r = theTable.getSelectedRows();
				for (int i=0; i!=r.length; ++i) {
					// remove old mapping
					HashSet<String> hs = nm.getSet(getManager(lists[r[i]]));
					hs.remove((String)dtm.getValueAt(i, 1));
					String aValue = (String)dtm.getValueAt(r[i], 0);
					aValue = applyChange(aValue, lists[r[i]]);
					dtm.setValueAt(aValue, r[i], 1);	
					hs.add(aValue);
				}
				theTable.repaint();
			}	

			protected abstract String applyChange(String in, Experiment pl);

			protected boolean acceptable(String s, Experiment pl) { 
				HashSet<String> hs = nm.getSet(getManager(pl));
				return nameOK(pl, s, getManager(pl), hs);
			}
		}

		protected class AddTimeAction extends ChangeElementsAction {

			public AddTimeAction() {
				super("Add current time");
			}

			@Override
			protected String applyChange(String in, Experiment pl) {
				DateFormat df = DateFormat.getTimeInstance();				
				String suggestion = in + " ("+df.format(new Date())+")";
				return suggestion;
			}

		}

		protected class AddPrimeAction extends ChangeElementsAction {

			public AddPrimeAction() {
				super("Add prime (') symbol");
			}

			@Override
			protected String applyChange(String in, Experiment pl) {				
				String suggestion = in;
				while (!acceptable(suggestion, pl))
					suggestion += "'";
				return suggestion;
			}

		}
		
		protected class AddSuffixAction extends ChangeElementsAction {

			String suffix;
			
			public AddSuffixAction() {
				super("Add custom suffix");
			}

			public void actionPerformed(ActionEvent e) {
				suffix = JOptionPane.showInputDialog(NamingFrame.this, "Please enter a suffix", "'");
				if (suffix!=null && !suffix.equals("")) {
					super.actionPerformed(e);
				}
			}
			
			@Override
			protected String applyChange(String in, Experiment pl) {				
				String suggestion = in;
				while (!acceptable(suggestion, pl))
					suggestion += suffix;
				return suggestion;
			}

		}
		
		protected class AddPrefixAction extends ChangeElementsAction {

			String prefix;
			
			public AddPrefixAction() {
				super("Add custom prefix");
			}

			public void actionPerformed(ActionEvent e) {
				prefix = JOptionPane.showInputDialog(NamingFrame.this, "Please enter a prefix", "'");
				if (prefix!=null && !prefix.equals("")) {
					super.actionPerformed(e);
				}
			}
			
			@Override
			protected String applyChange(String in, Experiment pl) {				
				String suggestion = in;
				while (!acceptable(suggestion, pl))
					suggestion = prefix + suggestion;
				return suggestion;
			}

		}
		
		protected class AddNumberAction extends ChangeElementsAction {

			public AddNumberAction() {
				super("Add number (x)");
			}

			@Override
			protected String applyChange(String in, Experiment pl) {
				int baseNum=1;
				String suggestion = in;
				while (!acceptable(suggestion, pl))
					suggestion = in + " (" + (++baseNum) + ")";
				return suggestion;
			}

		}

		protected class RestoreNameAction extends ChangeElementsAction {

			public RestoreNameAction() {
				super("Restore original name");
			}

			@Override
			protected String applyChange(String in, Experiment pl) {
				return in;
			}

		}

		protected class UniquenessRenderer extends DefaultTableCellRenderer {

			public Component getTableCellRendererComponent (JTable table, Object value, boolean selected, boolean focused, int row, int column) {
				// use defaults
				super.getTableCellRendererComponent(table, value, selected, focused, row, column);

				// override coloring
				Color fg = null;
				Color bg = null;

				if (selected) {
					fg = table.getSelectionForeground();
					bg = table.getSelectionBackground();
				} else {
					bg = table.getBackground();
					fg = table.getForeground();
				}

				if (column==1) {
					Experiment pl = lists[row];
					MasterTable mim = getManager(pl);

					HashSet<String> remainingNames = new HashSet<String>();
					for (int i=0; i!=dtm.getRowCount(); ++i)
						if (i!=row && getManager(lists[i])==mim)
							remainingNames.add((String)dtm.getValueAt(i, 1));

					if (!nameOK(pl, (String)value, mim, remainingNames))					
						bg = new Color(255, selected?64:128, selected?64:128);

				}
				
				setBackground(bg);
				setForeground(fg);

				return this;
			}
		}
	}
	
	
	protected static MasterTable getManager(Experiment d) {
		return d.getMasterTable();
	}
	
	@SuppressWarnings("serial")
	protected static class NameMapping extends HashMap<MasterTable, HashSet<String>> {
		public HashSet<String> getSet(MasterTable ds) {
			if (!containsKey(ds))
				put(ds, new HashSet<String>());
			return get(ds);
		}
	}

}
