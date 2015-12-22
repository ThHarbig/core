package mayday.vis3.plots.genomeviz;

public class EnumManagerGO {

		public enum MouseClickNumber{NULL,ONE,TWO};
		
		public enum ActionModes{NOTHING,MOVE_UP,MOVE_DOWN,MOVE_TO_TOP,MOVE_TO_BOTTOM,DELETE};

		public enum SizeMode{SIZE_LAYER, SIZE_ZOOM};
		
//		public enum KindOfTrack{SCALE_TRACK, STEM_TRACK, HM_TRACK, DHM_TRACK, P_TRACK};
		
		public enum KindOfColor{COLOR_PBLIST, COLOR_MIO, COLOR_PB};
		
		public enum Zoom{ZOOM_IN,ZOOM_OUT,ZOOM_NOT,ZOOM,ZOOM_ADAPTED};
		
		public enum Up_Down{UP_RANGE,DOWN_RANGE};
		
		public enum TrackPanelMode{TP_DEFAULT, TP_SELECTION};
		
		public enum ProbeSelection{SINGLE_SEL, CTRL_SEL, SHIFT_SEL};
		
		public enum Fixed{LEFT_FIXED_RIGHT_SMALLER, LEFT_FIXED_RIGHT_BIGGER, RIGHT_FIXED_LEFT_SMALLER, RIGHT_FIXED_LEFT_BIGGER};
		
		public enum Dragged{GOTO_RIGHT,GOTO_LEFT};
		
		public enum TrackComponentType{USER_COMP, PAINTING_COMP, TRACK_COMP}
		
}
