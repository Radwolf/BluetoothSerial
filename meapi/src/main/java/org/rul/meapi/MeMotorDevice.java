package org.rul.meapi;

import org.rul.meapi.model.CommandSimple;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by rgonzalez on 20/05/2016.
 */
public class MeMotorDevice extends MeDevice {

    public static final int COMMAND_PARAMETER_LENGTH = 2;
    public static final int COMMAND_PARAMETER_INDEX = 3;
    public static final int COMMAND_PARAMETER_ACTION = 4;
    public static final int COMMAND_PARAMETER_DEVICE = 5;

    public static final int COMMAND_PARAMETER_PORT = 6;
    public static final int COMMAND_PARAMETER_SPEED_LOW = 7;
    public static final int COMMAND_PARAMETER_SPEED_HIGH = 8;

    public static final int LENGTH_INSTRUCTION_DCMOTOR= 6;

    private int port;

    public MeMotorDevice(String name, int port) {
        super(name);
        this.port = port;
    }

    protected void initCommand(CommandSimple command){
        super.initCommand(command);
        command.setElementCadena(COMMAND_PARAMETER_LENGTH, LENGTH_INSTRUCTION_DCMOTOR);
        command.setElementCadena(COMMAND_PARAMETER_DEVICE, MeConstants.DEV_DCMOTOR);
        command.setElementCadena(COMMAND_PARAMETER_ACTION, MeConstants.WRITEMODULE);
    }

    //Rotacion sentido de las agujas del reloj
    public CommandSimple giroDirecto(byte speedDefaultDirect){
        CommandSimple command = new CommandSimple("Giro directo motor", LENGTH_INSTRUCTION_DCMOTOR);
        initCommand(command);
        //command.setElementCadena(PARAMETROS);
        command.setElementCadena(COMMAND_PARAMETER_INDEX, 2);
        command.setElementCadena(COMMAND_PARAMETER_PORT, port);
        command.setElementCadena(COMMAND_PARAMETER_SPEED_LOW, speedDefaultDirect);
        command.setElementCadena(COMMAND_PARAMETER_SPEED_HIGH, 0);
        return command;
    }

    //Rotacion sentido inverso a las agujas del reloj
    public CommandSimple giroInverso(byte speedDefaultReverse){
        CommandSimple command = new CommandSimple("Giro directo motor", LENGTH_INSTRUCTION_DCMOTOR);
        initCommand(command);
        //command.setElementCadena(PARAMETROS);
        command.setElementCadena(COMMAND_PARAMETER_INDEX, 1);
        command.setElementCadena(COMMAND_PARAMETER_PORT, port);
        command.setElementCadena(COMMAND_PARAMETER_SPEED_LOW, speedDefaultReverse);
        command.setElementCadena(COMMAND_PARAMETER_SPEED_HIGH, (byte) -1);
        return command;
    }
}
