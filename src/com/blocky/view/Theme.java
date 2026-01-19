package com.blocky.view;

import java.awt.Color;

public class Theme {
    public static final Color BG_COLOR = new Color(25, 25, 30); // Background închis

    // Culori Piese
    public static final Color RED_BLOCK = new Color(231, 76, 60);
    public static final Color BLUE_BLOCK = new Color(52, 152, 219);
    public static final Color GREEN_BLOCK = new Color(46, 204, 113);
    public static final Color ORANGE_BLOCK = new Color(230, 126, 34);
    public static final Color YELLOW_BLOCK = new Color(241, 196, 15);
    public static final Color CYAN_BLOCK = new Color(52, 231, 228);
    public static final Color PURPLE_BLOCK = new Color(162, 155, 254);
    public static final Color PINK_BLOCK = new Color(253, 121, 168);

    // Culoare BARIERĂ (Zid) - Gri închis metalic
    public static final Color WALL_COLOR = new Color(80, 80, 90);

    public static Color getSideColor(Color c) {
        return c.darker().darker();
    }

    public static Color getHighlight(Color c) {
        return new Color(
                Math.min(255, c.getRed() + 40),
                Math.min(255, c.getGreen() + 40),
                Math.min(255, c.getBlue() + 40)
        );
    }
}