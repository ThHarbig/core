package mayday.vis3.graph.renderer.dispatcher;

import java.util.Collection;

import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.AbstractMutableListSettingComponent;
import mayday.core.settings.generic.MultiselectObjectListSetting;

public abstract class SortedListSettingComponent<T extends Setting> extends AbstractMutableListSettingComponent<MultiselectObjectListSetting<T>, T>
{
	public SortedListSettingComponent(MultiselectObjectListSetting<T> s) 
	{
		super(s);
	}

	@Override
	protected String elementToString(T element) 
	{
		return element.toString();
	}

	@Override
	protected Iterable<T> elementsFromSetting(MultiselectObjectListSetting<T> mySetting) 
	{
		return mySetting.getSelection();
	}

	@Override
	protected abstract T getElementToAdd(Collection<T> alreadyPresent) ;

	@Override
	protected String renderListElement(T element) 
	{
		return element.toString();
	}

	@Override
	protected String renderToolTip(T element) 
	{
		return element.getDescription();
	}

	@Override
	protected void handleDoubleClickOnElement(T element) 
	{
		Setting s = element;
		if (s!=null) {
			SettingDialog sd = new SettingDialog(null,s.getName(),s);
			sd.showAsInputDialog();
		}
	}
	

}
