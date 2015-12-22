/*
 * Created on 02.12.2005
 */
package mayday.core.meta.io.datasetmio;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.Utilities;
import mayday.core.gui.MaydayDialog;
import mayday.core.gui.abstractdialogs.AbstractStandardDialogComponent;
import mayday.core.gui.components.AbstractProblemLabel;
import mayday.core.meta.MIType;
import mayday.core.meta.io.probemio.CSVImportPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;

/**
 * @author Matthias Zschunke
 * @version 0.1
 * Created on 02.12.2005
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractCSVMIOImportComponent 
extends AbstractStandardDialogComponent
{
	protected static final String[] STANDARD_ENTRIES = new String[] {
		"Key (Probe IDs)",  
		"Ignore",
		""
	};
	protected static final int KEY_KEY = 0;
	protected static final int IGNORE_KEY = KEY_KEY + 1;
	//private static final int SEPARATOR_KEY = IGNORE_KEY + 1;
	protected static final Icon WARNING_ICON = Utilities.getWarningIcon();


	protected ArrayList<Action> actions = new ArrayList<Action>();
	protected DataSet dataSet;
	protected TableModel model;
	protected JTable table;

	/**
	 * @param direction
	 */
	public AbstractCSVMIOImportComponent(DataSet dataSet, TableModel model)
	{
		super(BoxLayout.Y_AXIS);

		if(model==null)
			throw new IllegalArgumentException("Table model may not be null!");
		if(model.getColumnCount()<=0)
			throw new IllegalArgumentException("Table model may not be empty!");
		if(dataSet==null)
			throw new IllegalArgumentException("Data set may not be null!");


		this.model = model;
		this.dataSet = dataSet;

		compose();
	}

	/**
	 * 
	 */
	protected void compose()
	{
		removeAll();

		actions.clear();

		/*
		 * prepare components
		 */
		//final JComboBox combo = new JComboBox(new MIOParserComboBoxModel());

		table = new JTable(model) {

			private boolean created = false;
			public void createDefaultColumnsFromModel() 
			{
				if(created) return;
				created = true; 
				TableModel m = getModel();

				// Create new columns from the data model info
				for (int i = 0; i < m.getColumnCount(); i++) 
				{
					TableColumn newColumn = new TableColumn0(i);                   
					addColumn(newColumn);
				}

			}


		};
		((TableColumn0)table.getColumnModel().getColumn(0)).type = STANDARD_ENTRIES[KEY_KEY];

		final Action mergeAction = new AbstractAction("Merge Columns ...") {
			public void actionPerformed(ActionEvent e)
			{
				// ask for seperator
				String separator = JOptionPane.showInputDialog(
						AbstractCSVMIOImportComponent.this, 
						"You are about to merge "+table.getSelectedColumnCount()+" columns. " +
						"\nInsert a seperator that will be placed between the entries of the columns",
						"Merge Columns ...", //title
						JOptionPane.QUESTION_MESSAGE                    
				);

				if(separator!=null)
				{
					//get selected Columns
					int[] sel = table.getSelectedColumns();
//					String header = table.getColumnName(sel[0]);
//					for(int c=1; c<sel.length;++c) 
//						header += (separator + table.getColumnName(sel[c])); 

					for(int r=0; r!=table.getModel().getRowCount();++r)
					{
						String value = (String)table.getValueAt(r, sel[0]);
						for(int c=1; c<sel.length; ++c)
						{
							String v0 = (String)table.getValueAt(r, sel[c]);
							value += (separator + ( v0==null ? "" : v0 ) );  
						} 

						table.setValueAt(value, r, sel[0]);
					}  

					for(int c=1; c<sel.length; ++c)
					{
						((DefaultTableColumnModel)table.getColumnModel()).removeColumn(
								table.getColumnModel().getColumn(sel[c])
						);
					}
				}
			}};
			mergeAction.setEnabled(false);
			final Action editAction = new AbstractAction("Search/Replace ...") {
				private JTextField matchField;
				private JTextField replaceField;

				private void apply()
				{
					int sel = table.getSelectedColumn();
					TableColumn0 c = (TableColumn0)table
					.getColumnModel().getColumn(sel);

					if(matchField.getText().length()!=0)
					{
						c.repl = new ReplaceInfo(
								matchField.getText(), 
								replaceField.getText());

					}else
					{
						c.repl = null;
					}

					((AbstractTableModel)table.getModel()).fireTableDataChanged();
					table.setColumnSelectionInterval(sel,sel);
				}

				public void actionPerformed(ActionEvent e)
				{
					JPanel p = new JPanel(new GridLayout(2,2,5,2));
					final ReplaceInfo oldRi = ((TableColumn0)table
							.getColumnModel().getColumn(table.getSelectedColumn())).repl;
					matchField = new JTextField(20);
					replaceField = new JTextField(20);
					if(oldRi!=null)
					{
						matchField.setText(oldRi.regexp);
						replaceField.setText(oldRi.replacement);
					}

					final JDialog dlg = new MaydayDialog();
					final AbstractAction applyAction = new AbstractAction("Apply") {
						public void actionPerformed(ActionEvent e)
						{
							apply();                        
						}};
						final AbstractAction okAction = new AbstractAction("Ok") {
							public void actionPerformed(ActionEvent e)
							{
								apply();                        
								dlg.dispose();
							}};

							p.setAlignmentX(JPanel.LEFT_ALIGNMENT);
							AbstractProblemLabel matchLabel = new AbstractProblemLabel("Match")
							{
								protected boolean accept(String text)
								{
									try
									{
										@SuppressWarnings("unused")
										Pattern p = Pattern.compile(text);
										okAction.setEnabled(true);
										applyAction.setEnabled(true);
										return true;
									}catch(Exception ex)
									{
										okAction.setEnabled(false);
										applyAction.setEnabled(false);
										return false;
									}
								}

								protected String getProblemText()
								{
									return "The regular expression currently inserted does not compile.";
								}                    
							};
							matchField.getDocument().addDocumentListener(matchLabel);
							p.add(matchLabel);
							p.add(matchField);
							p.add(new JLabel("Replacement"));
							p.add(replaceField);


							Box b = Box.createVerticalBox();
							b.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

							Box b0 = Box.createHorizontalBox();
							b0.add(p);
							b0.add(Box.createHorizontalGlue());

							b.add(b0);
							b.add(Box.createVerticalStrut(10));


							//Buttons
							b0 = Box.createHorizontalBox();
							b0.add(new JButton(new AbstractAction("Help ...") {
								public void actionPerformed(ActionEvent e)
								{
									final JDialog dlg = new MaydayDialog();
									dlg.setTitle("Regular Expression Help");
									String html = new String(
											Utilities.fetchBytes(
													PluginManager.getInstance().getFilemanager().getFile("mayday/util/help/Regexp.html").getStream()
											));
									Box b = Box.createVerticalBox();
									b.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
									JLabel label = new JLabel();
									label.setMaximumSize(new Dimension(300,Integer.MAX_VALUE));
									label.setText(html);
									b.add(label);
									b.add(Box.createVerticalStrut(10));
									b.add(new JButton(new AbstractAction("Close") {
										public void actionPerformed(ActionEvent e)
										{
											dlg.dispose();                      
										}})); 
									dlg.getContentPane().add(b);
									dlg.pack();
									dlg.setModal(true);
									dlg.setSize(new Dimension(
											dlg.getContentPane().getSize().width+dlg.getInsets().left+dlg.getInsets().right,
											dlg.getContentPane().getSize().height+dlg.getInsets().top+dlg.getInsets().bottom
									));
									dlg.setVisible(true);
								}}));
							b0.add(Box.createHorizontalStrut(100));
							b0.add(Box.createHorizontalGlue());
							b0.add(new JButton(applyAction));
							b0.add(new JButton(new AbstractAction("Remove") {
								public void actionPerformed(ActionEvent e)
								{
									((TableColumn0)table.getColumnModel().getColumn(
											table.getSelectedColumn())).repl = null;
									matchField.setText("");
									replaceField.setText("");                        
								}}));
							b0.add(new JButton(new AbstractAction("Cancel") {
								public void actionPerformed(ActionEvent e)
								{
									((TableColumn0)table.getColumnModel().getColumn(
											table.getSelectedColumn())).repl = oldRi;
									dlg.dispose();                        
								}}));
							b0.add(new JButton(okAction));
							b.add(b0);


							dlg.setTitle("Edit Column '"+table.getColumnName(table.getSelectedColumn())+"'");
							dlg.getContentPane().add(b);
							dlg.pack();
							dlg.setModal(true);
							dlg.setSize(new Dimension(
									dlg.getContentPane().getSize().width+dlg.getInsets().left+dlg.getInsets().right,
									dlg.getContentPane().getSize().height+dlg.getInsets().top+dlg.getInsets().bottom
							));
							dlg.setVisible(true);                
				}
			};
			editAction.setEnabled(false);

			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.setCellSelectionEnabled(false);
			table.setColumnSelectionAllowed(true);
			table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
				{
					super.getTableCellRendererComponent(
							table,
							value,
							isSelected,
							hasFocus,
							row,
							column);

					TableColumn0 c0 = (TableColumn0)table.getColumnModel().getColumn(column);
					ReplaceInfo ri = c0.repl;
					String v = ri==null? (String)value : ((String)value).replaceAll(ri.regexp, ri.replacement); 
					setIcon(null);
					if(v==null || v.length()==0)
					{
						setText("<html><span style=\"color:#ff7f7f\">NA</span>");
					}
					if(c0.type==STANDARD_ENTRIES[IGNORE_KEY])
					{
						setText("<html><span style=\"color:#7f7f7f\">"+v+"</span></body>");

					}else if(c0.type==STANDARD_ENTRIES[KEY_KEY] && v!=null)
					{						
						//try to match probe: if yes black-on-white else gray in braces
						Probe p = dataSet.getMasterTable().getProbe(v.trim());
						if(p==null)
						{
							setText("<html><span style=\"color:#7f7f7f\">["+v+"]</span>");
						}else
						{
							setText(v);
						}                    
					}else if(c0.type instanceof PluginInfo)
					{
						try
						{
							MIType m = (MIType)((PluginInfo)c0.type).getInstance();
							if (m.deSerialize(MIType.SERIAL_TEXT, v)) 
								setText(v);
							else setText("<html><span style=\"color:#ff7f7f\">NA</span>");                      
						}catch(Exception ex)
						{
							setIcon(WARNING_ICON);
							setText("<html><span style=\"color:#7f7f7f\">["+v+"]</span>");
						}
					}                
					return this;
				}});


			table.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
				public void columnAdded(TableColumnModelEvent e)
				{}

				public void columnRemoved(TableColumnModelEvent e)
				{}

				public void columnMoved(TableColumnModelEvent e)
				{}

				public void columnMarginChanged(ChangeEvent e)
				{}

				public void columnSelectionChanged(ListSelectionEvent e)
				{
					mergeAction.setEnabled(false);
					if(table.getSelectedColumnCount()==1)
					{
						int sel = table.getSelectedColumn();
						//TableColumn0 c0 = (TableColumn0)table.getColumnModel().getColumn(sel);
						if(sel>=0 && sel<table.getColumnCount()) 
							editAction.setEnabled(true);
						else
							editAction.setEnabled(false);
					}else if(table.getSelectedColumnCount()>1)
					{
						mergeAction.setEnabled(true);
						editAction.setEnabled(false);
						//combo.setEnabled(false);
					}else
					{
						//combo.setEnabled(false);
						editAction.setEnabled(false);
						mergeAction.setEnabled(false);
					}
				}
			});

			final JPopupMenu popupMenu = new JPopupMenu();
			for (String s : STANDARD_ENTRIES)
				if (s.equals("")) 
					popupMenu.add(new JSeparator());
				else 
					popupMenu.add(new SetTypeAction(s,table));
			Set<PluginInfo> plis = PluginManager.getInstance().getPluginsFor(Constants.MC_METAINFO);
			for(PluginInfo pli : plis)
				popupMenu.add(new SetTypeAction(pli,table));
			popupMenu.add(new JSeparator());
			popupMenu.add(editAction);
			popupMenu.add(mergeAction);

			MouseAdapter ma  = new MouseAdapter() {
				public void mousePressed(MouseEvent e)
				{
					if (e.getSource()==table.getTableHeader()) {
						TableColumnModel columnModel = table.getColumnModel();
						int viewColumn = columnModel.getColumnIndexAtX(e.getX());
						int column = table.convertColumnIndexToModel(viewColumn);
						if (e.getClickCount() == 1 && column != -1) 
							table.setColumnSelectionInterval(column, column);
					}            		
					if(e.isPopupTrigger()) popup(e);                
				}
				public void mouseReleased(MouseEvent e)
				{
					if(e.isPopupTrigger()) popup(e);
				} 

				private void popup(MouseEvent e)
				{
					if(!e.isConsumed())
					{
						e.consume();                    
						popupMenu.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			};

			table.addMouseListener(ma);
			table.getTableHeader().addMouseListener(ma);



			/*
			 * fill boxes
			 */
			 setBorder(BorderFactory.createCompoundBorder(
					 BorderFactory.createTitledBorder("Content"),
					 BorderFactory.createEmptyBorder(5,5,5,5)
			 ));

			Box b0 = Box.createHorizontalBox();
			b0.add(new JLabel("Right-click on a column to select a MIO type."));
			//b0.add(Box.createHorizontalStrut(10));
			// b0.add(combo);
			b0.add(Box.createHorizontalGlue());
			b0.add(new JButton(new AbstractAction("Save table...") {
				public void actionPerformed(ActionEvent e)
				{
					JFileChooser fc = new JFileChooser(
							PluginInfo.getPreferences("PAS.mio.import.csv")
							.get(CSVImportPlugin.LAST_OPEN_DIR_KEY,
									System.getProperty("user.home"))                    
					);
					fc.setMultiSelectionEnabled(false);

					if(JFileChooser.APPROVE_OPTION
							==fc.showSaveDialog(AbstractCSVMIOImportComponent.this))
						try{

							File f = fc.getSelectedFile();                    
							if(!f.exists() 
									|| JOptionPane.YES_OPTION
									==JOptionPane.showConfirmDialog(
											AbstractCSVMIOImportComponent.this, 
											"The file '"+f.getName()+"' already exists. Do you want to overwrite it?",
													"Save ...",
													JOptionPane.YES_NO_OPTION))
							{
								FileWriter w = new FileWriter(f);

								//print header
								boolean first=true;
								for(int c=0; c!=model.getColumnCount(); ++c)
								{    
									TableColumn0 c0 = (TableColumn0)table.getColumnModel().getColumn(c);
									if(c0.type==STANDARD_ENTRIES[IGNORE_KEY]) continue;

									if(!first)
									{
										w.write("\t");
									}else first=false;

									w.write(model.getColumnName(c)); 

								}
								w.write("\n");
								w.flush();

								//print values
								for(int r=0; r!=model.getRowCount(); ++r)
								{
									first=true;
									for(int c=0; c!=model.getColumnCount(); ++c)
									{    
										Object value = model.getValueAt(r, c);
										TableColumn0 c0 = (TableColumn0)table.getColumnModel().getColumn(c);    
										if(c0.type==STANDARD_ENTRIES[IGNORE_KEY]) continue;

										if(!first)
										{
											w.write("\t");
										}else first=false;

										if(value!=null)
										{
											w.write(
													"\""+
													(c0.repl==null || c0.repl.regexp.length()==0 ? 
															value.toString() : 
																value.toString().replaceAll(c0.repl.regexp, c0.repl.replacement)        
													) +
													"\"");
										}
									}
									w.write("\n");
									w.flush();
								}

								w.close();
							}

						}catch(Exception ex)
						{
							ex.printStackTrace();

							JOptionPane.showMessageDialog(
									AbstractCSVMIOImportComponent.this, 
									"<html>The save operation failed due to the following exception:<br><pre>" +
									ex.getClass().getName() + ": " +                        
									ex.getMessage() + 
									"</pre>",
									"Save ...",
									JOptionPane.ERROR_MESSAGE
							);
						}

				}}
			));

			add(b0);
			add(Box.createVerticalStrut(5));

			JScrollPane scroll = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			scroll.setMaximumSize(new Dimension(
					Integer.MAX_VALUE,
					Toolkit.getDefaultToolkit().getScreenSize().height/4
			));
			scroll.setPreferredSize(new Dimension(
					scroll.getPreferredSize().width,
					scroll.getMaximumSize().height
			));

			add(scroll);
			add(Box.createVerticalGlue());

			/*
			 * the action
			 */
			 actions.add(getProcessingAction());
	}

	/* (non-Javadoc)
	 * @see mayday.core.gui.AbstractStandardDialogComponent#getOkActions()
	 */
	public ArrayList<Action> getOkActions()
	{
		return actions;
	}


	protected abstract AbstractAction getProcessingAction();

	public static class TableColumn0
	extends TableColumn
	{
		public ReplaceInfo repl = null;
		public Object type = STANDARD_ENTRIES[IGNORE_KEY];

		public TableColumn0(int modelIndex)
		{
			super(modelIndex);
			this.setHeaderRenderer(new headerRenderer());
		}   
	}

	public static class ReplaceInfo
	{
		public String regexp="";
		public String replacement="";
		public ReplaceInfo(String regexp, String replacement)
		{
			super();
			this.regexp = regexp;
			this.replacement = replacement;
		}        
	}


	protected static class headerRenderer implements TableCellRenderer {

		private JPanel pnl = new JPanel();
		private JLabel title = new JLabel();
		private JLabel type = new JLabel();

		public headerRenderer() {
			pnl.setLayout(new BorderLayout());
			pnl.add(title, BorderLayout.NORTH);
			pnl.add(type, BorderLayout.SOUTH);
			type.setFont(new Font(type.getFont().getName(), type.getFont().getStyle(), type.getFont().getSize()-2));
			pnl.setBorder(BorderFactory.createRaisedBevelBorder());
		}

		public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
			TableColumn0 c0 = (TableColumn0)arg0.getColumnModel().getColumn(arg5);
			title.setText(arg0.getColumnName(arg5));
			if (c0.type instanceof PluginInfo) {
				type.setText("("+((PluginInfo)c0.type).getName()+")");
				type.setForeground(Color.BLACK);
			} else {
				type.setText("("+c0.type+")");
				type.setForeground(Color.RED);
			}
			if (arg2 || arg3) //selected
				type.setBackground(UIManager.getColor("textHighlight"));
			else
				type.setBackground(UIManager.getColor("control"));
			return pnl;
		}

	}

	public class SetTypeAction extends AbstractAction {

		private Object mytype;
		private JTable table; 

		public SetTypeAction(Object myType, JTable Table) {
			mytype = myType;
			table = Table;
			super.putValue(AbstractAction.NAME, toString());
		}

		public void actionPerformed(ActionEvent arg0) {
			int col = table.getSelectedColumn();                
			if(col>=0 && col<table.getColumnCount())
			{
				TableColumn0 c = ((TableColumn0)table.getColumnModel().getColumn(col));
				if(mytype==STANDARD_ENTRIES[KEY_KEY])  //reset the old key column
				{
					//search the other column with this key
					Enumeration<TableColumn> cols = table.getColumnModel().getColumns();
					while(cols.hasMoreElements()) 
					{
						TableColumn0 c0 = (TableColumn0)cols.nextElement();
						if( c0.type == STANDARD_ENTRIES[KEY_KEY] )
						{
							c0.type = STANDARD_ENTRIES[IGNORE_KEY];
							break;
						}
					}
					c.type = STANDARD_ENTRIES[KEY_KEY];                       
				}else if(mytype!=null) 
				{
					c.type = mytype;
				}                       

				((AbstractTableModel)table.getModel()).fireTableDataChanged();
				JTableHeader head = table.getTableHeader();
				head.repaint(); 
			}

		}

		public String toString() {
			if(mytype instanceof String) {
				return ((String)mytype);
			}else if(mytype instanceof PluginInfo)
			{
				setToolTipText( "<html>"+((PluginInfo)mytype).getIdentifier());
				return (((PluginInfo)mytype).getName());
			}
			return "--undefined--";
		}

	}
}
