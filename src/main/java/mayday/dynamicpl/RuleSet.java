package mayday.dynamicpl;

import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import mayday.core.Probe;
import mayday.core.io.StorageNode;
import mayday.dynamicpl.gui.OptionPanelProvider;
import mayday.dynamicpl.gui.RuleSetGUI;
import mayday.dynamicpl.miostore.StorageNodeStorable;

public class RuleSet implements ProbeFilter, ChangeListener, OptionPanelProvider, StorageNodeStorable {

	private int combinationMode = COMBINE_AND;
	public static final int COMBINE_AND = 0;
	public static final int COMBINE_OR = 1;
	private DynamicProbeList dynamicProbeList;
	
	private LinkedList<ProbeFilter> subRules = new LinkedList<ProbeFilter>();
	private EventListenerList eventListenerList = new EventListenerList();
	private boolean isSilent=false;
	private boolean isInactive=false;
	
	public RuleSet(DynamicProbeList dpl) {
		dynamicProbeList = dpl;
	}
	
	public ProbeFilter get(int index) {
		return subRules.get(index);
	}
	
	public LinkedList<ProbeFilter> getSubRules() {
		return new LinkedList<ProbeFilter>(subRules); //copy, not reference
	}
	
	public void addSubRule(ProbeFilter sub) {
		subRules.add(sub);
		sub.addChangeListener(this);
		fireChanged();
	}
	
	public void removeSubRule(ProbeFilter sub) {
		subRules.remove(sub);
		sub.removeChangeListener(this);
		fireChanged();		
	}
	
	public Boolean passesFilter(Probe pb) {
		
		if (isInactive)
			return null;  //let parent ruleset decide what to do here.
		
		boolean pass = (combinationMode==COMBINE_AND);
		
		switch (combinationMode) {
		case COMBINE_AND:
			for (ProbeFilter pf : subRules) {
				Boolean p = pf.passesFilter(pb);				
				pass&=(p==null || p);  // explicit test needed to account for "null" values
				if (!pass) break; // fast eval
			}
			break;
		case COMBINE_OR:
			if (subRules.size()==0)
				pass=true;
			else
				for (ProbeFilter pf : subRules) {				
					Boolean p = pf.passesFilter(pb); 
					pass|=(p!=null && p);  //explicit test needed to account for "null" values
					if (pass) break; // fast eval
				}
			break;
		}
		
		return pass;
	}
	
	public String toString() {
		return (combinationMode==COMBINE_AND?"AND":"OR") + " ("+subRules.size()+" rules)";		
	}
	
	public int size() {
		int res=0;
		for (ProbeFilter p : subRules) {
			if (p instanceof RuleSet)
				res+=((RuleSet)p).size();
			else
				res++;
		}
		return res;
	}
	
	public int getCombinationMode() {
		return combinationMode;
	}
	
	
	
	public void addChangeListener( ChangeListener listener ) {
		eventListenerList.add( ChangeListener.class, listener );
	}


	public void removeChangeListener( ChangeListener listener ) {
		eventListenerList.remove( ChangeListener.class, listener );
	}

	public void fireChanged(  )
	{
		if (isSilent())
			return;
		
		Object[] l_listeners = this.eventListenerList.getListenerList();

		if (l_listeners.length==0)
			return;

		ChangeEvent ce = new ChangeEvent(this);

		// process the listeners last to first, notifying
		// those that are interested in this event
		for ( int i = l_listeners.length-2; i >= 0; i-=2 )  {
			if ( l_listeners[i] == ChangeListener.class )  {
				ChangeListener list = ((ChangeListener)l_listeners[i+1]);
				list.stateChanged(ce);
			}
		}
	}

	public void stateChanged(ChangeEvent arg0) {
		fireChanged(); // propagate changes up to root ruleset
	}

	public boolean isSilent() {
		return isSilent;
	}

	public void setSilent(boolean isSilent) {
		if (!isSilent && isSilent!=this.isSilent)  {
			this.isSilent = isSilent;
			fireChanged();
		} else {
			this.isSilent = isSilent;
		}
	}
	
	public String toHTMLString() {
		return "<html>"+
			(isInactive?"<s>":"")+			
			((getCombinationMode()==RuleSet.COMBINE_AND)?
			//AND:
			"<font color=#0000FF>ALL</font>" 
			:
			//OR:
			"<font color=#00FF00>ANY</font>"
			)+" of these "+subRules.size()+" rules";
	}

	public void setCombinationMode(int newMode) {
		if (newMode!=combinationMode) {
			this.combinationMode = newMode;
			fireChanged();
		}
	}

	public JPanel getOptionPanel() {
		return new RuleSetGUI(this);
	}

	public void setActive(boolean act) {
		if (act==isInactive) { // inverted semantics in both variables
			isInactive=!act;
			fireChanged();
		}
	}
	
	public boolean isActive() {
		return !isInactive;
	}

	public DynamicProbeList getDynamicProbeList() {
		return dynamicProbeList;
	}
	
	
	public StorageNode toStorageNode() {
		StorageNode sn = new StorageNode();
		sn.Name="RuleSet";
		sn.addChild("isActive", Boolean.toString(!this.isInactive));
		sn.addChild("CombinationMode", Integer.toString(this.getCombinationMode()));
		StorageNode subRuleNode = new StorageNode("SubRules","");
		sn.addChild(subRuleNode);
		int i=0;
		for (ProbeFilter subRule : subRules) {
			StorageNode subNode = new StorageNode(""+i,"");
			subNode.addChild(((StorageNodeStorable)subRule).toStorageNode());
			subRuleNode.addChild(subNode);
			++i;
		}
		return sn;
	}
	
	public void fromStorageNode(StorageNode sn) {
		if (sn.getChildren().size()==0) {
			System.err.println("DPL RuleSet cannot be constructed from empty StorageNode");
			return;
		}
		setSilent(true);		
		StorageNode subRuleNode = sn.getChild("SubRules");
		for (StorageNode subNode : subRuleNode.getChildren()) {
			StorageNode childNode = subNode.getChildren().iterator().next(); //exactly one child here
			StorageNodeStorable pf = null;
			if (childNode.Name.equals("RuleSet")) 
				pf = new RuleSet(getDynamicProbeList());
			if (childNode.Name.equals("Rule")) 
				pf = new Rule(getDynamicProbeList());
			if (pf!=null) {
				pf.fromStorageNode(childNode);
				addSubRule((ProbeFilter)pf);
			}
		}	
		this.setActive(Boolean.parseBoolean(sn.getChild("isActive").Value));
		this.setCombinationMode(Integer.parseInt(sn.getChild("CombinationMode").Value));
		setSilent(false);
	}
	
	public String toDescription() {
		String ret ="";
		for (ProbeFilter subRule : subRules) {
			ret += "["+subRule.toDescription()+"]";
			if (subRule!=subRules.getLast())
				ret+=getCombinationMode()==COMBINE_AND?" AND ":" OR";
		}
		return ret;
	}
	
	public void clear() {
		dispose();
		fireChanged();
	}
	
	public void dispose() {
		for (ProbeFilter r : subRules) {
			r.dispose();
			r.removeChangeListener(this);
		}
		subRules.clear();
	}
	
}
