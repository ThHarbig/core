package mayday.genetics.basic.coordinate;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.SpeciesContainer;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.coordinatemodel.GBAtom;
import mayday.genetics.coordinatemodel.GBNode;
import mayday.genetics.coordinatemodel.GBParser;


/**
 * A complex genetic coordinate is composed of one or more loci.
 * The loci are ordered, e.g. by order of translation in an exon model.
 * Loci can be on different strands, but they are all on the same species/chromosome 
 */


public class GeneticCoordinate extends AbstractGeneticCoordinate {

	private static final Pattern PATTERN_COMPLEX = Pattern.compile(
			"(.*)\\>" + //capturing group 1 --> species
/* 101015 fb: Removed the non-capturing group
 * Reason: Changing the name of chromosomes from the input has only a small benefit:
 *         Users get more concise chromosome names from badly formatted input files.
 *         The disadvantage is that importing locus data changes chrome names while
 *         importing mapped reads (for example) does not. The end result is that
 *         users wonder why their mapped reads are not mapped to the chromosomes
 *         from their annotation files. I.e. the reads are NOT CONSIDERED to be 
 *         mapped to their target chromosomes. 
 *   	        
 */		
//			"(?>.*chromosome|.*chr)?" + //non-capturing
			"(.+)"+ //capturing group 2 --> chrome
			":" +
			"\\s*(.+)\\s*+"  //capturing group 3, remove trailing exclamation mark 
			,Pattern.CASE_INSENSITIVE);

    private static final Pattern PATTERN_PRIMITIVE = Pattern.compile(
	        "(.*)\\>" + //capturing group 1
/* 101015 fb: Removed the non-capturing group
 * Reason: Changing the name of chromosomes from the input has only a small benefit:
 *         Users get more concise chromosome names from badly formatted input files.
 *         The disadvantage is that importing locus data changes chrome names while
 *         importing mapped reads (for example) does not. The end result is that
 *         users wonder why their mapped reads are not mapped to the chromosomes
 *         from their annotation files. I.e. the reads are NOT CONSIDERED to be 
 *         mapped to their target chromosomes. 
 *   	        
 */
//	        "(?>.*chromosome|.*chr)?" + //non-capturing
	        "(.+)"+ //capturing group 2
	        ":" +
	        "\\s*(\\d+)\\s*" + //capturing group 3
	        "-" +
	        "\\s*(\\d+)" + //capturing group 4
	        "\\s*(?::?\\s*\\z|:\\s*(?:(\\+\\-|\\-\\+)|([#\\+\\-])).*)" //capturing group 5[+,- oder +,-], 6[+,-,#]
	        ,Pattern.CASE_INSENSITIVE);
	
	protected Chromosome chromosome;
	protected GBNode model;

	private void setup(Chromosome c, GBNode model) {
		this.chromosome=c;
		this.model=model;
		if (chromosome!=null)
			chromosome.updateLength(getTo());
	}
	
	public GeneticCoordinate(Chromosome c, GBNode model) {
		setup(c, model);
	}

	public GeneticCoordinate(Chromosome c, String genbankstring) {
		this(c, GBParser.parse(genbankstring));
	}
	
	public GeneticCoordinate(Chromosome c, GBAtom... atoms) {
		this(c,  GBParser.convert(atoms));
	}

	public GeneticCoordinate(Chromosome c, Collection<GBAtom> atoms) {
		this(c,  GBParser.convert(atoms));
	}
	
	public GeneticCoordinate(Chromosome chromosome, Strand strand, long from, long to) {
		this(chromosome, new GBAtom(from, to, strand));
	}
	
	public GeneticCoordinate(Chromosome chromosome, String aStrand, long from, long to)	{
		this(chromosome, Strand.fromString(aStrand), from, to);
	}
	
	public GeneticCoordinate(AbstractGeneticCoordinate other) {
		this(other.getChromosome(), other.getModel());
	}
	
	public GeneticCoordinate(String serializedForm, ChromosomeSetContainer container) {
		boolean complex = serializedForm.endsWith("!");
		if (complex)
			serializedForm = serializedForm.substring(0, serializedForm.length()-1);
		complex |= serializedForm.endsWith(")");
		
		if (complex) {
			// first parse out species and chrome, then parse the model		
			Matcher m = PATTERN_COMPLEX.matcher(serializedForm);
			m.find();
			setup(container.getChromosome(
					SpeciesContainer.getSpecies(m.group(1).trim()), 
					m.group(2).trim()),
				  GBParser.parse(m.group(3)));			
		} else {
			Matcher m = PATTERN_PRIMITIVE.matcher(serializedForm);
			m.find();
			
			Strand cstrand=Strand.UNSPECIFIED;
			if(m.group(5)!=null)
				cstrand = Strand.BOTH;
			else if(m.group(6)!=null)
				cstrand = Strand.fromChar(m.group(6).charAt(0));      
			
			Chromosome chromosome = container.getChromosome(
							SpeciesContainer.getSpecies(m.group(1).trim()), 
							m.group(2).trim());
			long from = Integer.parseInt(m.group(3).trim());
			long to = Integer.parseInt(m.group(4).trim());
			setup(chromosome, GBParser.convert(new GBAtom(from, to, cstrand)));
		}
		

	}

	/** the length of a complex coordinate is the sum of lengths of it's composing parts.
	 * Thus, getFrom()+length() IS NOWHERE NEAR getTo() ! **/
	public long length() {
		return model.getCoveredBases();
	}

	public long getFrom() {
		return model.getStart(); 
	}

	public long getTo() {
		return model.getEnd();
	}

	public Strand getStrand() {
		return model.getStrand();
	}

	public Chromosome getChromosome() {
		return this.chromosome;
	}

	@Override
	public List<GBAtom> getCoordinateAtoms() {
		return model.getCoordinateAtoms();
	}

	@Override
	public GBNode getModel() {
		return model;
	}

	@Override
	public long getCoveredBases() {
		return getModel().getCoveredBases();
	}


}
