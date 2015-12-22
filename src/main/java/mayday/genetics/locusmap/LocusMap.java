package mayday.genetics.locusmap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.structures.ReferenceCache;
import mayday.genetics.Locus;
import mayday.genetics.LocusMIO;
import mayday.genetics.advanced.LocusData;
import mayday.genetics.advanced.chromosome.LocusChromosomeObject;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.Species;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;

/** Maps object names (strings) to locus information */
public class LocusMap implements LocusData {

	protected String name;
	protected HashMap<String, AbstractGeneticCoordinate> mapping = new HashMap<String, AbstractGeneticCoordinate>();
	protected Iterable<String> subset;
	protected int cachedSize;

	// always cache LAST USED instance of ChromosomeSetContainer, this gives HUGE speedups
	private static ReferenceCache<ChromosomeSetContainer> cache = new ReferenceCache<ChromosomeSetContainer>();

	
	public LocusMap(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public LocusMap(MIGroup mg) {
		this(mg.getMIManager().getDataSet().getName()+": "+mg.getName());
		for (Entry<Object, MIType> e : mg.getMIOs())
			mapping.put(e.getKey().toString(), ((LocusMIO)e.getValue()).getValue().getCoordinate());
	}
	
	/** the subset is static, changes in the featureSubset are not followed in the equals() and hashCode() functions */
	protected LocusMap(String name, HashMap<String, AbstractGeneticCoordinate> baseMap, Iterable<String> featureSubset) {
		this.name = name;
		this.mapping = baseMap;
		this.subset = featureSubset;
		if ((cachedSize=numberOfLoci(subset))==size())
			subset = null; // no need for subsetting
	}

	public String toString() {
		return name+ " ("+size()+" ids)";
	}
	
	public int size() {
		if (subset!=null)
			return cachedSize;			
		return mapping.size();
	}

	public int numberOfLoci(Iterable<String> featureNames) {
		int i=0;
		for (String s : featureNames)
			if (get(s)!=null)
				++i;
		return i;
	}

	public AbstractGeneticCoordinate get(String key) {
		return mapping.get(key);
	}
	
	public Iterable<String> keySet() {
		if (subset!=null)
			return subset;
		return mapping.keySet();
	}
		
	public void put(String name, AbstractGeneticCoordinate c) {
		if (c.isValid())
			mapping.put(name, c);
		else
			System.out.println("Coordinate invalid: "+name+" "+c);
	}
	
	public LocusMap subsetLocusMap(Iterable<String> featureNames) {
		return new LocusMap(name,mapping,featureNames);
	}
		
	/* LocusData interface */
	
	@SuppressWarnings("unchecked")
	public ChromosomeSetContainer asChromosomeSetContainer() {
		ChromosomeSetContainer csc = cache.getCache(this);
		
		if (csc==null) {
			csc = new ChromosomeSetContainer( new LocusChromosomeObject.Factory<String>() );
		
			for (String featureName : keySet()) {
				AbstractGeneticCoordinate locus = get(featureName);
				if (locus!=null) {
					Chromosome in_chr = locus.getChromosome();
					Species in_spc = in_chr.getSpecies();
					LocusChromosomeObject<String> out_chr = (LocusChromosomeObject<String>)csc.getChromosome(in_spc, in_chr.getId());
					out_chr.addLocus(locus.getModel(), featureName);
				}
			}
			
			cache.setCache(csc, this);
		}
		
		return csc;
	}
	
	public boolean equals(Object o) {
		if (o==this)
			return true;
		if (o instanceof LocusMap) {
			LocusMap other = (LocusMap)o;
			return other.hashCode()==this.hashCode(); 
		}
		return super.equals(o);
	}

	public int hashCode() {
		return 
		(subset!=null?subset.hashCode():0)
		+mapping.hashCode()
		+name.hashCode();
	}
	
	
	
	public void writeToStream(BufferedWriter bw) throws IOException {
		bw.write(name+"\n");
		bw.write(size()+"\n");
		
		LocusMIO lm = new LocusMIO();
		Locus lo = new Locus();
		lm.setValue(lo);
		
		for (String locusname : keySet()) {
			bw.write(locusname+"\n");
			lo.setCoordinate(get(locusname));
			bw.write(lm.serialize(MIType.SERIAL_TEXT)+"\n");
		}
		bw.flush();
	}
	
	public LocusMap(BufferedReader br) throws IOException {
		name = br.readLine();
		int i = Integer.parseInt(br.readLine());
		
		LocusMIO lm = new LocusMIO();
		
		for (int j=0; j!=i; ++j) {
			String key = br.readLine();
			String val = br.readLine();
			lm.deSerialize(MIType.SERIAL_TEXT, val);
			put(key,lm.getValue().getCoordinate());			
		}
	}

}
