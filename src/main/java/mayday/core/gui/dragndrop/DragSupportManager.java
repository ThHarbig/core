package mayday.core.gui.dragndrop;

import java.awt.datatransfer.DataFlavor;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.CorePlugin;
import mayday.core.structures.maps.MultiHashMap;

public class DragSupportManager extends AbstractPlugin implements CorePlugin {
	
	private static MultiHashMap<Class<?>, DragSupportPlugin> byObject = new MultiHashMap<Class<?>,DragSupportPlugin>();
	private static MultiHashMap<DataFlavor, DragSupportPlugin> byFlavor = new MultiHashMap<DataFlavor,DragSupportPlugin>();
	
	public void init() {
	}
	
	public void run() {
		byFlavor.clear();
		byObject.clear();
		for (PluginInfo pli : PluginManager.getInstance().getPluginsFor(DragSupportPlugin.MC)) {
			DragSupportPlugin dsp = (DragSupportPlugin)pli.getInstance();
			for (Class<?> c : Arrays.asList(dsp.getSupportedTransferObjects()))
				byObject.put(c,dsp);
			byFlavor.put(dsp.getSupportedFlavor(),dsp);
		}
	};
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.Drag&Drop",
				new String[0],
				Constants.MC_CORE,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Manages all available drag&drop support plugins",
				"Drag&Drop support"
				);
		return pli;
	}
	
	public static DragSupportPlugin getSupportFor(Class<?> sourceObjectType, DataFlavor targetFlavor) {
		Collection<DragSupportPlugin> dsps = byFlavor.get(targetFlavor);
		for (DragSupportPlugin dsp : dsps)
			if (supportsType(dsp,sourceObjectType))
				return dsp;
		return null;
	}
	
	protected static boolean supportsType(DragSupportPlugin dsp, Class<?> type) {
		Class<?>[] acceptable = dsp.getSupportedTransferObjects();
		for (int i=0; i!=acceptable.length; ++i)
			if (acceptable[i].equals(type))
				return true;
		return false;		
	}
	
	
	public DragSupportManager() {
		// empty for pluma
	}

	public static DataFlavor[] getFlavorsFor(Class<?> sourceObjectType) {
		Collection<DragSupportPlugin> dsps = byObject.get(sourceObjectType);
		HashSet<DataFlavor> hdf = new HashSet<DataFlavor>();
		for (DragSupportPlugin dsp : dsps)
			hdf.add(dsp.getSupportedFlavor());
		return hdf.toArray(new DataFlavor[hdf.size()]);
	}
	
	public static DragSupportPlugin getSupportFor(Class<?> targetObjectType, DataFlavor[] availableFlavors) {
		Collection<DragSupportPlugin> dsps = byObject.get(targetObjectType);
		DragSupportPlugin preferential=null, regular = null;
		
		for (DataFlavor oneFlavor : availableFlavors)
			for (DragSupportPlugin dsp : dsps)
				if (dsp.getSupportedFlavor().equals(oneFlavor))
					if (oneFlavor.getPrimaryType().equals("mayday"))
						preferential = dsp;
					else
						regular = dsp;
		
		return preferential!=null?preferential:regular;		
	}
	

}
