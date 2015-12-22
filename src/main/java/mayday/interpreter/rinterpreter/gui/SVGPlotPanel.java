/*
 * Created on 19.07.2004
 *
 */
package mayday.interpreter.rinterpreter.gui;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.swing.svg.JSVGComponent;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;


/**
 * @author Matthias
 *
 */
@SuppressWarnings("serial")
public class SVGPlotPanel extends JSVGComponent //JSVGCanvas//RPlotPanel
{
    private File imageFile;
    public SVGPlotPanel(File f) throws IOException
    {
        super();
        this.imageFile=f;
        
        String parser=XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory df=new SAXSVGDocumentFactory(parser);
        SVGDocument doc=(SVGDocument)df.createDocument(
            imageFile.toURI().toString()
        );
        this.setSVGDocument(doc);
        
        setSize(RPlotPanel.INITIAL_WIDTH,RPlotPanel.INITIAL_HEIGHT);
        setPreferredSize(new Dimension(RPlotPanel.INITIAL_WIDTH,RPlotPanel.INITIAL_HEIGHT)); 
    }
}
