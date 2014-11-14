package mayday.genetics.advanced.chromosome;


public class LocusGeneticCoordinateDouble extends AbstractLocusGeneticCoordinate<LocusChromosomeDouble>{

	public LocusGeneticCoordinateDouble(LocusChromosomeDouble cont, long id) {
		super(cont, id);
	}		

	public double getValue() {
		return container.getValue(myId);
	}

}
