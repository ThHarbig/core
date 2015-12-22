package mayday.vis3.plots.heatmap2.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mayday.vis3.components.CenteredMiddleLayout;
import mayday.vis3.components.PlotScrollPane;
import mayday.vis3.components.PlotWithLegendAndTitle;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;

@SuppressWarnings("serial")
public class HeatmapOuterComponent extends JPanel {
	
	protected HeatmapStructure data;

	public HeatmapOuterComponent(PlotWithLegendAndTitle pwlat) {
		super(new CenteredMiddleLayout());
		setBackground(Color.WHITE);
		
		data = new HeatmapStructure(this);

		if (pwlat!=null)
			pwlat.setTitledComponent(data.getResponsibleComponent());

		
		JScrollPane heatMapScrollPane = new PlotScrollPane(data.getHeatmapComponent()) {
			@Override
			public Dimension getPreferredSize() {
				Dimension newSize = getViewport().getView().getPreferredSize();
				// add sizes of scroll bars
				int newWidth = newSize.width+getInsets().left+getInsets().right+
					(getVerticalScrollBar().isVisible()?getVerticalScrollBar().getWidth():0);
				int newHeight = newSize.height+getInsets().top+getInsets().bottom+
				    getColumnHeader().getSize().height+
				(getHorizontalScrollBar().isVisible()?getHorizontalScrollBar().getHeight():0);
				// add sizes of labels
				newHeight += getColumnHeader().getView().getPreferredSize().height;
				newWidth += getRowHeader().getView().getPreferredSize().width;
				
				return new Dimension(newWidth,newHeight);
			}
			
			public void validate() {
				getViewport().setBackground(Color.white);
				super.validate();
			}
			
		};
		
		heatMapScrollPane.setBackground(Color.white);
		heatMapScrollPane.setColumnHeaderView(data.getColumnHeaderComponent());
		heatMapScrollPane.setRowHeaderView(data.getRowHeaderComponent());
		

		JPanel heatMapTablePane = new JPanel(new BorderLayout());
		heatMapTablePane.setBorder(BorderFactory.createEmptyBorder());
		heatMapTablePane.add(heatMapScrollPane);
		
		JPanel row = new JPanel(new BorderLayout());		
		row.add(heatMapTablePane, BorderLayout.CENTER);		
		
		add(row, "Middle");
		
	}
	
	public void paint(Graphics g) {
		super.paint(g);
	}
	
	public void removeNotify() {
		if (data!=null)
			data.dispose();
		super.removeNotify();
	}
	
}
