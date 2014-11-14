/*
 * Created on 21.11.2004
 */
package mayday.interpreter.rinterpreter.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import mayday.core.tasks.AbstractTask;
import mayday.core.tasks.TaskStateEvent;
import mayday.core.tasks.TaskStateListener;
import mayday.interpreter.rinterpreter.RDefaults;

/**
 * @author Matthias
 *
 */
public class RProcessStateMonitor
extends Thread
implements TaskStateListener
{
    private AbstractTask myTask;
    
    private RSettings settings;
    private String oldMessage=null;
    private boolean finished;
    
    RProcessStateMonitor(RSettings settings) throws IOException
    {
        super("R - Process State Monitor");
        this.settings=settings;
        this.settings.setStatusFile(
            File.createTempFile("status","",new File(this.settings.getWorkingDir()))
        );  
        this.finished=false;
    }
    
    public void setRTask( AbstractTask rtask) {
    	myTask=rtask;
    }
    
    public void run()
    {
        try
        {
            while(!finished)
            {
                try
                {
                    BufferedReader rd=new BufferedReader(
                        new FileReader(this.settings.getStatusFile())    
                    );
                    String tmp=rd.readLine();
                    rd.close();
                    
                    if(tmp!=null && !tmp.equals(this.oldMessage)) // (fb) checking for null here
                    {
                       this.oldMessage=tmp; 
                       
                       String[] splits=tmp.split(";",2);
                       int current=-1;
                       String msg=null;
                       if(splits[0].trim().length()!=0)
                       {
                           current=Integer.parseInt(splits[0]);
                       }
                       if(splits[1].trim().length()!=0)
                       {
                           msg=splits[1].trim();
                       }
                       
                       this.fireProgressStateChanged(msg,current); 
                    }
                }catch(Throwable ex)
                {
                    this.fireProgressStateChanged(
                        "R is working...",
                        -1
                    );
                }
                Thread.sleep(200);
            }
        }catch(InterruptedException ex)
        {
            ex.printStackTrace();
        }
    }
    
    public void setFinished()
    {
        this.finished=true;
        long t=System.currentTimeMillis()-this.settings.getBeginTimeStamp();
        fireProgressStateChanged(
            "R-process finished. (Duration: "+
            RDefaults.getTimeString(t)+
            ")",
            1000
        );
    }
    
    
    protected void fireProgressStateChanged(String msg, int current)
    {
    	if (myTask==null)
    		return;
    	if (current>=0) current*=10;    	
    	myTask.writeLog(msg);
    	myTask.setProgress(current);
    }


	public int getStateMask() {
		return TaskStateEvent.TASK_FINISHED;
	}

	public void processEvent(TaskStateEvent evt) {
		// can only be task finished
		setFinished();
	}


}

