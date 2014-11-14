package mayday.vis3.plots.genomeviz.genomeheatmap.probeinformation;

import java.util.HashMap;

import mayday.core.Probe;
import mayday.vis3.plots.genomeviz.genomeheatmap.additional.MasterManager;

public class ProbeInformation {
 
	protected MasterManager master;
	
	protected HashMap<Probe,String> informationSet;
	protected HashMap<String,String> informationNameSet;
	
	public ProbeInformation(MasterManager master){
		this.master = master;
		informationSet = new HashMap<Probe,String>();
		informationNameSet = new HashMap<String,String>();
	}
	
	public String getInformationAboutProbe(Probe probe){
		String information = "Probe name: " + probe.getDisplayName() +"\n" + "Startposition: " + 
		master.getStartPosition(probe)+ "\n" + "Endposition: " + master.getEndPosition(probe) + "\n" +
			"Additional information: " + "\n" + 
			"No additional information available";
		if(!informationSet.isEmpty()){
			if(informationSet.containsKey(probe)){
				information = informationSet.get(probe);
			}
		}
		return information;
	}
	
	public String getInformationAboutProbe(String name){
		String information = "<html><body>Probes:<br>Chromosome name: " + name + "<br>" + "Startposition: " + 
				"No information available sapfnhasopfho  wpnvfl  wap<fnhpwanvf jw fo�ibns<a v" +
				"sdaflanfowbgoiwbgn" +
				"asogb�gobawo�gib" +
				"awpgnwo�ginaw�gobn</body></htlm>";
		if(informationNameSet.containsKey(name)){
			information = informationNameSet.get(name);
		}
		return information;
	}
}
