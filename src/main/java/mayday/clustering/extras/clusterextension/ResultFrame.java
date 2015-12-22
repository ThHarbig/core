package mayday.clustering.extras.clusterextension;

import java.awt.BorderLayout;
import java.util.HashMap;

import javax.swing.JScrollPane;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.MaydayFrame;
import mayday.vis3.model.Visualizer;

@SuppressWarnings("serial")
public class ResultFrame extends MaydayFrame { 

	public ResultFrame(final Visualizer viz, HashMap<Probe, ProbeList> mapProbeProbeList){
		setLayout(new BorderLayout());
		setTitle("Cluster Extension: Results");
		setSize(500,300);
		
		ResultTable rt = new ResultTable(viz, mapProbeProbeList);
		JScrollPane scrollPane =  new JScrollPane(rt);
		add(scrollPane, BorderLayout.CENTER);
		
		pack();		
	}	
	
}
