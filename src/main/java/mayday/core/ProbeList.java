package mayday.core;
import java.awt.Color;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.meta.types.AnnotationMIO;
import mayday.core.probelistmanager.UnionProbeList;

/**
 * @author neil
 * @version 
 */
@SuppressWarnings("unchecked")
public class ProbeList
implements Comparable, Storable, Cloneable, Iterable<Probe>
{
	
    public static final int AND_MODE = 1;
    public static final int OR_MODE = 2;
    public static final int XOR_MODE = 3;

    private String name="";
    private Color color;
	private DataSet dataSet;
	
    protected Probe[] probesAsArray;
    protected int numberOfProbes = -1; // cached value for speedup with ConcurrentSkipListMap which has an O(n) size() function
     
    protected Map<String,Probe> probes; // a TreeMap, or better, a ConcurrentSkipListMap
    
    protected UnionProbeList parent;
    
    private boolean isSticky; // sticky probelists add themselves to their contained probes 
    private boolean isSilent; // indicates whether listeners are notified or not

    
    private EventFirer<ProbeListEvent, ProbeListListener> eventfirer
    = new EventFirer<ProbeListEvent, ProbeListListener>() {
    	protected void dispatchEvent(ProbeListEvent event, ProbeListListener listener) {
    		listener.probeListChanged(event);
    	}		
    };
    
  
    public ProbeList( DataSet dataSet, boolean isSticky )
    {
        this.color = MaydayDefaults.DEFAULT_PROBE_LIST_COLOR;
        this.dataSet = dataSet;
        this.isSticky = isSticky;
        this.isSilent = false;
    }
    
    @Override
    public int hashCode() {
    	return super.hashCode();
    }

       
    /** This function guarantees that it doesn't return null */
    public AnnotationMIO getAnnotation() 
    {
    	AnnotationMIO amio = null;
    	MIGroupSelection<MIType> mgs = getDataSet().getMIManager().getGroupsForType("PAS.MIO.Annotation");
    	if (mgs.size()>0) 
    		amio = (AnnotationMIO)mgs.get(0).getMIO(this);
    	if (amio==null) {
    		amio = new AnnotationMIO();
    		setAnnotation(amio);
    	}
    	return amio;
    }

    
    /** Remove probelist from MIManager */ 
    public void propagateClosing() {
		clearProbes();				
		// remove pl from all mio groups
		for (MIGroup mg : dataSet.getMIManager().getGroupsForObject(this)) {
			mg.remove(this);
		}
    }

    public String getName() {
  	  return name; 
    }
    
    public void setName(String Name) {
  	  name = Name;
  	  fireProbeListChanged(ProbeListEvent.LAYOUT_CHANGE);
    }
    
    public void setAnnotation( AnnotationMIO annotation )
    {
    	try {
    		MIGroupSelection<MIType> mgs = getDataSet().getMIManager().getGroupsForType("PAS.MIO.Annotation");
    		MIGroup annotationGroup;
    		if (mgs.size()==0)
    			annotationGroup = getDataSet().getMIManager().newGroup("PAS.MIO.Annotation", "Annotations");
    		else 
    			annotationGroup = mgs.get(0);
    		
    		AnnotationMIO oldAnnotation = (AnnotationMIO)annotationGroup.getMIO(this);
    		
    		if ( (oldAnnotation==null) ||         		
    				( !oldAnnotation.getQuickInfo().equals( annotation.getQuickInfo() ) ) ||
    				( !oldAnnotation.getInfo().equals( annotation.getInfo() ) ) )
    		{    
    			annotationGroup.add(this,annotation);
    			if ( isSticky() )
    				fireProbeListChanged( ProbeListEvent.ANNOTATION_CHANGE );
    		} else 
    			annotationGroup.add(this,annotation);
    	} catch (Exception e) {
    		System.err.println("Could not annotate ProbeList \""+getName()+"\"\n"+e.getMessage());
    	}

    }
    
    
    public void addProbeListListener( ProbeListListener listener ) {
    	synchronized(this) {
    		eventfirer.addListener( listener );
    	}
    }
    
    
    public void removeProbeListListener( ProbeListListener listener ) {
    	synchronized(this) {
    		eventfirer.removeListener( listener );
    	}
    }
    
    public List<ProbeListListener> getProbeListListeners() {
    	return Collections.unmodifiableList(new ArrayList<ProbeListListener>(eventfirer.getListeners()));
    }
    
    
    // 090116 fb: this is public so behind-the-scenes changes like experiment reordering can be propagated
    public void fireProbeListChanged( int change ) {
        if ( isSilent() )
            return;
    	synchronized(this) {    		
    		eventfirer.fireEvent(new ProbeListEvent( this, change ));
    	}
    }
    
    
    
    public Color getColor() {
        return ( this.color );
    }
    
    
    public void setColor( Color color ) {
        if ( !this.color.equals( color ) ) {
            this.color = color;            
            if ( isSticky() )
                fireProbeListChanged( ProbeListEvent.LAYOUT_CHANGE );
        }
    }
    
    protected void add_Internal(Probe probe) {
    	synchronized (this) {
    		toMap().put( probe.getName(), probe );
            invalidateCache();			
		}
    }
    
	public void addProbe( Probe probe ) throws RuntimeException {
        if ( toMap().containsKey( probe.getName() ) ) {
            throw ( new RuntimeException( "Unable to add probe \"" + 
                probe.getName() + 
                "\" to probe list \"" + 
                this.getName() + 
            "\". It is already contained in this probe list.") );	
        }
        
        add_Internal(probe);
        
        if ( isSticky() ) {
            probe.addProbeList( this );
            fireProbeListChanged( ProbeListEvent.CONTENT_CHANGE );    
        }
    }
    
    
	public void setProbes( Probe[] probes ) throws RuntimeException {
		// first check if all names are unique
		HashSet<String> names = new HashSet<String>();
        for ( int i = 0; i < probes.length; ++i ) {
        	if (!names.add(probes[i].getName())) {
        		throw new RuntimeException("Probe names are not unique. Probe \"" + probes[i].getName() + 
                    "\" appears more than one for probe list \"" + 
                    this.getName() + 
        			"\".") ; 
            }
        }
         
        for (int i = 0; i < probes.length; ++i) {
           add_Internal(probes[i]);
            if ( isSticky() )
                probes[i].addProbeList( this );
        }

        if ( isSticky() )
            fireProbeListChanged( ProbeListEvent.CONTENT_CHANGE );    
    }
	
	public void setProbes( Collection<Probe> probes ) throws RuntimeException {
		// first check if all names are unique
		HashSet<String> names = new HashSet<String>();
        for ( Probe pb : probes ) {
        	if (!names.add(pb.getName())) {
        		throw new RuntimeException("Probe names are not unique. Probe \"" + pb.getName() + 
                    "\" appears more than one for probe list \"" + 
                    this.getName() + 
        			"\".") ; 
            }
        }
         
        for ( Probe pb : probes ) {
        	add_Internal(pb);
        	if ( isSticky() )
        		pb.addProbeList( this );
        }

        if ( isSticky() )
            fireProbeListChanged( ProbeListEvent.CONTENT_CHANGE );    
    }
    
    protected void remove_Internal(String name) {
    	synchronized (this) {
    		if (probes!=null) {
    			probes.remove(name);
    			invalidateCache();
    		}
		}
    }
	
    public void removeProbe( Probe probe )
    throws RuntimeException
    {
        remove_Internal( probe.getName() );
        if ( isSticky() ) { 
            probe.removeProbeList( this );            
            fireProbeListChanged( ProbeListEvent.CONTENT_CHANGE );
        }
    }
    
    
    public DataSet getDataSet() {
        return ( this.dataSet );
    }
    
    
    public void setDataSet( DataSet dataSet ) {
        this.dataSet = dataSet;
    }
    
    
    public void setOperation( ProbeList probeListA, ProbeList probeListB, int mode )
    {
        String l_modeString;
        
        switch ( mode )
        {
            case AND_MODE:
                l_modeString = "and";
                break;
            case OR_MODE:
                l_modeString = "or";
                break;
            case XOR_MODE:
                l_modeString = "xor";
                break;
            default:
                return;    	  
        }
        
        if ( probeListA.getDataSet().getMasterTable() != probeListB.getDataSet().getMasterTable() )
        {
            throw ( new RuntimeException( "Unable to perform operation \"" + l_modeString + "\" on probe lists \"" +
                probeListA.getName() +
                "\" and \"" +
                probeListB.getName() +
            "\", the probe lists have different master tables." ) );    	
        }
        
        if ( probeListA.getDataSet().getMasterTable() != getDataSet().getMasterTable() )
        {
            throw ( new RuntimeException( "Unable to perform operation \"" + l_modeString + "\" on probe lists \"" +
                probeListA.getName() +
                "\" and \"" +
                getName() +
            "\", the probe lists have different master tables." ) );
        }
        
        BitSet l_bitSetA = probeListA.toBitSet();
        BitSet l_bitSetB = probeListB.toBitSet();
        
        // perform the set operation on the 
        switch ( mode )
        {
            case AND_MODE:
                l_bitSetA.and( l_bitSetB );
                break;
            case OR_MODE:
                l_bitSetA.or( l_bitSetB );
                break;
            case XOR_MODE:
                l_bitSetA.xor( l_bitSetB );
                break;
            default:
                return;    	  
        }
        
        // get all probes from the master table
        Object[] l_probes = getDataSet().getMasterTable().getProbes().values().toArray();
        
        // remove all probes from this probe list and release them
        clearProbes();
        
        for ( int i = 0; i < l_probes.length; ++i )
            if ( l_bitSetA.get( i ) )
                addProbe( (Probe)l_probes[i] ); 
        
        if ( isSticky() )
            fireProbeListChanged( ProbeListEvent.CONTENT_CHANGE );
    }
    
    
    public ProbeList invert( boolean isSticky )
    {    
        ProbeList l_newProbeList = new ProbeList( getDataSet(), isSticky );    
        BitSet l_bitSet = toBitSet();
        
        // get all probes from the master table
        Object[] l_probes = getDataSet().getMasterTable().getProbes().values().toArray();
        
        // a trick to allow this being set to 11111....111
        l_bitSet.set( l_probes.length );   
        
        // invert
        l_bitSet.flip( 0, l_bitSet.length() );
        
        
        for ( int i = 0; i < l_probes.length; ++i )
            if ( l_bitSet.get( i ) )
                l_newProbeList.addProbe( (Probe)l_probes[i] ); 
        
        return ( l_newProbeList );   
    }
    
    
    public int getNumberOfProbes() {
    	int i = numberOfProbes;
    	while (numberOfProbes<0) { 
       		this.numberOfProbes = i = toMap().size();
       		// could be reset to -1 now because of concurrent modifications
    	}
    	return i; // do not return numberOfProbes as this may have changed AGAIN du to concurrent modifications
    }
    
      
    public String toString() {
        return getName();
    }
    
    
    public String toDebugString() {
        String l_string = new String();
        
        //l_string += this.getAnnotation().getName() + "\n";
        //l_string += this.getAnnotation().getQuickInfo() + "\n";
        //l_string += this.getAnnotation().getInfo() + "\n";
        
        for ( int i = 0; i < getNumberOfProbes(); ++i ) {
            l_string += getProbe( i ).toString() + "\n";
        }
        
        //l_string += getNumberOfProbes();
        
        return ( l_string );
    }
    
    public Probe[] toArray() {  // speed this up for getProbe(int)
    	synchronized (this) {
    		Probe[] array = probesAsArray;
        	while (probesAsArray == null) {
        		probesAsArray = array = toMap().values().toArray(new Probe[0]);
        		// could be null again if probes were added/removed in the meantime
        	} 
        	return array; // do not return probesAsArray as this may have changed AGAIN due to concurrent modifications    			
		}
    }
    
    public Double getMaxValue() {
        Double l_max = Double.NEGATIVE_INFINITY;
        Object[] l_array = toArray();
        boolean l_hasMax = false;
        
        for ( int i = 0; i < l_array.length; ++i ) {
            Double l_double = ((Probe)l_array[i]).getMaxValue();
            
            
            if ( l_double != null && !l_double.equals(Double.NaN)) {
                if ( Double.compare( l_double.doubleValue(), l_max.doubleValue() ) > 0 )
                    l_max = l_double;
                l_hasMax = true;
            }
        }
        
        if ( l_hasMax )
            return ( l_max );
        
        return ( null );
    }
    
    
    public Double getMaxValue( int experiment ) {
        if ( experiment >= getDataSet().getMasterTable().getNumberOfExperiments() ) {
            throw ( new RuntimeException( "Experiment " +
                experiment +
            " does not exist." ) );
        }
        
        Double l_max = Double.NEGATIVE_INFINITY;
        Object[] l_array = toArray();
        boolean l_hasMax = false;
        
        for ( int i = 0; i < l_array.length; ++i ) {
            Double l_double = ((Probe)l_array[i]).getValue( experiment );			
            
            if ( l_double != null  && !l_double.equals(Double.NaN)) {
                if ( Double.compare( l_double.doubleValue(), l_max.doubleValue() ) > 0 )
                    l_max = l_double;               
                l_hasMax = true;
            }
        }
        
        if ( l_hasMax )
            return ( l_max );
        
        return ( null );
    }
    
    
    
    public Double getMinValue() {
        Double l_min = Double.POSITIVE_INFINITY;
        Object[] l_array = toArray();
        boolean l_hasMin = false;
        
        for ( int i = 0; i < l_array.length; ++i )  {
            Double l_double = ((Probe)l_array[i]).getMinValue();
            
            if ( l_double != null && !l_double.equals(Double.NaN)) {
                if ( Double.compare( l_double.doubleValue(), l_min.doubleValue() ) < 0 )
                    l_min = l_double;
                
                l_hasMin = true;
            }
        }
        
        if ( l_hasMin )
            return ( l_min );
        
        return ( null );
    }
    
    
    public Double getMinValue( int experiment )
    throws RuntimeException
    {
        if ( experiment >= getDataSet().getMasterTable().getNumberOfExperiments() ) {
            throw ( new RuntimeException( "Experiment " +
                experiment +
            " does not exist." ) );
        }
        
        Double l_min = Double.POSITIVE_INFINITY;
        Object[] l_array = toArray();
        boolean l_hasMin = false;
        
        for ( int i = 0; i < l_array.length; ++i ) {
            Double l_double = ((Probe)l_array[i]).getValue( experiment );
            
            if ( l_double != null  && !l_double.equals(Double.NaN)) {
                if ( Double.compare( l_double.doubleValue(), l_min.doubleValue() ) < 0 )
                    l_min = l_double;
                
                l_hasMin = true;
            }
        }
        
        if ( l_hasMin )
            return ( l_min );
        
        return ( null );
    }
    
    
	public ProbeList.Statistics getStatistics()
    {
        Probe l_meanProbe = new Probe( getDataSet().getMasterTable() );
        Probe l_medianProbe = new Probe( getDataSet().getMasterTable() );
        Probe l_q1Probe = new Probe( getDataSet().getMasterTable() );
        Probe l_q3Probe = new Probe( getDataSet().getMasterTable() );
        
        // compute the mean for each experiment
        Object[] l_probes = toArray();
        
        for ( int i = 0; i < getDataSet().getMasterTable().getNumberOfExperiments(); ++i )
        {
            ArrayList l_list = new ArrayList();
            int l_counter = 0; // counts all non-null experiments
            double l_sum = 0;
            
            for ( int j = 0; j < l_probes.length; ++j )
            {
                if ( ((Probe)l_probes[j]).getValue( i ) != null )
                {
                    l_list.add( ((Probe)l_probes[j]).getValue( i ) );
                    
                    // mean
                    l_sum += ((Probe)l_probes[j]).getValue( i ).doubleValue();
                    ++l_counter;
                }
            }
            
            // mean
            if ( l_counter > 0 )
            {
                l_meanProbe.addExperiment( new Double( ((double)l_sum)/((double)l_counter) ) );
            }
            else
            {
                l_meanProbe.addExperiment( null );
            }
            
            
            // sort the list
            Collections.sort( l_list );
            
            // compute median      
            double l_median = 0.0;
            
            if ( l_list.size() > 0 )
            {
                if ( ( l_list.size() % 2 ) != 0 )
                {
                    l_median = ((Double)l_list.get( ( l_list.size() - 1 ) / 2 )).doubleValue();
                    
                    l_medianProbe.addExperiment( new Double( l_median ) );          
                }
                else
                {
                    l_median = ((Double)l_list.get( ( l_list.size() / 2 ) - 1 ) ).doubleValue() +
                    ((Double)l_list.get( ( l_list.size() / 2 ) ) ).doubleValue();
                    
                    l_median /= 2.0; 
                    
                    l_medianProbe.addExperiment( new Double( l_median ) );          
                }
            }
            else
            {
                l_medianProbe.addExperiment( null );
            }      
            
            int l_quartile;
            double l_bound;
            
            // compute q1      
            l_quartile = 1;
            l_bound = 0.0;
            
            if ( l_list.size() > 0 )
            {
                // determine the index
                int l_index = (int)Math.floor( ((double)l_list.size())*0.25*(4-l_quartile) ); 
                
                l_bound = ((Double)l_list.get( l_index )).doubleValue();
                
                l_q1Probe.addExperiment( new Double( l_bound ) );          
            }
            else
            {
                l_q1Probe.addExperiment( null );
            }      
            
            // compute q3     
            l_quartile = 3;
            l_bound = 0.0;
            
            if ( l_list.size() > 0 )
            {
                // determine the index
                int l_index = (int)Math.floor( ((double)l_list.size())*0.25*(4-l_quartile) ); 
                
                l_bound = ((Double)l_list.get( l_index )).doubleValue();
                
                l_q3Probe.addExperiment( new Double( l_bound ) );          
            }
            else
            {
                l_q3Probe.addExperiment( null );
            }      
        }
        
        return ( new ProbeList.Statistics( l_meanProbe, l_medianProbe, l_q1Probe, l_q3Probe ) );
    }
    
	
	public static Probe getMean(Object[] l_probes, DataSet ds) {
		Probe l_meanProbe = new Probe( ds.getMasterTable() );
        
        // compute the mean for each experiment
        
        for ( int i = 0; i < ds.getMasterTable().getNumberOfExperiments(); ++i ) {
            int l_counter = 0; // counts all non-null experiments
            double l_sum = 0;
            
            for ( int j = 0; j < l_probes.length; ++j ) {
            	Probe pb = (Probe)l_probes[j];
                if ( pb.getValue( i ) != null ) {
                    l_sum += pb.getValue( i );
                    ++l_counter;
                }
            }
            
            if ( l_counter > 0 )
                l_meanProbe.addExperiment( new Double( ((double)l_sum)/((double)l_counter) ) );
            else
                l_meanProbe.addExperiment( null );
        }
        
        return ( l_meanProbe );
	}
    
    public Probe getMean() {
        return getMean(toArray(), getDataSet());
    }
    
    
    public static Probe getMedian(Object[] l_probes, DataSet ds)
    {
        Probe l_medianProbe = new Probe( ds.getMasterTable() );
        
        for ( int i = 0; i < ds.getMasterTable().getNumberOfExperiments(); ++i ) {
            ArrayList l_list = new ArrayList();
            
            for ( int j = 0; j < l_probes.length; ++j )
                if ( ((Probe)l_probes[j]).getValue( i ) != null )
                    l_list.add( ((Probe)l_probes[j]).getValue( i ) );
            
            // sort the list
            Collections.sort( l_list );
            
            double l_median = 0.0;
            
            if ( l_list.size() > 0 ) {
                if ( ( l_list.size() % 2 ) != 0 ) {
                    l_median = ((Double)l_list.get( ( l_list.size() - 1 ) / 2 )).doubleValue();
                    l_medianProbe.addExperiment( new Double( l_median ) );          
                } else {
                    l_median = ((Double)l_list.get( ( l_list.size() / 2 ) - 1 ) ).doubleValue() +
                    ((Double)l_list.get( ( l_list.size() / 2 ) ) ).doubleValue();
                    l_median /= 2.0; 
                    l_medianProbe.addExperiment( new Double( l_median ) );          
                }
            } else {
                l_medianProbe.addExperiment( null );
            }      
        }
        
        return ( l_medianProbe );
    }
    
    
    public Probe getMedian() {
    	return getMedian(toArray(), getDataSet());
    }
    
    public static Probe getQuartile( Object[] l_probes, DataSet ds, int l_quartile )
    throws RuntimeException
    {
        Probe l_quartileProbe = new Probe( ds.getMasterTable() );
        
        if ( ( l_quartile < 1 ) || ( l_quartile > 4 ) )
            throw ( new RuntimeException( "Unable to compute quartile " + l_quartile + "." ) );
        
        // compute the mean for each experiment        
        for ( int i = 0; i < ds.getMasterTable().getNumberOfExperiments(); ++i ) {
            ArrayList l_list = new ArrayList();
            
            for ( int j = 0; j < l_probes.length; ++j )
                if ( ((Probe)l_probes[j]).getValue( i ) != null )
                    l_list.add( ((Probe)l_probes[j]).getValue( i ) );
            
            // sort the list DESCENDING!
            Collections.sort( l_list );
            Collections.reverse( l_list );
            
            double l_bound = 0.0;
            
            if ( l_list.size() > 0 ) {
                // determine the index
                int l_index = (int)Math.floor( ((double)l_list.size())*0.25*(4-l_quartile) ); 
                l_bound = ((Double)l_list.get( l_index )).doubleValue();
                l_quartileProbe.addExperiment( new Double( l_bound ) );          
            } else {
                l_quartileProbe.addExperiment( null );
            }
        }
        
        return ( l_quartileProbe );
    }
    
    public Probe getQuartile(int l_quartile) {
    	return getQuartile(toArray(),getDataSet(),l_quartile);
    }
    
    
    public Probe getProbe( int index )
    throws RuntimeException
    {
        Object[] l_probes = toArray();
        
        if ( index >= l_probes.length )
            throw ( new RuntimeException( "Probe with index " + index + " does not exist." ) );
        
        return ( (Probe)l_probes[index] );
    }
    
    
    public Probe getProbe( String name )
    throws RuntimeException
    {
        if ( !contains(name) )
            throw ( new RuntimeException( "Probe \"" + name + "\" does not exist." ) );
        
        return ( toMap().get( name ) );
    }
    
    
	public void setProbe( String name, Probe probe )
    throws RuntimeException
    {
        if ( !contains(name) )
            throw ( new RuntimeException( "Probe \"" + name + "\" does not exist." ) );
        
        probe.setName(name);
        add_Internal(probe);
        
        if ( isSticky() )
            fireProbeListChanged( ProbeListEvent.CONTENT_CHANGE );
    }
    
    
    public boolean contains( String name ) {
        return toMap().containsKey( name );
    }
    
    
    public boolean contains( Probe probe ) {
        return contains( probe.getName() ) ;
    }
    
    public void clear_internal() {
    	synchronized (this) {
    		if (probes!=null)
    			probes.clear();			
		}
    }
    
    
    public void clearProbes() {    	
    	synchronized(this) {
            if ( isSticky() ) {
                for ( Probe pb : toMap().values() ) {
                	pb.removeProbeList(this);
                }
            }
            clear_internal();
            invalidateCache();
    	}
        
        if ( isSticky() )
            fireProbeListChanged( ProbeListEvent.CONTENT_CHANGE );
    }
    
    
    public void clearAnnotation()
    {
    	this.getAnnotation().clear();
        if ( isSticky() )
            fireProbeListChanged( ProbeListEvent.ANNOTATION_CHANGE );
    }
    
    
    public void clearLayout() {
        setColor(MaydayDefaults.DEFAULT_PROBE_LIST_COLOR);
    }
        
    
    protected BitSet toBitSet()
    {
        // initialize a bit set the size of the master table
        BitSet l_bitSet = new BitSet( getDataSet().getMasterTable().getNumberOfProbes() );
        
        // get all probes from the master table
        Object[] l_probes = getDataSet().getMasterTable().getProbes().values().toArray();
        
        for ( int i = 0; i < l_probes.length; ++i )
            if ( contains( (Probe)l_probes[i] ) )
                l_bitSet.set( i );
        
        return ( l_bitSet );
    }
    
    
    public int compareTo( Object probeList )
    {

    	if (probeList==null) {
    		//System.err.println("Someone is trying to compare a ProbeList (\""+getName()+"\") to a null object. This is the current stack");
    		//Thread.dumpStack();
    		return (-1); 
    	}
    	
        if(!(probeList instanceof ProbeList))
        {
        	return (-1);
//            throw new ClassCastException(
//                "Cannot compare to an instance of "+probeList.getClass().getName()+".");
        }
        
        ProbeList otherProbeList = ((ProbeList)probeList);
        
        if ( otherProbeList.getName() != null && getName()!=null) {
        	int nc = getName().compareTo( otherProbeList.getName() );
        	if (nc != 0)
        		return nc;
        }
        
        // if the names are equal or any of the two probelists has no name, check for object identity
        	
        if (this == otherProbeList)
        	return 0;
        
        // compare the hashcodes that we get from object.hashCode()
        return Integer.valueOf(hashCode()).compareTo(otherProbeList.hashCode());
        
    }
    
    
    public boolean equals( Object probeList ) {
    	return compareTo(probeList)==0;
    }
    
    /** creates a clone of this probelists which copies ALL properties EXCEPT the list's name
     * @see cloneProperly() */
    public Object clone()
    {
        ProbeList l_probeList = new ProbeList( getDataSet(), isSticky() );
        l_probeList.setAnnotation( getAnnotation().clone() );
        l_probeList.setColor( new Color( color.getRGB() ) );    
        Object[] l_probes = toArray();
        for ( int i = 0; i < l_probes.length; ++i )
            l_probeList.addProbe( (Probe)l_probes[i] );
        return l_probeList;
    }
    
    /** creates a clone of this probelists which copies ALL properties EXCEPT the list's name
     * and sets the name to a random new name based on the original list's name.
     * @see clone()
     * @return
     */
    public Object cloneProperly() {
    	ProbeList l_probeList = (ProbeList)clone();
        l_probeList.setName( this.getName()+(new Random()).nextInt() );
        return l_probeList;
    }
    
    public boolean isSticky() {
        return ( isSticky );
    }
    
    
    public void setSticky( boolean isSticky ) {
    	// modify probes according to stickyness
    	boolean hasChanged = this.isSticky^isSticky;
    	if (hasChanged) {
    		if (isSticky) {
    			for (Object op : toCollection()) {
    				((Probe)op).addProbeList(this);   				
    			}
    		} else {
    			for (Object op : toCollection()) {
    				((Probe)op).removeProbeList(this);   				
    			}    			
    		}
    	}
        this.isSticky = isSticky;
        
    }
    
    public Collection<Probe> toCollection() {
    	return toMap().values();
    }
    
    public boolean isSilent() {
        return ( isSilent );
    }
    
    
    public void setSilent( boolean isSilent ) {
        this.isSilent = isSilent;
    }
    
    
    //MZ: 2005-11-08
    /**
     * Get a set of the probes contained in this probe list.
     * 
     * <p>
     * Note: The set is not just another view on those probes,
     * i.e. changes at the set remain invisible for this probe list.
     * 
     * @return A set of the probes contained in this probe list.
     * 
     */
	public synchronized Set<Probe> getAllProbes() {
        return new TreeSet<Probe>(toCollection());
    }
    //end MZ.
    
    
	protected static Object[] mergeProbeLists0(List probeLists, MasterTable masterTable) {
		ProbeList uniqueProbeList = new ProbeList( masterTable.getDataSet(), false );				
        // this list is sorted according to the sorting of the layers, top layer at the top of the list
        List<Probe> uniqueProbes = new ArrayList<Probe>();
        // create a unique subset of the input probe lists
        for ( int i = 0; i < probeLists.size(); ++i ) {
          ProbeList newProbeList = new ProbeList( masterTable.getDataSet(), false );    
          ProbeList probeList = (ProbeList)probeLists.get( i );          
          // extract new probes
          newProbeList.setOperation( uniqueProbeList.invert( false ), probeList, ProbeList.AND_MODE );
          // store new probes
          uniqueProbeList.setOperation( uniqueProbeList, probeList, ProbeList.OR_MODE );          
          uniqueProbes.addAll( 0, newProbeList.toCollection() );
        }        
        return new Object[]{uniqueProbes, uniqueProbeList};
	}
	
    /** 
     * Merge several probelists into one list of unique probes. The new ProbeList is not sticky, i.e. 
     * the Probes don't know they're contained in the new list. This allows the new list to be in a different dataset
     * than any one of the input lists.  The new list is sorted according to the sorting of the layers, top layer at 
     * the top of the list
     */
    public static ProbeList createUniqueProbeList(java.util.List probeLists) {
    	if (probeLists.size()==0)
    		return null;
		if (probeLists.size()==1) {
			// create a copy of the input probelist. do not use clone() to avoid creating a new DPL
			ProbeList src = (ProbeList)probeLists.get(0);
			ProbeList ret = new ProbeList(src.getDataSet(), false);
	        ret.setAnnotation( src.getAnnotation().clone() );
	        ret.setColor( src.getColor() );    
			ret.setProbes(src.getAllProbes());
			return ret;
		}
			
    	return (ProbeList)mergeProbeLists0(probeLists, ((ProbeList)probeLists.get(0)).getDataSet().getMasterTable())[1];
    }
    
    /**
     * @param probeLists
     * @param masterTable
     * @return
     * Merge all ProbeLists in probeLists to one List<Probes> of unique probes. The new list is sorted according to 
     * the sorting of the layers, top layer at the top of the list
     */
	public static List<Probe> mergeProbeLists(List probeLists, MasterTable masterTable) {
    	if (probeLists.size()==0)
    		return Collections.emptyList();
        return (List<Probe>)mergeProbeLists0(probeLists, masterTable)[0];
    }
    
    
    
    /** returns a probelist by computing the set operation large\small **/
    public static ProbeList setOperationMinus(ProbeList large, ProbeList small, boolean isSticky) {
    	Set<Probe> input = large.getAllProbes();
    	input.removeAll(small.getAllProbes());
    	ProbeList resultProbeList = new ProbeList(large.getDataSet(), isSticky);
		resultProbeList.setName(large.getName()+" \\ "+small.getName());
		resultProbeList.setProbes(input);
		return resultProbeList;
    }
    
    public static class Statistics
    {
        private Probe mean;
        private Probe median;
        private Probe q1; 
        private Probe q3;
        
        
        public Statistics( Probe mean, Probe median, Probe q1, Probe q3 )
        {
            this.mean = mean;
            this.median = median;
            this.q1 = q1;
            this.q3 = q3;
        }
        
        
        public void setMedian( Probe median )
        {
            this.median = median;
        }
        
        
        public Probe getMedian()
        {
            return ( this.median );
        }
        
        
        public void setMean( Probe mean )
        {
            this.mean = mean;
        }
        
        
        public Probe getMean()
        {
            return ( this.mean );
        }
        
        
        public void setQ1( Probe q1 )
        {
            this.q1 = q1;
        }
        
        
        public Probe getQ1()
        {
            return ( this.q1 );
        }
        
        
        public void setQ3( Probe q3 )
        {
            this.q3 = q3;
        }
        
        
        public Probe getQ3()
        {
            return ( this.q3 );
        }
        
        
    }
    

    public UnionProbeList getParent() {
    	return parent;
    }
    	    
    public void setParent(UnionProbeList plgparent) {
    	parent=plgparent;
    }

    public void invalidateCache() {
    	synchronized (this) {
        	probesAsArray = null;   
        	numberOfProbes = -1;
		}
    }
    
    protected boolean isCacheValid() {
    	synchronized (this) {
        	return (probesAsArray!=null);			
		}
    }
    
    /** this is the method that returns the actual probe content. all other methods in ProbeList use toMap() either directly or via
     * toCollection() or toArray().
     * @return the probeList content
     */    
	public Map<String,Probe> toMap()  {
		synchronized (this) {
			if (probes==null) {
				probes = new ConcurrentSkipListMap<String, Probe>();
			}
			return probes;			
		}
	}
	
	protected void finalize() {
		if (MaydayDefaults.isDebugMode())
			System.out.println("** Discarding obsolete probelist \""+getName()+"\" with "+getNumberOfProbes()+" probes");
	}

	public Iterator<Probe> iterator() 
	{
		return toMap().values().iterator();
	}


}
