package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.component.script.HeapResearchScript;
import com.evgenltd.hnhtool.harvester.core.entity.Area;
import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
import com.evgenltd.hnhtools.entity.IntPoint;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HeapResearchService {

    private final AreaService areaService;
    private final KnownObjectRepository knownObjectRepository;
    private final AgentService agentService;
    private final ObjectFactory<HeapResearchScript> heapResearchScriptFactory;

    public HeapResearchService(
            final AreaService areaService,
            final KnownObjectRepository knownObjectRepository,
            final AgentService agentService,
            final ObjectFactory<HeapResearchScript> heapResearchScriptFactory
    ) {
        this.areaService = areaService;
        this.knownObjectRepository = knownObjectRepository;
        this.agentService = agentService;
        this.heapResearchScriptFactory = heapResearchScriptFactory;
    }

    public void doResearch() {

        // check if any previous tasks still in progress
        // skip handling heaps that still in progress in another task

        final List<KnownObject> invalidHeaps = knownObjectRepository.findByResourceHeapIsTrueAndInvalidIsTrue();
        if (invalidHeaps.isEmpty()) {
            return;
        }

        final Map<IntPoint, Area> areaIndex = areaService.prepareAreaIndex();
        if (areaIndex.isEmpty()) {
            return;
        }

        // check if there is containers in any areas but not on valid cell

        final Map<Long, List<Long>> areaToHeapsIndex = new HashMap<>();
        for (final KnownObject heap : invalidHeaps) {
            final Area area = areaIndex.get(heap.getPosition());
            if (area == null) {
                // log heap not in area
                continue;
            }

            final List<Long> heaps = areaToHeapsIndex.computeIfAbsent(area.getId(), id -> new ArrayList<>());
            heaps.add(heap.getId());
        }

        areaToHeapsIndex.forEach((areaId, heapIds) -> {
            final HeapResearchScript script = heapResearchScriptFactory.getObject();
            script.setAreaId(areaId);
            script.setHeaps(heapIds);
//            agentService.scheduleScriptExecution(script);
        });


    }
}
