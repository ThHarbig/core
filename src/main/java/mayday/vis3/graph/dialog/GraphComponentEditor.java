package mayday.vis3.graph.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import mayday.core.gui.MaydayDialog;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.EditableGraphComponent;
import mayday.core.structures.graph.edges.Edges;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.graph.nodes.Nodes;

/**
 * @author symons
 *
 */
@SuppressWarnings("serial")
public class GraphComponentEditor extends MaydayDialog 
{
	private boolean cancelled;
	
	private EditableGraphComponent object; 
	
	public EditableGraphComponent getObject() {
		return object;
	}

	private JTextField nameField;
	private JComboBox roleBox;
	private PropertyEditor propertyEditor;
	
	private List<String> addedRoles=new ArrayList<String>();
	
	public GraphComponentEditor(EditableGraphComponent node)
	{
		super();
		setTitle(node.getName());
		this.object=node;
		nameField=new JTextField(node.getName(), 30);
		Vector<String> items=new Vector<String>();
		items.add(node.getRole());
		if(node instanceof DefaultNode)
		{
			for(String s: Nodes.Roles.ROLES)
				items.add(s);
		}
		if(node instanceof Edge)
		{
			for(String s: Edges.Roles.ROLES)
				items.add(s);
		}
		
		roleBox=new JComboBox(items);
		roleBox.setSelectedIndex(0);
		
		setLayout(new BorderLayout());
//		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		
		nameField.setBorder(BorderFactory.createTitledBorder("Name"));
//		add(nameField);
		
//		add(roleBox);
		
		JPanel nameRolePanel=new JPanel();
		nameRolePanel.setLayout(new GridLayout(2, 1));
		nameRolePanel.add(nameField);
		
		Box roleBoxBox=Box.createHorizontalBox();
		roleBoxBox.add(roleBox);
		roleBoxBox.add(Box.createHorizontalStrut(10));
		roleBoxBox.add(new JButton(new AddRoleAction()));
		roleBoxBox.setBorder(BorderFactory.createTitledBorder("Role"));
		
		nameRolePanel.add(roleBoxBox);
		
		
		
		add(nameRolePanel,BorderLayout.NORTH);
		
		propertyEditor=new PropertyEditor(node.getProperties()!=null?node.getProperties():new HashMap<String, String>());
		
		JScrollPane propertiesPane=new JScrollPane(propertyEditor);
		propertiesPane.setPreferredSize(new Dimension(200,200));

//		propertiesPane.setMinimumSize(new Dimension(250, 200));
		add(propertiesPane, BorderLayout.CENTER);
		
		Box buttonBox=Box.createHorizontalBox();
		JButton okButton=new JButton(new CancelOKAction(true));
		okButton.setMnemonic(KeyEvent.VK_ENTER);
		buttonBox.add(okButton);
		
		buttonBox.add(Box.createHorizontalGlue());	
		JButton cButton=new JButton(new CancelOKAction(false));
		cButton.setMnemonic(KeyEvent.VK_ESCAPE);
		buttonBox.add(cButton);
		
		add(buttonBox, BorderLayout.SOUTH);		
		pack();
	}
	
	
	
	public boolean isCancelled() {
		return cancelled;
	}



	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public void setRoles(Iterable<String> roles)
	{
		roleBox.removeAllItems();
		for(String s: roles)
		{
			roleBox.addItem(s);
		}
		roleBox.insertItemAt(object.getRole(), 0);
		roleBox.setSelectedIndex(0);
	}


	public void addAdditionalRoles(String[] roles)
	{
		for(String s:roles)
			roleBox.addItem(s);
	}

	private class CancelOKAction extends AbstractAction
	{
		private boolean cancel;
		
		public CancelOKAction(boolean c) 
		{
			super(c?"Cancel":"Ok");
			cancel=c;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			if(!cancel)
			{
				if(!propertyEditor.checkConsistency())
				{
					JOptionPane.showMessageDialog(GraphComponentEditor.this, "Ambigous Property Name\n!Remove property key name clash before continuing.", "Property Name Clash", JOptionPane.ERROR_MESSAGE);
				}
				object.setName(nameField.getText());
				if(roleBox.getSelectedItem()==null)
					object.setRole(Edges.Roles.EDGE_ROLE);
				else
					object.setRole(roleBox.getSelectedItem().toString());
				if(!propertyEditor.getProperties().isEmpty())
					object.setProperties(propertyEditor.getProperties());
			}
			cancelled=cancel;
			dispose();			
		}
	}
	
	private class AddRoleAction extends AbstractAction
	{
		public AddRoleAction() 
		{
			super("Add");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			String s= JOptionPane.showInputDialog("New Role:",null );
			if(s!=null)
			{
				roleBox.addItem(s);
				roleBox.setSelectedItem(s);
				addedRoles.add(s);
			}
		}
	}
	
	public List<String> getAddedRoles() {
		return addedRoles;
	}
	
	
	
}
