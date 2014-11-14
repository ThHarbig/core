package mayday.vis3;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mayday.core.DataSet;
import mayday.core.Mayday;
import mayday.core.ProbeList;
import mayday.core.datasetmanager.gui.DataSetManagerView;
import mayday.core.gui.PluginInfoMenuAction;
import mayday.core.gui.components.ExcellentBoxLayout;
import mayday.core.pluginrunner.ProbeListPluginRunner;
import mayday.core.plugins.menu.ProbeListMenu;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.GenericPlugin;
import mayday.vis3.gui.PluginSorter;

public class PluggableVisualizer extends AbstractPlugin implements GenericPlugin {

	protected static Component myComponent;

	@Override
	public void init() {
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				getClass(), 
				"PAS.pluggableUI.defaultViz", 
				null, 
				Constants.MC_PLUGGABLEVIEWS, 
				null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de",
				"Displays icons for quickly opening plots",
				"Quick visualization"
		);
	}

	@Override
	public void run() {
		if (myComponent==null) {
			myComponent = new JScrollPane(new QuickVizElement());
			((JScrollPane)myComponent).setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			((JScrollPane)myComponent).getVerticalScrollBar().setUnitIncrement(10);
		}
		Mayday.sharedInstance.addPluggableViewElement(myComponent, "Quick visualization");
	}

	@SuppressWarnings("serial")
	protected class QuickVizElement extends JPanel implements ListSelectionListener {

		public QuickVizElement() {
			setLayout(new FlowLayout());
			loadContent();						
		}

		@Override
		public void valueChanged(ListSelectionEvent e) { // via dataset selection
			loadContent();
		}

		public synchronized void loadContent() {
			List<DataSet> sel = DataSetManagerView.getInstance().getSelectedDataSets();
			removeAll();

			if (sel.size()==1) {
				List<Object> temp = PluginSorter.createPluginInfoList(true,true,true);
				setLayout(new ExcellentBoxLayout(true, 0));
				boolean firstCat = true;
				for (Object o : temp) {
					JButton b;
					if (o == null) 
						o = "";
					
					if (o instanceof String) {
						b = new JButton("<html><b>"+o+"&nbsp;");
						if (!firstCat)
							b.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
						else {
							firstCat = false;
							b.setBorder(BorderFactory.createEmptyBorder());
						}
						b.setEnabled(false);
					} else {
						b = new JButton(new RunPluginAction((PluginInfo)o));
						b.setBorder(BorderFactory.createEmptyBorder());
					}					
					b.setOpaque(true);
					b.setBackground(Color.white);
					b.setHorizontalAlignment(SwingConstants.LEFT);
					add(b);
				}
				invalidate();
				validate();
				setEnabled(true);
			}
		}

		public void addNotify() {
			DataSetManagerView.getInstance().addSelectionListener(this);
			super.addNotify();
		}

		public void removeNotify() {
			DataSetManagerView.getInstance().removeSelectionListener(this);
			super.removeNotify();
		}

	}

	@SuppressWarnings("serial")
	protected class RunPluginAction extends PluginInfoMenuAction {

		public RunPluginAction(PluginInfo pli) {
			super(pli);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			List<ProbeList> sel = ProbeListMenu.getCurrentSelection();
			ProbeListPluginRunner plpr = new ProbeListPluginRunner(getPlugin(), sel, null);
			plpr.execute();
		}

	}

}
