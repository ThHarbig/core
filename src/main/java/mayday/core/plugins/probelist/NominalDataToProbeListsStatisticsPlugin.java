/*
 * Created on Dec 8, 2004
 *
 */
package mayday.core.plugins.probelist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.GenericMIO;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.meta.NominalMIO;
import mayday.core.meta.gui.MIGroupSelectionDialog;
import mayday.core.meta.types.AnnotationMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;

/**
 * @author gehlenbo
 *
 */
public class NominalDataToProbeListsStatisticsPlugin
extends AbstractPlugin
implements ProbelistPlugin
{

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		//System.out.println("PL1: Register");		
		PluginInfo pli= new PluginInfo(
				(Class)this.getClass(),
				"PAS.statistics.nominal",
				new String[0], 
				Constants.MC_PROBELIST_CREATE,
				(HashMap<String,Object>)null,
				"Nils Gehlenborg",
				"neil@mangojelly.org",
				"Creates new probe lists from information in a nominal MIO group.",
				"Nominal Data To Probe Lists");
		//pli.addCategory(MaydayDefaults.Plugins.CATEGORY_METAINFORMATION+"/"+MaydayDefaults.Plugins.SUBCATEGORY_STATISTICS);
		pli.setMenuName("From nominal MIO...");
		return pli;
	}

	@SuppressWarnings("unchecked")
	public List<ProbeList> run( List<ProbeList> probeLists, MasterTable masterTable )
	{
		LinkedList<ProbeList> result = new LinkedList<ProbeList>();
		MIGroupSelectionDialog dialog = new MIGroupSelectionDialog( masterTable.getDataSet().getMIManager(), NominalMIO.class );
		dialog.setVisible( true );

		Map< Object, MIType > inputMIOList = dialog.getSelection().computeUniqueSelection();

		if ( inputMIOList.size() == 0 )
			return ( result );

		ProbeList inputSet = ProbeList.createUniqueProbeList(probeLists);

		String namePrefix = dialog.getSelection().get(0).getName()+"= ";

		TreeMap< Object, ProbeList > newProbeLists = new TreeMap< Object, ProbeList >();

		for ( Entry<Object, MIType> e : inputMIOList.entrySet()) {
			if (!(e.getKey() instanceof Probe && inputSet.contains((Probe)e.getKey())))
				continue;
			Probe probe = (Probe)e.getKey();
			Object bin =  ((GenericMIO)e.getValue()).getValue();
			if ( newProbeLists.containsKey( bin ) )
				newProbeLists.get( bin ).addProbe( probe );
			else {
				ProbeList probeList = new ProbeList( masterTable.getDataSet(), true );

				probeList.setName( namePrefix + bin.toString());
				probeList.setAnnotation(new AnnotationMIO(
						((MIGroup)dialog.getSelection().get( 0 )).getName(),
						"" ) );

				// add new probe list
				newProbeLists.put( bin, probeList );

				// add probe to new probe list
				newProbeLists.get( bin ).addProbe( probe );
			}
		}

		Object[] tempList = newProbeLists.values().toArray();

		for ( int i = 0; i < tempList.length; ++i ) 
			result.add( (ProbeList)tempList[i] );

		return result;
	}

	@Override
	public void init() {
	}
}
