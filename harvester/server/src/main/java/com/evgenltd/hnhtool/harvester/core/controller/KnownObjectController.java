package com.evgenltd.hnhtool.harvester.core.controller;

import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
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
    public Response<List<KnownObjectRecord>> list(@RequestParam("space") final Long spaceId) {
        final List<KnownObjectRecord> result = knownObjectRepository.findBySpaceId(spaceId)
                .stream()
                .map(knownObject -> new KnownObjectRecord(
                        knownObject.getPosition().getX(),
                        knownObject.getPosition().getY(),
                        knownObject.getResource().getName()
                ))
                .collect(Collectors.toList());
        return new Response<>(result);
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static record KnownObjectRecord(int x, int y, String resource) {}

}
