package mayday.genetics.advanced.chromosome;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import mayday.core.structures.CompactableStructure;
import mayday.core.structures.natives.LinkedDoubleArray;
import mayday.core.structures.natives.LinkedLongArray;
import mayday.core.structures.natives.QuickSorter;
import mayday.core.structures.natives.mmap.MMBooleanArray;
import mayday.core.structures.natives.mmap.MMLongArray;
import mayday.core.structures.overlap.OverlapArrayLong;
import mayday.genetics.advanced.ChromosomeArrayLong;
import mayday.genetics.advanced.MMStrandArray;
import mayday.genetics.basic.Species;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.SimpleChromosome;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.genetics.basic.coordinate.GeneticCoordinate;
import mayday.genetics.coordinatemodel.GBAtom;
import mayday.genetics.coordinatemodel.GBNode;

/**
 * Contains a chromosome on which loci are located. Each locus can be connected to an object
 * @author battke
 * In addition to the Species, ID and length, 
 * 
 * This subclass stores complex loci alongside "normal" loci. 
 * Complex loci are split into and stored as normal loci and reassembled on access.
 * 
 * Most functions (for instance all iterators over _positions_) work directly on the primitive loci.
 * Some functions (for instance all iterators over _loci_ ordered by something) work on complex loci.
 * getOverlappingLoci returns ALL overlapping loci, even if none of their primitive parts overlaps. 
 *   The return values are complex loci.
 *   
 * For this class, a distinction is made between COVERing and SPANning. See this explanation:
 * ----------XXXXXX--------------- genome, region of interest indicated by Xs
 *    AAAAAAAAAA-----BBBB          genetic coordinate COVERING a part of the region
 *    AAAA------------BBBB         genetic coordinate SPANNING a part of the region
 */
@SuppressWarnings("unchecked")
public abstract class AbstractLocusChromosome<CoordinateType extends AbstractGeneticCoordinate> 
extends SimpleChromosome implements CompactableStructure { //, DumpableStructure {

	/* stores true for each starting element, false for each continuation element in a complex coordinate
	 * "normal" coordinates have one element, while "complex" coordinates can have any number of elements.
	 * Per default, complex coordinates are split into many "normal" coordinates.
	 * Special functions can be used to reconstruct the full "complex" coordinate
	 * @see isComplexCoordinate(long i);
	 * @see getComplexCoordinate(long i);
	 */	
	protected MMBooleanArray isStart;
	protected long size=0;
	
	protected ChromosomeArrayLong mappedIDs; // contains each primitive coordinate
	
	// for multi-element (complex) coordinates this maps base position to all coordinates spanning this position
	// contents are the identifiers of the FIRST component of each complex coordinate
	// this will be lazily created, if necessary
	protected OverlapArrayLong spanningIDs;  
	
	protected MMStrandArray strands;
	 
	protected AbstractLocusChromosome(Species organism, String id, long length) {
		super(organism, id, length);
		mappedIDs = new ChromosomeArrayLong();
		strands = new MMStrandArray(ChromosomeArrayLong.BLOCKSIZE);
		isStart = new MMBooleanArray(ChromosomeArrayLong.BLOCKSIZE);
//		System.out.println("ACL strand: "+strands.content.id);
//		System.out.println("ACL isstart:"+isStart.underling.id);
	}

	protected long addLocus(long startposition, long endposition, Strand strand) {
		long key = mappedIDs.put(startposition, endposition);
		if (spanningIDs!=null)
			spanningIDs.put(startposition, endposition, key);
		++size;
		strands.add(strand);
		isStart.set(key, true);
		updateLength(endposition);
		return key;
	}	
	
	protected long addLocus(GBNode model) {
		List<GBAtom> atoms = model.getCoordinateAtoms();
		// add first element
		GBAtom gba = atoms.get(0);
		long key = mappedIDs.put(gba.from, gba.to);
		++size;
		strands.add(gba.strand);
		isStart.set(key, true);
		
		for (int i=1; i<atoms.size(); ++i) {
			gba = atoms.get(i);
			mappedIDs.put(gba.from, gba.to);
			strands.add(gba.strand);
		}
		
		if (spanningIDs!=null)
			spanningIDs.put(model.getStart(), model.getEnd(), key);
			
		updateLength(model.getEnd());
		return key;
	}

	/** add a copy of an existing Coordinate in a given position). The model parameter overrides
	 * what is found in the coordinate. Should the coordinate contain further data, this is added in the new position 
	 */
	public abstract long addLocus(GBNode model, CoordinateType coord);
	public abstract long addLocus(long startposition, long endposition, Strand strand, CoordinateType coord);
	
	/** @return true if the coordinate is part of a complex coordinate set */ 
	protected boolean isComplexCoordinate(long i) {
		return (i+1<size && !isStart.get(i+1)); 
	}
		
	/** @return the number of parts in the complex coordinate that "i" is a part of */
	private int getComplexCoordinateNumberOfParts(long i) {
		int count=1;		
		while (i+count < size && !isStart.get(i+count))
			++count;
		return count;
	}
	
	/** @return the first coordinate part in the complex coordinate that "i" is part of */
	protected long getComplexCoordinateFirstPart(long i) {
		while (!isStart.get(i) && i>0) 
			--i;
		return i;
	}
	
	protected long getStart(long i) {
		return mappedIDs.getStart(i);
	}

	protected long getEnd(long i) {
		return mappedIDs.getEnd(i);
	}
	
	protected long getLength(long i){
		return mappedIDs.getEnd(i)-mappedIDs.getStart(i) + 1;
	}
	
	protected Strand getStrand(long i) {
		return strands.get(i);
	}
	
	public CoordinateType getCoordinate(long i) {
		return makeCoordinate(i);
	}
	
	public long getNumberOfLoci() {
		return size;
	}
	
	protected void lazyInitSpanning() {
 		if (spanningIDs==null && size!=strands.size()) { // only in this case we need it
			spanningIDs = new OverlapArrayLong(3);
			Iterator<CoordinateType> ctit = iterateUnsorted();
			while (ctit.hasNext()) {
				CoordinateType ct = ctit.next();
				spanningIDs.put(ct.getFrom(), ct.getTo(), ((AbstractLocusGeneticCoordinate)ct).myId);
			}
		}
	}
	
	public Iterator<CoordinateType> iterateUnsorted() {		
		return new Iterator<CoordinateType>() {
			protected long next=0;

			public boolean hasNext() {
				return next < mappedIDs.size();
			}

			public CoordinateType next() {
				int skip = getComplexCoordinateNumberOfParts(next);
				CoordinateType result = makeCoordinate(next);
				next+=skip;
				return result;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}		
		};
	}
	
	public Iterator<Long> iterateStartPositions() {
		final MMLongArray sortedIDs = new MMLongArray(mappedIDs.getStarts());
		sortedIDs.sort();		
		return sortedIDs.iterator();
	}

	public Iterator<Long> iterateEndPositions() {
		final MMLongArray sortedIDs = new MMLongArray(mappedIDs.getEnds());
		sortedIDs.sort();		
		return sortedIDs.iterator();
	}

	public Iterator<Long> iterateAllPositions() {
		final Iterator<Long> istart = iterateStartPositions();
		final Iterator<Long> iend = iterateEndPositions();		
		return new Iterator<Long>() {

			protected Long nstart = istart.hasNext()?istart.next():null;
			protected Long nend = iend.hasNext()?iend.next():null;

			public boolean hasNext() {
				return nstart!=null || nend!=null;
			}

			public Long next() {
				Long tmp;
				if (nend==null || (nstart!=null && nstart<nend)) {
					tmp = nstart;
					nstart = istart.hasNext() ? istart.next() : null;						
				} else { // nend!=null && nstart==null || nstart>nend
					tmp = nend;
					nend = iend.hasNext() ? iend.next() : null;
				}
				return tmp;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}
	
	public Iterator<Long> iterateAllCoveredPositions() {
		return mappedIDs.coveredPositionsIterator(0);
	}

	/** Iterate over all (complex) loci ordered by their size (distance from first base to last base
	 * This sorting does NOT CONSIDER the bases actually covered.
	 */
	public Iterator<CoordinateType> iterateByLocusSize(boolean increasing) {
		
		final LinkedLongArray sortedIDs = new LinkedLongArray(1000);
		for (long l=0; l!=mappedIDs.size(); ++l)
			sortedIDs.add(l);
		
		new QuickSorter<LinkedLongArray, Long>(sortedIDs) {
			@Override
			protected int compareElements(Long i1, Long i2) {
				if (!isStart.get(i1))
					return 1;
				if (!isStart.get(i2))
					return -1;
				Long l1 = makeCoordinate(i1).length(); // in complex coords, end is in another particle than start
				Long l2 = makeCoordinate(i2).length(); // in complex coords, end is in another particle than start
				return l1.compareTo(l2);
			}

			@Override
			protected long size(LinkedLongArray x) {
				return x.size();
			}

			@Override
			protected void swapElements(LinkedLongArray x, long i1, long i2) {
				long t = x.get(i1);
				x.set(i1, x.get(i2));
				x.set(i2, t);
			}

			@Override
			protected Long getElement(LinkedLongArray x, long index) {
				return x.get(index);
			}

		}.sort();
		
		return convertToIterator(sortedIDs, increasing);
	}
	
	/** iterate over the set sorted by a) start position and b) increasing length */
	public Iterator<CoordinateType> iterateByStartPosition(boolean increasing) {
		final LinkedLongArray sortedIDs = new LinkedLongArray(1000);
		for (long l=0; l!=mappedIDs.size(); ++l)
			sortedIDs.add(l);
		
		new QuickSorter<LinkedLongArray, Long>(sortedIDs) {
			@Override
			protected long size(LinkedLongArray x) {
				return x.size();
			}

			@Override
			protected void swapElements(LinkedLongArray x, long i1, long i2) {
				long t = x.get(i1);
				x.set(i1, x.get(i2));
				x.set(i2, t);
			}

			@Override
			protected Long getElement(LinkedLongArray x, long index) {
				return x.get(index);
			}

			@Override
			protected int compareElements(Long i1, Long i2) {
				if (!isStart.get(i1))
					return 1;
				if (!isStart.get(i2))
					return -1;
				Long l1 = mappedIDs.getStart(i1);
				Long l2 = mappedIDs.getStart(i2);
				int c = l1.compareTo(l2); 
				if (c==0) {
					l1 = makeCoordinate(i1).length(); // in complex coords, end is in another particle than start
					l2 = makeCoordinate(i2).length(); // in complex coords, end is in another particle than start
					c = l1.compareTo(l2);
				}
				return c;
			}

		}.sort();
		
		return convertToIterator(sortedIDs, increasing);
	}
	
	/** iterate over the set sorted by a) end position and b) increasing length */
	public Iterator<CoordinateType> iterateByEndPosition(boolean increasing) {
		final LinkedLongArray sortedIDs = new LinkedLongArray(1000);
		for (long l=0; l!=mappedIDs.size(); ++l)
			sortedIDs.add(l);
		
		new QuickSorter<LinkedLongArray, Long>(sortedIDs) {
			
			protected long size(LinkedLongArray x) {
				return x.size();
			}

			@Override
			protected void swapElements(LinkedLongArray x, long i1, long i2) {
				long t = x.get(i1);
				x.set(i1, x.get(i2));
				x.set(i2, t);
			}

			@Override
			protected Long getElement(LinkedLongArray x, long index) {
				return x.get(index);
			}
			
			protected int compareElements(Long i1, Long i2) {
				if (!isStart.get(i1))
					return 1;
				if (!isStart.get(i2))
					return -1;
				CoordinateType ct1 = makeCoordinate(i1);
				CoordinateType ct2 = makeCoordinate(i2);
				Long l1 = ct1.getTo();
				Long l2 = ct2.getTo();
				int c = l1.compareTo(l2);
				if (c==0) {
					l1 = ct1.length();
					l2 = ct2.length();
					c = l1.compareTo(l2);
				}
				return c;
			}

		}.sort();
		
		return convertToIterator(sortedIDs, increasing);
	}

	
	/** @return a list of elements that span the given locus, i.e. that at least one of the bases indicated is within coordinate.start and coordinate.end**/  
	public List<CoordinateType> getSpanningLoci(long startposition, long endposition, Strand strand) {
		lazyInitSpanning();
		if (spanningIDs==null) // if this is still null after lazy init, then only primitive loci exist in this container
			return getOverlappingLoci(startposition, endposition, strand);
		Collection<Long> l = spanningIDs.get(startposition, endposition, null);
		return filterLoci(l, strand);
	}

	/** @return a list of elements that span the given position, i.e. that the base indicated is within coordinate.start and coordinate.end **/
	public List<CoordinateType> getSpanningLoci(long position, Strand strand) {
		return getSpanningLoci(position, position, strand);
	}

	/** @return true if at least one base of the indicated locus is SPANNED by an element **/
	public boolean isSpanned(long start, long end, Strand strand) {
		return getSpanningLoci(start,end,strand).size()>0;
	}
	
	/** @return a list of elements that overlap the given locus, i.e. that COVER at least one of the bases indicated,
	 * as well as all elements they in turn overlap **/
	public List<CoordinateType> getSpanningLociCluster(long startposition, long endposition, Strand strand) {
		lazyInitSpanning();
		if (spanningIDs==null) // if this is still null after lazy init, then only primitive loci exist in this container
			return getOverlappingLociCluster(startposition, endposition, strand);

		// find leftmost cluster coordinate
		long from = startposition;
		long lastfrom = from;
		do {
			lastfrom=from;
			Collection<Long> l = spanningIDs.get(from, null);
			for (long ll : l)
				if (strand==Strand.UNSPECIFIED || strand==Strand.BOTH || strand.similar(strands.get(ll)))
					from = Math.min(getStart(ll),from);
		} while (lastfrom!=from);
		// find rightmost cluster coordinate
		long to = endposition;
		long lastto = to;
		do {
			lastto=to;
			Collection<Long> l = spanningIDs.get(to, null);
			for (long ll : l)
				if (strand==Strand.UNSPECIFIED || strand==Strand.BOTH || strand.similar(strands.get(ll)))
					to = Math.max(getEnd(ll),to);
		} while (lastto!=to);
		// return everything
		return getOverlappingLoci(from, to, strand);
	}
	
	/** @return a list of elements that overlap the given coordinate, i.e. that COVER at least one of the bases indicated **/  
	public List<CoordinateType> getOverlappingLoci(GBNode model) {		
		List<GBAtom> atoms = model.getCoordinateAtoms();
		if (atoms.size()>1) {
			Set<Long> total = new TreeSet<Long>();
			for (GBAtom gba : atoms) {
				Collection<Long> s = mappedIDs.getIDs(gba.from, gba.to);
				filterInPlace(s, gba.strand);
				total.addAll(s);
			}
			return createLociList(total);
		} else {
			GBAtom gba = atoms.get(0);
			return getOverlappingLoci(gba.from, gba.to, gba.strand);
		}
	}
	
	/** @return a list of elements that overlap the given locus, i.e. that COVER at least one of the bases indicated **/  
	public List<CoordinateType> getOverlappingLoci(long startposition, long endposition, Strand strand) {
		Collection<Long> l = mappedIDs.getIDs(startposition, endposition);
		return filterLoci(l, strand);
	}

	/** @return a list of elements that overlap the given position, i.e. that COVER the base indicated **/
	public List<CoordinateType> getOverlappingLoci(long position, Strand strand) {
		return getOverlappingLoci(position, position, strand);
	}

	/** @return true if at least one base of the indicated locus is COVERED by an element **/
	public boolean isOverlapped(long start, long end, Strand strand) {
		return getOverlappingLoci(start,end,strand).size()>0;
	}
	
	protected final Comparator<Long> startComp = new Comparator<Long>() {
		public int compare(Long o1, Long o2) {
			return Long.valueOf(getStart(o1)).compareTo(getStart(o2));
		}
	};
	
	/** @return a list of elements that overlap the given locus, i.e. that COVER at least one of the bases indicated,
	 * as well as all elements they in turn overlap **/
	public List<CoordinateType> getOverlappingLociCluster(long startposition, long endposition, Strand strand) {
		// find leftmost cluster coordinate
		long from = startposition;
		long lastfrom = from;
		do {
			lastfrom=from;
			Collection<Long> l = mappedIDs.getIDs(from);
			for (long ll : l)
				if (strand==Strand.UNSPECIFIED || strand==Strand.BOTH || strand.similar(strands.get(ll)))
					from = Math.min(getStart(ll),from);
		} while (lastfrom!=from);
		// find rightmost cluster coordinate
		long to = endposition;
		long lastto = to;
		do {
			lastto=to;
			Collection<Long> l = mappedIDs.getIDs(to);
			for (long ll : l)
				if (strand==Strand.UNSPECIFIED || strand==Strand.BOTH || strand.similar(strands.get(ll)))
					to = Math.max(getEnd(ll),to);
		} while (lastto!=to);
		// return everything
		return getOverlappingLoci(from, to, strand);
	}
	
	
	/** @return true if at EACH base of the indicated locus is COVERED by an element **/
	public boolean isCompletelyOverlapped(long start, long end, Strand strand) {
		List<Long> l = new ArrayList<Long>(mappedIDs.getIDs(start, end));
		if (l.size()==0)
			return false;
		
		Collections.sort(l, startComp);
				
		for (long ll : l) {
			if (strand.similar(strands.get(ll))) {
				long s = mappedIDs.getStart(ll);
				long e = mappedIDs.getEnd(ll);
				if (s>start) // definitely not completely covered
					return false;
				// start is covered, move start as far as possible
				start = e+1; // next un-covered position
				if (start>end)
					return true;
			}
		}
		return start>end;
	}
	
	public GeneticCoordinate trimByOverlapping(AbstractGeneticCoordinate t) {
		long curStart = t.getFrom();
		long curEnd = t.getTo();
		Strand strand = t.getStrand();
		long i;
		for (i = curStart; i!=curEnd; ++i) {
			if (!isOverlapped(i, i, strand)) 
				break;
		}
		curStart = Math.max(curStart, i-1);
		for (i = curEnd; i!=curStart; --i) {
			if (!isOverlapped(i, i, strand)) 
				break;
		}
		curEnd = Math.min(curEnd, i+1);		
		return new GeneticCoordinate(t.getChromosome(), t.getStrand(), curStart, curEnd);
	}

	
	
	protected List<CoordinateType> filterLoci(Collection<Long> l, Strand strand) {
		if (l.size()==0)
			return Collections.emptyList();

		HashSet<Long> hl = new HashSet<Long>();
		LinkedList<CoordinateType> ret = new LinkedList<CoordinateType>();

		for (long ll : l) 
			if (strand==Strand.UNSPECIFIED || strand==Strand.BOTH || strand.similar(strands.get(ll))) {
				ll = getComplexCoordinateFirstPart(ll);
				if (hl.add(ll))
					ret.add(makeCoordinate(ll));
			}
		
		return ret;
	}
	
	protected void filterInPlace(Collection<Long> l, Strand strand) {
		
		if (strand==Strand.UNSPECIFIED || strand==Strand.BOTH)
			return;
		
		Iterator<Long> il = l.iterator();
		while (il.hasNext()) {
			long ll = getComplexCoordinateFirstPart(il.next());
			if (!strand.similar(strands.get(ll)))
				il.remove();
		}
	}
	
	protected List<CoordinateType> createLociList(Collection<Long> l) {
		if (l.size()==0)
			return Collections.emptyList();

		HashSet<Long> hl = new HashSet<Long>();
		LinkedList<CoordinateType> ret = new LinkedList<CoordinateType>();
		for (long ll : l) {
			ll = getComplexCoordinateFirstPart(ll);
			if (hl.add(ll))
				ret.add(makeCoordinate(ll));
		}
		
		return ret;
	}
	
	/**
	 * computes the coverage for each base position in the specified interval
	 * @param from the start of the interval to compute (included)
	 * @param to the end of the interval to compute (included)
	 * @return an array containing two LinkedDoubleArray instances, one for forward and one for reverse strand
	 */
	public LinkedDoubleArray[] computeCoverage(long from, long to) {
		LinkedDoubleArray[] result = new LinkedDoubleArray[2];
		LinkedDoubleArray fwd = result[0] = new LinkedDoubleArray(10000);
		LinkedDoubleArray bwd = result[1] = new LinkedDoubleArray(10000);
		fwd.ensureSize(to-from+1);
		bwd.ensureSize(to-from+1);
		Iterator<Long> cover = mappedIDs.coveredPositionsIterator(from);
		while (cover.hasNext()) {
			long np = cover.next();
			if (np>to)
				break;
			int fsum=0, bsum=0;
			Collection<Long> l = mappedIDs.getIDs(np, np);
			for (long ll : l) {
				Strand s = strands.get(ll);
				switch(s) {
				case UNSPECIFIED: // fall through
				case BOTH:
					++bsum;		  // fall through
				case PLUS:
					++fsum;
					break;
				case MINUS:
					++bsum;
					break;
				}
			}
			fwd.set(np-from, fsum);
			bwd.set(np-from, bsum);
		}			
		return result;
	}
	
	/** Compute the coverage for a short stretch of genome 
	 * @param from, to genomic positions to compute coverage in
	 * @param result, an array double[2][n] to hold the per-strand coverage [0]=forward, [1]=backward
	 * @return the filled coverage array. if coverage[][] was null or too small, a new array will be returned.*/
	public double[][] computeCoverageShort(long from, long to, double[][] result) {
		long length = to-from+1;
		if (length>Integer.MAX_VALUE)
			throw new IllegalArgumentException("computeCoverageShort can not work on stretches larger than "+Integer.MAX_VALUE+" bases.");
		
		if (result==null || result.length!=2 || result[0].length<length || result[1].length<length) {
			result = new double[2][];
			result[0] = new double[(int)(length)];
			result[1] = new double[(int)(length)];
		}
		
		double[] fwd = result[0];
		double[] bwd = result[1];
		Iterator<Long> cover = mappedIDs.coveredPositionsIterator(from);
		while (cover.hasNext()) {
			long np = cover.next();
			if (np>to)
				break;
			int fsum=0, bsum=0;
			Collection<Long> l = mappedIDs.getIDs(np, np);
			for (long ll : l) {
				Strand s = strands.get(ll);
				switch(s) {
				case UNSPECIFIED: // fall through
				case BOTH:
					++bsum;		  // fall through
				case PLUS:
					++fsum;
					break;
				case MINUS:
					++bsum;
					break;
				}
			}
			fwd[(int)(np-from)]=fsum;
			bwd[(int)(np-from)]=bsum;
		}			
		return result;
	}
	
	public double[] computeCoverage(long from, long to, boolean mean) {
		double fsum=0, bsum=0;
		Collection<Long> l = mappedIDs.getIDs(from, to);
		for (long ll : l) {
			Strand s = strands.get(ll);
			switch(s) {
			case UNSPECIFIED: // fall through
			case BOTH:
				++bsum;		  // fall through
			case PLUS:
				++fsum;
				break;
			case MINUS:
				++bsum;
				break;
			}
		}
		if (mean) {
			double bc = to-from+1;
			bsum/=bc;
			fsum/=bc;
		}
		return new double[]{fsum, bsum}; 
	}	
	
	/** reduce the size of this chromosome by choosing a representation of coordinates with fewer bytes if possible */
	public void compact() {
		isStart.compact();
		mappedIDs.compact(); 
	}
	
//	@Override
//	public void readDump(DataInputStream dis) throws IOException {
//		size = dis.readLong();
//		mappedIDs.readDump(dis);
//		isStart.readDump(dis);
//		strands.readDump(dis);		
//	}
//
//	@Override
//	public void writeDump(DataOutputStream dos) throws IOException {
//		dos.writeLong(size); 
//		mappedIDs.writeDump(dos);
//		isStart.writeDump(dos);
//		strands.writeDump(dos);		
//	}
	
	@Override
	public String getCompactionInitializer() {
		return mappedIDs.getCompactionInitializer()+"\t"+isStart.getCompactionInitializer();
	}

	@Override
	public void setCompaction(LinkedList<String> compactionInitializer) {
		mappedIDs.setCompaction(compactionInitializer);
		isStart.setCompaction(compactionInitializer);
	}
	
	
	
	protected LongIteratorToComplexCoordinateTypeIteratorAdapter convertToIterator(LinkedLongArray sorted, boolean increasing) {
		if (increasing)
			return new LongIteratorToComplexCoordinateTypeIteratorAdapter(sorted.iterator());
		else
			return new LongIteratorToComplexCoordinateTypeIteratorAdapter(sorted.reverseIterator());
	}

	protected class LongIteratorToComplexCoordinateTypeIteratorAdapter implements Iterator<CoordinateType> {

		protected Iterator<Long> input;
		protected long next=-1;

		public LongIteratorToComplexCoordinateTypeIteratorAdapter(Iterator<Long> input) {
			this.input = input;
			next();
		}

		public boolean hasNext() {
			return next>=0;
		}

		public CoordinateType next() {
			CoordinateType retval=null;
			if (next>0)
				retval = makeCoordinate(next);
			if (!input.hasNext())
				next = -1;
			else {
				next = input.next();
				if (!isStart.get(next))
					next = -1; // done
			}
			return retval;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}			
	};

	protected abstract CoordinateType makeCoordinate(long index);

	
}
