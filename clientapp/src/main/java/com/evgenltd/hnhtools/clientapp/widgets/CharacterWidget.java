package com.evgenltd.hnhtools.clientapp.widgets;

import com.evgenltd.hnhtools.clientapp.impl.widgets.CharacterWidgetImpl;

import java.util.List;

public interface CharacterWidget extends Widget {
    Integer getLearningPoints();

    Integer getExperiencePoints();

    List<CharacterWidgetImpl.Attribute> getAttributes();
}
