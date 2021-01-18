package com.evgenltd.hnhtools.clientapp;

import com.evgenltd.hnhtools.clientapp.widgets.Widget;

import java.util.List;
import java.util.function.Supplier;

public interface ClientApp {

    List<Widget> getWidgets();

    List<Prop> getProps();

    void await(Supplier<Boolean> condition, long timeout);

    // ##################################################
    // #                                                #
    // #  Commands                                      #
    // #                                                #
    // ##################################################

    void login();

    void play(String characterName);

    void logout();

    void sendWidgetCommand(int id, String name, Object... args);

}
