package com.blocky.model;

import java.awt.Color;

public class Block extends Entity {
    public boolean isSelected = false;
    public boolean isExiting = false;
    public float scale = 1.0f;
    public ExitGate targetGate = null;

    public int[][] shapeMatrix;

    // --- UPDATE: MĂRIME GIGANTĂ ---
    // Era 40, acum este 60. Piesele sunt cu 50% mai mari vizual.
    // UPDATE 3: CELL_SIZE = 45 (Tile 90px / 2).
    public static final int CELL_SIZE = 45;
    
    public enum Axis { NONE, HORIZONTAL, VERTICAL }
    public Axis restriction = Axis.NONE;

    public Block(int x, int y, int[][] matrix, Color color) {
        super(x, y, 0, 0, color);
        this.shapeMatrix = matrix;
        this.restriction = Axis.NONE;
        recalculateBounds();
    }
    
    public Block(int x, int y, int[][] matrix, Color color, Axis restriction) {
        super(x, y, 0, 0, color);
        this.shapeMatrix = matrix;
        this.restriction = restriction;
        recalculateBounds();
    }

    // New Constructor for Custom Sized Blocks (Walls)
    public Block(int x, int y, int w, int h, int[][] matrix, Color color) {
        super(x, y, w, h, color);
        this.shapeMatrix = matrix;
        this.restriction = Axis.NONE;
        // Do not recalculate bounds, respect passed w/h
    }

    public Block(int x, int y, int w, int h, int[][] matrix, Color color, Axis restriction) {
        super(x, y, w, h, color);
        this.shapeMatrix = matrix;
        this.restriction = restriction;
        // Do not recalculate bounds, respect passed w/h
    }

    public void recalculateBounds() {
        if (shapeMatrix == null || shapeMatrix.length == 0) return;
        int rows = shapeMatrix.length;
        int maxCols = 0;
        for (int[] row : shapeMatrix) {
            if (row != null && row.length > maxCols) maxCols = row.length;
        }
        this.width = maxCols * CELL_SIZE;
        this.height = rows * CELL_SIZE;
    }

    public boolean containsPoint(int mx, int my) {
        if (!getBounds().contains(mx, my)) return false;
        int relativeX = mx - this.x;
        int relativeY = my - this.y;
        int col = relativeX / CELL_SIZE;
        int row = relativeY / CELL_SIZE;

        if (row < 0 || row >= shapeMatrix.length) return false;
        if (col < 0 || col >= shapeMatrix[row].length) return false;

        return shapeMatrix[row][col] == 1;
    }

    public void move(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }
}