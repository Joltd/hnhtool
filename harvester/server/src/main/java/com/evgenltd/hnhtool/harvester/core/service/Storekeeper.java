package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.Agent;
import com.evgenltd.hnhtool.harvester.core.component.Holder;
import com.evgenltd.hnhtool.harvester.core.component.storekeeper.Warehousing;
import com.evgenltd.hnhtool.harvester.core.entity.Area;
import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.entity.Resource;
import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
import com.evgenltd.hnhtools.entity.IntPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Storekeeper {

    private Agent agent;

    private final KnownObjectRepository knownObjectRepository;
    private final AreaService areaService;

    public Storekeeper(
            final KnownObjectRepository knownObjectRepository,
            final AreaService areaService
    ) {
        this.knownObjectRepository = knownObjectRepository;
        this.areaService = areaService;
    }

    public void setAgent(final Agent agent) {
        this.agent = agent;
    }

    public boolean store(final Area area, final KnownObject item) {
        return store(area, item);
    }

    private boolean store(final Area area, final KnownObject item, final Long... exclude) {

        final List<Long> containerExclude = Arrays.asList(exclude);
        final List<IntPoint> cells = areaService.splitByPositions(area.getId());

        while (true) {

            final List<KnownObject> containers = knownObjectRepository
                    .findObjectsInArea(
                            area.getSpace().getId(),
                            area.getFrom().getX(),
                            area.getFrom().getY(),
                            area.getTo().getX(),
                            area.getTo().getY()
                    )
                    .stream()
                    .filter(knownObject -> knownObject.getResource().isBox() || knownObject.getResource().isHeap())
                    .filter(knownObject -> !knownObject.getInvalid())
                    .filter(knownObject -> !containerExclude.contains(knownObject.getId()))
                    .collect(Collectors.toList());

            if (containers.isEmpty()) {
                return false;
            }

            final Warehousing.Result solution = new Warehousing().solve(containers, cells, item);
            if (solution.skipped()) {
                return false;
            }

            if (solution.heapEntry() != null) {
                final Holder<KnownObject> heap = solution.heapEntry().heap();
                // move by route
                if (heap.get().getId() == null) {
                    agent.takeItemInHandFromInventory(item.getId());
                    final Long heapId = agent.placeHeap(heap.get().getPosition());
                    final KnownObject newHeap = knownObjectRepository.getOne(heapId);
                    heap.set(newHeap);
                    return true;
                }

                agent.openHeap(heap.get().getId());
                agent.takeItemInHandFromInventory(item.getId());
                agent.dropItemFromHandInCurrentHeap();
                return true;
            }

            if (solution.boxEntry() != null) {
                final KnownObject container = solution.boxEntry().container();
                final IntPoint position = solution.boxEntry().position();
                // move by route
                agent.openContainer(container); // may have different item positions
                agent.takeItemInHandFromInventory(item.getId());
                agent.dropItemFromHandInCurrentInventory(position);
                return true;
            }
        }
    }

    public boolean take(final Area area, final KnownObject knownItem) {
        final KnownObject character = knownObjectRepository.getOne(agent.getCharacter().knownObjectId());
        final KnownObject container = knownItem.getParent();
        final Resource resource = container.getResource();
        if (resource.isBox()) {
            agent.openContainer(container);
            final Warehousing.BoxEntry solution = new Warehousing().solve(character, knownItem);
            if (solution == null) {
                return false;
            }
            agent.takeItemInHandFromInventory(knownItem.getId());
            agent.dropItemFromHandInMainInventory(solution.position());
            return true;
        }

        if (resource.isHeap()) {
            return takeFromHeap(area, knownItem);
        }

        return false;

    }

    private boolean takeFromHeap(final Area area, final KnownObject targetItem) {
        final KnownObject heap = targetItem.getParent();
        final IntPoint position = heap.getPosition();
        Long heapId = heap.getId();

        agent.openHeap(heapId);
        if (isBecameInvalid(heapId)) {
            return false;
        }

        final List<KnownObject> otherItems = new ArrayList<>();

        while (true) {
            agent.takeItemInHandFromCurrentHeap();
            if (isBecameInvalid(heapId)) {
                heapId = returnItemInHandBackToHeap(heapId, position);
                returnItemsBackToHeap(otherItems, heapId);
                return false;
            }

            final KnownObject itemInHand = getItemInHand();

            final Warehousing.BoxEntry solution = new Warehousing().solve(getCharacter(), itemInHand);
            if (solution == null) { // may inv filled up
                heapId = returnItemInHandBackToHeap(heapId, position);
                if (!otherItems.isEmpty()) {
                    final boolean result = storeItemsInOtherHeap(area, otherItems, heapId);
                    if (!result) {
                        return false;
                    }
                    otherItems.clear();
                    continue;
                } else {
                    return false;
                }
            }

            agent.dropItemFromHandInMainInventory(solution.position());

            if (!Objects.equals(targetItem.getId(), itemInHand.getId())) {
                returnItemsBackToHeap(otherItems, heapId);
                return true;
            }

            otherItems.add(itemInHand);
        }
    }

    private boolean isBecameInvalid(final Long heap) {
        return knownObjectRepository.findById(heap)
                .map(KnownObject::getInvalid)
                .orElse(true);
    }

    private KnownObject getCharacter() {
        return knownObjectRepository.getOne(agent.getCharacter().knownObjectId());
    }

    private KnownObject getItemInHand() {
        return knownObjectRepository.getOne(agent.getHand().knownItemId());
    }

    private Long returnItemInHandBackToHeap(final Long heapId, final IntPoint position) {
        final KnownObject heap = knownObjectRepository.findById(heapId).orElse(null);
        if (heap == null) {
            return agent.placeHeap(position);
        } else {
            agent.dropItemFromHandInCurrentHeap();
            return heapId;
        }
    }

    private void returnItemsBackToHeap(final List<KnownObject> items, final Long heapId) {
        for (final KnownObject item : items) {
            agent.openHeap(heapId);
            agent.takeItemInHandFromInventory(item.getId());
            agent.dropItemFromHandInCurrentHeap();
        }
    }

    private boolean storeItemsInOtherHeap(final Area area, final List<KnownObject> items, final Long heapExclude) {
        boolean result = true;
        for (final KnownObject item : items) {
            result = store(area, item, heapExclude) && result;
        }
        return result;
    }
/*
    public Result take(final List<KnownObject> items) {

        final KnownObject character = agent.getKnownObjectRepository().getOne(agent.getCharacterId());

        final Warehousing warehousing = new Warehousing();
        final Warehousing.Result solveResult = warehousing.solve(character, KnownObject.Place.MAIN_INVENTORY, items);

        final Result result = new Result(new ArrayList<>(), new ArrayList<>());
        for (final KnownObject knownItem : solveResult.skipped()) {
            result.skipped().add(knownItem);
        }

        for (final Warehousing.BoxEntry entry : solveResult.boxEntries()) {
            final KnownObject knownItem = entry.item();
            final IntPoint position = entry.position();

            final KnownObject container = knownItem.getParent();
        }

        return result;
    }

    private void takeFromHeap(KnownObject heap, final KnownObject item) {
        final IntPoint heapPosition = heap.getPosition();
        final List<IntPoint> freeCells = agent.getKnownObjectService().findFreeCellsInLinkedArea(heap);

        agent.openHeap(heap.getId());
        heap = agent.getKnownObjectRepository().getOne(heap.getId());
        if (heap.getInvalid()) {
            // do something
        }

        final List<KnownObject> itemPrevIterations = new ArrayList<>();

        while (true) {

            agent.takeItemInHandFromCurrentHeap();

            final KnownObject character = agent.getKnownObjectRepository().getOne(agent.getCharacterId());
            final KnownObject inHand = agent.getKnownObjectRepository().findByParentIdAndPlace(agent.getCharacterId(), KnownObject.Place.HAND)
                    .stream()
                    .findFirst()
                    .orElse(null);
            heap = agent.getKnownObjectRepository().findById(heap.getId()).orElse(null);

            final boolean isItemFound = inHand != null && Objects.equals(inHand.getId(), item.getId());
            final Warehousing.Result solveResult = new Warehousing().solve(
                    character,
                    KnownObject.Place.MAIN_INVENTORY,
                    Collections.singletonList(inHand)
            );
            final boolean isHaveFreeSpace = !solveResult.boxEntries().isEmpty();
            final boolean isHeapExists = heap != null;
            final boolean isHeapValid = isHeapExists && heap.getInvalid();
            final boolean isHaveItemFromPrevIteration = !itemPrevIterations.isEmpty();

            if (isHaveFreeSpace) {
                final Warehousing.BoxEntry entry = solveResult.boxEntries().get(0);
                agent.dropItemFromHandInMainInventory(entry.position());
                if (isItemFound) {
                    itemPrevIterations.add(inHand);
                }
            } else if (isHeapExists) {
                agent.dropItemFromHandInCurrentHeap();
            } else {
                agent.placeHeap(heapPosition);
            }

            if (isItemFound) {
                if (isHaveItemFromPrevIteration) {
                    dropPrevItemSomewhere(itemPrevIterations);
                    itemPrevIterations.clear();
                }
            } else {
                if (!isHeapValid || !isHaveFreeSpace) {
                    dropPrevItemSomewhere(itemPrevIterations);
                    itemPrevIterations.clear();
                }
            }

        }
    }

    private void dropPrevItemSomewhere(final List<KnownObject> items) {

    }
*/
    public static final record Result(List<KnownObject> done, List<KnownObject> skipped) {}

}
