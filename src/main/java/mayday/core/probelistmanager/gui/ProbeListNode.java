package mayday.core.probelistmanager.gui;

import javax.swing.tree.DefaultMutableTreeNode;

import mayday.core.ProbeList;
import mayday.core.probelistmanager.UnionProbeList;


@SuppressWarnings("serial")
public class ProbeListNode extends DefaultMutableTreeNode { 

	public ProbeListNode(ProbeList probeList) {
		super(probeList);
	}
	
	public void setProbeList(ProbeList pl) {
		setUserObject(pl);
	}
	
	public ProbeList getProbeList() {
		if (getUserObject()!=null && getUserObject() instanceof ProbeList) 
			return (ProbeList)getUserObject();
		else
			return null;
	}
	
	public void setParent(ProbeListNode newParent) {
		
		ProbeList pl = getProbeList();
		
		UnionProbeList ppl = null;		
		if (newParent!=null)
			ppl = (UnionProbeList)newParent.getProbeList();
		
		if (pl!=null) {
			pl.setParent(ppl);
		}
			
	}
	
	public ProbeListNode getParent() {
		return (ProbeListNode)super.getParent();
	}

}
