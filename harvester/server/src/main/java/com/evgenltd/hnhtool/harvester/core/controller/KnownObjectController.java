package com.evgenltd.hnhtool.harvester.core.controller;

import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/known-object")
public class KnownObjectController {

    private final KnownObjectRepository knownObjectRepository;

    public KnownObjectController(final KnownObjectRepository knownObjectRepository) {
        this.knownObjectRepository = knownObjectRepository;
    }

    @GetMapping
    public List<KnownObjectRecord> list(
            @RequestParam("spaceId") final Long spaceId,
            @RequestParam("fromX") final Integer fromX,
            @RequestParam("fromY") final Integer fromY,
            @RequestParam("toX") final Integer toX,
            @RequestParam("toY") final Integer toY
    ) {
        return knownObjectRepository.findObjectsInArea(spaceId, fromX, fromY, toX, toY)
                .stream()
                .map(knownObject -> new KnownObjectRecord(knownObject.getPosition(), knownObject.getResource().getName()))
                .collect(Collectors.toList());
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static record KnownObjectRecord(IntPoint position, String resource) {}

}
