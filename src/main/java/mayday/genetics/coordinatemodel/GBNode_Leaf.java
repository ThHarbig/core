package mayday.genetics.coordinatemodel;

import java.util.ArrayList;
import java.util.List;

import mayday.genetics.basic.Strand;

public class GBNode_Leaf implements GBNode {

	protected long start, stop;
	
	public GBNode_Leaf() {};
	
	public GBNode_Leaf(long start, long stop) {
		this.start = start;
		this.stop = stop;
	}
	
	public GBNode_Leaf(String s) {
		parse(s);
	}
	
	public Strand getStrand() {
		return Strand.PLUS;
	}
	
	public long getStart() {
		return start;
	}
	
	public long getEnd() {
		return stop;
	}
	
	public void parse(String position) {
		String start = null;
		String stop = null;
		
		if (position.contains("..")) {
			// from..to position
			String[] p = position.split("\\.\\.");
			start = p[0];
			stop = p[1];
		} else if (position.contains(".")) {
			// not really well known position, handle as region
			String[] p = position.split("\\.");
			start = p[0];
			stop = p[1];
			System.out.println("Fuzzy positions are regarded as well-defined in location: "+position);
		} else if (position.contains("^")) {
			// between bases
			String[] p = position.split("^");
			start = p[0];
			stop = p[1];			
			System.out.println("In-between-bases locations are expanded to cover the neighboring bases in location: "+position);
		} else {
			// single base position
			start = position;
			stop = position;
		}
		
		try {
			this.start=Long.parseLong(start);
			this.stop=Long.parseLong(stop);
		} catch (Exception e) {
			this.start=-1;
			this.stop=-1;
		}
	}

	@Override
	public List<GBAtom> getCoordinateAtoms() {
		ArrayList<GBAtom> lgbre = new ArrayList<GBAtom>();
		addCoordinateAtoms(lgbre);
		return lgbre;
	}

	@Override
	public void addCoordinateAtoms(List<GBAtom> list) {
		list.add(new GBAtom(start,stop,getStrand()));
	}

	@Override
	public long getCoveredBases() {
		return stop-start+1;
	}

	@Override
	public String serialize() {
		return start+".."+stop;
	}

	@Override
	public boolean isPrimitive() {
		return true;
	}
	
	
	
}
