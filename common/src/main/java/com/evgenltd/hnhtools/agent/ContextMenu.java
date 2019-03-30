package com.evgenltd.hnhtools.agent;

import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 15:54</p>
 */
final class ContextMenu {

    private Integer id;
    private List<String> commands = new ArrayList<>();

    public ContextMenu(final Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public List<String> getCommands() {
        return commands;
    }

    public int getCommandId(final String command) {
        return commands.indexOf(command);
    }

}
