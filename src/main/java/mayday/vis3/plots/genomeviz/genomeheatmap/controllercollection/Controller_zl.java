package mayday.vis3.plots.genomeviz.genomeheatmap.controllercollection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import mayday.vis3.plots.genomeviz.EnumManagerGHM.ZoomLevel;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.ComboObject;
import mayday.vis3.plots.genomeviz.genomeheatmap.usergestures.UserGestures;

public class Controller_zl implements ActionListener, ItemListener {

	protected GenomeHeatMapTableModel tableModel;
	protected Controller c;
	 
	public Controller_zl(GenomeHeatMapTableModel tableModel, Controller c){
		this.tableModel = tableModel;
		this.c = c;
	}

	
	public void actionPerformed(ActionEvent e) {
		
		if(UserGestures.ZOOM_MINUS_BUTTON.equals(e.getActionCommand())){
			c.zoomLevelChangedOperations(decrementOrIncrementZoomLevel(tableModel.getZoomLevel(), true));
		}
		else if(UserGestures.ZOOM_PLUS_BUTTON.equals(e.getActionCommand())){
			c.zoomLevelChangedOperations(decrementOrIncrementZoomLevel(tableModel.getZoomLevel(), false));
			
		} else if(UserGestures.Zoom_FIT_TO_WINDOW_BUTTON.equals(e.getActionCommand())){
			c.zoomLevelChangedOperations_fitted(ZoomLevel.fit);
		}	
	}
	
	public void itemStateChanged(ItemEvent e) {
		if(e.getStateChange() == ItemEvent.SELECTED){
			ComboObject obj = (ComboObject) e.getItem();
			if (obj != null) {
				ZoomLevel level = obj.getZoomLevel();
				c.zoomLevelChangedOperations(level);
			}
		}
		
	}
	
	protected ZoomLevel decrementOrIncrementZoomLevel(ZoomLevel level, boolean increment){
		
		switch(level){
		case one:
			if(increment) return ZoomLevel.two;
			else return ZoomLevel.one;

		case two:
			if(increment) return ZoomLevel.five;
			else return ZoomLevel.one;

		case five:
			if(increment) return ZoomLevel.ten;
			else return ZoomLevel.two;
			
		case ten:
			if(increment) return ZoomLevel.fifteen;
			else return ZoomLevel.five;

		case fifteen:
			if(increment) return ZoomLevel.twenty;
			else return ZoomLevel.ten;
			
		case twenty:
			if(increment) return ZoomLevel.twentyfive;
			else return ZoomLevel.fifteen;
			
		case twentyfive:
			if(increment) return ZoomLevel.fifty;
			else return ZoomLevel.twenty;
			
		case fifty:
			if(increment) return ZoomLevel.hundred;
			else return ZoomLevel.twentyfive;
			
		case hundred:
			if(increment) return ZoomLevel.twohundred;
			else return ZoomLevel.fifty;
			
		case twohundred:
			if(increment) return ZoomLevel.thousand;
			else return ZoomLevel.hundred;
			
		case thousand:
			if(increment) return ZoomLevel.twothousand;
			else return ZoomLevel.twohundred;
			
		case twothousand:
			if(increment) return ZoomLevel.fivethousand;
			else return ZoomLevel.thousand;
			
		case fivethousand:
			if(increment) return ZoomLevel.fivethousand;
			else return ZoomLevel.twothousand;
			
		case fit:
			if(increment){
				return getHigherZoomlevel(tableModel.getZoomMultiplikator());
			} else{
				return getLowerZoomlevel(tableModel.getZoomMultiplikator());
			}
		default:	
			System.err.println("ZoomLevelController: decrementOrIncrementZoomLevel - selected ZoomLevel " + level + " not applicable for this method." +
					"Default ZoomLevel 1x returned");
			return ZoomLevel.one;
		}
	}
	
	public ZoomLevel getHigherZoomlevel(int zoomMultiplikator){
		System.out.println("zoomMultiplikator " + zoomMultiplikator);
		if(zoomMultiplikator >= 5000 || zoomMultiplikator >= 2000){
			return ZoomLevel.fivethousand;
		} else if(zoomMultiplikator >= 1000){
			return ZoomLevel.twothousand;
		} else if(zoomMultiplikator >= 200){
			return ZoomLevel.thousand;
		} else if(zoomMultiplikator >= 100){
			return ZoomLevel.twohundred;
		} else if(zoomMultiplikator >= 50){
			return ZoomLevel.hundred;
		} else if(zoomMultiplikator >= 25){
			return ZoomLevel.fifty;
		} else if(zoomMultiplikator >= 20){
			return ZoomLevel.twentyfive;
		} else if(zoomMultiplikator >= 15){
			return ZoomLevel.twenty;
		} else if(zoomMultiplikator >= 10){
			return ZoomLevel.fifteen;
		} else if(zoomMultiplikator >= 5){
			return ZoomLevel.ten;
		} else if(zoomMultiplikator >= 2){
			return ZoomLevel.five;
		} else if(zoomMultiplikator > 1){
			return ZoomLevel.two;
		}  else {
			return ZoomLevel.one;
		}
	}
	
	public ZoomLevel getLowerZoomlevel(int zoomMultiplikator){

		if(zoomMultiplikator <= 2){
			return ZoomLevel.one;
		} else if(zoomMultiplikator <= 5){
			return ZoomLevel.two;
		} else if(zoomMultiplikator <= 10){
			return ZoomLevel.five;
		} else if(zoomMultiplikator <= 15){
			return ZoomLevel.ten;
		} else if(zoomMultiplikator <= 20){
			return ZoomLevel.fifteen;
		} else if(zoomMultiplikator <= 25){
			return ZoomLevel.twenty;
		} else if(zoomMultiplikator <= 50){
			return ZoomLevel.twentyfive;
		} else if(zoomMultiplikator <= 100){
			return ZoomLevel.fifty;
		} else if(zoomMultiplikator <= 200){
			return ZoomLevel.hundred;
		} else if(zoomMultiplikator <= 1000){
			return ZoomLevel.twohundred;
		} else if(zoomMultiplikator <= 2000){
			return ZoomLevel.thousand;
		} else if(zoomMultiplikator <= 5000){
			return ZoomLevel.twothousand;
		} else if(zoomMultiplikator > 5000){
			return ZoomLevel.fivethousand;
		} else {
			System.err.println("ZoomLevelController: getLowerZoomlevel - zoomMultiplikator " + zoomMultiplikator + " fits no case. " +
					"Default ZoomLevel 1x returned");
			return ZoomLevel.one;
		}
	}
}
