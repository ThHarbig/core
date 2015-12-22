/**
 * 
 */
package mayday.core.io.dataset.tabular;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.gui.tablespecials.EditableHeaders.TableHeaderPanel;

@SuppressWarnings("serial")
public class ColumnHeader extends TableHeaderPanel {
	
	private JLabel title = new JLabel();
	private ButtonGroup bg;
	private JRadioButton[] jrb;
	private JComboBox jcb;		
	private Object[] comboItems;

	public ColumnHeader() {
		int tc = ColumnType.values().length;
		int rows = 2+tc;
		setLayout(new GridLayout(rows,1));
		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		title.setAlignmentY(CENTER_ALIGNMENT);
		add(title);
		bg = new ButtonGroup();
		jrb = new JRadioButton[tc];
		for (int i=0; i!=tc; ++i) {
			jrb[i] = new JRadioButton(ColumnType.values()[i].name());
			bg.add(jrb[i]);
			add(jrb[i]);
		}
		jcb = new JComboBox();
		add(jcb);			
		
		jcb.setEnabled(false);
		final JRadioButton mioButton = jrb[ColumnType.MetaInfo.ordinal()];
		mioButton.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				jcb.setEnabled(mioButton.isSelected());
			}
    		
    	});
	}

	public void setComboItems(Object[] items) {
		comboItems = items;
		jcb.removeAllItems();
		if (items!=null)
			for (Object o : comboItems)
				jcb.addItem(o);
	}
	
	public void setColumnIndex(int colIndex) {
		try {
			jcb.setVisible(colIndex>0);
			for (JRadioButton rb : jrb)
				rb.setVisible(colIndex>0);
		} catch (NullPointerException e) {/*forget it*/}
	}

	public void setComboItem(Object item) {
		jcb.setSelectedItem(item);
	}
	
	public void setTitle(String Title) {
		if (title==null) 
			title = new JLabel();
		title.setText(Title);
	}
	
	public void setType(ColumnType ct) {
		//System.out.println(id+" "+title.getText()+": "+ct+"  settype");
		for (JRadioButton rb : jrb)
			if (rb.getText().equals(ct.name()))
				rb.setSelected(true);
	}

	@Override
	public TableHeaderPanel clone() {
		ColumnHeader ch = new ColumnHeader();
		ch.setComboItems(comboItems);
		ch.setValue(getValue());
		return ch;
	}

	@Override
	public Object getValue() {
		ColumnType ct = ColumnType.Ignore;
		for (int i=0; i!=jrb.length; ++i)
			if (jrb[i].isSelected())
				ct = ColumnType.valueOf(jrb[i].getText());
		//System.out.println(id+" "+title.getText()+": "+ct);
		Object[] oo = jcb.getSelectedObjects();
		Object o = null;
		if (oo.length>0)
			o = oo[0];
		return new Object[]{ct, o};
	}

	@Override
	public void setValue(Object value) {
		if (value!=null && value instanceof Object[] && ((Object[])value).length==2) {
			Object[] v = (Object[])value;
			if (v[0] instanceof ColumnType)
				setType((ColumnType)v[0]);
			jcb.setSelectedItem(v[1]);
		}
	}

}