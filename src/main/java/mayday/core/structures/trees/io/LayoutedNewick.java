package mayday.core.structures.trees.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import mayday.core.structures.trees.layout.Coordinate;
import mayday.core.structures.trees.layout.EdgeLayout;
import mayday.core.structures.trees.layout.ILayoutValue;
import mayday.core.structures.trees.layout.Layout;
import mayday.core.structures.trees.layout.NodeLayout;
import mayday.core.structures.trees.layouter.TopDownDendrogram;
import mayday.core.structures.trees.tree.Edge;
import mayday.core.structures.trees.tree.ITreePart;
import mayday.core.structures.trees.tree.Node;


public class LayoutedNewick extends PlainNewick {

	protected boolean defaultLayout = true;
	
	public Layout parseWithLayout(BufferedReader br) throws IOException {
		Node root = super.parse(br);
		
		Layout layout = null;
		
		if (br.ready()) {
			// layout is also there
			try {
				layout = parseLayout(root, br);
				defaultLayout = false;
			} catch (Exception e) {
				// ignore
			}
		}
		
		// do default layout if nothing found
		if (layout==null) {
			layout = new TopDownDendrogram().doLayout(root);
		}
			
		return layout;
	}
	
	public boolean isDefaultLayout() {
		return defaultLayout;
	}
	
	protected Layout parseLayout(Node root, BufferedReader br) throws IOException {
		Layout layout = new Layout();
		layout.setRoot(root);
		br.readLine(); // swallow the newline
		
		// read info about layout
		String layoutInfo = br.readLine();
		layout.parse(layoutInfo);
		
		// read coordinates		
		parseCoordinates(layout, layout.getRoot(), null, br);
		br.readLine(); // swallow the newline
		
		// read default node layout
		NodeLayout defaultNodeLayout = new NodeLayout(br.readLine());
		// add node layouts
		parseLayouts(layout, layout.getRoot(), null, br, true);
		br.readLine(); // swallow the newline
		
		// read default edge layout
		EdgeLayout defaultEdgeLayout = new EdgeLayout(br.readLine());
		// add node layouts
		parseLayouts(layout, layout.getRoot(), null, br, false);		
		
		layout.setDefaultLayouts(defaultNodeLayout, defaultEdgeLayout);
		
		return layout;
	}

	
	public void serialize(Layout layout, Edge incomingEdge, BufferedWriter bw) throws IOException {
		super.serialize(layout.getRoot(), incomingEdge, bw);
		bw.write("\n");
		
		// add info about layout
		bw.write(layout.serialize());
		bw.write("\n");
		
		// add coordinates		
		serializeCoordinates(layout, layout.getRoot(), incomingEdge, bw);
		bw.write("\n");
		
		// add default node layout
		bw.write(layout.getNodeLayouts().getDefaultLayout().serialize());
		bw.write("\n");
		// add node layouts
		serializeLayouts(layout, layout.getRoot(), incomingEdge, bw, true);
		bw.write("\n");
		
		// add default edge layout
		bw.write(layout.getEdgeLayouts().getDefaultLayout().serialize());
		bw.write("\n");
		// add edge layouts
		serializeLayouts(layout, layout.getRoot(), incomingEdge, bw, false);
		bw.flush();
	}

	
	protected void serializeCoordinates(Layout layout, Node root, Edge incomingEdge, BufferedWriter bw) throws IOException {
		// postorder serialization
		if (!root.isLeaf()) {
			for (Edge e : root.getEdges()) {
				if (e!=incomingEdge) {
					serializeCoordinates(layout, e.getOtherNode(root), e, bw);					
				}
			}
			
		}
		bw.write(layout.getCoordinate(root).serialize());
		bw.write(":");
	}
	
	protected void serializeLayouts(Layout layout, Node startNode, Edge incomingEdge, BufferedWriter bw, boolean doNodes) throws IOException {
		// postorder serialization
		if (!startNode.isLeaf()) {
			for (Edge e : startNode.getEdges()) {
				if (e!=incomingEdge) {
					serializeLayouts(layout, e.getOtherNode(startNode), e, bw, doNodes);
				}
			}		
		}
		// empty string if default
		ITreePart serializedValue = doNodes ? startNode : incomingEdge;
		if (!layout.hasDefaultLayout(serializedValue))
			bw.write(layout.getLayout(serializedValue).serialize());
		bw.write(":");
	}
	
	protected void parseCoordinates(Layout layout, Node startNode, Edge incomingEdge, BufferedReader bw) throws IOException {
		// postorder serialization
		if (!startNode.isLeaf()) {
			for (Edge e : startNode.getEdges()) {
				if (e!=incomingEdge) {
					parseCoordinates(layout, e.getOtherNode(startNode), e, bw);
				}
			}
			
		}
		
		// read everything up to the separator char ":"
		StringBuffer sb = new StringBuffer();
		char c;
		while ( (c=(char)bw.read())!=':' )
			sb.append(c);
		Coordinate coord = new Coordinate(sb.toString());
		layout.setCoordinate(startNode, coord);		
	}
	
	protected void parseLayouts(Layout layout, Node startNode, Edge incomingEdge, BufferedReader bw, boolean doNode) throws IOException {
		// postorder serialization
		if (!startNode.isLeaf()) {
			for (Edge e : startNode.getEdges()) {
				if (e!=incomingEdge) {
					parseLayouts(layout, e.getOtherNode(startNode), e, bw, doNode);
				}
			}
			
		}
		
		// read everything up to the separator char ":"
		StringBuffer sb = new StringBuffer();
		char c;
		while ( (c=(char)bw.read())!=':' )
			sb.append(c);
		if (sb.length()>0) {
			// not DEFAULT layout
			ILayoutValue i = doNode ? new NodeLayout(sb.toString()) : new EdgeLayout(sb.toString());
			ITreePart layoutTarget = doNode ? startNode : incomingEdge;
			layout.setLayout(layoutTarget, i);		
		}
	}
}
