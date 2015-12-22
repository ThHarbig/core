package wsi.ra.plotting;


import java.awt.Color;
import java.awt.Graphics2D;

import wsi.ra.chart2d.DBorder;
import wsi.ra.chart2d.DPointIcon;

/**
 * Created by IntelliJ IDEA.
 * User: streiche
 * Date: 01.04.2004
 * Time: 09:55:30
 */
public class Chart2DDPointIconCircle implements DPointIcon {

    private Color   m_Border    = Color.BLACK;
    private Color   m_Fill      = null;
    private int     m_Size      = 4;

    /**
     * this method has to be overridden to paint the icon. The point itself lies
     * at coordinates (0, 0)
     */
    public void paint( Graphics2D g ){
        Color prev = g.getColor();
        if (m_Fill != null) {
            g.setColor(m_Fill);
            g.fillOval(-this.m_Size, -this.m_Size, 2*this.m_Size +1, 2*this.m_Size +1);
        }
        if (this.m_Border != null) g.setColor(m_Border);
        g.drawOval(-this.m_Size, -this.m_Size, 2*this.m_Size, 2*this.m_Size);
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

    public void setBorderColor(Color c) {
        this.m_Border = c;
    }
    public void setFillColor(Color c) {
        this.m_Fill = c;
    }
    public void setSize(int d) {
        this.m_Size = d;
    }

	public boolean wantDeviceCoordinates() {
		return true;
	}

    
}
