package mayday.genetics.advanced.chromosome;

import java.util.LinkedList;
import java.util.List;

import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.genetics.coordinatemodel.GBAtom;
import mayday.genetics.coordinatemodel.GBNode;
import mayday.genetics.coordinatemodel.GBNode_Complement;
import mayday.genetics.coordinatemodel.GBNode_Leaf;
import mayday.genetics.coordinatemodel.GBParser;

@SuppressWarnings("unchecked")
public abstract class AbstractLocusGeneticCoordinate<T extends AbstractLocusChromosome> extends AbstractGeneticCoordinate {
	
	protected long myId;
	protected T container;
	protected GBNode cache;
	
	public AbstractLocusGeneticCoordinate(T cont, long id) {
		container = cont;
		myId = id;
	}

	@Override
	public Chromosome getChromosome() {
		return container;
	}

	@Override
	public long getFrom() {
		return getModel().getStart();
	}

	@Override
	public Strand getStrand() {
		return getModel().getStrand();
	}

	@Override
	public long getTo() {
		return getModel().getEnd();
	}
	
	/** return the number of covered bases, usually different from length */
	public long getCoveredBases() {
		return getModel().getCoveredBases();
	}
	
	protected GBNode createModel() {
		// reconstruct the model from the container		
		return GBParser.convert(getCoordinateAtoms());
	}
	
	protected GBNode getNodeFor(long id) {
		GBNode node = new GBNode_Leaf(container.getStart(id), container.getEnd(id));
		if (container.getStrand(id)==Strand.MINUS)
			node = new GBNode_Complement(node);
		return node;
	}
	
	public GBNode getModel() {
		if (cache==null)
			cache = createModel();
		return cache;
	}
	
	public List<GBAtom> getCoordinateAtoms() {
		LinkedList<GBAtom> lga = new LinkedList<GBAtom>();
		lga.add(new GBAtom(container.getStart(myId),container.getEnd(myId),container.getStrand(myId)));
		long i=myId+1;
		while ( i < container.strands.size() && !container.isStart.get(i)) {
			lga.add(new GBAtom(container.getStart(i),container.getEnd(i),container.getStrand(i)));
			++i;
		}
		return lga;
	}

}

