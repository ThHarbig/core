package mayday.vis3.graph.dialog;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mayday.core.gui.components.ExcellentBoxLayout;

@SuppressWarnings("serial")
public class PropertyEditor extends JPanel
{
	private Map<String,String> map;

	private List<PropertyEditPanel> panels=new ArrayList<PropertyEditPanel>();
	
	public PropertyEditor(Map<String,String> map) 
	{
		this.map=map;
		setLayout(new ExcellentBoxLayout(true, 10));
		
		Box buttonBox=Box.createHorizontalBox();
		buttonBox.add(new JButton(new AddAction()));
		add(buttonBox);
		List<String> keys=new ArrayList<String>(map.keySet());
		for(String k:keys)
		{
			PropertyEditPanel panel=new PropertyEditPanel(k, this.map.get(k));
			add(panel);
			panels.add(panel);
		}
	}
	
	public Map<String,String> getProperties()
	{
		Map<String, String> res= new HashMap<String, String>(panels.size());
		for(PropertyEditPanel p:panels)
		{
			res.put(p.getKey(), p.getValue());
		}
		return res;
	}

	public boolean checkConsistency()
	{
		Set<String> keys=new HashSet<String>();
		for(PropertyEditPanel p:panels)
		{
			if(keys.contains(p.getKey()))
				return false;
			else
				keys.add(p.getKey());
		}
		return true;
	}
	
	private class AddAction extends AbstractAction
	{
		public AddAction() 
		{
			super("Add Property");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			PropertyEditPanel panel=new PropertyEditPanel("New", "Property");
			add(panel);
			panels.add(panel);	
			revalidate();
//			setPreferredSize(new Dimension(panel.getWidth(), (panel.getHeight()+10)*panels.size()));
			revalidate();	
		}
	}
	
	private class RemoveAction extends AbstractAction
	{
		private PropertyEditPanel panel;
		
		public RemoveAction(PropertyEditPanel panel) 
		{
			super("Remove");
			this.panel=panel;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			PropertyEditor.this.remove(panel);
			panels.remove(panel);
			repaint();
		}
	}
	
	private class PropertyEditPanel extends JPanel
	{
		private JTextField key;
		private JTextField valueField;
		
		public PropertyEditPanel(String key, String value)
		{
			super();
			this.key=new JTextField(key,10);
			valueField=new JTextField(value!=null?value:"");
			setLayout(new GridLayout(1,3));
			add(this.key);
			add(valueField);
			add(new JButton(new RemoveAction(this)));
			
		}

		public String getKey() {
			return key.getText();
		}
		
		public String getValue()
		{
			return valueField.getText();
		}
	}
	
	
}


