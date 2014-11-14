package mayday.vis3.model;

import java.util.EventObject;

@SuppressWarnings("serial")
public class ViewModelEvent extends EventObject {

	/* A probe list has been added or removed from the selection */
	public static final int PROBELIST_SELECTION_CHANGED = 0x10;

	/* The selection of probes was changed */
	public static final int PROBE_SELECTION_CHANGED = 0x20;

	/* The probe lists were rearranged, the top priority probe list has changed for some probes */
	public static final int PROBELIST_ORDERING_CHANGED = 0x30;

	/* The combined set of probes was changed, usually as a result of ProbeListEvent.CONTENT_CHANGE */
	public static final int TOTAL_PROBES_CHANGED = 0x40;
	
	/* The data manipulation has changed. There are the same probes and probelists, but the probe values are different */
	public static final int DATA_MANIPULATION_CHANGED = 0x80;
	
	/* The data manipulation has changed. There are the same probes and probelists, but the probe values are different */
	public static final int EXPERIMENT_SELECTION_CHANGED = 0x100;
	
	/* The viewmodel was disposed, because no more probelists are contained or the last open plot was closed or the dataset closed */
	public static final int VIEWMODEL_CLOSED = 0xFF; 

	private int change;

	public ViewModelEvent(Object source, int change) {
		super(source);
		this.change=change;
	}

	public int getChange() {
		return change;
	}

	public boolean equals(Object evt) {
		if (evt instanceof ViewModelEvent)
			return ((ViewModelEvent)evt).getSource()==source && ((ViewModelEvent)evt).getChange()==change;
		return super.equals(evt);
	}

}
