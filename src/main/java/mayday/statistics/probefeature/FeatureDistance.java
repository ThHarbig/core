package mayday.statistics.probefeature;

import java.util.HashMap;
import java.util.List;

import mayday.core.ClassSelectionModel;
import mayday.core.MasterTable;
import mayday.core.Mayday;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.math.distance.DistanceMeasureManager;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.methods.DistanceMeasureSetting;
import mayday.core.settings.typed.ClassSelectionSetting;

public class FeatureDistance extends AbstractProbeFeaturePlugin implements ProbelistPlugin {

	@Override
	public List<ProbeList> run(List<ProbeList> probeLists, MasterTable masterTable) {
		
		ClassSelectionModel csm = new ClassSelectionModel(masterTable);
		ClassSelectionSetting css = new ClassSelectionSetting("Define feature classes",null,csm,2,2);
		
		DistanceMeasureSetting distance = new DistanceMeasureSetting("Distance Measure",null,DistanceMeasureManager.get("Euclidean"));
		
		HierarchicalSetting setting = new HierarchicalSetting("Feature Distance")
			.addSetting(css)
			.addSetting(distance);
		
		SettingDialog sd = new SettingDialog(Mayday.sharedInstance, setting.getName(), setting);
		sd.showAsInputDialog();
		
		if(!sd.closedWithOK()) {
			return null;
		}
		
		csm = css.getModel();
		DistanceMeasurePlugin distanceMeasure = distance.getInstance();
		
		MIGroup mioGroup = masterTable.getDataSet().getMIManager().newGroup("PAS.MIO.Double", 
				"FeatureDistance: " + distanceMeasure.getPluginInfo().getName() + " between "+csm.getClassNames().get(0)+" and "+csm.getClassNames().get(1),"/Probe Statistic/"
		);
		
		List<Integer> c1 = csm.toIndexList(0);
		List<Integer> c2 = csm.toIndexList(1);
		int l1 = c1.size();
		int l2 = c2.size();
		
		ProbeList uniqueProbes = ProbeList.createUniqueProbeList(probeLists);
		
		for (Probe pb : uniqueProbes.getAllProbes()) {
			double[] vals = pb.getValues();
			
			double[] class1 = new double[l1];
			double[] class2 = new double[l2];
			
			for(int i = 0; i < l1; i++) {
				class1[i] = vals[c1.get(i)];
			}
			
			for(int i = 0; i < l2; i++) {
				class2[i] = vals[c2.get(i)];
			}
			
			double d = distanceMeasure.getDistance(class1, class2);
			
			DoubleMIO mio = new DoubleMIO( d );
			mioGroup.add( pb, mio );
		}
		
		return null;
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"PAS.statistics.FeatureDistance",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Günter Jäger",
				"jaeger@informatik.uni-tuebingen.de",
				"Compute distance between feature classes",
		"Feature Distance");
		pli.addCategory(MC);
		return pli;
	}

	@Override
	public void init() {}
}
