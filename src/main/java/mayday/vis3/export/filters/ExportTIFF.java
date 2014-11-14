package mayday.vis3.export.filters;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.export.ExportPlugin;

import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.TIFFTranscoder;

public class ExportTIFF extends RasterExport {

	@Override
	protected ImageTranscoder getImageTranscoder() {
		return new TIFFTranscoder();	
	}

	
	
	@Override
	public String getFormatName() {
		return "TIFF";
	}

	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.vis3.export.tiff",
				new String[]{},
				ExportPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"TIFF Graphics Export Filter",
				"TIFF"
		);
		pli.loadDefaultSettings(rasterSettings);
		return pli;	
	}

}
