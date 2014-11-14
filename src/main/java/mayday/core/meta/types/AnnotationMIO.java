package mayday.core.meta.types;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import mayday.core.meta.MIType;
import mayday.core.meta.WrappedMIO;
import mayday.core.meta.gui.AbstractMIRenderer;
import mayday.core.meta.gui.MIRendererDefault;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class AnnotationMIO extends StringMapMIO {

	private final static String myType = "PAS.MIO.Annotation";
	
	private final static String INFO_KEY = "Info";
	private final static String QUICKINFO_KEY = "Quickinfo";
	
	public AnnotationMIO() {
		super();
		Value.put(INFO_KEY,"");
		Value.put(QUICKINFO_KEY,"");
	}

	public AnnotationMIO(String info, String quickinfo) {
		super();
		Value.put(INFO_KEY,info);
		Value.put(QUICKINFO_KEY,quickinfo);
	}
	
	public String getInfo() {
		return Value.get(INFO_KEY);
	}
	
    public void setInfo( String info ) {
    	Value.put(INFO_KEY,info);
    }
    
	public String getQuickInfo() {
		return Value.get(QUICKINFO_KEY);
	}

    public void setQuickInfo( String quickinfo ) {
    	Value.put(QUICKINFO_KEY,quickinfo);
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
				"Represents annotations (name, description) as meta information",
				"Annotation MIO"
				);
	}
	

	@SuppressWarnings("unchecked")
	public AbstractMIRenderer getGUIElement() {
		return new AnnotationMIORenderer();
	}
	
	public AnnotationMIO clone() {
		AnnotationMIO slm = new AnnotationMIO();
		slm.deSerialize(MIType.SERIAL_TEXT, this.serialize(SERIAL_TEXT));
		return slm;
	}
	
	public void clear() {
		Value.clear();
		Value.put(INFO_KEY,"");
		Value.put(QUICKINFO_KEY,"");
	}
	
	
	public String getType() {
		return myType;
	}

	
	@SuppressWarnings("serial")
	private static class AnnotationMIORenderer extends AbstractMIRenderer<AnnotationMIO> {
		
		private class EditorPanel extends JPanel {
			public JTextArea info = new JTextArea();
			public JTextArea quickInfo = new JTextArea();
			private AnnotationMIO value; 

			public EditorPanel() {
				setLayout(new GridBagLayout());

				JScrollPane infoSP = new JScrollPane(info);
				infoSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				JScrollPane quickInfoSP = new JScrollPane(quickInfo);
				quickInfoSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

				GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,1.0,0.0,GridBagConstraints.PAGE_END,
						GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0);
				add(new JLabel("Info"),gbc);
				gbc.gridy+=2;
				add(new JLabel("Quick Info"),gbc);
				
				gbc.gridy--;
				gbc.fill = GridBagConstraints.BOTH;
				gbc.weighty = .3;
				add(infoSP,gbc);
				gbc.gridy+=2;
				gbc.weighty = .7;
				add(quickInfoSP,gbc);
				value = new AnnotationMIO();				
				infoSP.setMinimumSize(new Dimension(50,100));
				quickInfoSP.setMinimumSize(new Dimension(50,100));
				info.setWrapStyleWord(true);
				info.setLineWrap(true);
				quickInfo.setWrapStyleWord(true);
				quickInfo.setLineWrap(true);
				setEditable(false);

			}
			public void setValue(String serializedValue) {
				value.deSerialize(MIType.SERIAL_TEXT, serializedValue);
				info.setText(value.getValue().get(AnnotationMIO.INFO_KEY));
				quickInfo.setText(value.getValue().get(AnnotationMIO.QUICKINFO_KEY));
			}
			public String getValue() {
				value.getValue().put(INFO_KEY, info.getText());
				value.getValue().put(QUICKINFO_KEY, quickInfo.getText());
				return value.serialize(MIType.SERIAL_TEXT);
			}		
			public void setEditable(boolean editable) {
				info.setEditable(editable);
				quickInfo.setEditable(editable);
			}
		}
		
		private class CellPanel extends JTextArea {
			
			public CellPanel() {
				setLineWrap(true);
				setWrapStyleWord(true);
				Font f = getFont();
				f = new Font(f.getFontName(), f.getStyle(), f.getSize()-2);
				setFont(f);	
				//setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
			}
			
			public void setText(AnnotationMIO amio) {
				String info2display = amio.getQuickInfo();
				if (info2display.equals(""))
					info2display = amio.getInfo();
				if (info2display.equals(""))
					info2display = "(empty)";
				// trim text to k chars
				if (info2display.length()>150) {
					info2display = info2display.substring(0, 144) + " (...)";
				}
				setText(info2display);
				//jta.setText(info2display);
			}
		}
		
		private EditorPanel editorPanel;
		
		public AnnotationMIORenderer() {
			super();
			editorPanel = new EditorPanel();
		}

		@Override
		public String getEditorValue() {
			return editorPanel.getValue();
		}

		public void setEditable(boolean editable) {
			editorPanel.setEditable(editable);
		}
		
		@Override
		public void setEditorValue(String serializedValue) {
			editorPanel.setValue(serializedValue);
		}

		@Override
		public Component getEditorComponent() {
			return editorPanel;
		}

		
		
		private CellPanel cp = new CellPanel();
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int col) {
			
			MIType theMIO=null;
			
			if (value instanceof WrappedMIO) {
				WrappedMIO wm = (WrappedMIO)value;
				theMIO = wm.getMio(); 
			} else if (value instanceof MIType) {
				theMIO = (MIType)value;
			}
			
			if (theMIO instanceof AnnotationMIO) {
				AnnotationMIO am = (AnnotationMIO)theMIO;
				cp.setText(am);
				// fit size
				if (table!=null) {
					cp.setSize(table.getColumnModel().getColumn(col).getWidth(), getPreferredSize().height);
					int rowHeight = cp.getPreferredSize().height;
					if (table.getRowHeight(row) != rowHeight) 
						table.setRowHeight(row, rowHeight);
				}
				// show selection
			    if (selected) {
			        cp.setBackground(Color.ORANGE);
			    }else {
			        cp.setBackground(Color.white);
			    }

				return cp;
			}
			
			return new MIRendererDefault().getTableCellRendererComponent(table, value, selected, focused, row, col);
		}
	}
	
}
