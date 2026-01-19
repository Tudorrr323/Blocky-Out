package com.blocky.logic;

import com.blocky.model.Block;
import com.blocky.model.ExitGate;
import com.blocky.view.Theme;
import java.util.List;
import java.awt.Color;

public class LevelFactory {

    private static final int SCREEN_W = 800;
    private static final int HUD_H = 60;
    private static final int WALL = 40;
    
    // --- 8x8 GRID CONFIGURATION ---
    private static final int TILE = 90; 
    private static final int CELL = 45; 
    private static final int GRID_COLS = 8;
    private static final int GRID_SIZE = GRID_COLS * TILE; // 720px
    
    private static final int ARENA_W = GRID_SIZE + 2 * WALL; // 800px
    private static final int START_X = 0; 
    private static final int START_Y = 60; // Top Margin (Immediately after HUD)
    
    private static final int LEFT_WALL_X = START_X;
    private static final int RIGHT_WALL_X = START_X + WALL + GRID_SIZE; 
    private static final int TOP_WALL_Y = START_Y;
    private static final int BOT_WALL_Y = START_Y + WALL + GRID_SIZE; 
    
    private static final int GRID_X = START_X + WALL; 
    private static final int GRID_Y = START_Y + WALL; 

    // --- TILE SHAPES ---
    public static final int[][] S_1x1 = {{1}};
    public static final int[][] S_2x2 = {{1, 1}, {1, 1}};
    public static final int[][] S_3x3 = {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}};
    
    public static final int[][] S_1x2 = {{1, 1}}; 
    public static final int[][] S_1x3 = {{1, 1, 1}}; 
    public static final int[][] S_1x4 = {{1, 1, 1, 1}}; 
    
    public static final int[][] S_2x1 = {{1}, {1}}; 
    public static final int[][] S_3x1 = {{1}, {1}, {1}}; 
    public static final int[][] S_4x1 = {{1}, {1}, {1}, {1}};
    
    public static final int[][] L_TL = {{1, 1}, {1, 0}}; 
    public static final int[][] L_TR = {{1, 1}, {0, 1}}; 
    public static final int[][] L_BL = {{1, 0}, {1, 1}}; 
    public static final int[][] L_BR = {{0, 1}, {1, 1}}; 

    public static final int[][] L_BR3x3 = {{0, 1}, {0, 1}, {1, 1}}; 
    public static final int[][] L_TL3x3 = {{1, 1}, {1, 0}, {1, 0}}; 
    public static final int[][] L_TR3x3 = {{1, 1}, {0, 1}, {0, 1}}; 
    public static final int[][] L_BL3x3 = {{1, 0}, {1, 0}, {1, 1}}; 
    
    public static final int[][] CROSS = {{0, 1, 0}, {1, 1, 1}, {0, 1, 0}};
    public static final int[][] U_UP = {{1, 0, 1}, {1, 1, 1}};
    public static final int[][] U_DOWN = {{1, 1, 1}, {1, 0, 1}};

    public static void loadLevel(int level, List<Block> blocks, List<ExitGate> gates) {
        blocks.clear();
        gates.clear();

        switch (level) {
            case 1: { // Blue/Yellow 2x2 - Vertical Corridor
                // Active Cols: 3, 4. (Indices 3,4).
                // Grid X starts at 40.
                // Col 3 X = 40 + 270 = 310.
                // Col 5 X = 40 + 450 = 490.
                // Inner Arena X: 310 to 490.
                // Left Wall X: 310 - 40 = 270.
                // Right Wall X: 490.
                // Height: Full (0-8) or shrink? Blocks are Row 1 to Row 5+2=7?
                // Blocks at Row 1 (y=100+90=190) and Row 5 (y=100+450=550).
                // Let's keep full height to be safe, or 100-820.
                
                int l1_LeftX = 270;
                int l1_RightX = 490;
                int l1_TopY = 60; // Standard Top
                int l1_BotY = 60 + 720; // Standard Bot
                
                blocks.clear(); gates.clear();
                
                // Left Wall (Blue Gate)
                // Gate needs to cover the Blue Block at Row 1 (y=190, h=180).
                // Gate Y = 190. H = 180.
                gates.add(new ExitGate(l1_LeftX, 190, 40, 180, Theme.BLUE_BLOCK, 3));
                blocks.add(new Block(l1_LeftX, l1_TopY, 40, 190 - l1_TopY, createRect(40, 130, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l1_LeftX, 370, 40, l1_BotY - 370 + 40, createRect(40, l1_BotY - 370 + 40, CELL), Theme.WALL_COLOR));
                
                // Right Wall (Yellow Gate)
                // Gate covers Yellow Block at Row 5 (y=550, h=180).
                // Gate Y = 550. H = 180.
                gates.add(new ExitGate(l1_RightX, 550, 40, 180, Theme.YELLOW_BLOCK, 1));
                blocks.add(new Block(l1_RightX, l1_TopY, 40, 550 - l1_TopY, createRect(40, 490, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l1_RightX, 730, 40, l1_BotY - 730 + 40, createRect(40, 50, CELL), Theme.WALL_COLOR));
                
                // Top/Bot Walls
                blocks.add(new Block(l1_LeftX, l1_TopY, l1_RightX - l1_LeftX + 40, 40, createRect(260, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l1_LeftX, l1_BotY, l1_RightX - l1_LeftX + 40, 40, createRect(260, 40, CELL), Theme.WALL_COLOR));
                
                // Blocks
                addBlock(blocks, 1, 3, S_2x2, Theme.BLUE_BLOCK);
                addBlock(blocks, 5, 3, S_2x2, Theme.YELLOW_BLOCK);
                break;
            }

            case 2: { // 3 Vertical Bars - Narrow Vertical
                // Active Cols: 2, 3, 4.
                // Col 2 X = 40 + 180 = 220.
                // Col 5 X = 40 + 450 = 490.
                // Left Wall X: 220 - 40 = 180.
                // Right Wall X: 490.
                
                int l2_LeftX = 180;
                int l2_RightX = 490;
                
                blocks.clear(); gates.clear();
                
                // Top Wall (Purple Gate)
                // Center X of arena (width 270): 220 + 135 = 355.
                // Gate 90px -> 310 to 400.
                gates.add(new ExitGate(310, TOP_WALL_Y, 90, 40, Theme.PURPLE_BLOCK, 0));
                blocks.add(new Block(l2_LeftX, TOP_WALL_Y, 310 - l2_LeftX, 40, createRect(130, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(400, TOP_WALL_Y, l2_RightX + 40 - 400, 40, createRect(130, 40, CELL), Theme.WALL_COLOR));
                
                // Bot Wall (Blue and Yellow Gates)
                // Gates at Col 2 and Col 4?
                // Col 2 X: 220. Col 4 X: 400.
                // Blue at 220. Yellow at 400. Size 90.
                gates.add(new ExitGate(220, BOT_WALL_Y, 90, 40, Theme.BLUE_BLOCK, 2));
                gates.add(new ExitGate(400, BOT_WALL_Y, 90, 40, Theme.YELLOW_BLOCK, 2));
                
                blocks.add(new Block(l2_LeftX, BOT_WALL_Y, 40, 40, createRect(40, 40, CELL), Theme.WALL_COLOR)); // Corner
                blocks.add(new Block(310, BOT_WALL_Y, 90, 40, createRect(90, 40, CELL), Theme.WALL_COLOR)); // Mid
                blocks.add(new Block(l2_RightX, BOT_WALL_Y, 40, 40, createRect(40, 40, CELL), Theme.WALL_COLOR)); // Corner
                
                // Side Walls
                blocks.add(new Block(l2_LeftX, GRID_Y, 40, GRID_SIZE, createRect(40, GRID_SIZE, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l2_RightX, GRID_Y, 40, GRID_SIZE, createRect(40, GRID_SIZE, CELL), Theme.WALL_COLOR));
                
                addBlock(blocks, 2, 2, S_3x1, Theme.BLUE_BLOCK);
                addBlock(blocks, 2, 3, S_3x1, Theme.YELLOW_BLOCK);
                addBlock(blocks, 2, 4, S_3x1, Theme.PURPLE_BLOCK);
                break;
            }

            case 3: { // Corner Ls - Compact 6x6 (Tight Fit - Corrected)
                // Arena 6x6 Tiles (540x540 px).
                // Inner Start: X=130, Y=140.
                // Inner End: X=670, Y=680.
                // Walls OUTSIDE.
                // LeftX = 90. RightX = 670.
                // TopY = 100. BotY = 680.
                
                int l3_LeftX = 90;
                int l3_RightX = 670;
                int l3_TopY = 100;
                int l3_BotY = 680;
                
                int innerX = l3_LeftX + 40; // 130
                int innerY = l3_TopY + 40;  // 140
                
                blocks.clear(); gates.clear();
                
                // --- WALLS & GATES (Centered 180px) ---
                
                // TOP WALL (Blue)
                // Inner Width 540. Center 270. Gate 180 (90 each side).
                // Center X = 130 + 270 = 400.
                // Gate 310 to 490.
                gates.add(new ExitGate(310, l3_TopY, 180, 40, Theme.BLUE_BLOCK, 0));
                blocks.add(new Block(l3_LeftX, l3_TopY, 310 - l3_LeftX, 40, createRect(220, 40, CELL), Theme.WALL_COLOR)); // 90 to 310
                blocks.add(new Block(490, l3_TopY, l3_RightX + 40 - 490, 40, createRect(220, 40, CELL), Theme.WALL_COLOR)); // 490 to 710
                
                // BOTTOM WALL (Green)
                gates.add(new ExitGate(310, l3_BotY, 180, 40, Theme.GREEN_BLOCK, 2));
                blocks.add(new Block(l3_LeftX, l3_BotY, 310 - l3_LeftX, 40, createRect(220, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(490, l3_BotY, l3_RightX + 40 - 490, 40, createRect(220, 40, CELL), Theme.WALL_COLOR));
                
                // LEFT WALL (Pink)
                // Height 540. Center Y = 140 + 270 = 410.
                // Gate 180 (90 each side). 320 to 500.
                gates.add(new ExitGate(l3_LeftX, 320, 40, 180, Theme.PINK_BLOCK, 3));
                // Wall Top: 140 to 320.
                blocks.add(new Block(l3_LeftX, 140, 40, 180, createRect(40, 180, CELL), Theme.WALL_COLOR));
                // Wall Bot: 500 to 680.
                blocks.add(new Block(l3_LeftX, 500, 40, 180, createRect(40, 180, CELL), Theme.WALL_COLOR));
                
                // RIGHT WALL (Yellow)
                gates.add(new ExitGate(l3_RightX, 320, 40, 180, Theme.YELLOW_BLOCK, 1));
                blocks.add(new Block(l3_RightX, 140, 40, 180, createRect(40, 180, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l3_RightX, 500, 40, 180, createRect(40, 180, CELL), Theme.WALL_COLOR));
                
                // --- BLOCKS (6x6 Grid) ---
                
                // TL (0,0) - Blue
                addExpandedBlock(blocks, innerX + 0*90, innerY + 0*90, L_TL, Theme.BLUE_BLOCK);
                
                // TR (0,4) - Yellow
                addExpandedBlock(blocks, innerX + 4*90, innerY + 0*90, L_TR, Theme.YELLOW_BLOCK);
                
                // BL (4,0) - Pink
                addExpandedBlock(blocks, innerX + 0*90, innerY + 4*90, L_BL, Theme.PINK_BLOCK);
                
                // BR (4,4) - Green
                addExpandedBlock(blocks, innerX + 4*90, innerY + 4*90, L_BR, Theme.GREEN_BLOCK);
                
                // Center (2,2) - Green 2x2
                addExpandedBlock(blocks, innerX + 2*90, innerY + 2*90, S_2x2, Theme.GREEN_BLOCK);
                break;
            }

            case 4: { // Compact Cross - Fixed Walls & Gates
                // Arena 5x5 Tiles (450x450 px).
                // Inner Area: X: 175 to 625. Y: 235 to 685.
                // Walls: Left=135, Right=625, Top=195, Bot=685.
                
                int l4_LeftX = 135;
                int l4_RightX = 625;
                int l4_TopY = 195;
                int l4_BotY = 685;
                
                int innerX = 175;
                int innerY = 235;
                
                blocks.clear(); gates.clear();
                
                // --- TOP & BOTTOM WALLS (Orange 270px) ---
                // Center 400. Gate 265 to 535.
                gates.add(new ExitGate(265, l4_TopY, 270, 40, Theme.ORANGE_BLOCK, 0));
                blocks.add(new Block(l4_LeftX, l4_TopY, 130, 40, createRect(130, 40, CELL), Theme.WALL_COLOR)); // 135 to 265
                blocks.add(new Block(535, l4_TopY, 130, 40, createRect(130, 40, CELL), Theme.WALL_COLOR)); // 535 to 665
                
                gates.add(new ExitGate(265, l4_BotY, 270, 40, Theme.ORANGE_BLOCK, 2));
                blocks.add(new Block(l4_LeftX, l4_BotY, 130, 40, createRect(130, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(535, l4_BotY, 130, 40, createRect(130, 40, CELL), Theme.WALL_COLOR));
                
                // --- LEFT WALL ---
                // Yellow Gate (Top Corner): 235 to 415 (180px)
                gates.add(new ExitGate(l4_LeftX, 235, 40, 180, Theme.YELLOW_BLOCK, 3));
                // Wall segment: 415 to 595 (180px)
                blocks.add(new Block(l4_LeftX, 415, 40, 180, createRect(40, 180, CELL), Theme.WALL_COLOR));
                // Green Gate (Bottom Corner): 595 to 685 (90px)
                gates.add(new ExitGate(l4_LeftX, 595, 40, 90, Theme.GREEN_BLOCK, 3));
                
                // --- RIGHT WALL ---
                // Cyan Gate (Top Corner): 235 to 325 (90px)
                gates.add(new ExitGate(l4_RightX, 235, 40, 90, Theme.CYAN_BLOCK, 1));
                // Wall segment: 325 to 505 (180px)
                blocks.add(new Block(l4_RightX, 325, 40, 180, createRect(40, 180, CELL), Theme.WALL_COLOR));
                // Pink Gate (Bottom Corner): 505 to 685 (180px)
                gates.add(new ExitGate(l4_RightX, 505, 40, 180, Theme.PINK_BLOCK, 1));
                
                // --- BLOCKS ---
                int[][] shape = CROSS;
                int rows = shape.length;
                int cols = shape[0].length;
                int[][] cellMatrix = new int[rows * 2][cols * 2];
                for(int r=0; r<rows; r++) {
                    for(int c=0; c<cols; c++) {
                        if(shape[r][c] == 1) {
                            cellMatrix[r*2][c*2] = 1;
                            cellMatrix[r*2][c*2+1] = 1;
                            cellMatrix[r*2+1][c*2] = 1;
                            cellMatrix[r*2+1][c*2+1] = 1;
                        }
                    }
                }
                blocks.add(new Block(265, 325, cellMatrix, Theme.ORANGE_BLOCK));
                blocks.add(new Block(175, 325, 180, 90, createRect(180, 90, CELL), Theme.CYAN_BLOCK));
                blocks.add(new Block(445, 280, 135, 135, createRect(135, 135, CELL), Theme.YELLOW_BLOCK));
                blocks.add(new Block(220, 505, 135, 135, createRect(135, 135, CELL), Theme.PINK_BLOCK));
                blocks.add(new Block(445, 505, 180, 90, createRect(180, 90, CELL), Theme.GREEN_BLOCK));
                break;
            }

            case 5: { // Layout from Image 5 (Compact 6x6 Arena)
                // Arena is shrunk to fit the 6x6 block structure (Cols 1-6, Rows 0-5)
                // Wall thickness: 40.
                
                // Define Bounds for 6x6 Grid centered horizontally
                // Original Grid X starts at 40. Tiles are 90.
                // We use cols 1-6 from original 8-col grid.
                // New Left Wall X = GRID_X + 1*TILE - WALL = 40 + 90 - 40 = 90
                // New Right Wall X = GRID_X + 7*TILE = 40 + 630 = 670
                // New Top Wall Y = GRID_Y = 100
                // New Bot Wall Y = GRID_Y + 6*TILE = 100 + 540 = 640
                
                int compactLeftX = GRID_X + TILE - WALL; // 90
                int compactRightX = GRID_X + 7*TILE;     // 670
                int compactTopY = GRID_Y - WALL;         // 60 (Standard Top)
                int compactBotY = GRID_Y + 6*TILE;       // 640
                
                int compactWidth = compactRightX - compactLeftX + WALL; // 670 - 90 + 40 = 620 (6 tiles * 90 + 2*40 walls? No. Inner is 540. Walls 40. Total 620.)
                int compactHeight = compactBotY - compactTopY + WALL;   // 640 - 60 + 40 = 620
                
                // Clear old
                blocks.clear(); gates.clear();

                // --- GATES & WALLS (Compact) ---
                
                // Top Wall (Cyan Gate 180px + Green Gate 180px)
                // Total width 540. Cyan (180), Green (180).
                // Let's place them: Cyan at left part, Green at right part.
                // Left Wall X = 90.
                // Cyan: 90 + 40 + gap?
                // Inner X starts at 130.
                // Cyan Gate: 130 to 310. Green Gate: 400 to 580.
                gates.add(new ExitGate(130, compactTopY, 180, 40, Theme.CYAN_BLOCK, 0));
                gates.add(new ExitGate(400, compactTopY, 180, 40, Theme.GREEN_BLOCK, 0));
                
                blocks.add(new Block(compactLeftX, compactTopY, 130 - compactLeftX, 40, createRect(130 - compactLeftX, 40, CELL), Theme.WALL_COLOR)); // Left Corner
                blocks.add(new Block(310, compactTopY, 400 - 310, 40, createRect(400 - 310, 40, CELL), Theme.WALL_COLOR)); // Mid Gap
                blocks.add(new Block(580, compactTopY, compactRightX + WALL - 580, 40, createRect(compactRightX + WALL - 580, 40, CELL), Theme.WALL_COLOR)); // Right Corner

                // Bot Wall (Pink Gate 180px + Blue Gate 180px)
                // Pink Left, Blue Right.
                gates.add(new ExitGate(130, compactBotY, 180, 40, Theme.PINK_BLOCK, 2));
                gates.add(new ExitGate(400, compactBotY, 180, 40, Theme.BLUE_BLOCK, 2));
                
                blocks.add(new Block(compactLeftX, compactBotY, 130 - compactLeftX, 40, createRect(130 - compactLeftX, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(310, compactBotY, 400 - 310, 40, createRect(400 - 310, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(580, compactBotY, compactRightX + WALL - 580, 40, createRect(compactRightX + WALL - 580, 40, CELL), Theme.WALL_COLOR));

                // Left Wall (Yellow Gate 270px)
                // Center Y of 6 rows: 100 + 540/2 = 370.
                // Gate 270 -> 235 to 505.
                gates.add(new ExitGate(compactLeftX, 235, 40, 270, Theme.YELLOW_BLOCK, 3));
                blocks.add(new Block(compactLeftX, compactTopY + 40, 40, 235 - (compactTopY + 40), createRect(40, 135, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(compactLeftX, 505, 40, compactBotY - 505, createRect(40, 135, CELL), Theme.WALL_COLOR));

                // Right Wall (Purple Gate 180px + Orange Gate 180px)
                // Total Height 540. 
                // Purple Top (100+?), Orange Bot.
                // Let's align with blocks.
                // Purple row 2-3? Orange row 4-5?
                // Y starts 100.
                // Purple: 190 to 370 (180px). Orange: 370 to 550 (180px).
                gates.add(new ExitGate(compactRightX, 190, 40, 180, Theme.PURPLE_BLOCK, 1));
                gates.add(new ExitGate(compactRightX, 415, 40, 180, Theme.ORANGE_BLOCK, 1));
                
                blocks.add(new Block(compactRightX, compactTopY + 40, 40, 190 - (compactTopY + 40), createRect(40, 90, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(compactRightX, 370, 40, 415 - 370, createRect(40, 45, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(compactRightX, 595, 40, compactBotY - 595, createRect(40, 45, CELL), Theme.WALL_COLOR));
                
                // Blocks (Offset by -1 col to fit new 0-index visual but keep logical consistency? 
                // No, simply map logical col 1 to 0 in this compact view or keep logic and just visual matches.
                // The addBlock uses GRID_X. We need to shift blocks left by 1 tile (90px) to match the new wall.
                // Standard GRID_X is 40. New wall start is 90. Inner start is 130.
                // Wait, standard grid starts at 40. Col 1 starts at 40 + 90 = 130.
                // Our new Left Wall is at 90. Inner edge at 130. 
                // So blocks at Col 1 (Logic) will naturally align with the new Left Wall. PERFECT.
                // We just use standard addBlock.
                
                // Row 0
                addBlock(blocks, 0, 1, S_2x2, Theme.PINK_BLOCK);
                addBlock(blocks, 0, 5, S_2x2, Theme.GREEN_BLOCK);
                
                // Row 1 (Center Vertical blocks)
                addBlock(blocks, 1, 3, S_2x1, Theme.GREEN_BLOCK);
                addBlock(blocks, 1, 4, S_2x1, Theme.PINK_BLOCK);
                
                // Row 3 (Long Purple Bar)
                addBlock(blocks, 3, 2, S_1x4, Theme.PURPLE_BLOCK);
                
                // Row 4
                addBlock(blocks, 4, 1, S_1x2, Theme.ORANGE_BLOCK);
                addBlock(blocks, 4, 5, S_1x2, Theme.BLUE_BLOCK);
                
                // Row 5
                addBlock(blocks, 5, 1, S_1x2, Theme.CYAN_BLOCK);
                addBlock(blocks, 5, 5, S_1x2, Theme.YELLOW_BLOCK);
                break;
            }

            case 6: { // Layout from Image 6 - Final Corrected (6x6 Arena Exact Fit)
                // Arena 6x6 Tiles (540x540 px).
                // Inner Area: X: 130 to 670. Y: 140 to 680.
                // Walls (40px thick) are OUTSIDE this area.
                // Left Wall X = 130 - 40 = 90.
                // Right Wall X = 670.
                // Top Wall Y = 140 - 40 = 100.
                // Bot Wall Y = 680.
                
                int l6_LeftX = 90;
                int l6_RightX = 670;
                int l6_TopY = 100;
                int l6_BotY = 680;
                
                int innerX = l6_LeftX + 40; // 130
                int innerY = l6_TopY + 40;  // 140
                
                blocks.clear(); gates.clear();
                
                // --- GATES ---
                
                // TOP WALL (Purple 180px - Centered)
                // Inner Width 540. Center X = 130 + 270 = 400.
                // Gate 180 -> 310 to 490.
                gates.add(new ExitGate(310, l6_TopY, 180, 40, Theme.PURPLE_BLOCK, 0));
                blocks.add(new Block(l6_LeftX, l6_TopY, 310 - l6_LeftX, 40, createRect(220, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(490, l6_TopY, l6_RightX + 40 - 490, 40, createRect(220, 40, CELL), Theme.WALL_COLOR));
                
                // BOTTOM WALL (Solid)
                blocks.add(new Block(l6_LeftX, l6_BotY, l6_RightX + 40 - l6_LeftX, 40, createRect(620, 40, CELL), Theme.WALL_COLOR));
                
                // LEFT WALL (Cyan Top 180px, Green Bot 90px)
                // Y Start 140.
                // Cyan (Rows 0-1): 140 to 320.
                gates.add(new ExitGate(l6_LeftX, 140, 40, 180, Theme.CYAN_BLOCK, 3));
                // Wall Gap (Rows 2,3,4?): 320 to 590. (270px)
                blocks.add(new Block(l6_LeftX, 320, 40, 270, createRect(40, 270, CELL), Theme.WALL_COLOR));
                // Green (Row 5): 590 to 680.
                gates.add(new ExitGate(l6_LeftX, 590, 40, 90, Theme.GREEN_BLOCK, 3));
                
                // RIGHT WALL (Pink Top 90px, Blue Bot 90px)
                // Pink (Row 0): 140 to 230.
                gates.add(new ExitGate(l6_RightX, 140, 40, 90, Theme.PINK_BLOCK, 1));
                // Wall Gap (Row 1,2,3,4): 230 to 590 (360px).
                blocks.add(new Block(l6_RightX, 230, 40, 360, createRect(40, 360, CELL), Theme.WALL_COLOR));
                // Blue (Row 5): 590 to 680.
                gates.add(new ExitGate(l6_RightX, 590, 40, 90, Theme.BLUE_BLOCK, 1));
                
                // --- BLOCKS (6x6 Grid) ---
                
                // Top Layer
                int[][] L_CYAN = {{1, 1}, {1, 0}}; 
                addExpandedBlock(blocks, innerX + 2*90, innerY + 0*90, L_CYAN, Theme.CYAN_BLOCK);
                int[][] L_PURPLE = {{1, 1}, {0, 1}}; 
                addExpandedBlock(blocks, innerX + 4*90, innerY + 0*90, L_PURPLE, Theme.PURPLE_BLOCK);
                
                // Row 2
                addExpandedBlock(blocks, innerX + 0*90, innerY + 2*90, S_1x3, Theme.PINK_BLOCK);
                addExpandedBlock(blocks, innerX + 4*90, innerY + 2*90, S_1x1, Theme.CYAN_BLOCK);
                
                // Row 3
                addExpandedBlock(blocks, innerX + 2*90, innerY + 3*90, S_1x1, Theme.GREEN_BLOCK);
                addExpandedBlock(blocks, innerX + 3*90, innerY + 3*90, S_1x3, Theme.BLUE_BLOCK);
                
                // Row 4
                addExpandedBlock(blocks, innerX + 0*90, innerY + 4*90, S_1x3, Theme.BLUE_BLOCK);
                addExpandedBlock(blocks, innerX + 4*90, innerY + 4*90, S_1x1, Theme.GREEN_BLOCK);
                
                // Row 5
                addExpandedBlock(blocks, innerX + 2*90, innerY + 5*90, S_1x1, Theme.PINK_BLOCK);
                addExpandedBlock(blocks, innerX + 3*90, innerY + 5*90, S_1x3, Theme.CYAN_BLOCK);
                break;
            }

            
            case 7: { // Compact 6x7 Arena - EXACT MODEL REPLICA
                // Arena 6x7 Tiles (540x630 px).
                // Walls: Left=130, Right=710. Top=60, Bot=730.
                
                int l7_LeftX = 130;
                int l7_RightX = 710;
                int l7_TopY = 60;
                int l7_BotY = 730;
                
                blocks.clear(); gates.clear();
                
                // --- GATES ---
                // Top: Orange (Left 270), Green (Right 270)
                gates.add(new ExitGate(l7_LeftX + 40, l7_TopY, 270, 40, Theme.ORANGE_BLOCK, 0));
                gates.add(new ExitGate(l7_LeftX + 310, l7_TopY, 270, 40, Theme.GREEN_BLOCK, 0));
                
                // Bot: 3 Gates - Pink (180), Green (180), Purple (180)
                gates.add(new ExitGate(l7_LeftX + 40, l7_BotY, 180, 40, Theme.PINK_BLOCK, 2));
                gates.add(new ExitGate(l7_LeftX + 220, l7_BotY, 180, 40, Theme.GREEN_BLOCK, 2));
                gates.add(new ExitGate(l7_LeftX + 400, l7_BotY, 180, 40, Theme.PURPLE_BLOCK, 2));
                
                // Corners (Solid Walls)
                blocks.add(new Block(l7_LeftX, l7_TopY, 40, 40, createRect(40, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l7_RightX, l7_TopY, 40, 40, createRect(40, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l7_LeftX, l7_BotY, 40, 40, createRect(40, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l7_RightX, l7_BotY, 40, 40, createRect(40, 40, CELL), Theme.WALL_COLOR));

                int gateStartY = l7_TopY + 40; // 100

                // Left Wall: Yellow(180), Cyan(180), Blue(270)
                gates.add(new ExitGate(l7_LeftX, gateStartY, 40, 180, Theme.YELLOW_BLOCK, 3));
                gates.add(new ExitGate(l7_LeftX, gateStartY + 180, 40, 180, Theme.CYAN_BLOCK, 3));
                gates.add(new ExitGate(l7_LeftX, gateStartY + 360, 40, 270, Theme.BLUE_BLOCK, 3));
                
                // Right Wall: Blue(180), Red(360), small Yellow(90)
                gates.add(new ExitGate(l7_RightX, gateStartY, 40, 180, Theme.BLUE_BLOCK, 1));
                gates.add(new ExitGate(l7_RightX, gateStartY + 180, 40, 360, Theme.RED_BLOCK, 1)); 
                gates.add(new ExitGate(l7_RightX, gateStartY + 540, 40, 90, Theme.YELLOW_BLOCK, 1));
                
                // --- BLOCKS ---
                int startX = l7_LeftX + 40; // 170
                int startY = l7_TopY + 40;  // 100
                
                // 1. Yellow L (L_TL)
                addExpandedBlock(blocks, startX + 1*90, startY + 0*90, L_TL, Theme.YELLOW_BLOCK); 
                // 2. Pink 1x1
                addExpandedBlock(blocks, startX + 5*90, startY + 0*90, S_1x1, Theme.PINK_BLOCK);
                // 3. Cyan 2x2 (H)
                addRestrictedExpandedBlock(blocks, startX + 3*90, startY + 1*90, S_2x2, Theme.CYAN_BLOCK, Block.Axis.HORIZONTAL);
                // 4. Purple Bar 1x3 (V)
                addRestrictedExpandedBlock(blocks, startX + 5*90, startY + 1*90, S_3x1, Theme.PURPLE_BLOCK, Block.Axis.VERTICAL);
                // 5. Yellow Bar (H) under L
                addExpandedBlock(blocks, startX + 1*90, startY + 2*90, S_1x2, Theme.YELLOW_BLOCK);
                // 6. Cyan 1x1
                addExpandedBlock(blocks, startX + 2*90, startY + 3*90, S_1x1, Theme.CYAN_BLOCK);
                // 7. Green L (V) - Stuck to Cyan 2x2
                int[][] greenL = {{0,1},{0,1},{1,1}};
                addRestrictedExpandedBlock(blocks, startX + 3*90, startY + 3*90, greenL, Theme.GREEN_BLOCK, Block.Axis.VERTICAL);
                // 8. Orange Bar 1x3 (V)
                addRestrictedExpandedBlock(blocks, startX + 1*90, startY + 3*90, S_3x1, Theme.ORANGE_BLOCK, Block.Axis.VERTICAL);
                // 9. Blue 1x2 (H)
                addRestrictedExpandedBlock(blocks, startX + 2*90, startY + 6*90, S_1x2, Theme.BLUE_BLOCK, Block.Axis.HORIZONTAL);
                // 10. Yellow 1x1 (H)
                addRestrictedExpandedBlock(blocks, startX + 4*90, startY + 6*90, S_1x1, Theme.YELLOW_BLOCK, Block.Axis.HORIZONTAL);
                
                break;
            }

            case 8: { // Centered 5x6 Arena - Exact shapes and gates
                // Arena 5x6 Tiles (450x540 px).
                // Centered: StartX = 135. InnerX starts at 175.
                int l8_LeftX = 135;
                int l8_RightX = 715; 
                int l8_TopY = 100;
                int l8_BotY = 680;
                
                blocks.clear(); gates.clear();
                
                int startX = l8_LeftX + 40; // 175
                int startY = l8_TopY + 40;  // 140

                // --- GATES ---
                gates.add(new ExitGate(startX + 1*90, l8_TopY, 90, 40, Theme.ORANGE_BLOCK, 0));
                gates.add(new ExitGate(startX + 4*90, l8_TopY, 90, 40, Theme.GREEN_BLOCK, 0));
                gates.add(new ExitGate(startX + 0*90, l8_BotY, 270, 40, Theme.YELLOW_BLOCK, 2));
                gates.add(new ExitGate(startX + 3*90, l8_BotY, 270, 40, Theme.CYAN_BLOCK, 2));
                gates.add(new ExitGate(l8_LeftX, startY + 2*90, 40, 180, Theme.PURPLE_BLOCK, 3));
                gates.add(new ExitGate(l8_RightX, startY + 2*90, 40, 180, Theme.PINK_BLOCK, 1));
                
                // --- WALLS (Enclosing the arena) ---
                // Corners
                blocks.add(new Block(l8_LeftX, l8_TopY, 40, 40, createRect(40, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l8_RightX, l8_TopY, 40, 40, createRect(40, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l8_LeftX, l8_BotY, 40, 40, createRect(40, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l8_RightX, l8_BotY, 40, 40, createRect(40, 40, CELL), Theme.WALL_COLOR));

                // Top Wall segments
                blocks.add(new Block(startX, l8_TopY, 90, 40, createRect(180, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(startX + 2*90, l8_TopY, 180, 40, createRect(90, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(startX + 5*90, l8_TopY, 90, 40, createRect(90, 40, CELL), Theme.WALL_COLOR));
                
                // Side Wall segments
                blocks.add(new Block(l8_LeftX, startY, 40, 180, createRect(40, 180, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l8_LeftX, startY + 4*90, 40, 180, createRect(40, 180, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l8_RightX, startY, 40, 180, createRect(40, 180, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l8_RightX, startY + 4*90, 40, 180, createRect(40, 180, CELL), Theme.WALL_COLOR));

                // --- BLOCKS (9 shapes) ---
                addExpandedBlock(blocks, startX + 0*90, startY + 0*90, S_1x3, Theme.CYAN_BLOCK);
                addExpandedBlock(blocks, startX + 4*90, startY + 0*90, S_2x1, Theme.YELLOW_BLOCK);
                addExpandedBlock(blocks, startX + 0*90, startY + 1*90, S_3x1, Theme.GREEN_BLOCK);
                addRestrictedExpandedBlock(blocks, startX + 1*90, startY + 2*90, S_1x3, Theme.PINK_BLOCK, Block.Axis.HORIZONTAL);
                addRestrictedExpandedBlock(blocks, startX + 1*90, startY + 3*90, S_1x3, Theme.PURPLE_BLOCK, Block.Axis.HORIZONTAL);
                addExpandedBlock(blocks, startX + 4*90, startY + 2*90, S_1x1, Theme.ORANGE_BLOCK);
                addExpandedBlock(blocks, startX + 5*90, startY + 3*90, S_1x1, Theme.ORANGE_BLOCK);
                addExpandedBlock(blocks, startX + 0*90, startY + 4*90, L_BR, Theme.PINK_BLOCK);
                addExpandedBlock(blocks, startX + 3*90, startY + 5*90 - 45, S_1x3, Theme.YELLOW_BLOCK);
                
                break;
            }
            
            case 9: { // Horizontal Stack - Exact Replica of 9.png
                // Arena 4x6 Tiles (360x540 px).
                int l9_LeftX = 220; // Centered (800-360-80)/2 = 180? No, (800-440)/2 = 180.
                // Let's use 220. 220 + 40 (wall) + 360 (grid) + 40 (wall) = 660. 
                // To center: (800-440)/2 = 180.
                l9_LeftX = 180;
                int l9_RightX = 580; 
                int l9_TopY = 100;
                int l9_BotY = 640;
                
                blocks.clear(); gates.clear();
                int startX = l9_LeftX + 40; // 220
                int startY = l9_TopY + 40;  // 140

                // --- GATES (Side mixed colors) ---
                gates.add(new ExitGate(l9_LeftX, startY + 1*90, 40, 90, Theme.BLUE_BLOCK, 3));
                gates.add(new ExitGate(l9_LeftX, startY + 2*90, 40, 90, Theme.CYAN_BLOCK, 3));
                gates.add(new ExitGate(l9_LeftX, startY + 3*90, 40, 90, Theme.GREEN_BLOCK, 3));
                
                gates.add(new ExitGate(l9_RightX, startY + 3*90, 40, 90, Theme.ORANGE_BLOCK, 1));
                gates.add(new ExitGate(l9_RightX, startY + 4*90, 40, 90, Theme.YELLOW_BLOCK, 1));
                gates.add(new ExitGate(l9_RightX, startY + 5*90, 40, 90, Theme.PINK_BLOCK, 1));

                // --- WALLS ---
                blocks.add(new Block(l9_LeftX, l9_TopY, 440, 40, createRect(440, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l9_LeftX, l9_TopY + 7*90 + 40, 440, 40, createRect(440, 40, CELL), Theme.WALL_COLOR));

                blocks.add(new Block(l9_LeftX, startY + 0*90, 40, 90, createRect(40, 180, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l9_LeftX, startY + 4*90, 40, 270, createRect(40, 180, CELL), Theme.WALL_COLOR));
                
                blocks.add(new Block(l9_RightX, startY, 40, 270, createRect(40, 180, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l9_RightX, startY + 6*90, 40, 90, createRect(40, 90, CELL), Theme.WALL_COLOR));

                // --- BLOCKS (1x4 Horizontal Bars) - Normal shapes, no arrows ---
                addExpandedBlock(blocks, startX, startY + 1*90, S_1x4, Theme.PINK_BLOCK);
                addExpandedBlock(blocks, startX, startY + 2*90, S_1x4, Theme.ORANGE_BLOCK);
                addExpandedBlock(blocks, startX, startY + 3*90, S_1x4, Theme.YELLOW_BLOCK);
                addExpandedBlock(blocks, startX, startY + 4*90, S_1x4, Theme.GREEN_BLOCK);
                addExpandedBlock(blocks, startX, startY + 5*90, S_1x4, Theme.CYAN_BLOCK);
                addExpandedBlock(blocks, startX, startY + 6*90, S_1x4, Theme.BLUE_BLOCK);
                break;
            }

            case 10: { // Precise Replica of Level 10
                // Arena 6x8 Tiles (540x720 px).
                int l10_LeftX = 130;
                int l10_RightX = 620;
                int l10_TopY = 60;
                int l10_BotY = 730;
                
                blocks.clear(); gates.clear();
                int startX = l10_LeftX + 40; // 170
                int startY = l10_TopY + 40;  // 100

                // --- GATES ---
                // Top: Small Blue(90), Med Red(180), Med Cyan(180)
                gates.add(new ExitGate(startX + 0*90, l10_TopY, 90, 40, Theme.BLUE_BLOCK, 0));
                gates.add(new ExitGate(startX + 1*90, l10_TopY, 180, 40, Theme.PINK_BLOCK, 0));
                gates.add(new ExitGate(startX + 3*90, l10_TopY, 180, 40, Theme.CYAN_BLOCK, 0));

                // Bot: Med Yellow(180), Large Orange(360)
                gates.add(new ExitGate(startX + 0*90, l10_BotY, 180, 40, Theme.YELLOW_BLOCK, 2));
                gates.add(new ExitGate(startX + 2*90, l10_BotY, 270, 40, Theme.ORANGE_BLOCK, 2));

                // Left: Small Yellow(90), Med Cyan(180), Med Red(180), Med Green(180)
                gates.add(new ExitGate(l10_LeftX, startY + 0*90, 40, 90, Theme.YELLOW_BLOCK, 3));
                gates.add(new ExitGate(l10_LeftX, startY + 1*90, 40, 180, Theme.CYAN_BLOCK, 3));
                gates.add(new ExitGate(l10_LeftX, startY + 3*90, 40, 180, Theme.PINK_BLOCK, 3));
                gates.add(new ExitGate(l10_LeftX, startY + 5*90, 40, 180, Theme.GREEN_BLOCK, 3));

                // Right: Med Purple(180), Small Blue(90), Small Yellow(90), Large Blue(360)
                gates.add(new ExitGate(l10_RightX, startY + 0*90, 40, 180, Theme.PURPLE_BLOCK, 1));
                gates.add(new ExitGate(l10_RightX, startY + 2*90, 40, 90, Theme.BLUE_BLOCK, 1));
                gates.add(new ExitGate(l10_RightX, startY + 3*90, 40, 90, Theme.YELLOW_BLOCK, 1));
                gates.add(new ExitGate(l10_RightX, startY + 4*90, 40, 270, Theme.BLUE_BLOCK, 1));

                // Corners
                blocks.add(new Block(l10_LeftX, l10_TopY, 40, 40, createRect(40, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l10_RightX, l10_TopY, 40, 40, createRect(40, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l10_LeftX, l10_BotY, 40, 40, createRect(40, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l10_RightX, l10_BotY, 40, 40, createRect(40, 40, CELL), Theme.WALL_COLOR));

                // --- BLOCKS ---
                addExpandedBlock(blocks, startX + 0*90, startY + 1*90, S_1x1, Theme.ORANGE_BLOCK);
                addRestrictedExpandedBlock(blocks, startX + 0*90, startY + 2*90, S_1x1, Theme.BLUE_BLOCK, Block.Axis.VERTICAL);
                addExpandedBlock(blocks, startX + 1*90, startY + 0*90, L_TL, Theme.CYAN_BLOCK);
                addRestrictedExpandedBlock(blocks, startX + 2*90, startY + 0*90, L_BR, Theme.PURPLE_BLOCK, Block.Axis.HORIZONTAL);
                addExpandedBlock(blocks, startX + 4*90, startY + 0*90, S_1x1, Theme.PINK_BLOCK);
                addExpandedBlock(blocks, startX + 0*90, startY + 3*90, L_TL, Theme.YELLOW_BLOCK);
                addRestrictedExpandedBlock(blocks, startX + 1*90, startY + 2*90, L_BR3x3, Theme.PINK_BLOCK, Block.Axis.VERTICAL);
                addExpandedBlock(blocks, startX + 3*90, startY + 2*90, L_BL, Theme.BLUE_BLOCK);
                addExpandedBlock(blocks, startX + 1*90, startY + 5*90, S_1x1, Theme.PURPLE_BLOCK);
                addExpandedBlock(blocks, startX + 3*90, startY + 4*90, S_1x1, Theme.GREEN_BLOCK);
                addExpandedBlock(blocks, startX + 2*90, startY + 5*90, S_1x3, Theme.CYAN_BLOCK);
                addRestrictedExpandedBlock(blocks, startX + 0*90, startY + 7*90, S_1x2, Theme.BLUE_BLOCK, Block.Axis.HORIZONTAL);
                
                break;
            }

            case 11: { // Final Level 11 - Correct Replica
                // Arena 6x8 Tiles (540x720 px).
                int l11_LeftX = 40;
                int l11_RightX = 715;
                int l11_TopY = 60;
                int l11_BotY = 730;
                
                blocks.clear(); gates.clear();
                int startX = l11_LeftX + 40; // 170
                int startY = l11_TopY + 40;  // 100

                // --- GATES ---
                // SUS: Verde (90), Galben (180), Mov (180), Verde (90)
                gates.add(new ExitGate(startX + 0*90, l11_TopY, 90, 40, Theme.GREEN_BLOCK, 0));
                gates.add(new ExitGate(startX + 1*90, l11_TopY, 180, 40, Theme.YELLOW_BLOCK, 0));
                gates.add(new ExitGate(startX + 3*90, l11_TopY, 270, 40, Theme.PURPLE_BLOCK, 0));
                gates.add(new ExitGate(startX + 6*90, l11_TopY, 90, 40, Theme.GREEN_BLOCK, 0));
                
                // JOS: Roz (180), Albastru deschis (180), Roz (180)
                gates.add(new ExitGate(startX + 0*90, l11_BotY, 180, 40, Theme.PINK_BLOCK, 2));
                gates.add(new ExitGate(startX + 2*90, l11_BotY, 180, 40, Theme.CYAN_BLOCK, 2));
                gates.add(new ExitGate(startX + 4*90, l11_BotY, 270, 40, Theme.PINK_BLOCK, 2));

                // STANGA: Albastru închis (360), Verde (90), Galben (180), Albastru închis (90)
                gates.add(new ExitGate(l11_LeftX, startY + 0*90, 40,  270, Theme.BLUE_BLOCK, 3));
                gates.add(new ExitGate(l11_LeftX, startY + 3*90, 40, 90, Theme.GREEN_BLOCK, 3));
                gates.add(new ExitGate(l11_LeftX, startY + 4*90, 40, 180, Theme.YELLOW_BLOCK, 3));
                gates.add(new ExitGate(l11_LeftX, startY + 6*90, 40, 90, Theme.BLUE_BLOCK, 3));

                // DREAPTA: Albastru deschis (180), Galben (180), Portocaliu (360)
                gates.add(new ExitGate(l11_RightX, startY + 0*90, 40, 180, Theme.CYAN_BLOCK, 1));
                gates.add(new ExitGate(l11_RightX, startY + 2*90, 40, 180, Theme.YELLOW_BLOCK, 1));
                gates.add(new ExitGate(l11_RightX, startY + 4*90, 40, 270, Theme.ORANGE_BLOCK, 1));

                // Corners
                blocks.add(new Block(l11_LeftX, l11_TopY, 40, 40, createRect(40, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l11_RightX, l11_TopY, 40, 40, createRect(40, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l11_LeftX, l11_BotY, 40, 40, createRect(40, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l11_RightX, l11_BotY, 40, 40, createRect(40, 40, CELL), Theme.WALL_COLOR));

                // --- BLOCKS ---
                addExpandedBlock(blocks, startX + 0*90, startY + 0*90, S_1x2, Theme.ORANGE_BLOCK); 
                addExpandedBlock(blocks, startX + 3*90, startY + 1*90, L_BL, Theme.YELLOW_BLOCK);
                addExpandedBlock(blocks, startX + 0*90, startY + 2*90, L_BL, Theme.YELLOW_BLOCK);
                addExpandedBlock(blocks, startX + 5*90, startY + 0*90, S_1x2, Theme.YELLOW_BLOCK);
                addExpandedBlock(blocks, startX + 5*90, startY + 1*90, S_3x1, Theme.ORANGE_BLOCK);
                addExpandedBlock(blocks, startX + 1*90, startY + 2*90, S_1x2, Theme.GREEN_BLOCK);
                addExpandedBlock(blocks, startX + 4*90, startY + 1*90, S_1x1, Theme.CYAN_BLOCK);
                addExpandedBlock(blocks, startX + 1*90, startY + 1*90, S_1x1, Theme.CYAN_BLOCK);
                addExpandedBlock(blocks, startX + 2*90, startY + 3*90, S_1x1, Theme.BLUE_BLOCK);
                addExpandedBlock(blocks, startX + 3*90, startY + 3*90, S_2x1, Theme.PURPLE_BLOCK);
                addExpandedBlock(blocks, startX + 4*90, startY + 3*90, S_2x1, Theme.PINK_BLOCK);
                addExpandedBlock(blocks, startX + 4*90, startY + 5*90, S_1x2, Theme.PINK_BLOCK);
                addExpandedBlock(blocks, startX + 5*90, startY + 3*90, L_BR, Theme.PURPLE_BLOCK);
                addExpandedBlock(blocks, startX + 0*90, startY + 4*90, L_BR, Theme.ORANGE_BLOCK);
                addExpandedBlock(blocks, startX + 2*90, startY + 4*90, S_3x1, Theme.GREEN_BLOCK);
                addExpandedBlock(blocks, startX + 5*90, startY + 5*90, L_BR, Theme.CYAN_BLOCK);
                
                break;
            }

            case 12: { // Level 12 Replica - 8x8 Grid
                // 8x8 Tiles = 720x720 px.
                // Left Wall at 0, Right Wall at 760. (40 wall + 720 grid + 40 wall = 800).
                int l12_LeftX = 0;
                int l12_RightX = 760;
                int l12_TopY = 60;
                int l12_BotY = 1000; // 60 + 720
                
                blocks.clear(); gates.clear();
                int startX = l12_LeftX + 40; // 40
                int startY = l12_TopY + 40;  // 100

                // --- GATES ---
                // Top: CYAN
                gates.add(new ExitGate(startX + 2*90 + 45, l12_TopY, 270, 40, Theme.CYAN_BLOCK, 0));

                // Bot: Pink, Blue, Pink
                gates.add(new ExitGate(startX + 2*90 + 45, l12_BotY, 270, 40, Theme.ORANGE_BLOCK, 2));

                // Left: Green, Yellow, Pink (180 each)
                gates.add(new ExitGate(l12_LeftX + 45, startY + 0*90, 40, 180, Theme.GREEN_BLOCK, 3));
                gates.add(new ExitGate(l12_LeftX + 45, startY + 4*90 , 40, 180, Theme.YELLOW_BLOCK, 3));
                gates.add(new ExitGate(l12_LeftX + 45, startY + 8*90, 40, 180, Theme.PINK_BLOCK, 3));

                // Right: Green, Purple, Blue (180 each)
                gates.add(new ExitGate(l12_RightX - 45, startY + 0*90, 40, 180, Theme.GREEN_BLOCK, 1));
                gates.add(new ExitGate(l12_RightX - 45, startY + 4*90, 40, 180, Theme.PURPLE_BLOCK, 1));
                gates.add(new ExitGate(l12_RightX - 45, startY + 8*90, 40, 180, Theme.BLUE_BLOCK, 1));

                // Corners
                blocks.add(new Block(l12_LeftX + 45, l12_TopY, 220, 40, createRect(40, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l12_RightX - 225, l12_TopY, 220, 40, createRect(40, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l12_LeftX + 45, l12_BotY, 220, 40, createRect(40, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l12_RightX - 225, l12_BotY, 220, 40, createRect(40, 40, CELL), Theme.WALL_COLOR));

                // Walls
                blocks.add(new Block(l12_LeftX + 45, l12_TopY + 220, 40, 180, createRect(40, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l12_LeftX + 45, l12_TopY + 580, 40, 180, createRect(40, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l12_RightX - 45, l12_TopY + 220, 40, 180, createRect(40, 40, CELL), Theme.WALL_COLOR));
                blocks.add(new Block(l12_RightX - 45, l12_TopY + 580, 40, 180, createRect(40, 40, CELL), Theme.WALL_COLOR));

                // --- OBSTACLES (Static Walls in Grid) ---
                // Obs 1: Row 2, Cols 3,4. (2-wide, 1-high) -> NO, Logic was 4-wide? Or 2-wide?
                // Decided on 2-wide (3,4) to allow packing.
                // 2x1 Horizontal Wall.
                blocks.add(new Block(startX + 2*90 + 45, startY + 2*90, 270, 180, createRect(180, 90, CELL), Theme.WALL_COLOR));
                
                // Obs 2: Row 5, Cols 3,4.
                blocks.add(new Block(startX + 2*90 + 45, startY + 6*90 , 270, 180, createRect(180, 90, CELL), Theme.WALL_COLOR));

                // --- BLOCKS ---
                
                // Row 0
                addExpandedBlock(blocks, startX + 0*90 + 45, startY + 0*90, L_BR, Theme.CYAN_BLOCK);
                addRestrictedExpandedBlock(blocks, startX + 2*90 + 45, startY + 0*90, L_BL, Theme.GREEN_BLOCK, Block.Axis.HORIZONTAL);
                addRestrictedExpandedBlock(blocks, startX + 3*90 + 45, startY + 0*90, L_TR, Theme.GREEN_BLOCK, Block.Axis.HORIZONTAL);
                addExpandedBlock(blocks, startX + 5*90 + 45, startY + 0*90, L_BL, Theme.YELLOW_BLOCK);

                // Row 2 (Verticals Side)
                addExpandedBlock(blocks, startX + 0*90 + 45, startY + 3*90, S_2x1, Theme.BLUE_BLOCK); // Left
                addExpandedBlock(blocks, startX + 6*90 + 45, startY + 3*90, S_2x1, Theme.ORANGE_BLOCK); // Right

                // Row 3-4 (Center)
                // Pink (1,2)
                addExpandedBlock(blocks, startX + 1*90 + 45, startY + 4*90, S_2x2, Theme.PINK_BLOCK);
                // Orange (3,4) - Gap filler
                addExpandedBlock(blocks, startX + 3*90 + 45, startY + 4*90, S_1x1, Theme.CYAN_BLOCK);
                addExpandedBlock(blocks, startX + 3*90 + 45, startY + 5*90, S_1x1, Theme.ORANGE_BLOCK);
                // Green (5,6)
                addExpandedBlock(blocks, startX + 4*90 + 45, startY + 4*90, S_2x2, Theme.GREEN_BLOCK);

                // Row 4-5 (Verticals Side 2)
                addExpandedBlock(blocks, startX + 0*90 + 45, startY + 5*90, S_2x1, Theme.ORANGE_BLOCK); // Left
                addExpandedBlock(blocks, startX + 6*90 + 45, startY + 5*90, S_2x1, Theme.CYAN_BLOCK); // Right

                // Row 6
                addExpandedBlock(blocks, startX + 0*90 + 45, startY + 8*90, L_TR, Theme.PURPLE_BLOCK);
                addRestrictedExpandedBlock(blocks, startX + 2*90 + 45, startY + 8*90, S_1x2, Theme.PINK_BLOCK, Block.Axis.HORIZONTAL);
                addRestrictedExpandedBlock(blocks, startX + 3*90 + 45, startY + 9*90, S_1x2, Theme.BLUE_BLOCK, Block.Axis.HORIZONTAL);
                addExpandedBlock(blocks, startX + 5*90 + 45, startY + 8*90, L_TL, Theme.YELLOW_BLOCK);
                break;
            }

            default: {
                buildWalls(blocks, gates, Theme.RED_BLOCK, Theme.BLUE_BLOCK, Theme.GREEN_BLOCK, Theme.ORANGE_BLOCK, 180);
                addBlock(blocks, 3, 3, S_2x2, Theme.RED_BLOCK);
                break;
            }
        }
    }

    // Helper to manually add blocks with matrix expansion (1 tile -> 2x2 cells)
    private static void addExpandedBlock(List<Block> blocks, int x, int y, int[][] shapeTiles, Color color) {
        int rows = shapeTiles.length;
        int cols = shapeTiles[0].length;
        int[][] cellMatrix = new int[rows * 2][cols * 2];
        for(int r=0; r<rows; r++) {
            for(int c=0; c<cols; c++) {
                if(shapeTiles[r][c] == 1) {
                    cellMatrix[r*2][c*2] = 1;
                    cellMatrix[r*2][c*2+1] = 1;
                    cellMatrix[r*2+1][c*2] = 1;
                    cellMatrix[r*2+1][c*2+1] = 1;
                }
            }
        }
        blocks.add(new Block(x, y, cellMatrix, color));
    }

    private static void buildWalls(List<Block> blocks, List<ExitGate> gates, 
                                   Color top, Color right, Color bot, Color left, int gateSize) {
        buildTop(blocks, gates, top, gateSize);
        buildRight(blocks, gates, right, gateSize);
        buildBot(blocks, gates, bot, gateSize);
        buildLeft(blocks, gates, left, gateSize);
    }

    private static void buildTop(List<Block> blocks, List<ExitGate> gates, Color color, int gateSize) {
        int w = ARENA_W;
        if (color == null) {
            blocks.add(new Block(START_X, TOP_WALL_Y, w, WALL, createRect(w, WALL, CELL), Theme.WALL_COLOR));
        } else {
            int gx = GRID_X + (GRID_SIZE - gateSize)/2;
            int leftW = gx - START_X;
            int rightW = (START_X + w) - (gx + gateSize);
            blocks.add(new Block(START_X, TOP_WALL_Y, leftW, WALL, createRect(leftW, WALL, CELL), Theme.WALL_COLOR));
            blocks.add(new Block(gx + gateSize, TOP_WALL_Y, rightW, WALL, createRect(rightW, WALL, CELL), Theme.WALL_COLOR));
            gates.add(new ExitGate(gx, TOP_WALL_Y, gateSize, WALL, color, 0));
        }
    }

    private static void buildBot(List<Block> blocks, List<ExitGate> gates, Color color, int gateSize) {
        int w = ARENA_W;
        if (color == null) {
            blocks.add(new Block(START_X, BOT_WALL_Y, w, WALL, createRect(w, WALL, CELL), Theme.WALL_COLOR));
        } else {
            int gx = GRID_X + (GRID_SIZE - gateSize)/2;
            int leftW = gx - START_X;
            int rightW = (START_X + w) - (gx + gateSize);
            blocks.add(new Block(START_X, BOT_WALL_Y, leftW, WALL, createRect(leftW, WALL, CELL), Theme.WALL_COLOR));
            blocks.add(new Block(gx + gateSize, BOT_WALL_Y, rightW, WALL, createRect(rightW, WALL, CELL), Theme.WALL_COLOR));
            gates.add(new ExitGate(gx, BOT_WALL_Y, gateSize, WALL, color, 2));
        }
    }

    private static void buildLeft(List<Block> blocks, List<ExitGate> gates, Color color, int gateSize) {
        int h = GRID_SIZE; 
        if (color == null) {
            blocks.add(new Block(LEFT_WALL_X, GRID_Y, WALL, h, createRect(WALL, h, CELL), Theme.WALL_COLOR));
        } else {
            int gy = GRID_Y + (h - gateSize)/2;
            int topH = gy - GRID_Y;
            int botH = (GRID_Y + h) - (gy + gateSize);
            blocks.add(new Block(LEFT_WALL_X, GRID_Y, WALL, topH, createRect(WALL, topH, CELL), Theme.WALL_COLOR));
            blocks.add(new Block(LEFT_WALL_X, gy + gateSize, WALL, botH, createRect(WALL, botH, CELL), Theme.WALL_COLOR));
            gates.add(new ExitGate(LEFT_WALL_X, gy, WALL, gateSize, color, 3));
        }
    }

    private static void buildRight(List<Block> blocks, List<ExitGate> gates, Color color, int gateSize) {
        int h = GRID_SIZE;
        if (color == null) {
            blocks.add(new Block(RIGHT_WALL_X, GRID_Y, WALL, h, createRect(WALL, h, CELL), Theme.WALL_COLOR));
        } else {
            int gy = GRID_Y + (h - gateSize)/2;
            int topH = gy - GRID_Y;
            int botH = (GRID_Y + h) - (gy + gateSize);
            blocks.add(new Block(RIGHT_WALL_X, GRID_Y, WALL, topH, createRect(WALL, topH, CELL), Theme.WALL_COLOR));
            blocks.add(new Block(RIGHT_WALL_X, gy + gateSize, WALL, botH, createRect(WALL, botH, CELL), Theme.WALL_COLOR));
            gates.add(new ExitGate(RIGHT_WALL_X, gy, WALL, gateSize, color, 1));
        }
    }

    private static void addBlock(List<Block> blocks, int row, int col, int[][] shapeTiles, Color color) {
        int startX = GRID_X + col * TILE;
        int startY = GRID_Y + row * TILE;
        int rows = shapeTiles.length;
        int cols = shapeTiles[0].length;
        int[][] cellMatrix = new int[rows * 2][cols * 2];
        for(int r=0; r<rows; r++) {
            for(int c=0; c<cols; c++) {
                if(shapeTiles[r][c] == 1) {
                    cellMatrix[r*2][c*2] = 1;
                    cellMatrix[r*2][c*2+1] = 1;
                    cellMatrix[r*2+1][c*2] = 1;
                    cellMatrix[r*2+1][c*2+1] = 1;
                }
            }
        }
        blocks.add(new Block(startX, startY, cellMatrix, color));
    }

    private static void addRestrictedExpandedBlock(List<Block> blocks, int x, int y, int[][] shapeTiles, Color color, Block.Axis restriction) {
        int rows = shapeTiles.length;
        int cols = shapeTiles[0].length;
        int[][] cellMatrix = new int[rows * 2][cols * 2];
        for(int r=0; r<rows; r++) {
            for(int c=0; c<cols; c++) {
                if(shapeTiles[r][c] == 1) {
                    cellMatrix[r*2][c*2] = 1;
                    cellMatrix[r*2][c*2+1] = 1;
                    cellMatrix[r*2+1][c*2] = 1;
                    cellMatrix[r*2+1][c*2+1] = 1;
                }
            }
        }
        blocks.add(new Block(x, y, cellMatrix, color, restriction));
    }

    public static int[][] createRect(int w, int h, int cell) {
        int c = (int)Math.ceil((double)w/cell);
        int r = (int)Math.ceil((double)h/cell);
        int[][] m = new int[r][c];
        for(int i=0; i<r; i++) for(int j=0; j<c; j++) m[i][j] = 1;
        return m;
    }
}