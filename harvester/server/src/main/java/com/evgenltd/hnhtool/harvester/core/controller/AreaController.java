package com.evgenltd.hnhtool.harvester.core.controller;

import com.evgenltd.hnhtool.harvester.core.entity.Area;
import com.evgenltd.hnhtool.harvester.core.record.AreaRecord;
import com.evgenltd.hnhtool.harvester.core.repository.AreaRepository;
import com.evgenltd.hnhtool.harvester.core.service.AreaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/area")
public class AreaController {

    private final AreaRepository areaRepository;
    private final AreaService areaService;

    public AreaController(
            final AreaRepository areaRepository,
            final AreaService areaService
    ) {
        this.areaRepository = areaRepository;
        this.areaService = areaService;
    }

    @GetMapping
    public List<AreaRecord> list(@RequestParam(value = "space", required = false) final Long spaceId) {
        if (spaceId == null) {
            return areaRepository.findAll()
                    .stream()
                    .map(this::toRecord)
                    .collect(Collectors.toList());
        } else {
            return areaRepository.findBySpaceId(spaceId)
                    .stream()
                    .map(this::toRecord)
                    .collect(Collectors.toList());
        }
    }

    @PostMapping
    public AreaRecord update(@RequestBody final AreaRecord areaRecord) {
        final Area area = areaService.update(areaRecord);
        return toRecord(area);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") final Long id) {
        areaRepository.deleteById(id);
    }

    private AreaRecord toRecord(final Area area) {
        return new AreaRecord(
                area.getId(),
                area.getName(),
                area.getSpace().getId(),
                area.getFrom(),
                area.getTo()
        );
    }

}
