package org.rul.meapi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rgonzalez on 20/05/2016.
 */
public class CommandSequence {

    String name;
    List<CommandAbstract> commands;

    public CommandSequence(String name) {
        this.name = name;
        this.commands = new ArrayList<>();
    }

    public void addCommand(CommandAbstract command){
        commands.add(command);
    }

    public void addSequence(CommandSequence sequence){
        for(CommandAbstract command: sequence.commands){
            this.commands.add(command);
        }
    }

    public void print(){
        for(CommandAbstract command: commands){
            System.out.printf("%s: %s%n", command.getName(), command.toString());
        }
        System.out.println();
    }
}
