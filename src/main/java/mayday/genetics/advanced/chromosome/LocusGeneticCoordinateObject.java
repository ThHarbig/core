package mayday.genetics.advanced.chromosome;


public class LocusGeneticCoordinateObject<ObjectType> extends AbstractLocusGeneticCoordinate<LocusChromosomeObject<ObjectType>>{

	public LocusGeneticCoordinateObject(LocusChromosomeObject<ObjectType> cont, long id) {
		super(cont, id);
	}		

	public ObjectType getObject() {
		return container.getObject(myId);
	}

}
