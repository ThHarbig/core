package mayday.vis3.plots.profile;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.StringSetting;

public class BreakSetting extends  HierarchicalSetting  {
	
	StringSetting breaks;
	ObjectSelectionSetting<String> useBreaks;
	
	public final static String BREAK_OFF = "ignore breaks";
	public final static String BREAK_DISCONNECT = "disconnect at breaks";
	public final static String BREAK_JUMP = "start from left at breaks";
	public final static String BREAK_JUMPSHIFT = "start from left and shift profiles";
	
	public final static int BREAK_IGNORE = 0;
	public final static int BREAK_UNCONNECTED = 1;
	public final static int BREAK_START_LEFT = 2;
	public final static int BREAK_START_LEFT_SHIFTED = 3;
	
	protected final static String[] BREAK_TYPES = new String[]{
		BREAK_OFF, BREAK_DISCONNECT, BREAK_JUMP, BREAK_JUMPSHIFT
	};
	
	public BreakSetting(String Name) {
		super(Name);
		addSetting( useBreaks = new ObjectSelectionSetting<String>(
				"Use breaks",
				null,
				0,
				BREAK_TYPES
				) );
		addSetting( breaks = new StringSetting(
				"Break positions",
				"Enter experiment indices, comma separated.\n",
				"",
				true
				) );
	}

	public StringSetting getBreaks() {
		return breaks;
	}

	public ObjectSelectionSetting<String> getUseBreaks() {
		return useBreaks;
	}
	
	public int getBreakType() {
		if (useBreaks.getObjectValue()==BREAK_OFF)
			return BREAK_IGNORE;
		if (useBreaks.getObjectValue()==BREAK_DISCONNECT)
			return BREAK_UNCONNECTED;
		if (useBreaks.getObjectValue()==BREAK_JUMP)
			return BREAK_START_LEFT;
		if (useBreaks.getObjectValue()==BREAK_JUMPSHIFT)
			return BREAK_START_LEFT_SHIFTED;
		return BREAK_IGNORE;
	}
	
	
	public BreakSetting clone() {
		BreakSetting n = new BreakSetting(name);
		n.fromPrefNode(toPrefNode());
		return n;
	}
	 

}
