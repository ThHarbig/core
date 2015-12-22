package mayday.core.structures;

import java.util.Enumeration;

import javax.swing.DefaultListModel;

@SuppressWarnings("serial")
public class StringListModel extends DefaultListModel {

	public String get(int index) {
		return (String)super.elementAt(index);
	}

    @SuppressWarnings("unchecked")
	public Enumeration<String> elements() {
		return (Enumeration<String>)super.elements();
	}
    
    public void add(String s) {
    	super.addElement(s);
    }

}
	
