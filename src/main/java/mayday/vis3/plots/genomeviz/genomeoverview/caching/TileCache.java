package mayday.vis3.plots.genomeviz.genomeoverview.caching;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class TileCache {
	
	protected  HashMap<Integer, BufferedImage> tiles = new HashMap<Integer, BufferedImage>();
	protected int tileSize;
	
	public void setTileSize(int ts) {
		if (tileSize!=ts) {
			tileSize = ts;
			dropTiles();
		}
	}
	
	public int getTileSize() {
		return tileSize;
	}
	
	public boolean isTiling() {
		return tileSize>0;
	}
	
	public void dropTiles() {
		tiles.clear();
	}
	
	public void updateMissingTiles(List<Integer> requiredTiles) {
		Iterator<Integer> i = requiredTiles.iterator();
		tiles.keySet().retainAll(requiredTiles);
		while (i.hasNext())
			if (tiles.containsKey(i.next()))
				i.remove();
	}
	
	public BufferedImage getImageForRegion(int startX, int endX, int height) {
		int width = endX-startX+1;
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g2d = (Graphics2D)bi.getGraphics();
		renderRegion(g2d, startX, endX, 0, height);
		return bi;
	}
	
	public void renderRegion(Graphics2D target, int startX, int endX, int targetDeltaX, int height) {
		AffineTransform before = target.getTransform();
		target.translate(targetDeltaX, 0);
		if (tileSize==0) {
			BufferedImage tile = tiles.get(null);
			if (tile!=null) {
				renderRegionPart(target, 0,tile,startX,endX,height);
			}
		} else {
			int s = startX/tileSize;
			int e = endX/tileSize;
			for (int tileID = s; tileID<=e; ++tileID) {
				BufferedImage tile = tiles.get(tileID*tileSize);
				if (tile!=null) {
//					Graphics g = tile.getGraphics();
//					g.setColor(Color.RED);
//					g.drawLine(0, 0, tile.getWidth(), tile.getHeight());
					int delta = tileID*tileSize;
					renderRegionPart(target, delta, tile, startX,endX,height);
				}
			}
		}
		target.setTransform(before);
	}
	
	protected void renderRegionPart(Graphics2D target, int tileDelta, BufferedImage tile, int startX, int endX, int height) {
		int tileStart = tileDelta;
		int tileWidth = tileSize!=0?tileSize:tile.getWidth();
		int tileEnd = tileSize!=0 ? tileDelta+tileSize : tile.getWidth();		
		int startDelta = startX - tileStart;
		int endDelta = tileEnd - endX;
		int delta_start_in_tile = Math.max(0, startDelta);
		int delta_start_in_target = -Math.min(0, startDelta);
		int delta_end_in_tile = Math.max(0, endDelta);
		int delta_end_in_target = -Math.min(0, endDelta);
		int dx1 = delta_start_in_target+startX;
		int dx2 = endX-delta_end_in_target;
		int sx1 = delta_start_in_tile;
		int sx2 = tileWidth - delta_end_in_tile; 		
		target.drawImage(tile, dx1,0,dx2,height, sx1,0,sx2,height, null);
	}
				
	public Graphics2D getGraphicsForTile(int startX, int height, int totalwidth) {					
		Integer tileID = (tileSize==0)?null:startX;
		BufferedImage bi = tiles.get(tileID);
		if (bi==null) {
//			System.out.println("Creating tile "+tileID);
			tiles.put(tileID, bi = new BufferedImage(
					tileSize==0?totalwidth:tileSize,
					height, 
					BufferedImage.TYPE_INT_ARGB));
//			Graphics2D g2d = (Graphics2D)bi.getGraphics();		
//			g2d.setBackground(Color.LIGHT_GRAY);
//			g2d.clearRect(0, 0, bi.getWidth(), bi.getHeight());
		}
		Graphics2D g2d = (Graphics2D)bi.getGraphics();		
		// add translation to the tile start
		if (tileID!=null)
			g2d.translate(-startX, 0);
		return g2d;
	}
	
}