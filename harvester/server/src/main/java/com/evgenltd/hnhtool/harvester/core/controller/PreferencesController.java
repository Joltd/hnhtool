package com.evgenltd.hnhtool.harvester.core.controller;

import com.evgenltd.hnhtool.harvester.core.entity.Preferences;
import com.evgenltd.hnhtool.harvester.core.entity.Space;
import com.evgenltd.hnhtool.harvester.core.service.PreferencesService;
import com.evgenltd.hnhtools.entity.IntPoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/preferences")
public class PreferencesController {

    private final PreferencesService preferencesService;

    public PreferencesController(
            final PreferencesService preferencesService
    ) {
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

    record PreferencesRecord(Long id, Long spaceId, IntPoint offset, Integer zoom) {}

}
