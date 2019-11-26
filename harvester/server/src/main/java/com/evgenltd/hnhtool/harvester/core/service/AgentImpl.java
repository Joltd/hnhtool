package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.Agent;
import com.evgenltd.hnhtool.harvester.core.component.Intention;
import com.evgenltd.hnhtool.harvester.core.component.OpenContainer;
import com.evgenltd.hnhtools.clientapp.ClientApp;
import com.evgenltd.hnhtools.clientapp.widgets.Widget;
import com.evgenltd.hnhtools.entity.IntPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 25-11-2019 21:42</p>
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AgentImpl implements Agent {

    private ClientApp clientApp;

    private Integer mapViewId;
    private Integer gameUiId;
    private Hand hand;

    private Intention currentIntention;

    public void setClientApp(final ClientApp clientApp) {
        this.clientApp = clientApp;
    }

    private void scan() {

        final List<Widget> widgets = clientApp.getWidgets();
        widgets.stream().filter(widget -> widget.getType().equals("gameui")).findFirst().ifPresent(widget -> gameUiId = widget.getId());
        widgets.stream().filter(widget -> widget.getType().equals("mapview")).findFirst().ifPresent(widget -> mapViewId = widget.getId());

    }

    @Override
    public void move(final IntPoint position) {

    }

    @Override
    public void openContainer(final Long objectId) {
        currentIntention = new OpenContainer(objectId);
        doIntention();

    }

    private void doIntention() {
        currentIntention.doIntention(clientApp);
//        clientApp.await(this::checkIntention);
    }

    private boolean checkIntention() {
        scan();
        currentIntention.scan();
        return currentIntention.condition();
    }

    private static final class Hand {
        private Integer widgetId;
        private Long knownItemId;
    }
}
