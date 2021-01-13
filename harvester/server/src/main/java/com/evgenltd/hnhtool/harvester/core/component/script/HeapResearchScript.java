package com.evgenltd.hnhtool.harvester.core.component.script;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HeapResearchScript extends Script {

    private Long areaId;
    private List<Long> heaps;

    public void setAreaId(final Long areaId) {
        this.areaId = areaId;
    }

    public void setHeaps(final List<Long> heaps) {
        this.heaps = heaps;
    }

    @Override
    public void execute() {




    }

}
