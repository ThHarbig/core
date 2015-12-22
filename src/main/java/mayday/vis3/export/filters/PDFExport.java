package mayday.vis3.export.filters;

import java.awt.Component;
import java.awt.Graphics2D;
import java.io.FileOutputStream;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.vis3.export.ExportPlugin;
import mayday.vis3.export.ExportSetting;

import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.DefaultFontMapper;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFExport  extends ExportPlugin 
{
	private BooleanHierarchicalSetting metaDataSetting;
	private StringSetting author;
	private StringSetting title;
	private StringSetting subject;
	private StringSetting keywords;
	
	public PDFExport() 
	{
		metaDataSetting=new BooleanHierarchicalSetting("PDF Meta Data",null,false);
		author=new StringSetting("Author", null, "");
		title=new StringSetting("Title", null, "");
		subject=new StringSetting("Subject", null, "");
		keywords=new StringSetting("Key Words", null, "");
		metaDataSetting.addSetting(title).addSetting(author).addSetting(subject).addSetting(keywords);
	}
	
	@Override
	public void exportComponent(Component plotComponent, ExportSetting settings)
	throws Exception 
	{
		// points = 1/72 inches. 
		// first two parameters are lower left, then upper right)
		Rectangle docBounds=new Rectangle(0, 0, settings.getDimension().width, settings.getDimension().height);
		Document document = new Document(docBounds);
				//PageSize.A4);

		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(settings.getTargetFilename()));


		document.addCreationDate();
		document.addCreator("Created by "+MaydayDefaults.PROGRAM_FULL_NAME);
		if(metaDataSetting.getBooleanValue())
		{
			document.addAuthor(author.getStringValue());
			document.addTitle(title.getStringValue());
			document.addSubject(subject.getStringValue()); 
			document.addKeywords(keywords.getStringValue()); 
		}
		document.open();

		// no idea what this does. 
		PdfContentByte cb = writer.getDirectContent();
		PdfTemplate tp = cb.createTemplate((float)settings.getDimension().getWidth(),(float)settings.getDimension().getHeight());
		Graphics2D g2 = tp.createGraphics((float)settings.getDimension().getWidth(),(float)settings.getDimension().getHeight(), new DefaultFontMapper());

		// Create your graphics here - draw on the g2 Graphics object
		exportComponentToCanvas(plotComponent, g2, settings);
		
		g2.dispose();
		cb.addTemplate(tp, 0,0); // 0, 100 = x,y positioning of graphics in PDF page
		document.close();

		
	}

	@Override
	public String getFormatName() 
	{
		return "pdf";
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.vis3.export.pdf",
				new String[]{"LIB.iText"},
				ExportPlugin.MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"PDF export",
				"PDF"
		);
		return pli;	
	}
	
	@Override
	public Setting getSetting() 
	{
		return metaDataSetting;
	}

}

