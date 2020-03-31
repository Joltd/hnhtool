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

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 28-03-2020 19:58</p>
 */
@RestController
@RequestMapping("/knownObject")
public class KnownObjectController {

    private final KnownObjectRepository knownObjectRepository;

    public KnownObjectController(final KnownObjectRepository knownObjectRepository) {
        this.knownObjectRepository = knownObjectRepository;
    }

    @GetMapping
    public List<KnownObject> list(
            @RequestParam("spaceId") final Long spaceId,
            @RequestParam("fromX") final Integer fromX,
            @RequestParam("fromY") final Integer fromY,
            @RequestParam("toX") final Integer toX,
            @RequestParam("toY") final Integer toY
    ) {
        return knownObjectRepository.findObjectsInArea(spaceId, fromX, fromY, toX, toY)
                .stream()
                .map(knownObject -> new KnownObject(knownObject.getPosition(), knownObject.getResource().getName()))
                .collect(Collectors.toList());
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static record KnownObject(IntPoint position, String resource) {}

}
