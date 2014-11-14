package mayday.vis3.export;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import mayday.core.gui.MaydayDialog;
import mayday.core.pluma.PluginInfo;
import mayday.core.settings.Settings;
import mayday.core.settings.SettingsDialog;
import mayday.core.settings.generic.ComponentPlaceHolderSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.PluginTypeSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.vis3.export.filters.ExportPNG;
import mayday.vis3.export.filters.Preview;

public class ExportSetting extends Settings {

	private LinkedDimensionSetting lds;

	private PluginTypeSetting<ExportPlugin> method;
	private String targetFile;
	private Dimension baseDimension;	
	private BooleanSetting scaleContent;

	
	private Component pc;
	private SettingsDialog preview_settings_dialog;
 

	
	public ExportSetting(boolean allowChangeScaling) {
		super( new HierarchicalSetting("Graphics Export Settings"), null);
		
		scaleContent = new BooleanSetting("Scale content","If selected, the image will be scaled to the new dimensions.\n" +
				"Otherwise, the canvas will be scaled while the font sizes etc. are unchanged.",true);
		
		HierarchicalSetting h = new HierarchicalSetting("Dimensions");
		
		h.addSetting(lds = new LinkedDimensionSetting());
		if (allowChangeScaling)
			h.addSetting( scaleContent );		
		h.addSetting(new ComponentPlaceHolderSetting("preview", new JButton(new PreviewAction())));

		root.addSetting(h);
		root.addSetting(method = new PluginTypeSetting<ExportPlugin>("Format",null, new ExportPNG(), ExportPlugin.MC));
		
		connectToPrefTree(PluginInfo.getPreferences(ExportPlugin.MC));
	}
	
	public Dimension getDimension() {
		return lds.getTargetDimension();		
	}
	
	public Dimension getBaseDimension() {
		return baseDimension;
	}
	
	public void setInitialDimension(Dimension d) {
		baseDimension = d;
		lds.setTargetDimension(d);
	}
	
	public void setTargetDimension(Dimension d) {
		lds.setTargetDimension(d);
	}
	
	public String getTargetFilename() {
		return targetFile;
	}
	
	public void setTargetFileName(String tfn) {
		targetFile = tfn;
	}
	
	public boolean scaleContent() {
		return scaleContent.getBooleanValue();
	}
	
	public void setScaleContent(boolean sc) {
		scaleContent.setBooleanValue(sc);
	}
	
	public ExportPlugin getMethod() {
		return method.getInstance();
	}

	public void setPreviewActionObjects(Component plotComponent, SettingsDialog dialog) {
		pc = plotComponent;
		preview_settings_dialog = dialog;
	}

	
	@SuppressWarnings("serial")
	protected class PreviewAction extends AbstractAction {
		public PreviewAction() {
			super("Preview with these settings");
		}
		public void actionPerformed(ActionEvent e) {
			preview_settings_dialog.apply();
			
			MaydayDialog previewWindow = new MaydayDialog(preview_settings_dialog);
			previewWindow.setTitle("Export preview");
			previewWindow.setModal(true);
			JScrollPane jsp = new JScrollPane();
			previewWindow.add(jsp);
			Preview p = new Preview();
			p.getSetting().fromPrefNode(preview_settings_dialog.getSettings().getRoot().toPrefNode());
			try {
				p.exportComponent(pc, (ExportSetting)preview_settings_dialog.getSettings());
				JLabel jl  = new JLabel(new ImageIcon(p.getImage()));
				jsp.setViewportView(jl);
				previewWindow.pack();
				previewWindow.setMinimumSize(new Dimension(200,200));
				previewWindow.setPreferredSize(new Dimension(800,600));
				previewWindow.setVisible(true);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}		
	}
	
	
}
