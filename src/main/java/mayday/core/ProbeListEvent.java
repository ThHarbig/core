package mayday.core;
import java.util.*;

/**
 * @author Nils Gehlenborg
 * @version 0.1
 */
@SuppressWarnings("serial")
public class ProbeListEvent
extends EventObject
{
	public final static int LAYOUT_CHANGE = 1;
	public final static int ANNOTATION_CHANGE = 2;
	public final static int CONTENT_CHANGE = 4;
	public final static int PROBELIST_CLOSED = 8;
	public final static int OVERALL_CHANGE = LAYOUT_CHANGE
	| ANNOTATION_CHANGE
	| CONTENT_CHANGE 
	| PROBELIST_CLOSED;

	private int change;


	ProbeListEvent( Object source, int change )
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
		if (evt instanceof ProbeListEvent)
			return ((ProbeListEvent)evt).getSource()==source && ((ProbeListEvent)evt).getChange()==change;
		return super.equals(evt);
	}
}
