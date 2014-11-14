package mayday.core.datasetmanager.gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mayday.core.DataSet;
import mayday.core.DataSetEvent;
import mayday.core.DataSetListener;
import mayday.core.gui.GUIUtilities;
import mayday.core.pluma.PluginInfo;
import mayday.core.probelistmanager.ProbeListManager;
import mayday.core.probelistmanager.ProbeListManagerFactory;
import mayday.core.probelistmanager.gui.ProbeListManagerView;
import mayday.core.probelistmanager.search.ProbeListSearch;
import mayday.core.probelistmanager.search.ProbeListSearchPanel;

/*
 * Created on Apr 8, 2003
 *
 */

/**
 * @author neil
 * @version 
 */
@SuppressWarnings("serial")
public class DataSetView
extends JPanel
implements PropertyChangeListener, DataSetListener
{
    DataSet dataSet;
    ProbeListManagerView probeListManagerView;
    DataSetManagerViewInterface dataSetManagerView;
    ProbeListSearchPanel probeListSearchPanel;
    
    Component centerComponent;
    
    
	public DataSetView( DataSet dataSet )
    {
        super();
        
        dataSet.setDataSetView(this);
        this.dataSet = dataSet;
        dataSet.addDataSetListener(this);
        init();
    }
    
    
    protected void init()
    {
        ProbeListManager l_probeListManager = this.dataSet.getProbeListManager();
        
        setLayout( new BorderLayout() );
        
        // Element 1 = ProbeList View
        setProbeListManagerView( ProbeListManagerFactory.getManagerViewInstance(l_probeListManager) );
       
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        
        // Element 2  = Search Panel
        probeListSearchPanel=new ProbeListSearchPanel(new ProbeListSearch(),probeListManagerView);
        probeListSearchPanel.setVisible(false);
        bottom.add(probeListSearchPanel);
        
        // Element 3 = Action Panel
        Container actionComponent = probeListManagerView.getActionComponent();
        // -- add the find button
        actionComponent.add(Box.createHorizontalStrut(10)); // spacer
        actionComponent.add(GUIUtilities.makeIconButton(
        		probeListSearchPanel.getFindAction(),
				KeyEvent.VK_S, 
				"Search for probe lists", 
				PluginInfo.getIcon("mayday/images/search.png",20,20)));		
        
        Component changeComp = ProbeListManagerFactory.getChangeComponent(dataSet.getProbeListManager());
        if (changeComp!=null)  {
            actionComponent.add(Box.createHorizontalStrut(10)); // spacer
        	actionComponent.add( changeComp );
        }

        
        bottom.add( actionComponent );
        
        add(bottom, BorderLayout.SOUTH);
        
        // set name of this component
        updateName();
        
        addPropertyChangeListener( this );
    }
    
    public void setProbeListManagerView(ProbeListManagerView plmv) {
        probeListManagerView = plmv;
        if (centerComponent!=null)
        	remove(centerComponent);
        centerComponent = new JScrollPane(probeListManagerView.getComponent());
        add( centerComponent , BorderLayout.CENTER );
        centerComponent.invalidate();
        centerComponent.validate();
        invalidate();
        validate();
        repaint();
    }
    
    
    public void updateName() {
    	setName( this.dataSet.getName() );
    	
    	repaint();
    }
    
    public DataSet getDataSet()
    {
        return ( this.dataSet );
    }
    
    
    public void setDataSet( DataSet dataSet )
    {
        this.dataSet = dataSet;
    }
    
    
    public void propertyChange( PropertyChangeEvent event )
    {
        if ( event.getPropertyName() == "enabled" )
        {
            if ( ((Boolean)event.getNewValue()).booleanValue() == true )
            {
                // propagate this event to the children
                probeListManagerView.getComponent().setEnabled( true );
            }
            else
            {
                // propagate this event to the children
                probeListManagerView.getComponent().setEnabled( false );
            }
        }
    }
    //MZ: 05.03.2004
    /**
     * @return
     */
    public DataSetManagerViewInterface getDataSetManagerView()
    {
        return dataSetManagerView;
    }
    
    /**
     * @param view
     */
    public void setDataSetManagerView(DataSetManagerViewInterface view)
    {
        dataSetManagerView = view;
    }
    //end MZ
    
    
    //MZ 2005-11-09
    public ProbeListManagerView getProbeListManagerView()
    {
        return this.probeListManagerView;
    }


	/**
	 * @return the probeListSearchPanel
	 */
	public ProbeListSearchPanel getProbeListSearchPanel() {
		return probeListSearchPanel;
	}


	/**
	 * @param probeListSearchPanel the probeListSearchPanel to set
	 */
	public void setProbeListSearchPanel(ProbeListSearchPanel probeListSearchPanel) {
		this.probeListSearchPanel = probeListSearchPanel;
	}


	public void dataSetChanged(DataSetEvent event) {
		if (event.getChange()==DataSetEvent.CAPTION_CHANGE)
			updateName();		
	}
	
}
