package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.entity.Area;
import com.evgenltd.hnhtools.entity.IntPoint;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AreaService {

    private static final int GRID_STEP = 1024;

    public List<IntPoint> splitByPositions(final Area area) {
        final IntPoint from = area.getFrom();
        final IntPoint to = area.getTo();

        final List<IntPoint> cells = new ArrayList<>();
        for (int x = round(from.getX()); x < to.getX(); x = x + GRID_STEP) {
            for (int y = round(from.getY()); y < to.getY(); y = y + GRID_STEP) {
                cells.add(new IntPoint(x,y));
            }
        }
        return cells;
    }

    private Integer round(final Integer value) {
        final int offset = GRID_STEP / 2;
        if (value >= 0) {
            return (int) Math.floor((value - offset) / (double) GRID_STEP) * GRID_STEP + offset;
        } else {
            return (int) Math.ceil((value - offset) / (double) GRID_STEP) * GRID_STEP + offset;
        }
    }

}
