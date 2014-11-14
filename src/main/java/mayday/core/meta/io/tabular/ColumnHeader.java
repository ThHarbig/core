/**
 * 
 */
package mayday.core.meta.io.tabular;

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
	private JRadioButton[] columnTypes;
	private JComboBox mioType;		
	private Object[] mioTypes;

	public ColumnHeader() {
		int tc = ColumnType.values().length;
		int rows = 2+tc;
		setLayout(new GridLayout(rows,1));
		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		title.setAlignmentY(CENTER_ALIGNMENT);
		add(title);
		bg = new ButtonGroup();
		columnTypes = new JRadioButton[tc];
		for (int i=0; i!=tc; ++i) {
			columnTypes[i] = new JRadioButton(ColumnType.values()[i].name());
			bg.add(columnTypes[i]);
			add(columnTypes[i]);
		}
		mioType = new JComboBox();
		add(mioType);			
		
		mioType.setEnabled(false);
		final JRadioButton mioButton = columnTypes[ColumnType.MetaInfo.ordinal()];
		mioButton.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				mioType.setEnabled(mioButton.isSelected());
			}
    		
    	});
	}

	public void setComboItems(Object[] items) {
		mioTypes = items;
		mioType.removeAllItems();
		if (items!=null)
			for (Object o : mioTypes)
				mioType.addItem(o);
	}
	
	public void setColumnIndex(int colIndex) {
		// nothing to do
	}

	public void setComboItem(Object item) {
		mioType.setSelectedItem(item);
	}
	
	public void setTitle(String Title) {
		if (title==null) 
			title = new JLabel();
		title.setText(Title);
	}
	
	public void setType(ColumnType ct) {
		//System.out.println(id+" "+title.getText()+": "+ct+"  settype");
		for (JRadioButton rb : columnTypes)
			if (rb.getText().equals(ct.name()))
				rb.setSelected(true);
	}

	@Override
	public TableHeaderPanel clone() {
		ColumnHeader ch = new ColumnHeader();
		ch.setComboItems(mioTypes);
		ch.setValue(getValue());
		return ch;
	}

	@Override
	public Object getValue() {
		ColumnType ct = ColumnType.Ignore;
		for (int i=0; i!=columnTypes.length; ++i)
			if (columnTypes[i].isSelected())
				ct = ColumnType.valueOf(columnTypes[i].getText());
		//System.out.println(id+" "+title.getText()+": "+ct);
		Object[] oo = mioType.getSelectedObjects();
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
			mioType.setSelectedItem(v[1]);
		}
	}

}