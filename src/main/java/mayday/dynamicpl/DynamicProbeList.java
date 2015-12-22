package mayday.dynamicpl;

import java.awt.Color;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.dynamicpl.miostore.DynamicProbelistMIO;

public class DynamicProbeList extends ProbeList implements ChangeListener {

	private RuleSet ruleSet;
	private boolean ignoreChanges = false;
	
	protected boolean isCyclic = false;

	public DynamicProbeList( DataSet dataSet ) {
		this(dataSet,null);
	}
	
	public DynamicProbeList(DataSet dataSet, String name) {
		super(dataSet, true);
		if (name!=null)
			setName(name);
		getAnnotation().setInfo("Dynamic Probe List");
		ruleSet = new RuleSet(this);
		ruleSet.addChangeListener(this);
		// add our mio backup
		MIGroup mg = getMIGroup();
		DynamicProbelistMIO dplm = (DynamicProbelistMIO)mg.add(this);
		dplm.setDynamicProbeList(this);
		populate();
	}
	
	protected MIGroup getMIGroup() {
		MIManager mim = getDataSet().getMIManager();
		MIGroupSelection<MIType> mgs = mim.getGroupsForType(DynamicProbelistMIO.myType);
		MIGroup mg;
		if (mgs.size()==0)
			mg = mim.newGroup(DynamicProbelistMIO.myType, "DynamicPL");
		else 
			mg = mgs.get(0);
		return mg;
	}
	
	public RuleSet getRuleSet() {
		return ruleSet;
	}

	public String getName() {
		return (isCyclic?"CYCLIC DEPENDENCY! ":"")+super.getName();
	}
	
	/*
	public void addProbe( Probe probe ) {
		System.err.println("Dynamic Probe Lists manage themselves, thank you very much.");
	}

	public void removeProbe( Probe probe ) {
		System.err.println("Dynamic Probe Lists manage themselves, thank you very much.");
	}

	public void setProbes( Probe[] probes ){
		System.err.println("Dynamic Probe Lists manage themselves, thank you very much.");
	}

	public void setProbe( String name, Probe probe )	{
		System.err.println("Dynamic Probe Lists manage themselves, thank you very much.");
	}

	public void clearProbes(){
		System.err.println("Dynamic Probe Lists manage themselves, thank you very much.");
	}
	*/
	
    public void clearProbes_internal()
    {
        if ( isSticky() )  {
            Object[] l_probes = toArray();
            for ( int i = 0; i < l_probes.length; ++i ) {
                super.removeProbe( (Probe)l_probes[i] ); 
            }
        }        
        probes.clear();        
        if ( isSticky() ) {
            fireProbeListChanged( ProbeListEvent.CONTENT_CHANGE );
        }
    }

	public void stateChanged(ChangeEvent arg0) {
		// reapply all filters
		if (!ignoreChanges)
			populate();
	}
	
	protected boolean isUpdating = false;
	
	protected void populate() {
		if (isUpdating) {
			// cyclical dependency detected
			System.err.println("Dynamic ProbeList \""+getName()+"\" has a cyclical dependency. Stopping update.");
			isCyclic = true;
			return;
		} 
		
		isUpdating = true;
		boolean wasSilent = isSilent();
		setSilent(true);
		clearProbes_internal();
		// refilter, doing only changes that are really necessary
		for (Probe pb : getDataSet().getMasterTable().getProbes().values()) {
			if (ruleSet.passesFilter(pb)==true) {
				if (!super.contains(pb))
					super.addProbe(pb);	
			} else {
				if (super.contains(pb))
					super.removeProbe(pb);
			}			
		}
		setSilent(wasSilent);
		isCyclic = false;
		fireProbeListChanged(ProbeListEvent.CONTENT_CHANGE);
		isUpdating = false;
	}
	
	public static int countProbes(DataSet ds, RuleSet rs) {
		int count=0;
		for (Probe pb : ds.getMasterTable().getProbes().values()) {
			Boolean pf = rs.passesFilter(pb);
			if (pf==null || pf==true) 
				++count;
		}
		return count;
	}

	public boolean isIgnoreChanges() {
		return ignoreChanges;
	}

	public void setIgnoreChanges(boolean ignoreChanges) {
		if (this.ignoreChanges && !ignoreChanges)
			populate();
		this.ignoreChanges = ignoreChanges;
	}
	
    public void fireProbeListChanged( int change ) {
//        if ( isSilent() )
//            return;
//        System.out.println("*** DPL "+getName()+" firing to "+eventfirer.getListeners().size()+" listeners");
//        for (ProbeListListener el : eventfirer.getListeners())
//        	if (el instanceof AbstractDataProcessor)
//        		System.out.println("--- DPLAP: "+((AbstractDataProcessor)el).getDynamicProbeList()+" ; "+el);
//        	else
//        		System.out.println("-- "+el);
        super.fireProbeListChanged(change);
    }

    
    public void addProbeListListener( ProbeListListener listener ) {
//        System.out.println("*** DPL "+getName()+" contains "+eventfirer.getListeners().size()+" listeners");
//        for (ProbeListListener el : eventfirer.getListeners()) 
//        	if (el instanceof AbstractDataProcessor)
//        		System.out.println("--- DPLAP: "+((AbstractDataProcessor)el).getDynamicProbeList()+" ; "+el);
//        	else
//        		System.out.println("-- "+el);    
//        ProbeListListener el = listener;
//    	if (el instanceof AbstractDataProcessor)
//    		System.out.println("AND: DPLAP: "+((AbstractDataProcessor)el).getDynamicProbeList()+" ; "+el);
//    	else
//    		System.out.println("AND: "+el);
        super.addProbeListListener(listener);
    }
    
    
    public void removeProbeListListener( ProbeListListener listener ) {
//        System.out.println("*** DPL "+getName()+" contains "+eventfirer.getListeners().size()+" listeners");
//        for (ProbeListListener el : eventfirer.getListeners()) 
//        	if (el instanceof AbstractDataProcessor)
//        		System.out.println("--- DPLAP: "+((AbstractDataProcessor)el).getDynamicProbeList()+" ; "+el);
//        	else
//        		System.out.println("-- "+el);
//        ProbeListListener el = listener;
//    	if (el instanceof AbstractDataProcessor)
//    		System.out.println("NOT: DPLAP: "+((AbstractDataProcessor)el).getDynamicProbeList()+" ; "+el);
//    	else
//    		System.out.println("NOT: "+el);
    	super.removeProbeListListener(listener);
    }
    
    public void propagateClosing() {
    	// remove listeners set by all my processors
    	ruleSet.dispose();
    	super.propagateClosing();
    }
	
    public Object clone() {
    	DynamicProbeList newDPL = new DynamicProbeList(getDataSet());
    	newDPL.setSticky(isSticky());
    	newDPL.setName(getName());
    	newDPL.setAnnotation(getAnnotation().clone());
    	newDPL.setColor( new Color( getColor().getRGB() ));
    	
    	RuleSet newRS = newDPL.ruleSet;
    	newRS.fromStorageNode(getRuleSet().toStorageNode());

        return ( newDPL );
    }

}
