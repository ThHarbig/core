package mayday.core;
//import java.lang.RuntimeException;

/*
 * Created on Mar 30, 2003
 *
 */

/**
 * @author neil
 * @version 
 */
@SuppressWarnings("serial")
public class ProbeListStore
extends Store
{
	protected DataSet dataSet;

	public ProbeListStore()
	{
		super();
	}


	public DataSet getDataSet()
	{
		return ( this.dataSet );
	}


	public void setDataSet( DataSet dataSet )
	{
		this.dataSet = dataSet;
	}


	public boolean moveUpProbeList( int index )
	{
		if ( index < 0 ) {
			throw ( new RuntimeException( "Probe list not found." ) );    	
		}

		if ( index == 0 ) {
			return ( false );
		}

		Object o = super.remove(index-1);
		super.add(index, o);

		return ( true );
	}


	public boolean moveUpProbeList( ProbeList probeList ) {
		int l_index = indexOf( probeList );
		return ( moveUpProbeList( l_index ) );
	}


	public boolean moveDownProbeList( int index ) {
		if ( index < 0 ) {
			throw ( new RuntimeException( "Probe list not found." ) );    	
		}

		if ( index == getNumberOfObjects() - 1 ) {
			return ( false );
		}

		Object o = super.remove(index+1);
		super.add(index, o);

		return ( true );
	}


	public boolean moveDownProbeList( ProbeList probeList )	{
		int l_index = indexOf( probeList );

		return ( moveDownProbeList( l_index ) );
	}


	public int getPosition( ProbeList probeList ) {
		return ( indexOf( probeList ) );
	}
}
