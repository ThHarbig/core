package mayday.core.pluma;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.filemanager.FMFile;
import mayday.core.pluma.prototypes.GenericPlugin;

public class CreateFilemanagerList extends AbstractPlugin implements GenericPlugin {

	@Override
	public void init() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		if (!MaydayDefaults.isDebugMode())
			return null;
		
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.debug.FilemanagerList",
				new String[]{},
				Constants.MC_SESSION,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Outputs a list of all known files",
				"Create Filemanager Contents File"
		);
		pli.addCategory("DEBUG");
		return pli;		
		
	}

	public void run() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("files.txt")));			
			for (FMFile fmfi : PluginManager.getInstance().getFilemanager().getFiles(true)) {
				bw.write(fmfi.toString()+"\n");
			}
			bw.flush();
			bw.close();
			System.out.println("Saved file to \""+new File("files.txt").getAbsolutePath()+"\"");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
