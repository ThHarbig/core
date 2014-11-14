package mayday.core.datasetmanager;

import java.util.List;

import mayday.core.DataSet;
import mayday.core.Store;

/*
 * Created on Apr 8, 2003
 *
 */

/**
 * @author neil
 * @version 
 */
@SuppressWarnings("serial")
public class DataSetManager extends Store
{

	public final static DataSetManager singleInstance = new DataSetManager();

	private DataSetManager(){}

	@SuppressWarnings("unchecked")
	public List<DataSet> getDataSets() {
		return (List<DataSet>)getObjects();
	}
		
	protected void verifyNameUniqueness( DataSet newDS ) {     
		
		DatasetNamer.ensureNameUniqueness(newDS);
		
//		while ( newDS.getName()==null || newDS.getName().equals("") || this.contains( newDS.getName() , newDS ) ) {
//			String message = MaydayDefaults.Messages.DATA_SET_NOT_UNIQUE;
//			message = message.replaceAll( MaydayDefaults.Messages.REPLACEMENT, newDS.getName() );
//			message += "\n" + MaydayDefaults.Messages.ENTER_NEW_NAME;
//
//			String name = (String)JOptionPane.showInputDialog( null,
//					message,
//					MaydayDefaults.Messages.WARNING_TITLE,
//					JOptionPane.WARNING_MESSAGE,
//					null,
//					null,
//					newDS.getName() );
//
//			if (name!=null)
//				newDS.setName( name );                                                   
//		}
	}
	
	public void addObject( Object object ){
		verifyNameUniqueness((DataSet)object);
		super.addObject( object );
	}


	public void addObjectAtBottom( Object object ) {
		addObject(object); // the same
	}


	public void addObjectAtTop( Object object )	{
		verifyNameUniqueness((DataSet)object);
		super.addObjectAtTop( object );
	}

}
