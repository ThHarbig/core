package mayday.core;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mayday.core.meta.ComparableMIO;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.meta.types.AnnotationMIO;

/**
 * @author Nils Gehlenborg
 * @version 0.2 
 */
@SuppressWarnings("unchecked")
public class Probe
implements Comparable, Cloneable
{
	public static final int IMPLICIT_PROBE = 0x01;
	public static final int EXPLICIT_PROBE = 0x02;

	protected String name;

	protected double[] values;
	protected List<WeakReference<ProbeList>> probeLists;
	protected MasterTable masterTable;
	
	/**
	 * is the probe implicit? What does that mean:
	 * EXPLICIT = probe is taken from experimental data, representing something real.
	 * IMPLICIT = probe is a derived characteristic, not part of the real data (e.g. a mean profile).
	 * 
	 * All usual probes in Mayday are _explicit_ probes, i.e. implicitProbe is set to FALSE.
	 * To my knowledge, there is only one place where _implicit_ probes are used, namely in 
	 * vis3.plots.profile.ProfilePlotComponent. There, implicit probes are plotted without modifying them
	 * to allow the correct plotting of centroid profiles.
	 * If you don't know what you're doing, use IMPLICIT probes (i.e. the constructor Probe(MasterTable))
	 * (fb 090310)
	 */
	protected boolean implicitProbe;

	protected int nextAddPosition=0;

	/**
	 * Constructs a probe that is associated with the provided master table and can
	 * be either a user or a system probe.
	 * 
	 * @param masterTable The master table that holds the probe.
	 * @param systemProbe True if this probe has been created by the system, false otherwise.
	 * 
	 * @throws RuntimeException If the master table is <code>null</code>.
	 */
	public Probe( MasterTable masterTable, boolean systemProbe )
	throws RuntimeException
	{
		this.probeLists = new ArrayList<WeakReference<ProbeList>>();
		this.implicitProbe = systemProbe;

		if ( masterTable != null )
			this.masterTable = masterTable;
		else
			throw ( new RuntimeException( "Master table must not be null." ) );
		this.values = new double[masterTable.getNumberOfExperiments()];
	}


	/**
	 * Constructs a probe that is associated with the provided master table and is
	 * a user probe.
	 * 
	 * @param masterTable The master table that holds the probe.
	 * 
	 * @throws RuntimeException If the master table is <code>null</code>.
	 */
	public Probe( MasterTable masterTable )
	throws RuntimeException
	{
		this(masterTable, false);
	}


	public AnnotationMIO getAnnotation()
	{
		try {
			return (AnnotationMIO)masterTable.getDataSet().getMIManager().getGroupsForType("PAS.MIO.Annotation").get(0).getMIO(this);
		} catch (Exception e) {
			return null;
		}
	}


	public String getName() {
		return name; 
	}

	public void setName(String newName) {
		Probe maybeThis;
		if (masterTable!=null
			&& (maybeThis=masterTable.getProbe(name))!=null
			&& (maybeThis==this))
			masterTable.renameProbe(name, newName);
		else
			name = newName;
	}

	public void setAnnotation( AnnotationMIO annotation )
	{
		MIGroupSelection<MIType> mgs = masterTable.getDataSet().getMIManager().getGroupsForType("PAS.MIO.Annotation");
		MIGroup annotationGroup;
		if (mgs.size()==0)
			annotationGroup = masterTable.getDataSet().getMIManager().newGroup("PAS.MIO.Annotation", "Annotations");
		else 
			annotationGroup = mgs.get(0);
		annotationGroup.add(this,annotation);

	}


	public boolean isImplicitProbe()
	{
		return ( this.implicitProbe );
	}


	public void setImplicitProbe( boolean implicitProbe )
	{
		this.implicitProbe = implicitProbe;
	}


	public boolean isExplicitProbe()
	{
		return ( !isImplicitProbe() );
	}


	public void setExplicitProbe( boolean explicitProbe )
	{
		setImplicitProbe( !explicitProbe );
	}

	public Double getValue( int experiment )
	throws RuntimeException
	{
		if ( experiment >= getNumberOfExperiments() )
		{
			throw ( new RuntimeException( "Invalid experiment number (" + experiment + ")." ) );
		}

		double ret = values[experiment];
		if (Double.isNaN(ret))
			return null;
		return ret;
	}


	public void setValue( Double value, int experiment )
	throws RuntimeException
	{
		if ( experiment >= getNumberOfExperiments() )
		{
			throw ( new RuntimeException( "Invalid experiment number (" + experiment + ")." ) );
		}

		if (value==null)
			value = Double.NaN;

		values[experiment]=value;

	}

	public Double getMaxValue()
	{
		Double l_max = new Double( -Double.MAX_VALUE );
		boolean l_hasMax = false;

		for ( int i = 0; i < values.length; ++i )
		{
			Double l_double = getValue(i);			

			if ( l_double != null )
			{
				if ( Double.compare( l_double.doubleValue(), l_max.doubleValue() ) > 0 )
				{
					l_max = l_double;
				}

				l_hasMax = true;
			}
		}

		if ( l_hasMax )
		{
			return ( l_max );
		}

		return ( Double.NaN );
	}


	public Double getMinValue()
	{
		Double l_min = new Double( Double.MAX_VALUE );
		boolean l_hasMin = false;

		for ( int i = 0; i < values.length; ++i )
		{
			Double l_double = getValue( i );

			if ( l_double != null )
			{
				if ( Double.compare( l_double.doubleValue(), l_min.doubleValue() ) < 0 )
				{
					l_min = l_double;
				}

				l_hasMin = true;
			}
		}

		if ( l_hasMin )
		{
			return ( l_min );
		}

		return ( Double.NaN );   
	}	


	public int getNumberOfExperiments()
	{
		return ( this.values.length );
	}


	protected void removeStaleProbelists() {
		Iterator<WeakReference<ProbeList>> i = probeLists.iterator();
		while (i.hasNext()) {
			if (i.next().get()==null)
				i.remove();
		}
	}
	
	public List<ProbeList> getProbeLists()
	{
		removeStaleProbelists();		
		LinkedList<ProbeList> pls = new LinkedList<ProbeList>();
		for (WeakReference<ProbeList> rpl : this.probeLists) {
			ProbeList pl = rpl.get();
			if (pl!=null)
				pls.add(pl);
		}
		return ( pls );
	}


	public void setProbeLists( List<ProbeList> probeLists )
	{
		this.probeLists.clear();
		for (ProbeList pl : probeLists)
			this.probeLists.add(new WeakReference<ProbeList>(pl));
	}


	public int getNumberOfProbeLists()
	{
		removeStaleProbelists();		
		return ( probeLists.size() );
	}


	public void addProbeList( ProbeList probeList )
	throws RuntimeException
	{			
		if (getProbeLists().contains(probeList)) {
			return;
		}

		if ( !probeLists.add( new WeakReference<ProbeList>(probeList) ) )
		{
			throw ( new RuntimeException( "Unable to add probe list \"" +
					probeList.getName() +
					"\" to probe \"" +
					this.getName() +
			"\"." ) );
		}
	}

	protected boolean removeProbeList0(ProbeList pl) {
		Iterator<WeakReference<ProbeList>> i = probeLists.iterator();
		boolean removed = false;
		while (i.hasNext()) {
			ProbeList cpl = i.next().get();
			if (cpl==pl) {
				removed = true;
				i.remove();
			}
		}
		return removed;
	}

	public void removeProbeList( ProbeList probeList )
	throws RuntimeException
	{
		if ( !removeProbeList0( probeList ) )
		{
			throw ( new RuntimeException( "Unable to remove probe list \"" +
					probeList.getName() +
					"\" from probe \"" +
					this.getName() +
			"\"." ) );
		}

		// NOTE: is this a good way to handle system probes?
		// if this was the last probe list and this is a system probe,
		// remove the probe from the master table to avoid a dangling probe
		if ( ( getNumberOfProbeLists() == 0 ) && isImplicitProbe() )
		{
			if ( MaydayDefaults.REMOVE_DANGLING_IMPLICIT_PROBES )
			{
				this.masterTable.removeProbe( getName() );
			}
		}

		removeStaleProbelists();
		((ArrayList)this.probeLists).trimToSize();
	}


	public int compareTo( Object probe )
	{
		if (probe==null)  //fb:080617 this can happen, i.e. when using JList.setPrototypeCellValue()
			return -1;

		//MZ: 2005-05-20
		//compareTo should take this into account:
		if(!(probe instanceof Probe))
		{
			return (-1); 
		}

		return this.getName().compareTo(
				((Probe)probe).getName()
		);


	}


	public boolean equals( Object probe )
	{
		//return (probe instanceof Probe && this==probe);
		//MZ: 2005-07-12: 
		return compareTo(probe)==0;
	}


	public String toDebugString()
	{
		String l_string = new String();

		l_string += getName() + "\n";
		l_string += getAnnotation().getQuickInfo() + "\n";
		l_string += getAnnotation().getInfo() + "\n";
		l_string += ( isImplicitProbe() ? "system" : "user" ) + "\n";
		l_string += getNumberOfExperiments() + "\n";

		for ( int i = 0; i < getNumberOfExperiments(); ++i )
		{
			l_string += this.masterTable.getExperimentName( i ) +
			": " +
			getValue( i ) +
			"\n";
		}

		return ( l_string );
	}  


	public String toString()
	{
		return ( ( isImplicitProbe() ? "$" : "" ) +
				getName() +
				( isImplicitProbe() ? "$" : "" ) );	
	}  


	/*public MIContainer getMIContainer()
  {
    return ( this.miContainer );
  }*/


	public static class ExperimentMaximumComparator
	implements Comparator
	{
		private int mode;

		public ExperimentMaximumComparator( int mode )
		{
			this.mode = mode;
		}


		public int compare( Object object1, Object object2 )
		{
			Double l_maxObject1 = null;
			Double l_maxObject2 = null;

			if ( ( ( this.mode & IMPLICIT_PROBE ) != 0 ) && !( ( this.mode & EXPLICIT_PROBE ) != 0 ) )			
			{
				// only system probes

				if ( !((Probe)object1).isImplicitProbe() )
				{
					// object1 is not a system probe
					l_maxObject1 = null;
				}
				else
				{
					l_maxObject1 = ((Probe)object1).getMaxValue();
				}

				if ( !((Probe)object2).isImplicitProbe() )
				{
					// object2 is not a system probe
					l_maxObject2 = null;
				}
				else
				{
					l_maxObject2 = ((Probe)object2).getMaxValue();
				}
			}			
			else if ( !( ( this.mode & IMPLICIT_PROBE ) != 0 ) && ( ( this.mode & EXPLICIT_PROBE ) != 0 ) )
			{
				// only user probes

				if ( ((Probe)object1).isImplicitProbe() )
				{
					// object1 is a system probe
					l_maxObject1 = null;
				}
				else
				{
					l_maxObject1 = ((Probe)object1).getMaxValue();
				}

				if ( ((Probe)object2).isImplicitProbe() )
				{
					// object2 is a system probe
					l_maxObject2 = null;
				}
				else
				{
					l_maxObject2 = ((Probe)object2).getMaxValue();
				}
			}
			else
			{
				// both user and system probes
				l_maxObject1 = ((Probe)object1).getMaxValue();
				l_maxObject2 = ((Probe)object2).getMaxValue();				
			}


			if ( l_maxObject1 == null )
			{
				return ( -1 );
			}

			if ( l_maxObject2 == null )
			{
				return ( 1 );
			}

			return ( l_maxObject1.compareTo( l_maxObject2 ) );
		}
	}



	public static class ExperimentMinimumComparator
	implements Comparator
	{
		private int mode;

		public ExperimentMinimumComparator( int mode )
		{
			this.mode = mode;
		}


		public int compare( Object object1, Object object2 )
		{
			Double l_minObject1 = null;
			Double l_minObject2 = null;

			if ( ( ( this.mode & IMPLICIT_PROBE ) != 0 ) && !( ( this.mode & EXPLICIT_PROBE ) != 0 ) )			
			{
				// only system probes

				if ( !((Probe)object1).isImplicitProbe() )
				{
					// object1 is not a system probe
					l_minObject1 = null;
				}
				else
				{
					l_minObject1 = ((Probe)object1).getMinValue();
				}

				if ( !((Probe)object2).isImplicitProbe() )
				{
					// object2 is not a system probe
					l_minObject2 = null;
				}
				else
				{
					l_minObject2 = ((Probe)object2).getMinValue();
				}
			}			
			else if ( !( ( this.mode & IMPLICIT_PROBE ) != 0 ) && ( ( this.mode & EXPLICIT_PROBE ) != 0 ) )
			{
				// only user probes

				if ( ((Probe)object1).isImplicitProbe() )
				{
					// object1 is a system probe
					l_minObject1 = null;
				}
				else
				{
					l_minObject1 = ((Probe)object1).getMinValue();
				}

				if ( ((Probe)object2).isImplicitProbe() )
				{
					// object2 is a system probe
					l_minObject2 = null;
				}
				else
				{
					l_minObject2 = ((Probe)object2).getMinValue();
				}
			}
			else
			{
				// both user and system probes
				l_minObject1 = ((Probe)object1).getMinValue();
				l_minObject2 = ((Probe)object2).getMinValue();				
			}


			if ( l_minObject1 == null )
			{
				return ( 1 );
			}

			if ( l_minObject2 == null )
			{
				return ( -1 );
			}

			return ( l_minObject1.compareTo( l_minObject2 ) );
		}
	}


	public static class ExperimentComparator
	implements Comparator
	{
		private int mode;
		private int experiment;    

		public ExperimentComparator( int mode, int experiment )
		{
			this.mode = mode;
			this.experiment = experiment;
		}


		public int compare( Object object1, Object object2 )
		{
			Double l_object1;
			Double l_object2;

			if ( ( ( this.mode & IMPLICIT_PROBE ) != 0 ) && !( ( this.mode & EXPLICIT_PROBE ) != 0 ) )      
			{
				// only system probes

				if ( !((Probe)object1).isImplicitProbe() )
				{
					// object1 is not a system probe
					l_object1 = null;
				}
				else
				{
					l_object1 = ((Probe)object1).getValue( this.experiment );
				}

				if ( !((Probe)object2).isImplicitProbe() )
				{
					// object2 is not a system probe
					l_object2 = null;
				}
				else
				{
					l_object2 = ((Probe)object2).getValue( this.experiment );
				}
			}     
			else if ( !( ( this.mode & IMPLICIT_PROBE ) != 0 ) && ( ( this.mode & EXPLICIT_PROBE ) != 0 ) )
			{
				// only user probes

				if ( ((Probe)object1).isImplicitProbe() )
				{
					// object1 is a system probe
					l_object1 = null;
				}
				else
				{
					l_object1 = ((Probe)object1).getValue( this.experiment );
				}

				if ( ((Probe)object2).isImplicitProbe() )
				{
					// object2 is a system probe
					l_object2 = null;
				}
				else
				{
					l_object2 = ((Probe)object2).getValue( this.experiment );
				}
			}
			else
			{
				// both user and system probes
				l_object1 = ((Probe)object1).getValue( this.experiment );
				l_object2 = ((Probe)object2).getValue( this.experiment );        
			}


			if ( l_object1 == null )
			{
				return ( -1 );
			}

			if ( l_object2 == null )
			{
				return ( 1 );
			}

			return ( l_object1.compareTo( l_object2 ) );
		}
	}


	public static class MIOListComparator
	implements Comparator
	{
		private int mode;
		private Map< Object, ComparableMIO > mioList;    
		private MIGroup mg;

		public MIOListComparator( int mode, Map< Object, ComparableMIO > mioList )
		{
			this.mode = mode;
			this.mioList = mioList;
		}
		
		public MIOListComparator( int mode, MIGroup mg ) {
			this.mode = mode;
			this.mg = mg;
		}
		



		public int compare( Object object1, Object object2 )
		{
			Comparable<Object> l_object1 = null;
			Comparable<Object> l_object2 = null;
			
			Probe p1 = (Probe)object1;
			Probe p2 = (Probe)object2;
			
			Comparable<Object> l_temp1 = (mioList!=null)?mioList.get( object1 ):(ComparableMIO)mg.getMIO(object1);
			Comparable<Object> l_temp2 = (mioList!=null)?mioList.get( object2 ):(ComparableMIO)mg.getMIO(object2);

			if ((mode & IMPLICIT_PROBE) != 0) {
				if (p1.isImplicitProbe())
					l_object1 = l_temp1;
				if (p2.isImplicitProbe())
					l_object2 = l_temp2;						
			}
			
			if ((mode & EXPLICIT_PROBE) != 0) {
				if (p1.isExplicitProbe())
					l_object1 = l_temp1;
				if (p2.isExplicitProbe())
					l_object2 = l_temp2;						
			}
			
			if ( l_object1 == null )
				return -1 ;
			if ( l_object2 == null )
				return 1;

			return l_object1.compareTo( l_object2 ) ;
		}
	}


	public void addExperiment( Double experiment )
	throws RuntimeException
	{
		if (nextAddPosition==values.length) {		
			// increase size of the array (this is slow, but saved memory in the long run)
			double[] temp = new double[ values.length+1 ];
			System.arraycopy(values, 0, temp, 0, values.length);
			values=temp;
		}
		
		if (experiment==null) 
			experiment = Double.NaN;
		values[nextAddPosition]=experiment;
		
		++nextAddPosition;
			

		/*
  	if ( !this.values.add( experiment ) )
  	{
  		throw ( new RuntimeException( "Unable to add experiment to probe \"" + 
  		                               getName() +
  		                               "\"." ) );
  	}  	*/
	}


	/*
  public void removeExperiment( Double experiment ) throws RuntimeException
  {

		if ( !this.values.remove( experiment ) )
		{
			throw ( new RuntimeException( "Unable to remove experiment from probe \"" + 
																		 getName() +
																		 "\"." ) );

			// NOTE: probably it's better to set the experiment to null
		}  	
  }*/

	public String getDisplayName() {
		return getDisplayName(false);
	}

	/**
	 * Return current display name of the probe. If the meta information group
	 * contains no entry for this probe, then the result is
	 * exclusion ? null : probeid + "*"
	 * @param exclude
	 * @return
     */
	public String getDisplayName(boolean exclude)	{
		String displayName = this.getName();
		
		if (masterTable!=null) {
			MIGroup mg = masterTable.getDataSet().getProbeDisplayNames();
			if (mg!=null) {
				MIType mt = mg.getMIO(this);
				if (mt==null || mt.toString().trim().length()==0) {
					if (exclude) {
						return null;
					} else {
						return displayName + "*";
					}
				} else {
					return mt.toString();
				}
			}
		}
		// default fallback
		return displayName;
	}


	public double getMean()
	{
		double l_mean = 0.0;
		double count=0;
		for ( double d : values ) {
			if (!Double.isNaN(d)) {
				l_mean += d;
				count ++;
			}
		}
		return ( l_mean / count );
	}


	public double getStandardDeviation() {    
		return ( Math.sqrt( getVariance() ) );
	}


	public double getVariance()	{		
		double l_mean = getMean();
		double l_sum = 0.0;
		double count=0;
		for ( double d : values ) {
			if (!Double.isNaN(d)) {
				l_sum += (l_mean-d)*(l_mean-d);
				count ++;
			}
		}
		return ( l_sum / count );
	}


	// computes sum over values in all experiments
	protected static double sum( ArrayList<Double> values ) {
		double l_sum = 0.0;
		for ( double d : values ) {
			if (!Double.isNaN(d)) {
				l_sum += d;
			}
		}
		return ( l_sum );
	}


	// computes sum over products of values in all experiments of both probes
	protected static double productSum( ArrayList<Double> values1, ArrayList<Double> values2 )
	{
		if ( values1.size() != values2.size() )
		{
			throw ( new RuntimeException( "Number of experiments must be equal in both probes." ) );
		}

		double l_sum = 0.0;

		for ( int i = 0; i < values1.size(); ++i )
		{
			l_sum += ( ((Double)values1.get( i )).doubleValue() *  ((Double)values2.get( i )).doubleValue() );
		}

		return ( l_sum );
	}


	public double getPearsonCorrelationCoefficient( Probe l_probe, int l_rightShift )
	{
		if ( this.getNumberOfExperiments() != l_probe.getNumberOfExperiments() )
		{
			throw ( new RuntimeException( "Number of experiments must be equal in both probes." ) );
		}    

		if ( getNumberOfExperiments() <= 0 )
		{
			return ( 0.0 );
		}

		ArrayList<Double> l_x = new ArrayList<Double>();
		ArrayList<Double> l_y = new ArrayList<Double>();

		/*
		 * shift profiles into position:
		 * 
		 * x: __/\__ --> __/\__
		 * y: _/\___ -->  _/\___
		 * 
		 * ==> first l_rightShift experiments in x and last l_r.. experiments in y cannot be used
		 */    
		for ( int i = 0; i < this.getNumberOfExperiments(); ++i )
		{
			if ( i < this.getNumberOfExperiments() - l_rightShift  )
			{
				l_x.add( this.getValue( i + l_rightShift ) );
			}

			if ( i >= l_rightShift  )
			{
				l_y.add( l_probe.getValue( i - l_rightShift ) );
			}      
		}

		double l_n = this.getNumberOfExperiments() - l_rightShift;

		double l_denominator = productSum( l_x, l_y ) - ( ( sum( l_x ) * sum( l_y ) ) / l_n );
		double l_squaredNominatorX = productSum( l_x, l_x ) - ( ( sum( l_x ) * sum( l_x ) ) / l_n );
		double l_squaredNominatorY = productSum( l_y, l_y ) - ( ( sum( l_y ) * sum( l_y ) ) / l_n );
		double l_nominator = Math.sqrt( l_squaredNominatorX * l_squaredNominatorY );

		return ( l_denominator / l_nominator );
	}


	public Object clone()
	{
		Probe l_probe = new Probe( this.masterTable );

		if (getAnnotation()!=null)
			l_probe.setAnnotation( getAnnotation().clone() );
		l_probe.setName(getName());

		l_probe.setImplicitProbe( isImplicitProbe() );

		l_probe.values = Arrays.copyOf(this.values, this.values.length);

		for ( ProbeList pl : getProbeLists() )
		{
			l_probe.addProbeList( pl );
		}

		return ( l_probe );
	}

	//SY
	/**
	 * @return the index of the first experiment with a missing value.
	 */
	public int getFirstMissingValue()
	{
		for (int i=0; i!=values.length; ++i)
			if (values[i]==Double.NaN)
				return i;
		return -1;
	}
	
	//FB
	/**
	 * @return the internal double array holding the probes values
	 * Modifications on the returned object directly affect this probe!
	 */
	public double[] getValues() {
		return values;
	}
	
	public void setValues(double[] values) {
		this.values = values; 
	}
	
	public void setValues(double[] values, boolean doCopy) {
		double[] vals = values;
		if (doCopy) {
			vals = new double[values.length];
			System.arraycopy(values, 0, vals, 0, values.length);
		}
		setValues(vals);
	}


	/**
	 * @return the masterTable
	 */
	public MasterTable getMasterTable() {
		return masterTable;
	}
	
	//added by GJ 05022013: in order to make classes extending Probe 
	//comparable to their basic type when using hashsets
	public int hashCode() {
		if(getName() != null)
			return getName().hashCode();
		else
			return super.hashCode();
	}
}
