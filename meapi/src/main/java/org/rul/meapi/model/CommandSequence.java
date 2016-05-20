package org.rul.meapi.model;

import java.util.List;

/**
 * Created by rgonzalez on 20/05/2016.
 */
public class CommandSequence {

    List<CommandSimple> commands;

    public CommandSequence() {
    }

    public void addCommand(CommandSimple command){
        commands.add(command);
    }
}
