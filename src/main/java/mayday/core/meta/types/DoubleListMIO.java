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

public class DoubleListMIO extends GenericMIO<List<Double>> {

	protected final static String myType = "PAS.MIO.DoubleList";
	
	private final static String START_TAG = "<Double>";
	private final static String END_TAG = "</Double>";
	
	public DoubleListMIO() {
		Value = new ArrayList<Double>();
	}
	
	public DoubleListMIO(List<Double> stringlist) {		
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
				"Represents a list of double precision numeric values as meta information",
				"Double List MIO"
				);
	}

	private String protect(String s ) {
		return s.replace(",","&komma;");
	}
	
	private String unprotect(String s) {
		return s.replace("&komma;",",");
	}
	
	public boolean deSerialize(int serializationType, String serializedForm) {
		try {
			Value.clear();
			switch(serializationType) {
			case MIType.SERIAL_TEXT:
				String[] splits = serializedForm.split(",");
				for (String s : splits)
					Value.add(Double.parseDouble(unprotect(s.trim())));
				return true;
			case MIType.SERIAL_XML:
				Object[] ret = new Object[]{null,0};
				while (true) {
					ret = XMLTools.nextSubstring(serializedForm, START_TAG, END_TAG, (Integer)ret[1]);
					if (ret[0]==null)
						break;
					String s = (String)ret[0];
					s=XMLTools.unxmlize(s);
					Value.add(Double.parseDouble(s)); //no trimming here
				}
			}
		} catch (NumberFormatException nfe) {
			return false;
		}		
		return true;
	}
	
	public String serialize(int serializationType) {
		StringBuilder ret = new StringBuilder();
		if (Value.size()==0) 
			return "";
		switch(serializationType) {
		case MIType.SERIAL_TEXT:
			for (Double i: Value)
				ret.append(protect(""+i)+",");
			return ret.substring(0, ret.length()-1);
		case MIType.SERIAL_XML:
			for (Double i: Value)
				ret.append(START_TAG+XMLTools.xmlize(""+i)+END_TAG);
			return ret.toString();
		}
		throw new RuntimeException("Unsupported SerializationType "+serializationType);
	}

	@SuppressWarnings("unchecked")
	public AbstractMIRenderer getGUIElement() {
		return new DoubleListMIORenderer();
	}


	public DoubleListMIO clone() {
		DoubleListMIO slm = new DoubleListMIO();
		slm.deSerialize(MIType.SERIAL_TEXT, this.serialize(SERIAL_TEXT));
		return slm;
	}

	@SuppressWarnings({ "serial", "unchecked" })
	protected static class DoubleListMIORenderer extends AbstractMITableRenderer {

		private DoubleListMIO value; 
		
		public DoubleListMIORenderer() {
			tableModel.setColumnCount(1);
			tableField.getColumnModel().getColumn(0).setHeaderValue("Double value");
			value = new DoubleListMIO();
		}

		@Override
		public String getEditorValue() {
			ArrayList<Double> theList = new ArrayList<Double>();
			for (int i=0; i!=tableModel.getRowCount(); ++i)
				try {
					theList.add(Double.parseDouble((String)tableModel.getValueAt(i, 0)));
				} catch (NumberFormatException nfe) {				
					theList.add(value.getValue().get(i));
				}
				value.setValue(theList);
			return value.serialize(MIType.SERIAL_TEXT);
		}

		@Override
		public void setEditorValue(String serializedValue) {
			value.deSerialize(MIType.SERIAL_TEXT, serializedValue);
			tableModel.setRowCount(value.getValue().size());
			int position=0;
			for (Double s : value.getValue()) {
				tableModel.setValueAt(""+s, position++, 0);
			}
		}
		
	}
	
	public String getType() {
		return myType;
	}

}
