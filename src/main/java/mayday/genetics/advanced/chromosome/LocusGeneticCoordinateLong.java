package mayday.genetics.advanced.chromosome;


public class LocusGeneticCoordinateLong extends AbstractLocusGeneticCoordinate<LocusChromosomeLong>{

	public LocusGeneticCoordinateLong(LocusChromosomeLong cont, long id) {
		super(cont, id);
	}		

	public long getValue() {
		return container.getValue(myId);
	}

}
