package com.evgenltd.hnhtool.harvester.core.controller;

import com.evgenltd.hnhtool.harvester.core.entity.Preferences;
import com.evgenltd.hnhtool.harvester.core.entity.Space;
import com.evgenltd.hnhtool.harvester.core.repository.SpaceRepository;
import com.evgenltd.hnhtool.harvester.core.service.PreferencesService;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
    public Response<PreferencesRecord> get() {
        final Preferences preferences = preferencesService.get();
        final Space space = preferences.getSpace();
        return new Response<>(new PreferencesRecord(
                preferences.getId(),
                space != null ? space.getId() : null,
                preferences.getOffset(),
                preferences.getZoom()
        ));
    }

    @PostMapping
    public Response<Void> update(@RequestBody final PreferencesRecord preferencesRecord) {
        final Optional<Space> spaceHolder = spaceRepository.findById(preferencesRecord.space());
        if (spaceHolder.isEmpty()) {
            return new Response<>("Space [%s] not found", preferencesRecord.space());
        }
        preferencesService.update(
                spaceHolder.get(),
                preferencesRecord.offset(),
                preferencesRecord.zoom()

        );
        return new Response<>();
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    record PreferencesRecord(Long id, Long space, IntPoint offset, Integer zoom) {}

}
