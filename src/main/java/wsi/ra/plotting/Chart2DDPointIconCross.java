package wsi.ra.plotting;


import java.awt.Color;
import java.awt.Graphics2D;

import wsi.ra.chart2d.DBorder;
import wsi.ra.chart2d.DPointIcon;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 01.04.2004
 * Time: 10:00:50
 */
public class Chart2DDPointIconCross implements DPointIcon {

    private Color       m_Color;

    /**
     * this method has to be overridden to paint the icon. The point itself lies
     * at coordinates (0, 0)
     */
    public void paint( Graphics2D g ){
        Color prev = g.getColor();
        g.setColor(this.m_Color);
        g.drawLine(-1, 1, 1,-1);
        g.drawLine(-1,-1, 1, 1);
        g.setColor(prev);
    }

    /**
     * the border which is necessary to be paint around the DPoint that the whole
     * icon is visible
     *
     * @return the border
     */
    public DBorder getDBorder() {
        return new DBorder(4, 4, 4, 4);
    }

    public void setColor(Color c) {
        this.m_Color = c;
    }
    
	public boolean wantDeviceCoordinates() {
		return true;
	}

}
