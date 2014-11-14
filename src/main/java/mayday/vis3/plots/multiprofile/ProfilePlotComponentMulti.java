package mayday.vis3.plots.multiprofile;

import java.util.LinkedList;
import java.util.List;

import mayday.core.ProbeList;
import mayday.vis3.plots.profile.ProfilePlotComponent;


@SuppressWarnings("serial")
public class ProfilePlotComponentMulti extends ProfilePlotComponent {
	
	private ProbeList pl;
	private List<ProbeList> probeLists;
	
	public ProfilePlotComponentMulti(ProbeList pl) {		
		this.pl=pl;
		getZoomController().setActive(false);
		probeLists = new LinkedList<ProbeList>();
		probeLists.add(pl);
	}
	
	public List<ProbeList> getProbeLists() {
		return probeLists;
	}
	
	public String getPreferredTitle() {
		if (pl!=null)
			return pl.getName();
		else 
			return "No ProbeList";
	}
	
	
}

