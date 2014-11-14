package mayday.core.structures.trees.painter;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import mayday.core.structures.trees.layout.Layout;
import mayday.core.structures.trees.screen.ScreenLayout;
import mayday.core.structures.trees.tree.ITreePart;

/**
 * paints the tree
 * @author Andreas Friedrich, Florian Battke
 *
 */
@SuppressWarnings("serial")
public class TreePainterPanel extends JPanel {
	
	protected TreePainter painter;
	
	public TreePainterPanel() {
		painter = new TreePainter();
		
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				ITreePart t = painter.nearestObject(me.getX(),me.getY());
				if(t!=null)
					painter.getScreenLayout().toggleSelect(t);
				else
					painter.getScreenLayout().clearSelected();
				repaint();
			}			
		});
	}
	
	/**
	 * uses a Layout to paint each Node and each Edge
	 * 
	 */
	public void paint(Graphics g) {
		super.paint(g);
		painter.paint((Graphics2D)g, getSize());
	}

	public void setLayoutForPainting(Layout layout) {
		painter.setLayoutForPainting(layout);
	}
	
	public ScreenLayout getLayoutForPainting() {
		return painter.getScreenLayout();
	}
	
}
