package me.itstheholyblack.vigilant_eureka.core;


import me.itstheholyblack.vigilant_eureka.blocks.tiles.LeyLineTile;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class PolySolver {
    public ArrayList<BlockPos> solve(LeyLineTile te) {
        ArrayList<BlockPos> polygon = new ArrayList<>(20);
        polygon.add(te.getPos()); // add start position
        int i = 0;
        BlockPos next = te.getLinkOutAtIndex(0); // get the first link out from the start
        while (true) {
            if (polygon.contains(next)) {
                if (next.equals(te.getPos())) {
                    // Somehow we didn't get this returned before looping back.
                    return polygon;
                } else {
                    return null; // wheel with a spoke
                }
            } else if (i < 19) {
                polygon.add(next); // add the next link
                i++;
                if (te.getWorld().isAreaLoaded(next, 10)) { // check if area is loaded before moving on
                    LeyLineTile currentTile = (LeyLineTile) te.getWorld().getTileEntity(next); // get the tile at next
                    if (currentTile.numberOut() < 1) {
                        return null; // no polygon exists
                    } else {
                        BlockPos p = currentTile.getLinkOutAtIndex(0);
                        if (!p.equals(te.getPos())) {
                            next = p; // set next to the link out, only if it isn't the start
                        } else {
                            return polygon; // hit start, poly complete. return it.
                        }
                    }
                }
            } else {
                return null; // your polygon is too big!
            }
        }
    }
}
