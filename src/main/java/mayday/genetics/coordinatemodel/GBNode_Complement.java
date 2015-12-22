package mayday.genetics.coordinatemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mayday.genetics.basic.Strand;

public class GBNode_Complement implements GBNode {

	protected GBNode child;
	
	public GBNode_Complement(GBNode c) {
		child = c;
	}

	public GBNode_Complement(String s) {
		child = GBParser.parse(s);
	}

	
	public Strand getStrand() {
		Strand s = child.getStrand();
		if (s==Strand.PLUS)
			return Strand.MINUS;
		if (s==Strand.MINUS)
			return Strand.PLUS;
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
			if (gbre.strand==Strand.PLUS)
				gbre.strand=Strand.MINUS;
			else
				gbre.strand=Strand.PLUS;
		}
		// reverse children order
		for (int i=curPos, j=list.size()-1; i<j; i++, j--)
             Collections.swap(list, i, j);
	}

	@Override
	public long getCoveredBases() {
		return child.getCoveredBases();
	}
	
	@Override
	public String serialize() {
		StringBuffer sb = new StringBuffer();
		sb.append("complement(");
			sb.append(child.serialize());
		sb.append(")");
		return sb.toString();
	}

	@Override
	public boolean isPrimitive() {
		return child.isPrimitive();
	}
	
}
