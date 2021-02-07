package com.evgenltd.hnhtools.clientapp;

import com.evgenltd.hnhtools.clientapp.widgets.Widget;
import com.evgenltd.hnhtools.messagebroker.MessageBroker;

import java.util.List;
import java.util.function.Supplier;

public interface ClientApp {

    List<Widget> getWidgets();

    List<Prop> getProps();

    void await(Supplier<Boolean> condition, long timeout);

    MessageBroker.State getState();

    // ##################################################
    // #                                                #
    // #  Commands                                      #
    // #                                                #
    // ##################################################

    void login(String username, byte[] cookie);

    void play(String username, byte[] cookie, String characterName);

    void logout();

    void sendWidgetCommand(int id, String name, Object... args);

}
