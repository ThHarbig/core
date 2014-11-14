package mayday.mpf;

import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JFrame;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.mpf.options.OptBase;
import mayday.mpf.plumawrapper.WrappedMPFModule_Basic;

/**
 * FilterBase maintains information and methods common to all filters:
   - filter name, etc
   - filter execution  
   - callback to ProgressMeter to keep users informed of their filter's status
   - pointer to an of instance CancellationMessage to allow cooperative thread 
     cancellation  
 * @author Florian Battke
 */
public abstract class FilterBase extends AbstractPlugin {
	
	protected PluginInfo pli;

	/** A version number for this implementation of the filter. The version number
	 * is used to check whether all subfilters of a complex filter are present in 
	 * the correct version. */
	protected int Version = 1; 
	
	/** The number of input objects of type MaydayDataObject required for this filter  
	 * @see #InputData */
	public int InputSize;

	/** The number of output objects of type MaydayDataObject returned by this filter 
	 * @see #OutputData */
	protected int OutputSize;	
	
	/** This array contains the input objects for this filter and is populated before calling execute() */
	public MaydayDataObject[] InputData; 
	
	/** This array contains the result of this filter after a successful call to its execute() method */
	public MaydayDataObject[] OutputData;
		
	/** Options for this filter */
	public FilterOptions Options = new FilterOptions(this);	
		
	/** Progressmeter is called with status information to keep the user informed */
	public ProgressMeter ProgressMeter;  
	
	/** Execute the filter on the data assigned to the input slots and puts its results
	 * into the output slots. This function expects all inputs to be provided correctly.
	 * Subclasses MUST override this function. 
	 * @throws Exception if the filter could not finish it's task. Reasons for this
	 *                   could be malformed input, bad filter options. Also, the filter
	 *                   could not be applicable for the given data.
	 * @see #InputData           
	 * @see #OutputData       
	 */ 
	public abstract void execute() throws Exception;
	
	/**
	 * Shows filter options in a modal dialog.
	 * Subclasses MUST NOT override this function.
	 * @param parentDialog The JDialog that becomes the dialogs parent and is disabled until the dialog is closed 
	 */
	public final void ShowOptions(javax.swing.JDialog parentDialog) {
		Options.ShowWindow(getName(), getDescription(), parentDialog);
	}
	
	/**
	 * Shows filter options in a modal dialog.
	 * Subclasses MUST NOT override this function.
	 * @param parentFrame The JFrame that becomes the dialogs parent and is disabled until the dialog is closed 
	 */
	public final void ShowOptions(JFrame parentFrame) {
		Options.ShowWindow(getName(), getDescription(), parentFrame);		
	}
	
	/**
	 * Creates a new filter object with the given number of input and output slots.
	 * Subclasses SHOULD call this from their own constructor.
	 * @param inputSize The number of input slots for the new filter, must not be smaller than 1
	 * @param outputSize The number of output slots for the new filter, must not be smaller than 0
	 * @see #InputData
	 * @see #OutputData
	 */
	public FilterBase(int inputSize, int outputSize) {
		InputSize=inputSize;
		InputData = new MaydayDataObject[InputSize];
		OutputSize=outputSize;
		OutputData = new MaydayDataObject[OutputSize];
		try{
			pli=WrappedMPFModule_Basic.producePluginInfo(this); // placeholder only
		}catch (Throwable t) {}
	}
	
	/** Returns the module description
	 * @return A concise description of what this filter does, possible including notes on applicability and the like.
	 */
	public String getDescription() { 
		return pli.getAbout(); 
	}
	
	/** Returns the filter name for use in, among others, JLists.
	 * Subclasses MUST NOT override this function. (except complexfilter)
	 * @see java.lang.Object#toString()
	 */
	public final String toString() {
		return pli.getMenuName();
	}
	
	/** CancellationMessage is used to alert the filter to interrupt its execution and return control to the
	 * calling function whenever the user cancels the filtering process.
	 * This field can be used to determine whether the filter is currently used from Designer (cMgr==null)
	 * or from Applicator (cMgr!=null). (See also Applicator.Step1Panel.NextButtonHandler()
	 * For an example, see filters.RWrapper.OptIntegerFancy.createEditArea().
	 */
	protected CancellationMessage cMgr;
	
	public boolean calledFromDesigner() {
		return cMgr==null;
	}
	public boolean calledFromApplicator() {
		return cMgr!=null;
	}
	
	/** checks whether the user requested to cancel the filter execution
	 * Subclasses MUST check this at regular intervals and, if the result is true, MUST stop their work and
	 * return control to the calling function, i.e. return from their execute() method.
	 * The MPF will take care of handling the proper disposal of input and output data. 
	 * @return true if execution of the filter must be interrupted
	 */
	protected final boolean isCancelled() {
		return (cMgr!=null && cMgr.cancelRequested);
	}
	
	/** assigns an instance of CancellationMessage to this filter
	 * @param cmgr the CancellationMessage instance to assign
	 */
	public final void setCancellationMessage(CancellationMessage cmgr) {
		cMgr=cmgr;
	}
	
	
	/** returns a multiline string with details about the options used for this filter
	 *  this string will be appended in the form of a MIO to all probes created by this
	 *  filter. Subclasses may overload this function to provide more info.
	 *  @return the annotation string
	 */
	/*
	public String getAnnotation() {
		StringBuffer sb = new StringBuffer();
		for (OptBase ob : this.Options.getValues())
			if (ob.isVisible())
				sb.append(ob.Name+"="+ob.getAnnotation()+";\n");
		return sb.toString();
	}
	*/
	
	/** returns a string map with details about the options used for this filter
	 *  this will be appended in the form of a StringMapMIO to all probes created by this
	 *  filter. Subclasses may overload this function to provide more info.
	 *  @return the annotation map
	 */
	public Map<String, String> getAnnotationAsMap() {
		TreeMap<String, String> annomap = new TreeMap<String, String>();
		for (OptBase ob : this.Options.getValues())
			if (ob.isVisible())
				annomap.put(ob.Name,ob.getAnnotation());
		return annomap;
	}
	
	/** returns a name for every output slot of this filter.
	 * SHOULD be overloaded if a filter has more than one output.
	 * @param slotindex the number of the output slot
	 * @return the name for this output slot
	 */
	public String getOutputSlotName(int slotindex) {
		if (OutputSize>1) 
			return (new Integer(slotindex).toString());	
		else
			return "";		
	}
	
	/** returns a name for every input slot of this filter.
	 * SHOULD be overloaded if a filter expects more than one input.
	 * @param slotindex the number of the input slot
	 * @return the name for this input slot
	 */
	public String getInputSlotName(int slotindex) {
		if (OutputSize>1) 
			return (new Integer(slotindex).toString());	
		else
			return "";		
	}

	/** The FilterNode that represents this object if it's used in a Pipeline, or null else
	 */
	protected FilterNode attachedFilterNode;

	/** Tell this FilterBase object that it is being used within a Pipeline and that it is represented
	 * by the given FilterNode
	 * @param fn the FilterNode representing this object.
	 */
	public void attachFilterNode(FilterNode fn) {
		this.attachedFilterNode = fn;
	}
	
	@SuppressWarnings("unchecked")
	public String getCategory() {
		if (pli.getProperties().containsKey(Constants.CATEGORIES))
			return ((Vector<String>)pli.getProperties().get(Constants.CATEGORIES)).get(0);
		return "Basic Modules";
	}
	
	public String getName() {
		return pli.getMenuName();
	}
	
	public void setDescription(String description) {
		throw new RuntimeException("The description of a module can't be changed if the module is provided by a java class");
	}
	
	public void setName(String name) {
		throw new RuntimeException("The name of a module can't be changed if the module is provided by a java class");
	}
	
	public void setCategory(String category) {
		throw new RuntimeException("The category of a module can't be changed if the module is provided by a java class");
	}
	
	public void init() {
		
	}
	
	public PluginInfo register() throws PluginManagerException {
		return pli;
	}
	
}
