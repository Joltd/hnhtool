package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.entity.Preferences;
import com.evgenltd.hnhtool.harvester.core.entity.Space;
import com.evgenltd.hnhtool.harvester.core.record.Range;
import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
import com.evgenltd.hnhtool.harvester.core.repository.PreferencesRepository;
import com.evgenltd.hnhtool.harvester.core.repository.SpaceRepository;
import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.entity.IntPoint;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class PreferencesService {

    private final PreferencesRepository preferencesRepository;
    private final SpaceRepository spaceRepository;
    private final KnownObjectRepository knownObjectRepository;

    public PreferencesService(
            final PreferencesRepository preferencesRepository,
            final SpaceRepository spaceRepository,
            final KnownObjectRepository knownObjectRepository
    ) {
        this.preferencesRepository = preferencesRepository;
        this.spaceRepository = spaceRepository;
        this.knownObjectRepository = knownObjectRepository;
    }

    public Preferences get() {
        Optional<Preferences> preferencesHolder = preferencesRepository.find();
        if (preferencesHolder.isEmpty()) {
            spaceRepository.findByType(Space.Type.SURFACE)
                    .stream()
                    .findFirst()
                    .ifPresentOrElse(
                            space -> switchToSpace(space.getId(), null),
                            () -> update(null, new IntPoint(0,0), 1)
                    );

            preferencesHolder = preferencesRepository.find();
        }
        return preferencesHolder.orElseThrow();
    }

    public void update(final Space space, final IntPoint offset, final Integer zoom) {
        final Preferences preferences = preferencesRepository.find()
                .orElse(new Preferences());

        preferences.setSpace(space);
        preferences.setOffset(offset);
        preferences.setZoom(zoom);
        preferencesRepository.save(preferences);
    }


    public void switchToSpace(final Long spaceId, final Long knownObjectId) {
        final Optional<Space> spaceHolder = spaceRepository.findById(spaceId);
        if (spaceHolder.isEmpty()) {
            throw new ApplicationException("Space [%s] not found", spaceId);
        }

        final Space space = spaceHolder.get();
        final Range range = knownObjectRepository.calculateRange(spaceId);

        final IntPoint offset = range.to()
                .sub(range.from())
                .div(2)
                .asIntPoint()
                .add(range.from());

        update(space, offset, 1);
    }

}
