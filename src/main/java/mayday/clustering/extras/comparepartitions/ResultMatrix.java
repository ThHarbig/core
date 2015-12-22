package mayday.clustering.extras.comparepartitions;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import mayday.core.gui.tablespecials.RotatedHeaderRenderer;

@SuppressWarnings("serial")
public class ResultMatrix extends JTable  {

	public ResultMatrix( ConfusingMatrix cm ) {
		super( new ConfusingModel (cm ));
		setAutoResizeMode(AUTO_RESIZE_OFF);

		getTableHeader().setReorderingAllowed(true);
		setGridVisible(false);
		setBackground(Color.WHITE);

		TableColumn tc = getColumnModel().getColumn(0);
		tc.setHeaderRenderer(new EmptyHeaderRenderer());
		
		TableCellRenderer cellRenderer = new NonNullRenderer();

		TableCellRenderer rotatedHeaderRenderer = new RotatedHeaderRenderer();
		for (int i=1; i!=getColumnCount(); ++i) {
			tc = getColumnModel().getColumn(i);
			tc.setHeaderRenderer(rotatedHeaderRenderer);
			tc.setCellRenderer(cellRenderer);
			tc.setPreferredWidth(30);
		}
		
	}

	public void setGridVisible(boolean vis) {
		int dim = vis?1:0;
		this.setIntercellSpacing(new Dimension(dim,dim));
	}

	public boolean isGridVisible() {
		return getIntercellSpacing().height>0;
	}

}

