package mayday.vis3.legend;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class LegendItem extends JPanel {

	protected JPanel colorBox = new JPanel();
	protected JLabel name = new JLabel();

	public LegendItem(Color c, String labelText) {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		setOpaque(true);
		JPanel colorPanel = new JPanel();
		colorPanel.setBackground(Color.WHITE);
		colorPanel.setOpaque(true);
		colorPanel.add(colorBox);
		add(colorPanel, BorderLayout.WEST);
		
		add(name, BorderLayout.CENTER);
		//add(moreInfo);
		colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		colorBox.setBackground(c);
		name.setText(labelText);
	}
	
}