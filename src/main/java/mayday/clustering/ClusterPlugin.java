package mayday.clustering;

import java.util.List;

import mayday.core.MasterTable;
import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.structures.linalg.impl.ProbeMatrix;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.tasks.AbstractTask;


/**
 * @author gehlenbo
 */
public abstract class ClusterPlugin extends AbstractPlugin
{

	public final static String CATEGORY = MaydayDefaults.Plugins.CATEGORY_CLUSTERING;
	
	public static PermutableMatrix getClusterData( final List<ProbeList> probeLists, final  MasterTable masterTable )
	{
		final PermutableMatrix[] result = new PermutableMatrix[1]; 
		
		AbstractTask at = new AbstractTask("Preparing data matrix") {

			@Override
			protected void doWork() throws Exception {
				ProbeList l_uniqueProbeList = ProbeList.createUniqueProbeList(probeLists);
				for (Probe pb : l_uniqueProbeList.getAllProbes())
					if (pb.getFirstMissingValue()!=-1)
						throw new RuntimeException( "Unable to cluster probes with missing expression values." ); 

				PermutableMatrix matrix = new ProbeMatrix(l_uniqueProbeList.getAllProbes());		
				result[0] = matrix;
			}

			@Override
			protected void initialize() {
				
			}
			
		};
		at.start();
		at.waitFor();
		
				
		return result[0];
	}  

}
