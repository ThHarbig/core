package mayday.mpf.filters;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.TreeSet;

import mayday.core.Probe;
import mayday.mpf.FilterBase;
import mayday.mpf.MaydayDataObject;
import mayday.mpf.options.OptInteger;

/** @author Florian Battke */
public class ProbelistUnion extends FilterBase implements ActionListener {

	
	private OptIntegerFancy OptInputSize;
	
	private class OptIntegerFancy extends OptInteger {

		private ProbelistUnion myParent;
		private String originalDesc;
		
		public OptIntegerFancy(String name, String description, Integer DefaultValue, ProbelistUnion parent) {
			super(name, description, DefaultValue);
			this.setBounds(2,null);
			originalDesc=description;
			myParent=parent;
			this.externalizable=false;			
		}
		
		protected void createEditArea() {
			super.createEditArea();
			this.tf.setEnabled(myParent.cMgr==null);
			this.Description = originalDesc + ((myParent.cMgr!=null) ? "\n\nThis option can't be changed in Applicator mode. \n" +
					"If you need to change this option, use the module within a Processing Pipeline \n" +
					"and use MPF Designer to change this option." : "");
		}
		
		public void ValueFromString(String valueStr) {
			super.ValueFromString(valueStr);			
			// Here I have to reset my parent's input and output size so that loading the Rwrapper within a pipeline also works 
			myParent.restoreSlotNumbersOnLoading();
		}
		
	}
	
	public ProbelistUnion() {
		super(2,1);
		
		OptInputSize = new OptIntegerFancy("Input slots","Specify how many input slots (=ProbeLists) will be merged.",2,this);
		OptInputSize.addActionListener(this);
		Options.add(OptInputSize);
		
		pli.setName("Probelist Union");
		pli.setIdentifier("PAS.mpf.probelistunion");
		pli.replaceCategory("Data handling");
		pli.setAuthor("Florian Battke");
		pli.setEmail("battke@informatik.uni-tuebingen.de");
		pli.setAbout("Merges several probe lists into one, i.e. takes the set union of all input probe lists.");
	}

	public void restoreSlotNumbersOnLoading() {
		this.InputSize = this.OptInputSize.Value;
		// this.OutputSize = this.OptOutputSize.Value;
		this.InputData = new MaydayDataObject[InputSize];
		this.OutputData = new MaydayDataObject[OutputSize];
		if (this.attachedFilterNode!=null) {
			this.attachedFilterNode.resetIOSizes();
		}
	}	
	
	@SuppressWarnings("deprecation")
	public void execute() {
		OutputData[0]=InputData[0];

		this.ProgressMeter.statusChanged(0.0, null);
		
		Set<Probe> set = new TreeSet<Probe>();
        
		for (int i=0; i!=this.InputSize; ++i) {
    		this.ProgressMeter.statusChanged(0.5 * (double) i / (double) this.InputSize, null);
            set.addAll(InputData[i].getProbeList().getAllProbes());
			OutputData[0].addToAnnotation("Probelist "+i,InputData[i].getProbeList().getName());
            if (i>0) 
            	InputData[i].dismiss();
		}
		
		set.removeAll(InputData[0].getProbeList().getAllProbes());
		
		int i=0; 
		for (Probe pb: set) {
    		this.ProgressMeter.statusChanged(0.5 + 0.5 * (double) i / (double) set.size(), null);
			OutputData[0].addProbeWithoutCloning(pb);
		}
				
	
	}

	public void actionPerformed(ActionEvent arg0) {
		this.restoreSlotNumbersOnLoading();
	}

}
