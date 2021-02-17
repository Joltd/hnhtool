package com.evgenltd.hnhtool.harvester.core.controller;

import com.evgenltd.hnhtool.harvester.core.repository.SpaceRepository;
import com.evgenltd.hnhtool.harvester.core.service.PreferencesService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/space")
public class SpaceController {

    private final SpaceRepository spaceRepository;
    private final PreferencesService preferencesService;

    public SpaceController(
            final SpaceRepository spaceRepository,
            final PreferencesService preferencesService
    ) {
        this.spaceRepository = spaceRepository;
        this.preferencesService = preferencesService;
    }

    @GetMapping
    public List<SpaceRecord> list() {
        return spaceRepository.findAll()
                .stream()
                .map(space -> new SpaceRecord(space.getId(), space.getName()))
                .collect(Collectors.toList());
    }

    @PostMapping("/current/{id}")
    public void switchToSpace(@PathVariable("id") final Long id, @RequestParam(value = "knownObjectId", required = false) final Long knownObjectId) {
        preferencesService.switchToSpace(id, knownObjectId);
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    record SpaceRecord(Long id, String name) {}

}
