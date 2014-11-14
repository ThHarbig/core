package mayday.core.settings.typed;

import java.util.LinkedList;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.StringListMIO;
import mayday.core.meta.types.StringMIO;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.SelectableHierarchicalSetting;

public class MappingSourceSetting extends SelectableHierarchicalSetting {

	public final static int PROBE_NAMES = 0;
	public final static int PROBE_DISPLAY_NAMES = 1;
	public final static int MIO = 2;

	protected final static String PROBENAME = "Probe names";
	protected final static String PROBEDISPLAYNAME = "Probe display names";

	protected MIGroupSetting migroup;
	protected DataSet ds;

	public MappingSourceSetting(DataSet ds) {
		super("ID Mapping", null, 0, new Object[0]);
		setPredefined(createPredefinedArray(ds));
		setLayoutStyle(SelectableHierarchicalSetting.LayoutStyle.PANEL_VERTICAL);
		setTopMost(true);
		this.ds = ds;
		migroup.setAcceptableClass(StringMIO.class, StringListMIO.class);
	}

	protected static MIGroupSetting createMIGroupSetting(DataSet ds) {
		return new MIGroupSetting("MIO Group", null, null, ds.getMIManager(), false);
	}

	protected Object[] createPredefinedArray(DataSet ds) {
		return new Object[]{
				PROBENAME,
				PROBEDISPLAYNAME,
				migroup = createMIGroupSetting(ds) 
		};
	}

	public int getMappingSource() {
		Object o = getObjectValue();
		if (o==PROBENAME)
			return PROBE_NAMES;
		if (o==PROBEDISPLAYNAME)
			return PROBE_DISPLAY_NAMES;
		else
			return MIO;
	}

	public MIGroup getMappingGroup() {
		return migroup.getMIGroup();
	}

	public void showDialog() {
		SettingDialog dialog=new  SettingDialog(null, "Select mapping source", this);
		dialog.setModal(true);
		dialog.setVisible(true);
	}

	public MappingSourceSetting clone() {
		MappingSourceSetting gs = new MappingSourceSetting(ds);
		for (Setting childSetting : children) {
			gs.addSetting(childSetting.clone());
		}
		return gs;
	}

	/**
	 * Convenience method for mapping a probe to a name 
	 * @param p The probe to be mapped
	 * @return The name, or null if a getMappingSource==MIO and getMappingGroup().contains(p) returns false;
	 * @see MIGroup
	 */
	public String mappedName(Probe p)
	{
		if(getMappingSource()==MappingSourceSetting.PROBE_NAMES)
			return p.getName();
		if(getMappingSource()==MappingSourceSetting.PROBE_DISPLAY_NAMES)
			return p.getDisplayName();
		if(getMappingSource()==MappingSourceSetting.MIO)
		{
			if(getMappingGroup().contains(p)) {
				return getMappingGroup().getMIO(p).toString();
			}
		}
		return null;
	}
	
	public List<String> mappedNames(Probe p) {
		if(getMappingSource()==MappingSourceSetting.PROBE_NAMES) {
			LinkedList<String> l = new LinkedList<String>();
			l.add(p.getName());
			return l;
		}
		
		if(getMappingSource()==MappingSourceSetting.PROBE_DISPLAY_NAMES) {
			LinkedList<String> l = new LinkedList<String>();
			l.add(p.getDisplayName());
			return l;
		}
			
		if(getMappingSource()==MappingSourceSetting.MIO) {
			LinkedList<String> l = new LinkedList<String>();
			if(getMappingGroup().contains(p)) {
				String miType = getMappingGroup().getMIO(p).getType();
				
				if(miType.equals(StringMIO.myType)) {
					l.add(getMappingGroup().getMIO(p).toString());
				}
				
				if(miType.equals(StringListMIO.myType)) {
					StringListMIO mio = (StringListMIO)getMappingGroup().getMIO(p);
					for(String s : mio.getValue()) {
						l.add(s);
					}
				}
				
				return l;
			}
		}
		
		return null;
	}
	
	public void setDataSet(DataSet ds)
	{
		setPredefined(createPredefinedArray(ds));
		this.ds=ds;
		fireChanged();
	}

}
