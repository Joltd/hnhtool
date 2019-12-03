package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 03-12-2019 21:41</p>
 */
@Service
@Transactional
public class KnownObjectService {

    private KnownObjectRepository knownObjectRepository;

    public KnownObjectService(final KnownObjectRepository knownObjectRepository) {
        this.knownObjectRepository = knownObjectRepository;
    }

    public void moveToHand(final Long knownItemId) {
        final KnownObject knownItem = knownObjectRepository.findByIdOrThrow(knownItemId);
        knownItem.setPlace(KnownObject.Place.HAND);
        knownItem.setX(null);
        knownItem.setY(null);
        knownItem.setParent(null);
    }

}
