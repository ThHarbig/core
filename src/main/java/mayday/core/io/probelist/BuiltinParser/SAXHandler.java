package mayday.core.io.probelist.BuiltinParser;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.meta.types.AnnotationMIO;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXHandler
extends DefaultHandler
{
	public static final String XML_PROBELIST_TAG = "probelist";
	public static final String XML_ANNOTATION_TAG = "annotation";
	public static final String XML_NAME_TAG = "name";
	public static final String XML_INFO_TAG = "info";
	public static final String XML_QUICKINFO_TAG = "quickinfo";
	public static final String XML_LAYOUT_TAG = "layout";
	public static final String XML_COLOR_TAG = "color";
	public static final String XML_PROBE_TAG = "probe";


	boolean probeActive;
	boolean quickInfoActive;
	boolean infoActive;
	boolean nameActive;
	boolean colorActive;
	String probeName;
	ProbeList probeList;

	private StringBuffer characterBuf;


	public SAXHandler(ProbeList pl) 
	{
		super();
		probeList = pl;
		probeName = new String();
	}


	public InputSource resolveEntity( String publicId, String systemId )
	throws SAXException, IOException
	{
		InputSource l_inputSource = super.resolveEntity( publicId, systemId );

		//          // if the parser is looking for the probe list DTD, we give him the absolute path
		//          if ( systemId.subSequence( systemId.length() - MaydayDefaults.DTD_FILE.length(),
		//          systemId.length() ).toString().equals( MaydayDefaults.DTD_FILE ) )
		//          {        
		//          String l_dtd = new String();
		//          
		//          try
		//          {
		//          //MZ: 25.02.2004
		//          // to find the dtd file
		//          //old: l_dtd = "file:///" + (new File( MaydayDefaults.DTD_FILE ).toURL()).getPath();
		//          //old2: l_dtd="file:///"+MaydayDefaults.DTD_FULLPATH;
		//          
		//          //MZ: 26.02.2005
		//          l_dtd="file:///"+MaydayDefaults.DTD_FILE;
		//          }
		//          catch( Exception exception )
		//          {
		//          // ignore and reset
		//          l_dtd = "";
		//          }
		//          
		//          l_inputSource = new InputSource( l_dtd );
		//          }

		//the following should work also from a jar
		if(systemId.endsWith(MaydayDefaults.PROBELIST_DTD))
		{
			l_inputSource = new InputSource(
					ClassLoader.getSystemResourceAsStream(MaydayDefaults.PROBELIST_DTD_FILE)
			);
		}

		return (l_inputSource );
	}


	public void error( SAXParseException exception )
	throws SAXParseException
	{
		throw ( exception );
	}


	public void startDocument()
	{
		// nothing here yet
	}


	public void endDocument()
	{
		// nothing here yet
	}


	public void startElement( String uri, String name,
			String qName, Attributes attributes )
	{
		String l_name;

		if ( uri.length() == 0 )
		{
			l_name = qName;
		}
		else
		{
			l_name = name;
		}

		this.characterBuf=new StringBuffer();

		if ( l_name.equals(XML_NAME_TAG) )
		{
			nameActive = true;
		}
		else if ( l_name.equals(XML_QUICKINFO_TAG) )
		{
			quickInfoActive = true;
		}
		else if ( l_name.equals(XML_INFO_TAG) )
		{
			infoActive = true;
		}
		else if ( l_name.equals(XML_COLOR_TAG) )
		{
			colorActive = true;        
		}
		else if ( l_name.equals(XML_PROBE_TAG) )
		{
			probeActive = true;        
		}
	}


	public void endElement( String uri, String name, String qName )
	{
		String l_name;

		if ( uri.length() == 0 )
		{
			l_name = qName;
		}
		else
		{
			l_name = name;
		}

		if ( l_name.equals(XML_NAME_TAG) )
		{
			if(nameActive) probeList.setName(unxmlize( characterBuf.toString().trim()));
			nameActive = false;
		}
		else if ( l_name.equals(XML_QUICKINFO_TAG) )
		{
			if(quickInfoActive) {
				if (probeList.getAnnotation()==null)
					probeList.setAnnotation(new AnnotationMIO());
				probeList.getAnnotation().setQuickInfo(unxmlize( characterBuf.toString().trim()));
			}
			quickInfoActive = false;
		}
		else if ( l_name.equals(XML_INFO_TAG) )
		{
			if(infoActive) {
				if (probeList.getAnnotation()==null)
					probeList.setAnnotation(new AnnotationMIO());
				probeList.getAnnotation().setInfo(characterBuf.toString().trim());
			}

			infoActive = false;
		}
		else if ( l_name.equals(XML_COLOR_TAG) )
		{
			if(colorActive) probeList.setColor(Color.decode( characterBuf.toString().trim() ));
			colorActive = false;
		}
		else if ( l_name.equals(XML_PROBE_TAG) )
		{
			probeName=characterBuf.toString().trim();
			Probe l_probe = probeList.getDataSet().getMasterTable().getProbe( unxmlize( probeName ) );


			/*
                 if ( l_probe == null )
                 {
                 throw ( new RuntimeException( "Unable to load this probe list. No probe with identifier \"" + 
                 probeName +
                 "\" found in master table." ) );
                 }
			 */

			 if ( l_probe != null )
			 {
				 probeList.addProbe( l_probe );
			 }

			 probeActive = false;
		}
	}


	public void characters ( char characters[], int start, int length )
	throws RuntimeException
	{
		characterBuf.append(characters,start,length);
	}    
	
    
    // NG: added 2005-11-30
    /**
     * Replaces XML reserved characters by their respective character entities.
     * 
     * @param string String that contains reserved characters.
     * @return The input string with reserved characters encoded by their respective character entities.
     */
    protected String xmlize( String string )
    {
      string = string.replaceAll( "&", "&amp;" );
      string = string.replaceAll( "<", "&lt;" );
      string = string.replaceAll( ">", "&gt;" );
                
      return string;
    }

    
    // NG: added 2005-11-30
    /**
     * Replaces XML character entities with the respective reserved characters.
     * 
     * @param string String that contains XML character entities.
     * @return The input string with character entities replaced by the respective reserved characters.
     */
    protected String unxmlize( String string )
    {
      string = string.replaceAll( "&amp;", "&" );
      string = string.replaceAll( "&lt;", "<" );
      string = string.replaceAll( "&gt;", ">" );
                
      return string;
    }

    
    public int write( String fileName )
    throws FileNotFoundException, IOException
    {
        FileWriter l_fileWriter;
        
        l_fileWriter = new FileWriter( fileName );
        
        // write XML file
        
        l_fileWriter.write( "<?xml version=\"1.0\"?>\n" );
        l_fileWriter.write( "\n" );
        l_fileWriter.write( "<!-- Generated by " + MaydayDefaults.PROGRAM_FULL_NAME + " -->\n" );
        l_fileWriter.write( "\n" );    
        l_fileWriter.write( "<!DOCTYPE probelist SYSTEM \"" + MaydayDefaults.PROBELIST_DTD + "\">\n" );        
        l_fileWriter.write( "\n" );
        
        l_fileWriter.write( "<" + XML_PROBELIST_TAG + ">\n" );
        l_fileWriter.write( "\n" );
        
        l_fileWriter.write( " <" + XML_ANNOTATION_TAG + ">\n" );
        
        l_fileWriter.write( "  <" + XML_NAME_TAG + ">\n" );
        l_fileWriter.write( "   " + xmlize( probeList.getName() ) + "\n" );
        l_fileWriter.write( "  </" + XML_NAME_TAG + ">\n" );
        
        l_fileWriter.write( "  <" + XML_QUICKINFO_TAG + ">\n" );
        l_fileWriter.write( "   " + xmlize( probeList.getAnnotation().getQuickInfo() ) + "\n" );
        l_fileWriter.write( "  </" + XML_QUICKINFO_TAG + ">\n" );
        
        l_fileWriter.write( "  <" + XML_INFO_TAG + ">\n" );
        l_fileWriter.write( "   <![CDATA[" + probeList.getAnnotation().getInfo() + "]]>\n" );
        l_fileWriter.write( "  </" + XML_INFO_TAG + ">\n" );
        
        l_fileWriter.write( " </" + XML_ANNOTATION_TAG + ">\n" );
        
        l_fileWriter.write( "\n" );
        
        l_fileWriter.write( " <" + XML_LAYOUT_TAG + ">\n" );
        
        // write color
        l_fileWriter.write( "  <" + XML_COLOR_TAG + ">\n" );
        l_fileWriter.write( "   " + Integer.toString( probeList.getColor().getRed() * 256 * 256 +
        		probeList.getColor().getGreen() * 256 +
        		probeList.getColor().getBlue() ) +
        "\n" );
        l_fileWriter.write( "  </" + XML_COLOR_TAG + ">\n" );
        
        l_fileWriter.write( " </" + XML_LAYOUT_TAG + ">\n" );
        
        l_fileWriter.write( "\n" );
        
        int l_numberOfProbes = probeList.getNumberOfProbes();
        
        for ( int i = 0; i < l_numberOfProbes; ++i )
        {
            l_fileWriter.write( " <" + XML_PROBE_TAG + ">\n" );
            l_fileWriter.write( "  " + xmlize( (probeList.getProbe( i )).getName() ) + "\n" );
            l_fileWriter.write( " </" + XML_PROBE_TAG + ">\n" );			
            
            if ( i < l_numberOfProbes - 1 )
            {
                l_fileWriter.write( "\n" );
            }
        }
        
        l_fileWriter.write( "\n" );
        l_fileWriter.write( "</" + XML_PROBELIST_TAG + ">\n" );
        
        l_fileWriter.close(); 
        
        return ( probeList.getNumberOfProbes());
    }  
    
    public int read( String fileName )
    throws SAXException,
    SAXParseException,
    FileNotFoundException,
    IOException,
    ParserConfigurationException,
    RuntimeException
    {
        @SuppressWarnings("unused")
		FileReader l_fileReader;
        
        l_fileReader = new FileReader( fileName );
        
        // clear the list
        probeList.clearProbes();
        
        // clear the annotation
        probeList.clearAnnotation();     
        
        //  get the default (non-validating) parser
        SAXParserFactory l_factory = SAXParserFactory.newInstance();
        
        // make the parser validating
        l_factory.setValidating( true );
        
        // parse the input and add probes to the list
        SAXParser l_parser = l_factory.newSAXParser();
        
        l_parser.parse( new File( fileName ), this );
        
        probeList.fireProbeListChanged( ProbeListEvent.OVERALL_CHANGE );
        
        return (probeList.getNumberOfProbes());
    }
    
}
