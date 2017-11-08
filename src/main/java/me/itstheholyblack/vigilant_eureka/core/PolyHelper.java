package me.itstheholyblack.vigilant_eureka.core;


import me.itstheholyblack.vigilant_eureka.blocks.tiles.LeyLineTile;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class PolyHelper {
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

    /**
     * Return true if the given point is contained inside the boundary.
     * See: http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
     *
     * @param test The point to check
     * @return true if the point is inside the boundary, false otherwise
     */
    public static boolean contains(BlockPos test, ArrayList<BlockPos> points) {
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = points.size() - 1; i < points.size(); j = i++) {
            if ((points.get(i).getZ() > test.getZ()) != (points.get(j).getZ() > test.getZ()) &&
                    (test.getX() < (points.get(j).getX() - points.get(i).getX()) * (test.getZ() - points.get(i).getZ()) / (points.get(j).getZ() - points.get(i).getZ()) + points.get(i).getX())) {
                result = !result;
            }
        }
        return result;
    }
}
