package mayday.vis3.gradient.gui.setuppers;

import java.util.Collection;
import java.util.LinkedList;

import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.PredefinedGradients;
import mayday.vis3.gradient.agents.AbstractAgent;
import mayday.vis3.gradient.agents.Agent_Tricolore;
import mayday.vis3.gradient.gui.GradientList;

public class SetupAgentList extends GradientList {

	public SetupAgentList() {
		super(true);
	}
	
	public void fill() {
		for (Object o : getGradients())
			dlm.addElement(o);
	}

	@Override
	public void updateFromGradient(ColorGradient c,
			boolean overrideEverything) {
		silent=!overrideEverything;
		// find out if this is exactly one gradient from the list
		AbstractAgent inAgent = c.getAgent();
		for (int i=0; i!=dlm.getSize(); ++i) {
			ColorGradient cg = ((ColorGradient)dlm.get(i));
			if (inAgent.equals(cg.getAgent())) 
				theList.setSelectedIndex(i);					
			AbstractAgent agent = cg.getAgent(); //keep the agent
			cg.copySettings(c);
			cg.setAgent(agent); // keep the agent
		}
		theList.repaint();
		silent=false;
	}

	public void modifyGradient(ColorGradient c) {
		ColorGradient ret = (ColorGradient)theList.getSelectedValue();
		if (ret!=null)
			c.setAgent(ret.getAgent().clone());		
	}
	
	protected Collection<ColorGradient> getGradients() {

		LinkedList<ColorGradient> lcg = new LinkedList<ColorGradient>();
		
		ColorGradient cg = ColorGradient.createRainbowGradient(0d, 16d);
		lcg.add(cg);	
		
		for (PredefinedGradients pg : PredefinedGradients.values()) {
			ColorGradient ng = new ColorGradient(0d, 8d, 16d, true, 1024, 
					pg.getMid()!=null?ColorGradient.MIDPOINT_MODE.Center:ColorGradient.MIDPOINT_MODE.Minimum, 
							new Agent_Tricolore(false, pg.getLower(), pg.getMid(), pg.getUpper(), 5d));
			ng.setName(pg.toString());
			lcg.add(ng);
		}
		return lcg;
	}

}
