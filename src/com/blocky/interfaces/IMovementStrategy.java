package com.blocky.interfaces;

import com.blocky.model.Block;
import java.util.List;

public interface IMovementStrategy {
    boolean isValidMove(Block mover, int newX, int newY, List<Block> others);
}