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

    private Prop player;
    private Inventory mainInventory;
    private Inventory studyInventory;

    public Prop getPlayer() {
        return player;
    }
    public void setPlayer(final Prop player) {
        this.player = player;
    }

    public Inventory getMainInventory() {
        return mainInventory;
    }
    public void setMainInventory(final Inventory mainInventory) {
        this.mainInventory = mainInventory;
    }

    public Inventory getStudyInventory() {
        return studyInventory;
    }
    public void setStudyInventory(final Inventory studyInventory) {
        this.studyInventory = studyInventory;
    }

}
