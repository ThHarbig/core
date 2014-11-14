package mayday.interpreter.rinterpreter.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import mayday.core.pluma.PluginManager;
import mayday.interpreter.rinterpreter.RDefaults;
import mayday.interpreter.rinterpreter.RDefaults.RSDesc;
import mayday.interpreter.rinterpreter.core.RFunctionParser.FunParam;
import mayday.interpreter.rinterpreter.core.RFunctionParser.RFunction;
import mayday.interpreter.rinterpreter.core.mi.MIOGroupRequirement;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Validating parser for the R source description files.
 * The Parser takes the RSource and fills it with the
 * specifications from the file.
 * <br><br>
 * The other task of this class is to provide methods
 * for writing proper R source description files.
 * 
 * @author Matthias
 *
 */
public class RSourceDescriptionParser
{
    /**
     * Parse the given XML-file and invoke the 
     * <tt>RDescContentHandler</tt> to fill the
     * values in the given <tt>RSource</tt>.
     * 
	 * @param xmlFile
	 * @param RSrc
	 * @throws RuntimeException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void parseXML(File xmlFile, RSource RSrc) throws RuntimeException,SAXException,IOException
    {
    	SAXParserFactory factory=SAXParserFactory.newInstance();
    	factory.setValidating(true);
		SAXParser saxParser;
    	try
    	{
    		saxParser=factory.newSAXParser();
    	}catch(ParserConfigurationException ex)
    	{
    		 throw new RuntimeException(ex.getMessage());
    	}
  		RDescContentHandler contentHandler=new RDescContentHandler(RSrc); //this.RSrc);
   	   	saxParser.parse(xmlFile,contentHandler);
    }
        
	/**
	 * Write an R source description.
	 * 
	 * @param file, to write into
	 * @param fun
	 * @param description
	 * @param src
	 * @throws IOException
	 */
	public static void writeXML(File file, RFunction fun, String description, RSource src) throws IOException
	{		
		try
		{
			PrintWriter out=new PrintWriter(new FileOutputStream(file));
			
			//print xml header
			out.println(RDefaults.RSDesc.HEADER);
			//print root node
			out.println("<"+RDefaults.RSDesc.ROOT+">");
			//print functionname node
			out.println("\t<"+RDefaults.RSDesc.FUNCNAME_ELEM+" "
						+RDefaults.RSDesc.FUNCATTRIB_ID+"=\""
						+fun.getId()+"\" "
						+RSDesc.FUNCATTRIB_DESC+"=\""						
						+new File(src.getFilename()).getName()+"\"/>"
					 );
			
			//print quickinfo node
			out.print("\t<"+RSDesc.QUICKINFO_ELEM+"><![CDATA[");
			if(description==null)
			{
				out.print(RSDesc.QUICKINFO_DEFAULT);
			}else
			{
				out.print(description);
			}
			//end print quickinfo node
			out.println("]]></"+RSDesc.QUICKINFO_ELEM+">");
			
			//print parameter list
			out.println("\t<"+RSDesc.PARLIST_ELEM+">");
			Iterator<FunParam> iter=fun.getParamListIterator();
			while(iter.hasNext())
			{
				FunParam par=(FunParam)iter.next();
				out.println("\t\t<"+RSDesc.PARAM_ELEM+" "
						+RSDesc.PARAMATTRIB_NAME+"=\""
						+par.getId()+"\""
						+(par.getDefault()!=null?
							" "+RSDesc.PARAMATTRIB_DEFAULT+"=\""+
							escapeEntity(par.getDefault())+"\""
							:"")
						+(par.getDescription()!=null && !par.getDescription().trim().equals("")?
						 	" "+RSDesc.PARAMATTRIB_DESC+"=\""+
						 	escapeEntity(par.getDescription())+"\""
							:"")
						+"/>");
			}
			
			//end println parameterlist
			out.println("\t</"+RSDesc.PARLIST_ELEM+">");
			
			//end print root node
			out.println("</"+RDefaults.RSDesc.ROOT+">");
			
			out.close();
			
		}catch(FileNotFoundException ex)
		{
			throw new IOException(ex.getMessage());
		}
	}
    
    /**
     * Write an R source description.
     * 
	 * @param file, to write into
	 * @param src
	 * @throws IOException
	 */
	public static void writeXML(File file, RSource src) throws IOException
    {
		try
		{
			PrintWriter out=new PrintWriter(new FileOutputStream(file));
			
			//print xml header
			out.println(RDefaults.RSDesc.HEADER);
			//print root node
			out.println("<"+RDefaults.RSDesc.ROOT+">");
			//print functionname node
			out.println("\t<"+RDefaults.RSDesc.FUNCNAME_ELEM+" "
						+RDefaults.RSDesc.FUNCATTRIB_ID+"=\""
						+src.getName()+"\" "
						+RSDesc.FUNCATTRIB_DESC+"=\""
						+src.getDescriptor()+"\"/>"
					 );
			
			//print quickinfo node
			out.print("\t<"+RSDesc.QUICKINFO_ELEM+"><![CDATA[");
			out.print(src.getInfo());
			//end print quickinfo node
			out.println("]]></"+RSDesc.QUICKINFO_ELEM+">");
			
			//print parameter list
			out.println("\t<"+RSDesc.PARLIST_ELEM+">");
			Iterator<RSourceParam> iter=src.getParameters().iterator();
			while(iter.hasNext())
			{
				RSourceParam par=(RSourceParam)iter.next();
				out.print("\t\t<"+RSDesc.PARAM_ELEM+" "
						+RSDesc.PARAMATTRIB_NAME+"=\""
						+par.getName()+"\""
						+(par.getDefault()!=null?
							" "+RSDesc.PARAMATTRIB_DEFAULT+"=\""+
							escapeEntity(par.getDefault())+"\""
							:"")
						+(par.getDescription()!=null?
							" "+RSDesc.PARAMATTRIB_DESC+"=\""+
							escapeEntity(par.getDescription())+"\""
							:"")
						);
                if(par.getType()==null)
                {
                    out.println("/>");
                }else
                {
                    out.println(">");
                    out.println(par.getType().xmlString(3));
                    out.println("\t\t</"+RSDesc.PARAM_ELEM+">");
                }
			}
			
			//end println parameterlist
			out.println("\t</"+RSDesc.PARLIST_ELEM+">");
			
            //print requires
            out.println("\t<"+RSDesc.REQUIRES_ELEM+">");
            
            for(MIOGroupRequirement r: src.getRequiredMIOGroups())
            {
                out.println("\t\t"+r.xmlString());
            }
             
            //end requires
            out.println("\t</"+RSDesc.REQUIRES_ELEM+">");
            
            
            
			//end print root node
			out.println("</"+RDefaults.RSDesc.ROOT+">");
			
			out.close();
			
		}catch(FileNotFoundException ex)
		{
			throw new IOException(ex.getMessage());
		}    	
    }
    
    public static String escapeEntity(String input)
    {
        return input.replaceAll("&","&amp;").replaceAll("\"","&quot;");
    }
    
    /**
     * The ContentHandler for the R source description parser.
     * 
	 * @author Matthias
	 *
	 */
	private static class RDescContentHandler 
    extends DefaultHandler
    //implements ContentHandler
    {
    	private RSource RSrc;
        
        private boolean isQInfo=false;
        
        private StringBuffer qInfoBuffer=new StringBuffer();
        private List<String> typeEntries=new ArrayList<String>();
        private RSourceParam currentParam;
        private ParameterType currentType;
		
     	RDescContentHandler(RSource RSrc)
    	{
    		this.RSrc=RSrc;
    	}
    	
		// we need it to find the dtd file!
    	public InputSource resolveEntity(String publicId, String systemId)
    	throws SAXException,IOException
    	{
    		InputSource iSource=super.resolveEntity(publicId,systemId);
            
            if(systemId.endsWith(RDefaults.RSDesc.DTD))
            {
                iSource=new InputSource(
                		PluginManager.getInstance().getFilemanager().getFile(RDefaults.PATH+RDefaults.RSDesc.DTD).getStream()
//                    RDefaults.THIS_CLASSLOADER.getResourceAsStream(
//                        RDefaults.PATH+RDefaults.RSDesc.DTD
//                )
                );
            }
    		
    		return iSource;
    	}
    	

		public void startElement(String namespaceURI, String localName,
								 String qName, Attributes atts)
		{
			String name=(namespaceURI.length()==0)?qName:localName;
			
     		if(name.equals(RSDesc.FUNCNAME_ELEM))
			{ 
				this.RSrc.setName(atts.getValue(RSDesc.FUNCATTRIB_ID));
				String desc=atts.getValue(RSDesc.FUNCATTRIB_DESC);
				
				if(desc==null || (desc!=null && desc.equals("")))
				{
					this.RSrc.setDescriptor(this.RSrc.getName());
				} else
				{
					this.RSrc.setDescriptor(desc);
				}
			} else if(name.equals(RSDesc.PARAM_ELEM))
			{
				String defaultValue=atts.getValue(RSDesc.PARAMATTRIB_DEFAULT);
				if(defaultValue!=null)
				{
					defaultValue=defaultValue.replaceAll("%dq%","\"");
				}
				String description=atts.getValue(RSDesc.PARAMATTRIB_DESC);
				if(description!=null)
				{
					description=description.replaceAll("%dq%","\"");
				}
				
                this.currentParam=new RSourceParam(
                   atts.getValue(RSDesc.PARAMATTRIB_NAME),
                   defaultValue,
                   description
                 );
			} else if(name.equals(RSDesc.QUICKINFO_ELEM)) 
			{
				this.isQInfo=true;
			}else if(name.equals(RSDesc.TYPE_ELEM))
            {
                this.typeEntries.clear();     
                this.currentType=ParameterType.createInstance(
                    atts.getValue(RSDesc.TYPEATTRIB_NAME)
                );
                String noedit=atts.getValue(RSDesc.TYPEATTRIB_NOEDIT);
                if(noedit!=null)
                {
                    try
                    {
                        this.currentType.setSuppressEditing(
                            Boolean.parseBoolean(noedit)
                        );                            
                    }catch(Exception ex)
                    {
                        ; //ignore errors
                    }
                }
            }else if(name.equals(RSDesc.ENTRY_ELEM))
            {
                this.typeEntries.add(
                    atts.getValue(RSDesc.ENTRYATTRIB_VALUE)
                );
            }else if(name.equals(RSDesc.MIO_ELEM))
            {
                MIOGroupRequirement mioGroup =
                    new MIOGroupRequirement(
                        atts.getValue(RSDesc.MIOATTRIB_id),
                        atts.getValue(RSDesc.MIOATTRIB_classname),
                        atts.getValue(RSDesc.MIOATTRIB_direction)
                    );
                
                this.RSrc.getRequiredMIOGroups().add(mioGroup);
            }
		} 			
				
		public void characters(char[] ch, int b, int e)
		{
			if(this.isQInfo)
			{
				this.qInfoBuffer.append(new String(ch,b,e));
			}
		}
		
		public void endElement(String namespaceURI, String localName,
							   String qName)
		{
			String name=(namespaceURI.length()==0)?qName:localName;
			if(name.equals(RSDesc.QUICKINFO_ELEM))
			{
				this.RSrc.setInfo(this.qInfoBuffer.toString());
				this.isQInfo=false; 
			}else if(name.equals(RSDesc.TYPE_ELEM))
            {
                if(!this.typeEntries.isEmpty())
                {
                    this.currentType.setValues(
                            (String[])this.typeEntries.toArray(new String[0])
                    );
                }
            }else if(name.equals(RSDesc.PARAM_ELEM))
            {
			    this.currentParam.setType(this.currentType);
                this.RSrc.addParameter(this.currentParam);
                this.currentType=null;
                this.currentParam=null;
            }
		}
    }
}