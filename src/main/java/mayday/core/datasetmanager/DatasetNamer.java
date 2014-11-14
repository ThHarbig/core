package mayday.core.datasetmanager;

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

import mayday.core.DataSet;
import mayday.core.DelayedUpdateTask;
import mayday.core.gui.listdialog.BaseFrame;

public class DatasetNamer {

	protected static HashMap<DataSet, String> askUser = new HashMap<DataSet, String>();

	protected static DelayedUpdateTask userQuery = new DelayedUpdateTask("DataSet Namer", 2000) {

		@Override
		protected void performUpdate() {
			synchronized (DatasetNamer.class) {
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

	public static synchronized void ensureNameUniqueness(DataSet... lists) {

		// Find unique names for all datasets and insert them right away

		// Lists that needed changing are presented to the user.
		// Choices for suffixes are:
		// - current time
		// - prime ' / any character
		// - number in brackets (x2)


		// default naming scheme is "x (time)"
		 HashSet<String> additionalNames = new HashSet<String>();
		
		for (DataSet ds : lists) {
			String dsname = ds.getName();
			if (dsname == null || dsname.equals("")) {
				dsname = "Unnamed DataSet";
				askUser.put(ds, dsname);
			}
			if (!nameOK(ds, dsname, additionalNames)) {
				DateFormat df = DateFormat.getTimeInstance();

				String suggestion = dsname + " ("+df.format(new Date())+")";
				while (!nameOK(ds, suggestion, additionalNames)) {
					suggestion+="'";
				}
				dsname = suggestion;
				askUser.put(ds, ds.getName());
			}
			ds.setName(dsname);
			additionalNames.add(dsname);
		}

		if (!askUser.isEmpty()) {
			userQuery.trigger();
		}
		// now we come back in a few seconds to ask for proper names

	}

	protected static boolean nameOK(DataSet ds, String dsname, Set<String> additionalNames) {
		DataSet d2=null;
		for (DataSet dx : DataSetManager.singleInstance.getDataSets())
			if (dx.getName().equals(dsname))
				d2 = dx;
		
		if (d2!=null && d2!=ds)
			return false;
		return additionalNames==null || !additionalNames.contains(dsname);
	}


	@SuppressWarnings("serial")
	protected static class NamingFrame extends BaseFrame {

		protected DataSet[] lists;
		protected String[] newNames;
		protected String[] originalNames;
		protected DefaultTableModel dtm;
		protected JTable theTable;
		protected HashSet<String> nm = new HashSet<String>();

		protected NamingFrame(HashMap<DataSet, String> listsToName) {
			super("Resolve DataSet Name Conflicts");
			lists = listsToName.keySet().toArray(new DataSet[0]);
			originalNames = listsToName.values().toArray(new String[0]);
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
			return new JLabel("Please review the suggested DataSet names. List size: ");
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
			HashSet<String> additionalNames = new HashSet<String>();
			for (int i=0; i!=dtm.getRowCount(); ++i) {
				String newName = (String)dtm.getValueAt(i, 1);
				DataSet ds = lists[i];
				if (nameOK(ds, newName, additionalNames)) {
					additionalNames.add(newName);
				} else {
					JOptionPane.showMessageDialog(this, "Please make sure all names are unique before pressing OK.", "Not all names are unique.", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
			for (int i=0; i!=dtm.getRowCount(); ++i) {
				String newName = (String)dtm.getValueAt(i, 1);
				DataSet ds = lists[i];
				ds.setName(newName);
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
					nm.remove((String)dtm.getValueAt(i, 1));
					String aValue = (String)dtm.getValueAt(r[i], 0);
					aValue = applyChange(aValue, lists[r[i]]);
					dtm.setValueAt(aValue, r[i], 1);	
					nm.add(aValue);
				}
				theTable.repaint();
			}	

			protected abstract String applyChange(String in, DataSet pl);

			protected boolean acceptable(String s, DataSet pl) { 
				return nameOK(pl, s, nm);
			}
		}

		protected class AddTimeAction extends ChangeElementsAction {

			public AddTimeAction() {
				super("Add current time");
			}

			@Override
			protected String applyChange(String in, DataSet pl) {
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
			protected String applyChange(String in, DataSet pl) {				
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
			protected String applyChange(String in, DataSet pl) {				
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
			protected String applyChange(String in, DataSet pl) {				
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
			protected String applyChange(String in, DataSet pl) {
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
			protected String applyChange(String in, DataSet pl) {
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
					DataSet pl = lists[row];

					HashSet<String> remainingNames = new HashSet<String>();
					for (int i=0; i!=dtm.getRowCount(); ++i)
						if (i!=row)
							remainingNames.add((String)dtm.getValueAt(i, 1));

					if (!nameOK(pl, (String)value, remainingNames))					
						bg = new Color(255, selected?64:128, selected?64:128);

				}
				
				setBackground(bg);
				setForeground(fg);

				return this;
			}
		}
	}
	

}
