package com.blocky.view;

import java.awt.*;

public class Particle {
    double x, y, vx, vy;
    Color color;
    float alpha = 1.0f;
    float size;

    public Particle(int x, int y, Color c) {
        this.x = x;
        this.y = y;
        this.color = c;
        this.size = (float) (Math.random() * 8 + 4);
        double angle = Math.random() * Math.PI * 2;
        double speed = Math.random() * 5 + 2;
        this.vx = Math.cos(angle) * speed;
        this.vy = Math.sin(angle) * speed;
    }

    public boolean update() {
        x += vx;
        y += vy;
        alpha -= 0.03f;
        size *= 0.96f;
        return alpha <= 0;
    }

    public void draw(Graphics2D g2) {
        if (alpha > 0) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setColor(color);
            g2.fillOval((int)x, (int)y, (int)size, (int)size);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }
}