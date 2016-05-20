package org.rul.meapi;

import org.rul.meapi.model.CommandSimple;

public class MeDevice {

    public static final int COMMAND_PARAMETER_PREFIX1 = 0;
    public static final int COMMAND_PARAMETER_PREFIX2 = 1;

    public static final int INSTRUCTION_PREFIX1 = 255;
    public static final int INSTRUCTION_PREFIX2 = 85;

    public static final int LENGTH_INSTRUCTION_HEAD = 3;

    private String name;

    public MeDevice(String name) {
        this.name = name;
    }

    public MeDevice() {
    }

    protected void initCommand(CommandSimple command){
        command.setElementCadena(COMMAND_PARAMETER_PREFIX1, INSTRUCTION_PREFIX1);
        command.setElementCadena(COMMAND_PARAMETER_PREFIX2, INSTRUCTION_PREFIX2);
    }

}
