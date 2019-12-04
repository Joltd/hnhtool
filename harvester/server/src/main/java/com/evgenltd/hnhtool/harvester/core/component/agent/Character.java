package com.evgenltd.hnhtool.harvester.core.component.agent;

import com.evgenltd.hnhtools.clientapp.Prop;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 26-11-2019 23:47</p>
 */
public class Character {

    private Long knownObjectId;
    private String characterName;
    private Prop prop;
    private final Inventory mainInventory = new Inventory();
    private final Inventory studyInventory = new Inventory();

    public Long getKnownObjectId() {
        return knownObjectId;
    }
    public void setKnownObjectId(final Long knownObjectId) {
        this.knownObjectId = knownObjectId;
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

}
