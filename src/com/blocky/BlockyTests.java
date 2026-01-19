package com.blocky;

import com.blocky.logic.CollisionManager;
import com.blocky.model.Block;
import com.blocky.view.Theme;
import org.junit.Assert;
import org.junit.Test;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Plan de testare:
 * 1. Testare Model (Block): Verificarea calcularii dimensiunilor si a logicii de selectie (hitbox complex).
 * 2. Testare Logica (CollisionManager): Verificarea detectiei coliziunilor cu pereti si alte blocuri.
 * 3. Testare State: Verificarea actualizarii coordonatelor la mutare.
 */
public class BlockyTests {

    @Test
    public void testBlockDimensions() {
        // Justificare: Verificam daca matricea formei este corect tradusa in dimensiuni pixel (latime/inaltime).
        // Matrice 2x2. CELL_SIZE este 45. Deci dimensiunea ar trebui sa fie 90x90.
        int[][] matrix = {{1, 1}, {1, 1}};
        Block block = new Block(0, 0, matrix, Color.RED);
        
        Assert.assertEquals("Latimea blocului ar trebui sa fie 90", 90, block.getWidth());
        Assert.assertEquals("Inaltimea blocului ar trebui sa fie 90", 90, block.getHeight());
    }

    @Test
    public void testBlockMovement() {
        // Justificare: Verificam daca metoda move() actualizeaza corect coordonatele interne.
        int[][] matrix = {{1}};
        Block block = new Block(100, 100, matrix, Color.RED);
        block.move(150, 200);
        
        Assert.assertEquals("Coordonata X ar trebui sa fie 150", 150, block.getX());
        Assert.assertEquals("Coordonata Y ar trebui sa fie 200", 200, block.getY());
    }

    @Test
    public void testCollisionWithWall() {
        // Justificare: Este critic ca blocurile sa nu treaca prin pereti (Theme.WALL_COLOR).
        CollisionManager cm = new CollisionManager();
        
        // Mover: Bloc 1x1 la 100,100
        int[][] shape = {{1}};
        Block mover = new Block(100, 100, shape, Color.RED);
        
        // Wall: Bloc 1x1 la 145,100 (chiar langa). Latime mover = 45.
        // Daca mutam la 110, ar trebui sa se suprapuna cu peretele de la 145.
        Block wall = new Block(145, 100, 45, 45, shape, Theme.WALL_COLOR); 
        
        List<Block> others = new ArrayList<>();
        others.add(wall);
        
        // Incercam sa mutam peste perete
        boolean allowed = cm.isValidMove(mover, 110, 100, others);
        Assert.assertFalse("Coliziunea cu peretele ar trebui sa blocheze miscarea", allowed);
        
        // Incercam sa mutam in directia opusa (liber)
        boolean allowedFree = cm.isValidMove(mover, 50, 100, others);
        Assert.assertTrue("Miscarea in spatiu liber ar trebui permisa", allowedFree);
    }
    
    @Test
    public void testPixelPerfectSelection() {
         // Justificare: Jocul foloseste forme neregulate. Click-ul pe spatiul gol al unui "L" nu trebuie sa selecteze piesa.
         // Forma L:
         // 1 0
         // 1 1
         int[][] lShape = {{1, 0}, {1, 1}};
         Block block = new Block(0, 0, lShape, Color.BLUE);
         
         // Click pe (10, 10) -> Rand 0, Col 0 -> 1 -> True
         Assert.assertTrue("Click pe zona plina ar trebui sa selecteze", block.containsPoint(10, 10));
         
         // Click pe (50, 10) -> Rand 0, Col 1 -> 0 -> False (Spatiu gol in matricea L)
         // Col 1 incepe la pixelul 45.
         Assert.assertFalse("Click pe gaura din forma L NU ar trebui sa selecteze", block.containsPoint(50, 10));
         
         // Click pe (50, 50) -> Rand 1, Col 1 -> 1 -> True
         Assert.assertTrue("Click pe zona plina (coltul L) ar trebui sa selecteze", block.containsPoint(50, 50));
    }
}
