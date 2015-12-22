package mayday.vis3.plots.heatmap2.interaction;

import java.util.EventObject;

/**
 * Size changes are handled by the central structure, HeatmapStructure. Repaint wishes are done by the more
 * specialized containers, such as the *HeaderStack and CentralComponent classes
 * @author battke
 *
 */

@SuppressWarnings("serial")
public class UpdateEvent extends EventObject {

	public static final int REPAINT = 1;
	public static final int SIZE_CHANGE = 2;
	public static final int COLUMNS_CHANGE = 3; // the number of columns in the element changed 
	
	protected int type;
	
	public UpdateEvent(Object source, int type) {
		super(source);
		this.type = type;
//		System.out.println("Sending signal "+type+" from "+source);
	}
	
	public int getChange() {
		return type;
	}
	


}
