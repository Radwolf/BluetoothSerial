package org.rul.meapi.device;

import org.rul.meapi.common.MeConstants;
import org.rul.meapi.model.CommandSimple;

/**
 * Created by rgonzalez on 20/05/2016.
 */
public class MeUltrasonicDevice extends MeDevice {

    public static final int COMMAND_PARAMETER_LENGTH = 2;
    public static final int COMMAND_PARAMETER_INDEX = 3;
    public static final int COMMAND_PARAMETER_ACTION = 4;
    public static final int COMMAND_PARAMETER_DEVICE = 5;

    public static final int COMMAND_PARAMETER_PORT = 6;

    public static final int LENGTH_INSTRUCTION_ULTRASONIC= 4;

    private int port;
    private int index;

    public MeUltrasonicDevice(String name, int port, int index) {
        super(name);
        this.port = port;
        this.index = index;
    }

    protected void initCommand(CommandSimple command){
        super.initCommand(command);
        command.setElementCadena(COMMAND_PARAMETER_LENGTH, org.rul.meapi.common.Utils.intToByte(LENGTH_INSTRUCTION_ULTRASONIC));
        command.setElementCadena(COMMAND_PARAMETER_DEVICE, org.rul.meapi.common.Utils.intToByte(MeConstants.DEV_ULTRASOINIC));
        command.setElementCadena(COMMAND_PARAMETER_ACTION, org.rul.meapi.common.Utils.intToByte(command.getType()));
    }

    //Realizamos una peticion de lectura para el sensor de ultrasonido
    public CommandSimple getDistancia(){
        CommandSimple command = new CommandSimple("Lectura sensor ultrasonico", index, LENGTH_INSTRUCTION_ULTRASONIC, MeConstants.WRITEMODULE);
        initCommand(command);
        command.setElementCadena(COMMAND_PARAMETER_INDEX, org.rul.meapi.common.Utils.intToByte(command.getIndex()));
        command.setElementCadena(COMMAND_PARAMETER_PORT, org.rul.meapi.common.Utils.intToByte(port));
        return command;
    }

    public int convert4ByteToInt(byte [] dataResponded){
        int dataInt = 0;
        dataInt += (dataResponded[0] << 24);
        dataInt += (dataResponded[1] << 16);
        dataInt += (dataResponded[2] << 8);
        dataInt += dataResponded[3];
        return dataInt;
    }

}
