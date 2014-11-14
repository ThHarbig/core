/**
 * 
 */
package mayday.core.probelistmanager.gui.cellrenderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mayday.core.ProbeList;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.meta.types.AnnotationMIO;

@SuppressWarnings("serial")
public class GraphicProbeListRenderComponent extends JPanel
{
	protected JLabel nameLabel;
	protected JLabel imageLabel;
	protected JLabel numProbesLabel;
	public final static Insets INSETS = new Insets(5,5,5,5);
	
	protected JLabel miLabel;

	
	public GraphicProbeListRenderComponent()
	{
		super();
		setLayout(new GridBagLayout());
		
		nameLabel=new JLabel();
		imageLabel=new JLabel();
		numProbesLabel=new JLabel();
		numProbesLabel.setHorizontalAlignment(JLabel.LEFT);
		Font f= numProbesLabel.getFont();
		f=new Font(f.getName(),f.getStyle(),f.getSize()-2);
		numProbesLabel.setFont(f);
		numProbesLabel.setForeground(new Color(0x88,0x88,0x88));
		miLabel=new JLabel();
		
		//imageLabel.setBorder(BorderFactory.createLineBorder(Color.black,1));
		
		GridBagConstraints gbc =new GridBagConstraints(0,0,1,2,0.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE,INSETS,2,2);
        add(imageLabel,gbc);
		
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.gridwidth=1;
        gbc.gridheight=1;        
		gbc.gridx=1;
		gbc.weightx=1.0;
		
		
		Box nameBox=Box.createHorizontalBox();
		nameBox.add(nameLabel);
		nameBox.add(Box.createHorizontalStrut(12));
		nameBox.add(numProbesLabel);
		nameBox.add(Box.createHorizontalGlue());
		
//		JPanel plPanel=new JPanel();
//		plPanel.setLayout(new BorderLayout());
//		plPanel.add(nameLabel,BorderLayout.CENTER);
//		plPanel.add(numProbesLabel,BorderLayout.EAST);
		
		add(nameBox,gbc);
		gbc.weightx=0.0;
		gbc.gridy=1;
		gbc.gridwidth=1;
		add(miLabel,gbc);
		

		
	}



	public void setName(String name) {
		this.nameLabel.setText(name);
	}



	public void setImage(ImageIcon icon) {
		this.imageLabel.setIcon(icon);
	}

	public void setNumProbes(int numProbes) {
		setNumProbes(""+numProbes);
	}


	public void setNumProbes(String numProbes) {
		this.numProbesLabel.setText("<html><small><font color=#888888>"+numProbes);
	}

	public void setMI(ProbeList p) 
	{
		StringBuffer sb=new StringBuffer("<html><small>");
		
		MIGroupSelection<MIType> mios=p.getDataSet().getMIManager().getGroupsForObject(p);
		int numMio= mios.size()>=3?3:mios.size();
		if (numMio>0) {
			if(mios.get(0).getMIOType().equals("PAS.MIO.Annotation") && ((AnnotationMIO)mios.get(0).getMIO(p)).getQuickInfo().length()>0)
			{
				String anno=((AnnotationMIO)mios.get(0).getMIO(p)).getQuickInfo();
			//	System.out.println(anno);
				anno=anno.replaceAll("<\\S*?>", " ");
			//	System.out.println(anno);
				sb.append("&bull;&nbsp;"+anno+"<br>" );
			}
			
			for(int i=1; i< numMio; ++i)
			{		
				sb.append("&bull;&nbsp;"+mios.get(i).getName()+":&nbsp;"+mios.get(i).getMIO(p).toString()+"<br>");
			}
			if(mios.size()-numMio >=1)	sb.append((mios.size()-numMio) +" additional MIOs");
		}
		sb.append("</small></html>");
		this.miLabel.setText(sb.toString());
	}
	
	
	public int computeHeight() {
		return INSETS.top+INSETS.bottom+Math.max(imageLabel.getHeight(), nameLabel.getHeight()+miLabel.getHeight());
	}

	
	
}