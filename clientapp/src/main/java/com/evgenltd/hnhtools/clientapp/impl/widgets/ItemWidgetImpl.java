package com.evgenltd.hnhtools.clientapp.impl.widgets;

import com.evgenltd.hnhtools.clientapp.impl.WidgetState;
import com.evgenltd.hnhtools.clientapp.widgets.ItemWidget;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.evgenltd.hnhtools.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 20-11-2019 23:26</p>
 */
public final class ItemWidgetImpl extends WidgetImpl implements ItemWidget {

    private static final String LABEL_NAME = "tt";

    private final IntPoint position;
    private Long resourceId;
    private String resource;
    private List<ItemInfoImpl> itemInfoList = new ArrayList<>();

    private ItemWidgetImpl(final ItemWidgetImpl itemWidget) {
        super(itemWidget);
        this.position = itemWidget.position;
        this.resource = itemWidget.resource;
    }

    ItemWidgetImpl(
            final Integer id,
            final String type,
            final Integer parentId,
            final ArrayNode childArgs,
            final ArrayNode parentArgs
    ) {
        super(id, type, parentId, childArgs);
        position = JsonUtil.asPoint(parentArgs.get(0));
        resourceId = JsonUtil.asLong(childArgs.get(0));
    }

    @Override
    public WidgetImpl copy() {
        return new ItemWidgetImpl(this);
    }

    @Override
    public IntPoint getPosition() {
        return position;
    }

    public Long getResourceId() {
        return resourceId;
    }

    @Override
    public String getResource() {
        return resource;
    }
    public void setResource(final String resource) {
        this.resource = resource;
    }

    @Override
    public List<ItemInfoImpl> getItemInfoList() {
        return itemInfoList;
    }

    @Override
    public void handleMessage(final WidgetState.RelAccessor message) {
        if (Objects.equals(message.getWidgetMessageName(), LABEL_NAME)) {
            this.itemInfoList = readItemInfoList(message.getArgs());
        }
    }

    private List<ItemInfoImpl> readItemInfoList(final JsonNode infoNodes) {
        if (!infoNodes.isArray()) {
            return Collections.emptyList();
        }

        final List<ItemInfoImpl> result = new ArrayList<>();
        for (final JsonNode infoNode : infoNodes) {
            if (!infoNode.isArray()) {
                continue;
            }

            if (infoNode.size() <= 1) {
                continue;
            }

            result.add(readItemInfo(infoNode));
        }
        return result;
    }

    private ItemInfoImpl readItemInfo(final JsonNode args) {

        final ItemInfoImpl itemInfo = new ItemInfoImpl();
        itemInfo.setResourceId(JsonUtil.asLong(args.get(0)));

        if (args.get(1).isArray()) {

            final List<ItemInfoImpl> itemInfoList = readItemInfoList(args.get(1));
            itemInfo.setItemInfoList(itemInfoList);

        } else {

            final List<JsonNode> itemArgs = StreamSupport.stream(args.spliterator(), false)
                    .skip(1)
                    .collect(Collectors.toList());
            itemInfo.setArgs(itemArgs);

        }

        return itemInfo;

    }

}
