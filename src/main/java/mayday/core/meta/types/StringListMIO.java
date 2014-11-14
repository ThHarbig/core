package mayday.core.meta.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.core.XMLTools;
import mayday.core.meta.GenericMIO;
import mayday.core.meta.MIType;
import mayday.core.meta.gui.AbstractMIRenderer;
import mayday.core.meta.gui.AbstractMITableRenderer;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class StringListMIO extends GenericMIO<List<String>> {

	public final static String myType = "PAS.MIO.StringList";
	
	private final static String START_TAG = "<String>";
	private final static String END_TAG = "</String>";
	
	public StringListMIO() {
		Value = new ArrayList<String>();
	}
	
	public StringListMIO(List<String> stringlist) {		
		setValue(stringlist);
	}
	
	@Override
	public void init() {
//		deSerialize(MIType.SERIAL_TEXT,"Test & 1, <Test2, T>\"'est3");
//		deSerialize(MIType.SERIAL_XML,serialize(MIType.SERIAL_XML));		
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
				"Represents a list of character strings as meta informations",
				"String List MIO"
				);
	}

	private String protect(String s ) {
		return s.replace(",","&komma;");
	}
	
	private String unprotect(String s) {
		return s.replace("&komma;",",");
	}
	
	public boolean deSerialize(int serializationType, String serializedForm) {
		Value.clear();
		switch(serializationType) {
		case MIType.SERIAL_TEXT:
			if (serializedForm.length()>0) {
				String[] splits = serializedForm.split(",");
				for (String s : splits)
					Value.add(unprotect(s.trim()));
			}
			return true;
		case MIType.SERIAL_XML:
			Object[] ret = new Object[]{null,0};
			while (true) {
				ret = XMLTools.nextSubstring(serializedForm, START_TAG, END_TAG, (Integer)ret[1]);
				if (ret[0]==null)
					break;
				String s = (String)ret[0];
				s=XMLTools.unxmlize(s);
				Value.add(s); //no trimming here
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
			for (String i: Value)
				if (i!=null)
					ret.append(protect(i)+",");
			if (ret.length()>0)
				return ret.substring(0, ret.length()-1);
			return ret.toString();
		case MIType.SERIAL_XML:
			for (String i: Value)				
				ret.append(START_TAG+XMLTools.xmlize(i)+END_TAG);
			return ret.toString();
		}
		throw new RuntimeException("Unsupported SerializationType "+serializationType);
	}

	@SuppressWarnings("unchecked")
	public AbstractMIRenderer getGUIElement() {
		return new StringListMIORenderer();
	}


	public StringListMIO clone() {
		StringListMIO slm = new StringListMIO();
		slm.deSerialize(MIType.SERIAL_TEXT, this.serialize(SERIAL_TEXT));
		return slm;
	}

	@SuppressWarnings({ "serial", "unchecked" })
	public static class StringListMIORenderer extends AbstractMITableRenderer {

		private StringListMIO value; 
		
		public StringListMIORenderer() {
			tableModel.setColumnCount(1);
			tableField.getColumnModel().getColumn(0).setHeaderValue("Values");
			value = new StringListMIO();
		}

		@Override
		public String getEditorValue() {
			ArrayList<String> theList = new ArrayList<String>();
			for (int i=0; i!=tableModel.getRowCount(); ++i)
				theList.add((String)tableModel.getValueAt(i, 0));
			value.setValue(theList);
			return value.serialize(MIType.SERIAL_TEXT);
		}

		@Override
		public void setEditorValue(String serializedValue) {
			value.deSerialize(MIType.SERIAL_TEXT, serializedValue);
			tableModel.setRowCount(value.getValue().size());
			int position=0;
			for (String s : value.getValue()) {
				tableModel.setValueAt(s, position++, 0);
			}
		}
		
	}
	
	public String getType() {
		return myType;
	}

}
