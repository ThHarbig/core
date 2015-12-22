package mayday.vis3.components;

import java.lang.reflect.Field;

import javax.swing.JComponent;
import javax.swing.PopupFactory;

public class HeavyWeightWorkaround {

	protected final static Object forceHeavyWeightPopupKey;
	
	static {
		Object key = null;
		try {
			Class<PopupFactory> popup = PopupFactory.class;
			Field heavyweight = popup.getDeclaredField("forceHeavyWeightPopupKey");
			heavyweight.setAccessible(true);
			key = heavyweight.get(null);
		} catch(Exception ex) {						
		}
		forceHeavyWeightPopupKey = key;
	}
	
	public static boolean forceHeavyWeightPopups(JComponent jc) {
		if (forceHeavyWeightPopupKey != null) {
			jc.putClientProperty(forceHeavyWeightPopupKey, Boolean.TRUE);
			return true;
		}
		return false;
	}
	
}
