package mayday.vis3.plots.trees;

import mayday.core.Experiment;
import mayday.core.MasterTable;
import mayday.core.structures.maps.BidirectionalHashMap;
import mayday.core.structures.trees.layout.Layout;
import mayday.core.structures.trees.layout.ObjectMapper;
import mayday.core.structures.trees.tree.Node;

public class ExperimentNodeMapper implements ObjectMapper {
	
	public BidirectionalHashMap<Node,Experiment> mapping = new BidirectionalHashMap<Node, Experiment>();
	public Layout layout;
	
	public ExperimentNodeMapper(Layout layout, MasterTable mt) {
		this.layout = layout;
		for (Node n : layout.getRoot().getLeaves(null)) {
			Experiment ex = mt.getExperiment(n.getLabel());
			if (ex!=null)
				mapping.put(n,ex);
		}
	}
	
	public Object getObject(Node n) {
		return (Experiment)mapping.get(n);
	}
	
	public Node getNode(Object pb) {
		return (Node)mapping.get(pb);
	}

	public String getLabel(Node n) {
		Experiment ex = (Experiment)getObject(n);
		if (ex==null)
			return n.getLabel();
		return ex.getDisplayName();
	}


}
