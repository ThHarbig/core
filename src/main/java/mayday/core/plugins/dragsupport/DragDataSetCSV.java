package mayday.core.plugins.dragsupport;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashMap;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.gui.dragndrop.DragSupportPlugin;
import mayday.core.io.csv.ParsedLine;
import mayday.core.io.csv.ParserSettings;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class DragDataSetCSV extends AbstractPlugin implements
		DragSupportPlugin {

	@Override
	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.D&D.DataSetCSVFlavor",
				new String[0],
				DragSupportPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Implements drag / drop support for datasets",
				"DataSet CSV format"
				);
		return pli;
	}
	
	@Override
	public DataFlavor getSupportedFlavor() {
		return DataFlavor.stringFlavor;
	}

	@Override
	public Class<?>[] getSupportedTransferObjects() {
		return new Class[]{DataSet.class};
	}

	@Override
	public Object getTransferData(Object... input) {
		// only return the first dataset
		if (input.length>0) {
			DataSet ds = (DataSet)input[0];
			return asTabular("\t", ds.getMasterTable());
		}		
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] processDrop(Class<T> targetClass, Transferable t) {
		String str;
		try {
			str = t.getTransferData(DataFlavor.stringFlavor).toString();		
			// first copy the dataset content
			DataSet ds = new DataSet("Received DataSet");
			ds.getAnnotation().setQuickInfo("Recieved from another application via drag&drop");
			MasterTable mt = new MasterTable(ds);
			String[] lines = str.split("\n"); 			

			ParserSettings psett = new ParserSettings();
			ParsedLine pl = new ParsedLine("", psett);
			
			// first row: experiment names.
			pl.replaceLine(lines[0]);
			if (pl.get(0)==null) { 
				mt.setNumberOfExperiments(pl.size()-1);			
				mt.setExperimentNames(pl.asList().subList(1, pl.size()));
			} else {
				mt.setNumberOfExperiments(pl.size());			
				mt.setExperimentNames(pl.asList());
			}
			int noe = mt.getNumberOfExperiments();
			
			// other rows: probe data data data
			for (int row = 1; row<lines.length; ++row) {
				pl.replaceLine(lines[row]);
				Probe pb = new Probe(mt);
				pb.setName(pl.get(0));
				for (int col = 0; col!=noe; ++col) {
					Double value = Double.parseDouble(pl.get(col+1));
					pb.setValue(value, col);
				}
				mt.addProbe(pb);
			}					
			return (T[])new DataSet[]{ds};
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (T[])new Object[0];
	}

	protected String asTabular(String sep, MasterTable mt) {
		// serialize probelist matrix 
		StringBuilder sb = new StringBuilder();
		
		int noe = mt.getNumberOfExperiments();
		
		for (int i=0; i<noe; ++i) {
			sb.append(sep);
			sb.append(mt.getExperimentName(i));
		}
		
		sb.append("\n");
		
		for (Probe pb : mt.getProbes().values()) {
			sb.append(pb.getName());
			for (int i=0; i<noe; ++i) { 
				sb.append(sep);
				sb.append(pb.getValue(i));
			}
			sb.append("\n");					
		}		
		
		return sb.toString();
	}

	@Override
	public void setContext(Object contextObject) {
		// no context is needed
	}
}
