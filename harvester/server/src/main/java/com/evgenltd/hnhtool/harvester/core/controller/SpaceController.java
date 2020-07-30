package com.evgenltd.hnhtool.harvester.core.controller;

import com.evgenltd.hnhtool.harvester.core.entity.Space;
import com.evgenltd.hnhtool.harvester.core.repository.SpaceRepository;
import com.evgenltd.hnhtool.harvester.core.service.PreferencesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<Space> list() {
        return spaceRepository.findAll();
    }

    @PostMapping("/current/{id}")
    public void switchToSpace(@PathVariable("id") final Long id, @RequestParam(value = "knownObjectId", required = false) final Long knownObjectId) {
        preferencesService.switchToSpace(id, knownObjectId);
    }

}
