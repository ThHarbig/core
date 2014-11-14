package mayday.core.probelistmanager;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import mayday.core.DataSet;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.probelistmanager.gui.ProbeListManagerView;
import mayday.core.probelistmanager.gui.ProbeListManagerViewDefault;
import mayday.core.probelistmanager.plug.PLMVChangeListener;
import mayday.core.probelistmanager.plug.PLMVPlugin;
import mayday.core.probelistmanager.search.ProbeListSearchPanel;

public class ProbeListManagerFactory {

	public static PLMVPlugin defaultPLMVchoice; 
	public static List<PLMVPlugin> VIEWS;
	
	
	
	public static ProbeListManager newManagerInstance(DataSet ds) {
		return new ProbeListManagerTree(ds);
//		return new ProbeListManagerList(ds);
	}

	public static ProbeListManagerView getManagerViewInstance(final ProbeListManager plm) {
		lazyInit();
		ProbeListManagerView plmv;
		if (plm instanceof ProbeListManagerTree) {			
			plmv = changeView((ProbeListManagerTree)plm, defaultPLMVchoice);
		} else {
			plmv = new ProbeListManagerViewDefault(plm);
			addListener(plm, plmv);
			setupGUI(plm, plmv, defaultPLMVchoice);
		}
		
		return plmv;
	}
	
	public static List<PLMVPlugin> getViews(ProbeListManager plm) {
		if (!(plm instanceof ProbeListManagerTree))
			return Collections.emptyList();
		lazyInit();
		return VIEWS;
	}
	
	protected static void lazyInit() {
		if (VIEWS!=null)
			return;
		VIEWS = new ArrayList<PLMVPlugin>();
		for (PluginInfo pli : PluginManager.getInstance().getPluginsFor(PLMVPlugin.MC))
			VIEWS.add((PLMVPlugin)pli.newInstance());
		if (defaultPLMVchoice==null)
			defaultPLMVchoice = VIEWS.get(0);
	}
	
	public static ProbeListManagerView changeView(ProbeListManagerTree plm, PLMVPlugin view) {
		ProbeListManagerView plmv = plm.getOpenProbeListManagerView(view);
		if (plmv==null) {
			plmv = view.createView(plm);
			addListener(plm, plmv);
		}
		setupGUI(plm, plmv, view);
		return plmv;
	}

	protected static void setupGUI(final ProbeListManager plm, ProbeListManagerView plmv, PLMVPlugin plug) {
		((ProbeListManagerTree)plm).addOpenProbeListManagerView(plmv, plug);
		plm.setProbeListManagerView(plmv);
		plm.getDataSet().getDataSetView().setProbeListManagerView(plmv);
	}

	public static Component getChangeComponent(ProbeListManager plm) {
		
//		protected class PopupAction extends AbstractAction
//		{
//			public PopupAction()
//			{
//				super("More...");
//			}
//
//			public void actionPerformed(ActionEvent e)
//			{
//				JComponent component = (JComponent)e.getSource();
//				extenderPopup.show(component,0,component.getHeight());
//			}
//		}
		
		if (plm instanceof ProbeListManagerTree) {
			ProbeListManagerTree plmt = (ProbeListManagerTree)plm;
			lazyInit();
			if (VIEWS.size()==1)
				return null;
			// create the gui element for selection
			JPanel selection = new JPanel();
			for (PLMVPlugin view : VIEWS) {
				JButton button = new JButton(view.getPluginInfo().getName());
				button.addActionListener(new PLMVChangeListener(plmt, view));
				//			button.setEnabled(view != plug);
				selection.add(button);
			}
			return selection;
		}
		return null;
	}

	protected static void addListener(final ProbeListManager plm, ProbeListManagerView plmv) {
		// add key listener here
		plmv.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent event) {				
				int ctrlmask=MouseEvent.CTRL_DOWN_MASK;        				
				if ( (event.getModifiersEx() & ctrlmask) == ctrlmask && event.getKeyCode()==KeyEvent.VK_F  ) {
					ProbeListSearchPanel plsp = plm.getDataSet().getDataSetView().getProbeListSearchPanel();
					if (plsp!=null)
						plsp.setVisible(true);
				}
			}
		});
	}
	
}
