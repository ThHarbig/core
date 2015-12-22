package mayday.core.meta.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import mayday.core.gui.PluginMenu;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.pluginrunner.MetaInfoPluginRunner;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.MenuPlugin;

@PluginManager.IGNORE_PLUGIN
public class MIMenu extends AbstractPlugin implements MenuPlugin {

	protected MIGroupSelectionPanel selPanel;
	protected JDialog parent;
	protected MIManager mimanager;
	protected PluginMenu<MIGroup> dm;
	
	public MIMenu() {
		// for Pluma
	}
		
	@SuppressWarnings("serial")
	public MIMenu(MIGroupSelectionPanel myPanel, MIManager mim, JDialog parent) {
		selPanel = myPanel;
		this.parent = parent;
		mimanager = mim;
		dm = new PluginMenu<MIGroup>("MIO Groups", Constants.MC_METAINFO_PROCESS) {

			@Override
			public void callPlugin(PluginInfo pli, List<MIGroup> selection) {
				runMetaInfoPlugin(pli, selection);
			}
			
			public List<MIGroup> getSelection() {
				return selPanel.getSelection();
			}
			
		};
		initMenu();	
	}
	
	protected void initMenu() {
		dm.setMnemonic( KeyEvent.VK_G );

		try{
			dm.add(new ShowPropertiesOption(), false);
		} catch (Exception e) {}
		try{
			dm.add(new MoveOption(), false);
		} catch (Exception e) {}
		try{
			dm.add(new DeleteOption(), false);
		} catch (Exception e) {}
		dm.addSeparator();

		dm.fill();
	}
	
	protected boolean modalParent() {
		return (parent!=null && parent.isModal()); 
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


	
	
	@SuppressWarnings("serial")
	public class ShowPropertiesOption extends AbstractAction {
		public ShowPropertiesOption() throws Exception {
			super( "Properties" );
		}
		public void actionPerformed(ActionEvent a) {
			AbstractPropertiesDialog dlg;
			dlg = PropertiesDialogFactory.createDialog(selPanel.getSelection().toArray());
			if (parent!=null)
				dlg.setModal(parent.isModal());
			dlg.setVisible(true);
		}
	}

	@SuppressWarnings("serial")
	public class DeleteOption extends AbstractAction {
		public DeleteOption() throws Exception {
			super( "Delete Group(s)..." );
		}
		public void actionPerformed(ActionEvent a) {
			if (JOptionPane.showConfirmDialog(parent, "Really delete "+selPanel.getSelection().size()+" groups?", 
					"Confirm Deletion", JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)
				return;
			for (MIGroup mg : selPanel.getSelection())
				mimanager.removeGroup(mg);
		}
	}

	@SuppressWarnings("serial")
	public class MoveOption extends AbstractAction {
		public MoveOption() throws Exception {
			super( "Move Group(s)..." );
		}
		public void actionPerformed(ActionEvent a) {
			MIMoveDialog mmd = new MIMoveDialog(mimanager);
			mmd.setModal(true);
			mmd.setVisible(true);
			if (!mmd.isCanceled()) {
				String tgt = mmd.getTarget();
				for (MIGroup mg : selPanel.getSelection()) {
					mimanager.moveGroupInTree(mg, tgt);
				}
			}
		}
	}
	
	public void runMetaInfoPlugin(PluginInfo pli, List<MIGroup> selection) {
		// ignore the selection for now
		MetaInfoPluginRunner mipr = new MetaInfoPluginRunner(pli, selPanel.getSelection(), mimanager);
		mipr.execute();
	}


	@Override
	public void init() {
	}

}