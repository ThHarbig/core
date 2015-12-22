package mayday.core.plugins.dragsupport;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashMap;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.dragndrop.DragSupportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class DragProbeListCSV extends AbstractPlugin implements
		DragSupportPlugin {

	@Override
	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.D&D.ProbeListCSVFlavor",
				new String[0],
				DragSupportPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Implements drag / drop support for probelists",
				"ProbeList CSV format"
				);
		return pli;
	}
	
	@Override
	public DataFlavor getSupportedFlavor() {
		return DataFlavor.stringFlavor;
	}

	@Override
	public Class<?>[] getSupportedTransferObjects() {
		return new Class[]{ProbeList.class};
	}

	@Override
	public Object getTransferData(Object... input) {
		return asTabular("\t", ((ProbeList[])input));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] processDrop(Class<T> targetClass, Transferable t) {
		String str;
		try {
			str = t.getTransferData(DataFlavor.stringFlavor).toString();		
			// first copy the probeset content
			DataSet ds = new DataSet(true);
			ds.setName("another application via drag&drop");
			MasterTable mt = new MasterTable(ds);
			ProbeList targetPL = new ProbeList(ds, false);
			targetPL.setName("Dropped ProbeList");
			String[] nameCandidates = str.split("[\\s]+");
			if (nameCandidates.length==1)
				nameCandidates = str.split(",");
			if (nameCandidates.length==1)
				nameCandidates = str.split(";");				
			for (String nameCandidate : nameCandidates) {
				nameCandidate = nameCandidate.trim();	
				if (targetPL.contains(nameCandidate))
					continue;
				Probe pb = new Probe(mt);
				pb.setName(nameCandidate);
				targetPL.addProbe(pb);
			}
			return (T[])new ProbeList[]{targetPL};
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (T[])new Object[0];
	}

	protected String asTabular(String sep, ProbeList[] lists) {
		// serialize probelist matrix 
		StringBuilder sb = new StringBuilder();
		
		MasterTable mt = lists[0].getDataSet().getMasterTable();
		
		int noe = mt.getNumberOfExperiments();
		
		for (int i=0; i<noe; ++i) {
			sb.append(sep);
			sb.append(mt.getExperimentName(i));
		}
		
		sb.append("\n");
		
		for (ProbeList pl : lists) {
			for (Probe pb : pl.toCollection()) {
				sb.append(pb.getName());
				for (int i=0; i<noe; ++i) { 
					sb.append(sep);
					sb.append(pb.getValue(i));
				}
				sb.append("\n");					
			}		
		}
		
		return sb.toString();
	}

	@Override
	public void setContext(Object contextObject) {
		// no context is needed
	}
}
