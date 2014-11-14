package mayday.vis3.graph.renderer.dispatcher;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.stream.XMLStreamWriter;

import mayday.core.DataSet;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.AbstractMutableListSettingComponent;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.MultiselectObjectListSetting;
import mayday.core.settings.typed.ClassSelectionSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.vis3.graph.renderer.RendererDecorator;

public class DecoratorListSetting extends   MultiselectObjectListSetting<RendererDecorator> 
{
	private DataSet dataSet;

	public static final String DECORATOR_LIST="decoratorList";
	public static final String DECORATOR="decorator";

	public DecoratorListSetting(DataSet dataSet) 
	{
		super("Additional Information", "Add additional renderers for Class Labels, " +
				"Releavance and Meta Information",new ArrayList<RendererDecorator>());
		this.dataSet=dataSet;	

	}

	public DecoratorListSetting(String name, DataSet dataSet) 
	{
		super(name, "Add additional renderers for Class Labels, " +
				"Releavance and Meta Information",new ArrayList<RendererDecorator>());
		this.dataSet=dataSet;	

	}


	@Override
	public DecoratorListSetting clone() 
	{		
		DecoratorListSetting clone=new DecoratorListSetting(dataSet);
		clone.fromPrefNode(toPrefNode());
		return clone;
	}

	public SettingComponent getGUIElement() 
	{
		return new RendererDecoratorListSettingComponent(this);
	}


	public class RendererDecoratorListSettingComponent extends AbstractMutableListSettingComponent<MultiselectObjectListSetting<RendererDecorator>, RendererDecorator> 
	{
		public RendererDecoratorListSettingComponent(MultiselectObjectListSetting<RendererDecorator> s) 
		{
			super(s);
		}

		public List<RendererDecorator> getPlugins() 
		{
			return modelToList(theList.getModel());
		}

		@Override
		protected String elementToString(RendererDecorator element) 
		{
			return element.toString();
		}

		@Override
		protected Iterable<RendererDecorator> elementsFromSetting(MultiselectObjectListSetting<RendererDecorator> mySetting) 
		{
			return mySetting.getSelection();
		}

		@Override
		protected RendererDecorator getElementToAdd(Collection<RendererDecorator> alreadyPresent) 
		{
			Map<String, PluginInfo> av = new TreeMap<String, PluginInfo>();
			Map<String, RendererDecorator> bv=new TreeMap<String, RendererDecorator>();

			Set<PluginInfo> plis= PluginManager.getInstance().getPluginsFor(AbstractRendererPlugin.MC_DECORATOR);
			for (PluginInfo pli : plis)
				av.put(pli.getName(), pli);

			for(String s: av.keySet())
			{
				RendererDecorator decorator=(RendererDecorator)av.get(s).getInstance();
				decorator.setDataSet(dataSet);
				bv.put(s, decorator);
			}

			RestrictedStringSetting available = new RestrictedStringSetting("Select a plugin",null, 0, av.keySet().toArray(new String[0]));


			String[] tooltips = new String[av.size()];
			for (int i=0; i!=av.size(); ++i) {
				tooltips[i] = av.get(available.getPredefinedValues()[i]).getAbout();
			}
			available.setToolTips(tooltips);			
			available.setLayoutStyle(RestrictedStringSetting.LayoutStyle.LIST);

			SettingDialog sd = new SettingDialog(null, "Select a plugin to add", available);
			sd.showAsInputDialog();
			if (!sd.canceled()) 
			{
				RendererDecorator rd=(RendererDecorator)bv.get(available.getStringValue());				
				return rd;
			}
			return null;
		}

		@Override
		protected String renderListElement(RendererDecorator element) 
		{
			return PluginManager.getInstance().getPluginFromClass(element.getClass()).getName();
		}

		@Override
		protected String renderToolTip(RendererDecorator element) 
		{
			return  PluginManager.getInstance().getPluginFromClass(element.getClass()).getAbout();
		}

		protected void handleDoubleClickOnElement(RendererDecorator element) 
		{
			Setting s = element.getSetting();
			if (s!=null) {
				SettingDialog sd = new SettingDialog(null,s.getName(),s);
				sd.showAsInputDialog();
			}
		}

		// override method in AbstractSettingComponent
		@Override
		public boolean updateSettingFromEditor(boolean failSilently) 
		{
			if (theList==null)
				return true;

			List<RendererDecorator> ret = new LinkedList<RendererDecorator>();
			for (int i=0; i!=theList.getModel().getSize(); ++i) 
			{
				RendererDecorator element = (RendererDecorator)theList.getModel().getElementAt(i);
				ret.add(element);
			}
			((DecoratorListSetting)mySetting).updatePrefed(ret);
			mySetting.setSelection(ret);
			return true;
		}


	}

	private void updatePrefed(List<RendererDecorator> predef)
	{
		this.predef=predef;		
	}

	public void exportXML(XMLStreamWriter writer) throws Exception
	{
		if(getSelection().isEmpty())
			return;
		writer.writeStartElement(DECORATOR_LIST);
		for(RendererDecorator dec: getSelection())
		{
			writer.writeStartElement(DECORATOR);
			writer.writeAttribute("pluginId", dec.getPluginInfo().getIdentifier());
			if(dec.getSetting()!=null)
				writer.writeAttribute(RendererDispatcher.SETTINGS, serializeSetting(dec));
			// if there are migroup settings somewhere, we need to take care of them!
			if(dec.getSetting() instanceof HierarchicalSetting)
			{
				serializeDataSetHint(writer, (HierarchicalSetting)dec.getSetting());
				serializeClassSelectionHint(writer, (HierarchicalSetting)dec.getSetting());
			}
			writer.writeEndElement();
		}
		writer.writeEndElement();
	}

	private String serializeSetting(RendererDecorator dec) throws Exception
	{
		StringWriter w=new StringWriter();
		dec.getSetting().toPrefNode().saveTo(new BufferedWriter(w));
		return w.toString();
	}

	public static final String DATASET_HINT="dataset";
	public static final String CLASS_HINT="classSelection";
	public static final String MI_GROUP_KEY="miGroup";
	public static final String MI_GROUP_VALUE="dataset";
	public static final String CLASS_VALUE="classes";

	public static void serializeDataSetHint(XMLStreamWriter writer, HierarchicalSetting setting) throws Exception
	{
		List<HierarchicalSetting> children=new ArrayList<HierarchicalSetting>();
		for(Setting s: setting.getChildren())
		{
			if(s instanceof HierarchicalSetting)
				children.add((HierarchicalSetting)s);
			if(s instanceof MIGroupSetting)
			{
				writer.writeStartElement(DATASET_HINT);
				writer.writeAttribute(MI_GROUP_KEY,s.getName());
				writer.writeAttribute(MI_GROUP_VALUE,((MIGroupSetting) s).getMIGroup().getMIManager().getDataSet().getName());
				writer.writeEndElement();
			}
		}
		for(HierarchicalSetting s: children)
		{
			serializeDataSetHint(writer, s);
		}
	}

	public static void serializeClassSelectionHint(XMLStreamWriter writer, Setting setting) throws Exception
	{
		if(setting instanceof ClassSelectionSetting)
		{
			writer.writeStartElement(CLASS_HINT);
			writer.writeAttribute(MI_GROUP_KEY,setting.getName());
			writer.writeAttribute(CLASS_VALUE,((ClassSelectionSetting) setting).getModel().serialize());
			writer.writeEndElement();
		}
		if(setting instanceof HierarchicalSetting)
			for(Setting s: ((HierarchicalSetting) setting).getChildren())
			{
				serializeClassSelectionHint(writer, s);
			}
		if(setting instanceof BooleanHierarchicalSetting)
			for(Setting s: ((BooleanHierarchicalSetting) setting).getChildren())
			{
				serializeClassSelectionHint(writer, s);
			}

	}

	public void add(RendererDecorator dec) 
	{
		predef.add(dec);
		List<RendererDecorator> renderers=new ArrayList<RendererDecorator>(getSelection());
		renderers.add(dec);
		setSelection(renderers);		
	}

	public void clear()
	{
		setSelection(new ArrayList<RendererDecorator>());
		predef.clear();
	}

}
