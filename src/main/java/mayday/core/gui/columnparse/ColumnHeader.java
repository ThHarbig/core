/**
 * 
 */
package mayday.core.gui.columnparse;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;

import mayday.core.gui.tablespecials.EditableHeaders.TableHeaderPanel;

@SuppressWarnings("serial")
public class ColumnHeader<ColumnType> extends TableHeaderPanel {
	
	private JLabel title = new JLabel();
	private ButtonGroup bg;
	private JRadioButton[] jrb;
	private ColumnTypes<ColumnType> types;

	public ColumnHeader( ColumnTypes<ColumnType> types ) {
		this.types = types;
		int tc = types.values().length+1;
		int rows = 1+tc;
		setLayout(new GridLayout(rows,1));
		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		title.setAlignmentY(CENTER_ALIGNMENT);
		add(title);
		bg = new ButtonGroup();
		jrb = new JRadioButton[tc];
		jrb[0] = new JRadioButton("Ignore");
		bg.add(jrb[0]);
		add(jrb[0]);
		for (int i=1; i!=tc; ++i) {
			jrb[i] = new JRadioButton(types.values()[i-1].toString());
			bg.add(jrb[i]);
			add(jrb[i]);
		}
	}

	public void setColumnIndex(int colIndex) {
//		try {
//			for (JRadioButton rb : jrb)
//				rb.setVisible(colIndex>0);
//		} catch (NullPointerException e) {/*forget it*/}
	}

	public void setTitle(String Title) {
		if (title==null) 
			title = new JLabel();
		title.setText(Title);
	}
	
	public void setType(ColumnType ct) {
		if (ct==null)
			jrb[0].setSelected(true);
		else
			jrb[ types.indexOf(ct)+1 ].setSelected(true);
	}

	@Override
	public TableHeaderPanel clone() {
		ColumnHeader<ColumnType> ch = new ColumnHeader<ColumnType>(types);
		ch.setValue(getValue());
		return ch;
	}

	@Override
	public Object getValue() {
		Object ct = null;
		for (int i=1; i!=jrb.length; ++i)
			if (jrb[i].isSelected())
				ct = types.typeOf(jrb[i].getText());
		//System.out.println(id+" "+title.getText()+": "+ct);		
		return ct;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object value) {
		setType((ColumnType)value);
	}

}