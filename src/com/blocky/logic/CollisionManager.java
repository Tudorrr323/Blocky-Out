package com.blocky.logic;

import com.blocky.interfaces.IMovementStrategy;
import com.blocky.model.Block;
import java.awt.Rectangle;
import java.util.List;

public class CollisionManager implements IMovementStrategy {

    @Override
    public boolean isValidMove(Block mover, int newX, int newY, List<Block> others) {
        // 1. Limite Ecran (Relaxate)
        if (newX < 0 || newY < 60) return false;
        if (newX + mover.getBounds().width > 1000) return false;
        if (newY + mover.getBounds().height > 1200) return false;

        // 2. HITBOX REDUS (Secretul pentru a intra usor in porti)
        // Verificăm coliziunea cu un dreptunghi cu 4 pixeli mai mic decat piesa.
        // Asta iti permite sa "stergi" peretii fara sa te blochezi instant.
        Rectangle looseBounds = new Rectangle(newX + 2, newY + 2, mover.getBounds().width - 4, mover.getBounds().height - 4);

        for (Block other : others) {
            Rectangle otherBounds = other.getBounds();

            // Dacă bounding box-urile nu se ating, ignorăm
            if (!looseBounds.intersects(otherBounds)) continue;

            // STRICT COLLISION FOR WALLS (Fix for "passing through walls")
            // If the obstacle is a wall (gray), we treat it as a solid block 
            // and deny movement immediately upon intersection, skipping complex pixel checks.
            if (other.getColor().equals(com.blocky.view.Theme.WALL_COLOR)) {
                return false;
            }

            // Dacă se ating, facem verificarea pixel-perfect, dar folosind coordonatele relaxate
            if (checkPixelPerfectCollision(mover, newX, newY, other)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkPixelPerfectCollision(Block mover, int nextX, int nextY, Block other) {
        int cellSize = Block.CELL_SIZE;
        // Toleranță la suprapunere (permitem 2px overlap)
        int tolerance = 2;

        for (int r1 = 0; r1 < mover.shapeMatrix.length; r1++) {
            for (int c1 = 0; c1 < mover.shapeMatrix[r1].length; c1++) {
                if (mover.shapeMatrix[r1][c1] == 0) continue;

                // Coordonatele celulei curente (cu hitbox redus vizual)
                int cellLeft = nextX + c1 * cellSize + tolerance;
                int cellTop = nextY + r1 * cellSize + tolerance;
                int cellRight = cellLeft + cellSize - 2*tolerance;
                int cellBottom = cellTop + cellSize - 2*tolerance;

                // Verificăm coliziunea cu bounding box-ul celulei celuilalt bloc
                // Aceasta este o verificare mai simplă și mai robustă decât calculul de indici matriceali direcți
                // pentru mișcări fine.

                // Iterăm prin celulele blocului obstacol
                for(int r2 = 0; r2 < other.shapeMatrix.length; r2++) {
                    for(int c2 = 0; c2 < other.shapeMatrix[r2].length; c2++) {
                        if(other.shapeMatrix[r2][c2] == 0) continue;

                        int otherLeft = other.getX() + c2 * cellSize;
                        int otherTop = other.getY() + r2 * cellSize;
                        int otherRight = otherLeft + cellSize;
                        int otherBottom = otherTop + cellSize;

                        // Intersectia a doua dreptunghiuri
                        if (cellLeft < otherRight && cellRight > otherLeft &&
                                cellTop < otherBottom && cellBottom > otherTop) {
                            return true; // Coliziune!
                        }
                    }
                }
            }
        }
        return false;
    }
}