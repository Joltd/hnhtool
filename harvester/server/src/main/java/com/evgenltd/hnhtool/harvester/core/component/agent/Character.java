package com.evgenltd.hnhtool.harvester.core.component.agent;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 26-11-2019 23:47</p>
 */
public class Character {

    private Inventory mainInventory;
    private Inventory studyInventory;

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
