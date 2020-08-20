package com.evgenltd.hnhtool.harvester.core.controller;

import com.evgenltd.hnhtool.harvester.core.entity.Area;
import com.evgenltd.hnhtool.harvester.core.entity.Space;
import com.evgenltd.hnhtool.harvester.core.repository.AreaRepository;
import com.evgenltd.hnhtool.harvester.core.repository.SpaceRepository;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/area")
public class AreaController {

    private final AreaRepository areaRepository;
    private final SpaceRepository spaceRepository;

    public AreaController(
            final AreaRepository areaRepository,
            final SpaceRepository spaceRepository
    ) {
        this.areaRepository = areaRepository;
        this.spaceRepository = spaceRepository;
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
        Area area = areaRecord.id() == null
                ? new Area()
                : areaRepository.getOne(areaRecord.id());
        final Space space = spaceRepository.getOne(areaRecord.spaceId());
        area.setSpace(space);
        area.setFrom(areaRecord.from());
        area.setTo(areaRecord.to());
        areaRepository.save(area);
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

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static final record AreaRecord(Long id, Long spaceId, IntPoint from, IntPoint to) {}

}
