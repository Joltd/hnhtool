package com.evgenltd.hnhtool.harvester.core.component.script;

import com.evgenltd.hnhtool.harvester.core.aspect.AgentCommand;
import com.evgenltd.hnhtool.harvester.core.entity.Area;
import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.repository.AreaRepository;
import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
import com.evgenltd.hnhtool.harvester.core.service.A;
import com.evgenltd.hnhtool.harvester.core.service.AgentContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HeapResearchScript {

    private final AreaRepository areaRepository;
    private final KnownObjectRepository knownObjectRepository;

    public HeapResearchScript(
            final AreaRepository areaRepository,
            final KnownObjectRepository knownObjectRepository
    ) {
        this.areaRepository = areaRepository;
        this.knownObjectRepository = knownObjectRepository;
    }

    @AgentCommand
    public void execute() {

        A.scan();

        final List<Area> areas = areaRepository.findAll();
        for (final Area area : areas) {

            final List<KnownObject> invalidHeaps = knownObjectRepository.findInvalidHeapInArea(
                    area.getSpace().getId(),
                    area.getFrom().getX(),
                    area.getFrom().getY(),
                    area.getTo().getX(),
                    area.getTo().getY()
            );

            for (final KnownObject heap : invalidHeaps) {

                final List<Long> items = A.takeItemsInInventoryFromHeap(heap.getId(), AgentContext.InventoryType.MAIN);
                for (final Long item : items) {
                    final boolean result = A.store(area.getId(), item);
                    if (!result) {
                        return;
                    }
                }

            }

        }


    }

}
