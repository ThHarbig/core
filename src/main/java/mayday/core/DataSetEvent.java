package mayday.core;
import java.util.EventObject;

/*
 * Created on Apr 15, 2003
 *
 */

/**
 * @author Nils Gehlenborg
 * @version 0.1
 */
@SuppressWarnings("serial")
public class DataSetEvent
extends EventObject
{
	public final static int CAPTION_CHANGE = 1;
	public final static int CLOSING_CHANGE = 2;
	public final static int OVERALL_CHANGE = CAPTION_CHANGE |
	CLOSING_CHANGE;
	private int change;

	public DataSetEvent( Object source, int change )
	{
		super( source );

		this.change = change;
	}


	public int getChange()
	{
		return ( this.change ); 
	}


	public void setChange( int change )
	{
		this.change = change;
	}

	public boolean equals(Object evt) {
		if (evt instanceof DataSetEvent)
			return ((DataSetEvent)evt).getSource()==source && ((DataSetEvent)evt).getChange()==change;
		return super.equals(evt);
	}

}
