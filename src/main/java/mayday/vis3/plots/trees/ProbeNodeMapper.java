package mayday.vis3.plots.trees;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.structures.maps.BidirectionalHashMap;
import mayday.core.structures.trees.layout.Layout;
import mayday.core.structures.trees.layout.ObjectMapper;
import mayday.core.structures.trees.tree.Node;

public class ProbeNodeMapper implements ObjectMapper {
	
	public BidirectionalHashMap<Node,Probe> mapping = new BidirectionalHashMap<Node, Probe>();
	public Layout layout;
	
	public ProbeNodeMapper(Layout layout, MasterTable mt) {
		this.layout = layout;
		for (Node n : layout.getRoot().getLeaves(null)) {
			Probe pb = mt.getProbe(n.getLabel());
			if (pb!=null)
				mapping.put(n,pb);
		}
	}
	
	public Object getObject(Node n) {
		return (Probe)mapping.get(n);
	}
	
	public Node getNode(Object pb) {
		return (Node)mapping.get(pb);
	}

	public String getLabel(Node n) {
		Probe pb = (Probe)getObject(n);
		if (pb==null)
			return n.getLabel();
		return pb.getDisplayName();
	}


}
