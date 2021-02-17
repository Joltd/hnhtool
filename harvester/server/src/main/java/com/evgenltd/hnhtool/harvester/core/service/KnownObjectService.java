package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.entity.Area;
import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.entity.Resource;
import com.evgenltd.hnhtool.harvester.core.entity.Space;
import com.evgenltd.hnhtool.harvester.core.repository.AreaRepository;
import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
import com.evgenltd.hnhtool.harvester.core.repository.SpaceRepository;
import com.evgenltd.hnhtools.clientapp.Prop;
import com.evgenltd.hnhtools.clientapp.widgets.ItemWidget;
import com.evgenltd.hnhtools.entity.IntPoint;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class KnownObjectService {

    private final ResourceService resourceService;
    private final SpaceRepository spaceRepository;
    private final KnownObjectRepository knownObjectRepository;
    private final AreaRepository areaRepository;

    public KnownObjectService(
            final ResourceService resourceService,
            final SpaceRepository spaceRepository,
            final KnownObjectRepository knownObjectRepository,
            final AreaRepository areaRepository
    ) {
        this.resourceService = resourceService;
        this.spaceRepository = spaceRepository;
        this.knownObjectRepository = knownObjectRepository;
        this.areaRepository = areaRepository;
    }

    public KnownObject loadCharacterObject(final String characterName) {
        return knownObjectRepository.findByResourceName(characterName)
                .orElseGet(() -> {
                    final Resource resource = resourceService.findByName(characterName);
                    final KnownObject player = new KnownObject();
                    player.setResource(resource);
                    return knownObjectRepository.save(player);
                });
    }

    public void storeCharacter(final Long knownObjectId, final Long spaceId, final IntPoint characterPosition) {
        final Space space = spaceRepository.findOne(spaceId);
        final KnownObject characterObject = knownObjectRepository.findOne(knownObjectId);
        characterObject.setSpace(space);
        characterObject.setPosition(characterPosition);
    }

    public KnownObject storeHeap(final Long spaceId, final IntPoint position, final String resourceName) {
        final Space space = spaceRepository.findOne(spaceId);
        final Resource resource = resourceService.findByName(resourceName);
        final KnownObject heapObject = new KnownObject();
        heapObject.setSpace(space);
        heapObject.setPosition(position);
        heapObject.setResource(resource);
        heapObject.setLost(false);
        heapObject.setActual(LocalDateTime.now());
        return knownObjectRepository.save(heapObject);
    }

    public void storeNewKnownObject(final Long spaceId, final Resource resource, final IntPoint position) {
        final Space space = spaceRepository.findOne(spaceId);
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
        final KnownObject character = knownObjectRepository.findOne(characterId);
        final KnownObject knownItem = knownObjectRepository.findOne(knownItemId);
        moveToHand(character, knownItem, resourceName);
    }

    public void moveToHand(final KnownObject character, final KnownObject knownItem, final String resourceName) {
        final Resource resource = resourceService.findByName(resourceName);
        knownItem.setPlace(KnownObject.Place.HAND);
        knownItem.setPosition(null);
        knownItem.setParent(character);
        knownItem.setResource(resource);
    }

    public void moveToInventory(final Long knownItemId, final Long parentId, final KnownObject.Place place, final ItemWidget itemWidget) {
        final KnownObject knownItem = knownObjectRepository.findOne(knownItemId);
        final KnownObject parent = knownObjectRepository.findOne(parentId);
        knownItem.setPlace(place);
        knownItem.setPosition(itemWidget.getPosition());
        knownItem.setParent(parent);
//        knownItem.setResource(resource);
    }

    public void moveToWorld(final Long knownItemId, final Prop prop) {
        final Resource resource = resourceService.findByName(prop.getResource());
        final KnownObject knownItem = knownObjectRepository.findOne(knownItemId);
        knownItem.setPlace(null);
        knownItem.setPosition(prop.getPosition());
        knownItem.setParent(null);
        knownItem.setResource(resource);
    }

    public void moveToHeap(final Long knownItemId, final Long heapId, final Integer position) {
        final KnownObject knownItem = knownObjectRepository.findOne(knownItemId);
        final KnownObject heap = knownObjectRepository.findOne(heapId);
        knownItem.setPlace(null);
        knownItem.setPosition(new IntPoint(position, 0));
        knownItem.setParent(heap);
    }

    public void markHeapAsInvalid(final KnownObject heap) {
        for (Iterator<KnownObject> iterator = heap.getChildren().iterator(); iterator.hasNext(); ) {
            final KnownObject item = iterator.next();
            item.setParent(null);
            knownObjectRepository.delete(item);
            iterator.remove();
        }
        heap.setInvalid(true);
    }

    public void deleteHeap(final KnownObject heap) {
        knownObjectRepository.delete(heap);
    }

    public List<KnownObject> loadContainersInArea(final Long areaId) {
        final Area area = areaRepository.findOne(areaId);
        return knownObjectRepository
                .findContainerInArea(
                        area.getSpace().getId(),
                        area.getFrom().getX(),
                        area.getFrom().getY(),
                        area.getTo().getX(),
                        area.getTo().getY()
                );
    }

}
