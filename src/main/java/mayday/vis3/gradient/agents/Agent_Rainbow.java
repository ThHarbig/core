/*
 * Created on Aug 21, 2005
 *
 */
package mayday.vis3.gradient.agents;

import mayday.core.gui.GUIUtilities;

public class Agent_Rainbow extends AbstractAgentArray {

	public Agent_Rainbow clone() {
		return new Agent_Rainbow();
	}

	public void updateColors() {
		colors = GUIUtilities.rainbow2(parent.getResolution(), 1);
	}

	public boolean equals(AbstractAgent otherAgent) {
		return otherAgent.getClass()==this.getClass();
	}

	public void deserialize(String s) {
		//void
	}

	@Override
	public String serialize() {
		return "";
	}
	
}