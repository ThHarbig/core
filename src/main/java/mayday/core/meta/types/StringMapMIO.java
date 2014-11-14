package mayday.core.meta.types;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import mayday.core.XMLTools;
import mayday.core.meta.GenericMIO;
import mayday.core.meta.MIType;
import mayday.core.meta.gui.AbstractMIRenderer;
import mayday.core.meta.gui.AbstractMITableRenderer;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class StringMapMIO extends GenericMIO<Map<String,String>> {

	protected final static String myType = "PAS.MIO.StringMap";
	
	private final static String START_TAG = "<Entry";
	private final static String END_TAG = "/>";
	private final static String KEY_ATTR = "key=\"";
	private final static String VALUE_ATTR = "value=\"";
	private final static String ATTR_END="\"";
	
	public StringMapMIO() {
		Value = new TreeMap<String,String>();
	}
	
	public StringMapMIO(Map<String,String> stringmap) {
		setValue(stringmap);
	}
	
	@Override
	public void init() {
		//deSerialize(MIType.SERIAL_TEXT,"Test 1=5, Test2=BLA&equals;NO , Test3=&");
		//deSerialize(MIType.SERIAL_XML,serialize(MIType.SERIAL_XML));		
	}

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
				"Represents a mapping of character strings to character strings as meta informations",
				"String Map MIO"
				);
	}



	private String protect(String s ) {
		return s.replace(",","&komma;").replace("=","&equals;");
	}
	
	private String unprotect(String s) {
		return s.replace("&komma;",",").replace("&equals;","=");
	}
	
	public boolean deSerialize(int serializationType, String serializedForm) {
		Value.clear();
		switch(serializationType) {
		case MIType.SERIAL_TEXT:
			String[] splits = serializedForm.split(",");
			for (String s : splits) {
				String[] parts = s.split("=");
				String theValue;
				if (parts.length>1)
					theValue = unprotect(parts[1].trim());
				else theValue="";
				if (parts.length>0)
					Value.put(unprotect(parts[0].trim()), theValue);
			}
			return true;							
		case MIType.SERIAL_XML:
			Object[] ret = new Object[]{null,0};
			while (true) {
				ret = XMLTools.nextSubstring(serializedForm, START_TAG, END_TAG, (Integer)ret[1]);
				if (ret[0]==null)
					break;
				String key = (String)XMLTools.nextSubstring((String)ret[0], KEY_ATTR, ATTR_END, 0)[0];
				String value = (String)XMLTools.nextSubstring((String)ret[0], VALUE_ATTR, ATTR_END, 0)[0];				
				Value.put(XMLTools.unxmlize(key), XMLTools.unxmlize(value)); //no trimming here
			}
		}
		return true;
	}
	
	public String serialize(int serializationType) {
		StringBuilder ret = new StringBuilder();
		if (Value.size()==0) 
			return "";
		switch(serializationType) {
		case MIType.SERIAL_TEXT:
			for (Entry<String,String> i: Value.entrySet())
				ret.append(protect(i.getKey())+"="+protect(i.getValue())+",");
			return ret.substring(0, ret.length()-1);
		case MIType.SERIAL_XML:
			for (Entry<String,String> i: Value.entrySet())
				ret.append(START_TAG+" "
						+KEY_ATTR+  XMLTools.xmlize(i.getKey()) + "\" " 
						+VALUE_ATTR+  XMLTools.xmlize(i.getValue()) + "\"" 
						+END_TAG);
			return ret.toString();
		}
		throw new RuntimeException("Unsupported SerializationType "+serializationType);
	}

	@SuppressWarnings("unchecked")
	public AbstractMIRenderer getGUIElement() {
		return new StringMapMIORenderer();
	}


	public StringMapMIO clone() {
		StringMapMIO slm = new StringMapMIO();
		slm.deSerialize(MIType.SERIAL_TEXT, this.serialize(SERIAL_TEXT));
		return slm;
	}

	
	@SuppressWarnings("serial")
	public static class StringMapMIORenderer extends AbstractMITableRenderer<StringMapMIO> {

		private StringMapMIO value; 
		
		public StringMapMIORenderer() {
			tableModel.setColumnCount(2);
			tableField.getColumnModel().getColumn(0).setHeaderValue("Key");
			tableField.getColumnModel().getColumn(1).setHeaderValue("Value");
			value = new StringMapMIO();
		}

		@Override
		public String getEditorValue() {
			TreeMap<String,String> theMap = new TreeMap<String,String>();
			for (int i=0; i!=tableModel.getRowCount(); ++i) {
				String key =(String)tableModel.getValueAt(i, 0);
				String value = (String)tableModel.getValueAt(i, 1);
				if (key!=null) {
					if (value==null)
						value = "";
					theMap.put(key,value);
				}
			}
			value.setValue(theMap);
			return value.serialize(MIType.SERIAL_TEXT);
		}

		@Override
		public void setEditorValue(String serializedValue) {
			value.deSerialize(MIType.SERIAL_TEXT, serializedValue);
			tableModel.setRowCount(value.getValue().size());
			int position=0;
			for (Entry<String,String> e: value.getValue().entrySet()) {
				tableModel.setValueAt(e.getKey(), position, 0);
				tableModel.setValueAt(e.getValue(), position++, 1);
			}
		}
		
	}
	
	
	public String getType() {
		return myType;
	}

}
