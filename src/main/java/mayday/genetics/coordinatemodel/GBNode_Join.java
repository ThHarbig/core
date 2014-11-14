package mayday.genetics.coordinatemodel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import mayday.genetics.basic.Strand;

public class GBNode_Join implements GBNode {

	protected GBNode[] children;
	
	public GBNode_Join(GBNode[] children) {
		this.children = children;
	}
	
	public GBNode_Join(String s) {
		LinkedList<String> subparts = new LinkedList<String>();
		// first split the input into the sub-parts
		int bracelev = 0;
		int j=0;
		for (int i=0; i!=s.length(); ++i) {
			char c = s.charAt(i);
			switch(c) {
			case '(': 
				bracelev++;
				break;
			case ')':
				bracelev--;
				break;
			case ',': 
				if (bracelev==0) {
					subparts.add(s.substring(j, i));
					j=i+1;
					break;
				}
			}
		}
		subparts.add(s.substring(j, s.length()));
		children = new GBNode[subparts.size()];
		for (int i=0; i!=subparts.size(); ++i)
			children[i] = GBParser.parse(subparts.get(i));
	}
	
	public Strand getStrand() {
		Strand s = null;
		for (GBNode c : children) {
			Strand cs = c.getStrand();
			if (s==null)
				s = cs;
			else if (!s.equals(cs)) {
				s = Strand.UNSPECIFIED;
				break;
			}
		}
		if (s==null)
			s = Strand.UNSPECIFIED;
		return s;
	}
	
	public long getStart() {
		long min=Long.MAX_VALUE;
		for (GBNode c : children) {
			long cs = c.getStart();
			if (cs<min)
				min = cs;
		}
		if (min==Long.MAX_VALUE)
			min = -1;
		return min;
	}
	
	public long getEnd() {
		long max=Long.MIN_VALUE;
		for (GBNode c : children) {
			long cs = c.getEnd();
			if (cs>max)
				max = cs;
		}
		if (max==Long.MIN_VALUE)
			max = -1;
		return max;
	}
	
	
	@Override
	public List<GBAtom> getCoordinateAtoms() {
		ArrayList<GBAtom> lgbre = new ArrayList<GBAtom>();
		addCoordinateAtoms(lgbre);
		return lgbre;
	}
	
	@Override
	public void addCoordinateAtoms(List<GBAtom> list) {
		for (GBNode c : children)
			c.addCoordinateAtoms(list);
	}

	@Override
	public long getCoveredBases() {
		long l=0;
		for (GBNode c : children)
			l+=c.getCoveredBases();
		return l;
	}

	@Override
	public String serialize() {
		StringBuffer sb = new StringBuffer();
		sb.append("join(");
		boolean f=true;
		for (GBNode c : children) {
			if (f)
				f=false;
			else
				sb.append(",");
			sb.append(c.serialize());
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public boolean isPrimitive() {		
		// this is complex if it has more than 1 child or if the one child is complex
		// (which would mean the coordinate model is degenerate, but we should treat
		// that case correctly anyhow.
		return (children.length<2 && children[0].isPrimitive());
	}
	
	
	
	
}
