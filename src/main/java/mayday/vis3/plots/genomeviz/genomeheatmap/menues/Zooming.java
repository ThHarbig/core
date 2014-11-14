//package mayday.vis3.plots.genomeviz.genomeheatmap.menues;
//
//import java.awt.Component;
//import java.awt.Window;
//
//import javax.swing.JPanel;
//
//import mayday.core.settings.SettingDialogMenuItem;
//import mayday.core.settings.generic.ComponentPlaceHolderSetting;
//import mayday.core.settings.generic.HierarchicalSetting;
//
//public class Zooming {
//
//	protected ZoomingSetting zoomingSetting;
//	protected MenuManager menuManager;
//	
//	public Zooming(MenuManager MenuManager){
//		menuManager = MenuManager;
//		zoomingSetting = new ZoomingSetting("Zooming",null, menuManager);
//	}
//	
//	public ZoomingSetting getSetting(){
//		
//		return zoomingSetting;
//	}
//	
//	public class ZoomingSetting extends HierarchicalSetting{
//		protected MenuManager target;
//		protected JPanel zoomPanel;
//		public ZoomingSetting(String Name, String Description, MenuManager Target) {
//			super(Name);
//
//			target=Target;
//			zoomPanel = target.getZoomViewMenu().getZoomPanel();
//			addSetting((new ComponentPlaceHolderSetting("Zoomingfield", zoomPanel)));
//			description = Description;
//		
//			setChildrenAsSubmenus(false);
//			
//			setLayoutStyle(LayoutStyle.PANEL_VERTICAL);
//		}
//		
//		public ZoomingSetting clone() {
//			ZoomingSetting zs = new ZoomingSetting("Zooming",null, menuManager);	
//	        return ( zs );
//		}
//		
//		public Component getMenuItem( Window parent ) {
//			return new SettingDialogMenuItem(this,parent);
//		}
//		
//	}
//}
