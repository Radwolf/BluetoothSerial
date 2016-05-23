package org.rul.meapi.model;

import java.util.List;

/**
 * Created by rgonzalez on 20/05/2016.
 */
public class CommandSequence {

    String name;
    List<CommandAbstract> commands;

    public CommandSequence(String name) {
        this.name = name;
    }

    public void addCommand(CommandAbstract command){
        commands.add(command);
    }
}
