package mayday.core.structures.trees.layout;



import java.util.HashMap;

import mayday.core.structures.trees.tree.ITreePart;


/**
 * @author Andreas Friedrich
 * Generic Map to save coordinates, EdgeLayouts and NodeLayouts
 * @param <K> Node or Edge
 * @param <V> NodeLayout or EdgeLayout
 */
@SuppressWarnings("serial")
public class LayoutMap<K extends ITreePart, V extends ILayoutValue> extends HashMap<ITreePart,V> {
	
	V defaultLayout;
	
	/**
	 * Creates a new, empty LayoutMap
	 * @param defaultLayout the default Layout returned for each ITreePart not in the map
	 */
	public LayoutMap(V defaultLayout) {
		super();
		this.defaultLayout = defaultLayout;
	}
	
	public void SetDefaultLayout(V v) {
		this.defaultLayout = v;
	}

	public V get(ITreePart k) {
		V out = super.get(k);
		if(out == null)
			return this.defaultLayout;
		else return out;
	}
	
	public V getDefaultLayout() {
		return defaultLayout;
	}

}
