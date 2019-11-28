package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.Agent;
import com.evgenltd.hnhtool.harvester.core.component.agent.Character;
import com.evgenltd.hnhtool.harvester.core.component.agent.Hand;
import com.evgenltd.hnhtool.harvester.core.component.agent.Heap;
import com.evgenltd.hnhtool.harvester.core.component.agent.Inventory;
import com.evgenltd.hnhtools.clientapp.ClientApp;
import com.evgenltd.hnhtools.clientapp.Prop;
import com.evgenltd.hnhtools.clientapp.widgets.InventoryWidget;
import com.evgenltd.hnhtools.clientapp.widgets.ItemWidget;
import com.evgenltd.hnhtools.clientapp.widgets.StoreBoxWidget;
import com.evgenltd.hnhtools.clientapp.widgets.Widget;
import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.entity.IntPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

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

    private Map<Long,Prop> propIndex;
    private Map<Integer,Widget> widgetIndex;

    private Widget mapView;
    private Widget gameUi;
    private Character character = new Character();
    private Hand hand = new Hand();
    private Inventory currentInventory;
    private Heap currentHeap;
    private final Set<Integer> forClose = new HashSet<>();

    private Map<Long, Long> knownObjectToPropIndex;
    private Map<Long, Integer> knownObjectToWidgetndex;
    private Map<Long, Integer> knownItemToItemWidgetIndex;
    private Map<Long, Long> knownItemToPropIndex;

    public AgentImpl(final MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    void setClientApp(final ClientApp clientApp) {
        this.clientApp = clientApp;
    }

    // ##################################################
    // #                                                #
    // #  API                                           #
    // #                                                #
    // ##################################################

    @Override
    public void await() {
        clientApp.await(() -> {
            System.out.println();
            return false;
        });
    }

    @Override
    public void move(final IntPoint position) {

    }

    @Override
    public void openContainer(final Long knownObjectId) {
        final Long propId = getPropIdByKnownObjectId(knownObjectId);
        final Prop prop = getPropById(propId);

        clientApp.sendWidgetCommand(
                mapView.getId(),
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
            scan();
            if (currentInventory == null) {
                return false;
            }
            storeKnownObjectToWidget(knownObjectId, currentInventory.getWidget().getId());
            return true;
        });
    }

    @Override
    public void openHeap(final Long knownObjectId) {

    }

    @Override
    public void takeItemInHand(final Long knownItemId) {

    }

    @Override
    public void takeItemInHandFromCurrentHeap() {

    }

    @Override
    public void dropItemFromHandInCurrentInventory(final IntPoint position) {

    }

    @Override
    public void dropItemFromHandInMainInventory(final IntPoint position) {

    }

    @Override
    public void dropItemFromHandInStudyInventory(final IntPoint position) {

    }

    @Override
    public void dropItemFromHandInCurrentHeap() {

    }

    @Override
    public void dropItemFromHandInWorld() {

    }

    @Override
    public void dropItemFromHandInEquip(final Integer position) {

    }

    @Override
    public void dropItemFromInventoryInWorld(final Long knownItemId) {

    }

    @Override
    public void transferItem(final Long knownItemId) {

    }

    @Override
    public void transferItemFromCurrentHeap() {

    }

    @Override
    public void applyItemInHandOnObject(final Long knownObjectId) {

    }

    @Override
    public void applyItemInHandOnItem(final Long knownItemId) {

    }

    private void closeWidget(final Integer widgetId) {

    }

    // ##################################################
    // #                                                #
    // #  Scan                                          #
    // #                                                #
    // ##################################################

    private void scan() {
        propIndex.clear();
        widgetIndex.clear();

        final List<Prop> props = clientApp.getProps();
        propIndex = props.stream().collect(Collectors.toMap(Prop::getId, prop -> prop));

        final List<Widget> widgets = clientApp.getWidgets();
        widgetIndex = widgets.stream().collect(Collectors.toMap(Widget::getId, widget -> widget));

        for (final Widget widget : widgets) {
            switch (widget.getType()) {
                case "gameui":
                    gameUi = widget;
                    break;
                case "mapview":
                    mapView = widget;
                    break;
                case "inv":
                    prepareInventory((InventoryWidget) widget);
                    break;
                case "item":
                    prepareItem((ItemWidget) widget);
                    break;
                case "isbox":
                    prepareHeap((StoreBoxWidget) widget);
                    break;
            }
        }

    }

    private void prepareInventory(final InventoryWidget inventoryWidget) {
        final Integer parentId = inventoryWidget.getParentId();
        final Widget parent = widgetIndex.get(parentId);
        final Inventory inventory = new Inventory();
        inventory.setWidget(inventoryWidget);

        if (Objects.equals(parentId, gameUi.getId())) {
            character.setMainInventory(inventory);
            return;
        }

        if (Objects.equals(parent.getType(), "chr")) {
            character.setStudyInventory(inventory);
            return;
        }

        if (currentInventory == null) {
            currentInventory = inventory;
            return;
        }

        if (!Objects.equals(currentInventory.getWidget(), inventoryWidget)) {
            forClose.add(inventoryWidget.getId());
        }
    }

    private void prepareItem(final ItemWidget itemWidget) {
        final Integer parentId = itemWidget.getParentId();
        final Widget parent = widgetIndex.get(parentId);

        if (Objects.equals(parentId, gameUi.getId())) {
            hand.setItem(itemWidget);
            return;
        }

        if (Objects.equals(parent, character.getMainInventory().getWidget())) {
            character.getMainInventory()
                    .getItems()
                    .add(itemWidget);
            return;
        }

        if (Objects.equals(parent, character.getStudyInventory().getWidget())) {
            character.getStudyInventory()
                    .getItems()
                    .add(itemWidget);
            return;
        }

        if (Objects.equals(parent, currentInventory.getWidget())) {
            currentInventory.getItems().add(itemWidget);
        }
    }

    private void prepareHeap(final StoreBoxWidget storeBoxWidget) {
        final Heap heap = new Heap();
        heap.setStoreBox(storeBoxWidget);

        if (currentHeap == null) {
            currentHeap = heap;
            return;
        }

        if (!Objects.equals(currentHeap.getStoreBox(), storeBoxWidget)) {
            forClose.add(storeBoxWidget.getId());
        }
    }

    // ##################################################
    // #                                                #
    // #  Private                                       #
    // #                                                #
    // ##################################################


    private Widget getInventory() {
        return null;
    }

    private void storeKnownObjectToWidget(final Long knownObjectId, final Integer widgetId) {
        // make record in index
        // scan items of inventory
    }

    private Long getPropIdByKnownObjectId(final Long knownObjectId) {
        final Long propId = knownObjectToPropIndex.get(knownObjectId);
        if (propId == null) {
            throw new ApplicationException("No match for KnownObject=[%s]", knownObjectId);
        }
        return propId;
    }

    private Prop getPropById(final Long propId) {
        final Prop prop = propIndex.get(propId);
        if (prop == null) {
            throw new ApplicationException("No Prop by Id=[%s]", propId);
        }
        return prop;
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
