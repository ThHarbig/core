package mayday.mpf;

/**
 * FilterSlot represents one connection in a complex filter's graph
 * @author Florian Battke
 */
public class FilterSlot {
	
	public static final Integer UNASSIGNED = -1;
	
	public FilterNode Node = null;
	public Integer Slot = UNASSIGNED;
	public Integer NodeIndex = UNASSIGNED;
 
	/** Creates a new instance of FilterSlot that is not connected
	 */
	public FilterSlot() {}
	
	/** Creates a new instance of FilterSlot connected to a given slot on another node
	 * @param targetNode the node to connect to
	 * @param targetSlot the slot to connect to
	 */
	public FilterSlot(FilterNode targetNode, Integer targetSlot) {
		Node = targetNode;
		Slot = targetSlot;
	}
	
	/** Creates a new instance of FilterSlot connected to a given slot on another node, including the other node's index
	 * @param targetNode the node to connect to 
	 * @param targetSlot the slot to connect to
	 * @param targetNodeIndex the index of the node to connect to
	 */
	public FilterSlot(FilterNode targetNode, Integer targetSlot, Integer targetNodeIndex) {
		this(targetNode, targetSlot);
		NodeIndex = targetNodeIndex;
	}
	
	/** returns true if the index value of the connected node is assigned
	 * @return NodeIndex!=UNASSIGNED
	 */
	public boolean hasIndex() {
		return NodeIndex!=UNASSIGNED;
	}
	
	/** returns true if this Slot is connected to another FilterNode
	 * @return Node!=null
	 */
	public boolean isConnected() {
		return Node!=null;
	}
	
	/** sets the target node for this connection
	 * @param targetNode the node to connect to
	 * @param targetSlot the slot to connect to
	 */
	public void set(FilterNode targetNode, Integer targetSlot) {
		Node = targetNode;
		Slot = targetSlot;
	}
	
	/** removes connection info from this slot, i.e. make it unassigned 
	 */
	public void unset() {
		Node = null;
		Slot = UNASSIGNED;
		NodeIndex = UNASSIGNED;
	}
	
	/** removes only the index value for the connected node
	 */
	public void clearIndex() {
		NodeIndex = UNASSIGNED;
	}
}
