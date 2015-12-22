package mayday.core.settings.generic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mayday.core.settings.AbstractSettingComponent;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.typed.StringSetting;


public class ObjectSelectionSetting<T> extends StringSetting {

	public enum LayoutStyle {
		COMBOBOX,
		RADIOBUTTONS,
		LIST,
		RADIOBUTTONS_HORIZONTAL
	}

	
	protected T[] predef;
	protected LayoutStyle layout;
	protected String[] toolTips;
	
	public ObjectSelectionSetting(String Name, String Description, int Default, T[] predefined) {
		super(Name, Description, (Default<predefined.length&&Default>=0)?predefined[Default].toString():"");
		predef = predefined;
		layout = LayoutStyle.COMBOBOX;
	}
	
	protected ObjectSelectionSetting(String Name, String Description, T Default, T[] predefined) {
		super(Name, Description, Default!=null?Default.toString():"");
		predef = predefined;
		layout = LayoutStyle.COMBOBOX;
	}
	
	public SettingComponent getGUIElement() {
		switch(layout) {
		case COMBOBOX:	
			return new ObjectSelectionComboBoxSettingComponent(this);
		case RADIOBUTTONS:
			return new ObjectSelectionRadioButtonSettingComponent(this, false);
		case LIST:
			return new ObjectSelectionListSettingComponent(this);
		case RADIOBUTTONS_HORIZONTAL:
			return new ObjectSelectionRadioButtonSettingComponent(this, true);
		}		
		return null;
	}
	
	public ObjectSelectionSetting<T> setToolTips(String[] tooltips) {
		toolTips = tooltips;
		return this;
	}
	
	public T[] getPredefinedValues() {
		return predef;
	}
	
	public T getObjectValue() {
		String name = getValueString();
		for (T o : predef)
			if (o.toString().equals(name))
				return o;
		return null;
	}
	
	public void setObjectValue(T v) {
		setValueString(v.toString());
	}
	
	public ObjectSelectionSetting<T> setLayoutStyle(LayoutStyle style) {
		layout=style;
		return this;
	}
	
	public ObjectSelectionSetting<T> clone() {
		return new ObjectSelectionSetting<T>(name,description,getObjectValue(),predef);
	}
	
	public int getSelectedIndex() {
		Object o = getObjectValue();
		for (int i=0; i!=predef.length; ++i)
			if (predef[i]==o)
				return i;
		for (int i=0; i!=predef.length; ++i)
			if (predef[i].equals(o))
				return i;
		return -1;
	}
	
	public void setSelectedIndex(int i) {
		setObjectValue(predef[i]);
	}
	
	
	@SuppressWarnings("unchecked")
	public static class ObjectSelectionComboBoxSettingComponent extends AbstractSettingComponent<ObjectSelectionSetting> {
		
		protected JComboBox cb;
		protected Object predef;

		public ObjectSelectionComboBoxSettingComponent(ObjectSelectionSetting setting) {
			super(setting);
			predef = mySetting.predef;
		}

		public void stateChanged(SettingChangeEvent e) {
			if (mySetting.predef!=predef && cb!=null) {
				cb.removeAllItems();
				for (Object o : mySetting.predef)
					cb.addItem(o);
				predef = mySetting.predef;
			}
			setSelected();
		}
		
		public void setSelected() {
			String newVal = mySetting.getStringValue();
			int i;
			for (i=0; i!=cb.getItemCount(); ++i)
				if (cb.getItemAt(i).toString().equals(newVal))
					break;
			if (i<cb.getItemCount())
				cb.setSelectedIndex(i);			
			if (cb.getSelectedIndex()==-1 && mySetting.predef.length>0)
				cb.setSelectedIndex(0);
		}

		protected String getCurrentValueFromGUI() {
			if (cb==null || cb.getSelectedItem()==null)
				return null;
			return cb.getSelectedItem().toString();
		}

		@SuppressWarnings("serial")
		@Override
		protected Component getSettingComponent() {
			if (cb==null) {
				cb = new JComboBox(mySetting.getPredefinedValues());
				cb.setMaximumSize(new Dimension(Integer.MAX_VALUE,cb.getPreferredSize().height));
				cb.setRenderer(new DefaultListCellRenderer() {
					 public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus) {
						 if (mySetting.toolTips!=null)
							 this.setToolTipText(mySetting.toolTips[index]);
						 return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					 }
				});
				setSelected();
			}
			return cb;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static class ObjectSelectionRadioButtonSettingComponent extends AbstractSettingComponent<ObjectSelectionSetting> {
		
		protected JRadioButton[] rb;
		protected ButtonGroup bg;
		protected JPanel pnl;
		protected Object predef;
		protected boolean horizontal;

		public ObjectSelectionRadioButtonSettingComponent(ObjectSelectionSetting setting, boolean horizontal) {
			super(setting);
			predef = mySetting.predef;
			this.horizontal = horizontal;
		}

		public void stateChanged(SettingChangeEvent e) {
			if (mySetting.predef!=predef && pnl!=null) {
				pnl.removeAll(); 
				fillPanel();
				predef = mySetting.predef;
			}
			setSelected();
		}
		
		public void setSelected() {			
			String newVal = mySetting.getStringValue();
			int i;
			for (i=0; i!=mySetting.predef.length; ++i)
				if (newVal.equals(mySetting.predef[i]))
					break;
			if (i==mySetting.predef.length)
				i=0;
			rb[i].setSelected(true);
		}

		protected String getCurrentValueFromGUI() {
			if (rb!=null)
				for (JRadioButton jrb : rb)
					if (jrb.isSelected())
						return jrb.getText();
			return null;
		}
		
		protected void fillPanel() {
			bg = new ButtonGroup();
			rb = new JRadioButton[mySetting.predef.length];
			
			int i=0;
			for (Object o : mySetting.predef)  
				pnl.add( rb[i++] = new JRadioButton(o.toString()) );
			
			i=0;
			if (mySetting.toolTips!=null)				
				for (String s : mySetting.toolTips)  
					rb[i++].setToolTipText(s);
			
			for (JRadioButton jrb : rb)
				bg.add(jrb);			
		}

		@Override
		protected Component getSettingComponent() {
			if (bg==null) {
				pnl = new JPanel();
				pnl.setLayout(new BoxLayout(pnl, horizontal ? BoxLayout.LINE_AXIS : BoxLayout.PAGE_AXIS));				
				fillPanel();
			}
			setSelected();
			return pnl;
		}

		
	}
	
	@SuppressWarnings("unchecked")
	public static class ObjectSelectionListSettingComponent extends AbstractSettingComponent<ObjectSelectionSetting> {
		
		protected MenuList ml;
		protected JScrollPane jsp;
		protected Object predef;

		public ObjectSelectionListSettingComponent(ObjectSelectionSetting setting) {
			super(setting);
			predef = mySetting.predef;
		}

		public void stateChanged(SettingChangeEvent e) {
			if (mySetting.predef!=predef && ml!=null) {
				predef = mySetting.predef;
				ml.fill();
			}
			if (ml!=null)
				setSelected();
		}
		
		public void setSelected() {	
			String newVal = mySetting.getStringValue();
			int i=0;
			for (Object o : mySetting.predef) {
				if (o.toString().equals(newVal))
					ml.setSelectedIndex(i);
				++i;
			}
			if (ml.getSelectedIndex()==-1 && mySetting.predef.length>0)
				ml.setSelectedIndex(0);
		}

		protected String getCurrentValueFromGUI() {
			if (ml==null || ml.getSelectedValue()==null)
				return null;
			return ml.getSelectedValue().toString();
		}

	
		@Override
		protected Component getSettingComponent() {
			if (ml==null) {
				ml = new MenuList(mySetting, false);
				jsp = new JScrollPane(ml);
			}
			setSelected();
			return jsp;
		}
		
	}
	
	
	public Component getMenuItem( final Window parent ) {
		JMenu mnu = new JMenu(getName());
		final MenuList<T> ml = new MenuList<T>(this, true);
		addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				ml.fill();
			}
		});
		ml.setBackground(mnu.getBackground());
		mnu.add(ml);
		return mnu;
	}
	
	@SuppressWarnings("serial")
	protected static class MenuList<T> extends JList{
		
		public ObjectSelectionSetting<T> mySetting;

		public MenuList(ObjectSelectionSetting<T> setting, boolean reactDirectly){
			super(new DefaultListModel());
			mySetting = setting;
			setVisibleRowCount(12);
			setSelectedIndex(0);	
			setForeground(Color.BLACK);			
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
			if (reactDirectly) {
				addListSelectionListener(new ListSelectionListener() {					
					public void valueChanged(ListSelectionEvent event) {
						if (getSelectedIndex()>-1)
							mySetting.setStringValue(getSelectedValue().toString());
					}
				});
			}
			fill();
		}
		
		public DefaultListModel getModel() {
			return (DefaultListModel)super.getModel();
		}
		
		public void fill() {
			getModel().clear();
			int seli=-1;
			int i=0;
			for (T o : mySetting.predef) {
				getModel().addElement(o.toString());
				if (o.toString().equals(mySetting.getStringValue()))
					seli=i;
				++i;
			}
			if (mySetting.toolTips!=null)
				setCellRenderer(new DefaultListCellRenderer() {
					 public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus) {
						 if (mySetting.toolTips!=null)
							 this.setToolTipText(mySetting.toolTips[index]);
						 return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					 }
				});
			
			setSelectedIndex(seli);
		}
		
	}


}
