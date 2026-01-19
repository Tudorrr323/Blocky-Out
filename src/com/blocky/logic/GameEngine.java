package com.blocky.logic;

import com.blocky.interfaces.*;
import com.blocky.model.*;
import com.blocky.view.Particle;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.prefs.Preferences;

public class GameEngine {
    private static GameEngine instance;

    public enum GameState { MENU, LEVEL_SELECT, PLAYING, PAUSED, LEVEL_COMPLETE, GAME_COMPLETED, EDITOR, EDITOR_LEVEL_SELECT, CUSTOM_LEVEL_COMPLETE, GAME_OVER }
    public GameState state = GameState.MENU;
    public boolean placingNewObject = false;

    public List<Block> blocks;
    public List<ExitGate> gates;
    public List<Particle> particles;
    public int currentLevel = 1;
    public final int TOTAL_LEVELS = 12;
    public int coins = 0;
    public long timeRemaining;
    private long lastTimeUpdate;
    public Block selectedBlock = null; 
    public ExitGate selectedGate = null; 
    private int dragOffsetX, dragOffsetY;
    private int startMouseX, startMouseY;
    private int startBlockX, startBlockY;
    private int startGateX, startGateY;
    private int lockedAxis = 0;
    private static final int DRAG_THRESHOLD = 10;
    private IMovementStrategy movementStrategy;
    private List<IObserver> observers;

    // --- PROGRESS & TIMER ---
    public int maxUnlockedLevel = 1;
    public boolean timerStarted = false;
    public boolean showReward = false;
    private Preferences prefs;
    public String currentCustomLevelName; 
    public String currentEditingLevelName; // For overwriting saves
    
    // --- UNDO/REDO ---
    private Stack<String> undoStack = new Stack<>();
    private Stack<String> redoStack = new Stack<>();

    private GameEngine() {
        blocks = new ArrayList<>();
        gates = new ArrayList<>();
        particles = new ArrayList<>();
        observers = new ArrayList<>();
        movementStrategy = new CollisionManager();
        lastTimeUpdate = System.currentTimeMillis();
        prefs = Preferences.userNodeForPackage(GameEngine.class);
        loadProgress();
    }

    public static synchronized GameEngine getInstance() {
        if (instance == null) instance = new GameEngine();
        return instance;
    }

    private void loadProgress() {
        maxUnlockedLevel = prefs.getInt("maxUnlocked", 1);
        coins = prefs.getInt("coins", 0);
    }

    public void saveProgress() {
        prefs.putInt("maxUnlocked", maxUnlockedLevel);
        prefs.putInt("lastPlayedLevel", currentLevel);
        prefs.putInt("coins", coins);
    }

    public void resetProgress() {
        try {
            prefs.clear();
            prefs.flush(); 
        } catch (Exception e) {
            e.printStackTrace();
        }
        maxUnlockedLevel = 1;
        coins = 0;
        currentLevel = 1;
        saveProgress();
        notifyObservers();
    }

    public boolean hasSave() {
        return prefs.getInt("lastPlayedLevel", 1) > 1 || maxUnlockedLevel > 1 || coins > 0;
    }

    public void continueGame() {
        int lastLevel = prefs.getInt("lastPlayedLevel", 1);
        startGame(lastLevel);
    }

    public void startNewGame() {
        startGame(1);
    }

    public void startEditor() {
        this.state = GameState.EDITOR_LEVEL_SELECT;
        notifyObservers();
    }

    public void createEditorLevel() {
        this.blocks.clear();
        this.gates.clear();
        this.particles.clear();
        this.selectedBlock = null;
        this.selectedGate = null;
        this.undoStack.clear();
        this.redoStack.clear();
        this.currentEditingLevelName = null;
        this.state = GameState.EDITOR;
        notifyObservers();
    }
    
    public void captureGateStart() {
        if (selectedGate != null) {
            startGateX = selectedGate.getX();
            startGateY = selectedGate.getY();
        }
    }
    
    public void selectEditorObject(int mouseX, int mouseY) {
        selectedBlock = null;
        selectedGate = null;
        
        // Check Blocks (Reverse order for top-most)
        for (int i = blocks.size() - 1; i >= 0; i--) {
            Block b = blocks.get(i);
            if (b.getBounds().contains(mouseX, mouseY)) {
                selectedBlock = b;
                startBlockX = b.getX();
                startBlockY = b.getY();
                return;
            }
        }
        
        // Check Gates
        for (ExitGate g : gates) {
            if (g.getBounds().contains(mouseX, mouseY)) {
                selectedGate = g;
                startGateX = g.getX();
                startGateY = g.getY();
                return;
            }
        }
    }

    public List<String> getSavedLevels() {
        java.io.File dir = new java.io.File("levels");
        if (!dir.exists()) return new ArrayList<>();
        String[] files = dir.list((d, name) -> name.endsWith(".txt"));
        if (files == null) return new ArrayList<>();
        List<String> levels = new ArrayList<>();
        for (String f : files) levels.add(f.substring(0, f.length() - 4));
        return levels;
    }

    public void deleteSavedLevel(String name) {
        java.io.File file = new java.io.File("levels", name + ".txt");
        if(file.exists()) file.delete();
        notifyObservers();
    }
    
    public void saveState() {
        undoStack.push(serializeState());
        redoStack.clear();
    }

    public void undo() {
        if (undoStack.isEmpty()) return;
        redoStack.push(serializeState());
        restoreState(undoStack.pop());
        notifyObservers();
    }

    public void redo() {
        if (redoStack.isEmpty()) return;
        undoStack.push(serializeState());
        restoreState(redoStack.pop());
        notifyObservers();
    }

    private String serializeState() {
        StringBuilder sb = new StringBuilder();
        for (ExitGate g : gates) {
            sb.append(String.format("GATE %d %d %d %d %d %d %d %d\n", g.getX(), g.getY(), g.getWidth(), g.getHeight(), g.getColor().getRed(), g.getColor().getGreen(), g.getColor().getBlue(), g.side));
        }
        for (Block b : blocks) {
            sb.append(String.format("BLOCK %d %d %d %d %d %d %d %s %d %d", b.getX(), b.getY(), b.getWidth(), b.getHeight(), b.getColor().getRed(), b.getColor().getGreen(), b.getColor().getBlue(), b.restriction, b.shapeMatrix.length, b.shapeMatrix[0].length));
            for(int[] row : b.shapeMatrix) for(int val : row) sb.append(" ").append(val);
            sb.append("\n");
        }
        return sb.toString();
    }

    private void restoreState(String data) {
        blocks.clear();
        gates.clear();
        selectedBlock = null;
        selectedGate = null;
        if (data.trim().isEmpty()) return;
        
        String[] lines = data.split("\n");
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(" ");
            if (parts[0].equals("GATE")) {
                gates.add(new ExitGate(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), new Color(Integer.parseInt(parts[5]), Integer.parseInt(parts[6]), Integer.parseInt(parts[7])), Integer.parseInt(parts[8])));
            } else if (parts[0].equals("BLOCK")) {
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                int w = Integer.parseInt(parts[3]);
                int h = Integer.parseInt(parts[4]);
                Color c = new Color(Integer.parseInt(parts[5]), Integer.parseInt(parts[6]), Integer.parseInt(parts[7]));
                Block.Axis ax = Block.Axis.valueOf(parts[8]);
                int rows = Integer.parseInt(parts[9]);
                int cols = Integer.parseInt(parts[10]);
                int[][] m = new int[rows][cols];
                int idx = 11;
                for(int r=0; r<rows; r++) for(int col=0; col<cols; col++) m[r][col] = Integer.parseInt(parts[idx++]);
                blocks.add(new Block(x, y, w, h, m, c, ax));
            }
        }
    }

    public void saveLevel(String name) {
        try {
            java.io.File dir = new java.io.File("levels");
            if (!dir.exists()) dir.mkdirs();
            java.io.File file = new java.io.File(dir, name + ".txt");
            try (java.io.PrintWriter pw = new java.io.PrintWriter(file)) {
                pw.print(serializeState());
            }
            this.currentEditingLevelName = name;
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void loadEditorLevel(String name) {
        try {
            java.io.File file = new java.io.File("levels", name + ".txt");
            byte[] encoded = java.nio.file.Files.readAllBytes(file.toPath());
            restoreState(new String(encoded, java.nio.charset.StandardCharsets.UTF_8));
            this.currentEditingLevelName = name;
            this.undoStack.clear();
            this.redoStack.clear();
            this.state = GameState.EDITOR;
            notifyObservers();
        } catch(Exception e) { e.printStackTrace(); }
    }
    
    public void playEditorLevel(String name) {
        try {
            java.io.File file = new java.io.File("levels", name + ".txt");
            byte[] encoded = java.nio.file.Files.readAllBytes(file.toPath());
            restoreState(new String(encoded, java.nio.charset.StandardCharsets.UTF_8));
            
            // SHIFT FOR GAMEPLAY ALIGNMENT
            for(Block b : blocks) b.move(b.getX() + 40, b.getY() + 100);
            for(ExitGate g : gates) g.setBounds(g.getX() + 40, g.getY() + 100, g.getWidth(), g.getHeight());
            
            this.currentCustomLevelName = name;
            this.currentLevel = -1; 
            this.timeRemaining = 5 * 60 * 1000;
            this.particles.clear();
            this.lockedAxis = 0;
            this.selectedBlock = null;
            this.timerStarted = false;
            this.state = GameState.PLAYING;
            notifyObservers();
        } catch(Exception e) { e.printStackTrace(); }
    }

    public void spawnEditorWall() {
        saveState();
        Block b = new Block(-100, -100, 45, 45, LevelFactory.createRect(45, 45, Block.CELL_SIZE), com.blocky.view.Theme.WALL_COLOR);
        blocks.add(b);
        selectedBlock = b;
        placingNewObject = true;
        notifyObservers();
    }

    public void spawnEditorGate() {
        saveState();
        ExitGate g = new ExitGate(-100, -100, 90, 45, com.blocky.view.Theme.BLUE_BLOCK, 0);
        gates.add(g);
        selectedGate = g;
        placingNewObject = true;
        notifyObservers();
    }

    public void spawnEditorBlock() {
        saveState();
        int[][] shape = LevelFactory.S_1x1;
        int[][] expanded = new int[shape.length*2][shape[0].length*2];
        for(int r=0; r<shape.length; r++) {
            for(int c=0; c<shape[0].length; c++) {
                if(shape[r][c]==1) {
                    expanded[r*2][c*2]=1; expanded[r*2][c*2+1]=1;
                    expanded[r*2+1][c*2]=1; expanded[r*2+1][c*2+1]=1;
                }
            }
        }
        Block b = new Block(-100, -100, expanded, com.blocky.view.Theme.BLUE_BLOCK);
        blocks.add(b);
        selectedBlock = b;
        placingNewObject = true;
        notifyObservers();
    }

    public void deleteSelected() {
        if (selectedBlock != null || selectedGate != null) {
            saveState();
            if (selectedBlock != null) { blocks.remove(selectedBlock); selectedBlock = null; }
            if (selectedGate != null) { gates.remove(selectedGate); selectedGate = null; }
            notifyObservers();
        }
    }

    public void cycleBlockColor(Block b) {
        saveState();
        Color[] colors = { com.blocky.view.Theme.RED_BLOCK, com.blocky.view.Theme.BLUE_BLOCK, com.blocky.view.Theme.GREEN_BLOCK, com.blocky.view.Theme.ORANGE_BLOCK, com.blocky.view.Theme.YELLOW_BLOCK, com.blocky.view.Theme.CYAN_BLOCK, com.blocky.view.Theme.PURPLE_BLOCK, com.blocky.view.Theme.PINK_BLOCK };
        int idx = 0;
        for(int i=0; i<colors.length; i++) if(colors[i].equals(b.getColor())) { idx = i; break; }
        b.setColor(colors[(idx + 1) % colors.length]);
        notifyObservers();
    }

    public void cycleBlockShape(Block b) {
        saveState();
        int[][][] shapes = {
            LevelFactory.S_1x1, LevelFactory.S_1x2, LevelFactory.S_2x1, LevelFactory.S_2x2,
            LevelFactory.S_1x3, LevelFactory.S_3x1, LevelFactory.S_1x4, LevelFactory.S_4x1, LevelFactory.S_3x3,
            LevelFactory.L_TL, LevelFactory.L_TR, LevelFactory.L_BL, LevelFactory.L_BR, LevelFactory.L_BR3x3,
            LevelFactory.L_TL3x3, LevelFactory.L_TR3x3, LevelFactory.L_BL3x3,
            LevelFactory.CROSS, LevelFactory.U_UP, LevelFactory.U_DOWN
        };
        
        int currentIdx = -1;
        for(int i=0; i<shapes.length; i++) {
            int[][] s = shapes[i];
            int[][] expanded = new int[s.length*2][s[0].length*2];
            for(int r=0; r<s.length; r++) for(int c=0; c<s[0].length; c++) if(s[r][c]==1) {
                expanded[r*2][c*2]=1; expanded[r*2][c*2+1]=1; expanded[r*2+1][c*2]=1; expanded[r*2+1][c*2+1]=1;
            }
            if(java.util.Arrays.deepEquals(b.shapeMatrix, expanded)) {
                currentIdx = i; break;
            }
        }
        
        int nextIdx = (currentIdx + 1) % shapes.length;
        int[][] nextShape = shapes[nextIdx];
        int[][] nextExpanded = new int[nextShape.length*2][nextShape[0].length*2];
        for(int r=0; r<nextShape.length; r++) for(int c=0; c<nextShape[0].length; c++) if(nextShape[r][c]==1) {
            nextExpanded[r*2][c*2]=1; nextExpanded[r*2][c*2+1]=1; nextExpanded[r*2+1][c*2]=1; nextExpanded[r*2+1][c*2+1]=1;
        }
        
        b.shapeMatrix = nextExpanded;
        b.recalculateBounds();
        notifyObservers();
    }

    public void cycleGateColor(ExitGate g) {
        saveState();
        Color[] colors = { com.blocky.view.Theme.RED_BLOCK, com.blocky.view.Theme.BLUE_BLOCK, com.blocky.view.Theme.GREEN_BLOCK, com.blocky.view.Theme.ORANGE_BLOCK, com.blocky.view.Theme.YELLOW_BLOCK, com.blocky.view.Theme.CYAN_BLOCK, com.blocky.view.Theme.PURPLE_BLOCK, com.blocky.view.Theme.PINK_BLOCK };
        int idx = 0;
        for(int i=0; i<colors.length; i++) if(colors[i].equals(g.getColor())) { idx = i; break; }
        g.setColor(colors[(idx + 1) % colors.length]);
        notifyObservers();
    }
    
    public void cycleGateSide(ExitGate g) {
        saveState();
        g.side = (g.side + 1) % 4;
        // Swap dims
        int temp = g.getWidth();
        g.setWidth(g.getHeight());
        g.setHeight(temp);
        notifyObservers();
    }
    
    public void cycleBlockRestriction(Block b) {
        saveState();
        if (b.restriction == Block.Axis.NONE) b.restriction = Block.Axis.HORIZONTAL;
        else if (b.restriction == Block.Axis.HORIZONTAL) b.restriction = Block.Axis.VERTICAL;
        else b.restriction = Block.Axis.NONE;
        notifyObservers();
    }

    public void startGame(int level) {
        this.currentLevel = level;
        this.timeRemaining = 5 * 60 * 1000;
        this.particles.clear();
        this.lockedAxis = 0;
        this.selectedBlock = null;
        this.timerStarted = false;
        loadCurrentLevel();
        this.state = GameState.PLAYING;
        notifyObservers();
    }

    public void goToMenu() { this.state = GameState.MENU; notifyObservers(); }
    public void goToLevelSelect() { this.state = GameState.LEVEL_SELECT; notifyObservers(); }
    public void loadCurrentLevel() { LevelFactory.loadLevel(currentLevel, blocks, gates); }

    public void pauseGame() {
        if (state == GameState.PLAYING) {
            state = GameState.PAUSED;
            notifyObservers();
        }
    }

    public void resumeGame() {
        if (state == GameState.PAUSED) {
            state = GameState.PLAYING;
            notifyObservers();
        }
    }

    public void restartLevel() {
        if (currentLevel == -1 && currentCustomLevelName != null) {
            playEditorLevel(currentCustomLevelName);
        } else {
            startGame(currentLevel);
        }
    }

    public void nextLevel() {
        if (currentLevel <= TOTAL_LEVELS) {
            startGame(currentLevel);
        }
    }

    public void update() {
        long now = System.currentTimeMillis();
        long delta = now - lastTimeUpdate;
        lastTimeUpdate = now;

        if (state == GameState.PAUSED || state == GameState.LEVEL_COMPLETE || state == GameState.CUSTOM_LEVEL_COMPLETE || state == GameState.GAME_OVER) return;

        if (state == GameState.PLAYING) {
            if (timerStarted && timeRemaining > 0) {
                timeRemaining -= delta;
                if (timeRemaining <= 0) {
                    timeRemaining = 0;
                    state = GameState.GAME_OVER;
                    notifyObservers();
                    return;
                }
            }

            if(!particles.isEmpty()) {
                Iterator<Particle> pIt = particles.iterator();
                while (pIt.hasNext()) { if (pIt.next().update()) pIt.remove(); }
            }

            boolean needsCleanup = false;
            for (int i = blocks.size() - 1; i >= 0; i--) {
                Block b = blocks.get(i);
                if (b.isExiting) {
                    animateExit(b);
                    boolean destroyed = false;
                    ExitGate g = b.targetGate;
                    if (g != null) {
                        int bx = b.getX() + b.getWidth()/2;
                        int by = b.getY() + b.getHeight()/2;
                        if (g.side == 0 && by < g.getBounds().y + 40) destroyed = true;
                        else if (g.side == 2 && by > g.getBounds().y + g.getBounds().height - 40) destroyed = true;
                        else if (g.side == 3 && bx < g.getBounds().x + 40) destroyed = true;
                        else if (g.side == 1 && bx > g.getBounds().x + g.getBounds().width - 40) destroyed = true;
                    }
                    if (destroyed) {
                        blocks.remove(i);
                        createExplosion(b.getX() + b.getWidth()/2, b.getY() + b.getHeight()/2, b.getColor());
                        needsCleanup = true;
                    }
                }
            }

            if (blocks.isEmpty() || isLevelComplete()) {
                if (currentLevel == -1) {
                    state = GameState.CUSTOM_LEVEL_COMPLETE;
                } else {
                    boolean isFirstTimeCompletion = (currentLevel == maxUnlockedLevel);
                    
                    if (isFirstTimeCompletion) {
                        coins += 20;
                        showReward = true;
                    } else {
                        showReward = false;
                    }

                    boolean gameFinished = (currentLevel == TOTAL_LEVELS);
                    if (currentLevel >= maxUnlockedLevel) maxUnlockedLevel = currentLevel + 1;
                    if (currentLevel < TOTAL_LEVELS) currentLevel++; else currentLevel = 1;
                    saveProgress();
                    state = gameFinished ? GameState.GAME_COMPLETED : GameState.LEVEL_COMPLETE;
                }
                notifyObservers();
                return;
            }
            if (needsCleanup) notifyObservers();
        }
        notifyObservers();
    }

    private boolean isLevelComplete() {
        for(Block b : blocks) if(!b.getColor().equals(com.blocky.view.Theme.WALL_COLOR)) return false;
        return true;
    }

    private void createExplosion(int x, int y, Color c) {
        for (int i = 0; i < 15; i++) particles.add(new Particle(x, y, c));
    }

    private void animateExit(Block b) {
        ExitGate g = b.targetGate;
        if (g != null) {
            int speed = 8;
            if (g.side == 0) b.move(b.getX(), b.getY() - speed);
            else if (g.side == 2) b.move(b.getX(), b.getY() + speed);
            else if (g.side == 3) b.move(b.getX() - speed, b.getY());
            else if (g.side == 1) b.move(b.getX() + speed, b.getY());
            b.move(b.getX() + (int)(Math.random()*4-2), b.getY() + (int)(Math.random()*4-2));
        }
    }

    public void selectBlock(int mouseX, int mouseY) {
        if (state != GameState.PLAYING) return;
        for (int i = blocks.size() - 1; i >= 0; i--) {
            Block b = blocks.get(i);
            if (b.getColor().equals(com.blocky.view.Theme.WALL_COLOR)) continue;
            if (!b.isExiting && b.containsPoint(mouseX, mouseY)) {
                selectedBlock = b; b.isSelected = true;
                this.startMouseX = mouseX; this.startMouseY = mouseY;
                this.startBlockX = b.getX(); this.startBlockY = b.getY();
                this.dragOffsetX = mouseX - b.getX(); this.dragOffsetY = mouseY - b.getY();
                this.lockedAxis = 0;
                break;
            }
        }
    }

    public void dragBlock(int mouseX, int mouseY) {
        if (state != GameState.PLAYING || selectedBlock == null || selectedBlock.isExiting) return;
        if (!timerStarted) timerStarted = true;
        
        int targetX = mouseX - dragOffsetX;
        int targetY = mouseY - dragOffsetY;
        
        if (selectedBlock.restriction == Block.Axis.VERTICAL) targetX = startBlockX;
        else if (selectedBlock.restriction == Block.Axis.HORIZONTAL) targetY = startBlockY;
        
        List<Block> others = new ArrayList<>();
        for(Block b : blocks) if(b != selectedBlock && !b.isExiting) others.add(b);
        for (ExitGate gate : gates) {
            if (!selectedBlock.getColor().equals(gate.getColor())) {
                Rectangle r = gate.getBounds();
                int[][] gateMatrix = new int[(int)Math.ceil((double)r.height/Block.CELL_SIZE)][(int)Math.ceil((double)r.width/Block.CELL_SIZE)];
                for(int i=0; i<gateMatrix.length; i++) for(int j=0; j<gateMatrix[i].length; j++) gateMatrix[i][j] = 1;
                others.add(new Block(r.x, r.y, r.width, r.height, gateMatrix, com.blocky.view.Theme.WALL_COLOR));
            }
        }

        // CONTINUOUS COLLISION DETECTION (CCD)
        // Instead of jumping directly to targetX/Y, we move in small steps to prevent tunneling through walls.
        int currentX = selectedBlock.getX();
        int currentY = selectedBlock.getY();
        
        double totalDist = Math.hypot(targetX - currentX, targetY - currentY);
        int stepSize = 5; // Check every 5 pixels
        int steps = (int) Math.ceil(totalDist / stepSize);
        
        int lastValidX = currentX;
        int lastValidY = currentY;

        for (int i = 1; i <= steps; i++) {
            double t = (double) i / steps;
            int nextX = (int) (currentX + (targetX - currentX) * t);
            int nextY = (int) (currentY + (targetY - currentY) * t);
            
            if (movementStrategy.isValidMove(selectedBlock, nextX, nextY, others)) {
                lastValidX = nextX;
                lastValidY = nextY;
            } else {
                // Hit a wall/obstacle, stop here.
                break;
            }
        }
        
        // Move to the furthest valid position found
        if (lastValidX != currentX || lastValidY != currentY) {
            selectedBlock.move(lastValidX, lastValidY);
            checkGateInteraction();
        }
    }

    public void releaseBlock() {
        if (selectedBlock != null) {
            int cellSize = Block.CELL_SIZE;
            int snapX, snapY;
            
            if (state == GameState.EDITOR) {
                snapX = Math.round((float)selectedBlock.getX() / cellSize) * cellSize;
                snapY = Math.round((float)selectedBlock.getY() / cellSize) * cellSize;
                
                // UNDO LOGIC: If moved, save PREVIOUS state
                if (snapX != startBlockX || snapY != startBlockY) {
                    int curX = selectedBlock.getX();
                    int curY = selectedBlock.getY();
                    // Revert to start to save state
                    selectedBlock.move(startBlockX, startBlockY);
                    saveState();
                    // Restore current (to be snapped)
                    selectedBlock.move(curX, curY);
                }
            } else {
                int gridOffsetX = 40;
                int gridOffsetY = 100;
                int col = Math.round((float)(selectedBlock.getX() - gridOffsetX) / cellSize);
                int row = Math.round((float)(selectedBlock.getY() - gridOffsetY) / cellSize);
                snapX = gridOffsetX + col * cellSize;
                snapY = gridOffsetY + row * cellSize;
            }

            List<Block> others = new ArrayList<>();
            for(Block b : blocks) if(b != selectedBlock && !b.isExiting) others.add(b);
            
            if (movementStrategy.isValidMove(selectedBlock, snapX, snapY, others)) selectedBlock.move(snapX, snapY);
            
            if (state != GameState.EDITOR) {
                selectedBlock.isSelected = false;
                selectedBlock = null;
            }
        }
        
        if (selectedGate != null && state == GameState.EDITOR) {
             if (selectedGate.getX() != startGateX || selectedGate.getY() != startGateY) {
                 int curX = selectedGate.getX(); 
                 int curY = selectedGate.getY();
                 selectedGate.setBounds(startGateX, startGateY, selectedGate.getWidth(), selectedGate.getHeight());
                 saveState();
                 selectedGate.setBounds(curX, curY, selectedGate.getWidth(), selectedGate.getHeight());
             }
        }
        
        if (state != GameState.EDITOR) selectedBlock = null;
    }

    private void checkGateInteraction() {
        if (selectedBlock == null) return;
        int tolerance = 30;
        for (ExitGate gate : gates) {
            if (selectedBlock.getColor().equals(gate.getColor()) && selectedBlock.getBounds().intersects(gate.getBounds())) {
                boolean sideIsVertical = (gate.side == 0 || gate.side == 2);
                if (selectedBlock.restriction == Block.Axis.VERTICAL && !sideIsVertical) continue;
                if (selectedBlock.restriction == Block.Axis.HORIZONTAL && sideIsVertical) continue;
                boolean aligned = false;
                Rectangle bRect = selectedBlock.getBounds();
                Rectangle gRect = gate.getBounds();
                if (sideIsVertical) { 
                    if (bRect.x >= gRect.x - tolerance && bRect.x + bRect.width <= gRect.x + gRect.width + tolerance) aligned = true;
                } else { 
                    if (bRect.y >= gRect.y - tolerance && bRect.y + bRect.height <= gRect.y + gRect.height + tolerance) aligned = true;
                }
                if (aligned) {
                    selectedBlock.isExiting = true;
                    selectedBlock.targetGate = gate;
                    selectedBlock.isSelected = false;
                    selectedBlock = null;
                    break;
                }
            }
        }
    }
    public void addObserver(IObserver o) { observers.add(o); }
    private void notifyObservers() { for (IObserver o : observers) o.refreshView(); }
}
