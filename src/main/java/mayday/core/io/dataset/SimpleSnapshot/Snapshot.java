package mayday.core.io.dataset.SimpleSnapshot;

import java.io.BufferedReader;
import java.io.OutputStream;

import mayday.core.DataSet;
import mayday.core.io.ReadyBufferedReader;
import mayday.core.tasks.AbstractTask;

public abstract class Snapshot {

	public static final String  PATH= Snapshot.class.getPackage().getName().replace(".", "/")+"/icons/";    	
    public static final String  QUICKLOAD_ICON=PATH+"folderr.png";
    public static final String  SNAPSHOT_LOAD_ICON=PATH+"folderm.png";    
    public static final String  QUICKSAVE_ICON=PATH+"diskr16.png";       
    public static final String	SNAPSHOT_SAVE_ICON=PATH+"diskm16.png";
    public static final String  QUICKSAVE_FILENAME = "quicksave.maydayZ";
    public static final String  SESSION_QUICKSAVE_FILENAME = "session.maydayZ";

	protected SnapshotStreamProvider streamprovider;
    
    
	public final static Snapshot getCorrectVersion(BufferedReader br) throws Exception {
		// check version id
		String line = br.readLine();
		Snapshot snap = getNewestVersion();
		while (snap!=null) {
			if (line.equals(snap.supportedVersion())) 
				break;
			else 
				snap = snap.olderVersion();
		}	
		return snap;
	}
	

	
	public final static Snapshot getNewestVersion() {
		return new Snapshot_v3_2();
	}
	
	protected abstract String supportedVersion();
	
	public abstract Snapshot olderVersion();
	
	public abstract void read(final ReadyBufferedReader br) throws Exception;
	
	public abstract void write(final OutputStream ostr) throws Exception;
	
	public abstract DataSet getDataSet();
	
	public abstract void setDataSet(DataSet dataSet);
	
	public abstract void setProcessingTask(AbstractTask at);
	
	public void setStreamProvider(SnapshotStreamProvider ssp) {
		streamprovider = ssp;
	}
}
