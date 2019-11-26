package com.evgenltd.hnhtools.clientapp;

import com.evgenltd.hnhtools.clientapp.widgets.Widget;

import java.util.List;
import java.util.function.Supplier;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 07-11-2019 00:16</p>
 */
public interface ClientApp {

    List<Widget> getWidgets();

    List<WorldObject> getWorldObjects();

    void await(Supplier<Boolean> condition) throws InterruptedException;

    // ##################################################
    // #                                                #
    // #  Commands                                      #
    // #                                                #
    // ##################################################

    void play();

    void logout();

    void click();

    void take();

    void drop();

    void itemAct();

    void itemActShort();

    void transfer();

    void transferExt();

    void place();

    void close();

    void contextMenu();

}
