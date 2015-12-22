package mayday.clustering.hierarchical;

import java.util.HashMap;

import mayday.core.meta.GenericMIO;
import mayday.core.meta.gui.AbstractMIRenderer;
import mayday.core.meta.gui.MIRendererDefault;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class TreeMIO extends GenericMIO<TreeInfo> {

	protected final static String myType = "PAS.MIO.HierarchicalClusteringTree";
	
	public TreeMIO() {}
	
	public TreeMIO(TreeInfo value) {
		Value=value;
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(),
				myType,
				new String[0],
				Constants.MC_METAINFO,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Represents the result of an hierarchical clustering  as meta information",
				"Hierarchical Clustering MIO"
				);
	}


	public boolean deSerialize(int serializationType, String serializedForm) {
		Value = new TreeInfo(serializedForm);
		return true;
	}
	
	public String serialize(int serializationType) {
		try {
			return Value.serialize();			
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Serialization of the Tree Information failed: "+e);
		}
	}

	@SuppressWarnings("unchecked")
	public AbstractMIRenderer getGUIElement() {
		MIRendererDefault mrd = new MIRendererDefault();
		mrd.setEditable(false);
		return mrd;
	}


	public TreeMIO clone() {
		return new TreeMIO(Value);
	}

	
	public int compareTo(Object arg0) {
		if (arg0 instanceof TreeMIO) {
			TreeInfo v = ((TreeMIO)arg0).getValue();
			return Value.compareTo(v);
		}
		return 0;
	}

	public String getType() {
		return myType;
	}
	
	public String toString() {
		return Value.getTree().numberOfDescendantLeaves(null)+" taxa";
	}
	
}
