package mayday.vis3.plots.trees;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenu;

import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.structures.trees.io.LayoutedNewick;
import mayday.core.structures.trees.io.PlainNewick;
import mayday.core.structures.trees.layout.Layout;
import mayday.core.structures.trees.layouter.TreeLayoutPlugin;
import mayday.core.structures.trees.painter.TreePainter;
import mayday.core.structures.trees.screen.ScreenLayout;
import mayday.core.structures.trees.tree.Edge;
import mayday.core.structures.trees.tree.ITreePart;
import mayday.core.structures.trees.tree.Node;
import mayday.vis3.gui.PlotComponent;
import mayday.vis3.gui.PlotContainer;

public abstract class TreeDelegate {

	protected ScreenLayout sl;
	protected TreePainter painter;
	protected MouseListener ml;
	protected TreePainterSetting setting;

	
	public TreeDelegate( Node tree, TreePainterSetting Setting , TreePainter Painter) {
		this.painter = Painter;
		this.setting = Setting;
		init(setting.getLayouter().doLayout(tree));
	}
	
	public void replaceTree(Node tree) {
		init(setting.getLayouter().doLayout(tree));
	}
	
	protected void init(Layout l) {
		setLayoutForPainting(l);
		applyLabelSettings();
		ml = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				switch (e.getButton()) {
				case MouseEvent.BUTTON1:
					handleButton1(e);
					break;
				case MouseEvent.BUTTON3:
					handleButton3(e);
					break;
				}
			}
		};

		setting.getLayouterSetting().addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				TreeLayoutPlugin tlp = setting.getLayouter();
				Layout l = tlp.doLayout(sl.getRoot());
				setLayoutForPainting(l);
				applyLabelSettings();
			}
		});
		
		setting.getIgnoreEdgeLengths().addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				TreeLayoutPlugin tlp = setting.getLayouter();
				Layout l = tlp.doLayout(sl.getRoot());
				setLayoutForPainting(l);
				applyLabelSettings();
			}
		});

		setting.getNodeLabelSetting().addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				applyLabelSettings();
			}
		});
		setting.getEdgeLabelSetting().addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				applyLabelSettings();
			}
		});
	}
	
	public TreeDelegate( String newick, TreePainterSetting Setting , TreePainter Painter) {
		StringReader sr = new StringReader(newick);
		BufferedReader br = new BufferedReader(sr);
		Layout l;
		try {			
			LayoutedNewick ln = new LayoutedNewick();
			l = new LayoutedNewick().parseWithLayout(br);

			// use loaded layout or make new one, update settings accordingly
			if (ln.isDefaultLayout()) {
				l = setting.getLayouter().doLayout(l.getRoot());
			} else {
				setting.setLayouter(l.getLayouter());
			}
			init(l);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	protected void applyLabelSettings() {
		sl.getNodeLayouts().getDefaultLayout().setLabelVisible(
				setting.getNodeLabelSetting().getBooleanValue()
		);
		sl.getEdgeLayouts().getDefaultLayout().setLabelVisible(
				setting.getEdgeLabelSetting().getBooleanValue()
		);
	}

	protected void setLayoutForPainting(Layout l) {
		painter.setLayoutForPainting(l);
		sl = painter.getScreenLayout();
		sl.getEdgeLayouts().getDefaultLayout().setColor(Color.black);
		sl.getEdgeLayouts().getDefaultLayout().setWidth(2);
	}

	public MouseListener getMouseListener() {
		return ml;
	}

	protected void handleButton1(MouseEvent e) {

		ITreePart t = painter.nearestObject(e.getX(),e.getY());

		if (t!=null) {

			if (t instanceof Edge)
				if (handleEdgeSelection((Edge)t))
					return;

			int CONTROLMASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
			if ((e.getModifiers()&CONTROLMASK) == CONTROLMASK) {
				painter.getScreenLayout().toggleSelect(t);
			} else {
				painter.getScreenLayout().clearSelected();
				painter.getScreenLayout().setSelected(t, true);
			}
		}
	}

	protected void handleButton3(MouseEvent e) {};

	protected boolean handleEdgeSelection(Edge e) {
		return false;
	}

	public ScreenLayout getScreenLayout() {
		return sl;
	}

	public TreePainter getPainter() {
		return painter;
	}

	@SuppressWarnings("deprecation")
	public void setupMenus(PlotContainer pc, PlotComponent askingObject) {
		JMenu fileMenu = pc.getMenu(PlotContainer.FILE_MENU, askingObject);
		fileMenu.add(new StoreAction(false));
		fileMenu.add(new StoreAction(true));
	}

	@SuppressWarnings("serial")
	protected class StoreAction extends AbstractAction {

		protected boolean savelayout;

		public StoreAction(boolean withLayout) {
			super(!withLayout?"Save tree in newick format...":"Save tree with layout information...");
			savelayout = withLayout;
		}

		public void actionPerformed(ActionEvent e) {
			JFileChooser jfc = new JFileChooser();
			jfc.setDialogTitle("Save the tree "+(savelayout?"and layout":""));
			jfc.setDialogType(JFileChooser.SAVE_DIALOG);
			int returnVal = jfc.showSaveDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File targetFile = jfc.getSelectedFile();
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter(targetFile));
					if (savelayout)
						new LayoutedNewick().serialize(painter.getScreenLayout(), null, bw);
					else
						new PlainNewick().serialize(painter.getScreenLayout().getRoot(), null, bw);
					bw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

}
