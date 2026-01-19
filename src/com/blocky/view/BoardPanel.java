package com.blocky.view;

import com.blocky.interfaces.IObserver;
import com.blocky.logic.GameEngine;
import com.blocky.model.Block;
import com.blocky.model.ExitGate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

public class BoardPanel extends JPanel implements IObserver {

    private float scaleFactor = 1.0f;
    private int xOffset = 0;
    private int yOffset = 0;
    
    private List<MenuParticle> menuParticles = new ArrayList<>();
    private Random random = new Random();

    // --- NEW MENU BUTTONS ---
    private Rectangle btnNewGame = new Rectangle(250, 350, 300, 70);
    private Rectangle btnContinue = new Rectangle(250, 440, 300, 70);
    private Rectangle btnLevels = new Rectangle(250, 530, 300, 70);
    private Rectangle btnEditor = new Rectangle(250, 620, 300, 70);
    private Rectangle btnReset = new Rectangle(250, 710, 300, 70);
    private Rectangle btnExit = new Rectangle(250, 800, 300, 70);
    
    private Rectangle btnBack = new Rectangle(300, 750, 200, 60);

    // --- EDITOR BUTTONS ---
    // Layout: Wall(20), Gate(110), Block(200), Undo(300), Redo(390), Save(480), Delete(570), Back(780)
    private Rectangle btnAddWall = new Rectangle(20, 20, 80, 50);
    private Rectangle btnAddGate = new Rectangle(110, 20, 80, 50);
    private Rectangle btnAddBlock = new Rectangle(200, 20, 80, 50);
    
    private Rectangle btnUndo = new Rectangle(300, 20, 70, 50);
    private Rectangle btnRedo = new Rectangle(390, 20, 70, 50);
    
    private Rectangle btnSave = new Rectangle(480, 20, 80, 50);
    private Rectangle btnDelete = new Rectangle(570, 20, 80, 50);
    
    private Rectangle btnEditorBack = new Rectangle(780, 20, 100, 50); 
    
    private Rectangle btnCreateLevel = new Rectangle(250, 650, 300, 70);
    
    // --- HUD BUTTONS ---
    private Rectangle btnHudMenu = new Rectangle(730, 10, 50, 50);
    private Rectangle btnHudRestart = new Rectangle(670, 10, 50, 50);

    // --- MODAL BUTTONS ---
    private Rectangle btnModalResume = new Rectangle(250, 350, 300, 70);
    private Rectangle btnModalMenu = new Rectangle(250, 440, 300, 70);
    private Rectangle btnModalExit = new Rectangle(250, 530, 300, 70);
    
    private Rectangle btnModalEdit = new Rectangle(250, 530, 300, 70); // Replaces Exit position in Custom Mode
    private Rectangle btnModalExitCustom = new Rectangle(250, 620, 300, 70); // Shifted down in Custom Mode
    
    // --- GAME OVER BUTTONS ---
    private Rectangle btnModalRestart = new Rectangle(250, 440, 300, 70);
    private Rectangle btnModalMenuOver = new Rectangle(250, 530, 300, 70);

    // --- LEVEL COMPLETE BUTTONS ---
    private Rectangle btnModalClaim = new Rectangle(230, 550, 160, 60);
    private Rectangle btnModalClaimX2 = new Rectangle(410, 550, 160, 60);
    
    // --- CUSTOM LEVEL COMPLETE BUTTONS ---
    private Rectangle btnCustomPlayAgain = new Rectangle(250, 350, 300, 70);
    private Rectangle btnCustomEdit = new Rectangle(250, 440, 300, 70);
    private Rectangle btnCustomMenu = new Rectangle(250, 530, 300, 70);
    
    private String sliderDragTarget = null;
    
    private String lastSaveMessage = "";
    private long lastSaveTime = 0;

    public BoardPanel() {
        this.setPreferredSize(new Dimension(900, 965));
        this.setBackground(Color.BLACK);
        MouseAdapter ma = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { handleMouseInput(e, "PRESS"); }
            @Override public void mouseDragged(MouseEvent e) { handleMouseInput(e, "DRAG"); }
            @Override public void mouseMoved(MouseEvent e) { handleMouseInput(e, "MOVE"); }
            @Override public void mouseReleased(MouseEvent e) { 
                GameEngine.getInstance().releaseBlock();
                sliderDragTarget = null;
            }
        };
        this.addMouseListener(ma);
        this.addMouseMotionListener(ma);
        GameEngine.getInstance().addObserver(this);
        initMenuParticles();
    }
    
    private void initMenuParticles() {
        for(int i=0; i<25; i++) {
            menuParticles.add(new MenuParticle(
                random.nextInt(1000) - 100,
                random.nextInt(1000) - 100,
                random.nextInt(40) + 20,
                (random.nextFloat() * 1.5f + 0.5f) * (random.nextBoolean() ? 1 : -1),
                new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255), 50)
            ));
        }
    }

    private void handleMouseInput(MouseEvent e, String action) {
        int rawX = e.getX();
        int rawY = e.getY();
        int logicX = (int) ((e.getX() - xOffset) / scaleFactor);
        int logicY = (int) ((e.getY() - yOffset) / scaleFactor);
        GameEngine engine = GameEngine.getInstance();

        if (action.equals("PRESS")) {
            if (engine.state == GameEngine.GameState.MENU) {
                if (btnNewGame.contains(logicX, logicY)) engine.startNewGame();
                else if (btnContinue.contains(logicX, logicY) && engine.hasSave()) engine.continueGame();
                else if (btnLevels.contains(logicX, logicY)) engine.goToLevelSelect();
                else if (btnEditor.contains(logicX, logicY)) engine.startEditor();
                else if (btnExit.contains(logicX, logicY)) System.exit(0);
                else if (btnReset.contains(logicX, logicY)) {
                    int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to reset your progress?", "Confirm Reset", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        engine.resetProgress();
                    }
                }
            } else if (engine.state == GameEngine.GameState.LEVEL_SELECT) {
                for (int i = 1; i <= engine.TOTAL_LEVELS; i++) {
                    int row = (i - 1) / 5; int col = (i - 1) % 5;
                    Rectangle btn = new Rectangle(110 + col * 120, 300 + row * 120, 100, 100);
                    if (btn.contains(logicX, logicY) && i <= engine.maxUnlockedLevel) { 
                        engine.startGame(i); return; 
                    }
                }
                if (btnBack.contains(logicX, logicY)) engine.goToMenu();
            } else if (engine.state == GameEngine.GameState.EDITOR_LEVEL_SELECT) {
                if (btnCreateLevel.contains(logicX, logicY)) engine.createEditorLevel();
                else if (btnBack.contains(logicX, logicY)) engine.goToMenu();
                else {
                    java.util.List<String> levels = engine.getSavedLevels();
                    int startY = 200;
                    int rowH = 60;
                    for (int i = 0; i < levels.size(); i++) {
                        int y = startY + i * (rowH + 10);
                        Rectangle btnPlay = new Rectangle(410, y, 80, rowH);
                        Rectangle btnEdit = new Rectangle(500, y, 80, rowH);
                        Rectangle btnDel = new Rectangle(590, y, 80, rowH);
                        if (btnPlay.contains(logicX, logicY)) { engine.playEditorLevel(levels.get(i)); return; }
                        if (btnEdit.contains(logicX, logicY)) { engine.loadEditorLevel(levels.get(i)); return; }
                        if (btnDel.contains(logicX, logicY)) { 
                            int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the level '" + levels.get(i) + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                            if (response == JOptionPane.YES_OPTION) {
                                engine.deleteSavedLevel(levels.get(i)); 
                            }
                            return; 
                        }
                    }
                }
            } else if (engine.state == GameEngine.GameState.EDITOR) {
                if (engine.placingNewObject) {
                    engine.placingNewObject = false;
                    return;
                }

                // UI Check (Screen Space)
                if (btnAddWall.contains(rawX, rawY)) engine.spawnEditorWall();
                else if (btnAddGate.contains(rawX, rawY)) engine.spawnEditorGate();
                else if (btnAddBlock.contains(rawX, rawY)) engine.spawnEditorBlock();
                else if (btnDelete.contains(rawX, rawY)) engine.deleteSelected();
                else if (btnUndo.contains(rawX, rawY)) engine.undo();
                else if (btnRedo.contains(rawX, rawY)) engine.redo();
                else if (btnSave.contains(rawX, rawY)) {
                    if (engine.currentEditingLevelName != null) {
                        engine.saveLevel(engine.currentEditingLevelName);
                        lastSaveMessage = "Saved: " + engine.currentEditingLevelName;
                        lastSaveTime = System.currentTimeMillis();
                    } else {
                        String name = JOptionPane.showInputDialog(this, "Enter map name:");
                        if (name != null && !name.trim().isEmpty()) {
                            engine.saveLevel(name);
                            lastSaveMessage = "Saved: " + name;
                            lastSaveTime = System.currentTimeMillis();
                        }
                    }
                }
                else if (btnEditorBack.contains(rawX, rawY)) engine.goToMenu();
                else {
                    boolean handled = false;
                    // Check Sliders/Buttons - Logic Space
                    if (engine.selectedBlock != null) {
                        int sx = engine.selectedBlock.getX() + engine.selectedBlock.getWidth() + 20;
                        int sy = engine.selectedBlock.getY();
                        
                        if (engine.selectedBlock.getColor().equals(Theme.WALL_COLOR)) {
                             if (sx + 150 > 900) sx = engine.selectedBlock.getX() - 170;

                             if (new Rectangle(sx, sy, 30, 30).contains(logicX, logicY)) { 
                                 int newW = Math.max(45, engine.selectedBlock.getWidth() - 45);
                                 engine.selectedBlock.shapeMatrix = com.blocky.logic.LevelFactory.createRect(newW, engine.selectedBlock.getHeight(), Block.CELL_SIZE);
                                 engine.selectedBlock.recalculateBounds();
                                 handled = true;
                             }
                             if (new Rectangle(sx + 150, sy, 30, 30).contains(logicX, logicY)) { 
                                 int newW = engine.selectedBlock.getWidth() + 45;
                                 engine.selectedBlock.shapeMatrix = com.blocky.logic.LevelFactory.createRect(newW, engine.selectedBlock.getHeight(), Block.CELL_SIZE);
                                 engine.selectedBlock.recalculateBounds();
                                 handled = true;
                             }
                             if (new Rectangle(sx, sy + 40, 30, 30).contains(logicX, logicY)) { 
                                 int newH = Math.max(45, engine.selectedBlock.getHeight() - 45);
                                 engine.selectedBlock.shapeMatrix = com.blocky.logic.LevelFactory.createRect(engine.selectedBlock.getWidth(), newH, Block.CELL_SIZE);
                                 engine.selectedBlock.recalculateBounds();
                                 handled = true;
                             }
                             if (new Rectangle(sx + 150, sy + 40, 30, 30).contains(logicX, logicY)) { 
                                 int newH = engine.selectedBlock.getHeight() + 45;
                                 engine.selectedBlock.shapeMatrix = com.blocky.logic.LevelFactory.createRect(engine.selectedBlock.getWidth(), newH, Block.CELL_SIZE);
                                 engine.selectedBlock.recalculateBounds();
                                 handled = true;
                             }
                             if (new Rectangle(sx, sy, 100, 30).contains(logicX, logicY)) { sliderDragTarget = "W_BLOCK"; handled = true; } 
                             if (new Rectangle(sx, sy + 40, 100, 30).contains(logicX, logicY)) { sliderDragTarget = "H_BLOCK"; handled = true; }
                        } else {
                             // Block Context: Shape, Color, Restriction
                             int leftX = engine.selectedBlock.getX() - 170;
                             if (leftX < 0) leftX = engine.selectedBlock.getX() + engine.selectedBlock.getWidth() + 20;

                             if (new Rectangle(leftX, sy, 150, 30).contains(logicX, logicY)) {
                                 engine.cycleBlockShape(engine.selectedBlock); handled = true;
                             }
                             if (new Rectangle(leftX, sy + 40, 150, 30).contains(logicX, logicY)) {
                                 engine.cycleBlockColor(engine.selectedBlock); handled = true;
                             }
                             if (new Rectangle(leftX, sy + 80, 150, 30).contains(logicX, logicY)) {
                                 engine.cycleBlockRestriction(engine.selectedBlock); handled = true;
                             }
                        }
                    } else if (engine.selectedGate != null) {
                         Rectangle r = engine.selectedGate.getBounds();
                         int sx = r.x + r.width + 20;
                         int sy = r.y;
                         if (sx + 150 > 900) sx = r.x - 170;

                         // Width Arrows
                         if (new Rectangle(sx, sy, 30, 30).contains(logicX, logicY)) { 
                             engine.selectedGate.setBounds(r.x, r.y, Math.max(45, r.width - 45), r.height); handled = true; 
                         }
                         if (new Rectangle(sx + 150, sy, 30, 30).contains(logicX, logicY)) { 
                             engine.selectedGate.setBounds(r.x, r.y, r.width + 45, r.height); handled = true; 
                         }
                         // Height Arrows (Y + 32)
                         if (new Rectangle(sx, sy + 32, 30, 30).contains(logicX, logicY)) { 
                             engine.selectedGate.setBounds(r.x, r.y, r.width, Math.max(45, r.height - 45)); handled = true; 
                         }
                         if (new Rectangle(sx + 150, sy + 32, 30, 30).contains(logicX, logicY)) { 
                             engine.selectedGate.setBounds(r.x, r.y, r.width, r.height + 45); handled = true; 
                         }
                         // Color Cycle (Y + 64)
                         if (new Rectangle(sx, sy + 64, 150, 30).contains(logicX, logicY)) {
                             engine.cycleGateColor(engine.selectedGate); handled = true;
                         }
                         // Rotate (Y + 96)
                         if (new Rectangle(sx, sy + 96, 150, 30).contains(logicX, logicY)) {
                             engine.cycleGateSide(engine.selectedGate); handled = true;
                         }
                    }

                    if (!handled) {
                        engine.selectEditorObject(logicX, logicY);
                    }
                }
            } else if (engine.state == GameEngine.GameState.PLAYING) {
                if (btnHudMenu.contains(logicX, logicY)) engine.pauseGame();
                else if (btnHudRestart.contains(logicX, logicY)) engine.restartLevel();
                else engine.selectBlock(logicX, logicY);
            } else if (engine.state == GameEngine.GameState.PAUSED) {
                if (btnModalResume.contains(logicX, logicY)) engine.resumeGame();
                else if (btnModalMenu.contains(logicX, logicY)) engine.goToMenu();
                else if (engine.currentLevel == -1) {
                    // Custom Level Logic
                    if (btnModalEdit.contains(logicX, logicY)) engine.loadEditorLevel(engine.currentCustomLevelName);
                    else if (btnModalExitCustom.contains(logicX, logicY)) System.exit(0);
                } else {
                    // Normal Level Logic
                    if (btnModalExit.contains(logicX, logicY)) System.exit(0);
                }
            } else if (engine.state == GameEngine.GameState.LEVEL_COMPLETE) {
                if (btnModalClaim.contains(logicX, logicY)) engine.nextLevel();
                else if (btnModalClaimX2.contains(logicX, logicY)) engine.goToMenu();
            } else if (engine.state == GameEngine.GameState.GAME_COMPLETED) {
                if (btnModalExit.contains(logicX, logicY)) engine.goToMenu();
            } else if (engine.state == GameEngine.GameState.CUSTOM_LEVEL_COMPLETE) {
                if (btnCustomPlayAgain.contains(logicX, logicY)) engine.restartLevel();
                else if (btnCustomEdit.contains(logicX, logicY)) engine.loadEditorLevel(engine.currentCustomLevelName);
                else if (btnCustomMenu.contains(logicX, logicY)) engine.goToMenu();
            } else if (engine.state == GameEngine.GameState.GAME_OVER) {
                if (btnModalRestart.contains(logicX, logicY)) engine.restartLevel();
                else if (btnModalMenuOver.contains(logicX, logicY)) engine.goToMenu();
            }
        } else if (action.equals("DRAG") || action.equals("MOVE")) {
            if (engine.state == GameEngine.GameState.PLAYING && action.equals("DRAG")) {
                engine.dragBlock(logicX, logicY);
            }
            else if (engine.state == GameEngine.GameState.EDITOR) {
                if (engine.placingNewObject) {
                    int snapX = (logicX / 45) * 45;
                    int snapY = (logicY / 45) * 45;
                    if (engine.selectedBlock != null) engine.selectedBlock.move(snapX, snapY);
                    else if (engine.selectedGate != null) engine.selectedGate.setBounds(snapX, snapY, engine.selectedGate.getWidth(), engine.selectedGate.getHeight());
                    repaint();
                } else if (action.equals("DRAG")) {
                    if (engine.selectedBlock != null) {
                        engine.selectedBlock.move((logicX / 45) * 45, (logicY / 45) * 45);
                    } else if (engine.selectedGate != null) {
                        Rectangle r = engine.selectedGate.getBounds();
                        engine.selectedGate.setBounds((logicX / 45) * 45, (logicY / 45) * 45, r.width, r.height);
                    }
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(20, 20, 25));
        g2.fillRect(0, 0, getWidth(), getHeight());

        GameEngine engine = GameEngine.getInstance();
        AffineTransform componentIdentity = g2.getTransform();

        float sx = (float)getWidth() / 1000f;
        float sy = (float)getHeight() / 1075f; 
        scaleFactor = Math.min(sx, sy);
        xOffset = (int)((getWidth() - 800 * scaleFactor) / 2);
        
        if (engine.state == GameEngine.GameState.MENU || engine.state == GameEngine.GameState.LEVEL_SELECT || engine.state == GameEngine.GameState.EDITOR || engine.state == GameEngine.GameState.EDITOR_LEVEL_SELECT) {
            yOffset = (int)((getHeight() - 860 * scaleFactor) / 2);
        } else {
            yOffset = 0; 
        }

        g2.translate(xOffset, yOffset);
        g2.scale(scaleFactor, scaleFactor);

        switch (engine.state) {
            case MENU: drawMenu(g2, engine); break;
            case LEVEL_SELECT: drawLevelSelect(g2, engine); break;
            case EDITOR_LEVEL_SELECT: drawEditorLevelSelect(g2, engine); break;
            case EDITOR: drawEditor(g2, engine, componentIdentity); break;
            case PLAYING: drawGame(g2, engine); break;
            case PAUSED: drawGame(g2, engine); drawPauseModal(g2); break;
            case LEVEL_COMPLETE: drawGame(g2, engine); drawLevelComplete(g2); break;
            case CUSTOM_LEVEL_COMPLETE: drawGame(g2, engine); drawCustomLevelComplete(g2); break;
            case GAME_COMPLETED: drawGame(g2, engine); drawGameCompleted(g2); break;
            case GAME_OVER: drawGame(g2, engine); drawGameOver(g2); break;
        }
        for(Particle p : engine.particles) p.draw(g2);
        g2.setTransform(componentIdentity);
    }

    private void drawGameCompleted(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(-1000, -1000, 4000, 4000);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(150, 250, 500, 400, 40, 40);
        g2.setColor(new Color(50, 200, 100));
        g2.setFont(new Font("Arial", Font.BOLD, 45));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("CONGRATULATIONS!", (800 - fm.stringWidth("CONGRATULATIONS!"))/2, 340);
        g2.setColor(Color.DARK_GRAY);
        g2.setFont(new Font("Arial", Font.BOLD, 22));
        fm = g2.getFontMetrics();
        g2.drawString("You have completed all levels!", (800 - fm.stringWidth("You have completed all levels!"))/2, 410);
        g2.drawString("New maps will be added soon.", (800 - fm.stringWidth("New maps will be added soon."))/2, 450);
        drawModernButton(g2, btnModalExit, "MAIN MENU", Theme.BLUE_BLOCK);
    }

    private void drawLevelComplete(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(-1000, -1000, 4000, 4000);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(200, 300, 400, 350, 40, 40);
        g2.setColor(new Color(180, 50, 250));
        g2.fillRoundRect(220, 260, 360, 80, 20, 20);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("VICTORY!", 220 + (360 - fm.stringWidth("VICTORY!"))/2, 315);
        
        if (GameEngine.getInstance().showReward) {
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.drawString("REWARD", 360, 380);
            g2.setColor(new Color(255, 200, 0));
            g2.fillOval(350, 400, 100, 100);
            g2.setColor(new Color(255, 230, 50));
            g2.fillOval(360, 410, 80, 80);
            g2.setColor(new Color(200, 150, 0));
            g2.setFont(new Font("Arial", Font.BOLD, 40));
            g2.drawString("20", 378, 465);
        } else {
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            String msg = "LEVEL COMPLETED";
            g2.drawString(msg, 220 + (360 - g2.getFontMetrics().stringWidth(msg))/2, 420);
        }

        drawModernButton(g2, btnModalClaim, "Claim", Theme.GREEN_BLOCK);
        drawModernButton(g2, btnModalClaimX2, "GO TO MENU", Theme.BLUE_BLOCK);
    }
    
    private void drawCustomLevelComplete(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(-1000, -1000, 4000, 4000);
        g2.setColor(new Color(40, 40, 45));
        g2.fillRoundRect(200, 250, 400, 400, 30, 30);
        g2.setColor(new Color(60, 60, 65));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(200, 250, 400, 400, 30, 30);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("LEVEL COMPLETE", 200 + (400 - fm.stringWidth("LEVEL COMPLETE")) / 2, 320);
        drawModernButton(g2, btnCustomPlayAgain, "PLAY AGAIN", Theme.GREEN_BLOCK);
        drawModernButton(g2, btnCustomEdit, "EDIT LEVEL", Theme.BLUE_BLOCK);
        drawModernButton(g2, btnCustomMenu, "GO TO MENU", Theme.ORANGE_BLOCK);
    }

    private void drawGameOver(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(-1000, -1000, 4000, 4000);
        
        g2.setColor(new Color(40, 20, 20)); // Reddish tint for failure
        g2.fillRoundRect(200, 300, 400, 350, 30, 30);
        g2.setColor(new Color(80, 40, 40));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(200, 300, 400, 350, 30, 30);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 50));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("TIME'S UP!", 200 + (400 - fm.stringWidth("TIME'S UP!")) / 2, 380);
        
        drawModernButton(g2, btnModalRestart, "TRY AGAIN", Theme.GREEN_BLOCK);
        drawModernButton(g2, btnModalMenuOver, "MAIN MENU", Theme.ORANGE_BLOCK);
    }

    private void drawGame(Graphics2D g2, GameEngine engine) {
        g2.setColor(new Color(25, 25, 30));
        g2.fillRect(-1000, 60, 3000, 2000); 
        g2.setColor(new Color(35, 35, 40));
        for(int i=-1000; i<=2000; i+=60) g2.drawLine(i, -1000, i, 2000);
        for(int i=-1000; i<=2000; i+=60) g2.drawLine(-1000, i, 2000, i);
        for (ExitGate gate : engine.gates) drawGateHole(g2, gate);
        for (Block b : engine.blocks) if (b.getColor().equals(Theme.WALL_COLOR)) drawWallBlock(g2, b);
        for (Block b : engine.blocks) if (!b.getColor().equals(Theme.WALL_COLOR)) draw3DBlock(g2, b);
        for (ExitGate gate : engine.gates) drawGateTeeth(g2, gate);
        g2.setColor(new Color(15, 15, 20));
        g2.fillRect(-1000, 0, 3000, 60); 
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Consolas", Font.BOLD, 30));
        long m = engine.timeRemaining / 60000, s = (engine.timeRemaining / 1000) % 60;
        g2.drawString(String.format("TIME: %02d:%02d", m, s), 20, 40);
        g2.setColor(new Color(255, 200, 0));
        g2.fillOval(300, 15, 35, 35);
        g2.setColor(new Color(255, 230, 50));
        g2.fillOval(305, 20, 25, 25);
        g2.setColor(Color.WHITE);
        g2.drawString("" + engine.coins, 345, 42);
        drawIconButton(g2, btnHudRestart, "RESTART", new Color(200, 150, 50));
        drawIconButton(g2, btnHudMenu, "MENU", new Color(50, 150, 200));
    }

    private void drawPauseModal(Graphics2D g2) {
        GameEngine engine = GameEngine.getInstance();
        boolean isCustom = (engine.currentLevel == -1);
        
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(-1000, -1000, 4000, 4000);
        
        int h = isCustom ? 490 : 400;
        
        g2.setColor(new Color(40, 40, 45));
        g2.fillRoundRect(200, 250, 400, h, 30, 30);
        g2.setColor(new Color(60, 60, 65));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(200, 250, 400, h, 30, 30);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 50));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("PAUSED", 200 + (400 - fm.stringWidth("PAUSED")) / 2, 320);
        
        drawModernButton(g2, btnModalResume, "RESUME", Theme.GREEN_BLOCK);
        drawModernButton(g2, btnModalMenu, "GO TO MENU", Theme.ORANGE_BLOCK);
        
        if (isCustom) {
            drawModernButton(g2, btnModalEdit, "EDIT LEVEL", Theme.BLUE_BLOCK);
            drawModernButton(g2, btnModalExitCustom, "EXIT GAME", Theme.RED_BLOCK);
        } else {
            drawModernButton(g2, btnModalExit, "EXIT GAME", Theme.RED_BLOCK);
        }
    }

    private void drawIconButton(Graphics2D g2, Rectangle r, String type, Color baseColor) {
        g2.setColor(baseColor);
        g2.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        int cx = r.x + r.width/2, cy = r.y + r.height/2;
        if (type.equals("MENU")) {
            g2.drawOval(cx-10, cy-10, 20, 20);
            for(int i=0; i<8; i++) {
                double a = Math.toRadians(i * 45);
                g2.drawLine((int)(cx + 10 * Math.cos(a)), (int)(cy + 10 * Math.sin(a)), (int)(cx + 14 * Math.cos(a)), (int)(cy + 14 * Math.sin(a)));
            }
            g2.drawOval(cx-4, cy-4, 8, 8);
        } else if (type.equals("RESTART")) {
            g2.drawArc(cx-10, cy-10, 20, 20, 45, 270);
            g2.drawLine(cx+4, cy-4, cx+10, cy);
            g2.drawLine(cx+4, cy-4, cx, cy-8);
        }
    }

    private void drawWallBlock(Graphics2D g2, Block b) {
        int w = b.getWidth(), h = b.getHeight();
        g2.setColor(new Color(50, 50, 55)); g2.fillRect(b.getX(), b.getY(), w, h);
        // Border removed to allow seamless corners
        g2.setColor(new Color(30, 30, 35));
        g2.fillOval(b.getX()+5, b.getY()+5, 6, 6); g2.fillOval(b.getX()+w-11, b.getY()+5, 6, 6);
        g2.fillOval(b.getX()+5, b.getY()+h-11, 6, 6); g2.fillOval(b.getX()+w-11, b.getY()+h-11, 6, 6);
    }

    private void drawGateHole(Graphics2D g2, ExitGate gate) { }

    private void drawGateTeeth(Graphics2D g2, ExitGate gate) {
        Rectangle r = gate.getBounds();
        long time = System.currentTimeMillis();
        int offset = (int)((time / 20) % 20);
        g2.setColor(new Color(140, 140, 140));
        Shape oldClip = g2.getClip(); g2.setClip(r);
        if (gate.side == 0 || gate.side == 2) {
            int startX = (r.width % 20) / 2;
            for(int i=-20; i<r.width+20; i+=20) {
                int x1 = r.x + i + offset + startX, x2 = r.x + i - offset + startX;
                g2.fillPolygon(new int[]{x1, x1+10, x1+20}, new int[]{r.y, r.y+20, r.y}, 3);
                g2.fillPolygon(new int[]{x2, x2+10, x2+20}, new int[]{r.y+r.height, r.y+r.height-20, r.y+r.height}, 3);
            }
        } else {
            int startY = (r.height % 20) / 2;
            for(int i=-20; i<r.height+20; i+=20) {
                int y1 = r.y + i + offset + startY, y2 = r.y + i - offset + startY;
                g2.fillPolygon(new int[]{r.x, r.x+20, r.x}, new int[]{y1, y1+10, y1+20}, 3);
                g2.fillPolygon(new int[]{r.x+r.width, r.x+r.width-20, r.x+r.width}, new int[]{y2, y2+10, y2+20}, 3);
            }
        }
        g2.setClip(oldClip);
        g2.setColor(gate.getColor()); g2.setStroke(new BasicStroke(4)); g2.drawRect(r.x, r.y, r.width, r.height);
    }

    private void draw3DBlock(Graphics2D g2, Block block) {
        Shape originalClip = g2.getClip();
        g2.clipRect(0, 60, 2000, 2000);
        AffineTransform old = g2.getTransform();
        g2.translate(block.getX() + block.getWidth()/2, block.getY() + block.getHeight()/2);
        g2.scale(block.scale, block.scale);
        g2.translate(-(block.getX() + block.getWidth()/2), -(block.getY() + block.getHeight()/2));
        int cs = Block.CELL_SIZE;
        Color c = block.isSelected ? block.getColor().brighter() : block.getColor();
        for(int r=0; r<block.shapeMatrix.length; r++) {
            for(int col=0; col<block.shapeMatrix[r].length; col++) {
                if(block.shapeMatrix[r][col] == 1) {
                    int x = block.getX() + col*cs, y = block.getY() + r*cs;
                    g2.setColor(c.darker().darker()); g2.fillRoundRect(x+6, y+6, cs, cs, 10, 10);
                    g2.setColor(c); g2.fillRoundRect(x, y, cs, cs, 10, 10);
                    g2.setColor(new Color(255,255,255,100)); g2.setStroke(new BasicStroke(3)); g2.drawRoundRect(x+2, y+2, cs-4, cs-4, 8, 8);
                }
            }
        }
        if (block.restriction != Block.Axis.NONE) {
            double sx = 0, sy = 0; int cnt = 0;
            for(int r=0; r<block.shapeMatrix.length; r++) for(int col=0; col<block.shapeMatrix[r].length; col++) if(block.shapeMatrix[r][col] == 1) { sx += col; sy += r; cnt++; }
            int cx = block.getX() + (int)((sx/cnt) * cs + cs/2), cy = block.getY() + (int)((sy/cnt) * cs + cs/2);
            g2.setColor(new Color(255, 255, 255, 230)); g2.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            if (block.restriction == Block.Axis.HORIZONTAL) drawArrow(g2, block.getX() + 15, cy, block.getX() + block.getWidth() - 15, cy);
            else if (block.restriction == Block.Axis.VERTICAL) drawArrow(g2, cx, block.getY() + 15, cx, block.getY() + block.getHeight() - 15);
        }
        g2.setTransform(old); g2.setClip(originalClip);
    }

    private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
        g2.drawLine(x1, y1, x2, y2);
        int s = 8; double a = Math.atan2(y2 - y1, x2 - x1);
        g2.drawLine(x2, y2, (int)(x2 - s * Math.cos(a - Math.PI / 6)), (int)(y2 - s * Math.sin(a - Math.PI / 6)));
        g2.drawLine(x2, y2, (int)(x2 - s * Math.cos(a + Math.PI / 6)), (int)(y2 - s * Math.sin(a + Math.PI / 6)));
        g2.drawLine(x1, y1, (int)(x1 + s * Math.cos(a - Math.PI / 6)), (int)(y1 + s * Math.sin(a - Math.PI / 6)));
        g2.drawLine(x1, y1, (int)(x1 + s * Math.cos(a + Math.PI / 6)), (int)(y1 + s * Math.sin(a + Math.PI / 6)));
    }

    private void drawEditor(Graphics2D g2, GameEngine engine, AffineTransform identity) {
        // Draw World (Transformed)
        g2.setColor(new Color(25, 25, 30));
        g2.fillRect(-1000, -1000, 4000, 4000);
        g2.setColor(new Color(40, 40, 50));
        for(int i=-1000; i<=2000; i+=45) g2.drawLine(i, -1000, i, 2000);
        for(int i=-1000; i<=2000; i+=45) g2.drawLine(-1000, i, 2000, i);
        
        // 1. Draw All Objects
        for (ExitGate gate : engine.gates) {
            drawGateTeeth(g2, gate);
            if (gate == engine.selectedGate) {
                g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(2));
                g2.drawRect(gate.getBounds().x - 2, gate.getBounds().y - 2, gate.getBounds().width + 4, gate.getBounds().height + 4);
            }
        }
        for (Block b : engine.blocks) {
            if (b.getColor().equals(Theme.WALL_COLOR)) drawWallBlock(g2, b); else draw3DBlock(g2, b);
            if (b == engine.selectedBlock) {
                g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(2));
                g2.drawRect(b.getX() - 2, b.getY() - 2, b.getWidth() + 4, b.getHeight() + 4);
            }
        }

        // 2. Draw Context Menus (Always on Top)
        if (engine.selectedGate != null) {
            ExitGate gate = engine.selectedGate;
            int sx = gate.getBounds().x + gate.getBounds().width + 20;
            if (sx + 150 > 900) sx = gate.getBounds().x - 170; // Clamp Right

            drawContextSliders(g2, sx, gate.getBounds().y, gate.getBounds().width, gate.getBounds().height);
            // Color Button (Y + 64)
            int sy = gate.getBounds().y + 64;
            drawModernButton(g2, new Rectangle(sx, sy, 150, 30), "COLOR", gate.getColor());
            // Rotate Button (Y + 96)
            drawModernButton(g2, new Rectangle(sx, sy + 32, 150, 30), "ROTATE", Color.GRAY);
        }
        
        if (engine.selectedBlock != null) {
            Block b = engine.selectedBlock;
            int sx = b.getX() + b.getWidth() + 20;
            int sy = b.getY();
            
            if (b.getColor().equals(Theme.WALL_COLOR)) {
                if (sx + 150 > 900) sx = b.getX() - 170; // Clamp Right
                drawContextSliders(g2, sx, sy, b.getWidth(), b.getHeight());
            } else {
                // Block Context: Shape, Color (LEFT SIDE)
                int leftX = b.getX() - 170;
                if (leftX < 0) leftX = b.getX() + b.getWidth() + 20; // Clamp Left
                
                drawModernButton(g2, new Rectangle(leftX, sy, 150, 30), "SHAPE", Color.GRAY);
                drawModernButton(g2, new Rectangle(leftX, sy + 40, 150, 30), "COLOR", b.getColor());
                String restrName = "NONE";
                if(b.restriction == Block.Axis.HORIZONTAL) restrName = "HORIZ";
                if(b.restriction == Block.Axis.VERTICAL) restrName = "VERT";
                drawModernButton(g2, new Rectangle(leftX, sy + 80, 150, 30), restrName, Color.DARK_GRAY);
            }
        }

        // Draw HUD (Screen Space)
        AffineTransform worldTransform = g2.getTransform();
        g2.setTransform(identity);
        
        g2.setColor(new Color(50, 50, 60)); 
        g2.fillRect(0, 0, getWidth(), 80); // Full width background relative to panel

        drawModernButton(g2, btnAddWall, "Wall", Color.GRAY);
        drawModernButton(g2, btnAddGate, "Gate", Theme.BLUE_BLOCK);
        drawModernButton(g2, btnAddBlock, "Block", Theme.GREEN_BLOCK);
        
        drawModernButton(g2, btnUndo, "Undo", Theme.PURPLE_BLOCK);
        drawModernButton(g2, btnRedo, "Redo", Theme.PURPLE_BLOCK);
        
        drawModernButton(g2, btnSave, "Save", Theme.ORANGE_BLOCK);
        drawModernButton(g2, btnDelete, "Delete", Theme.RED_BLOCK);
        
        drawModernButton(g2, btnEditorBack, "< BACK", Color.DARK_GRAY);

        // SAVE NOTIFICATION
        if (System.currentTimeMillis() - lastSaveTime < 2500 && !lastSaveMessage.isEmpty()) {
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRoundRect(getWidth() - 320, getHeight() - 60, 300, 40, 10, 10);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.drawString(lastSaveMessage, getWidth() - 300, getHeight() - 35);
        }

        g2.setTransform(worldTransform); // Restore for any further drawing
    }
    
    private void drawContextSliders(Graphics2D g2, int x, int y, int w, int h) {
        // Width Control
        g2.setColor(Color.DARK_GRAY); g2.fillRect(x, y, 30, 30); // Left Btn
        g2.fillRect(x+150, y, 30, 30); // Right Btn
        g2.setColor(Color.WHITE); 
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("<", x+8, y+22);
        g2.drawString(">", x+158, y+22);
        
        String tW = "W: " + w;
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(tW, x + 30 + (120 - fm.stringWidth(tW))/2, y+22);

        // Height Control (Compact: y + 32)
        int y2 = y + 32;
        g2.setColor(Color.DARK_GRAY); g2.fillRect(x, y2, 30, 30); // Left Btn
        g2.fillRect(x+150, y2, 30, 30); // Right Btn
        g2.setColor(Color.WHITE); 
        g2.drawString("<", x+8, y2+22);
        g2.drawString(">", x+158, y2+22);
        
        String tH = "H: " + h;
        g2.drawString(tH, x + 30 + (120 - fm.stringWidth(tH))/2, y2+22);
    }
    
    private void drawMenu(Graphics2D g2, GameEngine engine) {
        // 1. Modern Background (Radial Gradient)
        java.awt.RadialGradientPaint rgp = new java.awt.RadialGradientPaint(
            new java.awt.geom.Point2D.Float(400, 400), 800,
            new float[]{0.0f, 1.0f},
            new java.awt.Color[]{new java.awt.Color(40, 40, 50), new java.awt.Color(10, 10, 15)}
        );
        g2.setPaint(rgp);
        g2.fillRect(-1000, -1000, 3000, 3000);

        // 2. Subtle Grid Pattern
        g2.setColor(new java.awt.Color(255, 255, 255, 15));
        g2.setStroke(new BasicStroke(1));
        for(int i=-1000; i<=2000; i+=60) {
            g2.drawLine(i, -1000, i, 2000);
            g2.drawLine(-1000, i, 2000, i);
        }
        
        // 3. Animated Particles
        for(MenuParticle p : menuParticles) {
            p.update();
            p.draw(g2);
        }

        // 4. 3D Title "BLOCKY OUT"
        g2.setFont(new Font("Segoe UI", Font.BOLD, 100));
        FontMetrics fm = g2.getFontMetrics();
        String t1 = "BLOCKY";
        String t2 = "OUT";
        int w1 = fm.stringWidth(t1);
        int w2 = fm.stringWidth(t2);
        int centerX = 400; // Approx center of logic logicX range
        int titleY = 180;

        // Draw "BLOCKY"
        int startX1 = (800 - w1) / 2;
        // Shadow/Extrusion
        g2.setColor(new java.awt.Color(20, 20, 20));
        for(int i=1; i<=8; i++) g2.drawString(t1, startX1+i, titleY+i);
        // Main Text
        g2.setColor(Theme.BLUE_BLOCK);
        g2.drawString(t1, startX1, titleY);
        // Highlight
        g2.setColor(new java.awt.Color(255, 255, 255, 50));
        g2.drawString(t1, startX1, titleY);

        // Draw "OUT"
        g2.setFont(new Font("Segoe UI", Font.BOLD, 100)); // Keep size
        int startX2 = (800 - w2) / 2;
        int titleY2 = titleY + 90;
        
        // Shadow/Extrusion for OUT
        g2.setColor(new java.awt.Color(20, 20, 20));
        for(int i=1; i<=8; i++) g2.drawString(t2, startX2+i, titleY2+i);
        // Main Text
        g2.setColor(Theme.ORANGE_BLOCK);
        g2.drawString(t2, startX2, titleY2);

        // 5. Decorative Cube Icon (Floating above/near title)
        int iconX = startX1 + w1 + 20;
        int iconY = titleY - 70;
        g2.setColor(Theme.GREEN_BLOCK.darker());
        g2.fillRoundRect(iconX+5, iconY+5, 60, 60, 10, 10); // Shadow
        g2.setColor(Theme.GREEN_BLOCK);
        g2.fillRoundRect(iconX, iconY, 60, 60, 10, 10);
        g2.setColor(new java.awt.Color(255,255,255,100));
        g2.drawRoundRect(iconX+5, iconY+5, 50, 50, 8, 8); // Highlight

        // 6. Buttons
        drawModernButton(g2, btnNewGame, "NEW GAME", Theme.GREEN_BLOCK);
        
        if (engine.hasSave()) drawModernButton(g2, btnContinue, "CONTINUE", Theme.BLUE_BLOCK);
        else {
            // Disabled state look
            g2.setColor(new java.awt.Color(40, 40, 45)); 
            g2.fillRoundRect(btnContinue.x, btnContinue.y, btnContinue.width, btnContinue.height, 20, 20);
            g2.setColor(new java.awt.Color(255, 255, 255, 20)); 
            g2.drawRoundRect(btnContinue.x, btnContinue.y, btnContinue.width, btnContinue.height, 20, 20);
            
            g2.setColor(java.awt.Color.GRAY.darker()); 
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            fm = g2.getFontMetrics();
            g2.drawString("CONTINUE", btnContinue.x + (btnContinue.width - fm.stringWidth("CONTINUE")) / 2, btnContinue.y + (btnContinue.height + fm.getAscent()) / 2 - 5);
        }
        
        drawModernButton(g2, btnLevels, "LEVELS", Theme.ORANGE_BLOCK);
        drawModernButton(g2, btnEditor, "LEVEL EDITOR", Theme.PURPLE_BLOCK);
        drawModernButton(g2, btnReset, "RESET DATA", java.awt.Color.DARK_GRAY);
        drawModernButton(g2, btnExit, "EXIT", Theme.RED_BLOCK);
    }

    private void drawLevelSelect(Graphics2D g2, GameEngine engine) {
        drawTitle(g2, "LEVELS", 100);
        for(int i=1; i<=engine.TOTAL_LEVELS; i++) {
            int r=(i-1)/5, c=(i-1)%5;
            Rectangle btn = new Rectangle(110+c*120, 300+r*120, 100, 100);
            if (i < engine.maxUnlockedLevel) drawModernButton(g2, btn, ""+i, Theme.GREEN_BLOCK);
            else if (i == engine.maxUnlockedLevel) drawModernButton(g2, btn, ""+i, Theme.ORANGE_BLOCK);
            else {
                g2.setColor(new Color(60, 60, 65)); g2.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 20, 20);
                g2.setColor(Color.GRAY); g2.setFont(new Font("Arial", Font.BOLD, 24));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("LOCK", btn.x + (btn.width - fm.stringWidth("LOCK")) / 2, btn.y + (btn.height + fm.getAscent()) / 2 - 5);
            }
        }
        drawModernButton(g2, btnBack, "BACK", Color.GRAY);
    }

    private void drawEditorLevelSelect(Graphics2D g2, GameEngine engine) {
        drawTitle(g2, "MY LEVELS", 100);
        
        java.util.List<String> levels = engine.getSavedLevels();
        int startY = 200;
        int rowH = 60;
        
        g2.setFont(new Font("Arial", Font.PLAIN, 24));
        FontMetrics fm = g2.getFontMetrics();
        
        for (int i = 0; i < levels.size(); i++) {
            String name = levels.get(i);
            int y = startY + i * (rowH + 10);
            
            // Name BG
            g2.setColor(new Color(60, 60, 70));
            g2.fillRoundRect(150, y, 250, rowH, 10, 10);
            g2.setColor(Color.WHITE);
            g2.drawString(name, 170, y + 40);
            
            // Play Button
            Rectangle btnPlay = new Rectangle(410, y, 80, rowH);
            drawModernButton(g2, btnPlay, "PLAY", Theme.BLUE_BLOCK);

            // Edit Button (Green)
            Rectangle btnEdit = new Rectangle(500, y, 80, rowH);
            drawModernButton(g2, btnEdit, "EDIT", Theme.GREEN_BLOCK);
            
            // Delete Button (Red)
            Rectangle btnDel = new Rectangle(590, y, 80, rowH);
            drawModernButton(g2, btnDel, "DEL", Theme.RED_BLOCK);
        }
        
        drawModernButton(g2, btnCreateLevel, "CREATE NEW", Theme.ORANGE_BLOCK);
        drawModernButton(g2, btnBack, "BACK", Color.GRAY);
    }

    private void drawModernButton(Graphics2D g2, Rectangle r, String t, Color c) {
        g2.setColor(c); g2.fillRoundRect(r.x, r.y, r.width, r.height, 20, 20);
        g2.setColor(new Color(0,0,0,60)); g2.setStroke(new BasicStroke(2)); g2.drawRoundRect(r.x+2, r.y+2, r.width-4, r.height-4, 18, 18);
        g2.setColor(Color.WHITE); g2.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(t, r.x + (r.width - fm.stringWidth(t)) / 2, r.y + (r.height + fm.getAscent()) / 2 - 5);
    }

    private void drawTitle(Graphics2D g2, String t, int y) {
        g2.setFont(new Font("Arial", Font.BOLD, 80)); g2.setColor(Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(t, (800 - fm.stringWidth(t)) / 2, y);
    }

    @Override public void refreshView() { repaint(); }
    
    private class MenuParticle {
        float x, y;
        int size;
        float speed;
        Color color;
        float angle = 0;
        
        public MenuParticle(float x, float y, int size, float speed, Color color) {
            this.x = x; this.y = y; this.size = size; this.speed = speed; this.color = color;
        }
        
        public void update() {
            y -= speed;
            angle += 0.02f;
            if (y < -100) y = 1000;
            if (y > 1000) y = -100;
        }
        
        public void draw(Graphics2D g2) {
            AffineTransform old = g2.getTransform();
            g2.translate(x + size/2, y + size/2);
            g2.rotate(angle);
            g2.translate(-(x + size/2), -(y + size/2));
            g2.setColor(color);
            g2.fillRoundRect((int)x, (int)y, size, size, 10, 10);
            g2.setTransform(old);
        }
    }
}
