package mayday.core.structures.trees.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import mayday.core.structures.trees.tree.Edge;
import mayday.core.structures.trees.tree.Node;


public class PlainNewick {

	public Node parse(BufferedReader br) throws IOException {
		Node rootNode = new Node("",null);
		inLabel = true;
		inLength = false;
		parseSubtree(rootNode, null, br);
		return rootNode;
	}
	
	public Node parse(String s) {
		StringReader sr = new StringReader(s);
		BufferedReader br = new BufferedReader(sr);
		try {
			return parse(br);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected String currentLabel;
	protected double currentLength;
	protected String currentEdgeLabel;
	protected boolean inLabel;
	protected boolean inLength;
	protected boolean inSubtree;
	protected boolean inEdgeLabel;
	protected StringBuffer collector = new StringBuffer();
	
	protected void finishLabel() {
		if (inLabel) {
			currentLabel = collector.toString();
			collector.setLength(0);
			inLabel = false;
		}
	}
	
	protected void finishEdgeLabel() {
		if (inEdgeLabel) {
			currentEdgeLabel = collector.toString();
			collector.setLength(0);
			inEdgeLabel = false;
		}
	}
	
	protected void finishLength() {
		if (inLength) {
			if (collector.length()>0)
				currentLength = Double.parseDouble(collector.toString());
			else
				currentLength = 1;
			collector.setLength(0);
			inLength = false;
		}
	}
	
	protected void finishNode(Node n, Edge e) {
		finishLabel();
		finishLength();
		finishEdgeLabel();
		n.setLabel(currentLabel);
		if (e!=null) {
			e.setLength(currentLength);
			e.setLabel(currentEdgeLabel);
		}
		currentLabel = null;
		inLabel = true;
	}
	
	protected void finishSubtree() {
		inLabel = true;
		inLength = false;
		inSubtree = false;
		inEdgeLabel = false;
	}
	
	protected void parseSubtree(Node parent, Edge incomingEdge, BufferedReader br) throws IOException {
		/*
		 * Generic case, stripped of whitespace
		 *  (...) LBL [: LEN [:EDGELBL]]
		 *  
		 * As leaf node
		 *  LBL [: LEN [:EDGELBL]] 
		 *  
		 *  this subtree ends at "," or ")"
		 */
		boolean inEscapedString = false;
		inLabel = true;
		
		while (br.ready()) {
			
			char nextChar = (char)br.read();

			switch(nextChar) {
				case '(': // this starts a host of new subtrees
					inLabel = false;
					if (collector.length()>0)
						System.err.println("Can not start subtree edges with \"(\" after reading "+collector.toString());
					collector.setLength(0);
					inSubtree = true;
					while (inSubtree) {
						Node nextChild = new Node("",null);
						Edge nextEdge = new Edge(1,parent,nextChild);
						nextChild.addEdge(nextEdge);
						parent.addEdge(nextEdge);
						parseSubtree(nextChild, nextEdge, br);
					}
					break;	
				case ';': // end of tree
					// fallthrough
				case ')': // end of this subtree, last child of parent
					finishNode(parent, incomingEdge);
					inSubtree = false;
					return;
				case ',': // end of this subtree, parent has more children
					finishNode(parent, incomingEdge);
					inSubtree = true;
					return;
				case ':': // end of label or edge length field
					if (inLength) {
						finishLength();
						currentEdgeLabel = null;
						inEdgeLabel = true;
					} else {
						finishLabel();
						currentLength = 0;
						inLength = true;
					}
					break;
				case '\"': // begins an escaped string
					if (inLabel)
						inEscapedString = true;
					break;
				default: // collect this character
					collector.append(nextChar);
					break;
			}			
			
			boolean inEscape = false;
			
			while (br.ready() && inEscapedString) {
				nextChar = (char)br.read();
				switch (nextChar) {
				case '\\':
					if (inEscape)
						collector.append("\\");
					inEscape = !inEscape; // swallow escape character
					break;
				case '\"':
					if (inEscape) {
						collector.append("\"");
						inEscape = false;
					} else 
						inEscapedString = false;
					break;
				default:
					inEscape = false;
					collector.append(nextChar);				
				}
			}
		}				
	}

	
	public void serialize(Node root, Edge incomingEdge, BufferedWriter bw) throws IOException {

		if (!root.isLeaf()) {
			// serialize all subtrees
			bw.write("(");
			boolean isFirst = true; 
			for (Edge e : root.getEdges()) {
				if (e!=incomingEdge) {
					if (!isFirst) 
						bw.write(",");
					else
						isFirst = false;
					Node targetNode = e.getOtherNode(root);
					serialize(targetNode, e, bw);
				}
			}
			bw.write(")");
		}

		bw.write("\""+escapeSpecials(root.getLabel())+"\"");
		
		if (incomingEdge!=null) {
			// serialize incoming edge
			bw.write(":"+incomingEdge.getLength());
			if (incomingEdge.getLabel()!=null)
				bw.write(":"+incomingEdge.getLabel());
		}
		else
			// finish tree
			bw.write(";");	
		
		bw.flush();
	}

	public static String escapeSpecials(String input) {
		return input
		.replace("\\", "\\\\")
		.replace("\n", "\\n")
		.replace("\t", "\\t")
		.replace("\"", "\\\"");
	}
	
	public static String unescapeSpecials(String input) {
		return input
		.replace("\\n", "\n")
		.replace("\\t", "\t")
		.replace("\\\"", "\"")
		.replace("\\\\", "\\");
	}
	
	public String serialize(Node root) {
		StringWriter sw = new StringWriter();
		BufferedWriter bw = new BufferedWriter(sw);
		try {
			serialize(root, null, bw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sw.toString();
	}

}
