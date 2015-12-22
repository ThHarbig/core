package mayday.vis3.graph.renderer.dispatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.MultiselectObjectListSetting;
import mayday.vis3.graph.vis3.SuperColorProvider;

public class AssignedRendererListSetting extends MultiselectObjectListSetting<AssignedRendererSetting>
{
	private DataSet dataset;
	private SuperColorProvider coloring;

	public AssignedRendererListSetting(DataSet ds, SuperColorProvider coloring, Collection<AssignedRendererSetting> settings) 
	{
		super("Role renderers","The renderers responsible to display data associated with nodes with certain rules",settings);
		this.dataset=ds;
		this.coloring=coloring;
		setSelection(settings);
	}

	public SettingComponent getGUIElement() 
	{
		return new SortedListSettingComponent<AssignedRendererSetting>(this) {

			@Override
			protected AssignedRendererSetting getElementToAdd(Collection<AssignedRendererSetting> alreadyPresent) 
			{
				AssignedRendererSetting	newSetting=new AssignedRendererSetting("New Role", dataset, coloring);
				SettingDialog sd = new SettingDialog(null, "Add new Setting", newSetting);
				sd.showAsInputDialog();
				if (!sd.canceled()) 
				{
					return newSetting;					
				}
				return null;
			}

			// override method in AbstractSettingComponent
			@Override
			public boolean updateSettingFromEditor(boolean failSilently) 
			{
				if (theList==null)
					return true;

				List<AssignedRendererSetting> ret = new LinkedList<AssignedRendererSetting>();
				for (int i=0; i!=theList.getModel().getSize(); ++i) 
				{
					AssignedRendererSetting element = (AssignedRendererSetting)theList.getModel().getElementAt(i);
					ret.add(element);
				}

				update(ret);
//				((AssignedRendererListSetting)mySetting).updatePrefed(ret);
//				mySetting.setSelection(ret);
				return true;
			}
			
			@Override
			protected void handleDoubleClickOnElement(AssignedRendererSetting element) 
			{
				Setting s = element;
				if (s!=null) {
					SettingDialog sd = new SettingDialog(null,s.getName(),s);
					sd.showAsInputDialog();
				}
			}
		};
	}
	
	public void update(List<AssignedRendererSetting> ret)
	{
		updatePrefed(ret);
		setSelection(ret);
		fireChanged();
	}

	protected void updatePrefed(List<AssignedRendererSetting> ret) 
	{
		predef=new ArrayList<AssignedRendererSetting>(ret);

	}

	public void addRenderer(AssignedRendererSetting renderer)
	{
		predef.add(renderer);
		List<AssignedRendererSetting> renderers=new ArrayList<AssignedRendererSetting>(getSelection());
		renderers.add(renderer);
		setSelection(renderers);
		fireChanged();
	}

	@Override
	public AssignedRendererListSetting clone() 
	{
		AssignedRendererListSetting res=new AssignedRendererListSetting(dataset, coloring, predef);
		res.fromPrefNode(toPrefNode());
		return res;
	}

	public void clear()
	{
		setSelection(new ArrayList<AssignedRendererSetting>());
		predef.clear();
	}


}

