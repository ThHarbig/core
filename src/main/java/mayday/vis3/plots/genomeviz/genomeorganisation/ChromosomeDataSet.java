package mayday.vis3.plots.genomeviz.genomeorganisation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.structures.maps.MultiTreeMap;
import mayday.core.structures.natives.LinkedLongArray;
import mayday.core.structures.natives.LinkedObjectArray;
import mayday.genetics.LocusMIO;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinateObject;
import mayday.genetics.basic.Species;
import mayday.genetics.basic.Strand;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.KindOfChromeView;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.KindOfData;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.ZoomLevel;
import mayday.vis3.plots.genomeviz.ILogixVizModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.additional.MasterManager;
import mayday.vis3.plots.genomeviz.genomeheatmap.delegates.CheckTranslatedKeys_Delegate;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.ForwardBackwardProbes;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.SelectedRange;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.TranslatedKey;
import mayday.vis3.plots.genomeviz.genomeoverview.FromToPosition;

public class ChromosomeDataSet{

	protected Chrome actualChrome;

	public ArrayList<ForwardBackwardProbes> condensed_THOUSAND = null;
	public ArrayList<ForwardBackwardProbes> condensed_TWOTHOUSAND = null;
	public ArrayList<ForwardBackwardProbes> condensed_FIFETHOUSAND = null;
	public ArrayList<List<Probe>> fitted_actualCondensedProbes = null;

	public TreeMap<Long,ForwardBackwardProbes> whole_THOUSAND = null;
	public TreeMap<Long,ForwardBackwardProbes> whole_TWOTHOUSAND = null;
	public TreeMap<Long,ForwardBackwardProbes> whole_FIFETHOUSAND = null;
	protected MultiTreeMap<Long,Probe> fitted_actualWholeProbes = null;

	protected MIGroup miGroup;						// contains stored locus information

	protected KindOfData kindOfData = null; // stores kind of data, so if standard, range selection by probes or range sel. by positions

	protected FromToPosition ftp;
	protected boolean initialized = false;
	protected ChromosomeSettings chromosomeSettings;

	// Subranges
	protected SelectedRange range = null;
	protected long condensed_start=-1, condensed_end=-1;
	protected Set<Probe> plusProbes=Collections.emptySet();
	protected Set<Probe> minusProbes=Collections.emptySet(); 


	/**
	 * standard constructor, here the actualChromeData is filled with all probes from selected species and chromosome.
	 * @param miGroup
	 * @param species
	 * @param chrome
	 */
	public ChromosomeDataSet(MIGroup MiGroup,Chrome Chrome){
		miGroup = MiGroup;
		actualChrome = Chrome;
		kindOfData = KindOfData.STANDARD;
		initialized = true;		
	}


	/**
	 * constructor for range selection, here the actualChromeData is filled with probes between a specified range.
	 * @param miGroup
	 * @param species
	 * @param chrome
	 * @param range
	 */
	public ChromosomeDataSet(MIGroup MiGroup, Chrome chrome, SelectedRange Range){
		this(MiGroup, chrome);
		kindOfData = KindOfData.BY_POSITION;
		range = Range;

		long from = range.getFromPosition();
		long to = range.getToPosition();

		if(from != 0 && to != 0){
			initialized = true;
		}
	}
	
	
	public List<LocusGeneticCoordinateObject<Probe>> getProbes(long position){
		return actualChrome.get(position, Strand.UNSPECIFIED);
	}
	
	public List<LocusGeneticCoordinateObject<Probe>> getProbes(long position, Strand strand){
		return actualChrome.get(position, strand);
	}
	
	public List<LocusGeneticCoordinateObject<Probe>> getProbes(long start, long end, Strand strand){
		return actualChrome.get(start, end, strand);
	}

	private void initProbesetChromosome() {
		if(initialized && plusProbes==Collections.<Probe>emptySet()){
			plusProbes = new HashSet<Probe>();
			minusProbes = new HashSet<Probe>();
			LinkedObjectArray<Probe> allProbes = actualChrome.getAllProbes();
			long from = -1;
			long to = -1;
			if(range!=null){
				from = range.getFromPosition();
				to = range.getToPosition();
			} else{
				from = 0;
				to = actualChrome.getLength();
			}
				
			for (Probe pb : allProbes) {
				Strand pbs = getStrand(pb);
				if (range!=null) {
					if (! (getStartPosition(pb)<=to && getEndPosition(pb)>=from))
						pbs = null;
				}
				if (pbs!=null) {
					if (pbs.similar(Strand.PLUS))
						plusProbes.add(pb);
					if (pbs.similar(Strand.MINUS))
						minusProbes.add(pb);
				}
			}
		}

	}

	public Set<Probe> getForwardProbesetChromosome(){
		initProbesetChromosome();
		return plusProbes;
	}

	public Set<Probe> getBackwardProbesetChromosome(){
		initProbesetChromosome();
		return minusProbes;
	}
	
	public long getCondensedSize() {
		return getCondensed().size();
	}


	// (called by shift selection)
	/**
	 * returns all Probes contained in cellNumber, no matter if forward or backward probe and no
	 * matter which strand.
	 * @param cellnumber
	 * @return LinkedList of all probes both strands contained in cell/cellrange
	 */
	private List<Probe> getAllProbes(int cellnumber, TranslatedKey transKey, int originalNumberOfCell, KindOfChromeView view){

		if(transKey == null) return Collections.emptyList();

		long translatedKeyLow = transKey.getTranslatedKeyFirst();

		if(translatedKeyLow > originalNumberOfCell){
			return Collections.emptyList();
		}

		if (view == KindOfChromeView.WHOLE) {
			return getAllProbes_Whole(transKey);

		} else if (view == KindOfChromeView.CONDENSED) {
			return getAllProbes_Condensed(transKey);
		}
		return Collections.emptyList();
	}


	/**
	 * search for this key all forward probes.
	 * @param translatedKey_low
	 * @param translatedKey_high
	 * @return
	 */
	private List<Probe> getStrandProbes_Whole(int cellNumber, int originalNumberOfCells, MasterManager master, Strand strand) {

		TranslatedKey transKey = master.getTranslatedKey(cellNumber, originalNumberOfCells); 	
		if(transKey == null) return Collections.emptyList();

		long translatedKey_low = (long)transKey.getTranslatedKeyFirst();
		long translatedKey_high = (long)transKey.getTranslatedKeyLast();
		translatedKey_high = CheckTranslatedKeys_Delegate.execute((int)translatedKey_low,
				(int)translatedKey_high, originalNumberOfCells);

		List<Probe> probes = new LinkedList<Probe>();
		translatedKey_low = translatedKey_low + getChromeSettings().getSkipValue();
		translatedKey_high = translatedKey_high + getChromeSettings().getSkipValue();

		List<LocusGeneticCoordinateObject<Probe>> pbs;
		
		if(translatedKey_low == translatedKey_high)
			pbs = getProbes(translatedKey_low, strand);
		else if(translatedKey_low < translatedKey_high)
			pbs = getProbes(translatedKey_low, translatedKey_high-1, strand);
		else {
			System.err.println("ActualChromeData - getAllForwardProbes: Some error occurde with translatedKeys");
			pbs = Collections.emptyList();
		}
		
		for (LocusGeneticCoordinateObject<Probe> pb : pbs) {
			probes.add(pb.getObject());
		}
		return probes;
	}

	
	public LinkedLongArray getCondensed() {
		LinkedLongArray cov = actualChrome.getCoveredPositions();
		if (condensed_start==-1) {
			if (range!=null) {
				long from = range.getFromPosition();
				long to = range.getToPosition();
				for (long index = 0; index!= cov.size(); ++index) {
					long pos = cov.get(index);
					if (pos >= from && condensed_start==-1) 
						condensed_start = index;
					if (pos<= to)
						condensed_end = index;
					else
						break;
				}				
			} else {
				condensed_start=0;
				condensed_end = cov.size()-1;
			}
		}
		return cov;
	}
	

	/**
	 * returns for condensed chrome view all forward probes for range of translated cellnumbers.
	 * @param translatedCell_low
	 * @param translatedCell_high
	 * @param zoomLevel 
	 * @return LinkedList of all forward probes in condensed view
	 */
	private List<Probe> getStrandProbes_Condensed(int cellNumber, int originalNumberOfCells, MasterManager master, Strand strand){
		TranslatedKey transKey = master.getTranslatedKey(cellNumber, originalNumberOfCells); 	
		if(transKey == null) return Collections.emptyList();

		long translatedKey_low = transKey.getTranslatedKeyFirst();
		long translatedKey_high = transKey.getTranslatedKeyLast();
		translatedKey_high = CheckTranslatedKeys_Delegate.execute(translatedKey_low,
				translatedKey_high, originalNumberOfCells);

		// get always cellNumber -1 because index of array is beginning at position 0
		
		LinkedLongArray condensed = getCondensed();		
		
		translatedKey_low+=condensed_start;
		translatedKey_high+=condensed_start;
		
		if (translatedKey_low>condensed_end || translatedKey_high>condensed_end)
			return Collections.emptyList();
		
		List<LocusGeneticCoordinateObject<Probe>> pbs;
		
		if(translatedKey_low == translatedKey_high)
			pbs = getProbes(condensed.get(translatedKey_low-1), strand);
		else if(translatedKey_low < translatedKey_high)
			pbs = getProbes(condensed.get(translatedKey_low-1), condensed.get(translatedKey_high-2), strand);
		else {
			System.err.println("ActualChromeData-getAllForwardProbes_Condensed: Some error occured with translated keys");
			pbs = Collections.emptyList();
		}
		
		List<Probe> probes = new LinkedList<Probe>();

		for (LocusGeneticCoordinateObject<Probe> pb : pbs) {
			probes.add(pb.getObject());
		}
		return probes;
		
	}


	

	//########################################################################
	//----------------- Beginning for getting Whole ToolTipText --------------
	//########################################################################

	public List<Probe> getAllProbes_Whole(TranslatedKey transKey){
		long translatedKey_low = transKey.getTranslatedKeyFirst();
		long translatedKey_high = transKey.getTranslatedKeyLast();
		translatedKey_low = translatedKey_low + getChromeSettings().getSkipValue();
		translatedKey_high = translatedKey_high + getChromeSettings().getSkipValue();

		List<LocusGeneticCoordinateObject<Probe>> pbs;
		List<Probe> probes = new ArrayList<Probe>();
		
		if(translatedKey_low == translatedKey_high)
			pbs = getProbes(translatedKey_low, Strand.UNSPECIFIED);
		else if(translatedKey_low < translatedKey_high)
			pbs = getProbes(translatedKey_low, translatedKey_high-1, Strand.UNSPECIFIED);
		else {
			System.err.println("ActualChromeData - getBothProbes_Whole: some error with translated keys");
			pbs = Collections.emptyList();
		}
		
		for (LocusGeneticCoordinateObject<Probe> pb : pbs) {
			probes.add(pb.getObject());
		}
		return probes;
	}

	/**
	 * returns for translated keys all contained probes in cells for condensed view
	 * no matter which strand.
	 * @param translatedKey_low
	 * @param translatedKey_high
	 * @return LinkedList of all contained Probes
	 */
	public List<Probe> getAllProbes_Condensed(TranslatedKey transKey){

		long translatedKey_low = transKey.getTranslatedKeyFirst();
		long translatedKey_high = transKey.getTranslatedKeyLast();

		LinkedLongArray condensed = getCondensed();		
		
		translatedKey_low+=condensed_start;
		translatedKey_high+=condensed_start;
		
		if (translatedKey_low>condensed_end || translatedKey_high>condensed_end)
			return Collections.emptyList();
		
		List<LocusGeneticCoordinateObject<Probe>> pbs;
		
		if(translatedKey_low == translatedKey_high)
			pbs = getProbes(condensed.get(translatedKey_low-1), Strand.UNSPECIFIED);
		else if(translatedKey_low < translatedKey_high)
			pbs = getProbes(condensed.get(translatedKey_low-1), condensed.get(translatedKey_high-2), Strand.UNSPECIFIED);
		else {
			System.err.println("ActualChromeData - getBothProbes_Condensed: some error with translated keys");
			pbs = Collections.emptyList();
		}
		
		List<Probe> probes = new LinkedList<Probe>();

		for (LocusGeneticCoordinateObject<Probe> pb : pbs) {
			probes.add(pb.getObject());
		}
		return probes;
		
	}



	//########################################################################
	//           ----------------- END ----------------------------
	//########################################################################

	public MIGroup getMIGroup() {
		return miGroup;
	}

	public LocusMIO getLocusMIO(Probe probe) {
		return (LocusMIO) miGroup.getMIO(probe);
	}

	public Strand getStrand(Probe probe) {
		LocusMIO lm = ((LocusMIO) miGroup.getMIO(probe));
		if (lm==null)
			return Strand.UNSPECIFIED;
		return lm.getValue().getCoordinate().getStrand();
	}

	public long getStartPosition(Probe probe) {
		LocusMIO lm = ((LocusMIO) miGroup.getMIO(probe));
		if (lm==null)
			return 0;
		return lm.getValue().getCoordinate().getFrom();
	}

	public long getEndPosition(Probe probe) {
		LocusMIO lm = ((LocusMIO) miGroup.getMIO(probe));
		if (lm==null)
			return 0;
		return lm.getValue().getCoordinate().getTo();
	}

	public void clearFittedData() {
		fitted_actualWholeProbes = null;
		fitted_actualCondensedProbes = null;
	}

	private List<Probe> getAllBackwardProbes_Fitted_Condensed(
			int cellNumber, int originalNumberOfCells, MasterManager master) {
		if(fitted_actualCondensedProbes==null){
			return getStrandProbes_Condensed(
					cellNumber, originalNumberOfCells, master, Strand.MINUS);
		}

		List<Probe> minusProbes = new LinkedList<Probe>();

		for(Probe probe: fitted_actualCondensedProbes.get(cellNumber-1)){
			if(getStrand(probe) == Strand.MINUS && !minusProbes.contains(probe))minusProbes.add(probe);
		}
		if (!minusProbes.isEmpty())
			return minusProbes;

		return Collections.emptyList();
	}

	private List<Probe> getAllForwardProbes_Fitted_Condensed(int cellnumber, int originalNumberOfCells, MasterManager master) {

		if(fitted_actualCondensedProbes==null){
			return getStrandProbes_Condensed(cellnumber, originalNumberOfCells, master, Strand.PLUS);
		}

		List<Probe> plusProbes = new LinkedList<Probe>();
		for(Probe probe: fitted_actualCondensedProbes.get(cellnumber-1)){
			if(getStrand(probe) == Strand.PLUS && !plusProbes.contains(probe))plusProbes.add(probe);
		}

		if (!plusProbes.isEmpty())
			return plusProbes;

		return Collections.emptyList();
	}

	public List<Probe> getAllBackwardProbes_5000_Condensed(int cellNumber, int originalNumberOfCells, MasterManager master) {

		if(condensed_FIFETHOUSAND==null){
			return getStrandProbes_Condensed(
					cellNumber, originalNumberOfCells, master, Strand.MINUS);
		}

		List<Probe> minusProbes = condensed_FIFETHOUSAND.get(cellNumber-1).getBackwardProbes();

		if (!minusProbes.isEmpty())
			return minusProbes;

		return Collections.emptyList();
	}

	private List<Probe> getAllForwardProbes_5000_Condensed(int cellNumber, int originalNumberOfCells, MasterManager master) {

		if(condensed_FIFETHOUSAND==null){
			return getStrandProbes_Condensed(
					cellNumber, originalNumberOfCells, master, Strand.PLUS);
		}

		List<Probe> plusProbes = condensed_FIFETHOUSAND.get(cellNumber-1).getForwardProbes();

		if (!plusProbes.isEmpty())
			return plusProbes;

		return Collections.emptyList();
	}

	private List<Probe> getAllForwardProbes_1000_Condensed(int cellNumber, int originalNumberOfCells, MasterManager master ) {
		if(condensed_THOUSAND==null){
			return getStrandProbes_Condensed(
					cellNumber, originalNumberOfCells, master, Strand.PLUS);
		}

		List<Probe> plusProbes = condensed_THOUSAND.get(cellNumber-1).getForwardProbes();

		if (!plusProbes.isEmpty())
			return plusProbes;

		return Collections.emptyList();
	}

	private List<Probe> getAllBackwardProbes_1000_Condensed(int cellNumber, int originalNumberOfCells, MasterManager master) {
		if(condensed_THOUSAND==null){
			return getStrandProbes_Condensed(
					cellNumber, originalNumberOfCells, master, Strand.MINUS);
		}

		List<Probe> minusProbes = condensed_THOUSAND.get(cellNumber-1).getBackwardProbes();

		if (!minusProbes.isEmpty())
			return minusProbes;

		return Collections.emptyList();
	}

	private List<Probe> getAllForwardProbes_2000_Condensed(int cellNumber, int originalNumberOfCells, MasterManager master) {
		if(condensed_TWOTHOUSAND==null){
			return getStrandProbes_Condensed(
					cellNumber, originalNumberOfCells, master, Strand.PLUS);
		}

		List<Probe> plusProbes = condensed_TWOTHOUSAND.get(cellNumber-1).getForwardProbes();

		if (!plusProbes.isEmpty())
			return plusProbes;

		return Collections.emptyList();
	}

	private List<Probe> getAllBackwardProbes_2000_Condensed(int cellNumber, int originalNumberOfCells, MasterManager master) {
		if(condensed_TWOTHOUSAND==null){
			//			System.err.println("ActualData - getAllBackwardProbes_Fitted_Condensed: Condensed Probes not setted");
			return getStrandProbes_Condensed(
					cellNumber, originalNumberOfCells, master, Strand.MINUS);
		}

		List<Probe> minusProbes =  condensed_TWOTHOUSAND.get(cellNumber-1).getBackwardProbes();

		if (!minusProbes.isEmpty())
			return minusProbes;

		return Collections.emptyList();
	}

	public void setWhole(TreeMap<Long, ForwardBackwardProbes> fittedWholeProbes, ZoomLevel zoom){
		switch(zoom){
		case thousand:
			setWhole_1000(fittedWholeProbes);
			break;
		case twothousand:
			setWhole_2000(fittedWholeProbes);
			break;
		case fivethousand:
			setWhole_5000(fittedWholeProbes);
			break;
		}
	}

	public void setFittedWholeProbes(MultiTreeMap<Long, Probe> fitted) {
		this.fitted_actualWholeProbes = fitted;
	}
	private void setWhole_1000(
			TreeMap<Long, ForwardBackwardProbes> fittedWholeProbes) {
		this.whole_THOUSAND = fittedWholeProbes;
	}

	private void setWhole_2000(
			TreeMap<Long, ForwardBackwardProbes> fittedWholeProbes) {
		this.whole_TWOTHOUSAND = fittedWholeProbes;
	}

	private void setWhole_5000(
			TreeMap<Long, ForwardBackwardProbes> fittedWholeProbes) {
		this.whole_FIFETHOUSAND = fittedWholeProbes;
	}


	public List<Probe> getProbes(int cellnumber, int origCellnumber, Strand strand, 
			ZoomLevel zoom, MasterManager master, KindOfChromeView view){
		//	System.out.println("StrandInformation " + strand);
		if(strand == null){
			return Collections.emptyList();
		}
		switch (zoom){
		case thousand:
			switch(strand){
			case PLUS:
				switch(view){
				case WHOLE:
					return getAllForwardProbes_1000_Whole(cellnumber, origCellnumber, master);
				case CONDENSED:
					return getAllForwardProbes_1000_Condensed(cellnumber, origCellnumber, master);
				}
				break;
			case MINUS:
				switch(view){
				case WHOLE:
					return getAllBackwardProbes_1000_Whole(cellnumber, origCellnumber, master);
				case CONDENSED:
					return getAllBackwardProbes_1000_Condensed(cellnumber, origCellnumber, master);
				}
				break;
			case BOTH:
				TranslatedKey transKey = master.getTranslatedKey(cellnumber,origCellnumber);
				if(transKey == null) return Collections.emptyList();
				checkTranslatedKey(origCellnumber, transKey);
				getAllProbes(cellnumber, transKey, origCellnumber, view);
				break;
			default:
				return Collections.emptyList();
			}
			break;
		case twothousand:
			switch(strand){
			case PLUS:
				switch(view){
				case WHOLE:
					return getAllForwardProbes_2000_Whole(cellnumber, origCellnumber, master);
				case CONDENSED:
					return getAllForwardProbes_2000_Condensed(cellnumber, origCellnumber, master);
				}
				break;
			case MINUS:
				switch(view){
				case WHOLE:
					return getAllBackwardProbes_2000_Whole(cellnumber, origCellnumber, master);
				case CONDENSED:
					return getAllBackwardProbes_2000_Condensed(cellnumber, origCellnumber, master);
				}
				break;
			case BOTH:
				TranslatedKey transKey = master.getTranslatedKey(cellnumber,origCellnumber);
				if(transKey == null) return Collections.emptyList();
				checkTranslatedKey(origCellnumber, transKey);
				getAllProbes(cellnumber, transKey, origCellnumber, view);
				break;
			default:
				return Collections.emptyList();
			}
			break;
		case fivethousand:
			switch(strand){
			case PLUS:
				switch(view){
				case WHOLE:
					return getAllForwardProbes_5000_Whole(cellnumber, origCellnumber, master);
				case CONDENSED:
					return getAllForwardProbes_5000_Condensed(cellnumber, origCellnumber, master);
				}
				break;
			case MINUS:
				switch(view){
				case WHOLE:
					return getAllBackwardProbes_5000_Whole(cellnumber, origCellnumber, master);
				case CONDENSED:
					return getAllBackwardProbes_5000_Condensed(cellnumber, origCellnumber, master);
				}
				break;
			case BOTH:
				TranslatedKey transKey = master.getTranslatedKey(cellnumber,origCellnumber);
				if(transKey == null) return Collections.emptyList();
				checkTranslatedKey(origCellnumber, transKey);
				getAllProbes(cellnumber, transKey, origCellnumber, view);
				break;
			default:
				return Collections.emptyList();
			}
			break;

		case fit:
			switch(strand){
			case PLUS:
				switch(view){
				case WHOLE:
					return getAllForwardProbes_Fitted_Whole(cellnumber, origCellnumber, master);
				case CONDENSED:
					return getAllForwardProbes_Fitted_Condensed(cellnumber, origCellnumber, master);
				}
				break;
			case MINUS:
				switch(view){
				case WHOLE:
					return getAllBackwardProbes_Fitted_Whole(cellnumber, origCellnumber, master);
				case CONDENSED:
					return getAllBackwardProbes_Fitted_Condensed(cellnumber, origCellnumber, master);
				}
				break;
			case BOTH:
				TranslatedKey transKey = master.getTranslatedKey(cellnumber,origCellnumber);
				if(transKey == null) return Collections.emptyList();
				checkTranslatedKey(origCellnumber, transKey);
				getAllProbes(cellnumber, transKey, origCellnumber, view);
				break;
			default:
				return Collections.emptyList();
			}
			break;
		default:
			switch(strand){
			case PLUS:
				switch(view){
				case WHOLE:
					return getStrandProbes_Whole(cellnumber, origCellnumber, master, Strand.PLUS);
				case CONDENSED:
					return getStrandProbes_Condensed(cellnumber, origCellnumber, master, Strand.PLUS);
				}
				break;
			case MINUS:
				switch(view){
				case WHOLE:
					return getStrandProbes_Whole(cellnumber, origCellnumber, master, Strand.MINUS);
				case CONDENSED:
					return getStrandProbes_Condensed(cellnumber, origCellnumber, master, Strand.MINUS);
				}
				break;
			case BOTH:
				TranslatedKey transKey = master.getTranslatedKey(cellnumber,origCellnumber);
				if(transKey == null) return Collections.emptyList();
				checkTranslatedKey(origCellnumber, transKey);
				getAllProbes(cellnumber, transKey, origCellnumber, view);
				break;
			default:
				return Collections.emptyList();
			}
		}
		return Collections.emptyList();
	}

	private void checkTranslatedKey(int origCellnumber, TranslatedKey transKey) {
		CheckTranslatedKeys_Delegate.execute(transKey.getTranslatedKeyFirst(),
				transKey.getTranslatedKeyLast(), origCellnumber);
	}



	private List<Probe> getAllForwardProbes_5000_Whole(int cellnumber,
			int originalNumberOfCells, MasterManager master) {

		if (whole_FIFETHOUSAND == null) {
			return getStrandProbes_Whole(cellnumber, originalNumberOfCells,
					master, Strand.PLUS);
		}

		if (whole_FIFETHOUSAND.containsKey((long) cellnumber)) {

			List<Probe> actual = (whole_FIFETHOUSAND.get((long) cellnumber))
			.getForwardProbes();
			if (!actual.isEmpty())
				return actual;
		}
		return Collections.emptyList();
	}

	private List<Probe> getAllForwardProbes_2000_Whole(int cellnumber,
			int originalNumberOfCells, MasterManager master) {

		if (whole_TWOTHOUSAND == null) {
			return getStrandProbes_Whole(cellnumber, originalNumberOfCells,
					master, Strand.PLUS);
		}

		if (whole_TWOTHOUSAND.containsKey((long) cellnumber)) {
			List<Probe> actual = (whole_TWOTHOUSAND.get((long) cellnumber))
			.getForwardProbes();
			if (!actual.isEmpty())
				return actual;
		}
		return Collections.emptyList();
	}

	private List<Probe> getAllForwardProbes_1000_Whole(int cellnumber,
			int originalNumberOfCells, MasterManager master) {

		if (whole_THOUSAND == null) {
			return getStrandProbes_Whole(cellnumber, originalNumberOfCells,
					master, Strand.PLUS);
		}

		if (whole_THOUSAND.containsKey((long) cellnumber)) {
			List<Probe> actual = (whole_THOUSAND.get((long) cellnumber))
			.getForwardProbes();
			if (!actual.isEmpty())
				return actual;
		}
		return Collections.emptyList();
	}

	private List<Probe> getAllBackwardProbes_1000_Whole(int cellnumber, int originalNumberOfCells, MasterManager master) {

		if(whole_THOUSAND== null){
			return getStrandProbes_Whole(cellnumber, originalNumberOfCells, master, Strand.MINUS);
		}

		if(whole_THOUSAND.containsKey((long)cellnumber)){
			List<Probe> actual = (whole_THOUSAND.get((long)cellnumber)).getBackwardProbes();
			if(!actual.isEmpty()) return actual;
		}
		return Collections.emptyList();
	}

	private List<Probe> getAllBackwardProbes_2000_Whole(int cellnumber, int originalNumberOfCells, MasterManager master) {

		if(whole_TWOTHOUSAND== null){
			return getStrandProbes_Whole(cellnumber, originalNumberOfCells, master, Strand.MINUS);
		}

		if(whole_TWOTHOUSAND.containsKey((long)cellnumber)){
			List<Probe> actual = (whole_TWOTHOUSAND.get((long)cellnumber)).getBackwardProbes();
			if(!actual.isEmpty()) return actual;
		}
		return Collections.emptyList();
	}

	private List<Probe> getAllBackwardProbes_5000_Whole(int cellnumber,
			int originalNumberOfCells, MasterManager master) {

		if (whole_FIFETHOUSAND == null) {
			return getStrandProbes_Whole(cellnumber,
					originalNumberOfCells, master, Strand.MINUS);
		}

		if (whole_FIFETHOUSAND.containsKey((long) cellnumber)) {
			List<Probe> actual = (whole_FIFETHOUSAND.get((long) cellnumber))
			.getBackwardProbes();
			if (!actual.isEmpty())
				return actual;
		}
		return Collections.emptyList();
	}

	private List<Probe> getAllBackwardProbes_Fitted_Whole(int cellnumber, int originalNumberOfCells, MasterManager master) {
		if(fitted_actualWholeProbes== null){
			return getStrandProbes_Whole(cellnumber, originalNumberOfCells, master, Strand.MINUS);
		}

		List<Probe> minusProbes = new LinkedList<Probe>();

		if(!fitted_actualWholeProbes.get((long)cellnumber).isEmpty()){	
			for(Probe probe: fitted_actualWholeProbes.get((long)cellnumber)){
				if(getStrand(probe) == Strand.MINUS && !minusProbes.contains(probe))minusProbes.add(probe);
			}
			if(!minusProbes.isEmpty()) return minusProbes;
		}
		return Collections.emptyList();
	}

	private List<Probe> getAllForwardProbes_Fitted_Whole(int cellnumber,
			int originalNumberOfCells, MasterManager master) {
		if (fitted_actualWholeProbes == null) {
			return getStrandProbes_Whole(cellnumber, originalNumberOfCells,
					master, Strand.PLUS);
		}

		List<Probe> plusProbes = new LinkedList<Probe>();

		if (!fitted_actualWholeProbes.get((long) cellnumber).isEmpty()) {

			for (Probe probe : fitted_actualWholeProbes.get((long) cellnumber)) {
				if (getStrand(probe) == Strand.PLUS && !plusProbes.contains(probe))
					plusProbes.add(probe);
			}
			if (!plusProbes.isEmpty())
				return plusProbes;
		}
		return Collections.emptyList();
	}


	public void setCondensed(ArrayList<ForwardBackwardProbes> condensedProbes, ZoomLevel zoom){
		switch(zoom){
		case thousand:
			setCondensed_1000(condensedProbes);
			break;
		case twothousand:
			setCondensed_2000(condensedProbes);
			break;
		case fivethousand:
			setCondensed_5000(condensedProbes);
			break;
		}
	}

	public void setFittedCondensedProbes(ArrayList<List<Probe>> fitted) {
		this.fitted_actualCondensedProbes = fitted;
	}

	private void setCondensed_1000(
			ArrayList<ForwardBackwardProbes> condensedProbes) {
		this.condensed_THOUSAND = condensedProbes;

	}

	private void setCondensed_2000(
			ArrayList<ForwardBackwardProbes> condensedProbes) {
		this.condensed_TWOTHOUSAND = condensedProbes;
	}

	private void setCondensed_5000(
			ArrayList<ForwardBackwardProbes> condensedProbes) {
		this.condensed_FIFETHOUSAND = condensedProbes;
	}


	public long getStartPosition(Probe probe, MIGroup miGroup) {
		return ((LocusMIO) miGroup.getMIO(probe)).getValue().getCoordinate()
		.getFrom();
	}


	/**
	 *	return the size of the actual shown data, if complete chromosome is shown, so length of chromosome is used
	 * else (for range selected data) the length is computed by start-/endposition of first/last probe.
	 * @param kindOfChromeView: difference if whole chromosome ore condensed chromosome is shown
	 * @return
	 */
	public int getViewLength(KindOfChromeView kindOfChromeView) {
		return (int)getChromeSettings().getViewLength(kindOfChromeView);
	}

	public KindOfData getKindOfData() {
		return kindOfData;
	}

	public Chrome getActualChrome() {
		return actualChrome;
	}

	public Species getActualSpecies() {
		return actualChrome.getSpecies();
	}

	public void clear() {
		initialized = false;

	}

	protected ChromosomeSettings getChromeSettings(){
		if(chromosomeSettings==null)chromosomeSettings = new ChromosomeSettings(this);
		return chromosomeSettings;
	}

	public ChromosomeSettings getChromosomeSettings(
			ILogixVizModel Model) {
		getChromeSettings().initialize(Model);
		return getChromeSettings();
	}

	public SelectedRange getRange() {
		return range;
	}
}
