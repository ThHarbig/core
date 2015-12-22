package mayday.mpf.filters;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;

import mayday.core.Probe;
import mayday.core.math.pcorrection.PCorrectionManager;
import mayday.core.math.pcorrection.PCorrectionPlugin;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.gui.MIGroupSelectionDialog;
import mayday.core.meta.types.DoubleMIO;
import mayday.mpf.FilterBase;
import mayday.mpf.options.OptDropDown;
import mayday.mpf.options.OptString;

public class AdjustPValue extends FilterBase {

	private ArrayList<MIGroup> selectedGroups = null;
	//private ArrayList<MIOGroup> adjustedGroups = null;
	private boolean reuseGroup = false;
	private OptDropDown method;
	private OptString mioName = new OptString("MIO groups", "the name(s) of the MIO group(s), that contain the p-value(s)", "p-value");


	public AdjustPValue() {
		super(1,1);

		pli.setName("p-value correction");
		pli.setIdentifier("PAS.affyrma.adjustpvalue");
		pli.setAuthor("Anna Jasper");
		pli.setEmail("Anna.ivic@gmail.com");
		pli.setAbout("p-value correction for multiple testing");
		pli.addDependencies(new String[]{"LIB.Commons.math"});
		pli.replaceCategory("Statistics");

		method = new OptDropDown("correction-method:",
				"Which method do you want to use to correct your p-values?",
				PCorrectionManager.values().toArray(),0);

		Options.add(method);
		Options.add(mioName);

	}
	
	

	@SuppressWarnings("deprecation")
	public void execute() throws Exception {
		OutputData[0]=InputData[0];

		// no correction method
		PCorrectionPlugin pcmethod = (PCorrectionPlugin)(method.getObject());
		
		if (selectedGroups==null || !reuseGroup) {
			getMIOGroups();
		}
		
		ProgressMeter.initializeStepper(selectedGroups.size());

		// create new MIO Groups
		MIGroup[] nMG = new MIGroup[selectedGroups.size()];
		MIManager mim = OutputData[0].getProbeList().getDataSet().getMIManager();
	
		createNewMIOGroups(mim, selectedGroups, nMG, pcmethod.toString());
		
		for (int i=0; i<nMG.length; ++i)  {
			MIGroup mg = selectedGroups.get(i);
			double[] pvals = new double[OutputData[0].size()];
			//extract p-values for ranking
			int k=0;
			for (Probe pb : OutputData[0]) {
				DoubleMIO theMIO = (DoubleMIO)mg.getMIO(pb);
				pvals[k]=theMIO.getValue();
				++k;
			}
			// correction
			List<Double> correctedpvals = pcmethod.correct(pvals);
			
			k=0;
			//rank p-values, the highest value gets the 1st rank
			for (Probe pb : OutputData[0]) {
				((DoubleMIO)nMG[i].add(pb)).setValue(correctedpvals.get(k));
				++k;
			}

			ProgressMeter.stepStepper(1);
		}
		
	}


	private void createNewMIOGroups(MIManager mim, ArrayList<MIGroup> selectedGroups, MIGroup[] nMG, String correctionMethod){
		for (int i=0; i<nMG.length; ++i)  {
			nMG[i] = mim.newGroup("PAS.MIO.Double", (correctionMethod + " (corrected "+selectedGroups.get(i).getName()+")"), selectedGroups.get(i));
		}
	}

	@SuppressWarnings("deprecation")
	private void getMIOGroups() {

		// previously collected only MIO groups in the input data, now we simply take all groups
		MIManager mimanager = InputData[0].getProbeList().getDataSet().getMIManager();		

		int groupCount = mimanager.getGroups().size();

		//070724: Use preselected list of MIO names
		if (!mioName.Value.equals("")) {
			String[] theMIONames = mioName.Value.split(",");
			boolean allMatched = true;
			for (String MIOName : theMIONames) {
				boolean thisMatched=false;
				MIGroupSelection<MIType> mgs = mimanager.getGroupsForName(MIOName);
				if (mgs.size()==1 && mayday.core.meta.types.DoubleMIO.class.isAssignableFrom(mgs.get(0).getMIOClass())) {
					thisMatched=true;
					selectedGroups = mgs;
					break;
				}
				allMatched &= thisMatched;
				if (!allMatched) break;
			}
			if (allMatched) 
				return;
			ProgressMeter.writeLogLine(this.getName()+": Could not find all specified MIO groups - opening dialog.");
		}

		//060720: If there is only one matching MIO group, select that one, else show a selection dialog.
		if (groupCount==1) {
			selectedGroups = mimanager.getGroups();
			reuseGroup = false; // perhaps there are several groups for the next job... if not, we do the same thing again ;)
			ProgressMeter.writeLogLine("MIOPresence: Only one MIOGroup found => automatically selected");
		} else 
			if (groupCount>1) {
				MIGroupSelectionDialog mgsd = new MIGroupSelectionDialog(mimanager, MIManager.getMIOClass("PAS.MIO.Double"));
				JCheckBox useForAllJobs = new JCheckBox("<html>Use the selected group for all jobs in batchmode</html>",true);
				mgsd.getAdditionalDialogElementsPanel().add(useForAllJobs);
				mgsd.setDialogDescription("Select one or more MIO Groups for P-Value adjustment");
				mgsd.setVisible(true);
				MIGroupSelection<MIType> mgs = mgsd.getSelection();
				if (mgs.size()>0) {
					selectedGroups=mgs;
					reuseGroup=useForAllJobs.isSelected();
				} else
					throw new RuntimeException("No MIOs selected for filtering.");
			} else if (groupCount==0) 
				throw new RuntimeException("No MIO groups found to allow selection.");
	}

}
