/*
 * Created on Feb 7, 2005
 *
 */
package mayday.core.gui.probelist;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;

import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.Store;
import mayday.core.probelistmanager.gui.cellrenderer.ProbeListCellRenderer;

/**
 * @author gehlenbo
 *
 */
@SuppressWarnings("serial")
public class ProbeListListbox
extends JList
implements ProbeListListener
{
	public ProbeListListbox() {
		super(new DefaultListModel());
		init();
	}
	
	public ProbeListListbox(Store probeListStore) {
		super(probeListStore);
		init();
	}

	private void init() {
		this.setCellRenderer( new ProbeListCellRenderer() );
	}

	protected void removeProbeListListeners() {
		ListModel lm = this.getModel();		
		for ( int i = 0; i < lm.getSize(); ++i ) {
			((ProbeList)lm.getElementAt( i ) ).removeProbeListListener( this );
		}
	}


	public void removeNotify()	{       
		removeProbeListListeners();
		super.removeNotify();
	}


	@Deprecated
	public void setListData( Object[] objects )	{
		removeProbeListListeners();
		for ( Object l_object: objects )
			((ProbeList)l_object).addProbeListListener( this );
		super.setListData( objects );

	}


	public void probeListChanged( ProbeListEvent event ) {
		if ( ( event.getChange() & ProbeListEvent.OVERALL_CHANGE ) != 0 ) {
			this.repaint();
		}
	}     

}
