package mayday.vis3.legend;

import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;

@SuppressWarnings("serial")
public class ProbeListLegendItem extends LegendItem {

	private ProbeList probeList;
	private ProbeListListener listener;

	public ProbeListLegendItem(ProbeList pl) {
		super(pl.getColor(), "");
		probeList=pl;
		pl.addProbeListListener(listener = new ProbeListListener() {
			public void probeListChanged(ProbeListEvent event) {
				updateItem();
			}
		});
		updateItem();
	}
	
	public void updateItem() {
		if (probeList == null)
			return;
		colorBox.setBackground(probeList.getColor());
		name.setText("<html>"+probeList.getName()+
				(probeList.getNumberOfProbes()>1?" <font size=-2>("+probeList.getNumberOfProbes()+")  ":"")
			);
	}
	
	public void removeNotify() {
		super.removeNotify();
		probeList.removeProbeListListener(listener);
	}
}