package mayday.core;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import mayday.core.gui.GUIUtilities;
import mayday.core.structures.maps.MultiHashMap;


/**
 * 
 * @author Stephan Symons <symons@informatik.uni-tuebingen.de>
 * @version 0.5
 */
public class ClassSelectionModel 
{

	private List<String> _classNames = new LinkedList<String>();
	private List<String> _partition = new LinkedList<String>();
	private List<String> _objectNames = new LinkedList<String>();

	private final static String NOCLASS = "** not in a class **";

	/**
	 * Default constructor
	 */
	public ClassSelectionModel() { }

	public void addObject(String objectName, String className) {
		if (className==null)
			className = NOCLASS;
		className = getOrAddClass(className);
		if (_objectNames.contains(objectName))
			throw new RuntimeException("Object already in class selection model: "+objectName);
		_objectNames.add(objectName);
		_partition.add(className);
	}

	public void addObject(String objectName) {
		addObject(objectName, null);
	}

	public void removeObject(String objectName) {
		int oidx = _objectNames.indexOf(objectName);
		if (oidx<0)
			throw new RuntimeException("Object not in class selection model: "+objectName);
		_objectNames.remove(oidx);
		_partition.remove(oidx);
	}

	public void addClass(String className) {
		if (_classNames.contains(className))
			throw new RuntimeException("Class Name already in class selection model: "+className);
		_classNames.add(className);
	}

	public void removeClass(String className) {
		if (!_classNames.remove(className))
			throw new RuntimeException("Class Name not in class selection model: "+className);
		for (int i=0; i!=_partition.size(); ++i)
			if (_partition.get(i).equals(className))
				_partition.set(i, NOCLASS);				
	}

	/**
	 * Renames a class
	 * @param index
	 * @param name
	 */
	public void setClassName(int index, String name) {
		if (name.equals(NOCLASS))
			name+="*";
		String className = _classNames.get(index);
		for (int i=0; i!=_partition.size(); ++i)
			if (_partition.get(i).equals(className))
				_partition.set(i, name);
		_classNames.set(index, name);
	}

	/**
	 * Set the class for a given object
	 * @param obj The index of the object
	 * @param className The class name to be set
	 */
	public void setClass(int index, String className) {
		String oldClass = getObjectClass(index);
		_partition.set(index, getOrAddClass(className));
		for (int i=0; i!=_partition.size(); ++i)
			if (_partition.get(i).equals(oldClass))
				return;
		_classNames.remove(oldClass);
	}


	protected String getOrAddClass(String name) {
		if (!name.equals(NOCLASS)) {// do not add the NOCLASS to the list of class names
			if (_classNames.indexOf(name)<0)
				_classNames.add(name);
			return _classNames.get(_classNames.indexOf(name));
		} else
			return NOCLASS;
	}

	/**
	 * Uses the experiments of the masterTable as object names
	 * @param masterTable
	 */
	public ClassSelectionModel(final MasterTable masterTable) {
		for(int i=0; i!= masterTable.getNumberOfExperiments(); ++i)
			addObject(masterTable.getExperimentName(i));
	}

	/**
	 * Uses the probe names as object names
	 * @param masterTable
	 * @param probeLists
	 */
	@SuppressWarnings("unchecked")
	public ClassSelectionModel(final MasterTable masterTable, final List probeLists) {
		List<Probe> probes=ProbeList.mergeProbeLists(probeLists, masterTable);
		for(Probe p:probes)
			addObject(p.getName());
	}

	/** 
	 * @param objectCount
	 * @param classCount
	 */
	public ClassSelectionModel(final int objectCount, final int classCount)
	{
		if (objectCount==0 || classCount==0)
			throw new RuntimeException("Class Selection Model needs to have at least one object and one class");
		for (int i=0; i!=classCount; ++i)
			getOrAddClass(""+i);
		for (int i=0; i!=objectCount; ++i) {
			addObject(""+i, _classNames.get(0));
		}
	}

	/**
	 * @param objectNames The names of the objects
	 */
	public ClassSelectionModel(final Collection<String> objectNames)
	{
		for (String object : objectNames)
			addObject(object);
	}

	/**
	 * @param objectNames The names of the objects...
	 * @param classNames ...and their classes 
	 */
	public ClassSelectionModel(final List<String> objectNames, final List<String> partition)
	{
		if (objectNames.size()!=partition.size())
			throw new RuntimeException("Number of objects and number of classes must match");
		if (objectNames.size()==0) // both sizes are equal now
			throw new RuntimeException("Class Selection Model needs to have at least one object and one class");
		for (int i=0; i!=objectNames.size(); ++i)
			addObject(objectNames.get(i), partition.get(i));
	}


	/**
	 * Parses class labeling from strings<br>
	 * Example string: "a,b,c\nd,e,f\ng,h" for three classes of 3,3,2 items each.
	 * It is also allowed, but no required,  to add class names as in:  "class1:a,b,c\nclass2:d,e,f\nclass3:g,h"
	 * Additional Whitespace is allowed, but only lines containing labels are ignored. 
	 * @param classLabeling the class labeling String to be de-serialized
	 * @see toString() which serializes a model into this format, omitting the class names.
	 * @see toString(boolean) which serializes a model into this format, allowing to choose whether to use the class names.  
	 * 
	 */
	public ClassSelectionModel(final String classLabeling)
	{
		fromString(classLabeling);
	}

	/**
	 * Parses class labeling from strings<br>
	 * Example string: "a,b,c\nd,e,f\ng,h" for three classes of 3,3,2 items each.
	 * It is also allowed, but no required,  to add class names as in:  "class1:a,b,c\nclass2:d,e,f\nclass3:g,h"
	 * Additional Whitespace is allowed, but only lines containing labels are ignored. 
	 * @param classLabeling the class labeling String to be de-serialized
	 * @see toString() which serializes a model into this format, omitting the class names.
	 * @see toString(boolean) which serializes a model into this format, allowing to choose whether to use the class names.  
	 * 
	 */
	public void fromString(final String classLabeling)
	{
		clear();

		String classes[]=classLabeling.split("\n");
		//remove empty lines: 
		List<String> classes2 = new ArrayList<String>();
		for(String s:classes)
		{
			if(s.trim().length()>0) classes2.add(s);
		}

		for(int i=0; i!= classes2.size(); ++i)
		{
			String objects[];

			String className = null;

			if(classes2.get(i).indexOf(":")>0) {
				className = classes2.get(i).substring(0, classes2.get(i).indexOf(":"));
				className = unwrapString(className);
				if (!className.equals(NOCLASS))
					_classNames.add(className);
				objects=classes2.get(i).substring(classes2.get(i).indexOf(":")+1).split(",");
			}else
			{
				className = "Class"+i;				
				_classNames.add(className);
				objects=classes2.get(i).split(",");
			}
			for(String o:objects)
			{
				if(o.trim().length()==0) continue;
				addObject(unwrapString(o), className);
			}
		}
	}

	private static final String[] wrapFrom = new String[]{"~","=","\n",":",","};
	private static final String[] wrapTo = new String[]{"~tilde~","~equals~","~newline~","~colon~","~comma~"};
	
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
	
	public String serialize() {
		String oind ="";
		if (_objectNames.size()>0) {
			oind = wrapString(_objectNames.get(0));
			for (int i=1; i<_objectNames.size(); ++i)
				oind+=":"+wrapString(_objectNames.get(i));
		}
		String res = toString(true);
		return oind+'\n'+res;
	}

	public static ClassSelectionModel deserialize(String s) {
		ClassSelectionModel csm = new ClassSelectionModel();
		csm.initFromString(s);
		return csm;
	}
	
	protected void initFromString(String s) {
		String oind = s.substring(0, s.indexOf('\n'));
		String res = s.substring(s.indexOf('\n')+1);
		HashMap<String, Integer> oindmap = new HashMap<String, Integer>();
		for (String oname : oind.split(":")) {
			oname = unwrapString(oname);
			this.addObject(oname);
			oindmap.put(oname, oindmap.size());
		}
		ClassSelectionModel helper = new ClassSelectionModel(res);
		for (int i=0; i!=helper.getNumObjects(); ++i) {
			String oname = helper.getObjectName(i);
			String oclass = helper.getObjectClass(i);
			this.setClass(oindmap.get(oname), oclass);
		}
	}
	
	public ClassSelectionModel(ClassSelectionModel otherModel) {
		initFromString(otherModel.serialize());
	}

	/**
	 * @return The number of classes
	 */
	public int getNumClasses()	{
		return _classNames.size();
	}

	/**
	 * @return The number of objects
	 */
	public int getNumObjects(){
		return _objectNames.size();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return toString(false);
	}

	/** DO NOT USE THIS FUNCTION TO SERIALIZE THE OBJECT AS INDICES ARE NOT STORED. Only the mapping classname->objectname is serialized (if param=true).
	 * @param useClassNames write class names as well, if false, objects without a class are not exported
	 * @return
	 */
	public String toString(boolean useClassNames)
	{
		StringBuffer sb=new StringBuffer();
		for(int i=0; i!= _classNames.size();++i) {
			if(useClassNames) {
				String cname = _classNames.get(i);
				cname = wrapString(cname);
				sb.append(cname+":");
			}
			writeClassContent(sb, _classNames.get(i));
			sb.append("\n");
		}
		if (useClassNames && _partition.contains(NOCLASS)) {
			sb.append(NOCLASS+":");
			writeClassContent(sb, NOCLASS);
		} else {		
			if (_classNames.size()>0)
				sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}

	protected void writeClassContent(StringBuffer sb, String className) {
		for(int j=0; j!=_partition.size(); ++j)
		{				
			if(_partition.get(j).equals(className))
			{
				sb.append(wrapString(_objectNames.get(j))+",");
			}
		}
		sb.deleteCharAt(sb.length()-1);
	}

	/**
	 * @param index
	 * @return
	 */
	public String getObjectName(int index)
	{
		return _objectNames.get(index);
	}

	/**
	 * @param index
	 * @return The class label of the object at index index.
	 */
	public String getObjectClass(int index)
	{
		return _partition.get(index);
	}

	/**
	 * @param className
	 * @return A list of the indices of the members of class className. 
	 */
	public List<Integer> toIndexList(String className)
	{
		List<Integer> res=new ArrayList<Integer>();
		for(int i=0; i!= _partition.size(); ++i)
		{
			if(_partition.get(i).equals(className))
			{
				res.add(i);
			}
		}
		return res;
	}

	/**
	 * @param classIndex
	 * @return A list of the indices of the members of class classIndex. 
	 */
	public List<Integer> toIndexList(int classIndex)
	{
		return toIndexList(_classNames.get(classIndex));
	}

	/**
	 * 
	 * @param className
	 * @return String representation of the objects belonging to the given class
	 */
	public String toString(String className)
	{
		StringBuffer sb=new StringBuffer("Class "+className+":");
		for(int i=0; i!= _partition.size(); ++i)
		{
			if(_partition.get(i).equals(className))
			{
				sb.append(i+",");
			}
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}

	/**
	 * @param i Class Index
	 * @return String representation of the objects belonging to the class with index i
	 */
	public String toString(int i)
	{
		return toString(i,false);
	}

	/**
	 * @param i Class Index
	 * @param showClassNames Whether class names should be displayed.
	 * @return String representation of the objects belonging to the class with index i
	 */
	public String toString(int i, boolean showClassNames)
	{
		String t= toString(_classNames.get(i));
		if(!showClassNames)
			return t.substring(t.indexOf(":")+1);
		else
			return t;
	}

	/**
	 * @return
	 */
	public List<String> getClassNames() 
	{
		return Collections.unmodifiableList(_classNames);
	}

	/**
	 * Renames the classes, adds new classes or removes classes if the new list is shorter than the current
	 * @param classNames
	 */
	public void setClassNames(List<String> classNames) 
	{
		int i=0;
		int renameable = Math.min(classNames.size(), _classNames.size());
		for (i=0; i!=renameable; ++i) {
			setClass(i, classNames.get(i));
		}
		// add classes
		for (i=renameable; i<classNames.size(); ++i) {
			getOrAddClass(classNames.get(i));
		}
		// remove classes
		for (i=renameable; i<_classNames.size(); ++i) {
			removeClass(_classNames.get(renameable)); // always remove last entry
		}
	}

	/**
	 * @return
	 */
	public List<String> getObjectNames() 
	{
		return Collections.unmodifiableList(_objectNames);
	}

	/** 
	 * Clears the model and adds the provided object names, all assigned to the unnamed default class
	 * @param objectNames
	 */
	public void setObjectNames(List<String> objectNames) 
	{
		_partition.clear();
		_objectNames.clear();
		for (String object : objectNames)
			addObject(object);
	}

	protected List<String> returnedPartition;
	protected int hashCode=-1;

	/**
	 * @return
	 */
	public List<String> getPartition() {
		if (returnedPartition==null || hashCode != _partition.hashCode()) {
			LinkedList<String> partitionClone = new LinkedList<String>();
			for (int i=0; i!=_partition.size(); ++i)
				if (_partition.get(i)==NOCLASS)
					partitionClone.add(null);
				else
					partitionClone.add(_partition.get(i));
			returnedPartition = partitionClone;
			hashCode = _partition.hashCode();
		}
		return Collections.unmodifiableList(returnedPartition);
	}

	/**
	 * @param partition
	 */
	public void setPartition(List<String> partition) {
		for (int i=0; i!=partition.size(); ++i)
			if (i>=getNumObjects())
				addObject(""+i, partition.get(i));
			else
				setClass(i, partition.get(i));
	}

	/**
	 * Parse the file, by determining the type of the file and calling an appropriate parser method.
	 * @param file The file to be parsed
	 * @return true if successful 
	 * @throws IOException
	 * @see parseMaydayFormat
	 * @see parseGSEAFormat
	 * @see parseSimpleFormat
	 */
	public boolean parse(File file) throws IOException
	{
		BufferedReader r=new BufferedReader(new FileReader(file));
		String line=r.readLine();
		r=new BufferedReader(new FileReader(file));
		boolean res=false;
//		if(line.matches("\\d+ \\d+ \\d+"))
//		{
//			r.reset();
//			//System.out.println("GSEA-format");
//			res=parseGSEAFormat(r);
//			r.close();
//		}
		if(line.split("\\s").length>2 && r.ready())
		{
			//System.out.println("simple-format");
			res=parseSimpleFormat(r);
		}
		if(line.split("\t").length==2 && r.ready())
		{
			r=new BufferedReader(new FileReader(file));
			//System.out.println("Mayday-format");
			res=parseMaydayFormat(r);
		}
		if(r.ready())
		{
			throw new IOException("Error parsing file");
		}
		return res;
	}

	/**
	 * Parse a file containing class labels in the Mayday format, with one class 
	 * name and the corresponding class label in one line 
	 * @param r
	 * @return
	 * @throws IOException
	 */
	public boolean parseMaydayFormat(BufferedReader r) throws IOException
	{
		clear();

		String line="";
		line=r.readLine();
		while(line!=null && line.length()>0 && !line.equals(""))
		{            
			String[] split=line.split("\\s");
			if(split.length!=2)
			{
				throw new IOException("Error reading file.");
			}

			addObject(split[0].trim(), split[1].trim());            
			//System.out.println(split[0].trim()+"\t"+split[1].trim());
			line=r.readLine();
		}       
		//        System.out.println("Class Names: "+classNames.toString());
		//        System.out.println("Objects: "+objectNames.toString());
		//        System.out.println("Partiton: "+partition.toString());
		return true;
	}

	/**
	 * Parse a file containing values in a simple format with either 
	 * one line class labels or two lines, the first containing object names
	 * the second one containing class labels 
	 * @param r
	 * @return true if successful
	 * @throws IOException
	 */
	public boolean parseSimpleFormat(BufferedReader r) throws IOException
	{
		_partition.clear();
		_classNames.clear();

		String[] line1=r.readLine().split("\\s");
		String secondLine=r.readLine();
		String[] line2=secondLine!=null?secondLine.split("\\s"):null;

		if(line2!=null && line2.length==line1.length)
		{
			_objectNames.clear();
			if(line1.length!=line2.length)
			{
				throw new IOException("Error parsing file");
			}
			for(int i=0; i!= line1.length; ++i)
			{
				addObject(line1[i], line2[i]);
			}    	
		}else
		{

			for(int i=0; i!= line1.length; ++i)
			{
				_partition.set(i, "");
				setClass(i, getOrAddClass(line1[i]));
			}   
		}    	
		return true;

	}

//	/**
//	 * Parse a file containing class labes in GSEA format.
//	 * @param r A BufferedReader object
//	 * @return true if successful 
//	 * @throws IOException
//	 * @Deprecated This function only clears the model but doesn't do anything except checking if the file could principally be parsed
//	 * 
//	 */
//	public boolean parseGSEAFormat(BufferedReader r) throws IOException
//	{
//		// to do: this function does not do anything!
//		clear();
//
//		Map<String, String> values = new HashMap<String, String>();
//		String line="";
//		r.mark(100);
//		line=r.readLine(); //1
//
//		if(line.matches("\\d+ \\d+ \\d+"))
//		{
//			//this is a GESA class file-parse it:
//			//skip first and second line 
//			line=r.readLine(); //2
//			line=r.readLine(); //3 -> here we are:
//			String[] val=line.split(" ");
//			for(int i=0; i!= val.length; ++i)
//			{
//				values.put("Object"+i,val[i].trim());                
//			}           
//		}else
//		{
//			throw new IOException("Error parsing file");
//		}
//		return true;
//	}


	/**
	 * Save the class partition to a file
	 * @param file The target file
	 * @return true if succesful.
	 * @throws IOException
	 */
	public boolean write(File file) throws IOException
	{
		BufferedWriter w=new BufferedWriter(new FileWriter(file));
		for(int i=0; i!= _partition.size(); ++i) {
			w.write(_objectNames.get(i)+"\t"+_partition.get(i)+"\n");
		}    		
		w.flush();
		return true;
	}



	/**
	 * Calculates the absolute number of occurrences of the class label in the partition
	 * @param className
	 * @return The number of occurrences of the class in the partition
	 */
	public int getClassCount(String className) {
		int c=0;
		for(String s: _partition) {
			if(s.equals(className)) c++;    			
		}
		return c;
	}


	/**
	 * Calculates the relative frequency of the class label in the partition
	 * @param className
	 * @return The frequency of the class in the partition
	 */
	public double getClassFrequency(String className)
	{
		return (double)getClassCount(className) / (double)_partition.size();
	}

	/**
	 * @return A map of object name -> class name, for Mayday ML plugins compatibility. 
	 */
	public Map<String,String> convertToMap()
	{
		Map<String,String> res=new HashMap<String, String>();
		for(int i=0; i!= _partition.size();++i)
		{
			res.put(_objectNames.get(i), _partition.get(i));
		}
		return res;
	}
	
	/*
	 * @return A multimap of class name -> obejct name(s) 
	 */
	public MultiHashMap<String,String> convertToMultiMap()
	{
		MultiHashMap<String,String> res=new MultiHashMap<String, String>();
		for(int i=0; i!= _partition.size();++i)
		{
			res.put(_partition.get(i), _objectNames.get(i));
		}
		return res;
	}

	/**
	 * @return The first index of the second class label, or -1 if one or more than two classes.
	 */
	public int convertToSplit()
	{
		if(_classNames.size()!=2) return -1;

		int s=_partition.indexOf(_classNames.get(0));
		int t=_partition.indexOf(_classNames.get(1));

		if(s>t)	return s;
		return t;
	}

	/**
	 * @return  An ordered list of the class labels
	 */
	public List<String> getClassesLabels()
	{
		List<String> res= new ArrayList<String>();
		res.addAll(this._classNames);
		Collections.sort(res);
		return res;
	}

	/**
	 * Erase the content of the object
	 */
	public void clear()
	{
		_objectNames.clear();
		_partition.clear();
		_classNames.clear();
	}

	/**
	 * Set an objects name
	 * @param i The index of the object
	 * @param name The name
	 */
	public void setObjectName(int i, String name)
	{    	
		_objectNames.set(i,name);
	}


	/**
	 * removes all classes that are not assigned to any object
	 * @Deprecated not needed any more
	 */
	public void tidyUp()
	{
		// is already implicitely guaranteed
	}

	/**
	 * Uses Collections.shuffle  to permutate the class partition
	 * @return
	 */
	public ClassSelectionModel permute()
	{
		return permute(null);
	}

	/**
	 * Uses Collections.shuffle  to permutate the class partition
	 * @return
	 */
	public ClassSelectionModel permute(Random rand)
	{
		ClassSelectionModel result=new ClassSelectionModel();
		result._classNames=_classNames;
		result._objectNames=_objectNames;

		List<String> part=new ArrayList<String>();
		for(String s: _partition)
		{
			part.add(s);
		}
		if (rand==null)
			Collections.shuffle(part);
		else
			Collections.shuffle(part,rand);
		result._partition=part;
		return result;    	
	}

	public String getNoClassLabel() {
		return NOCLASS;
	}
	

	
	public Color getColorForClass(String className) {
		int ix = _classNames.indexOf(className);
		if (ix<0)
			return Color.black;
		return getColor(ix,getNumClasses());
	}
	
	public Color getColorForObject(String objectName) {
		String className = getClassOf(objectName);
		if (className == NOCLASS)
			return Color.black;
		return getColorForClass(className);
	}
	
	public String getClassOf(String objectName) {
		int ix = _objectNames.indexOf(objectName);
		if (ix<0)
			return NOCLASS;
		return _partition.get(ix);
	}
	
	/**
	 * @param c
	 * @param numClasses
	 * @return A color for the c'th class of a total numClasses classes
	 */
	public static Color getColor(int c, int numClasses)
	{
		return c<0?Color.black:GUIUtilities.rainbow(numClasses, 1d)[c];
		//		System.out.println(numClasses);
		//		final Color[] colors={		
		//				new Color(0xFF0000), new Color(0xFF3300), new Color(0xFF6600), new Color(0xFF9900), new Color(0xFFCC00), new Color(0xFFFF00),
		//				new Color(0xCCFF00), new Color(0x99FF00), new Color(0x66FF00), new Color(0x33FF00), new Color(0x00FF00), new Color(0x00FF33),
		//				new Color(0x00FF66), new Color(0x00FF99), new Color(0x00FFCC), new Color(0x00FFFF), new Color(0x00CCFF), new Color(0x0099FF),
		//				new Color(0x0066FF), new Color(0x0033FF), new Color(0x0000FF), new Color(0x3300FF), new Color(0x6600FF), new Color(0x9900FF),
		//				new Color(0xCC00FF), new Color(0xFF00FF), new Color(0xFF00CC), new Color(0xFF0099), new Color(0xFF0066), new Color(0xFF0033)
		//		};
		//		return c < 0?Color.BLACK:colors[(30*(c))/numClasses];
	}

}
