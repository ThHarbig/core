package mayday.core.gui.dragndrop;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

/** 
 * a plugin that can convert a given set of input object types into a certain flavor
 * and vice-versa. can support different object types associated with one flavor, so that different 
 * drop targets can be supported
 * @author battke
 *
 */
public interface DragSupportPlugin {

	public final static String MC = "Core/Drag&Drop Support";
	
	public Class<?>[] getSupportedTransferObjects();
	public DataFlavor getSupportedFlavor();
	
	/** create data to transfer OUT of the current component */
	public Object getTransferData(Object... input);
	
	/** create data to transfer INTO the target component, given the contextObject */
	public <T> T[] processDrop(Class<T> targetClass, Transferable t);

	/** specify the context for drop operations. this will be called before processDrop */
	public void setContext(Object contextObject);
	
}
