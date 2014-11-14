package mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures;

import mayday.vis3.plots.genomeviz.EnumManagerGHM.ZoomLevel;
import mayday.vis3.plots.genomeviz.genomeheatmap.delegates.TableComputations;

public class ComboObject implements Comparable<Object>{
 
	protected String text;
	protected Integer zoom;
	protected ZoomLevel level;
	
	public ComboObject(String text, ZoomLevel zoomlevel){
		this.text = text;
		this.level = zoomlevel;
		this.zoom = TableComputations.getZoomMultiplikator(level);
		
	}
	
	public String toString(){
		return text;
	}
	
	public String getName() {
		return text; 
	}
	
	public Integer getValue(){
		return zoom;
	}
	
	public ZoomLevel getZoomLevel(){
		return level;
	}

	public int compareTo(Object comboObject) {
		if (comboObject==null) 
			return -1;

		if(!(comboObject instanceof ComboObject))
		{
			return (-1); 
		}

		return this.getValue().compareTo(
				((ComboObject)comboObject).getValue()
		);
	}

}
