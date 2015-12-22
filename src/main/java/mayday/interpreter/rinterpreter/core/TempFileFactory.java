package mayday.interpreter.rinterpreter.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.filemanager.FMFile;
import mayday.interpreter.rinterpreter.RDefaults;
import mayday.interpreter.rinterpreter.core.mi.MIOGroupRequirement;
import mayday.interpreter.rinterpreter.core.mi.MIOParserNotFoundException;
import mayday.interpreter.rinterpreter.core.mi.MIParserFactory;
import mayday.interpreter.rinterpreter.core.mi.MITypeParser;

/**
 * This class is used to create the temporary files.
 * 
 * 
 * @author Matthias
 *
 */
public class TempFileFactory
{
	
	public static File createRInputFile(RSettings settings)
		throws IOException, MIOParserNotFoundException
	{
        MasterTable mt=settings.getMasterTable();
        List<ProbeList> pl=settings.getProbeLists();
        
		File RinputFile=File.createTempFile(
			RDefaults.INPUTFILEPREFIX,
			RDefaults.TEMPFILESUFFIX,
			new File(settings.getWorkingDir())
			);
		
		FileWriter out=new FileWriter(RinputFile);
		
		//write DataSet
		writeDataSet(mt.getDataSet(), out);
		
		//write MasterTable
		writeMasterTable(mt, out);
        
        //write MIOTypes
        writeMIOTypes(settings, out);
        
        //write MIOGroups
        writeMIOGroups(settings, out);
        
        //compute a mapping of MIOExtendable->Arraylist<MIOType> which reflects the MIOGroupsValues
        Map<Object,List<MIType>> map=new HashMap<Object,List<MIType>>();
        
        /*
        Set<Object> mioExtendables=new TreeSet<Object>();
        mioExtendables.addAll(Arrays.asList(mt.getProbes().values().toArray()));
        for (Object o : pl)
        	mioExtendables.add(o);
        mioExtendables.addAll(pl);*/
        
/*        for(Object p:mioExtendables) {
            map.put(p,new ArrayList<MIType>());
        }
        */        
        for (Object o : mt.getProbes().values())
        	map.put(o, new ArrayList<MIType>());
        for (Object o : pl)
        	map.put(o, new ArrayList<MIType>());
        
        for(MIGroupSelection<MIType> select:settings.getMIOSelection())
        {
            Map<Object, MIType> list=select.computeUniqueSelection();
            Set<Object> visited=new TreeSet<Object>();
            for(Entry<Object,MIType> entry:list.entrySet())
            {
                if(map.containsKey(entry.getKey()))
                {
                    visited.add(entry.getKey());
                    map.get(entry.getKey()).add(entry.getValue());
                }
            }
            
            //add NULLs/NAs for the other possible MIOExtendables
            
            TreeSet<Object> diff=new TreeSet<Object>(map.keySet());
            if(!diff.removeAll(visited))
            {
                throw new RuntimeException("Could not create the R input file. (MIOExtendable exception)");
            }
            
            for(Object ext:diff)
            {
                map.get(ext).add(null);
            }
        }
		
		//write List of ProbeLists
		writeProbeLists(pl,map,settings,out);
		
		//write Probes
		writeProbes(mt,map,settings,out);
		
		out.close();
		return RinputFile;
	}
	
	/**
     * Write the MIOGroups section. That is a list of the given
     * MIOGroup ids (see RSrcDescription files).
     * <br><br>
     * E.g. 
     * <pre>
     *   [%miogroups]
     *   variance   mean    pearson
     * </pre>
     * 
     * @param settings
     * @param out
     */
    private static void writeMIOGroups(RSettings settings, FileWriter out)
    throws IOException
    {
        out.write(RDefaults.RResults.MG+"\n");
        List<MIOGroupRequirement> list=settings.getSource().getRequiredMIOGroups();
        for(Iterator<MIOGroupRequirement> iter=list.iterator(); iter.hasNext();)
        {
            MIOGroupRequirement r=iter.next();
            if(!r.isIN()) continue;
            
            out.write(
                r.getId()+
                (iter.hasNext()? "\t":"\n")
            );
        }   
        out.write("\n"); //Do not remove! here we need to write 2 linefeeds!!!
    }

    /**
     * This method collects the runtime types of the the given MIOs. 
     * The output looks like:
     * <pre>
     *   [%miotypes]
     *   mayday.core.mi.DoubleMIO   mayday.core.mi.IntegerMIO ...
     * </pre>
     * 
     * 
     * @param settings
     * @param out
     */
    private static void writeMIOTypes(RSettings settings, FileWriter out)
    throws IOException
    {
        out.write(RDefaults.RResults.MI+"\n");
        Map<String, Integer> map=new HashMap<String, Integer>();
        
        int i=0;
        for(MIGroupSelection<MIType> s:settings.getMIOSelection())
        {
            Map<Object,MIType> list=s.computeUniqueSelection();
            for(Entry<Object,MIType> t : list.entrySet())
            {
                if(!map.containsKey(t.getValue().getType()))
                {
                    map.put(t.getValue().getType(), ++i);
                }
            }
        }
        
        settings.setMioTypes(map); 
        
        for(int j=1; j<=map.size(); ++j)
        {
            out.write(
                settings.getMioTypes2().get(j)+
                (j!=map.size()? "\t" : "\n")
            );
        }
        
        out.write("\n"); //Do not remove! here we need to write 2 linefeeds!!!    
    }

    /**
	 * Write the DataSet to the temporary R-input-file.
	 * 
	 * @param ds
	 * @param out
	 * @throws IOException
	 */
	private static void writeDataSet(DataSet ds, FileWriter out)
		throws IOException
	{
		out.write(RDefaults.RResults.DS+"\n");
		out.write(ds.getName()+"\n");
		out.write(replaceControlChars(ds.getAnnotation().getQuickInfo())+"\n");
		out.write(replaceControlChars(ds.getAnnotation().getInfo())+"\n");
		out.write(RDefaults.RString(ds.isSilent())+"\n");
	}
	
	/**
	 * Write the MasterTable information to the temporary R-input-file.
	 *  
	 * @param mastertab
	 * @param out
	 * @throws IOException
	 */
	private static void writeMasterTable(MasterTable mt, FileWriter out) 
		throws IOException
	{
		out.write(RDefaults.RResults.MT+"\n");
		
		//data mode
		out.write("-- datamode removed --\n");//mt.getDataMode().getName()+"\n");
		
		//transformation mode
		out.write("--transformation mode removed --\n");//mt.getTransformationMode().getName()+"\n");
		
		//probes
		Object[] probes=mt.getProbes().values().toArray();
		for(int i=0; i!=probes.length;++i)
		{
			out.write(((Probe)probes[i]).getName());
			if(i!=probes.length-1)
			{
				out.write("\t");
			}
		}
		out.write("\n");
		
		//Experiments
		for(int i=0; i!=mt.getNumberOfExperiments();++i)
		{
			out.write(
				mt.getExperimentName(i)
			);
			if(i!=mt.getNumberOfExperiments()-1)
			{
				out.write("\t");
			}
		}
		
		/* This implementation uses the expOrder List
		List expOrder=mt.getExperimentOrder();
		System.out.println(expOrder);
		for(int i=0;i!=expOrder.size();++i)
		{
			out.write(
				mt.getExperimentName(
					((Integer)expOrder.get(i)).intValue()
				)
			);
			if(i!=expOrder.size())
			{
				out.write("\t");
			}
		}*/
		out.write("\n");
	}
	
	
	/**
	 * Write the probe lists to the temporary R-input-file.
	 * 
	 * @param probelists
	 * @param map 
	 * @param settings
     * @param out
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private static void writeProbeLists(List<ProbeList> probelists, Map<Object, List<MIType>> map, RSettings settings, FileWriter out) 
		throws IOException, MIOParserNotFoundException
	{
		out.write(RDefaults.RResults.PL+"\n");
		
		Object[] l_pls=probelists.toArray();
		for(int i=0; i!=l_pls.length; ++i)
		{
			ProbeList pl=(ProbeList)l_pls[i];
			out.write(pl.getName()+"\t");
			out.write(replaceControlChars(pl.getAnnotation().getQuickInfo())+"\t");
			out.write(replaceControlChars(pl.getAnnotation().getInfo())+"\t");
			out.write(RDefaults.RString(pl.getColor())+"\t");
			out.write(RDefaults.RString(pl.isSticky())+"\t");
			out.write(RDefaults.RString(pl.isSilent()));
			
            //write mios
            for(Iterator<MIType> iter=map.get(pl).iterator(); iter.hasNext();)
            {
                MIType mio=iter.next();
                if(mio==null)
                {
                    out.write("\tNA");
                }else {
                    MITypeParser parser=MIParserFactory.createParser(mio.getType());
                    out.write(
                        "\t"+
                        settings.getMioTypes().get(mio.getType())+":"+
                        replaceControlChars(parser.asString(mio))
                    );
                }
            }
            
            // write probes
            int n=pl.getNumberOfProbes();
            for(int j=0; j!=n; ++j)
            {
                Probe p=pl.getProbe(j);
                out.write(
                    "\t"+
                    p.getName()
                );              
            }       
            
            out.write("\n");
		}		
	}

	/**
	 * Write all the probes contained in the master table. If you only
	 * need to extract the probes from the given probelists, use the
	 * related function in R.
	 * 
	 * @param mt, the MasterTable
	 * @param settings 
     * @param map 
     * @param out
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private  static void writeProbes(MasterTable mt, Map<Object, List<MIType>> map, RSettings settings, FileWriter out) 
		throws IOException, MIOParserNotFoundException
	{ 
		out.write(RDefaults.RResults.PR+"\n");
		
		Object[] probes=mt.getProbes().values().toArray();
		for(int i=0; i!=probes.length;++i)
		{
			Probe p=(Probe)probes[i];
			
			out.write(p.getName()+"\t");
			if (p.getAnnotation()!=null) {
				out.write(replaceControlChars(p.getAnnotation().getQuickInfo())+"\t");
				out.write(replaceControlChars(p.getAnnotation().getInfo())+"\t");
			} else {
				out.write("\t\t");
			}
			out.write(RDefaults.RString(p.isImplicitProbe()));
			
			//print the values
			for(int j=0; j!=mt.getNumberOfExperiments();++j)
			{
				out.write(
                    "\t"+
					(p.getValue(j)!=null?
                            p.getValue(j).toString():
                            "NA")
				);
			}
			/*using the experiment order list
			for(int j=0; j!=expOrder.size();++j)
			{
				out.write(
					p.getValue(
						((Integer)expOrder.get(j)).intValue()
					).toString()
					+"\t"
				);
			}//*/
			
            
			// write mios
            for(Iterator<MIType> iter=map.get(p).iterator(); iter.hasNext();)
            {
                MIType mio=iter.next();
                
                if(mio==null)
                {
                    out.write("\tNA");
                }else
                {
                    MITypeParser parser=MIParserFactory.createParser(mio.getType());
                    out.write(
                        "\t"+
                        settings.getMioTypes().get(mio.getType())+":"+
                        replaceControlChars(parser.asString(mio))
                    );
                }
            }
            
            //print the problists
			List<ProbeList> pl=p.getProbeLists();
			for(int j=0; j!=pl.size();++j)
			{
				out.write(
                    "\t"+
					((ProbeList)pl.get(j)).getName()
				);
			}
            
            out.write("\n");
		}
	}
	
	/**
	 * Write the temporary source file. This file will be loaded into
	 * R via its StdIn.
	 * 
	 * @throws IOException
	 */
	public static File createTempSourceFile(RSettings settings, RJob job) 
    throws IOException, MIOParserNotFoundException, ClassNotFoundException
	{
		String prefix=new File(settings.getSource().getFilename()).getName();
		File tmpSource=File.createTempFile(
			prefix.substring(0,prefix.lastIndexOf(".")),
			RDefaults.RSUFFIX,
			new File(settings.getWorkingDir())
		);	
		
		
	  	FileWriter out=new FileWriter(tmpSource);
	  	
	  	//write initial hint, with current date and time
	  	out.write(RDefaults.RSrcComponents.INITIAL_HINT.replaceAll(
			RDefaults.RSrcComponents.TIME_REPLACE,
		  	""+new Date(System.currentTimeMillis())
	  	));

		// make the RForMayday functions available		
	  	// get path to RForMayday file
	  	FMFile RForMayday = PluginManager.getInstance().getFilemanager().getFiles("RForMayday.R", true).next();
	  	if (RForMayday==null) {
	  		// fall back to old package mechanism
	  		out.write(RDefaults.RSrcComponents.LIBRARY+"\n");
	  	} else {
	  		RForMayday.extract();
	  		// Windows directory separators have to be doubly escaped in R, or converted to "/"
	  		out.write("source(\""+RForMayday.getFullPath().replace("\\", "/")+"\")\n");
	  	}
		
		// set default plotting device:
		out.write(
		    RDefaults.RSrcComponents.SET_DEFAULT_DEVICE.
		    replaceAll(
		        RDefaults.RSrcComponents.DEVICE_REPLACE,
		        RDefaults.RSrcComponents.SET_GRAPHICS_DEVICES[settings.getPlotType()]
		    )+"\n"
		);
  	
	  	//setwd
	  	out.write(
	  		RDefaults.replaceFileName(
	  			RDefaults.RSrcComponents.SETWD,
	  			RDefaults.RSrcComponents.FILE_REPLACE,
	  			settings.getWorkingDir()
	  	));
  	
	  	//"communication area" :)
	  	out.write(
	  	    RDefaults.replaceFileName(
	  	        RDefaults.RSrcComponents.COM_AREA,
	  	        RDefaults.RSrcComponents.PID_REPLACE,
	  	        settings.getStatusFile().getAbsolutePath()
	  	));
	  	
	  	//create the process id file
	  	out.write(
	  	    RDefaults.replaceFileName(
	  	        RDefaults.RSrcComponents.CAT_PID,
	  	        RDefaults.RSrcComponents.PID_REPLACE,
	  	        settings.getWorkingDir()+"/"+RDefaults.PID_FILENAME
	  	));
	  	
	  	
	  	//add some presets as given in RDefaults.RSrcComponents
	  	for(int i=0; i!=RDefaults.RSrcComponents.PRESETS.length; ++i)
	  	{
	  	    out.write(RDefaults.RSrcComponents.PRESETS[i]);
	  	    out.write("\n");
	  	}
        
        //create the parsing/output functions
        for(int i=1; i<=settings.getMioTypes().size();++i) 
        {
            //parser functions: direction Java --> R
            out.write(
                settings.getMioTypes2().get(i)+".parse<-"+
                MIParserFactory.createParser(settings.getMioTypes2().get(i)
                ).parseR()+"\n"
            );
            
            //output functions: direction R --> Java
            out.write(
                settings.getMioTypes2().get(i)+".output<-"+
                MIParserFactory.createParser(settings.getMioTypes2().get(i)
                ).outputR()+"\n"
            );
        }
        
        
	  	//source("<selectedFile.R>")
	  	out.write(
	  		RDefaults.replaceFileName(
	  			RDefaults.RSrcComponents.SOURCE,
	  			RDefaults.RSrcComponents.FILE_REPLACE,
	  			settings.getSource().getFilename()	
	  	));
	    	
		//datastructures<-datastructures.read(...)
		out.write(
		  RDefaults.RSrcComponents.DATASTRUCTURES_FIELD
		  + "<-"
		  + RDefaults.replaceFileName(
		  		RDefaults.RSrcComponents.DATASTRUCTURES_READ,
		  		RDefaults.RSrcComponents.FILE_REPLACE,
		  		job.getRInputFile().getAbsolutePath()
			)		
		);
	  
	  	//apply
	  	out.write(
	  		RDefaults.RSrcComponents.RESULT_FIELD
	  		+ "<-"
	  		+ settings.getSource().getName()+"("
	  		+ RDefaults.RSrcComponents.DATASTRUCTURES_FIELD
	  	);
	  	ArrayList<RSourceParam> params=settings.getSource().getParameters();
	  	for(int i=1; i!=params.size(); ++i) //the first parameter is already written
	  	{
	  		RSourceParam p=(RSourceParam)params.get(i);
	  		
	  		//handle the formal argument
	  		if(p.getName().equals("..."))
	  		{
	  			
	  			if(p.getValue()!=null && !p.getValue().trim().equals(""))
	  			{
	  				out.write(
	  				  ", "
	  				  +p.getValue()
	  				);
	  				
	  			}
	  		}
	  		//added 05.10.08 to compensate for String arguments nt
	  		else if(p.getType() != null && p.getType().id().equals(RDefaults.RSDesc.TYPENAME_STRING)){
	  			if(p.getValue()!=null && !p.getValue().trim().equals("")){
	  				String tmp = ", " +  p.getName() +"=" +"\"" + p.getValue() +"\"";
	  				System.out.println(tmp);
                    out.write(tmp);
                          
	  			}
	  			
	  		}
	  		
	  		else //handle normal arguments
	  		{
	  			out.write(
					", "
					+p.getName()+"="
					+(p.getValue()!=null && !p.getValue().trim().equals("")? 
					  	p.getValue():"NULL" ) 
				);
	  		}
	  	}
	  	out.write(");\n"); //close bracket and enter
        
        //write warnings
        out.write("warnings(file=2)\n");
        out.write("graphics.off()\n");

	  	out.write(RDefaults.RSrcComponents.RESULT_COMMENT+"\n");

		//create the output of result.field
		out.write(RDefaults.RSrcComponents.CREATE_OUTPUT.
			replaceAll(
				RDefaults.RSrcComponents.FIELD_REPLACE,
				RDefaults.RSrcComponents.RESULT_FIELD				
			)
		);
		
		out.close();
	  
	  	return tmpSource;
	}

	public static File createErrorFile(RSettings settings)
		throws IOException
	{
		File error=File.createTempFile(
			RDefaults.ERRORPREFIX,
			RDefaults.ERRORSUFFIX,
			new File(settings.getWorkingDir())
		);
		
		return error;
	}
	
	public static File createResultFile(RSettings settings)
		throws IOException
	{
		File result=File.createTempFile(
			RDefaults.RESULTPREFIX,
			RDefaults.RESULTSUFFIX,
			new File(settings.getWorkingDir())
		);
		
		return result;
	}
	
	
	/**
	 * Write the shell script that invokes R and redirects the
	 * three standard streams to the related files.
	 * 
	 * @throws Exception
	 */
	public static File createBatch(RSettings settings,RJob job)
		throws IOException
	{
		File batchFile=File.createTempFile(
			RDefaults.BATCHPREFIX,
			RDefaults.BATCHSUFFIX,
			new File(settings.getWorkingDir())
		);
		
		if (settings==null || settings.getBinary()==null)
			throw new RuntimeException ("Can not find R binary!");
	
		String rhome=new File(settings.getBinary())
			.getParentFile().getParentFile().getAbsolutePath();
        
        
        String rexec = new File(settings.getBinary()).getAbsolutePath();
        
		String exec=
			RDefaults.replaceFileName(
				RDefaults.BATCH_CONTENT,
				RDefaults.REPLACE_RHOME,
				"\""+rhome+"\"");
		exec=RDefaults.replaceFileName(
				exec,
				RDefaults.REPLACE_RBINARY,
				"\""+rexec+"\"");
		exec=exec.replaceAll(
				RDefaults.REPLACE_RARGS,
				RDefaults.getRArgs(settings.getBinary()));
		exec=RDefaults.replaceFileName(
				exec,
				RDefaults.REPLACE_StdIn,
				"\""+job.getTmpSource().getAbsolutePath()+"\"");
		exec=RDefaults.replaceFileName(
				exec,
				RDefaults.REPLACE_StdOut,
				"\""+job.getResult().getAbsolutePath()+"\"");
		exec=RDefaults.replaceFileName(
				exec,
				RDefaults.REPLACE_StdErr,
				"\""+job.getError().getAbsolutePath()+"\"");
				

		FileWriter w=new FileWriter(batchFile);
		w.write(exec);	
		w.close();	

		return batchFile;
	}
	
	private static String replaceControlChars(String s)
	{
		return s.replaceAll("\\\\","\\\\")
				.replaceAll("\t","\\t")
				.replaceAll("\r\n","\\n")
				.replaceAll("\n","\\n");
	}
}	



