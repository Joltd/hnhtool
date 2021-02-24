package com.evgenltd.hnhtool.harvester.core.component.agent;

import com.evgenltd.hnhtools.clientapp.Prop;
import com.evgenltd.hnhtools.entity.IntPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Character {

    private Long knownObjectId;
    private String characterName;
    private Prop prop;
    private final Inventory mainInventory = new Inventory();
    private final Inventory studyInventory = new Inventory();
    private Integer learningPoints;
    private Integer experiencePoints;
    private List<Attribute> attributes = new ArrayList<>();

    public Long getKnownObjectId() {
        return knownObjectId;
    }
    public void setKnownObjectId(final Long knownObjectId) {
        this.knownObjectId = knownObjectId;
        this.getMainInventory().setKnownObjectId(knownObjectId);
        this.getStudyInventory().setKnownObjectId(knownObjectId);
    }

    public String getCharacterName() {
        return characterName;
    }
    public void setCharacterName(final String characterName) {
        this.characterName = characterName;
    }

    public Prop getProp() {
        return prop;
    }
    public void setProp(final Prop prop) {
        this.prop = prop;
    }

    public Inventory getMainInventory() {
        return mainInventory;
    }

    public Inventory getStudyInventory() {
        return studyInventory;
    }

    public Integer getLearningPoints() {
        return learningPoints;
    }
    public void setLearningPoints(final Integer learningPoints) {
        this.learningPoints = learningPoints;
    }

    public Integer getExperiencePoints() {
        return experiencePoints;
    }
    public void setExperiencePoints(final Integer experiencePoints) {
        this.experiencePoints = experiencePoints;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public Record toRecord(final IntPoint offset) {
        return new Record(
                knownObjectId,
                characterName,
                prop.getPosition().add(offset),
                learningPoints,
                experiencePoints,
                Collections.unmodifiableList(attributes)
        );
    }

    public static final record Record(
            Long knownObjectId,
            String name,
            IntPoint position,
            Integer learningPoints,
            Integer experiencePoints,
            List<Attribute> attributes
    ) {}

    public static final record Attribute(String name, Integer base, Integer complex) {}
}
