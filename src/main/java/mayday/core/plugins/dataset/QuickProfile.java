package mayday.core.plugins.dataset;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import mayday.core.DataSet;
import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.PreferencePane;
import mayday.core.meta.GenericMIO;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.DatasetPlugin;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.StringSetting;

public class QuickProfile extends AbstractPlugin implements DatasetPlugin {

	protected HierarchicalSetting setting;
	protected BooleanSetting useMIOs, useDisplayName, useProbeName;
	protected StringSetting query;

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.QuickProfilePlot",
				new String[0],
				Constants.MC_DATASET,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Quickly create a profile plot from a few probe identifiers",
				"Quick profile plot"
		);
		pli.setMenuName("Quickly Visualize Probes");
		return pli;
	}
	
	@Override
	public PreferencePane getPreferencesPanel() {
		return null;
	}

	public Setting getSetting() {
		if (setting==null) {
			useProbeName = new BooleanSetting("Search in Probe names", 
					"Find probes with a given name. Only perfect matches are considered. \n" +
					"If a probe is found, the corresponding query is not considered for further searches.", true);
			useDisplayName = new BooleanSetting("Search in Probe display names", 
					"Find probes with a given display name. Only perfect matches are considered.", true);
			useMIOs = new BooleanSetting("Search in meta information",
					"Find probes with meta information matching the query, substring matches are allowed.\n" +
					"All meta information values are converted to character strings." , false);
			query = new StringSetting("Query", 
					"Enter probe identifiers, display names or a query for meta information. \n" +
					"Multiple values can be entered comma-separated.", "", false);
			setting = new HierarchicalSetting("Quickly visualize probes")
			.addSetting(query)
			.addSetting(useProbeName)
			.addSetting(useDisplayName)			
			.addSetting(useMIOs);
		}
		return setting;
	}

	@SuppressWarnings("unchecked")
	public List<DataSet> run(List<DataSet> datasets) {

		DataSet ds = datasets.get(0);

		getSetting();

		SettingDialog sdlg = new SettingDialog(null, "Quickly visualize probes", setting);
		if (!sdlg.showAsInputDialog().closedWithOK())
			return null;

		HashSet<Probe> probesToFind = new HashSet<Probe>();

		String[] queries_ = query.getStringValue().split(",");
		LinkedList<String>  queries = new LinkedList<String>();
		for (String q : queries_)
			queries.add(q.trim());

		// first search probe names, remove those found
		if (useProbeName.getBooleanValue()) {
			for (String query : new LinkedList<String>(queries)) {
				Probe p = ds.getMasterTable().getProbe(query);
				if (p!=null) {
					probesToFind.add(p);
					queries.remove(query);
				}
			}
		}

		// display names in remaining set
		if (useDisplayName.getBooleanValue()) {
			for (String query : new LinkedList<String>(queries)) {
				// slow...
				for (Probe npb : ds.getMasterTable().getProbes().values()) {
					if (npb.getDisplayName().equals(query)) {
						probesToFind.add(npb);
					}
				}
			}
		}

		if (useMIOs.getBooleanValue()) {
			for (String query : new LinkedList<String>(queries)) {
				// slow...
				for (Probe npb : ds.getMasterTable().getProbes().values()) {
					for (MIGroup mg : ds.getMIManager().getGroupsForObject(npb)) {
						MIType mt = mg.getMIO(npb);
						if (mt!=null) {
							String asString = ((GenericMIO)mt).getValue().toString();
							if (asString.contains(query)) 
								probesToFind.add(npb);
						}
					}
				}
			}
		}


		if (probesToFind.size()==0) {
			JOptionPane.showMessageDialog( null,
					"No probes found to visualize.",                                           
					MaydayDefaults.Messages.ERROR_TITLE,
					JOptionPane.ERROR_MESSAGE );
			return null;
		}

		ProbeList tmpList = new ProbeList(ds, false);
		tmpList.setName("Search for \""+query.getStringValue()+"\"");
		for (Probe pb : probesToFind)
			tmpList.addProbe(pb);

		PluginInfo pli = PluginManager.getInstance().getPluginFromID("PAS.incubator.ProfilePlot");
		if (pli!=null) {
			LinkedList<ProbeList> lpl   = new LinkedList<ProbeList>();
			lpl.add(tmpList);
			((ProbelistPlugin)pli.newInstance()).run(lpl,ds.getMasterTable());
		}

		return null;

	}

}
