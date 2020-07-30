package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.entity.Resource;
import com.evgenltd.hnhtool.harvester.core.entity.Space;
import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
import com.evgenltd.hnhtools.clientapp.Prop;
import com.evgenltd.hnhtools.clientapp.widgets.ItemWidget;
import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.entity.IntPoint;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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

    @NotNull
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

    public KnownObject loadKnownItemFromHeap(final Long heapId) {
        final KnownObject heap = findById(heapId);
        return heap.getChildren()
                .stream()
                .max(Comparator.comparingInt(knownObject -> knownObject.getPosition().getX()))
                .orElseThrow(() -> new ApplicationException("There is no items in Heap [%s]", heapId));
    }

    public void storeCharacter(final Long knownObjectId, final Space space, final IntPoint characterPosition) {
        final KnownObject characterObject = findById(knownObjectId);
        characterObject.setSpace(space);
        characterObject.setPosition(characterPosition);
    }

    public Long storeHeap(final Space space, final IntPoint position, final String resourceName) {
        final Resource resource = resourceService.findByName(resourceName);
        final KnownObject heapObject = new KnownObject();
        heapObject.setSpace(space);
        heapObject.setPosition(position);
        heapObject.setResource(resource);
        heapObject.setLost(false);
        heapObject.setActual(LocalDateTime.now());
        return knownObjectRepository.save(heapObject).getId();
    }

    public void storeNewKnownObject(final Space space, final Resource resource, final IntPoint position) {
        final KnownObject knownObject = new KnownObject();
        knownObject.setSpace(space);
        knownObject.setResource(resource);
        knownObject.setPosition(position);
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

    public KnownObject storeNewKnownItem(final KnownObject parent, final KnownObject.Place place, final ItemWidget item) {
        final Resource resource = resourceService.findByName(item.getResource());
        return storeNewKnownItem(parent, place, resource, item.getPosition());
    }

    private KnownObject storeNewKnownItem(
            final KnownObject parent,
            final KnownObject.Place place,
            final Resource resource,
            final IntPoint position
    ) {
        resource.setVisual(Resource.Visual.WIDGET);
        final KnownObject knownItem = new KnownObject();
        knownItem.setParent(parent);
        knownItem.setPlace(place);
        knownItem.setResource(resource);
        knownItem.setPosition(position);
        knownObjectRepository.save(knownItem);
        return knownItem;
    }

    public void moveToHand(final Long characterId, final Long knownItemId, final String resourceName) {
        final Resource resource = resourceService.findByName(resourceName);
        moveToHand(characterId, knownItemId, resource);
    }

    public void moveToHand(final Long characterId, final Long knownItemId, final Resource resource) {
        final KnownObject character = findById(characterId);
        final KnownObject knownItem = findById(knownItemId);
        knownItem.setPlace(KnownObject.Place.HAND);
        knownItem.setPosition(null);
        knownItem.setParent(character);
        knownItem.setResource(resource);
    }

    public void moveToInventory(final Long knownItemId, final Long parentId, final KnownObject.Place place, final ItemWidget itemWidget) {
        final KnownObject knownItem = findById(knownItemId);
        final KnownObject parent = findById(parentId);
        knownItem.setPlace(place);
        knownItem.setPosition(itemWidget.getPosition());
        knownItem.setParent(parent);
//        knownItem.setResource(resource);
    }

    public void moveToWorld(final Long knownItemId, final Prop prop) {
        final Resource resource = resourceService.findByName(prop.getResource());
        final KnownObject knownItem = findById(knownItemId);
        knownItem.setPlace(null);
        knownItem.setPosition(prop.getPosition());
        knownItem.setParent(null);
        knownItem.setResource(resource);
    }

    public void moveToHeap(final Long knownItemId, final Long heapId, final Integer position) {
        final KnownObject knownItem = findById(knownItemId);
        final KnownObject heap = findById(heapId);
        knownItem.setPlace(null);
        knownItem.setPosition(new IntPoint(position, 0));
        knownItem.setParent(heap);
    }

    public void deleteHeapIfEmpty(final Long heapId) {
        final KnownObject heap = findById(heapId);
        if (heap.getChildren().isEmpty()) {
            knownObjectRepository.delete(heap);
        }
    }

}
