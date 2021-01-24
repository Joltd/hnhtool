package com.evgenltd.hnhtool.harvester.core.controller;

import com.evgenltd.hnhtool.harvester.core.entity.Preferences;
import com.evgenltd.hnhtool.harvester.core.entity.Space;
import com.evgenltd.hnhtool.harvester.core.repository.SpaceRepository;
import com.evgenltd.hnhtool.harvester.core.service.PreferencesService;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/preferences")
public class PreferencesController {

    private final SpaceRepository spaceRepository;
    private final PreferencesService preferencesService;

    public PreferencesController(
            final SpaceRepository spaceRepository,
            final PreferencesService preferencesService
    ) {
        this.spaceRepository = spaceRepository;
        this.preferencesService = preferencesService;
    }

    @GetMapping
    public PreferencesRecord get() {
        final Preferences preferences = preferencesService.get();
        final Space space = preferences.getSpace();
        return new PreferencesRecord(
                preferences.getId(),
                space != null ? space.getId() : null,
                preferences.getOffset(),
                preferences.getZoom()
        );
    }

    @PostMapping
    public void update(@RequestBody final PreferencesRecord preferencesRecord) {
        final Space space = spaceRepository.findOne(preferencesRecord.space());
        preferencesService.update(
                space,
                preferencesRecord.offset(),
                preferencesRecord.zoom()

        );
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    record PreferencesRecord(Long id, Long space, IntPoint offset, Integer zoom) {}

}
