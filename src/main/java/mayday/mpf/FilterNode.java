package mayday.mpf;

import java.lang.reflect.Array;

/**
 * FilterNode represents one subfilter in the filter graph of a complex filter instance
 * This class contains all information needed to calculate dependencies within the filter
 * graph, validate the graph etc.
 * 
 * 
 * Graph connection representation:
 * - Every filter has n input and m output slots.
 * - For every slot of a FilterNode, connections are represented by a pair of 
 *    1) a pointer to another FilterNode instance
 *    2) the index of the slot that we link to
 * - Before the complex filter is executed, validated or saved to a file, the pointers
 *   are used to calculate an index number for every FilterNode starting with index 0 (zero)
 *   for Global Input. Nodes in the graph get index numbers in a recursive traversal of 
 *   the tree. Invariant: For two filters with indices k and l, l will be executed 
 *   AFTER k if l>k. 
 *   The filter index determines the execution order of the filters and reflects filter 
 *   dependencies in the graph.
 * - For the execution of complex filters, only inbound connections are needed, so only those
 *   are stored in this object / in the filter definition file
 * - Connections are encapsulated in the FilterSlot class 
 * 
 * @author Florian Battke
 */

public class FilterNode {
	
	/** The link to the underlying MPF Filter Object  */ 
	public FilterBase attachedFilter;
	
	/** The link to the visual representation of this node if in Designer 
	 * This object is created lazily only when needed, see getVisualNode() */
	private VisualNode attachedVisualNode;
	
	private int FilterIndex=-1;
	
	public FilterSlot[] Output;
	public FilterSlot[] Input;
	
	/* connectedInputs counts the number of inputs that are properly connected, see connectInput */ 
	private int connectedInputs;
	
	/**
	 * connects an input filter (given by it's index) to this filter's input slot inputNo.
	 * @param inputNo the input slot to connect to
	 * @param connectToIndex the filter that will send us data
	 * @return true if this was the last unassigned input slot and all slots are now set, false otherwise
	 * @throws Exception if this input slot was already assigned
	 */
	public final boolean connectInput(int inputNo, int connectToIndex) throws Exception {
		if (!Input[inputNo].hasIndex()) {
			Input[inputNo].NodeIndex=connectToIndex;
			++connectedInputs;
		} else throw new Exception(
					"["+attachedFilter.getName()+"] Input slot "+inputNo+" already bound to "+connectToIndex+", cannot bind again. " +
					"\nThis should never happen. If you see this message, contact the Mayday developers\n" +
					"with a description of what you did before this happened. Thank you.");
		return (connectedInputs==attachedFilter.InputSize);
	}

	/**
	 * checks whether all slots are assigned
	 * @return true if all slots are assigned
	 */
	public boolean validateConnections() {
		boolean result = true;
		for (int i=0; i!=attachedFilter.InputSize; ++i)
			result &= (Input[i].isConnected());
		for (int i=0; i!=attachedFilter.OutputSize; ++i)
			result &= (Output[i].isConnected());
		return result;
	}
	
	/**
	 * Returns the VisualNode attached to this FilterNode. If the VisualNode was not yet initialized,
	 * it is created now.
	 * @return the (possible newly created) VisualNode
	 */
	public VisualNode getVisualNode() {
		if (attachedVisualNode==null) { //create visual node for the first time => only when needed.
			attachedVisualNode = new VisualNode(this);			
		}
		return attachedVisualNode;
	}
		
	/* Only one constructor */
	/** Creates a new instance of FilterNode and attaches a filter
	 * @param attachFilter the filter to attach
	 */
	public FilterNode(FilterBase attachFilter) {
		attachedFilter=attachFilter;
		resetIOSizes();
		attachedFilter.attachFilterNode(this);
	}
	
	/** Creates an incoming connection by connecting an input slot to another FilterNode's output slot 
	 * @param inputSlot the input slot to connect to
	 * @param sendingObject the FilterNode to recieve from
	 * @param sendingSlot the output slot of the sending FilterNode
	 */
	public void connectInput(int inputSlot, FilterNode sendingObject, int sendingSlot) {
		if (sendingObject!=null) {
			Input[inputSlot].set(sendingObject, sendingSlot);
			if (attachedVisualNode!=null) 
				attachedVisualNode.setInputConnected(inputSlot,true);
		} else {
			Input[inputSlot].unset();
			if (attachedVisualNode!=null) 
				attachedVisualNode.setInputConnected(inputSlot,false);
		}
	}
	
	/** Creates an outgoing connection by connecting an output slot to another FilterNode's input slot
	 * @param outputSlot the output slot to connect from
	 * @param receivingObject the FilterNode to send to
	 * @param receivingSlot the input slot of the receiving FilterNode
	 */
	public void connectOutput(int outputSlot, FilterNode receivingObject, int receivingSlot) {
		if (receivingObject!=null) {
			Output[outputSlot].set(receivingObject, receivingSlot);
			if (attachedVisualNode!=null) 
				attachedVisualNode.setOutputConnected(outputSlot,true);			
		} else {
			Output[outputSlot].unset();
			if (attachedVisualNode!=null) 
				attachedVisualNode.setOutputConnected(outputSlot,false);	
		}
	}
	
	/**
	 * sets all Input[].NodeIndex entries to UNASSIGNED to prepare this Node for graph building 
	 */
	public void resetAllInputIndices() {
		for (int i=0; i!=attachedFilter.InputSize; ++i)
			Input[i].clearIndex();
		connectedInputs=0;
	}

	/** returns the filter index of this node. Valid only after the graph has been built
	 * @return the filter index 
	 */
	public int getFilterIndex() {
		return FilterIndex;
	}

	/** sets this filters index
	 * @param filterIndex the index to assign to this filter
	 */
	public void setFilterIndex(int filterIndex) {
		FilterIndex = filterIndex;
		if (attachedVisualNode!=null) 
			attachedVisualNode.setTitle(attachedFilter.getName()+" ("+FilterIndex+")");
	}
	
	@SuppressWarnings("unchecked")
	private static Object resizeArray(Object a, int newLength) {
		if (a==null || !a.getClass().isArray()) return null;
		if (Array.getLength(a)==newLength) return a;
	    Class componentType = a.getClass().getComponentType();
	    Object newArray = Array.newInstance(componentType, newLength);
	    System.arraycopy(a, 0, newArray, 0, Math.min(Array.getLength(a), newLength));
		return newArray;
	}
	
	/** resizes the input and output slot arrays so that their length corresponds
	 * to the values of InputSize and OutputSize, respectively. This function should
	 * only be called by the FilterNode constructor and by an instance of Designer
	 * when the user changes the number of global input/output slots.
	 */
	public void resetIOSizes() { // can also be run to initialize during constructor
		// First we change the input size
		int newInputSize = attachedFilter.InputSize;
		int oldInputSize = (Input==null) ? 0 : Input.length;
		// keep old connections, if present. disconnect those we remove
		if (Input!=null) {
			for (int i=newInputSize; i<oldInputSize; ++i)
				if (Input[i].isConnected())
					Input[i].Node.connectOutput(Input[i].Slot, null, -1);
			Input = (FilterSlot[])resizeArray(Input, newInputSize);
		} else {
			Input = new FilterSlot[newInputSize];
			for (int i=0; i!=newInputSize; ++i) 
				if (Input[i]!=null) Input[i].clearIndex();	
		}
		for (int i=oldInputSize; i<newInputSize; ++i) Input[i] = new FilterSlot();
		
		// Now we change the output size
		int newOutputSize = attachedFilter.OutputSize;
		int oldOutputSize = (Output==null) ? 0 : Output.length;
		// keep old connections, if present. disconnect those we remove
		if (Output!=null) {
			for (int i=newOutputSize; i<oldOutputSize; ++i)
				if (Output[i].isConnected())
					Output[i].Node.connectInput(Output[i].Slot, null, -1);
			Output = (FilterSlot[])resizeArray(Output, newOutputSize);
		} else {
			Output = new FilterSlot[newOutputSize];
		}
		for (int i=oldOutputSize; i<newOutputSize; ++i) Output[i] = new FilterSlot();
		
		// Finally we recreate our visual appearance
		// We know that we are in designer mode, because there is no other legal
		// way to call this function then by changing the global input/output size
		// 060518: OK, another way is via the RWrapper, but setting that option can 
		// only be done in Designer anyway
		if (Input!=null && Output!=null) // if all is properly initialized... 
			getVisualNode().setupComponent();  // ...construct graphical representation
	}
			
}
