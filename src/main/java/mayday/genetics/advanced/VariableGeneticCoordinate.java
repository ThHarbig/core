package mayday.genetics.advanced;

import java.util.List;

import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.SpeciesContainer;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.genetics.coordinatemodel.GBAtom;
import mayday.genetics.coordinatemodel.GBNode;
import mayday.genetics.coordinatemodel.GBParser;

public class VariableGeneticCoordinate extends AbstractGeneticCoordinate {
	
	protected String _species;
	protected String _chromosome;
	protected Long _length;
	protected GBAtom myAtom;
	protected Chromosome chromosome;
	protected ChromosomeSetContainer _csc;


	public VariableGeneticCoordinate(ChromosomeSetContainer csc) {
		myAtom = new GBAtom(-1, -1, Strand.UNSPECIFIED);
		_csc = csc;
	}
	
	public final void setChromosome(Chromosome s) {
		this.chromosome = s;
	}
	
	public final void setFrom(long s) {
		myAtom.from = s;
	}
	
	public final void setTo(long s) {
		myAtom.to =  s;
	}
	
	public final void setStrand(Strand s) {
		myAtom.strand = s;
	}
	
	public final void update(Integer what, Object value) {
		if (what==null) 
			return;
		if (value==null) {
			if (what==VariableGeneticCoordinateElement.Length)  
				_length = null;
			return;
		}
		if (value instanceof CharSequence) {
			update_charseq(what, (CharSequence)value);
		} else if (value instanceof Strand && what==VariableGeneticCoordinateElement.Strand) {
			setStrand((Strand)value);
		} else if (value instanceof Long) {
			update_long(what, (Long)value);
		} else
			if (!update_extended(what, value))
				throw new IllegalArgumentException("Can't update coordinate with object of "+(value!=null?value.getClass():"null")+" in field "+what);
	}
	
	// override this for extended variable coordinates
	protected boolean update_extended(int what, Object value) {
		return false;
	}
	
	public final void update_long(Integer what, long lvalue) {
		if (what==null)
			return;
		switch (what) {
		case VariableGeneticCoordinateElement.From:
			setFrom(lvalue);
			if (_length!=null && getTo()==-1)
				setTo(getFrom()+_length-1);				
			break;
		case VariableGeneticCoordinateElement.To:
			setTo(lvalue);
			if (_length!=null && getFrom()==-1)
				setFrom(getTo()-_length+1);
			break;
		case VariableGeneticCoordinateElement.Length:
			_length=lvalue;
			if (getFrom()!=-1 && getTo()==-1)
				setTo(getFrom()+_length-1);
			break;			
		default: 
			if (!update_long_extended(what, lvalue))
				throw new IllegalArgumentException("Can't update coordinate with Long value for target field "+what);
		}
	}
	
	// override this for extended variable coordinates
	protected boolean update_long_extended(int what, long lvalue) {
		return false;
	}
		
	public final void update_charseq(Integer what, CharSequence value) {
		if (what==null || (value==null && what!=VariableGeneticCoordinateElement.Length))
			return;
		if (what==VariableGeneticCoordinateElement.From || what==VariableGeneticCoordinateElement.To || what==VariableGeneticCoordinateElement.Length) {
			if (value!=null) {
				long lvalue = parseLong(value, 10);
				update_long(what, lvalue);
				return;
			}
		}
		
		if (value==null)
			return;
		
		switch (what) {
		case VariableGeneticCoordinateElement.Species: 
			_species = value.toString();
			if (_chromosome!=null)
				setChromosome(_csc.getChromosome(SpeciesContainer.getSpecies(_species), _chromosome));
			break;
		case VariableGeneticCoordinateElement.Chromosome:
			_chromosome = value.toString();
			if (_species!=null)
				setChromosome(_csc.getChromosome(SpeciesContainer.getSpecies(_species), _chromosome));
			break;			
		case VariableGeneticCoordinateElement.Strand:
			if (value!=null) {
				char c = value.toString().charAt(0);
				Strand str; 
				switch(c) {
				// allow D/P F/R W/C S/A
				case 'D': // fall through  "Direct"
				case 'F': // fall through  "Forward"
				case 'W': // fall through  "Watson"
				case 'S': // 			   "Sense"
					str = Strand.PLUS;
					break;
				case 'P': // fall through  "Palindromic"
				case 'R': // fall through  "Reverse"
				case 'C': // fall through  "Crick"
				case 'A': //			   "Antisense"
					str = Strand.MINUS;
					break;
				default:
					str = Strand.fromChar(c);
				}
				setStrand(str);
			}
			break;
		default: 
			if (!update_charseq_extended(what, value))
				throw new RuntimeException("Can't update coordinate. Illegal element type: "+what);
		}
	}
	
	// override this for extended variable coordinates
	protected boolean update_charseq_extended(int what, CharSequence value) {
		return false;
	}

	
	@Override
	public Chromosome getChromosome() {
		return chromosome;
	}

	@Override
	public List<GBAtom> getCoordinateAtoms() {
		return getModel().getCoordinateAtoms();
	}

	@Override
	public long getCoveredBases() {
		return myAtom.to-myAtom.from+1;
	}

	@Override
	public long getFrom() {
		return myAtom.from;
	}

	@Override
	public GBNode getModel() {
		return GBParser.convert(myAtom);
	}

	@Override
	public Strand getStrand() {
		return myAtom.strand;
	}

	@Override
	public long getTo() {
		return myAtom.to;
	}
	
	/** Long does not define a parsing method for charsequences - which is stupid since String is an instanceof CharSequence 
	 * Apart from including some private fields of LONG, this code is _exactly_ the same as Long.parseLong(String s, int radix) */
	
	public static long parseLong(CharSequence s, int radix)
	throws NumberFormatException
	{
		if (s == null) {
			throw new NumberFormatException("null");
		}

		if (radix < Character.MIN_RADIX) {
			throw new NumberFormatException("radix " + radix +
			" less than Character.MIN_RADIX");
		}
		if (radix > Character.MAX_RADIX) {
			throw new NumberFormatException("radix " + radix +
			" greater than Character.MAX_RADIX");
		}

		long result = 0;
		boolean negative = false;
		int i = 0, max = s.length();
		long limit;
		long multmin;
		int digit;
	    long   MULTMIN_RADIX_TEN =  Long.MIN_VALUE / 10;
	    long N_MULTMAX_RADIX_TEN = -Long.MAX_VALUE / 10;


		if (max > 0) {
			if (s.charAt(0) == '-') {
				negative = true;
				limit = Long.MIN_VALUE;
				i++;
			} else {
				limit = -Long.MAX_VALUE;
			}
			if (radix == 10) {
				multmin = negative ? MULTMIN_RADIX_TEN : N_MULTMAX_RADIX_TEN;
			} else {
				multmin = limit / radix;
			}
			if (i < max) {
				digit = Character.digit(s.charAt(i++),radix);
				if (digit < 0) {
					throw new NumberFormatException(s.toString());
				} else {
					result = -digit;
				}
			}
			while (i < max) {
				// Accumulating negatively avoids surprises near MAX_VALUE
				digit = Character.digit(s.charAt(i++),radix);
				if (digit < 0) {
					throw new NumberFormatException(s.toString());
				}
				if (result < multmin) {
					throw new NumberFormatException(s.toString());
				}
				result *= radix;
				if (result < limit + digit) {
					throw new NumberFormatException(s.toString());
				}
				result -= digit;
			}
		} else {
			throw new NumberFormatException(s.toString());
		}
		if (negative) {
			if (i > 1) {
				return result;
			} else {	/* Only got "-" */
				throw new NumberFormatException(s.toString());
			}
		} else {
			return -result;
		}
	}
	
	
}