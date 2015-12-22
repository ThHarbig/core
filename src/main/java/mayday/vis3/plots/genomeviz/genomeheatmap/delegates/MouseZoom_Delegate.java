package mayday.vis3.plots.genomeviz.genomeheatmap.delegates;

public class MouseZoom_Delegate {
	
	public static int execute_boxSizeX(boolean inZoom, boolean resizeWidth, boolean resizeHeight, int _boxSizeX, int _minzoom, int _maxzoom){
		int delta = inZoom?5:-5;
		int xdelta = resizeWidth?delta:0;
		
		int boxSizeX = _boxSizeX;
		int minzoom = _minzoom;
		int maxzoom = _maxzoom;
		boxSizeX += xdelta;
		
		// check if size of boxes is still in range
		boxSizeX = (boxSizeX>maxzoom)?maxzoom:boxSizeX;
		boxSizeX = (boxSizeX<minzoom)?minzoom:boxSizeX;
		
		return boxSizeX;
	}
	
	public static int execute_boxSizeY(boolean inZoom, boolean resizeWidth, boolean resizeHeight, int _boxSizeY, int _minzoom, int _maxzoom){
		int delta = inZoom?5:-5;
		int ydelta = resizeHeight?delta:0;
		
		int boxSizeY = _boxSizeY;
		int minzoom = _minzoom;
		int maxzoom = _maxzoom;
		boxSizeY += ydelta;
		
		// check if size of boxes is still in range
		boxSizeY = (boxSizeY>maxzoom)?maxzoom:boxSizeY;
		boxSizeY = (boxSizeY<minzoom)?minzoom:boxSizeY;
		
		return boxSizeY;
	}
}
