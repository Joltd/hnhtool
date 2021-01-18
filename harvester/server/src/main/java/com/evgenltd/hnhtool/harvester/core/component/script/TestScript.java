package com.evgenltd.hnhtool.harvester.core.component.script;

import com.evgenltd.hnhtool.harvester.core.aspect.AgentCommand;
import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
import com.evgenltd.hnhtool.harvester.core.service.A;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TestScript extends Script {

    private final KnownObjectRepository knownObjectRepository;

    public TestScript(final KnownObjectRepository knownObjectRepository) {
        this.knownObjectRepository = knownObjectRepository;
    }

    @Override
    @AgentCommand
    public void execute() {
        A.scan();

//        A.takeItemsInInventoryFromHeap(5440L, Agent.InventoryType.MAIN);
//        A.takeItemInMainInventoryFromHeap(5440L);

        final List<KnownObject> items = knownObjectRepository.findByParentIdAndPlace(
                A.getCharacter().knownObjectId(),
                KnownObject.Place.MAIN_INVENTORY
        );
        for (final KnownObject item : items) {
            if (item.getLost()) {
                continue;
            }
            if (!item.getResource().getName().equals("gfx/invobjs/branch")) {
                continue;
            }
            A.store(3L, item.getId());
        }
    }

}
