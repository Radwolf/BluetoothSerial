package org.rul.meapi.device;

import org.rul.meapi.model.CommandSimple;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MeDevice {

    public static final int COMMAND_PARAMETER_PREFIX1 = 0;
    public static final int COMMAND_PARAMETER_PREFIX2 = 1;

    public static final byte INSTRUCTION_PREFIX1 = (byte)0xFF;
    public static final byte INSTRUCTION_PREFIX2 = (byte)0x55;

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
