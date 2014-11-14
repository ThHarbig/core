package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.border.Border;

import mayday.vis3.plots.genomeviz.EnumManagerGO.ActionModes;
import mayday.vis3.plots.genomeviz.genomeoverview.caching.TileCache;

public interface ITrack{

	public void init();

	public AbstractTrackPlugin getTrackPlugin();
	
	public void resetActionMode();
	
	public void setActionMode(ActionModes mode);
	
	public ActionModes getActionMode();
	
	public int getPositionInPane();
	
	public Point getLocation();
	
	public int getIndexInPane();
	
	public void repositionPanel();
	
	public void setDeleteFlag(boolean b);

	public boolean getDeleteFlag();
	
	public void setIndex(int newIndex);
	
	public void setLocationInPanel(int positionInPanel);
	
	public void resizeTrackwidth();
	
	public void deleteBufferedImage();
	
	public void setPreviousTrackLocation(MouseEvent e, ITrack track);
	
	public void moveComponentByDragging();
	
	public void movePanelToFront(int button);

	public void movePanelByRealeased(boolean pressedflag);
	
	public BufferedImage getBufferedImage();
	
	//###############################################
	//
	//	Operations on colorprovider
	//
	//###############################################

	public void setNewLabel(String text);

	public void paintRange(int[] range);

	public void paint(Graphics g);

	public void setLocationOfUserpanel();

	public boolean isMovedFlag();

	public void setMovedFlag(boolean movedFlag);

	public boolean isDraw();

	public void setDrawing(boolean b);

	public void setBorder(Border object);

	public Object getParent();

	public void resizeTrackheight(int newHeight);

	public void setBufferedImage(BufferedImage img);

	public void repaint();

	public int getWidth();
	
	public int getHeight();
	
	// Tiling
	public void setTileCache(TileCache c);
	
	public TileCache getTileCache();

}
