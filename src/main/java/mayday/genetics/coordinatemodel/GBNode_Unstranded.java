package mayday.genetics.coordinatemodel;

import java.util.ArrayList;
import java.util.List;

import mayday.genetics.basic.Strand;

public class GBNode_Unstranded implements GBNode {

	protected GBNode child;
	
	public GBNode_Unstranded(GBNode c) {
		child = c;
	}

	public GBNode_Unstranded(String s) {
		child = GBParser.parse(s);
	}

	
	public Strand getStrand() {
		return Strand.UNSPECIFIED;
	}
	
	public long getStart() {
		return child.getStart();
	}
	
	public long getEnd() {
		return child.getEnd();
	}

	@Override
	public List<GBAtom> getCoordinateAtoms() {
		ArrayList<GBAtom> lgbre = new ArrayList<GBAtom>();
		addCoordinateAtoms(lgbre);
		return lgbre;
	}
	
	@Override
	public void addCoordinateAtoms(List<GBAtom> list) {
		int curPos = list.size();
		child.addCoordinateAtoms(list);
		// turn children
		for (int i=curPos; i!=list.size(); ++i) {
			GBAtom gbre = list.get(i);
			gbre.strand=Strand.UNSPECIFIED;
		}
	}

	@Override
	public long getCoveredBases() {
		return child.getCoveredBases();
	}
	
	@Override
	public String serialize() {		
		StringBuffer sb = new StringBuffer();
		sb.append("unstrand(");
			sb.append(child.serialize());
		sb.append(")");
		return sb.toString();
	}

	@Override
	public boolean isPrimitive() {
		return child.isPrimitive();
	}
	
}
