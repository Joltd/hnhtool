package com.evgenltd.hnhtools.clientapp.impl.widgets;

import com.evgenltd.hnhtools.clientapp.impl.WidgetState;
import com.evgenltd.hnhtools.clientapp.widgets.CharListWidget;
import com.evgenltd.hnhtools.util.JsonUtil;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CharListWidgetImpl extends WidgetImpl implements CharListWidget {

    private static final String ADD_NAME = "add";

    private final List<String> characters = new ArrayList<>();

    private CharListWidgetImpl(final CharListWidgetImpl widget) {
        super(widget);
        this.characters.addAll(widget.characters);
    }

    CharListWidgetImpl(
            final Integer id,
            final String type,
            final Integer parentId,
            final ArrayNode args
    ) {
        super(id, type, parentId, args);
    }

    @Override
    public WidgetImpl copy() {
        return new CharListWidgetImpl(this);
    }

    @Override
    public List<String> getCharacters() {
        return Collections.unmodifiableList(characters);
    }

    @Override
    public void handleMessage(final WidgetState.RelAccessor message) {
        if (Objects.equals(message.getWidgetMessageName(), ADD_NAME)) {
            final String characterName = JsonUtil.asText(message.getArgs().get(0));
            characters.add(characterName);
        }
    }
}
