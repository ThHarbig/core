/**
 * 
 */
package mayday.vis3.categorical;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map.Entry;

import javax.swing.JColorChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import mayday.core.Mayday;
import mayday.core.gui.MaydayFrame;
import mayday.vis3.components.PlotScrollPane;
import mayday.vis3.gui.actions.ExportPlotAction;
import mayday.vis3.model.ViewModel;

@SuppressWarnings("serial") 
public class CategoricalAssignmentComponent extends PlotScrollPane {
	
	protected final CategoricalColoring catColoring;
	protected JTable table;
	protected MaydayFrame myWindow;
	protected boolean silent = false;
	
	public CategoricalAssignmentComponent(CategoricalColoring colorProvider) {			
		this.catColoring = colorProvider;
		init();
	}

	
	public void init() {			
		table = new JTable();
		table.setTableHeader(null);
		table.setModel(new DefaultTableModel(this.catColoring.getNumberOfCategories(),2) {
		    public boolean isCellEditable(int row, int column) {
		        return false;
		    }
		});
		table.getColumnModel().getColumn(0).setMaxWidth(20);
		table.setIntercellSpacing(new Dimension(0,0));
		table.setGridColor(table.getBackground());
		int row=0;
		for (Entry<Object, Color> ecolor : this.catColoring.getCategoricalColoring()) {
			table.setValueAt(ecolor.getValue(), row, 0);
			table.setValueAt(ecolor.getKey(), row, 1);
			++row;
		}
		
		table.getColumnModel().getColumn(0).setCellRenderer(new TableCellRenderer() {

			private JPanel component = new JPanel() {
				public void paint(Graphics g) {
					((Graphics2D)g).setColor(this.getBackground());
					((Graphics2D)g).fillRect(0, 0, getWidth(), getHeight());
				}
			};

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				if (value instanceof Color) {
					component.setBackground(((Color)value));
				}
				return component;
			}

		});
		
		table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
			
			protected Border NOFOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
			
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				setBorder(NOFOCUS_BORDER);
				return this;
			}
			
		});
		
		table.addMouseListener(new MouseAdapter() {		
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount()==2 && evt.getButton()==MouseEvent.BUTTON1) {
					// let user edit color assignment
					Color currentcolor = (Color)table.getValueAt(table.getSelectedRow(),0);
					Object key = table.getValueAt(table.getSelectedRow(), 1);
					Color lcolor = JColorChooser.showDialog( Mayday.sharedInstance, "Choose color for "+key.toString(), currentcolor );
					if ( lcolor != null ) {
						catColoring.replaceCategoricalColor( key, lcolor ); 
						table.setValueAt(lcolor, table.getSelectedRow(), 0);
					}
				}
			}
		});
		
		this.setViewportView(table);
		this.setColumnHeader(null);
	}
	
	public void deselect() {
		table.getSelectionModel().clearSelection();
	}
	

	public void showWindow() {
		if (myWindow!=null) {
			init();
		} else {
			myWindow = new MaydayFrame();
			myWindow.setSize(400, 300);
			myWindow.add(this);
			JMenuBar mbar = new JMenuBar();
			myWindow.setJMenuBar(mbar);
			JMenu mnu = new JMenu("Legend");
			mnu.add(new ExportPlotAction(this));
			mbar.add(mnu);
		}
		ViewModel viewModel = catColoring.getViewModel();
		
		myWindow.setTitle(viewModel.getDataSet().getName()+" <"+viewModel.getVisualizer().getID()+"> - Colors assigned by: "+catColoring.getSourceName());
		myWindow.setVisible(true);
	}

	public void hideWindow() {
		if (myWindow!=null)
			myWindow.dispose();
		myWindow = null;
	}

	public void update() {
		if (myWindow!=null && !silent) {
			deselect();
		}
	}
	
	
	public JTable getInternalTable() {
		return table;
	}
	
	
}