package mayday.clustering.qt.algorithm.searchdiameter;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.EventHandler;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mayday.clustering.qt.algorithm.QTPMath;
import mayday.clustering.qt.algorithm.QTPSettings;
import mayday.core.MaydayDefaults;
import mayday.core.math.distance.DistanceMeasureManager;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.structures.linalg.matrix.AbstractMatrix;

/**
 * @author Sebastian Nagel
 * @author G&uuml;nter J&auml;ger
 * @version 0.1
 */
@SuppressWarnings({"serial", "rawtypes"})
public class QTDiameterPlot extends JComponent implements ActionListener {
	private JTextField diameter_Field;
	private JButton cancel;
	private JButton ok;
	private JPanel optionsRegion;
	private QTDiamPlotDrawRegion drawRegion;
	private Double diameter = -1.;
	private JComboBox distanceMeasures;
	private AbstractMatrix matrix;
	private QTPSettings settings = null;
	private QTSearchDiameter sd;
	private JButton updatePlotButton;
	private JComboBox subset;
	private JComboBox threadCount;
	
	/**
	 * default constructor
	 * @param matrix 
	 * @param sd 
	 */
	@SuppressWarnings({ "unchecked" })
	public QTDiameterPlot(AbstractMatrix matrix, QTSearchDiameter sd) {
		this.sd = sd;
		this.distanceMeasures = new JComboBox(DistanceMeasureManager.values().toArray());

		QTPSettings qts = new QTPSettings(); // import last used config here			
		this.distanceMeasures.setSelectedItem(qts.getDistanceMeasure());
		this.setLayout(new BorderLayout());
		this.drawRegion = new QTDiamPlotDrawRegion(600, 350);
//		JScrollPane jsp = new PlotScrollPane(drawRegion);
//		jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
//		this.addComponentListener(EventHandler.create(ComponentListener.class, this.drawRegion, "plotResized", "", "componentResized"));
		this.add(drawRegion, BorderLayout.CENTER);
		this.diameter = 0.;
		this.matrix = matrix;
		
		this.createWidgets();
	}
	
	public void setEnabled(boolean e) {
		super.setEnabled(e);
		distanceMeasures.setEnabled(e);
		updatePlotButton.setEnabled(e);
		cancel.setEnabled(e);
		ok.setEnabled(e);
		diameter_Field.setEditable(e);
	}
	
	/**
	 * @return optionsRegion
	 */
	public JPanel getOptionsRegion() {
		return this.optionsRegion;
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void createWidgets() {
		this.optionsRegion = new JPanel();
		this.optionsRegion.setLayout(new BorderLayout());
		
		this.cancel = new JButton("Cancel");
		this.ok = new JButton("Start QT-Clust");
		this.diameter_Field = new JTextField("Please click into the diagram to choose a diameter!");
		this.diameter_Field.addKeyListener(EventHandler.create(KeyListener.class, this, "keyPressed", "", "keyPressed"));
		String[] subset = {"10%","20%","30%","40%","50%","60%","70%","80%","90%","100%"};
		this.subset = new JComboBox(subset);
		
		int prozessorCount = Runtime.getRuntime().availableProcessors();
		this.threadCount = new JComboBox(QTPSettings.getProzessorCountList());
		this.threadCount.setSelectedIndex(prozessorCount-1);
		this.threadCount.setToolTipText("Number of Threads");
		
		this.updatePlotButton = new JButton("Update Plot");
		this.updatePlotButton.addActionListener(this);
		
		JLabel diameterText = new JLabel();
		diameterText.setText("Chosen threshold diameter for QT-clustering:");
		this.optionsRegion.add(diameterText, BorderLayout.NORTH);
		this.optionsRegion.add(this.diameter_Field, BorderLayout.CENTER);
		
		JPanel buttonRegion = new JPanel();
		buttonRegion.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonRegion.add(new JLabel("Number of Threads:"));
		buttonRegion.add(this.threadCount);
		buttonRegion.add(this.distanceMeasures);
		buttonRegion.add(this.subset);
		buttonRegion.add(this.updatePlotButton);
		buttonRegion.add(this.cancel);
		buttonRegion.add(this.ok);
		buttonRegion.setVisible(true);
		
		this.optionsRegion.add(buttonRegion, BorderLayout.SOUTH);
		
		this.add(this.optionsRegion, BorderLayout.SOUTH);
		this.drawRegion.addMouseListener(EventHandler.create(MouseListener.class, this, "drawDiameterLine", "", "mouseClicked"));
		this.drawRegion.addMouseMotionListener(EventHandler.create(MouseMotionListener.class, this, "drawDiameterLine", "", "mouseDragged"));
		
	}
	
	public int getThreadCount() {
		if (this.threadCount != null) {
			return ((Integer) this.threadCount.getSelectedItem()).intValue();
		}
		return 1;
	}
	
	/**
	 * @return DrawRegion
	 */
	public QTDiamPlotDrawRegion getDrawRegion() {
		return this.drawRegion;
	}
	
	/**
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void drawDiameterLine(MouseEvent e) {
		double x = e.getPoint().x;
		double newDiameter = this.drawRegion.getXScaling(x);
		this.diameter = QTPMath.round(newDiameter);
		this.diameter_Field.setText(Double.toString(this.diameter));
		this.drawRegion.setDiameterLine(x);
	}
	
	/**
	 * Calculate new distance values and update distance plot
	 */
	public void updatePlot() {
		this.sd.updateValues();
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		this.updatePlot();
		if(((JButton)e.getSource()).getText().equals("Suggest Diameter")) {
			this.diameter = QTPMath.round(this.drawRegion.calculateBestDiameter());
			this.diameter_Field.setText(Double.toString(this.diameter));
		}
	}

	/**
	 * (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {
		if(KeyEvent.VK_ENTER == e.getKeyCode()) {
			try	{
				this.diameter = Double.valueOf(((JTextField)e.getSource()).getText());
				this.drawRegion.setDiameter(this.diameter);
			} catch(Exception ex) {
				JOptionPane.showMessageDialog(null, "\""+((JTextField)e.getSource()).getText()+"\""+ "\n"+"is not valid!\n" +
						"Please check your entry and the possible range.\n"+"Then type in a valid number!",
						MaydayDefaults.Messages.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * @return QTSettings
	 */
	public QTPSettings getSettings() {
		return this.settings;
	}
	
	/**
	 * @return OKButton
	 */
	public JButton getOK() 	{
		return this.ok;
	}
	
	/**
	 * @return CancelButton
	 */
	public JButton getCancel() 	{
		return this.cancel;
	}
	
	/**
	 * @return DistanceMeasureType
	 */
	public DistanceMeasurePlugin getDistanceMeasure() {
		return (DistanceMeasurePlugin)this.distanceMeasures.getSelectedItem();
	}
	
	
	public double getSubset() {
		String str = (String) this.subset.getSelectedItem();
		return (double) Double.parseDouble(str.substring(0, str.length()-1))/100;
	}
	
	/**
	 * @return Diameter
	 */
	public double getDiameter() {
		return this.diameter;
	}

	/**
	 * @return matrix
	 */
	public AbstractMatrix getMatrix() {
		return this.matrix;
	}
}