package me.itstheholyblack.vigilant_eureka.core;


import me.itstheholyblack.vigilant_eureka.blocks.tiles.LeyLineTile;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class PolySolver {
    public static ArrayList<BlockPos> stackSolve(LeyLineTile te) {
        ArrayList<BlockPos> polygon = new ArrayList<>();
        Stack<BlockPos> stack = new Stack<>();
        HashMap<Integer, BlockPos> visited = new HashMap<>();
        LeyLineTile t = te;
        Integer i = 0;
        while (!t.getLinkOut().equals(BlockPos.ORIGIN)) {
            if (visited.containsValue(t.getPos())) {
                // popping
                // we'll call return here to break from the loop
                while (!stack.peek().equals(t.getPos())) {
                    polygon.add(stack.pop());
                }
                polygon.add(t.getPos());
                return polygon;
            }
            i++;
            stack.push(t.getPos());
            visited.put(i, t.getPos());
            t = (LeyLineTile) t.getWorld().getTileEntity(t.getLinkOut());
        }
        return null;
    }
}
