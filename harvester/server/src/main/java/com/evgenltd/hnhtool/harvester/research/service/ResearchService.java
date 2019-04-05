package com.evgenltd.hnhtool.harvester.research.service;

import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.entity.Resource;
import com.evgenltd.hnhtool.harvester.common.entity.Space;
import com.evgenltd.hnhtool.harvester.common.repository.KnownObjectRepository;
import com.evgenltd.hnhtool.harvester.common.repository.SpaceRepository;
import com.evgenltd.hnhtool.harvester.common.service.Module;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.evgenltd.hnhtool.harvester.common.ResourceConstants.*;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 04-04-2019 00:57</p>
 */
@Service
public class ResearchService implements Module {

    private SpaceRepository spaceRepository;
    private KnownObjectRepository knownObjectRepository;

    public ResearchService(
            final SpaceRepository spaceRepository,
            final KnownObjectRepository knownObjectRepository
    ) {
        this.spaceRepository = spaceRepository;
        this.knownObjectRepository = knownObjectRepository;
    }

    @Scheduled(fixedDelay = 10_000L)
    public void main() {
        final List<KnownObject> unknownDoorways = knownObjectRepository.findUnknownDoorways();
        if (unknownDoorways.isEmpty()) {
            return;
        }

        for (final KnownObject unknownDoorway : unknownDoorways) {
            final Resource resource = unknownDoorway.getResource();
            final Space spaceTo = buildNewSpace(resource.getId());
            unknownDoorway.setConnectedSpace(spaceTo);


        }
        // schedule tasks for researching
    }

    private Space buildNewSpace(final Long resourceId) {
        final Space space = new Space();
        if (isDoorwayToBuilding(resourceId)) {
            space.setType(Space.Type.BUILDING);
        } else if (isDoorwayToHole(resourceId)) {
            space.setType(Space.Type.HOLE);
        } else if (isDoorwayToMine(resourceId)) {
            space.setType(Space.Type.MINE);
        } else {
            space.setType(Space.Type.SURFACE);
        }
        space.setName(space.getType().name());
        return spaceRepository.save(space);
    }



}
