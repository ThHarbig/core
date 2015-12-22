package mayday.core.io;

// Author: Florian Battke

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * A StorageNode is one element in a tree representation of data (as key-value pairs). 
 * It allows saving and loading of the whole tree or of subtrees.  
 * @author Florian Battke
 */
public class StorageNode {
	
	public String Name;
	public String Value;
	
	protected static Pattern splitter = Pattern.compile("=");
	
	protected TreeMap<String, StorageNode> children = new TreeMap<String, StorageNode>();

	/**
	 * Sets this node's name(=key) and value.
	 * @param name the node's name
	 * @param value the node's value
	 */
	public StorageNode(String name, String value) {
		Name= (name==null) ? "" : name;
		Value= (value==null) ? "" : value;
	}
	
	public StorageNode() {
		this(null,null);
	}
	
	/** Saves ONLY the subtree below this node to a given Writer
	 * @param wr A BufferedWriter to save to
	 * @throws Exception if the BufferedWriter fails
	 */
	public void saveTo(BufferedWriter wr) throws Exception {
		saveTo(wr, false);
	}
	
	/** Saves this node's name and value and the subtree below this node to a given Writer
	 * @param wr A BufferedWriter to save to
	 * @param includeSelf set to true to store the name and value of the current node as well, if false, only store children
	 * @throws Exception if the BufferedWriter fails
	 */
	public void saveTo(BufferedWriter wr, boolean includeSelf) throws Exception {
		saveTo(wr, null, includeSelf);
		wr.flush();
	}
	
	protected void saveTo(BufferedWriter wr, String depth, boolean includeSelf) throws Exception {
		if (includeSelf) {
			String ndepth = depth==null?"":depth;
			wr.write(ndepth + wrapString(Name.trim()) +"="+ wrapString(Value) +"\n");  // 060501-WRAP STRINGS
			depth=ndepth;
		}
		for (StorageNode child : children.values()) 
			child.saveTo(wr, depth==null?"":depth+" ", true);
	}
	
	
	public String toDebugString() {
		StringWriter sw = new StringWriter();
		try {
			saveTo(new BufferedWriter(sw));
			return sw.toString();
		} catch (Exception e) {
			return e+": "+e.getMessage();
		}
	}
	
	/** Reads the subtree below this node from a reader
	 * @param rd A BufferedReader to load from
	 * @throws Exception if the BufferedReader fails
	 */
	public void loadFrom(BufferedReader rd) throws Exception {
		loadFrom(rd, false);
	}
	
	/** Reads this node and the subtree below this node from a reader
	 * @param rd A BufferedReader to load from
	 * @param includeSelf set to true to load the name and value of the current node as well, if false, only load children
	 * @throws Exception if the BufferedReader fails
	 */
	public void loadFrom(BufferedReader rd, boolean includeSelf) throws Exception {
		// read my name and value
		loadFrom(rd, null, includeSelf);
	}
	
	protected void loadFrom(BufferedReader rd, String depth, boolean includeSelf) throws Exception {				

		String line = null;
		
		if (includeSelf) {
			line=rd.readLine();
			// read my name and value
			try {
				if (depth!=null && depth.length()>0)
					line = line.substring(depth.length());
				String[] elements = splitter.split(line);
				Name=unwrapString(elements[0]);
				Value=(elements.length>1) ? unwrapString(elements[1]) : "";
			} catch (NullPointerException noe) {
				System.out.println("Could not load StorageNode from String: "+line);
			}
			if (depth==null) 
				depth="";
		}

		String newdepth= (depth!=null)? depth+" " : "";

		
		boolean hasChild = false;
		// look for children
		do {

			// see if a child starts
			rd.mark(2*newdepth.length()+1);
			if (rd.read()==-1) { // end-of-stream
				hasChild=false;
			} else {
				rd.reset();				
				char[] testdepth = new char[newdepth.length()];
				hasChild = rd.read(testdepth)==newdepth.length() && new String(testdepth).equals(newdepth);
			}
			
			rd.reset();
			
			if (hasChild) {
				//this is a child
				StorageNode child = createEmptyChild();
				child.loadFrom(rd, newdepth, true);
				children.put(child.Name, child);
			} 
		} while (hasChild);
		// if line.startsWith(depth) then the next line is a sibling of this		
	}
	
	/** Adds a new child node to this node
	 * @param name the name of the new child
	 * @param value the value of the new child
	 * @return the new child node.
	 */
	public StorageNode addChild(String name, Object value) {
		StorageNode n = new StorageNode(name, value.toString());
		children.put(n.Name, n);
		return n;
	}
	
	public StorageNode addChild(Object name, Object value) {
		return addChild(name.toString(), value);
	}
	
	/** Adds an existing node (and the subtree below it) as a child to this node 
	 * @param childNode the node to adopt as a child
	 */
	public void addChild(StorageNode childNode) {
		children.put(childNode.Name, childNode);
	}
	 
	/** Returns the number of children for this node
	 * @return the number of children
	 */
	public int childCount() {
		return children.size();
	}
	
	/** Returns all children in a collection
	 * @return the children
	 */
	public java.util.Collection<StorageNode> getChildren() {
		return children.values(); 
	}
	
	/** Returns a child specified by its name(=key) 
	 * @param byName the name of the child to return
	 * @return the child with the given name or null if there is no such child
	 */
	public StorageNode getChild(String byName) {
		return children.get(byName);
	}
	
	public StorageNode getChild(Object name) {
		return getChild(name.toString());
	}
	
	private static final String[] wrapFrom = new String[]{"~","=","\n"};
	private static final String[] wrapTo = new String[]{"~tilde~","~equals~","~newline~"};
	
	private static String replaceAll(String in, String[] from, String[] to, int direction) {		
		String out = in;
		if (direction==1)
			for (int i=0; i!=from.length; ++i) 
				out = out.replace(from[i],to[i]);
		else if (direction==-1) //unescape must be done backwards! 
			for (int i=from.length-1; i>=0; --i) 
				out = out.replace(from[i],to[i]);
		return out;
	}
	
	public static String unwrapString(String wrapped) {
		return replaceAll(wrapped, wrapTo, wrapFrom, -1);
	}
	
	public static String wrapString(String unwrapped) {
		return replaceAll(unwrapped, wrapFrom, wrapTo, 1);
	}
	
	public String toString() {
		return Name+"="+Value;
	}
	
	protected StorageNode createEmptyChild() {
		return new StorageNode(null,null);
	}

}
