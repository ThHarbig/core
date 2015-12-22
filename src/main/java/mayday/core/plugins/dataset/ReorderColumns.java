package mayday.core.plugins.dataset;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.gui.MaydayFrame;
import mayday.core.gui.components.ReorderableJList;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.DatasetPlugin;

public class ReorderColumns extends AbstractPlugin implements DatasetPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.DatasetReorderColumns",
				new String[0],
				Constants.MC_DATASET,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Reorders the columns of the expression matrix",
				"Change Experiment Order"
		);
		pli.addCategory("Transform");
		return pli;
	}

	public List<DataSet> run(List<DataSet> datasets) {
		final DataSet ods = datasets.get(0);            

		// we will reorder in place, so no return value
		final LinkedList<DataSet> retval = new LinkedList<DataSet>(); 

		int[] oldIndices = new int[ods.getMasterTable().getNumberOfExperiments()];
		for (int i=0; i!=oldIndices.length; ++i)
			oldIndices[i]=i; 

		final JFrame jf = new MaydayFrame();
		jf.setTitle("Change Experiment Order for \""+ods.getName()+"\"");
		jf.setLayout(new BorderLayout());

//		final ExperimentIndexTableModel tm = new ExperimentIndexTableModel(oldIndices); 
//		JTable table = new JTable(tm);
//		JScrollPane scrollpane = new JScrollPane(table);
		
		ReorderableJList list = new ReorderableJList();
		final ExperimentListModel dlm = new ExperimentListModel(ods.getMasterTable());
		list.setModel(dlm);
		for (int i=0; i!=oldIndices.length; ++i)
			dlm.add(i, oldIndices[i]);
		JScrollPane scrollpane = new JScrollPane(list); 

		Box buttonpane = Box.createHorizontalBox();
		JButton okButton = new JButton("Apply");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				int[] newIndices = dlm.getIndices();
				ods.getMasterTable().reorderExperiments(newIndices);
				jf.dispose();
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				jf.dispose();
			}
		});

		buttonpane.add(cancelButton);
		buttonpane.add(Box.createHorizontalGlue());
		buttonpane.add(okButton);

		jf.add(new JLabel("Use drag&drop to rearrange the experiments"), BorderLayout.NORTH);		
		jf.add(scrollpane, BorderLayout.CENTER);
		jf.add(buttonpane, BorderLayout.SOUTH);

		jf.pack();
		jf.setVisible(true);

		return retval;
	}

	@SuppressWarnings("serial")
	private class ExperimentListModel extends DefaultListModel {

		protected MasterTable mata;
		
		public ExperimentListModel(MasterTable mata) {
			this.mata = mata;
		}
		
		public Object getElementAt(int index) {
			// map int to string
			Object el = super.getElementAt(index);
			return mata.getExperimentName((Integer)el);
		}
		
		public void add(int index, Object o) {
			if (o instanceof Integer)
				super.add(index,o);
			if (o instanceof String) {
				// map string to int
				int i;
				for (i=0;i!=mata.getNumberOfExperiments(); ++i)
					if (mata.getExperimentName(i).equals(o))
						break;
				super.add(index, i);
			}
		}
		
		public int[] getIndices() {
			int[] tmp = new int[this.size()];
			for (int i=0; i!=tmp.length; ++i)
				tmp[i]  = (Integer)super.getElementAt(i);
			return tmp;
		}
	}
	

}
