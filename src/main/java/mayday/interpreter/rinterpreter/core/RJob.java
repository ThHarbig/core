package mayday.interpreter.rinterpreter.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

import mayday.core.tasks.AbstractTask;
import mayday.core.tasks.TaskCancelledException;
import mayday.interpreter.rinterpreter.RDefaults;


/**
 * An RJob creates all temporary files needed for the execution
 * of R, and executes R.
 * Use the run method to run R.
 * 
 * 
 * @author Matthias
 *
 */
public class RJob
{
    public static final int CANCELED=-1;
    public static final int OK=0;
    
	private File rinputFile;
	private File tmpSource;
	private File result;
	private File batch;
	private File error;
	private RSettings settings;
	private Date preExecutionDate;
	
	private int returnValue=-1;
    private int exitState=RJob.CANCELED;
    private PrintWriter logWriter;
	
	
	/**
	 * Creates a new RJob with the related files.
	 * 
	 * @param settings
	 * @param m
	 * @param pl
	 */
	public RJob(RSettings settings)
	{
		this.settings=settings;
    }
	
	/**
	 * @return error file, containing the content of Rs StdErr output.<br>
	 * The returned file does not exist if it had length 0 (if no error output was
	 * produced).
	 * 
	 */
	public File getError()
	{
		return error;
	}
	
	/**
	 * @return the temporary R-input-file that contains the data structures.
	 */
	public File getRInputFile()
	{
		return this.rinputFile;
	}
	
	/**
	 * @return the temporary source file
	 */
	public File getTmpSource()
	{
		return this.tmpSource;
	}
	
	
	/**
	 * @return the temporary result file
	 */
	public File getResult()
	{
		return this.result;
	}  
	

	/**
	 * Run the RJob:
	 * Execute the shell script,
	 * wait for execution (this can take some time with
	 * respect to the complexity of the given R source code),
	 * delete the error file, if it is empty,
	 * and delete the temporary files if <tt>deleteTmpFiles</tt> is set.
	 * 
	 * @return the exit value of the r process
	 * @throws Exception
	 */
	public int run() throws Exception
	{
        RProcessStateMonitor stateMonitor=new RProcessStateMonitor(settings);
        
        try
	    {
            //stateMonitor creates a pid12345 file for dynamic
            //correspondence with R
	        stateMonitor.start();
	        
	        this.preExecutionDate=new Date(System.currentTimeMillis());
	        
	        AbstractTask fileTask=new AbstractTask(
                "Create temporary files" 
                )
            {
                protected synchronized void initialize()
                {}

                protected void doWork()
                {
                    createFiles();
                }
                
                public void createFiles()
                {   
                    fireIndeterminateChanged(true);
                    setProgress(1000);
                    //create the files
                    try                    
                    {  
                    	System.out.println("RJob::createFiles starting");
                        //create mastertable file
                        rinputFile=TempFileFactory.createRInputFile(settings);
                        setProgress(2000);
                        
                        //create temporary source file
                        tmpSource=TempFileFactory.createTempSourceFile(
                            settings,
                            RJob.this
                        );
                        setProgress(4000);
                        
                        //create output file
                        result=TempFileFactory.createResultFile(settings);
                        setProgress(6000);
                        
                        //create error file
                        error=TempFileFactory.createErrorFile(settings);
                        setProgress(7000);
                        
                        // create batch file
                        batch=TempFileFactory.createBatch(
                            settings,
                            RJob.this
                        );
                        setProgress(10000);
                        System.out.println("RJob::createFiles finished ok");
                                               
                    }catch(IOException ex)
                    {
                        ex.printStackTrace();
                        throw new RuntimeException(
                            "Could not create the files needed for execution.\n\n"
                            + ex.getMessage()
                        );
                    }catch(Exception ex)
                    {
                        ex.printStackTrace();
                        throw new RuntimeException(ex.getMessage());        
                    }
                }
            };
            fileTask.start();
            fileTask.waitFor();
            
            System.out.println("RJob::run log part");
            try
	        {
	            File f=new File(this.settings.getLogFilename());
	            if(f.length()==0)
	            {
	                PrintWriter w=new PrintWriter(new FileWriter(f));
	                w.println(RDefaults.R_LOGFILE_HEADER);
	                w.close();
	            }else //test if this is really a RForMayday logfile
	            {
	                BufferedReader rd=new BufferedReader(new FileReader(f));
	                String firstLine=rd.readLine();
	                rd.close();
	                if(!firstLine.equals(RDefaults.R_LOGFILE_HEADER))
	                {
	                    throw new IOException("Not a log-file!");
	                }
	            }
	            
	            logWriter=new PrintWriter(
	                new FileWriter(
	                    f,
	                    true  //append?
	                ),
	                true //autoflush?
	            );
	        }catch(Exception ex)
	        {
	            logWriter=new PrintWriter(System.out,true);
	        }
	        
	        
	        long timeFrom=System.currentTimeMillis();
	        this.settings.setBeginTimeStamp(timeFrom);
	        logWriter.println(
	            ""+new Date(timeFrom)
	            +": R session started with \n\t"+settings.toString().replaceAll("\\n","\n\t")
	        );
	        
	        
            System.out.println("RJob::run creating rtask");
	        //Doing real, hard work :) 
            RTask task=new RTask();       
	        stateMonitor.setRTask(task);
            task.addStateListener(stateMonitor);
	        
            settings.setProbeLists(null);
            System.out.println("RJob::run starting rtask");
            task.start();  
           	task.waitFor();
            System.out.println("RJob::run finished rtask");
            this.exitState=task.getValue();
            
            
	        if(!this.settings.getStatusFile().delete())
	        {
	            this.settings.getStatusFile().deleteOnExit();
	        }
	        
	        long timeTo=System.currentTimeMillis();
	        this.settings.setEndTimeStamp(timeTo);
	        logWriter.print(
	            ""+new Date(timeTo)+": "
	            + "(" + ((timeTo-timeFrom)/1000.0) +" sec.)" 
	            + " R session "
	            
	        );
	        
	        if(task.getValue()==RJob.CANCELED)
	        {
	            logWriter.println(
	                "canceled."	       
	            );
	            returnValue=task.getValue();
	            
	        }else if(error.length()==0)
	        {
	            error.delete();
	            error=null;
	            logWriter.println("finished successfully.");
	        }else
	        {
	            logWriter.println(
	                "finished with errors or warnings." +
	                (this.settings.deleteOutputFiles()!=RDefaults.TempFiles.DEL_YES?
	                        "":" See file '"+error.getName()+"'.")			
	            );
	        }		
	        logWriter.close();
	        
	        return this.returnValue;
            
	    }catch(Exception ex)
	    {
	        stateMonitor.setFinished();
	        throw ex;
	    }
	}
	
	public boolean isCanceled()
	{
	    return this.exitState==RJob.CANCELED;
	}

	public File[] getInputFiles()
	{
		File[] f=
		{
			rinputFile,
			tmpSource,
			batch
		};
		return f;
	}
	
	public File[] getOutputFiles()
	{
		File[] f=
		{
			result,
			error
		};
		return f;		
	}
	
	
	/**
	 * @return
	 */
	public Date getPreExecutionDate()
	{
		return preExecutionDate;
	}

    /**
     * @return Returns the returnValue.
     */
    public int getExitValue()
    {
        return returnValue;
    }
    
       
    
    private class RTask extends AbstractTask
    {
        //private String cmd;
        private Process p=null;
        private int returnState=RJob.OK;
        
        public RTask()
        {
            super(
                "R Process"
            );
        }

        /* (non-Javadoc)
         * @see mayday.core.tasks.AbstractTask#initialize()
         */
        protected synchronized void initialize()        {        }
      
        /**
         * Start the R task.
         * 
         * @see mayday.core.tasks.AbstractTask#doWork()
         */
        protected void doWork()
        {         
            try
            {                
            	System.out.println("RTask::doWork starting");
                //cmd=RDefaults.COMMAND_INTERPRETER+" "+batch.getAbsolutePath();
                //logWriter.println("executing the cmd: "+cmd);
                //p=Runtime.getRuntime().exec(cmd); 
            	
            	ArrayList<String> commands = new ArrayList<String>();
            	commands.add(RDefaults.COMMAND_INTERPRETER);
            	if (RDefaults.COMMAND_INTERPRETER_SWITCH.length()>0)
            		commands.add(RDefaults.COMMAND_INTERPRETER_SWITCH);
            	commands.add(batch.getAbsolutePath());
            	
            	ProcessBuilder pb = new ProcessBuilder(commands);
            	pb.directory(new File(settings.getWorkingDir()));
            	p=pb.start();
                
                Thread.yield();
                
                //wait for finishing R
                returnValue=p.waitFor();
                
                doOK();
                System.out.println("RTask::doWork done");
                
            }catch(Exception e)
            {
            	if (e instanceof TaskCancelledException) {
            		doCancel();
            	} else {
            		logWriter.println(e.getStackTrace());
            	}
            }                 
        }
        
        /**
         * Forcibly kills the R process. This can be called by the
         * user, if the R process seems to hang.
         * <p>
         * 
         * The process is killed either with the
         * <code>kill -9</code> command (on unix-like systems)
         * or <code>tskill</code> (on Windows XP).
         *
         * If there is a system with other <em>kill</em>-commands,
         * imediately call me!
         */
        private void killRProcess()
        {
            //this forcibly kills on WindowsXP and Unix-derivates
            //killing on Win2k/NT has not been tested by mz (090116 fb: removed a todo saying it should be done)
            try
            {
                //read the pid file
                File pidfile=new File(
                    settings.getWorkingDir(),
                    RDefaults.PID_FILENAME
                );
                BufferedReader rd=new BufferedReader(new FileReader(
                    pidfile
                ));
                String pid=rd.readLine().trim();
                rd.close();
                
                //execute the killer with the given pid
                Process kill=Runtime.getRuntime().exec(
                    RDefaults.KILL_PROCESS+pid
                );
                kill.waitFor();
                
                if(!pidfile.delete())
                {
                    pidfile.deleteOnExit();
                }                
            }catch(Throwable t)
            {
                if(RDefaults.DEBUG) t.printStackTrace();
                
            }                    
        }
        
        public void doCancel() {
        	this.processingCancelRequest();
            killRProcess();
            returnState=RJob.CANCELED;
        }
        
        private void doOK() {
        	returnState=RJob.OK;
        }
              
        
        /**
         * The value of this tasks object is the value
         * supplied by the pressed action.
         * 
         * @return
         */
        public int getValue()
        {
            return returnState;
        }

    }
}
