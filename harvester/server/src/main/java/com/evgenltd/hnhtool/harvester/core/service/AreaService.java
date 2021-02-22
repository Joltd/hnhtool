package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.entity.Area;
import com.evgenltd.hnhtool.harvester.core.entity.Space;
import com.evgenltd.hnhtool.harvester.core.record.AreaRecord;
import com.evgenltd.hnhtool.harvester.core.repository.AreaRepository;
import com.evgenltd.hnhtool.harvester.core.repository.SpaceRepository;
import com.evgenltd.hnhtools.entity.IntPoint;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AreaService {

    private static final int GRID_STEP = 1024;

    private final AreaRepository areaRepository;
    private final SpaceRepository spaceRepository;

    public AreaService(
            final AreaRepository areaRepository,
            final SpaceRepository spaceRepository
    ) {
        this.areaRepository = areaRepository;
        this.spaceRepository = spaceRepository;
    }

    public Map<IntPoint, Area> prepareAreaIndex() {
        final List<Area> areas = areaRepository.findAll();
        final Map<IntPoint, Area> index = new HashMap<>();
        for (final Area area : areas) {
            splitByPositions(area).forEach(cell -> index.put(cell, area));
        }
        return index;
    }

    public List<IntPoint> splitByPositions(final Long areaId) {
        final Area area = areaRepository.findOne(areaId);
        return splitByPositions(area);
    }

    private List<IntPoint> splitByPositions(final Area area) {
        final IntPoint from = area.getFrom();
        final IntPoint to = area.getTo();

        final List<IntPoint> cells = new ArrayList<>();
        for (int x = round(from.getX()); x <= to.getX(); x = x + GRID_STEP) {
            for (int y = round(from.getY()); y <= to.getY(); y = y + GRID_STEP) {
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

    public Area update(final AreaRecord areaRecord) {
        final Area area = areaRecord.id() != null
                ? areaRepository.findOne(areaRecord.id())
                : new Area();

        final Space space = spaceRepository.findOne(areaRecord.spaceId());
        area.setName(areaRecord.name());
        area.setSpace(space);
        area.setFrom(new IntPoint(
                Math.min(areaRecord.from().getX(), areaRecord.to().getX()),
                Math.min(areaRecord.from().getY(), areaRecord.to().getY())
        ));
        area.setTo(new IntPoint(
                Math.max(areaRecord.from().getX(), areaRecord.to().getX()),
                Math.max(areaRecord.from().getY(), areaRecord.to().getY())
        ));

        return areaRepository.save(area);
    }

}

