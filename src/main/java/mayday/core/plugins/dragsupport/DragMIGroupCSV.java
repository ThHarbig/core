package mayday.core.plugins.dragsupport;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import mayday.core.gui.dragndrop.DragSupportPlugin;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.io.probemio.CSVExportPlugin;
import mayday.core.meta.io.probemio.CSVImportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class DragMIGroupCSV extends AbstractPlugin implements
		DragSupportPlugin {

	@Override
	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.D&D.MIGroupCSVFlavor",
				new String[0],
				DragSupportPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Implements CSV drag / drop support for MIGroups",
				"MIGroup CSV format"
				);
		return pli;
	}
	
	protected MIManager targetManager;
	
	@Override
	public DataFlavor getSupportedFlavor() {
		return DataFlavor.stringFlavor;
	}

	@Override
	public Class<?>[] getSupportedTransferObjects() {
		return new Class[]{MIGroup.class};
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getTransferData(Object... input) {
		StringWriter sw = new StringWriter();
		BufferedWriter bw = new BufferedWriter(sw);
		try {
			CSVExportPlugin.exportToWriter((List<MIGroup>)(List<?>)Arrays.asList(input), bw, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sw.getBuffer().toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] processDrop(Class<T> targetClass, Transferable t) {
		/* This is not very nice to run import via a temp file, but I really like to use
		 * the existing parser and I don't feel like rewriting that one.
		 */
		File tf = null;
		try {
			tf  = File.createTempFile("MAYDAY_MIO_DropSupport","");
			tf.deleteOnExit();
			String s = t.getTransferData(DataFlavor.stringFlavor).toString();
			BufferedWriter bw = new BufferedWriter(new FileWriter(tf));
			bw.write(s);
			bw.flush();
			bw.close();
			MIGroup g[] = new CSVImportPlugin().runWithFile(targetManager.getDataSet(), tf);
			if (g==null)
				return (T[])(new MIGroup[0]);
			return (T[])g;
		} catch (Exception exc) {
			// nothing
		} finally {
			if (tf!=null)
				tf.delete();
		}
		return (T[])new Object[0];
	}

	

	@Override
	public void setContext(Object contextObject) {
		targetManager = (MIManager)contextObject;
	}
}
