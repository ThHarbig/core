package mayday.genetics.coordinatemodel;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import mayday.genetics.basic.Strand;

public class GBParser {

	public static GBNode parse(String s) {
		if (s.endsWith(")")) {
			String operator = s.substring(0, s.indexOf("("));
			String operand = s.substring(s.indexOf("(")+1, s.length()-1);
			if (operator.equals("join"))
				return new GBNode_Join(operand);
			if (operator.equals("complement"))
				return new GBNode_Complement(operand);
			if (operator.equals("unstrand"))
				return new GBNode_Unstranded(operand);
		}
		
		// a simple child node
		return new GBNode_Leaf(s);
	}
	
	// this is a speed-up function to quickly convert from one-element collections
	// collections with multiple elements need to be sorted anyways, this is done via conversion 
	// to arrays in Collections.sort(), so we might as well convert the collection right away.
	public static GBNode convert(Collection<GBAtom> atoms) {
		if (atoms.size()==0)
			throw new RuntimeException("Coordinates must at least contain one atom");		
		
		// quick method for primitive coordinates
		if (atoms.size()==1) {
			GBAtom atom = atoms.iterator().next();
			GBNode returnNode = new GBNode_Leaf(atom.from, atom.to);
			if (atom.strand==Strand.MINUS)
				returnNode = new GBNode_Complement(returnNode);
			return returnNode;
		} else {
			// use the complicated function :)
			return convert(atoms.toArray(new GBAtom[0]));			
		}
		
	}
	
	public static GBNode convert(GBAtom... atoms) {
		if (atoms.length==0)
			throw new RuntimeException("Coordinates must at least contain one atom");
		
		GBNode returnNode;
		
		// quick method for primitive coordinates
		if (atoms.length==1) {
			GBAtom atom = atoms[0];
			returnNode = new GBNode_Leaf(atom.from, atom.to);
			if (atom.strand==Strand.MINUS)
				returnNode = new GBNode_Complement(returnNode);
		} else {
			// atoms are first sorted, then grouped by strandedness, then joined
			Arrays.sort(atoms);
			
			GBAtom current = atoms[0];
			int last=0;
			
			LinkedList<GBNode> innerNodes = new LinkedList<GBNode>();
			
			for (int i=1; i<=atoms.length; ++i) {
							
				GBAtom next = i<atoms.length?atoms[i]:null;
				
				if (next==null || next.strand!=current.strand) {
					// finish last group [last, i-1]
					GBNode[] nodes = new GBNode[i-last];
					for (int j=last; j!=i; ++j) {
						nodes[j-last] = new GBNode_Leaf(atoms[j].from, atoms[j].to);
					}
					
					GBNode curNode;
					
					if (nodes.length>1)
						curNode = new GBNode_Join(nodes);
					else 
						curNode = nodes[0];
							
					if (current.strand==Strand.MINUS)
						curNode = new GBNode_Complement(curNode);
					innerNodes.add(curNode);
					last = i;
					current = next;
				}
			}

			if (innerNodes.size()==1)
				returnNode = innerNodes.getFirst();
			else
				returnNode = new GBNode_Join(innerNodes.toArray(new GBNode[0]));
		}
		
		return returnNode;
		
	}
	
}
