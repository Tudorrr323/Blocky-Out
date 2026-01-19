package com.blocky.model;

import java.awt.Color;

public class ExitGate extends Entity {
    public int side; // 0=Top, 1=Right, 2=Bottom, 3=Left

    public ExitGate(int x, int y, int w, int h, Color color, int side) {
        super(x, y, w, h, color);
        this.side = side;
    }
}