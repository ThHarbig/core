package mayday.core;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.meta.types.AnnotationMIO;

public class Experiment implements Comparable<Experiment> {
	
	protected String name;
	protected MasterTable mt;
	
	public Experiment(MasterTable mata, String exname) {
		name =exname;
		mt = mata;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisplayName()	{
		String displayName = this.getName();
		
		if (mt!=null) {
			MIGroup mg = mt.getDataSet().getExperimentDisplayNames();
			if (mg!=null) {
				MIType mt = mg.getMIO(this);
				if (mt==null || mt.toString().trim().length()==0)
					displayName+="*";
				else
					displayName=mt.toString();
			}
		}
		
		return displayName;
	}
	
	public String toString() {
		return getName();
	}
	
	public MasterTable getMasterTable() {
		return mt;
	}
	
	public int getIndex() {
		return mt.getExperiments().indexOf(this);
	}
	
    public AnnotationMIO getAnnotation()
    {
  	  try {
  		  return (AnnotationMIO)getMasterTable().getDataSet().getMIManager().getGroupsForType("PAS.MIO.Annotation").get(0).getMIO(this);
  	  } catch (Exception e) {
  		  return null;
  	  }
    }

    void setName0(String name2) {
    	name = name2;
		mt.fireMasterTableChanged(MasterTableEvent.EXPERIMENT_ORDERING_CHANGED);
    }
    
	public void setName(String name2) {
		name = name2;
		ExperimentNamer.ensureNameUniqueness(this);
	}
	
    public void setAnnotation( AnnotationMIO annotation )
    {
    	try {
    		MIGroupSelection<MIType> mgs = mt.getDataSet().getMIManager().getGroupsForType("PAS.MIO.Annotation");
    		MIGroup annotationGroup;
    		if (mgs.size()==0)
    			annotationGroup = mt.getDataSet().getMIManager().newGroup("PAS.MIO.Annotation", "Annotations");
    		else 
    			annotationGroup = mgs.get(0);
  		
    		annotationGroup.add(this,annotation);
    	} catch (Exception e) {
    		System.err.println("Could not annotate Experiment \""+getName()+"\"\n"+e.getMessage());
    	}
    }

	@Override
	public int compareTo(Experiment o) {
		return Integer.valueOf(getIndex()).compareTo(o.getIndex());
	}
	

}
