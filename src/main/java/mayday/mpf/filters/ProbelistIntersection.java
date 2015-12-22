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
public class ProbelistIntersection extends FilterBase implements ActionListener {

	private OptIntegerFancy OptInputSize;
	
	private class OptIntegerFancy extends OptInteger {

		private ProbelistIntersection myParent;
		private String originalDesc;
		
		public OptIntegerFancy(String name, String description, Integer DefaultValue, ProbelistIntersection parent) {
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
	
	public ProbelistIntersection() {
		super(2,1);
		
		pli.setName("Probelist Intersection");
		pli.setIdentifier("PAS.mpf.probelistintersection");
		pli.replaceCategory("Data handling");
		pli.setAuthor("Florian Battke");
		pli.setEmail("battke@informatik.uni-tuebingen.de");
		pli.setAbout("Intersects several probe lists, i.e. keeps only probes that appear in all input probe lists.");
				
		OptInputSize = new OptIntegerFancy("Input slots","Specify how many input slots (=ProbeLists) will be intersected.",2,this);
		OptInputSize.addActionListener(this);
		Options.add(OptInputSize);
	}

	public void restoreSlotNumbersOnLoading() {
		this.InputSize = this.OptInputSize.Value;
		// this.OutputSize = this.OptOutputSize.Value;
		this.InputData = new MaydayDataObject[InputSize];
		//this.OutputData = new MaydayDataObject[OutputSize];
		if (this.attachedFilterNode!=null) {
			this.attachedFilterNode.resetIOSizes();
		}
	}	
	
	@SuppressWarnings("deprecation")
	public void execute() {
		OutputData[0]=InputData[0];

		this.ProgressMeter.statusChanged(0.0, null);

		Set<Probe> set = new TreeSet<Probe>();
        //StringBuffer quickInfo = new StringBuffer();
                
        
        //quickInfo.append("Intersection of \n");
        
		for (int i=0; i!=this.InputSize; ++i) {
    		this.ProgressMeter.statusChanged(0.5 * (double) i / (double) this.InputSize, null);
			set.retainAll(InputData[i].getProbeList().getAllProbes());
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
