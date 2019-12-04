package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.entity.Resource;
import com.evgenltd.hnhtool.harvester.core.entity.Space;
import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
import com.evgenltd.hnhtools.clientapp.Prop;
import com.evgenltd.hnhtools.clientapp.widgets.ItemWidget;
import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.entity.IntPoint;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    private ResourceService resourceService;
    private KnownObjectRepository knownObjectRepository;

    public KnownObjectService(
            final ResourceService resourceService,
            final KnownObjectRepository knownObjectRepository
    ) {
        this.resourceService = resourceService;
        this.knownObjectRepository = knownObjectRepository;
    }

    public KnownObject findById(final Long id) {
        return knownObjectRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("There is no KnownObject for id [%s]", id));
    }

    public Long loadCharacterObjectId(final String characterName) {
        final KnownObject characterObject = knownObjectRepository.findByResourceName(characterName)
                .orElseThrow(() -> new ApplicationException(
                        "There is no KnownObject for character [%s]",
                        characterName
                ));

        final Long id = characterObject.getId();
        if (!characterObject.getResource().isPlayer()) {
            throw new ApplicationException("KnownObject [%s] is not a character", id);
        }

        return id;
    }

    public void storeCharacter(final Long knownObjectId, final Space space, final IntPoint characterPosition) {
        final KnownObject characterObject = findById(knownObjectId);
        characterObject.setSpace(space);
        characterObject.setX(characterPosition.getX());
        characterObject.setY(characterPosition.getY());
    }

    public void storeNewKnownObject(final Space space, final Resource resource, final IntPoint position) {
        final KnownObject knownObject = new KnownObject();
        knownObject.setSpace(space);
        knownObject.setResource(resource);
        knownObject.setX(position.getX());
        knownObject.setY(position.getY());
        knownObject.setLost(false);
        knownObject.setActual(LocalDateTime.now());
        knownObjectRepository.save(knownObject);
    }

    public void storeNewKnownItems(final KnownObject parent, final KnownObject.Place place, final List<ItemWidget> items) {

        final Map<String, Resource> resourceIndex = resourceService.loadResourceIndexByNames(
                items,
                ItemWidget::getResource
        );

        items.forEach(itemWidget -> storeNewKnownItem(
                parent,
                place,
                resourceIndex.get(itemWidget.getResource()),
                itemWidget.getPosition()
        ));

    }

    private void storeNewKnownItem(
            final KnownObject parent,
            final KnownObject.Place place,
            final Resource resource,
            final IntPoint position
    ) {
        final KnownObject knownItem = new KnownObject();
        knownItem.setParent(parent);
        knownItem.setPlace(place);
        knownItem.setResource(resource);
        knownItem.setX(position.getX());
        knownItem.setY(position.getY());
        knownObjectRepository.save(knownItem);
    }

    public void moveToHand(final Long characterId, final Long knownItemId, final String resourceName) {
        final KnownObject character = findById(characterId);
        final KnownObject knownItem = findById(knownItemId);
        final Resource resource = resourceService.findByName(resourceName);
        knownItem.setPlace(KnownObject.Place.HAND);
        knownItem.setX(null);
        knownItem.setY(null);
        knownItem.setParent(character);
        knownItem.setResource(resource);
    }

    public void moveToInventory(final Long knownItemId, final Long parentId, final KnownObject.Place place, final ItemWidget itemWidget) {
        final KnownObject knownItem = findById(knownItemId);
        final KnownObject parent = findById(parentId);
        knownItem.setPlace(place);
        knownItem.setX(itemWidget.getPosition().getX());
        knownItem.setY(itemWidget.getPosition().getY());
        knownItem.setParent(parent);
//        knownItem.setResource(resource);
    }

    public void moveToWorld(final Long knownItemId, final Prop prop) {
        final Resource resource = resourceService.findByName(prop.getResource());
        final KnownObject knownItem = findById(knownItemId);
        knownItem.setPlace(null);
        knownItem.setX(prop.getPosition().getX());
        knownItem.setY(prop.getPosition().getY());
        knownItem.setParent(null);
        knownItem.setResource(resource);
    }

}
