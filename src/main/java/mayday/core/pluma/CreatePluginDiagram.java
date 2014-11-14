package mayday.core.pluma;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.prototypes.GenericPlugin;

public class CreatePluginDiagram extends AbstractPlugin implements GenericPlugin {

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
				"PAS.debug.PluginDiagramDependency",
				new String[]{},
				Constants.MC_SESSION,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Creates a graphviz dot file of the plugins and their dependencies",
				"Create Plugin Dependency Diagram"
		);
		pli.addCategory("DEBUG");
		return pli;		
		
	}

	public void run() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("plugins.dot")));
			// build the diagram
			bw.write("digraph plugins {\n" +
			"node [shape=record];\n");

			for (String MC : PluginManager.getInstance().getMasterComponents()) {
				// add the component
				bw.write("subgraph \"cluster"+MC.replace(" ","")+"\" {\n" +
						"label = \""+MC+"\";\n");
				for (PluginInfo pli : PluginManager.getInstance().getPluginsFor(MC)) {
					// add the node with record style label
					bw.write("\""+pli.getIdentifier()+"\" [label=\"{"+pli.getIdentifier()+"|"
							+pli.getName().replace(" ","\\ ")+"|"
							+pli.getPluginClass().getCanonicalName()+"}\"];\n");
					// add edges for dependencies
					for (String dep : pli.getDependencies())
						bw.write("\""+pli.getIdentifier()+"\" -> \""+dep+"\"\n");
				}
				//close component
				bw.write("};\n");
			}
			// close diagram
			bw.write("}");
			bw.flush();
			bw.close();
			System.out.println("Saved dot file to \""+new File("plugins.dot").getAbsolutePath()+"\"");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
