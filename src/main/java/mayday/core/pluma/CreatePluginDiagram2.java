package mayday.core.pluma;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.prototypes.GenericPlugin;

public class CreatePluginDiagram2 extends AbstractPlugin implements GenericPlugin {

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
				"PAS.debug.PluginDiagramMC",
				new String[]{},
				Constants.MC_SESSION,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Creates a GML file of the plugins and their mastercomponents",
				"Create Plugin Hierarchy Diagram"
		);
		pli.addCategory("DEBUG");
		return pli;		
		
	}

	public void run() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("plugins.gml")));
			// build the diagram
			bw.write(
					"graph [\n" +
					"directed 1\n");

			for (String MC : PluginManager.getInstance().getMasterComponents()) {
				// add the component
				 
				bw.write("node [ id "+MC.hashCode()+" label \""+MC+"\" ]\n");								
				for (PluginInfo pli : PluginManager.getInstance().getPluginsFor(MC)) {
					// add the node with record style label
					String pli_id = pli.getIdentifier();
					bw.write("node [ id "+pli_id.hashCode()+" label \""+pli.getName()+"\" ]\n");
					bw.write("edge [ source "+pli_id.hashCode()+" target "+MC.hashCode()+" ]\n");
				}
				//close component
			}
			// close diagram
			bw.write("]");
			bw.flush();
			bw.close();
			System.out.println("Saved gml file to \""+new File("plugins.gml").getAbsolutePath()+"\"");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
