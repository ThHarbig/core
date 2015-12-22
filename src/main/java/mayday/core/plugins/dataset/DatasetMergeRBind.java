package mayday.core.plugins.dataset;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.MaydayDialog;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.StringMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.DatasetPlugin;
import mayday.core.tasks.AbstractTask;

public class DatasetMergeRBind extends AbstractPlugin implements DatasetPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.alignedDS.DatasetMergeR",
				new String[0],
				Constants.MC_DATASET,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Merges two datasets, renaming probes to avoid clashes",
				"Merge row wise"
		);
		pli.addCategory("Merge & Split");
		return pli;
	}

	public List<DataSet> run(final List<DataSet> datasets) {
        
		MergeTaskRBind atask = new MergeTaskRBind(datasets);
		
		atask.start();
		
		atask.waitFor();
		
		return atask.getResult();
	}


	public static class MergeTaskRBind extends AbstractTask {

		protected Boolean safeMode = null;
		
		private List< DataSet > datasets;
		
		private List< DataSet > resultsets = new LinkedList<DataSet>();
		
		private final static String MIOPATH = "/Merged DataSets";
		
		public MergeTaskRBind( List<DataSet> datasets2 ) {
			super("Merging DataSets");
			this.datasets=datasets2;
		}

		protected void doWork() throws Exception { 
		
			if (datasets.size()!=2)
				throw new RuntimeException("Currently this plugin can only merge two datasets.");
			
			new PropertiesDialog();
			if (safeMode==null)
				return;


			// 1 - create new dataset
			DataSet d1, d2;
			d1 = datasets.get(0);
			d2 = datasets.get(1);
			String name = d1.getName()+" + "+d2.getName();
			DataSet ds = new DataSet(name);		
			ds.getMasterTable().setNumberOfExperiments(d1.getMasterTable().getNumberOfExperiments());
			resultsets.add(ds);
			
			// 2 - create MIO groups
			MIGroup mgName = ds.getMIManager().newGroup("PAS.MIO.String", "Original Name", MIOPATH);
			MIGroup mgDS = ds.getMIManager().newGroup("PAS.MIO.String", "Source DataSet", MIOPATH);
			MasterTable mata = ds.getMasterTable();
			
			for (DataSet d : datasets) {
				HashMap<Probe,Probe> copiedProbes = new HashMap<Probe, Probe>();
				
				StringMIO dsMIO = new StringMIO(d.getName());
				// 3a - copy probes, attach meta information
				for (Probe pb : d.getMasterTable().getProbes().values()) {
					String pname = pb.getName()+(safeMode?" |"+d.getName():"");
					Probe pbX = new Probe(mata);
					double[] newVals = new double[pb.getValues().length];
					System.arraycopy(pb.getValues(), 0, newVals, 0, newVals.length);
					pbX.setValues(pb.getValues());
					pbX.setName(pname);
					pbX.setAnnotation(pb.getAnnotation());
					mata.addProbe(pbX);
					if (safeMode)
						((StringMIO)mgName.add(pbX)).setValue(pb.getName());
					mgDS.add(pbX,dsMIO);
					copiedProbes.put(pb,pbX);
				}
				// 3b - copy probelists
				for (ProbeList pl : d.getProbeListManager().getProbeLists()) {
					ProbeList plX = new ProbeList(ds, pl.isSticky());
					plX.setName(pl.getName()+" |"+d.getName());
					plX.setAnnotation(pl.getAnnotation());
					plX.setColor(pl.getColor());
					for (Probe pb : pl.getAllProbes()) {
						plX.addProbe(copiedProbes.get(pb));
					}
					((StringMIO)mgName.add(plX)).setValue(pl.getName());
					mgDS.add(plX, dsMIO);
					ds.getProbeListManager().addObjectAtBottom(plX);
				}
			}
			for (int i=0; i!=d1.getMasterTable().getNumberOfExperiments(); ++i)
				mata.setExperimentName(i, d1.getMasterTable().getExperimentName(i));		
		}
		
		
		protected void initialize() {
		}
		
		public List<DataSet> getResult() {
			return resultsets;
		}
		
		@SuppressWarnings("serial")
		protected class PropertiesDialog extends MaydayDialog {
			private JCheckBox changeNames;
			
			public PropertiesDialog()
			{
				this.setTitle("Merge two DataSets");
				this.createWidgets();
				this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				this.setModal(true);
				this.pack();
				this.setVisible(true);
			}
			
			private void createWidgets()
			{
				this.changeNames = new JCheckBox("Change all probe names to avoid name clashes.");
				this.changeNames.setSelected(true);
				
				setLayout(new GridLayout(2,1));

				add(changeNames);
				
				JButton ok_button = new JButton("OK");
				ok_button.setSelected(true);
				JButton cancel_button = new JButton("Cancel");
				
				ok_button.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent ae) {
						safeMode = changeNames.isSelected();
						dispose();
					}					
				});
				
				cancel_button.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent ae) {
						safeMode = null; 
						dispose();
					}
				});
				
				JPanel buttonPanel = new JPanel();
				buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
				buttonPanel.add(cancel_button);
				buttonPanel.add(ok_button);
				
				add(buttonPanel);		
			}
		}

	}
	
	

	
}
