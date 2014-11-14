package mayday.core.datasetmanager.gui;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;

import mayday.core.DataSet;
import mayday.core.EventFirer;
import mayday.core.MasterTable;
import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.StoreEvent;
import mayday.core.StoreListener;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.gui.PluginMenu;
import mayday.core.gui.dataset.DataSetOverview;
import mayday.core.meta.types.AnnotationMIO;
import mayday.core.pluma.PluginManager;
import mayday.core.probelistmanager.ProbeListManagerList;

/*
 * Created on Apr 8, 2003
 *
 */

/**
 * @author neil
 * @version 
 */
@SuppressWarnings("serial")
public class DataSetManagerViewTabbed extends JTabbedPane
//SY
implements ChangeListener, PropertyChangeListener, MouseListener, StoreListener,
           DataSetManagerViewInterface
{
    CloseDataSetAction closeDataSetAction;
    String lastOpenPath;
    private JMenu datasetMenu;
    private JMenu probelistMenu; // kept here because we have only one instance here but many ProbeListManagerViews

    protected EventFirer<Object, ListSelectionListener> selectionFirer = new EventFirer<Object, ListSelectionListener>() {
		protected void dispatchEvent(Object event,
				ListSelectionListener listener) {
			listener.valueChanged(null);
		}
	};
    //public static final DataSetManagerView singleInstance = new DataSetManagerView(DataSetManager.singleInstance, TOP);
      
    
    public DataSetManagerViewTabbed(  ) {
    	this(TOP);
    }
      /**
     * @param tabPlacement
     */
    public DataSetManagerViewTabbed( int tabPlacement )
    { 
        super( tabPlacement );       
        init();    
    }
    

    
    protected void init()
    {  	
        // initialize objects
        this.closeDataSetAction = new CloseDataSetAction( "Close" );  	  
        
       
        //MZ 23.01.04
        this.lastOpenPath=
            MaydayDefaults.Prefs.NODE_PREFS.get(
                MaydayDefaults.Prefs.KEY_LASTOPENDIR,
                MaydayDefaults.Prefs.DEFAULT_LASTOPENDIR);
        //end MZ
        
        // deactivate actions
        setActionsEnabled(false);

        
        // create a new tab for each data set
        Object[] l_objects = DataSetManager.singleInstance.getObjects().toArray();
        int l_maxHeight = 0;
        int l_maxWidth = 0;
        
        for ( int i = 0; i < DataSetManager.singleInstance.getNumberOfObjects(); ++i )
        {
            DataSetView l_dataSetView = new DataSetView( (DataSet)l_objects[i] );
            
            add( l_dataSetView );
            setToolTipTextAt( i, l_dataSetView.getDataSet().getAnnotation().getQuickInfo() );
            
            if ( l_dataSetView.getPreferredSize().width > l_maxWidth )
            {
                l_maxWidth = l_dataSetView.getPreferredSize().width; 
            }
            
            if ( l_dataSetView.getPreferredSize().height > l_maxHeight )
            {
                l_maxHeight = l_dataSetView.getPreferredSize().height; 
            }  		
        }
        
        
        // set the size of the pane
        if (DataSetManager.singleInstance.getNumberOfObjects() > 0 )
        {
            setPreferredSize( new Dimension( l_maxWidth + 10, l_maxHeight + 10 ) );
        }
        else
        {
            setPreferredSize( new DataSetView( new DummyDataSet() ).getPreferredSize() );			
        }
        
        setMinimumSize( getPreferredSize() );
        
        addChangeListener( this );
        addPropertyChangeListener( this );
        addMouseListener( this );
        //SY
        DataSetManager.singleInstance.addStoreListener(this);
    }
    
    @SuppressWarnings("unchecked")
	public void setActionsEnabled(boolean enabled) {
    	if (datasetMenu!=null)
			((PluginMenu)datasetMenu).setSelectionChanged();
    	if (probelistMenu!=null)
    		probelistMenu.setEnabled(enabled);
    }    
    
    //MZ 05.03.2004
    // add a DataSetView
    public void addDataSetView(DataSetView dsv)
    {		
        //test if the DataSet is already contained in the DataSetManager
        DataSet l_dataSet=dsv.getDataSet();
        while(DataSetManager.singleInstance.contains(l_dataSet))
        {
            String l_message = MaydayDefaults.Messages.DATA_SET_NOT_UNIQUE;
            l_message = l_message.replaceAll( MaydayDefaults.Messages.REPLACEMENT,
                l_dataSet.getName() );
            l_message += "\n" + MaydayDefaults.Messages.ENTER_NEW_NAME;
            
            String l_name = (String)JOptionPane.showInputDialog( 
                null,
                l_message,
                MaydayDefaults.Messages.WARNING_TITLE,
                JOptionPane.WARNING_MESSAGE,
                null,
                null,
                l_dataSet.getName() );
            
            // quit if the user pressed cancel
            if ( l_name == null )
            {
                return;
            }
            
            l_dataSet.setName( l_name );    
        }
        
        dsv.setDataSetManagerView(this);
        
        
        DataSetManager.singleInstance.addObjectAtBottom(dsv.getDataSet());
        add(dsv.getDataSet().getName(),dsv);		

       
    }
    //end MZ
    
    public void setDataSetMenu(JMenu DataSetMenu) {
    	this.datasetMenu = DataSetMenu;
    	setActionsEnabled(getSelectedComponent()!=null);
    }
    
    public JMenu getMenu() {
    	return datasetMenu;
    }
    
    
    public JPopupMenu getPopupMenu()
    {    	
    	return getMenu().getPopupMenu();
    }
    
    public JMenu getProbeListMenu() {
    	if (probelistMenu==null) 
    		return new JMenu("");
    	else 
    		return probelistMenu;
    }
    
    public void setProbeListMenu(JMenu plmenu) {
    	probelistMenu = plmenu;
    	setActionsEnabled(getSelectedComponent()!=null);
    }
    
 
    
    public AbstractAction getCloseDataSetAction()
    {
        return ( this.closeDataSetAction );
    }
    
    
  
    
   
    public void propertyChange( PropertyChangeEvent event )
    {
        // enabled
        if ( event.getPropertyName() == "enabled" )
        {
            boolean l_activationMode;
            
            if ( ((Boolean)event.getNewValue()).booleanValue() == true )
            {
                l_activationMode = true;
                
                // enable actions (if required)
                fireStateChanged();       
            }
            else
            {
                l_activationMode = false;
                
                // disable actions (this must be done!)
                setActionsEnabled(l_activationMode);
            }
            
            // propagate this event to the children
            for ( int i = 0; i < getTabCount(); ++i )
            {
                ((DataSetView)getComponentAt( i )).setEnabled( l_activationMode );
            }
                 
        }
    }
    
    
    public void stateChanged( ChangeEvent event )
    {
        boolean l_activationMode;
        
        if ( getSelectedDataSets().size()==0 )
        {
            l_activationMode = false;    
        }
        else
        {
            l_activationMode = true;
        }
        
        if (probelistMenu!=null)
        	this.probelistMenu.setEnabled( l_activationMode );

        // make sure probe list menu is up-to-date
        try {
        	DataSet ds = (DataSet)(DataSetManager.singleInstance.getObjects().get(this.getSelectedIndex()));
        	ds.getProbeListManager().getProbeListManagerView().updateMenu();
        } catch (Throwable t) {
        	 ;
         }
        
        setActionsEnabled(l_activationMode);
        
        selectionFirer.fireEvent(this);
    }
    
    
/*    public void setProbeListMenu( JMenu plmenu ) {
   		if (probeListMenu==null)  // only use the first menu, later invocations happen when the popupmenu is made
   			this.probeListMenu = plmenu;
    }*/    
    
    public void mouseClicked( MouseEvent event )
    {
        if ( event.getButton() == MouseEvent.BUTTON1 )
        {  		
            // double-click
            if ( event.getClickCount() == 2 )
            {
                DataSet l_dataSet = ((DataSetView)getSelectedComponent()).getDataSet();
                MasterTable l_masterTable = l_dataSet.getMasterTable();
                
                String l_extrema = new String();
                
                l_extrema += "global maximum = " +l_masterTable.getMaxValue( Probe.EXPLICIT_PROBE | Probe.IMPLICIT_PROBE ) + " \n";
                l_extrema += "global minimum = " +l_masterTable.getMinValue( Probe.EXPLICIT_PROBE | Probe.IMPLICIT_PROBE ) + "\n";
                l_extrema += "explicit global maximum = " +l_masterTable.getMaxValue( Probe.EXPLICIT_PROBE ) + " \n";
                l_extrema += "explicit global minimum = " +l_masterTable.getMinValue( Probe.EXPLICIT_PROBE ) + "\n";
                l_extrema += "implicit global maximum = " +l_masterTable.getMaxValue( Probe.IMPLICIT_PROBE ) + " \n";
                l_extrema += "implicit global minimum = " +l_masterTable.getMinValue( Probe.IMPLICIT_PROBE ) + "\n";
                
                JOptionPane.showMessageDialog( null,
                    l_dataSet.getName() + "\n" +
                    l_dataSet.getAnnotation().getQuickInfo() + "\n" +
                    l_dataSet.getAnnotation().getInfo() + "\n" + "\n" +
                    l_dataSet.getMasterTable().getNumberOfProbes() + " probes\n" +
                    l_dataSet.getMasterTable().getNumberOfExperiments() + " experiments\n" + "\n" +
                    "\n" +
                    l_extrema,
                    "Data Set Info",
                    JOptionPane.INFORMATION_MESSAGE ); 
            }
        }
    }
    
    
    public void mousePressed( MouseEvent event )
    {
        if ( event.getButton() == MouseEvent.BUTTON3 )
        {
            getPopupMenu().show( this, event.getX(), event.getY() );
        }
    }
    
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased( MouseEvent event )
    {
    }
    
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered( MouseEvent event )
    {
    }
    
    
    /* (non-Javadoc)(PluginInfo pli : plis)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited( MouseEvent event )
    {
    }
    
	public class CloseDataSetAction
    extends AbstractAction
    {
        public CloseDataSetAction( String text, Icon icon )
        {
            super( text, icon );			
        }
        
        
        public CloseDataSetAction( String text )
        {
            super( text );
        }
        
        
        public CloseDataSetAction()
        {
            super( "Close Data Set" );
        }
        
        
        public void actionPerformed( ActionEvent event )
        {		
            Component l_selectedComponent = getSelectedComponent();
            
            if ( l_selectedComponent != null )
            {
            	closeDataSet(((DataSetView)(l_selectedComponent)).getDataSet());	
            }     
        }
    }


    //SY
	public void objectAdded(StoreEvent event) {

        DataSet d = (DataSet)event.getObject();
        
        //ignore ghost datasets frequently emerging while mayday is starting and running...
        if(d.getMasterTable().getNumberOfExperiments()==0 && 
           d.getMasterTable().getNumberOfProbes()==0)
           {
               return;
           }        
        
        //MZ 2006-07-16
        if(d.getDataSetView()==null)
        {
            this.add(new DataSetView(d));
            d.getDataSetView().setDataSetManagerView(this);
        }
        
        
        //prepare dialog
        DataSetOverview overview=new DataSetOverview(d);
        overview.setVisible(true);   
    }


    public void objectRemoved(StoreEvent event) {
    	closeDataSet((DataSet)event.getObject());
    }
    
    private class DummyDataSet extends DataSet {
    	// a minimal dataset for getPreferredSize in DataSetManagerView.init()
    	public DummyDataSet( )
        {    		
    		super(true);
            //this.masterTable = new MasterTable( this );  	
            this.probeListManager = new ProbeListManagerList(this); //ProbeListManagerFactory.newManagerInstance(this);
            this.name = "Dummy DataSet";        
            //this.miManager = new MIManager( this );
        }
    	
    	public void setAnnotation(AnnotationMIO annotation) {
    		//ignore
    	}
    	
    	public AnnotationMIO getAnnotation() {
    		return new AnnotationMIO("Dummy","Dummy");
    	}

    }


	public void addDataSet(DataSet ds) {
		addDataSetView(new DataSetView(ds));
	}
	
	@SuppressWarnings("unchecked")
	public List<DataSet> getSelectedDataSets() {
		// this view can only handle single selections
    	int selectedDataSet = getSelectedIndex();
    	if (selectedDataSet==-1)
    		return Collections.EMPTY_LIST;
    	DataSet ds = (DataSet)(DataSetManager.singleInstance.getObjects().get(selectedDataSet));
    	LinkedList<DataSet> ls = new LinkedList<DataSet>();
    	ls.add(ds);
		return ls;
	}
	
	public void setSelectedDataSets(Collection<DataSet> ds) {
		if (ds.size()>0)
			setSelectedIndex(DataSetManager.singleInstance.getObjects().indexOf(ds.iterator().next()));
	}
	
	public void updateInfo(DataSet ds) {
		for (int i=0; i!=getComponentCount(); ++i) {
			if (((DataSetView)getComponentAt(i)).getDataSet()==ds) {
				setTitleAt( i, ds.getName() );
				setToolTipTextAt( i, ds.getAnnotation().getQuickInfo() );
			}
        }
		invalidate();
		revalidate();
		repaint();
	}
	
	public void closeDataSet(DataSet ds) {
		for (int i=0; i!=getComponentCount(); ++i) {
			DataSet mine = ((DataSetView)getComponentAt(i)).getDataSet(); 
			if (mine==ds) {
				closeDataSet(i);
		        ds.propagateClosing();
		        DataSetManager.singleInstance.removeObject(ds );	
				break;
			}
		}
	}
	
	private void closeDataSet(int index) {

    	
        if ( DataSetManager.singleInstance.getNumberOfObjects() > 1 )
        {
            removeTabAt( index );
        }
        else
        {
            // bug work-around
            int l_tempTabLayoutPolicy = getTabLayoutPolicy(); 
            
            setTabLayoutPolicy( WRAP_TAB_LAYOUT );
            removeTabAt( index );
            setTabLayoutPolicy( l_tempTabLayoutPolicy );
        }
        
	}
	public Component getGUIComponent() {
		return this;
	}
	
	
	@PluginManager.IGNORE_PLUGIN
	public static class DSMVPlugin extends DataSetManagerViewPlugin {

		protected Class<? extends DataSetManagerViewInterface> getViewClass() {
			return DataSetManagerViewTabbed.class;
		}

		protected String getViewClassName() {
			return "Tabbed View";
		}
		
	}
	

	public void addSelectionListener(ListSelectionListener lsl) {
		selectionFirer.addListener(lsl);		
	}

	public void removeSelectionListener(ListSelectionListener lsl) {
		selectionFirer.removeListener(lsl);		
	}

	public void moveListenersTo(DataSetManagerViewInterface other) {
		for (ListSelectionListener lsl : selectionFirer.getListeners()) {
			other.addSelectionListener(lsl);
			removeSelectionListener(lsl);
		}
	}

	
}
