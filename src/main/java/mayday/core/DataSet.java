package mayday.core;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.datasetmanager.gui.DataSetView;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.types.AnnotationMIO;
import mayday.core.probelistmanager.ProbeListManager;
import mayday.core.probelistmanager.ProbeListManagerFactory;

/*
 * Created on Apr 8, 2003
 */

/**
 * @author gehlenbo
 * @version 
 */
public class DataSet
implements Storable
{
    protected MasterTable masterTable;
    protected ProbeListManager probeListManager;
    protected String name="";
    
    protected MIGroup probeDisplayNameMIGroup;
    protected MIGroup experimentDisplayNameMIGroup;
    
    //MZ: 05.03.2004
    protected DataSetView dataSetView;
    //end MZ
    
	protected EventFirer<DataSetEvent, DataSetListener> eventfirer
		= new EventFirer<DataSetEvent, DataSetListener>() {
		protected void dispatchEvent(DataSetEvent event, DataSetListener listener) {
			listener.dataSetChanged(event);
		}		
	};
   
    protected boolean isSilent; // indicates whether listeners are notified or not
    
    // NG: 2004-11-15
    protected MIManager miManager;
    // end NG
    
    /** do nothing here. This is a non-functional dataset, used by DataSetManagerView during Mayday init
     * @param ignore this parameter is ignored
     */
    public DataSet(boolean ignore) {
    	
    }
    
    
    public DataSet()
    {
        this( new String() );
    }
    
    
    public DataSet( String name )
    {
        this.masterTable = new MasterTable( this );  	
        this.probeListManager = ProbeListManagerFactory.newManagerInstance(this);
        this.name = name;        
        
        this.miManager = new MIManager( this );   
        
        //guarantee that annotation is not null
        setAnnotation(new AnnotationMIO());             
    }
    
    
    public void addDataSetListener( DataSetListener listener )
    {
        eventfirer.addListener( listener );
    }
    
    
    public void removeDataSetListener( DataSetListener listener )
    {
        eventfirer.removeListener( listener );
    }
    
    
    protected void fireDataSetChanged( int change ) {
    	if (!isSilent())
    		eventfirer.fireEvent(new DataSetEvent( this, change ));
    }
    
        
    public boolean isSilent()
    {
        return ( isSilent );
    }
    
    
    public void setSilent( boolean isSilent )
    {
        this.isSilent = isSilent;
    }    
    
    public MasterTable getMasterTable()
    {
        return ( this.masterTable );
    }
    
    //MZ 16.03.2004
    //if this is not possible, the mastertable can know about the
    // dataset but not inverse
    public void setMasterTable(MasterTable m)
    {
        this.masterTable=m;
    }
    
    public ProbeListManager getProbeListManager()
    {
        return ( this.probeListManager );
    }
    
    
    public AnnotationMIO getAnnotation()
    {
  	  try {
  		  return (AnnotationMIO)getMIManager().getGroupsForType("PAS.MIO.Annotation").get(0).getMIO(this);
  	  } catch (Exception e) {
  		  return null;
  	  }
    }


    public String getName() {
  	  return name; 
    }
    
    public void setName(String Name) {
  	  name = Name;
  	  fireDataSetChanged(DataSetEvent.CAPTION_CHANGE);
    }
    
    public void setAnnotation( AnnotationMIO annotation )
    {
    	try {
    		MIGroupSelection<MIType> mgs = getMIManager().getGroupsForType("PAS.MIO.Annotation");
    		MIGroup annotationGroup;
    		if (mgs.size()==0)
    			annotationGroup = getMIManager().newGroup("PAS.MIO.Annotation", "Annotations");
    		else 
    			annotationGroup = mgs.get(0);
  		
    		annotationGroup.add(this,annotation);
    		fireDataSetChanged(DataSetEvent.CAPTION_CHANGE);
    	} catch (Exception e) {
    		System.err.println("Could not annotate DataSet \""+getName()+"\"\n"+e.getMessage());
    	}
    }
    
    
    
    public int compareTo( Object dataSet )
    {
        return ( this.getName().compareTo(
            ((DataSet)dataSet).getName() ) );
    }
    
    
    public boolean equals( Object dataSet )
    {
    	if ((dataSet instanceof DataSet))
    		return ( getName().equals(
    				((DataSet)dataSet).getName() ) );
    	else return false;
    }
    
    
    public void propagateClosing()
    {
        fireDataSetChanged( DataSetEvent.CLOSING_CHANGE );
    }
    
    //MZ 05.03.2004
    /**
     * @return
     */
    
    public DataSetView getDataSetView()
    {
        return dataSetView;
    }
    
    /**
     * @param view
     */	
    
    public void setDataSetView(DataSetView view)
    {
        dataSetView = view;
    }
    //end MZ
    
    
    public MIManager getMIManager()
    {
        return ( this.miManager );
    }
    
    public DataSetManager getDataSetManager()
    {
        return DataSetManager.singleInstance;
    }
    
    public String toString()
    {
        return ( this.getName() );
    }
    
	public void setProbeDisplayNames( MIGroup mioGroup ) {
		probeDisplayNameMIGroup = mioGroup;
        this.getMasterTable().fireMasterTableChanged( MasterTableEvent.OVERALL_CHANGE );
    }

	public MIGroup getProbeDisplayNames() {
		return ( this.probeDisplayNameMIGroup );
	}
	
	public void setExperimentDisplayNames( MIGroup mioGroup ) {
		experimentDisplayNameMIGroup = mioGroup;
        this.getMasterTable().fireMasterTableChanged( MasterTableEvent.OVERALL_CHANGE );
    }

	public MIGroup getExperimentDisplayNames() {
		return ( this.experimentDisplayNameMIGroup );
	}

}
