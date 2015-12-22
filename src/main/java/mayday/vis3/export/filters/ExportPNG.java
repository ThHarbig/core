package mayday.vis3.export.filters;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.export.ExportPlugin;

import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;

public class ExportPNG extends RasterExport {

	@Override
	protected ImageTranscoder getImageTranscoder() {
		return new PNGTranscoder();	
	}

	
	
	@Override
	public String getFormatName() {
		return "PNG";
	}

	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.vis3.export.png",
				new String[]{},
				ExportPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"PNG Graphics Export Filter",
				"PNG"
		);
		pli.loadDefaultSettings(rasterSettings);
		return pli;	
	}

}
