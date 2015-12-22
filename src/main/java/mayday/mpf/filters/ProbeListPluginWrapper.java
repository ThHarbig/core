package mayday.mpf.filters;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.mpf.FilterBase;
import mayday.mpf.MaydayDataObject;
import mayday.mpf.ProgressMeter;
import mayday.mpf.options.OptBoolean;
import mayday.mpf.options.OptDropDown;
import mayday.mpf.options.OptInteger;

public class ProbeListPluginWrapper extends FilterBase implements ActionListener {
	
	private OptDropDown Plugin;
	private OptIntegerFancy OptInputSize, OptOutputSize;
	private OptBoolean PassThrough;
	
	public ProbeListPluginWrapper() {
		super(1,1); // By default, we have 1 input & 1 output if the user doesn't modify this
		
		pli.setName("Wrapper for other Mayday plugins");
		pli.setIdentifier("PAS.mpf.plumawrapper");
		pli.setAuthor("Florian Battke");
		pli.setEmail("battke@informatik.uni-tuebingen.de");
		pli.setAbout("Allows using any probelist plugin in the processing pipeline. \n\n" +
				"This is dangerous because the MPF knows nothing about the external plugins, so be careful what you do." +
				"During processing, the external plugin may show a settings dialog, interrupting the flow of the pipeline.");
		pli.replaceCategory("Wrapper modules");
		
		Set<PluginInfo> plis = PluginManager.getInstance().getPluginsFor(Constants.MC_PROBELIST);
		
		Plugin = new OptDropDown("Wrapped Plugin",
				"Choose the plugin to apply to the data.",
				plis.toArray(),0);
		
		OptInputSize = new OptIntegerFancy("Input slots","Specify how many input slots (=ProbeLists) will be passed to the plugin.",1);
		OptOutputSize = new OptIntegerFancy("Output slots",
				"Specify how many output slots (=ProbeLists) will be returned from the plugin.\n" +
				"If the plugin returns less than the specified number, empty ProbeLists will be used for missing output.\n" +
				"If the plugin returns more than the specified number, the additional ProbeLists will be discarded.",
				1);
		OptInputSize.addActionListener(this);
		OptOutputSize.addActionListener(this);
		
		PassThrough = new OptBoolean("Pass-through","Select this if the plugin does not return any probelists\n" +
				"but directly modifies the input probelists (e.g. adding MIOs). \n" +
				"Input probelists will be passed directly to the output slots.", false);
		
		Options.add(Plugin);
		Options.add(OptInputSize);
		Options.add(OptOutputSize);		
		Options.add(PassThrough);
				
	}

	@SuppressWarnings("deprecation")
	public void execute() {
		PluginInfo pli = (PluginInfo)Plugin.getObject();
		ProbelistPlugin plp = (ProbelistPlugin)pli.newInstance();
		LinkedList<ProbeList> inlists = new LinkedList<ProbeList>();
		for (MaydayDataObject mdo : InputData) {
			inlists.add(mdo.getProbeList());
		}
		List<ProbeList> outlists = plp.run(inlists, InputData[0].getProbeList().getDataSet().getMasterTable());
		
		if (PassThrough.Value) {
			System.arraycopy(InputData, 0, OutputData, 0, InputSize);
		} else {

			if (outlists == null) {
				outlists = new LinkedList<ProbeList>();
			}
			
			// Do we have the right number of outputs? If not, create some empty lists or discard some outputs
			if (OutputSize>outlists.size()) {
				this.ProgressMeter.writeLogLine(
						"Warning: Plugin returned only "+outlists.size() +
						" probe lists when "+OutputSize+" probe lists were expected. " +
						"Substituting empty probe lists for missing output.");
				DataSet ds = InputData[0].getProbeList().getDataSet();
				while (OutputSize>outlists.size()) {
					outlists.add(new ProbeList(ds,true));  // don't care about stickyness
				}
			} else if (OutputSize<outlists.size()) {
				// discard overhanging output
				ProgressMeter.writeLogLine(
						"Warning: R returned "+outlists.size() +
						" probe lists when only "+OutputSize+" probe lists were expected. " +
						"Discarding "+ (outlists.size()-OutputSize) + " probe lists.");			
				while (OutputSize<outlists.size()) 
					outlists.remove(outlists.size()-1);
			}
			
			for (int i=0; i!=OutputSize; ++i) {				
				ProbeList src = outlists.get(i);
				MaydayDataObject tgt = new MaydayDataObject(src);
				
				// finally, add the new ProbeList to our output
				OutputData[i] = tgt;
			}
		}
		
	}
	
	public int getOutputSize() {
		return OutputSize;
	}

	public int getInputSize() {
		return InputSize;
	}
	
	public ProgressMeter getProgressMeter() {
		return ProgressMeter;
	}

	public void restoreSlotNumbersOnLoading() {
		this.InputSize = this.OptInputSize.Value;
		this.OutputSize = this.OptOutputSize.Value;
		this.InputData = new MaydayDataObject[InputSize];
		this.OutputData = new MaydayDataObject[OutputSize];
		if (this.attachedFilterNode!=null) {
			this.attachedFilterNode.resetIOSizes();
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource()==this.OptInputSize || arg0.getSource()==this.OptOutputSize) {
			restoreSlotNumbersOnLoading();
		} 
	}

	private class OptIntegerFancy extends OptInteger {

		private String originalDesc;
		
		public OptIntegerFancy(String name, String description, Integer DefaultValue) {
			super(name, description, DefaultValue);
			this.setBounds(0,null);
			originalDesc=description;
			this.externalizable=false;			
		}
		
		protected void createEditArea() {
			super.createEditArea();
			boolean cfa = this.calledFromApplicator();
			this.tf.setEnabled(!cfa);
			this.Description = originalDesc + (cfa ? "\n\nThis option can't be changed in Applicator mode. \n" +
					"If you need to change this option, use the Plugin Wrapper within a Processing Pipeline \n" +
					"and use MPF Designer to change this option." : "");
		}
		
		public void ValueFromString(String valueStr) {
			super.ValueFromString(valueStr);			
			// Here I have to reset my parent's input and output size so that loading the Wrapper within a pipeline also works 
			if (parent!=null && parent instanceof ProbeListPluginWrapper)
				((ProbeListPluginWrapper)parent).restoreSlotNumbersOnLoading();
		}
		
	}

	
}
