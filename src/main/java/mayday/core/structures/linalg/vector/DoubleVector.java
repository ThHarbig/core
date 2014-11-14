package mayday.core.structures.linalg.vector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mayday.core.io.csv.ParsedLine;
import mayday.core.io.csv.ParserSettings;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.meta.NumericMIO;
import mayday.core.structures.linalg.Algebra;



public class DoubleVector extends AbstractVector {

	protected double[] array;
	protected String[] names;
	
	public DoubleVector(double[] array) {
		this.array = array;
	}
	
	public DoubleVector(Double[] array) {
		this(Algebra.<double[]>createNativeArray(array));
	}
	
	public DoubleVector(int length) {
		array = new double[length];
	}
		
	public DoubleVector(int[] iarray) {
		array = new double[iarray.length];
		for (int i=0; i!=iarray.length; ++i)
			array[i] = (double)iarray[i];
	}
	
	public DoubleVector(Collection<Double> cd) { 
		array = new double[cd.size()];
		int i=0;
		for (Double d : cd)
			array[i++] = d;
	}
	
	public DoubleVector(Map<Object, Number> map) {
		array = new double[map.size()];
		names = new String[map.size()];
		int i=0;
		for (Entry<Object, Number> e : map.entrySet()) {
			Number o = e.getValue();
			array[i] = o.doubleValue();
			names[i] = e.getKey().toString();
			++i;
		}		
	}
	
	@SuppressWarnings("unchecked")
	public DoubleVector(MIGroup mg) {
		if (!NumericMIO.class.isAssignableFrom(mg.getMIOClass()))
			throw new RuntimeException("Only numeric MIO types can be put into a vector.");
		array = new double[mg.size()];
		names = new String[mg.size()];
		int i=0;
		for (Entry<Object, MIType> e : mg.getMIOs()) {
			Object o = ((NumericMIO)e.getValue()).getValue();
			array[i] = ((Number)o).doubleValue();
			names[i] = e.getKey().toString();
			++i;
		}			
	}

	public static DoubleVector parse(File input, String separator, String commentChars, char quote, boolean hasHeaderLine) throws IOException {
		return parse(new FileInputStream(input), separator, commentChars, quote, hasHeaderLine);
	}
	
	public static DoubleVector parse(File input, ParserSettings psettings) throws IOException {
		return parse(new FileInputStream(input), psettings);
	}
	
	public static DoubleVector parse(InputStream input, String separator, String commentChars, char quote, boolean hasHeaderLine) throws IOException {
		ParserSettings psett = new ParserSettings();
		psett.commentChars = commentChars;
		psett.separator = separator;
		psett.quote = quote;
		psett.hasHeader = hasHeaderLine;
		return parse(input, psett);
	}
	
	public static DoubleVector parse(InputStream input, ParserSettings psettings) throws IOException {
		ArrayList<Double> values = new ArrayList<Double>();
		ArrayList<String> names = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(input));
		String line;
		ParsedLine pl = new ParsedLine("", psettings);
		
		if (psettings.hasHeader) // ignore header
			br.readLine();
		
		
		while ((line=br.readLine())!=null) {
			pl.replaceLine(line);
			if (!pl.isCommentLine()) {
				double d = Double.NaN;
				int i = 0;
				if (pl.size()>1) { // first is name
					names.add(pl.get(0));
					i = 1;
				} 
				try { 
					d = Double.parseDouble(pl.get(i));
				} catch (NumberFormatException nfe) {
					if (!pl.get(i).equals("NA"))
						throw nfe;
				}
				values.add(d);
			}
		}
		br.close();
		
		DoubleVector result = new DoubleVector(values);
		if (names.size()==values.size()) 
			result.setNames(names);
		else 
			throw new RuntimeException("Not all elements were named");
		
		return result;
	}
	
	@Override
	public double get0(int i) {
		return array[i];
	}

	@Override
	public void set0(int i, double v) {
		array[i] = v;
	}

	@Override
	public int size() {
		return array.length;
	}
	
	@Override
	public double[] toArrayUnpermuted() {
		return array;
	}

	
	public DoubleVector clone() {
		DoubleVector nv = clone( array );
		nv.setPermutation(indices);
		if (names!=null)
			nv.setNames(this);
		return nv;
	}
	
	public static DoubleVector clone( double[] array ) {
		double[] second = new double[array.length];
		System.arraycopy(array, 0, second, 0, array.length);
		return new DoubleVector( second );
	}
	
	public static DoubleVector rep( double value, int count) {
		double[] array = new double[count];
		for (int i=0; i!=array.length; ++i)
			array[i] = value;
		return new DoubleVector(array);
	}

	protected String getName0(int i) {
		if (names==null)
			return null;
		return names[i];
	}

	protected void setName0(int i, String name) {
		if (names==null)
			names = new String[size()];
		names[i] = name;
	}
	
	public List<String> getNamesList() {		
		if (indices==null)
			return Arrays.asList(names);
		else
   		    return super.getNamesList();
	}

	/** optimized implementation of the setNames command, does not COPY the names but replaces the name array. 
	 * fills the provided map on the first access via get(String). */
	public void setNamesDirectly(String[] Names, Map<String, Integer> nameCache) {
		if (Names.length!=size())
			throw new RuntimeException("Number of names must match number of vector elements: "+size());
		names = Names;
		this.nameCache = nameCache;
	}
	
	/** optimized implementation of the setNames command, does not COPY the names but replaces the name array */
	public void setNamesDirectly(String[] Names) {
		setNamesDirectly(Names, null);
	}
	
	/** optimized implementation of the setNames command, does not COPY the names but replaces the name array 
	 *  AND the cached name mapping*/
	public void setNamesDirectly(AbstractVector other) {
		if (other.size()!=size())
			throw new RuntimeException("Size of naming vector must be identical to size of data vector");
		if (other instanceof DoubleVector)
			names = ((DoubleVector)other).names;
		else {
			setNames(other.getNamesList());
		}
		nameCache = other.nameCache;		
	}
}
