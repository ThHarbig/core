package mayday.core.settings.generic;

import mayday.core.meta.MIType;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.AbstractSetting;
import mayday.core.settings.SettingComponent;

public class GenericSetting extends AbstractSetting {
	
	protected Class<? extends AbstractPlugin> clazz;
	protected MIType representative;

	/* MITypes are not referenced by pluma id but by class for two reasons:
	   a) We need settings very early, maybe even before all MITypes have been registered with pluma
	   b) We know that these MITypes are in the core package, otherwise settings wouldn't compile anyways
	   */ 
	public GenericSetting(String Name, Class<? extends AbstractPlugin> miclazz, String Description) {
		super(Name, Description);
		clazz = miclazz;
		Object o;
		try {
			o = clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		if (!(o instanceof MIType))
			throw new RuntimeException("GenericSetting can only be built around a MIType class");
		representative = (MIType)o;
	}	
	
	public boolean isValidValue(String value) {
		MIType mt2 = representative.clone();
		return mt2.deSerialize(MIType.SERIAL_TEXT, value);		
	}

	@SuppressWarnings("unchecked")
	public SettingComponent getGUIElement() {
		return new GenericSettingComponent(this);
	}

	public MIType getValue() {
		return representative;
	}

	public String getValueString() {
		return representative.serialize(MIType.SERIAL_TEXT);
	}
	
	protected String getType() {
		return (PluginManager.getInstance().getPluginFromClass(clazz).getIdentifier());
	}

	public void setValueString(String newValue) {
		String oldVal = getValueString();
		if (!representative.deSerialize(MIType.SERIAL_TEXT, newValue))
			throw new RuntimeException("Invalid value \""+newValue+"\" for Setting of type "+getType());
		if (oldVal==null || !oldVal.equals(getValueString()))
			fireChanged();
	}

	public GenericSetting clone() {
		GenericSetting gs = new GenericSetting(name, clazz, description);
		if (this.getClass()!=gs.getClass())
			throw new RuntimeException("Exception cloning does not work for class "+this.getClass());
		gs.setValueString(getValueString());
		return gs;
	}
	
}
