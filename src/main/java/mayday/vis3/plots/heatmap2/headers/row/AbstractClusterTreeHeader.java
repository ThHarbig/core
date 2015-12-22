package mayday.vis3.plots.heatmap2.headers.row;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.settings.Setting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.structures.trees.layout.Layout;
import mayday.core.structures.trees.layouter.TopDownDendrogram;
import mayday.core.structures.trees.painter.TreePainter;
import mayday.core.structures.trees.painter.edge.DendrogramEdges;
import mayday.core.structures.trees.screen.ScreenLayout;
import mayday.core.structures.trees.tree.Node;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.headers.AbstractHeaderPlugin;
import mayday.vis3.plots.heatmap2.interaction.UpdateEvent;

public abstract class AbstractClusterTreeHeader extends AbstractHeaderPlugin implements ChangeListener {

	protected TreePainter painter;
	protected Node root;
	protected Layout l;
	protected TopDownDendrogram dendrolayouter;

	protected BooleanSetting extendEdges = new BooleanSetting("Extend edges", "Extend edges (will distort tree distances)", true);
	
	protected HeatmapStructure data;
	
	protected int size;
	protected boolean topDown;
	
	protected long lastModified=-1;


	protected void init(boolean topDown) {
		painter = new TreePainter();
		dendrolayouter = new TopDownDendrogram();
		dendrolayouter.setTopDown(topDown);
		produceLayout();
	}

	protected abstract int getFirstIndex();

	public void produceLayout() {
		if (dendrolayouter==null || painter==null) {
			painter = new TreePainter();
			dendrolayouter = new TopDownDendrogram();
			dendrolayouter.setTopDown(topDown);
		}
		
		if (root==null) {
			painter.setLayoutForPainting(dendrolayouter.doLayout(new Node("Error parsing tree",null)));
			return;
		}
		
		HashMap<Node, Double> leafmap = new HashMap<Node, Double>();
		Double coord;

		int idx=getFirstIndex();
		double myFirstPos = (dendrolayouter.isTopDown()?data.getColStart(idx):data.getRowStart(idx));
		
		for (Node n : root.getLeaves(null)) {
			if (dendrolayouter.isTopDown())
				coord = data.getColStart(idx)+data.getColWidth(idx)/2.0; 
			else
				coord =  data.getRowStart(idx)+data.getRowHeight(idx)/2.0; 
			leafmap.put(n, coord - myFirstPos);
			++idx;
		}
		l = dendrolayouter.doLayout(root, leafmap);
		painter.setLayoutForPainting(l);
		ScreenLayout sl = painter.getScreenLayout();

		sl.getEdgeLayouts().getDefaultLayout().setColor(Color.BLACK);
		sl.getNodeLayouts().getDefaultLayout().setWidth(0);
		sl.getNodeLayouts().getDefaultLayout().setHeight(0);		
		
		((DendrogramEdges)sl.getEdgeLayouts().getDefaultLayout().getPainter()).selectionColor = Color.red;
		
		sl.setMinimalMargin(0);
	}
	
	@Override
	public MouseListener getMouseListener() {
		return null;		
	}

	@Override
	public MouseMotionListener getMouseMotionListener() {
		return null;
	}

	@Override
	public MouseWheelListener getMouseWheelListener() {
		return new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int CONTROLMASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
				if ((e.getModifiers()&CONTROLMASK) == CONTROLMASK) {
					if (e.getWheelRotation()>0 && size>10)
						size-=20;
					if (e.getWheelRotation()<0 && size<1000)
						size+=20;
					fireChange(UpdateEvent.SIZE_CHANGE);
				}
			}

		};
	}

	@Override
	public int getSize() {
		return size;
	}
	
	public Setting getSetting() {
		return extendEdges;
	}

	protected abstract long getCurrentModificationCount();
	protected abstract Node getNewTree();
	
	public void render0(Graphics2D g) {		
		g.setBackground(Color.white);
		Rectangle2D oldClip = g.getClipBounds();
		g.clearRect(0, 0, (int)oldClip.getWidth(), (int)oldClip.getHeight());
		if (painter!=null) {
			long curModCount = getCurrentModificationCount();
			if (curModCount!=lastModified) {
				produceLayout();
				lastModified = curModCount;
			}
			painter.paint(g, new Dimension((int)oldClip.getWidth(), (int)oldClip.getHeight()));
		}
	}


	public void stateChanged(ChangeEvent e) {
		boolean hasHadTree = root!=null;
		boolean willHaveTree = getNewTree()!=null;
		boolean changed = (hasHadTree!=willHaveTree) || (hasHadTree && willHaveTree && getNewTree()!=root);
		if (changed)
			if (willHaveTree) {
				root = getNewTree();
				size = 100;						
				init(topDown);
			} else {
				root = null;
				size = 0;
			}
		fireChange(UpdateEvent.SIZE_CHANGE);

	}

}
