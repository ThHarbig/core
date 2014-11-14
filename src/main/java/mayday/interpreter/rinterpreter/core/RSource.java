package mayday.interpreter.rinterpreter.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mayday.interpreter.rinterpreter.RDefaults;
import mayday.interpreter.rinterpreter.core.RFunctionParser.FunParam;
import mayday.interpreter.rinterpreter.core.RFunctionParser.RFunction;
import mayday.interpreter.rinterpreter.core.mi.MIOGroupRequirement;
import mayday.interpreter.rinterpreter.exceptions.RFunctionParserException;
import mayday.interpreter.rinterpreter.exceptions.RSourceNotFoundException;
import mayday.interpreter.rinterpreter.exceptions.RSourceParamException;
import mayday.interpreter.rinterpreter.gui.FunctionChooserDialog;

import org.xml.sax.SAXException;

/**
 * In principle the representation of the R source description
 * file.
 * 
 * @author Matthias
 *
 */
@SuppressWarnings("unchecked")
public class RSource implements Comparable
{
    private String filename;    	// full filename, including the path
    private String name;        	// Function name to apply
    private String descriptor;  	// normally the function name
    private String quickinfo;   	// short info what the specified function does
    private ArrayList<RSourceParam> parlist;     	// the parameters
   	private File srcFile;
    private List<MIOGroupRequirement> mioGroups=null;

	/**
	 * 	Create an RSource by a given File,
	 *  read and parse the related description file.
	 * 
	 * @param srcFile
	 * @throws RuntimeException
	 * @throws IOException
	 * @throws SAXException
	 */

    public RSource(File srcFile) throws RuntimeException,IOException,SAXException{
    	this(srcFile, false);
    }
    
    public RSource(File srcFile, boolean silent) throws RuntimeException,IOException,SAXException
	{
		this.parlist=new ArrayList<RSourceParam>();
		this.filename=srcFile.getAbsolutePath();
		this.srcFile=srcFile;
		
		// testing if the source file exists
		if(!srcFile.exists())
			throw new FileNotFoundException(srcFile.getName());
		
		// testing if File(filename) is an R-source file
		if(!(new RFileFilter()).accept(srcFile))
			throw new RuntimeException("'"+srcFile.getName()+"' is not an R Source File!");
		
		// try to open the related description File
		File descFile=new File(RSource.getRSDescFileName(srcFile.getAbsolutePath()));	
		if(!descFile.exists())
		{
			createRSDescFile(descFile);
		}
		
		//parsing the description file
		RSourceDescriptionParser.parseXML(descFile,this);
		
		//if the source file is more recent than the xml file
		//removed 06.01.2009 because it is not needed Nastasja Trunk
//	//	if(this.getSourceFile().lastModified()>descFile.lastModified() && !silent)
//	//	{
//	//		RDefaults.messageGUI(
////			"The source file '"+this.getSourceFile().getName()+"' "
//				+ "seems to be more recent than its description file. \n"
//				+ "The function information and the file description "
//				+ "will be read again.\n"
//				+ "",
//				RDefaults.Messages.Type.INFO
//			);
//			
//			this.chooseRFunction();
//			this.readInfo();
//		}
	}
	
	private RSource()
	{
		filename=null;
		name=null;        
		descriptor=null; 
		quickinfo=null;  
		parlist=new ArrayList<RSourceParam>();    
		srcFile=null;
		
	}
	
	/**
	 * Create and write the R source description of this RSource
	 * to the given file.
	 * 
	 * @param descFile
	 * @throws RuntimeException
	 * @throws IOException
	 */
	private void createRSDescFile(File descFile) throws RuntimeException,IOException
	{
		try
		{
			descFile.createNewFile();

			// parse the R source file for function declarations
			FunctionChooserDialog fDlg=new FunctionChooserDialog(RDefaults.MAYDAY_FRAME(), this.srcFile);
			
			
			// write the description to file
			if(fDlg.showDialog()==RDefaults.Actions.SELECT)
			{				
				//write the file
				RSourceDescriptionParser.writeXML(descFile, fDlg.getRFunction(), fDlg.getDescription(),this);				
				
			} else
			{
				descFile.delete();
			}
			
		} catch(IOException ex)
		{
			
	 		throw new IOException("Could not write the file '"+descFile.getAbsolutePath()+"'."+
		 								"\n"+ex.getMessage());
		}
	}
	

	/**
	 * Compare RSources.
	 * 
	 * @param cmp the Object to compare to
	 * @return true, if the filename of the given source
	 * equals the filename of this source.
	 */
	public boolean equals(Object cmp)
	{
		return this.filename.equals(((RSource)cmp).getFilename());
	}
	
    public String getFilename()
    {
        return this.filename;
    }

    public void setFilename(String filename)
    {
        this.filename=filename;
    }
    
    public File getSourceFile()
    {
    	return this.srcFile;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name=name;
    }

    public String getDescriptor()
    {
        return this.descriptor;
    }

    public void setDescriptor(String descriptor)
    {
        this.descriptor = descriptor;
    }

	public ArrayList<RSourceParam> getParameters()
	{
		return this.parlist;
	}
	
	public void addParameter(RSourceParam p)
	{
		this.parlist.add(p);
	}

    /**
     * Set the value of the specified parameter.
     * This method can be used to specify the parameters
     * for an applicable function when using the
     * R-interface in internal mode.
     * 
	 * @param par, the name of the parameter to change
	 * @param val, the new value (any R-expression as defined in
	 *             the R-language-specification)
	 * @throws RSourceParamException indicating that the considered
	 *         parameter does not exist.
	 */
	public void setValueOf(String parId, String value)
    {
    	int index=this.parlist.indexOf(parId);
    	if(index==-1)
    	{
    		throw new RSourceParamException(
				"There is no parameter '"+parId+"' " +
				"in function '"+this.name+"' " +
				"in file ' "+this.srcFile.getName()+"'!");
    	}
    	RSourceParam p=(RSourceParam)this.parlist.get(index);
    	p.setValue(value);
    }
    
    /**
     * Set the value of the parameters.
     * This method can be used to specify the parameters
     * for an applicable function when using the
     * R-interface in internal mode.
     * 
     * The number of values must be exactly parlist.size()-1
     * because the DATA-argument cannot be given a value explicitly.
     * The order of values is the same as in the function definition.
     * 
	 * @param values
	 * 
	 * @throws RSourceParamException indicating that the number of
	 *         values does not match the expected number.
	 */
	public void setValues(List<String> values)
    {
    	if(this.parlist.size()-1!=values.size())
    	{
    		throw new RSourceParamException(
    			"Number of values does not match the expected number" +
    			" of parameters!"
    		);
    	}
    	
    	Iterator<RSourceParam> iter=this.parlist.iterator();
    	iter.next(); //ommit the DATA-object
    	Iterator<String> jter=values.iterator();
    	while(iter.hasNext())
    	{
    		((RSourceParam)iter.next()).setValue(jter.next());
    	}
    }

	public String getInfo()
	{
		return this.quickinfo;
	}
	
	public void setInfo(String qInfo)
	{
		this.quickinfo=qInfo;
	}
	
    /**
     * Re-read the function description, i.e. the first comment
     * from the R-source file.
     *
     */
	public void readInfo()
	{
		try
		{
			RFunctionParser fparser=new RFunctionParser(this.srcFile);
			this.quickinfo=new String(fparser.parseDescription());
		}catch(IOException ex)
		{
			ex.printStackTrace();
			RDefaults.messageGUI(
				"Could not open the source file '"+
				this.srcFile.getName()+
				"'.",
				RDefaults.Messages.Type.ERROR);		
		}
		
	}
	
	public static String getRSDescFileName(String filename)
	{
		return filename.substring(0,filename.length()-2)+".xml";
	}
	public String getRSDescFileName()
	{
		return RSource.getRSDescFileName(this.filename);
	}
	
	public void setRFunction(RFunction fun)
	{
		if(!this.name.equals(fun.getId()))
		{
			this.name=fun.getId();
			this.parlist=new ArrayList<RSourceParam>();
			Iterator<FunParam> iter=fun.getParamListIterator();
			while(iter.hasNext())
			{
				this.addParameter(new RSourceParam((FunParam)iter.next()));			
			}
		}else
		{
			ArrayList<RSourceParam> newParList=new ArrayList<RSourceParam>();
			Iterator<FunParam> iter=fun.getParamListIterator();
			
			while(iter.hasNext())
			{
				RSourceParam p=new RSourceParam((FunParam)iter.next());
				if(this.parlist.contains(p))
				{
					int i=this.parlist.indexOf(p);
					RSourceParam cmp=(RSourceParam)this.parlist.get(i);
                    p=cmp;
//					if(cmp.getDefault()!=null && !cmp.getDefault().trim().equals(""))
//					{
//						p.setDefault(cmp.getDefault());
//					} 
//					p.setDescription(cmp.getDescription());
				}
				newParList.add(p);
			}
			this.parlist=newParList;
		}
		
	}

	public void chooseRFunction()
	{
		try
		{
			FunctionChooserDialog fcDlg=
				new FunctionChooserDialog(
						RDefaults.MAYDAY_FRAME(), 
						new File(this.getFilename())
				);
					
			int result=fcDlg.showDialog();
				
			if(result==RDefaults.Actions.SELECT)
			{
				RFunction fun=fcDlg.getRFunction();
				this.setRFunction(fun);
			}
			
			//write the changes to description file
			RSourceDescriptionParser.writeXML(
				new File(this.getRSDescFileName()),
				this
			);
		}catch(IOException ex)
		{
			ex.printStackTrace();
			RDefaults.messageGUI(
				"Could not open the source file '"+
				this.srcFile.getName()+
				"'.",
				RDefaults.Messages.Type.ERROR);			
		}		
	}
	
	public String toString()
	{
		return this.descriptor;
	}
	
	public String functionString()
	{
		StringBuffer buf=new StringBuffer();
		buf.append(this.name+"(");
		buf.append(RDefaults.RSrcComponents.DATASTRUCTURES_FIELD);
		for(int i=1; i!=this.parlist.size(); ++i)
		{
			RSourceParam par=(RSourceParam)this.parlist.get(i);
			if(par.getName().equals("..."))
			{
				if(par.getValue()!=null && par.getValue().trim().equals(""))
				{
					buf.append(
					  ","+
					  par.getValue()
					);
				}
			}else
			{
				buf.append(
					","+
					par.getName()+
					"="+(par.getValue()!=null && !par.getValue().equals("")?par.getValue():"NULL")
				);
			}		
		}		
		buf.append(")");
		return buf.toString();
	}
	
	
	/**
	 * Create an instance of RSource to use it in an internal 
	 * run of the R interface. In this construction mechanism
	 * the description file is not used, so the original 
	 * default values as given in the function definition are used.
	 * 
	 * @param src, the source file containing an applicable function
	 * @param funId, the identifier of the applicable function
	 * @return a new object of type RSource
	 */
	public static RSource createInstance(File src, String funId)
	{
		if(!src.exists())
		{
			throw new RSourceNotFoundException("Could not find '"+src.getAbsolutePath()+"'!");
		}
		
		RFunctionParser fp=null;
		RSource rsrc=new RSource();
		rsrc.setName(funId);
		rsrc.setFilename(src.getAbsolutePath());
		rsrc.srcFile=src;
		try
		{
			fp=new RFunctionParser(src);
			ArrayList<RFunction> functions=fp.parse();
			int index=functions.indexOf(funId);
			if(index==-1)
			{
				 throw new RuntimeException("Function '"+funId+"' not found!");
			}			
			RFunction rfun=(RFunction)functions.get(index);
			if(rfun==null)
			{
				throw new RuntimeException("Function '"+funId+"' not found!");
			}
			
			rsrc.setRFunction(rfun);
			
		}catch(Exception ex)
		{
			throw new RFunctionParserException(ex.getMessage());
		}		
		
		return rsrc;
	}

    public List<MIOGroupRequirement> getRequiredMIOGroups()
    {
        if(this.mioGroups==null) //lazy instantiation
        {
            this.mioGroups=new ArrayList<MIOGroupRequirement>();
        }
        return this.mioGroups;
    }
    
    public void setRequiredMIOGroups(List<MIOGroupRequirement> list)
    {
        this.mioGroups=list;
    }
    
    /**
     *  Returns true if there are input MIOGroupRequirements for this
     *  RSource.
     */
    public boolean requiresMIOGroups()
    {
        
        if (this.mioGroups==null || this.mioGroups.size()==0)
        {
            return false;
        }
        
        for(MIOGroupRequirement r:getRequiredMIOGroups())
        {
            if(r.isIN()) return true;
        }
        
        return false;
    }
    
    
    /**
     * Used for sorting the RSources within the JList. 
     * Lexicographical order w.r.t. the descriptor.
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object cmp)
    {
        return this.toString().compareToIgnoreCase(cmp.toString());
    }
	
	
}
