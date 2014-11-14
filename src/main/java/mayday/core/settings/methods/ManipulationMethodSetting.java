package mayday.core.settings.methods;

import java.util.HashSet;
import java.util.Iterator;

import mayday.core.settings.generic.PluginInstanceSetting;
import mayday.vis3.model.ManipulationMethod;
import mayday.vis3.model.ManipulationMethodSingleValue;


public class ManipulationMethodSetting extends PluginInstanceSetting<ManipulationMethod> {

	protected boolean wasSingleOnly;
	
	public ManipulationMethodSetting(String Name, String Description, ManipulationMethod Default) {
		this(Name, Description, Default, false);	
	}
	
	public ManipulationMethodSetting(String Name, String Description, ManipulationMethod Default, boolean onlySingleValue) {
		super(Name, Description, Default, ManipulationMethod.MC);
		if (onlySingleValue) {
			predef = new HashSet<ManipulationMethod>(predef); // otherwise it might be unmodifiable
			Iterator<ManipulationMethod> pliter = predef.iterator();
			while (pliter.hasNext())
				if (!ManipulationMethodSingleValue.class.isAssignableFrom(pliter.next().getClass()))
					pliter.remove();
		}
	}
	
	public ManipulationMethodSetting clone() {
		return new ManipulationMethodSetting(getName(),getDescription(),getInstance());
	}
	
}
