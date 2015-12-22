package mayday.core.probelistmanager.plug;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import mayday.core.probelistmanager.ProbeListManagerFactory;
import mayday.core.probelistmanager.ProbeListManagerTree;

public class PLMVChangeListener implements ActionListener {

	protected ProbeListManagerTree plm;
	protected PLMVPlugin view;
	
	public PLMVChangeListener(ProbeListManagerTree plm, PLMVPlugin view) {
		this.view=view;
		this.plm=plm;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		ProbeListManagerFactory.changeView(plm, view);
	}

}
