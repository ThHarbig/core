package mayday.clustering.extras.comparepartitions;

import java.awt.Dimension;
import java.util.Collection;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class ResultTable extends JTable {

	public ResultTable( Collection<ClusterOverlap> incl ) {
		setAutoResizeMode(AUTO_RESIZE_OFF);

		getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		setModel(new DefaultTableModel(incl.size(),7));
		
		setIntercellSpacing(new Dimension(0,0));
		
		populate(incl);

	}
	
	protected void populate( Collection<ClusterOverlap> incl)  {
		
		String[] titles = new String[]{
				"% of left partner",
				"absolute values left",
				"left partner",
				"pairing",
				"right partner",
				"absolute values right",
				"% of right partner"				
		};
		
		for (int i=0; i!=getColumnCount(); ++i) {
			getColumnModel().getColumn(i).setHeaderValue(titles[i]);
		}
		
		DefaultTableModel dtm = (DefaultTableModel)getModel();
		int row=0;
		for (ClusterOverlap me : incl) {		
			
			double lperc = (double)Math.round(me.leftPercentage()*100) / 100;
			dtm.setValueAt(lperc+"%", row, 0);
						
			dtm.setValueAt(me.overlap+"/"+me.leftCount, row, 1);
			
			dtm.setValueAt(me.leftName, row, 2);
			
			String arrow="";
			switch (me.direction) {
			case ClusterOverlap.DIR_BOTH: arrow = "  <--->   "; break;
			case ClusterOverlap.DIR_LTR:  arrow = "   --->   "; break;
			case ClusterOverlap.DIR_RTL:  arrow = "  <---    "; break;
			}
							
			dtm.setValueAt(arrow, row, 3);
			
			dtm.setValueAt(me.rightName, row, 4);
			
			dtm.setValueAt(me.overlap+"/"+me.rightCount, row, 5);
			
			double rperc = (double)Math.round(me.rightPercentage()*100) / 100;
			dtm.setValueAt(rperc+"%", row, 6);
			++row;
		}
	}

}

