package mayday.mpf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.TreeMap;

import mayday.core.io.StorageNode;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.filemanager.FMFile;
import mayday.mpf.plumawrapper.WrappedMPFModule;
import mayday.mpf.plumawrapper.WrappedMPFModule_Basic;

/**
 * FilterClassList acts as a Plugin Manager for MPF filter plugins.
 * This class scans the MPF/Filters directory to find available plugins, 
 * holds information about those filters and provides methods to create new
 * instances. This class is a singleton.
 * @author Florian Battke
 */
public class FilterClassList {
	
	private static FilterClassList theInstance;
	private static PluginInfo ComplexFilterPluginInfo = null;
	
	public static FilterClassList getInstance() {
		if (theInstance==null)
			theInstance = new FilterClassList();
		return theInstance;
	}
	
	public static void dropInstance() {
		theInstance=null;
	}
	
	/**
	 * FilterClassList.Item represents a MPF filter plugin found by FilterClassList
	 * @author Florian Battke
	 */
	@SuppressWarnings("unchecked")
	public class Item implements Comparable {
		private final String mName;
		private final String mDesc;
		private final PluginInfo pli;
		private final String mComplexInit;
		private final String mCategory;
		
		/** the relative path of the filter file on disk (either .class or .mpd) */
		public final String filename; 
		
		/** true for complex filters (those loaded from a .mpd file) */
		public final boolean isComplex;
		
		/** the version number of a given filter */
		public final int mVersion;
		
		/** the number of input slots of the filter */
		public final int InputSize;
		
		/** 
		 * returns the name of the filter, e.g. for display in Swing components.
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return mName;
		}
		
		/** returns the description of the filter
		 * @return the description String
		 */
		public String getDescription() {return mDesc;}
		
		public String getCategory() {return mCategory;}
		
		/** creates a new instance of the filter represented by this item
		 * @return the newly created instance of the filter
		 * @throws Exception if the filter can not be instantiated
		 * @see Class#newInstance() for more information on possible exceptions 
		 */
		public FilterBase newInstance() throws Exception {
			FilterBase fb = null;
			if (mComplexInit!=null) {
				fb = new ComplexFilter(mComplexInit); 
			} else {
				return ((WrappedMPFModule)pli.newInstance()).getMPFModule();
//				if (pli instanceof WrappedMPFPluginInfo_Basic)
//					fb = ((WrappedMPFPluginInfo_Basic)pli).getFilterBase().getClass().newInstance();
//				else
//					fb = (FilterBase)(pli.newInstance());
//				if (fb == null)
//					throw new Exception("Cannot initialize a new instance of "+mName+"."); // Why?
			}
			return fb;
		}
		
		/** Creates a new instance of this class representing a ComplexFilter
		 * @param Name the name of the represented fiter
 		 * @param c the Class type
		 * @param Description the description String
		 * @param InitString the filename of the complex filter description file (.mpd) 
		 * @param InSize the number of input slots
		 * @param Version the version number as reported by the filter
		 * @param Filename of the .mpd file (relative)
		 */
		public Item(String Name, PluginInfo Pli, String Category, String Description, String InitString, int InSize, int Version, String Filename) {
			isComplex=true; mComplexInit=InitString;
			mCategory=Category;
			mName=Name; pli=Pli;  InputSize=InSize; mDesc=Description; mVersion=Version; filename=Filename;
		}
		
		/** Creates a new instance of this class representing a not-complex filter
		 * @param Name the name of the represented filter
		 * @param c the Class type
		 * @param Description the description String
		 * @param InSize the number of input slts
		 * @param Version the version number as reported by the filter
		 * @param Filename to the .class file (relative)
		 */
		public Item(String Name, PluginInfo Pli, String Category, String Description, int InSize, int Version, String Filename) {
			isComplex=false; mComplexInit=null; 
			mCategory=Category;
			mName=Name; pli=Pli; InputSize=InSize; mDesc=Description; mVersion=Version; filename=Filename;
		}

		public int compareTo(Object o) {
			String otostring = o.toString();
			int result = otostring.compareTo(this.toString()); 
			return result;
			//return ((FilterClassList.Item)o).toString().compareTo(this.toString());
		}
	}
	
	private TreeMap<String, Item> elements = new TreeMap<String, Item>(); //treemap = hashmap with automatic sorting
	
	/** Creates a new instance of FilterClassList and scans for MPF plugins in the given path
	 * @param SearchInPath the path to search 
	 */
	private FilterClassList() {
		ComplexFilterPluginInfo = PluginManager.getInstance().getPluginFromID(Constants.ComplexFilterID);
		buildList();
	}

	private void update(Item newItem) {
		if (newItem!=null)
			elements.put(newItem.toString(), newItem);
	}
	
	public void remove(Item removeItem) {
		elements.remove(removeItem.toString()); 
	}
	
	public void update(String filename) {
		// this is called by designer in case a file is changed on disk or newly created
		try {
			  update(evaluateComplexfile(new FileInputStream(filename),filename));
		} catch (Exception e) {
			  ExceptionHandler.handle(new Exception("\""+filename+"\" could not be added to the module list."),(javax.swing.JFrame)null);
		}
	}
	
	
	public void update(FMFile cfile) {
		try {
		  update(evaluateComplexfile(cfile.getStream(),cfile.getFullPath()));
		} catch (Exception e) {
		  ExceptionHandler.handle(new Exception("\""+cfile.Name+"\" could not be added to the module list."),(javax.swing.JFrame)null);
		}
	}
	
	private Item evaluateClass(PluginInfo pli) throws Exception {
		AbstractPlugin apl = pli.getInstance() ;
		if (apl instanceof WrappedMPFModule_Basic) {
			FilterBase fb = ((WrappedMPFModule_Basic)apl).getMPFModule();
			return new Item(fb.getName(), pli, fb.getCategory(), fb.getDescription(), fb.InputSize, fb.Version, null);
		}
		return (Item)null;
	}
	
	private Item evaluateComplexfile(InputStream is, String fpath) throws Exception {
		StorageNode temp = new StorageNode();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		temp.loadFrom(br);
		br.close();
		String FilterName = temp.getChild("Name").Value;
		String FilterDesc = temp.getChild("Desc").Value;
		StorageNode category;
		String FilterCategory = ((category=temp.getChild("Category"))!=null) ? FilterCategory = category.Value : "Unassigned";;
		int Version = Integer.parseInt(temp.getChild("Ver").Value);
		int InSize = Integer.parseInt(temp.getChild("InputSize").Value);
		return new Item(FilterName, ComplexFilterPluginInfo, FilterCategory, FilterDesc, fpath, InSize, Version, fpath);		
	}
	
	private void buildList() {
		// load classes
		for (PluginInfo pli : PluginManager.getInstance().getPluginsFor(Constants.masterComponent)) {
			try {
				update(evaluateClass(pli));
			} catch (Throwable t) {
				System.err.println("MPF.FilterClassList: Can't initialize module "+pli.getIdentifier()+"\n"
						+t.getMessage());
				t.printStackTrace();
			}
		}
		// load pipelines
		for (FMFile mpdfile : PluginManager.getInstance().getFilemanager().getFiles(Constants.FILEEXT_REGEX, true)) {
			/* make sure all mpd files are extracted on disk, to keep external files in synch with 
			most recent repository version, overwrite local changes */ 			
			mpdfile.force_extract();  
			try {
				update(evaluateComplexfile(mpdfile.getStream(),mpdfile.getFullPath()));
			} catch (Throwable t) {
				System.err.println("MPF.FilterClassList: Can't initialize module "+mpdfile.getFullPath()+"\n"
						+t.getMessage());
				t.printStackTrace();
			}
		}		
	}
	

			
	/** Creates a new instance of a filter class referenced by the filter name
	 * @param byName the name of the filter class to instantiate
	 * @return the newly created instance
	 * @throws Exception if there is no filter by this name. Usually the filter is not found
	 * in the MPF plugin directory or the file was present but there was an exception when 
	 * FilterClassList tried to create an instance of the filter.    
	 */
	public FilterBase newInstance(String byName) throws Exception {
		if (!elements.containsKey(byName)) throw new Exception("The module \""+byName+"\" can't be found." +
				" Make sure the appropriate file(s) \nare in the Mayday module plugin directory.");
		return ((Item)elements.get(byName)).newInstance();
	}
	
	/** Checks for a specific filter version. 
	 * @param byName The name of the filter to be checked
	 * @param Version The desired version
	 * @return true if the name and version match an element of this list
	 *         false if no element matches the given name
	 * @throws Exception if name matches but version differs
	 */
	public boolean checkPresence(String byName, int Version) throws Exception {
		if (elements.containsKey(byName)) {
			if (((Item)elements.get(byName)).mVersion!=Version) 
			   throw new Exception(
					   "Version mismatch:\n" +
					   "Expected \""+byName+"\" version "+Version+"\n"+
					   "Found \""+byName+"\" version "+((Item)elements.get(byName)).mVersion+"\n"+
					   "This could be mean that the module behaves differently or that option semantics have changed.");
			else return true; 
		} else return false;
	}
	
	/** returns a collection of all MPF plugins found
	 * @return the collection of plugins
	 */
	public Collection<Item> getValues() {
		return elements.values();
	}
		
	public Item getItemByName(String name) {
		return elements.get(name);
	}
	
	public String[] getCategories() {
		TreeMap<String, String> vs = new TreeMap<String,String>();
		for (Item i : elements.values()) {
			vs.put(i.mCategory, i.mCategory); //duplicates are sorted out automatically ,)
		}
		String[] result = new String[vs.size()];
		int i=0;
		for (String s : vs.values()) {
			result[i++] = s;
		}
		return result;
	}	
}
