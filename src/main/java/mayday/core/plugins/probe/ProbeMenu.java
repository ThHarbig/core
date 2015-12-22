package mayday.core.plugins.probe;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.gui.PluginMenu;
import mayday.core.pluginrunner.ProbePluginRunner;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.MenuPlugin;

@SuppressWarnings("serial")
@PluginManager.IGNORE_PLUGIN
public class ProbeMenu extends AbstractPlugin implements MenuPlugin {

	protected Collection<Probe> probes;
	protected MasterTable masterTable;
	protected AbstractAction selectionCounter = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {}
	};
	
	private PluginMenu<Probe> dm = new PluginMenu<Probe>("Probe", Constants.MC_PROBE) {

		@Override
		public void callPlugin(PluginInfo pli, List<Probe> selection) {
			runProbePlugin(pli);			
		}
		
		protected void updateSelection() {
			String pN = "no probe selected!";
			if (probes.size()>0)
				pN = probes.iterator().next().getDisplayName();
			if (probes.size()>1)
				pN += " ... ("+probes.size()+" probes)";

			selectionCounter.putValue(Action.NAME, pN);
			selectionCounter.setEnabled(false);
		}
		
	};


	public ProbeMenu() {
		// for Pluma
	}

	public ProbeMenu(Collection<Probe> _probes, MasterTable _masterTable) {
		probes=_probes;
		masterTable=_masterTable;
		initMenu();	
	}

	protected void initMenu() {
		dm.setMnemonic( KeyEvent.VK_P );
		dm.add(selectionCounter, false);

		dm.fill();
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return null;
	}


	public JMenu getMenu() {
		return dm;
	}

	public JPopupMenu getPopupMenu() {
		return dm.getPopupMenu();
	}

	public int getPreferredPosition() {
		return 0;
	}

	public void runProbePlugin(PluginInfo pli) {
		ProbePluginRunner pr = new ProbePluginRunner(pli, new ArrayList<Probe>(probes), masterTable.getDataSet());
		pr.execute();
	}

	public void init() {
	}
	

}