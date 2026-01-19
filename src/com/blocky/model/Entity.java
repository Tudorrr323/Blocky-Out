package com.blocky.model;

import java.awt.*;

public abstract class Entity {
    protected int x, y, width, height;
    protected Color color;

    public Entity(int x, int y, int w, int h, Color color) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.color = color;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }

    // --- Metode pentru Coordonate ---
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    // --- NEW: Metode Publice pentru Dimensiuni (FIX EROARE) ---
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }

    public void setBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}