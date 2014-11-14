package mayday.mpf;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JFrame;

import mayday.core.io.StorageNode;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.mpf.options.OptExternalized;
import mayday.mpf.options.OptInteger;
import mayday.mpf.options.OptString;

/**
 * ComplexFilter describes a MPF filter plugin that contains several other MPF plugins. This class describes how
 * the subfilters are connected to the input and output slots, how they are interconnected and what values their
 * options are set to. It also allows the externalization of subfilter options, i.e. the presentation of a subset
 * of its subfilter options to the user of the ComplexFilter. A ComplexFilter can be seen as a processing pipeline.
 * Included are
 * - a list of other (possibly complex) filters
 * - a representation of a directed, cycle-free graph on these filters
 * - methods for connecting, validating and executing the graph
 * etc 
 * @author Florian Battke
 */
@PluginManager.IGNORE_PLUGIN
public class ComplexFilter extends FilterBase {
		
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.mpf.complexfilter",
				new String[0],
				mayday.mpf.Constants.masterComponent,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Manages (recursive) pipelines of MPF modules stored as text files",
				"ComplexFilter"
				);
		return pli;
	}
	
	private String _name ="", _description ="", _category ="";
	
	/**
	 * The file name of this ComplexFilter's description file (.mpd). This value is only used by the Designer class
	 * and MUST NOT be changed by other classes
	 */
	public String nameOnDisk = null; 

	/** 
	 * The list of subfilters contained in this ComplexFilter
	 */
	public Vector<FilterNode> Filters = new Vector<FilterNode>();
	
	/**
	 * The list of filters sorted by their FilterIndex
	 * Valid only AFTER the tree has been built! 
	 */
	public Vector<FilterNode> sortedFilters = new Vector<FilterNode>();
	
	private int lastSortableIndex =-1;
	
	/**
	 * The global input and output slots are represented by instances of InputDummyFilter and
	 * OutputDummyFilter, respectively. They are public so that Designer can access them freely. 
	 */
	public FilterNode globalIn, globalOut;
	
	private void progress(double perc, String msg) {
		if (ProgressMeter!=null) ProgressMeter.statusChanged(perc, msg);
	}
	
	/** 
	 * executes the ComplexFilter by calling all subfilters in the correct order and linking their
	 * input and output slots as specified by the filter graph.
	 */
	public void execute() throws Exception {
		
		// attach initial input slots
		for (int i=0; i!=InputSize; ++i) {
			globalIn.attachedFilter.OutputData[i] = this.InputData[i];
		}		
  		// Run all subfilters
		validateGraph(); // this can throw an exception if something is wrong with the graph

		// Calculate Progress Meter size
		double scaling = 1.0/((double)sortedFilters.size()-2);
		
		// Execute all subfilters
		for (int i=0; i!=sortedFilters.size() && !isCancelled(); ++i) {
			try {
				// calculate percentage baseline for this subfilter.
				// (i-1) because processing global input (sortedFilters[0]) takes no time at all and isn't counted
				// using the max makes sure we don't get negative baselines here
				double baseline = Math.max( (double)(i-1)*scaling , 0);  
				progress(baseline, (i-1)+"/"+(sortedFilters.size()-2));
				sortedFilters.get(i).attachedFilter.ProgressMeter = new ProgressMeter(ProgressMeter, scaling, baseline);
				executeFilter(i);
			} catch (Exception e) {
				throw new Exception("While executing \""+sortedFilters.get(i).attachedFilter.getName()+"\":\n"+e.getMessage());
			}
		}		  
		// Attach final output slots
		for (int i=0; i!=OutputSize; ++i) {
			this.OutputData[i] = globalOut.attachedFilter.InputData[i];
		}
	}
	
	private void executeFilter(int FilterIndex) throws Exception {
		FilterNode currentNode = sortedFilters.get(FilterIndex);
		// Connect Filter inputs
		for (int i=0; i!=currentNode.attachedFilter.InputSize; ++i) {
			int incomingIndex = currentNode.Input[i].NodeIndex;
			int incomingSlot = currentNode.Input[i].Slot;
			FilterNode sendingNode = sortedFilters.get(incomingIndex);
			currentNode.attachedFilter.InputData[i] = sendingNode.attachedFilter.OutputData[incomingSlot];
			sendingNode.attachedFilter.OutputData[incomingSlot]=null; // free up memory (perhaps)
		}
		// Execute Filter
		currentNode.attachedFilter.setCancellationMessage(this.cMgr);
		currentNode.attachedFilter.execute();		
	}
	
	/** validates the filter graph to make sure that it is cycle-free and that all slots are connected
	 * Also recursively  validates all enclosed instances of ComplexFilter
	 * @throws Exception if the graph is not valid
	 */
	public void validateGraph() throws Exception {		
		if (sortedFilters.size() != Filters.size()) {
			buildGraph();
		}
		// check if all inputs and outputs are connected properly
		for (int i=0; i!=sortedFilters.size(); ++i) {
			if (!sortedFilters.get(i).validateConnections()) {
				throw new Exception("While validating \""+getName()+"\":\n" +
						"\"" + sortedFilters.get(i).attachedFilter.getName() +"\" ("+i+") not connected properly. \n" +
						"Check that all inputs/outputs are assigned correctly.");
			}
			// if this subfilter is also a complex filter, validate recursively
			if (sortedFilters.get(i).attachedFilter instanceof ComplexFilter)
				try {
					((ComplexFilter)sortedFilters.get(i).attachedFilter).validateGraph();	
				} catch (Exception e) {
					throw new Exception("While validating \""+getName()+"\": Could not validate component:\n"+e.getMessage());
				}
		}
		// check if we were able to assign indices to all subfilters based on the graph topology
		// if all inputs/outputs are set and this criterion is not met, then the graph contains cycles
		if (lastSortableIndex<sortedFilters.size()-1) 
			throw new Exception("While validating \""+this.getName()+"\":\n" +
					"\"" + sortedFilters.get(lastSortableIndex+1).attachedFilter.getName() +"\" ("+(lastSortableIndex+1)+") not connected properly. \n" +
					"Check that your graph contains no cycles.");
	};
	
	/**
	 * builds the filter graph, i.e. assigns indices to all contained subfilters, and populates the sortedFilters vector
	 * @throws Exception if the graph could not be built (see FilterNode.connectInput(...))
	 */
	public void buildGraph() throws Exception {
		sortedFilters.clear();
		// Prepare all nodes
		for (FilterNode fn : Filters) {
			fn.setFilterIndex(-1); // so we can find unattached filters later
			fn.resetAllInputIndices(); // so we can start connecting them anew
		}
		// Add global input as first node
		sortedFilters.add(globalIn); // Filters[0]==globalIn
		globalIn.setFilterIndex(0);
		// recursively build the graph (kind of DFS)
		try { 
			buildChain(0);
		} catch (Throwable t) {
			throw new Exception("While building the graph of \""+this.getName()+"\":\n" + t.getMessage());
		}
		// Collect unconnected nodes
		lastSortableIndex = sortedFilters.size()-1;
		for (FilterNode fn : Filters) { 
			if (fn.getFilterIndex()==-1) {
				sortedFilters.add(fn);
				fn.setFilterIndex(sortedFilters.size()-1);
			}
		}
	}
	
	private void buildChain(int startingPoint) throws Exception {
		FilterNode currentNode = sortedFilters.get(startingPoint);
		for (int i=0; i!=currentNode.attachedFilter.OutputSize; ++i) {
			FilterNode child = currentNode.Output[i].Node;			
			if (child!=null) { // Output slot assigned?
				if (child.connectInput(currentNode.Output[i].Slot, startingPoint)) { //child has all inputs it needs?					
					sortedFilters.add(child);
					child.setFilterIndex(sortedFilters.size()-1); // last added index					
					buildChain(child.getFilterIndex()); // recursion
				}
			}
		}
	}
	
	/**
	 * removes a subfilter from this ComplexFilter
	 * @param NodeToRemove the node that will be removed
	 */
	public void remove(FilterNode NodeToRemove) {
		// Remove all links to and from this index, this is a bit slow but neccessary
		for (FilterNode fn : Filters) {
			for (int i=0; i!=fn.attachedFilter.InputSize; ++i) {
				if (fn.Input[i].Node==NodeToRemove) fn.connectInput(i, null, -1);
			}
			for (int i=0; i!=fn.attachedFilter.OutputSize; ++i) {
				if (fn.Output[i].Node==NodeToRemove) fn.connectOutput(i, null, -1);
			}
		}
		// Remove this node
		Filters.remove(NodeToRemove);
	}
	
	/** adds a new subfilter to this ComplexFilter
	 * @param NodeToAdd the FilterNode for the new subfilter to be added
	 */
	public void add(FilterNode NodeToAdd) {
		Filters.add(NodeToAdd);
	}
	
	/** Saves this ComplexFilter to a file etc
	 * @param wr The BufferedWriter to save to
	 * @throws Exception if the graph can not be built or if the BufferedWriter throws an Exception
	 * To load a ComplexFilter, use the constructor
	 * @see #ComplexFilter(String)  
	 */
	public void SaveToStream(BufferedWriter wr) throws Exception {
		StorageNode root = new StorageNode();
		// 1. Properties of this complex filter
		root.addChild("Name", this.getName());
		root.addChild("Desc", this.getDescription());
		root.addChild("Ver", this.Version);
		root.addChild("Category", this.getCategory());
		root.addChild("InputSize", this.InputSize);
		root.addChild("OutputSize", this.OutputSize);
		root.addChild(this.Options.toStorageNode());
		StorageNode childrenNode = root.addChild("Subfilters","");
		// 2. Subfilters follow  
		buildGraph();
		for (FilterNode fn : sortedFilters) {
			// 2a: Subfilter node properties
			StorageNode fnnode = childrenNode.addChild(new Integer(fn.getFilterIndex()).toString(), "");
			fnnode.addChild("FilterName",fn.attachedFilter.getName());
			fnnode.addChild("Ver", fn.attachedFilter.Version);
			// 2b: Connections
			String inputIndices="";
			String inputSlots="";
			for (int i=0; i!=fn.attachedFilter.InputSize; ++i) {
				inputIndices+=fn.Input[i].NodeIndex + ((i==fn.attachedFilter.InputSize-1) ? "" : ",");
				inputSlots+=fn.Input[i].Slot + ((i==fn.attachedFilter.InputSize-1) ? "" : ",");
			}			
			fnnode.addChild("InputFromIndex", inputIndices);
			fnnode.addChild("InputFromSlot",inputSlots);
			fnnode.addChild(fn.attachedFilter.Options.toStorageNode()); 
		}
		root.saveTo(wr);
	} 

	// is called from constructor, hence private because we don't need it outside this class
	private void LoadFromStream(BufferedReader rd) throws Exception {
		FilterClassList FilterList = FilterClassList.getInstance();
		StorageNode root = new StorageNode();
		root.loadFrom(rd);
		// 1. Properties of this complex filter
		this.setName(root.getChild("Name").Value);
		this.setDescription(root.getChild("Desc").Value);
		if (root.getChild("Category")!=null)  // prevent old version files from causing exceptions
			this.setCategory(root.getChild("Category").Value);
		this.Version = Integer.parseInt(root.getChild("Ver").Value);
		// 2. Load subfilter list
		this.Filters.setSize(root.getChild("Subfilters").childCount());
		this.sortedFilters.clear();
		for (StorageNode sn : root.getChild("Subfilters").getChildren()) {			
			// 2a: Create respective subfilter, here we need the FilterClassList
			FilterNode SubFilterNode;
			String FilterName = sn.getChild("FilterName").Value;
			// special cases: input/output dummy filters
			if (FilterName.equals("Global Input")) {
				InputSize = Integer.parseInt(root.getChild("InputSize").Value);
				InputData = new MaydayDataObject[InputSize]; 
				globalIn = new FilterNode(new InputDummyFilter(InputSize));
				SubFilterNode = globalIn;
			} else if (FilterName.equals("Global Output")) {
				OutputSize = Integer.parseInt(root.getChild("OutputSize").Value);
				OutputData = new MaydayDataObject[OutputSize];
				globalOut = new FilterNode(new OutputDummyFilter(OutputSize));
				SubFilterNode = globalOut;
			} else {
				// first we check the subfilter version. Wrong versions give a WARNING only
				int expectedVersion = Integer.parseInt(sn.getChild("Ver").Value);
				try {
					FilterList.checkPresence(FilterName, expectedVersion);
				} catch(Exception e) {
					ExceptionHandler.handle(new Exception("While loading filter \""+this.getName()+"\":\n"+e.getMessage()), (JFrame)null);
				}
				// now we create an instance of the subfilter 
				FilterBase SubFilter;				
				try {					
					SubFilter = FilterList.newInstance(FilterName);
				} catch (Throwable t) {
					throw new Exception("While loading module \""+this.getName()+"\":\n"+t.getMessage());
				};
				SubFilterNode = new FilterNode(SubFilter);
			}		
			SubFilterNode.setFilterIndex(Integer.parseInt(sn.Name));
			//2c is situated before 2b because the RWrapper uses the information stored in his options for the reinitialization 
			//of the input and output size information. trunkn
			// 2c: Load Subfilter settings
			// Exceptions in this part are not neccessarily bad, so we only show them as warnings without passing them upwards
			try {
				SubFilterNode.attachedFilter.Options.fromStorageNode(sn.getChild("Options"));
			} catch (Throwable t) {
				ExceptionHandler.handle(new Exception("While loading module \""+this.getName()+"\":\n"+
						"While initializing options for \""+SubFilterNode.attachedFilter.getName()+"\"\n" +
						t.getMessage()), (javax.swing.JFrame)null);
			}
			// 2b: Set Connections
			String[] inputIndices = sn.getChild("InputFromIndex").Value.split(",");
			String[] inputSlots = sn.getChild("InputFromSlot").Value.split(",");
			int maxStep = Math.min(inputSlots.length,SubFilterNode.attachedFilter.InputSize);
			for (int i=0; i!=maxStep; ++i) {
				SubFilterNode.Input[i].NodeIndex = Integer.parseInt(inputIndices[i]);
				SubFilterNode.Input[i].Slot = Integer.parseInt(inputSlots[i]);
			}
			
			// Add filter in correct position as specified by its index
			Filters.set(SubFilterNode.getFilterIndex(), SubFilterNode);
		}
		// 3. Set up real connections by converting indices to pointers
		for (FilterNode targetNode : Filters) {
			for (int targetSlot=0; targetSlot!=targetNode.attachedFilter.InputSize; ++targetSlot) {
				int sourceIndex = targetNode.Input[targetSlot].NodeIndex;
				if (sourceIndex!=FilterSlot.UNASSIGNED) { // there is indeed a connection?
					try {
						FilterNode sourceNode = Filters.get(sourceIndex);
						int sourceSlot = targetNode.Input[targetSlot].Slot;
						sourceNode.connectOutput(sourceSlot, targetNode, targetSlot);
						targetNode.connectInput(targetSlot, sourceNode, sourceSlot);
					} catch (Throwable t) {
						// We'll land here if connection info is corrupted. 
						// For now we only warn so that the filter can be repaired. During application, the filter won't validate and thus can't be
						// applied.
						ExceptionHandler.handle(
								new Exception("While loading module \""+this.getName()+"\":\nConnection information is corrupted."),(JFrame)null);
					}
				}
			}
		}
		// 4. Copy Filters to SortedFilters
		for (FilterNode fn : Filters) sortedFilters.add(fn);
		// 5. Load and initialize externalized options here. We can't do it before all subfilters are ready!
		StorageNode optionRoot = root.getChild("Options");
		if (optionRoot!=null) 
			for (int i=0; i!=optionRoot.childCount(); ++i) 
				this.Options.add(new OptExternalized(this,optionRoot.getChild(new Integer(i).toString()).Value));
		// 6. Remake graph
		buildGraph(); 
	} 
	
	
	/** Creates an "empty" instance of ComplexFilter with a given number of input and output slots 
	 * @param inputSize the number of input slots
	 * @param outputSize the number of output slots
	 */
	public ComplexFilter(int inputSize, int outputSize) { //parent recieves connection building requests during design time
		super(inputSize, outputSize);		
		globalIn = new FilterNode(new InputDummyFilter(inputSize)); 
		globalIn.setFilterIndex(0);
		Filters.add(globalIn);
		globalOut = new FilterNode(new OutputDummyFilter(outputSize)); // 
		Filters.add(globalOut);
		setName("Complex Filter");
		setDescription("");
		this.Version = 1;
	}
	
	// Make sure that no infinite recursion occurs when one filter is included in another one that is including the first one ...
	// This is also used in Designer to prevent the user from creating such loops in the first place
	public static Stack<String> RecursionSteps = new Stack<String>();
	
	/** Creates a new instance of ComplexFilter, loading the filter from a file
	 * @param FilterInitString the filename to load the filter from (.mpd)
	 * @throws Exception if loading fails due to I/O problems or due to corruption of the input file 
	 * or when subfilters can't be instantiated or when subfilter recursion is too deep;
	 */
	public ComplexFilter(String FilterInitString) throws Exception {
		super(1,1); // We will change the number of slots in a moment
		nameOnDisk = (new File(FilterInitString)).getName();
		if (RecursionSteps.contains(nameOnDisk)) {
			RecursionSteps.clear();
			throw new Exception("Recursive module invocation detected ("+FilterInitString+"), aborting.");
		}
		RecursionSteps.push(nameOnDisk);
		BufferedReader br = new BufferedReader(new FileReader(new java.io.File(FilterInitString)));
		try {
			this.LoadFromStream(br);
		} finally {
			br.close();  // Clean up before we move on...
			RecursionSteps.pop();
		}						
	}

	public String getInputSlotName(int slotindex) {
		return this.globalIn.attachedFilter.getOutputSlotName(slotindex);
	}
	
	public String getOutputSlotName(int slotindex) {
		return this.globalOut.attachedFilter.getInputSlotName(slotindex);
	}
	
	// Classes for global input and output slots
	
	private class OptIntegerPrivate extends OptInteger {
		public OptIntegerPrivate(String name, String description, Integer DefaultValue) {
			super(name, description, DefaultValue);
		}
		public boolean allowExternalize() { return false; };
	}
	
	private class DummyFilter extends FilterBase {  // merge common methods of input and output dummy
		
		public PluginInfo register() {
			return null;
		}
		
		protected final String Name, Description;
		protected final String direction;
		protected OptInteger opt;
		protected Vector<OptString> slotnames = new Vector<OptString>();
		
		public DummyFilter(int countin, int countout, String dir, String desc) {
			super(countin,countout); 
			direction=dir;
			Name = "Global "+direction;
			Description =desc;
			opt = new OptIntegerPrivate("Number of slots","Specifies the number of global "+direction+" slots of this pipeline. \n" +
					"Click OK to update the list of slot names.",
					new Integer(Math.max(countin,countout)));
			opt.setBounds(1,null);
			opt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					addNameOptions();
				}
			});
			Options.add(opt);
			addNameOptions();
			Version = 1;
		}
		public String getName() {return Name;}
		public String getDescription() {return Description;}
		public void execute() {};
		
		@SuppressWarnings("deprecation")
		private void addNameOptions() {
			if (opt.Value==slotnames.size()) return;
			else 
			if (opt.Value>slotnames.size()) { // enlarge slotnames
				for (int i=slotnames.size();i!=opt.Value;++i) {
					slotnames.add(new OptString(direction+" slot "+(i+1)+" name",
							"Enter a descriptive name for this slot",""+(i+1)));
					Options.add(slotnames.get(i));
				}				
			}
			else
			if (opt.Value<slotnames.size()) { // shrink slotnames
				while (slotnames.size()>opt.Value) {
					Options.remove(slotnames.get(slotnames.size()-1));  // i know what i'm doing here
					slotnames.remove(slotnames.size()-1);
				}
			}
			Options.createOptionList();
		}
		
		// The next two methods are identical. This is done on purpose. 
		
		public String getOutputSlotName(int slotindex) {
			if (opt.Value==1) return "";
			else 
				return slotnames.get(slotindex).Value; 
		}
		
		public String getInputSlotName(int slotindex) {
			if (opt.Value==1) return "";
			else 
				return slotnames.get(slotindex).Value; 
		}	
	}
	
	private class InputDummyFilter extends DummyFilter {
		
		public InputDummyFilter(int count) {
			super(0, count, "Input", 
					"This object links your submodules to the global input of the pipeline, " +
					"i.e. to input provided by the Mayday Filter Applicator.");
		}
		public void execute() {};
	}
	
	private class OutputDummyFilter extends DummyFilter {		
		public OutputDummyFilter(int count) {
			super(count, 0, "Output",
					"This object links your submodules to the global output of the pipeline, " +
					"data from these slots will be passed back to Mayday.");
		}
		public void execute() {};
	}	

	public String getName() {
		return _name;
	}
	
	public String getDescription() {
		return _description;
	}

	public String getCategory() {
		return _category;
		
	}

	public void setCategory(String category) {
		_category=category;
	}
	
	public void setDescription(String description) {
		_description=description;
	}

	
	public void setName(String name) {
		_name=name;
	}

	
}
