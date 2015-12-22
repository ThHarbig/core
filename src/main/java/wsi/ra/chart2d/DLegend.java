package wsi.ra.chart2d;

import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import wsi.ra.plotting.FunctionArea;

/**
 * @author Nastasja Trunk 
 */
public class DLegend{
	JLabel[] legend;
	FunctionArea area;
	
	public DLegend(FunctionArea area){
		this.area = area;
		legend = new JLabel[area.getNumberOfGraphs()];
		init();
	}
	
	/**
	 * Initialization function for the legend which is collecting all the graph information
	 */
	private void init(){
		int size = area.getNumberOfGraphs();
        HashSet<Integer> graphlabels = new HashSet<Integer>();
		graphlabels = area.getGraphLabels();
		Iterator<Integer> it = graphlabels.iterator();

		for(int i=0; i!= size; ++i){
			if(it.hasNext()){
				int j = it.next();
				System.out.println("Delegend " + "GraphLabel " + j);
			JLabel tmp = new JLabel(area.getGraphInfo(j), new DLegendIcon(area.getColorGraph(j), 10, 10), SwingConstants.CENTER);
			//Debug
			System.out.println("Delegend: " + "Graphlabel " + j + " Name " +  area.getGraphInfo(j));
			legend[i] = tmp;
			}
			
		}
		
	}
	
	/**
	 * returns a collection of JLabels which include discription and color of the graphs included in the constructing FunctionArea
	 * @return
	 */
	public JLabel[] getLegend(){
		return this.legend;
	}

	

}
