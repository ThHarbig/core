package mayday.mpf.filters;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import mayday.mpf.MaydayDataObject;
import mayday.mpf.options.OptBase;
import mayday.mpf.options.OptDropDown;
import mayday.mpf.options.OptInteger;
import mayday.mpf.options.OptSpacer;

public class RWrapper extends mayday.mpf.FilterBase implements ItemListener, ActionListener {
	
	private OptDropDown RFunction;
	private OptIntegerFancy OptInputSize, OptOutputSize;
	private Vector<OptBase> ROptions = new Vector<OptBase>();
	
	private Object helper; // this is actually of type RWrapperHelper, but we don't want to import it here 
	

	public RWrapper() {
		super(1,1); // By default, we have 1 input & 1 output if the user doesn't modify this
		
		pli.setName("Wrapper for R Interpreter functions");
		pli.setIdentifier("PAS.mpf.rwrapper");
		pli.addDependencies(new String[]{"PAS.Rinterpreter"});
		pli.setAuthor("Florian Battke");
		pli.setEmail("battke@informatik.uni-tuebingen.de");
		pli.setAbout("Allows using R functions in the processing pipeline.");
		pli.replaceCategory("Wrapper modules");
		
		try {
			helper = new RWrapperHelper();
			((RWrapperHelper)helper).parent=this;
		} catch (Throwable t) {
			return;
		}
		
		// this code is only executed when RWrapperHelper could be initialized correctly
		RWrapperHelper rwh = (RWrapperHelper)helper;
		
		RFunction = new OptDropDown("R function",
				"Choose the R function to apply to the data.\n" +
				"Use the R Interpreter plugin to add more R functions to this list.\n" +
				"Changing this field can change the semantics of externalized options.\n" +
				"Be sure to check your externalized options and edit them appropriately.",
				rwh.availableSources,0);
		RFunction.externalizable=false; // this must be set to allow externalization of the r function's options
		RFunction.setItemListener(this); // for changes due to user interaction
		RFunction.addActionListener(this); // for changes during ValueFromString
		
		OptInputSize = new OptIntegerFancy("Input slots","Specify how many input slots (=ProbeLists) will be passed to the R function.",1);
		OptOutputSize = new OptIntegerFancy("Output slots",
				"Specify how many output slots (=ProbeLists) will be returned by the R function.\n" +
				"If the function returns less than the specified number, empty ProbeLists will be used for missing output.\n" +
				"If the function returns more than the specified number, the additional ProbeLists will be discarded.",
				1);
		OptInputSize.addActionListener(this);
		OptOutputSize.addActionListener(this);
		
		OptSpacer os = new OptSpacer("General Options");
		os.externalizable=false;
		Options.add(os);
		Options.add(OptInputSize);
		Options.add(OptOutputSize);		
		Options.add(RFunction);
		os = new OptSpacer("Options for this R function");
		os.externalizable=false;
		Options.add(os);
				
		createOptionList(0);
		
	}

	public void execute() {
		RWrapperHelper rwh = ((RWrapperHelper)helper);
		rwh.ProgressMeter=this.ProgressMeter;
		rwh.execute();
	}
	
	public int getOutputSize() {
		return OutputSize;
	}

	public int getInputSize() {
		return InputSize;
	}
	
	public mayday.mpf.ProgressMeter getProgressMeter() {
		return this.ProgressMeter;
	}

	@SuppressWarnings("deprecation")
	private void createOptionList(int selectedItemIndex) {
		RWrapperHelper rwh = (RWrapperHelper)helper;
		// remove previously created Options
		for (OptBase ob : ROptions) 
			Options.remove(ob); // I use this function on purpose and I know what I'm doing!								
		rwh.createOptionList(ROptions, selectedItemIndex);
		// recreate option window
		for (OptBase ob : ROptions) Options.add(ob);
		Options.createOptionList();

		
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

	public void itemStateChanged(ItemEvent arg0) {
		if (arg0.getStateChange()==java.awt.event.ItemEvent.SELECTED) {
			// This change can not be undone!			
			RFunction.accept();
			createOptionList(RFunction.Value);
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource()==this.OptInputSize || arg0.getSource()==this.OptOutputSize) {
			restoreSlotNumbersOnLoading();
		} else
		if (arg0.getSource()==RFunction) {
			createOptionList(RFunction.Value);
			// This is only called when RFunction.ValueFromString posts an event to me
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
					"If you need to change this option, use the RWrapper within a Processing Pipeline \n" +
					"and use MPF Designer to change this option." : "");
		}
		
		public void ValueFromString(String valueStr) {
			super.ValueFromString(valueStr);			
			// Here I have to reset my parent's input and output size so that loading the Rwrapper within a pipeline also works 
			if (parent!=null && parent instanceof RWrapper)
				((RWrapper)parent).restoreSlotNumbersOnLoading();
		}
		
	}

	
}
