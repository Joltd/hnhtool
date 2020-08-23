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
    public Response<List<AreaRecord>> list(@RequestParam("space") final Long spaceId) {
        final List<AreaRecord> result = areaRepository.findBySpaceId(spaceId)
                .stream()
                .map(this::toRecord)
                .collect(Collectors.toList());
        return new Response<>(result);
    }

    @PostMapping
    public Response<AreaRecord> update(@RequestBody final AreaRecord areaRecord) {
        final Area area = areaService.update(areaRecord);
        return new Response<>(toRecord(area));
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable("id") final Long id) {
        areaRepository.deleteById(id);
        return new Response<>();
    }

    private AreaRecord toRecord(final Area area) {
        return new AreaRecord(
                area.getId(),
                area.getSpace().getId(),
                area.getFrom(),
                area.getTo()
        );
    }

}
