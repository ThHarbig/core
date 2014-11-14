package mayday.core.plugins.dragsupport;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashMap;

import mayday.core.ProbeList;
import mayday.core.gui.dragndrop.DragSupportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class DragProbeListInternal extends AbstractPlugin implements
		DragSupportPlugin {

	@Override
	public void init() {
		 try {
			MAYDAY_PROBELIST_FLAVOR = new DataFlavor("mayday/probelist");
		} catch (ClassNotFoundException e) {
			System.err.println("Could not create dataflavor for mayday probelist d&d support.");
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.D&D.ProbeListInternalFlavor",
				new String[0],
				DragSupportPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Implements internal drag / drop support for probelists",
				"ProbeList internal format"
				);
		return pli;
	}
	
	protected static DataFlavor MAYDAY_PROBELIST_FLAVOR;
	
	@Override
	public DataFlavor getSupportedFlavor() {
		return MAYDAY_PROBELIST_FLAVOR;
	}

	@Override
	public Class<?>[] getSupportedTransferObjects() {
		return new Class[]{ProbeList.class};
	}

	@Override
	public Object getTransferData(Object... input) {
		return input; // no conversion
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] processDrop(Class<T> targetClass, Transferable t) {
		Object to;
		try {
			to = t.getTransferData(MAYDAY_PROBELIST_FLAVOR);
			if (to instanceof Object[]) {
				Object[] oto = ((Object[])to);
				if (targetClass.isAssignableFrom(oto[0].getClass()))
					return (T[])oto;
			}
		} catch (UnsupportedFlavorException e) {
			System.err.println("Could not accept dragged object:");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Could not accept dragged object:");
			e.printStackTrace();
		}
		
		return (T[])new Object[0];
	}
	
	public void setContext(Object contextObject) {
		// no context is needed
	}


}
