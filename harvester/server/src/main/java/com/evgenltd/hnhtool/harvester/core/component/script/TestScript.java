package com.evgenltd.hnhtool.harvester.core.component.script;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
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
    public void execute() {
        getAgent().scan();

        final String itemResource = "gfx/invobjs/branch";

        final List<KnownObject> items = knownObjectRepository.findByParentIdAndPlace(
                getAgent().getCharacter().knownObjectId(),
                KnownObject.Place.MAIN_INVENTORY
        );
        for (final KnownObject item : items) {
            if (item.getLost()) {
                continue;
            }
            if (!item.getResource().getName().equals(itemResource)) {
                continue;
            }
            getStorekeeper().store(3L, item.getId());
        }
    }

}
