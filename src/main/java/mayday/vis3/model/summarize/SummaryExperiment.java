package mayday.vis3.model.summarize;
import mayday.core.Experiment;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.meta.types.AnnotationMIO;
import mayday.vis3.model.wrapped.WrappedExperiment;
import mayday.vis3.model.wrapped.WrappedMasterTable;


/** A replacement for WrappedExperiment that can not explicitely be assigned to ONE parent experiment  
 * @author battke
 *
 */
public class SummaryExperiment extends WrappedExperiment
{

	// === REAL FUNCTIONS ===

	public SummaryExperiment(String name, WrappedMasterTable smt, String annot) {
		super(name, smt);
		setAnnotation(new AnnotationMIO(annot,"Summary experiment"));		
	}

	public int hashCode() {
		return name.hashCode();
	}
	
	public boolean equals(Object o) {
		if (o instanceof SummaryExperiment) {
			SummaryExperiment se = ((SummaryExperiment)o);
			return (se.name.equals(name)) && (se.mt==mt);
		}
		return false;
	}
	
	public Experiment getWrappedExperiment() {
		throw new RuntimeException("SummaryExperiments can not be unwrapped.");
	}

    public AnnotationMIO getAnnotation()
    {
  	  try {
  		  return (AnnotationMIO)getMasterTable().getDataSet().getMIManager().getGroupsForType("PAS.MIO.Annotation").get(0).getMIO(this);
  	  } catch (Exception e) {
  		  return null;
  	  }
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
	
	public String getName() {
		return name;
	}

	public String getDisplayName()	{
		return name;
	}

	public void setName(String name2) {
		name = name2;
	}

}
