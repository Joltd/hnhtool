package com.evgenltd.hnhtool.harvester.core.controller;

import com.evgenltd.hnhtool.harvester.core.entity.Space;
import com.evgenltd.hnhtool.harvester.core.entity.Warehouse;
import com.evgenltd.hnhtool.harvester.core.entity.WarehouseCell;
import com.evgenltd.hnhtool.harvester.core.repository.SpaceRepository;
import com.evgenltd.hnhtool.harvester.core.repository.WarehouseRepository;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/warehouse")
public class WarehouseController {

    private final WarehouseRepository warehouseRepository;
    private final SpaceRepository spaceRepository;

    public WarehouseController(
            final WarehouseRepository warehouseRepository,
            final SpaceRepository spaceRepository
    ) {
        this.warehouseRepository = warehouseRepository;
        this.spaceRepository = spaceRepository;
    }

    @GetMapping
    public Response<List<WarehouseRecord>> list(@RequestParam("space") final Long spaceId) {
        final List<WarehouseRecord> result = warehouseRepository.findBySpaceId(spaceId)
                .stream()
                .map(this::toWarehouseRecord)
                .collect(Collectors.toList());
        return new Response<>(result);
    }

    @GetMapping("/{id}")
    public Response<WarehouseRecord> get(@PathVariable("id") final Long id) {
        return warehouseRepository.findById(id)
                .map(warehouse -> {
                    final WarehouseRecord record = toWarehouseRecord(warehouse);
                    return new Response<>(record);
                })
                .orElseGet(() -> new Response<>("Warehouse [%s] not found", id));
    }

    @PostMapping
    public Response<WarehouseRecord> update(@RequestBody final WarehouseRecord warehouseRecord) {
        final long spaceId = warehouseRecord.spaceId();
        final Optional<Space> spaceHolder = spaceRepository.findById(spaceId);
        if (spaceHolder.isEmpty()) {
            return new Response<>("Space [%s] not found", spaceId);
        }

        final Space space = spaceHolder.get();
        final Long warehouseId = warehouseRecord.id();
        if (warehouseId == null) {
            final Warehouse warehouse = toWarehouse(warehouseRecord, space);
            final Warehouse saved = warehouseRepository.save(warehouse);
            final WarehouseRecord newWarehouseRecord = toWarehouseRecord(saved);
            return new Response<>(newWarehouseRecord);
        }

        final Optional<Warehouse> warehouseHolder = warehouseRepository.findById(warehouseId);
        if (warehouseHolder.isEmpty()) {
            return new Response<>("Warehouse [%s] not found", warehouseId);
        }

        final Warehouse warehouse = warehouseHolder.get();
        warehouse.setSpace(space);

        final Map<IntPoint, WarehouseCell> index = new HashMap<>();

        for (final WarehouseCellRecord warehouseCellRecord : warehouseRecord.cells()) {
            final WarehouseCell warehouseCell = toWarehouseCell(warehouseCellRecord);
            warehouseCell.setWarehouse(warehouse);
            index.put(warehouseCell.getPosition(), warehouseCell);
        }

        for (final WarehouseCell oldWarehouseCell : warehouse.getCells()) {
            final WarehouseCell newWarehouseCell = index.get(oldWarehouseCell.getPosition());
            if (newWarehouseCell != null) {
                newWarehouseCell.setContainer(oldWarehouseCell.getContainer());
            }
        }

        warehouse.getCells().clear();
        warehouse.getCells().addAll(index.values());

        final Warehouse saved = warehouseRepository.save(warehouse);

        return new Response<>(toWarehouseRecord(saved));
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable("id") final Long id) {
        warehouseRepository.deleteById(id);
        return new Response<>();
    }

    private WarehouseRecord toWarehouseRecord(final Warehouse warehouse) {
        final List<WarehouseCellRecord> cells = warehouse.getCells()
                .stream()
                .map(this::toWarehouseCellRecord)
                .collect(Collectors.toList());
        return new WarehouseRecord(
                warehouse.getId(),
                warehouse.getSpace().getId(),
                cells
        );
    }

    private WarehouseCellRecord toWarehouseCellRecord(final WarehouseCell warehouseCell) {
        return new WarehouseCellRecord(
                warehouseCell.getId(),
                warehouseCell.getPosition().getX(),
                warehouseCell.getPosition().getY()
        );
    }

    private Warehouse toWarehouse(final WarehouseRecord warehouseRecord, final Space space) {
        final Warehouse warehouse = new Warehouse();
        warehouse.setSpace(space);
        final Set<WarehouseCell> cells = warehouseRecord.cells()
                .stream()
                .map(this::toWarehouseCell)
                .peek(cell -> cell.setWarehouse(warehouse))
                .collect(Collectors.toSet());
        warehouse.setCells(cells);
        return warehouse;
    }

    private WarehouseCell toWarehouseCell(final WarehouseCellRecord warehouseCellRecord) {
        final WarehouseCell warehouseCell = new WarehouseCell();
        warehouseCell.setPosition(new IntPoint(warehouseCellRecord.x(), warehouseCellRecord.y()));
        return warehouseCell;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    record WarehouseRecord(Long id, Long spaceId, List<WarehouseCellRecord> cells) {}

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    record WarehouseCellRecord(Long id, int x, int y) {}

}
