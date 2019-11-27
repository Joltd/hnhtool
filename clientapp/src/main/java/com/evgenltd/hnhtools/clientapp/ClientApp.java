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

    List<Prop> getProps();

    void await(Supplier<Boolean> condition);

    // ##################################################
    // #                                                #
    // #  Commands                                      #
    // #                                                #
    // ##################################################

    void play();

    void logout();

    void sendWidgetCommand(int id, String name, Object... args);

}
