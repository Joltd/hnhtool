package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.Agent;
import com.evgenltd.hnhtools.clientapp.ClientApp;
import com.evgenltd.hnhtools.clientapp.Prop;
import com.evgenltd.hnhtools.clientapp.widgets.Widget;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.google.common.collect.BiMap;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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

    private static final String PLAY_COMMAND = "play";
    private static final String CLICK_COMMAND = "click";
    private static final String TAKE_COMMAND = "take";
    private static final String DROP_COMMAND = "drop";
    private static final String ITEM_ACT_COMMAND = "itemact";
    private static final String ITEM_ACT_SHORT_COMMAND = "iact";
    private static final String TRANSFER_COMMAND = "transfer";
    private static final String TRANSFER_EXT_COMMAND = "xfer";
    private static final String PLACE_COMMAND = "place";
    private static final String CLOSE_COMMAND = "close";
    private static final String CONTEXT_MENU_COMMAND = "cl";

    private static final int SKIP_FLAG = -1;

    private static final int UNKNOWN_FLAG = 0;

    private static final IntPoint SCREEN_POSITION = new IntPoint();

    private MatchingService matchingService;

    private ClientApp clientApp;

    private Integer mapViewId;
    private Integer gameUiId;

    private Map<Long,Prop> propIndex;
    private Map<Integer,Widget> inventoryIndex;
    private Map<Integer,Widget> storeBoxIndex;
    private Map<Integer,Widget> itemIndex;
    private Map<Integer,Integer> widgetToParentIndex;

    private BiMap<Long, Long> knownObjectToPropIndex;

    public AgentImpl(final MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    public void setClientApp(final ClientApp clientApp) {
        this.clientApp = clientApp;
    }

    // ##################################################
    // #                                                #
    // #  API                                           #
    // #                                                #
    // ##################################################

    @Override
    public void move(final IntPoint position) {

    }

    @Override
    public void openContainer(final Long knownObjectId) {
        final Prop prop = getProp(knownObjectId);

        clientApp.sendWidgetCommand(
                mapViewId,
                CLICK_COMMAND,
                SCREEN_POSITION,
                prop.getPosition(),
                Mouse.RMB.code,
                KeyModifier.NO.code,
                UNKNOWN_FLAG,
                prop.getId(),
                prop.getPosition(),
                UNKNOWN_FLAG,
                SKIP_FLAG
        );

        clientApp.await(() -> {
            final Widget inventory = getInventory();
            if (inventory == null) {
                return false;
            }
            storeKnownObjectToWidget(knownObjectId, inventory.getId());
            return true;
        });
    }

    // ##################################################
    // #                                                #
    // #  Private                                       #
    // #                                                #
    // ##################################################

    private void scan() {
        propIndex.clear();
        inventoryIndex.clear();
        itemIndex.clear();
        storeBoxIndex.clear();
        widgetToParentIndex.clear();

        final List<Prop> props = clientApp.getProps();

        knownObjectToPropIndex = matchingService.matchObjects(props);
    }

    private Prop getProp(final Long knownObjectId) {
        final Long propId = knownObjectToPropIndex.get(knownObjectId);
        return propIndex.get(propId);
    }

    private Widget getInventory() {
        return null;
    }

    private void storeKnownObjectToWidget(final Long knownObjectId, final Integer widgetId) {
        // make record in index
        // scan items of inventory
    }

    enum Mouse {
        LMB(1),
        MMB(2),
        RMB(3);

        private int code;

        Mouse(final int code) {
            this.code = code;
        }
    }

    enum KeyModifier {
        NO(0);

        private int code;

        KeyModifier(final int code) {
            this.code = code;
        }
    }

}
