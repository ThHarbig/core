package mayday.genetics.advanced;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.SpeciesContainer;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.genetics.coordinatemodel.GBAtom;
import mayday.genetics.coordinatemodel.GBNode;
import mayday.genetics.coordinatemodel.GBParser;

public class VariableComplexGeneticCoordinate extends AbstractGeneticCoordinate {

	protected LinkedList<GBAtom> _atoms;
	protected String _species;
	protected String _chromosome;
	protected Chromosome chromosome;
	protected ChromosomeSetContainer _csc;
	protected GBNode _model;

	public VariableComplexGeneticCoordinate(ChromosomeSetContainer csc) {
		_atoms = new LinkedList<GBAtom>();
		_csc = csc;
	}


	/** removes atoms, preserves chromosome, species information */
	public void clear() {
		_model = null;
		_atoms.clear();
	}

	@Override
	public Chromosome getChromosome() {
		if (_chromosome!=null)
			convertChromosome();
		return chromosome;
	}

	public void setChromosome(Chromosome c) {
		chromosome = c;
		_chromosome = null;
	}

	protected void convertChromosome() {
		setChromosome(_csc.getChromosome(SpeciesContainer.getSpecies(_species), _chromosome));
	}

	protected void convertModel() {
		_model = GBParser.convert(_atoms);
	}

	public void addAtoms(GBAtom... atoms) {
		for (GBAtom a : atoms)
			_atoms.add(a);
		_model = null;
	}

	@Override
	public List<GBAtom> getCoordinateAtoms() {
		return Collections.unmodifiableList(_atoms);
	}


	public long length() {
		return getModel().getCoveredBases();
	}

	public long getFrom() {
		return getModel().getStart(); 
	}

	public long getTo() {
		return getModel().getEnd();
	}

	public Strand getStrand() {
		return getModel().getStrand();
	}


	public GBNode getModel() {
		if (_model==null)
			convertModel();
		return _model;
	}

	@Override
	public long getCoveredBases() {
		return getModel().getCoveredBases();
	}

	public void update(Integer what, Object value) {
		if (what==null || value==null)
			return;

		switch (what) {
		case VariableGeneticCoordinateElement.Species: 
			_species = (String)value;
			break;
		case VariableGeneticCoordinateElement.Chromosome:
			_chromosome = (String)value;
			break;
		case VariableGeneticCoordinateElement.AddAtom:
			if (value instanceof GBAtom)
				addAtoms((GBAtom)value);			
			else if (value instanceof GBAtom[]) 
				addAtoms((GBAtom[])value);
			else 
				throw new RuntimeException("Can't add atom of class "+value.getClass().getCanonicalName());
			break;
		case VariableGeneticCoordinateElement.Model:
			if (value instanceof GBNode) {
				_atoms.clear();
				_model = ((GBNode)value);
				_atoms.addAll(_model.getCoordinateAtoms());
			} else {
				throw new RuntimeException("Can't set model of class "+value.getClass().getCanonicalName());
			}
			break;
		default:
			throw new RuntimeException("Can't update coordinate. Illegal element type: "+what);
		}
	}
	
}