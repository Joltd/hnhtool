package com.evgenltd.hnhtools.clientapp.impl.widgets;

import com.evgenltd.hnhtools.clientapp.impl.WidgetState;
import com.evgenltd.hnhtools.clientapp.widgets.CharacterWidget;
import com.evgenltd.hnhtools.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class CharacterWidgetImpl extends WidgetImpl implements CharacterWidget {

    private static final String EXP_NAME = "exp";
    private static final String ENC_NAME = "enc";

    private Integer learningPoints;
    private Integer experiencePoints;
    private List<Attribute> attributes = new ArrayList<>();

    public CharacterWidgetImpl(final WidgetImpl widget, final Integer learningPoints, final Integer experiencePoints, final List<Attribute> attributes) {
        super(widget);
        this.learningPoints = learningPoints;
        this.experiencePoints = experiencePoints;
        this.attributes = new ArrayList<>(attributes);
    }

    CharacterWidgetImpl(
            final Integer id,
            final String type,
            final Integer parentId,
            final ArrayNode args
    ) {
        super(id, type, parentId, args);
    }

    @Override
    public WidgetImpl copy() {
        return new CharacterWidgetImpl(this, learningPoints, experiencePoints, attributes);
    }

    @Override
    public void handleMessage(final WidgetState.RelAccessor message) {
        switch (message.getWidgetMessageName()) {
            case EXP_NAME -> learningPoints = JsonUtil.asInt(message.getArgs().get(0));
            case ENC_NAME -> experiencePoints = JsonUtil.asInt(message.getArgs().get(0));
        }
    }

    public void handleAttribute(final JsonNode characterAttributes) {
        attributes = StreamSupport.stream(characterAttributes.spliterator(), false)
                .map(attributeNode -> new Attribute(
                        JsonUtil.asText(attributeNode.get("attributeName")),
                        JsonUtil.asInt(attributeNode.get("baseValue")),
                        JsonUtil.asInt(attributeNode.get("complexValue"))
                ))
                .collect(Collectors.toList());
    }

    public static final record Attribute(String name, Integer base, Integer complex) {}

}
