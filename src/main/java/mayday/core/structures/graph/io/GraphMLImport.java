package mayday.core.structures.graph.io;

import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;

import mayday.core.Probe;
import mayday.core.structures.graph.Graph;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class GraphMLImport 
{
	public static List<Graph> importGraphML(File file, Collection<Probe> probes) throws Exception
	{
		XMLReader parser;
		parser = XMLReaderFactory.createXMLReader();
		parser.setEntityResolver(new EntityResolver() {
			public InputSource resolveEntity(String publicID, String systemID)
			throws SAXException {
				return new InputSource(new StringReader(""));
			}
		}
		);
		BasicGraphMLHandler handler=new BasicGraphMLHandler();
		parser.setContentHandler(handler);
		parser.parse(new InputSource(new FileReader(file)));
		return handler.getGraphs(probes);
	}
}
