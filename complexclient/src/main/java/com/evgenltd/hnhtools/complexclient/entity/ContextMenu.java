package com.evgenltd.hnhtools.complexclient.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 27-04-2019 20:05</p>
 */
public class ContextMenu {

    private final Integer id;
    private final List<String> commands = new ArrayList<>();

    public ContextMenu(final Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void addCommand(final String command) {
        commands.add(command);
    }

    public List<String> getCommands() {
        return commands;
    }

}
