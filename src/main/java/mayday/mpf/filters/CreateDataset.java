package mayday.mpf.filters;

import java.util.Vector;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.datasetmanager.gui.DataSetManagerView;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.mpf.FilterBase;
import mayday.mpf.options.OptBoolean;
import mayday.mpf.options.OptString;

/** @author Florian Battke */
public class CreateDataset extends FilterBase {

	private OptString DataSetName = new OptString("Dataset name","A name for the newly created Dataset","New Dataset");
	private OptBoolean CloneAnno= new OptBoolean("Clone Annotations","Select whether annotations should be cloned into the new Dataset",true);
	private OptBoolean CloneMIO = new OptBoolean("Clone MIOs","Select whether meta information should be cloned into the new Dataset",true);
	private OptBoolean StripNames = new OptBoolean("Strip names","Select whether Probe names should be stripped of all qualifiers added by previous invocations of the MPF.",true);
	
	public CreateDataset() {
		super(1,0);
		
		pli.setName("Create Dataset");
		pli.setIdentifier("PAS.mpf.createdataset");
		pli.replaceCategory("Data handling");
		pli.setAuthor("Florian Battke");
		pli.setEmail("battke@informatik.uni-tuebingen.de");
		pli.setAbout("Uses the input probe list as master probe list for a new dataset."
					 +" The input is copied into the new Dataset and returned unchanged as output.");
		
		Options.add(DataSetName);
		Options.add(CloneAnno);
		Options.add(CloneMIO);
		Options.add(StripNames);
	}

	@SuppressWarnings("deprecation")
	public void execute() {
		this.ProgressMeter.initializeStepper(InputData[0].size());		
		DataSet ds = new DataSet(DataSetName.Value);

		// Add the new DataSet BEFORE any experiments (columns) or probes (rows) are in it, so that the
		// "Successfully opened DataSet" dialog won't pop up.
		DataSetManagerView.getInstance().addDataSet(ds);
		
		MasterTable mt = new MasterTable(ds);
		mt.setNumberOfExperiments(InputData[0].getNumberOfExperiments());
		for (int i=0; i!=mt.getNumberOfExperiments(); ++i) {
			mt.setExperimentName(i, InputData[0].getProbeList().getDataSet().getMasterTable().getExperimentName(i));
		}
		
		for (Probe pb: InputData[0]) {
			Probe newPb = new Probe(mt);			
			for(int i=0; i!=pb.getNumberOfExperiments(); ++i)
				newPb.addExperiment(pb.getValue(i));
			if (CloneAnno.Value && pb.getAnnotation()!=null) {
				newPb.setAnnotation(pb.getAnnotation().clone());
			}
			if (CloneMIO.Value) {
				cloneMIOs(InputData[0].getProbeList().getDataSet(), pb, ds, newPb);
			}
			String PbName=pb.getName();
			int bracketIndex = PbName.indexOf("[");
			if (StripNames.Value && bracketIndex>=0) {
				PbName = PbName.substring(0,bracketIndex);
				if (PbName.charAt(PbName.length()-1)==' ')
					PbName = PbName.substring(0, PbName.length()-1);
			}
			newPb.setName(PbName);
			try {
				mt.addProbe(newPb);
			} catch (RuntimeException e) {
				String ex = "Create DataSet failed: " + e.getMessage();
				e.printStackTrace();
				if (this.StripNames.Value) 
					ex+="\nTry running the module without the \"Strip names\" option.";
				throw new RuntimeException(ex);
			}
			this.ProgressMeter.stepStepper(1);
		}
		ds.setMasterTable(mt);
		ProbeList gpl = mt.createGlobalProbeList(true);
		gpl.getAnnotation().setQuickInfo("This ProbeList was created by the Mayday Processing Pipeline from \n" +
				"ProbeList \""+InputData[0].getName()+"\" of \n" +
				"DataSet \""+InputData[0].getProbeList().getDataSet().getName()+"\"\n\n" +
				"Original annotation of the DataSet follows: \n"+InputData[0].getProbeList().getDataSet().getAnnotation().getQuickInfo()+
				"\n\nOriginal annotation of the ProbeList follows: \n"+InputData[0].getProbeList().getAnnotation().getQuickInfo());
		ds.getProbeListManager().addObject(gpl);
		if (CloneMIO.Value) 
			cloneMIOs(InputData[0].getProbeList().getDataSet(), InputData[0].getProbeList(), ds, gpl);
		
		InputData[0].dismiss();
		//OutputData[0]=InputData[0];
	}
	
	private Vector<MIGroup> createdMIOGroups = new Vector<MIGroup>(); 
	

	public void cloneMIOs(DataSet oldDS, Object oldMIOE, DataSet newDS, Object newMIOE) {

		MIManager newManager = newDS.getMIManager();
		MIManager oldManager = oldDS.getMIManager();
		
		for (MIGroup mg : oldManager.getGroupsForObject(oldMIOE)) {
			MIType mt = mg.getMIO(oldMIOE);
			/* go over all mios in this mioextendable object. clone the mio. if the miogroup has to be cloned as well,
			 * do that. find previously cloned miogroups by name */
			
			// 1) Clone the MIO
			MIType newMIO = mt.clone();
			
			// 2) find the corresponding group or create a new one
  			String mgname = mg.getName();
			MIGroup targetMG = null;
			for (MIGroup newMG : createdMIOGroups)
				if (newMG.getName().equals(mgname) )
					targetMG=newMG;
			if (targetMG==null) {
				targetMG = newManager.newGroup(mg.getMIOType(), mg.getName()); 
				createdMIOGroups.add(targetMG);
			}
			targetMG.add(newMIOE, newMIO);
		}
	}	
	
}
