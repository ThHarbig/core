package mayday.dynamicpl.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import mayday.core.gui.properties.items.AbstractPropertiesItem;
import mayday.dynamicpl.DynamicProbeList;

@SuppressWarnings("serial")
public class RuleSetEditorItem extends AbstractPropertiesItem  {

	JButton openEditor = new JButton(new OpenEditorAction());
	DynamicProbeList pl;
	RuleEditorPanel rep;

	public RuleSetEditorItem() {
		super("Rule Set");
	}

	public RuleSetEditorItem(DynamicProbeList pl) {
		this();
		setLayout(new BorderLayout());
		this.pl=pl;
		rep = new RuleEditorPanel(pl);
		add(rep, BorderLayout.CENTER);
//		JPanel pnl2 = new JPanel(new BorderLayout());
//		pnl2.add(openEditor, BorderLayout.EAST);
//		add(pnl2, BorderLayout.NORTH);
	}


	public Object getValue() {
		return null;
	}


	@Override
	public boolean hasChanged() {
		return false;
	}

	@Override
	public void setValue(Object value) {
	}
	
	public void apply() {
		rep.doExternalApply();
	}


	protected class OpenEditorAction extends AbstractAction {
		public OpenEditorAction() {
			super( "Open Rule Set Editor" );  		
		}		  	
		public void actionPerformed( ActionEvent event ) {
			new RuleEditorDialog(pl).setVisible(true);
		}
	}

}
