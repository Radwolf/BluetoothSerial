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
    private int speed;

    public MeMotorDevice(String name, int port) {
        super(name);
        this.port = port;
    }

    protected void initCommand(CommandSimple command){
        super.initCommand(command);
        command.setElementCadena(COMMAND_PARAMETER_LENGTH, Utils.intToByte(LENGTH_INSTRUCTION_DCMOTOR));
        command.setElementCadena(COMMAND_PARAMETER_DEVICE, Utils.intToByte(MeConstants.DEV_DCMOTOR));
        command.setElementCadena(COMMAND_PARAMETER_ACTION, Utils.intToByte(command.getTipo()));
    }

    //Rotacion sentido de las agujas del reloj
    public CommandSimple giroDirecto(int speed){
        CommandSimple command = new CommandSimple("Giro directo motor", 2, LENGTH_INSTRUCTION_DCMOTOR, MeConstants.WRITEMODULE);
        initCommand(command);
        command.setElementCadena(COMMAND_PARAMETER_INDEX, Utils.intToByte(command.getIndex()));
        command.setElementCadena(COMMAND_PARAMETER_PORT, Utils.intToByte(port));
        setSpeedToCommand(command, speed);
        return command;
    }

    //Rotacion sentido inverso a las agujas del reloj
    public CommandSimple giroInverso(int speed){
        CommandSimple command = new CommandSimple("Giro directo motor", 1, LENGTH_INSTRUCTION_DCMOTOR, MeConstants.WRITEMODULE);
        initCommand(command);
        command.setElementCadena(COMMAND_PARAMETER_INDEX, Utils.intToByte(command.getIndex()));
        command.setElementCadena(COMMAND_PARAMETER_PORT, Utils.intToByte(port));
        setSpeedToCommand(command, speed);
        return command;
    }

    private void setSpeedToCommand(CommandSimple command, int speed){
        this.speed = speed;
        command.setElementCadena(COMMAND_PARAMETER_SPEED_LOW, (byte) (speed & 0xff));
        command.setElementCadena(COMMAND_PARAMETER_SPEED_HIGH, (byte) ((speed >> 8) & 0xff));
    }
}
