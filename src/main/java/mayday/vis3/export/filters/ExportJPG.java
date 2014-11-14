package mayday.vis3.export.filters;

import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.export.ExportPlugin;

import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;

public class ExportJPG extends RasterExport {

	@Override
	protected ImageTranscoder getImageTranscoder() {
		JPEGTranscoder l_transcoder = new JPEGTranscoder();
		l_transcoder.addTranscodingHint( JPEGTranscoder.KEY_QUALITY, new Float( MaydayDefaults.DEFAULT_JPEG_QUALITY ) );
		return l_transcoder;
	}

	@Override
	public String getFormatName() {
		return "JPG";
	}

	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.vis3.export.jpg",
				new String[]{},
				ExportPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"JPEG Graphics Export Filter",
				"JPEG"
		);
		pli.loadDefaultSettings(rasterSettings);
		return pli;	
	}

}
