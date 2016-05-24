package org.rul.meapi.device;

import org.rul.meapi.model.CommandSimple;

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
    private int index;

    public MeMotorDevice(String name, int port, int index) {
        super(name);
        this.port = port;
        this.index = index;
    }

    protected void initCommand(CommandSimple command){
        super.initCommand(command);
        command.setElementCadena(COMMAND_PARAMETER_LENGTH, org.rul.meapi.common.Utils.intToByte(LENGTH_INSTRUCTION_DCMOTOR));
        command.setElementCadena(COMMAND_PARAMETER_DEVICE, org.rul.meapi.common.Utils.intToByte(org.rul.meapi.common.MeConstants.DEV_DCMOTOR));
        command.setElementCadena(COMMAND_PARAMETER_ACTION, org.rul.meapi.common.Utils.intToByte(command.getType()));
    }

    //Rotacion sentido de las agujas del reloj
    public CommandSimple giroDirecto(int speed){
        CommandSimple command = new CommandSimple("Giro directo motor", index, LENGTH_INSTRUCTION_DCMOTOR, org.rul.meapi.common.MeConstants.WRITEMODULE);
        initCommand(command);
        command.setElementCadena(COMMAND_PARAMETER_INDEX, org.rul.meapi.common.Utils.intToByte(command.getIndex()));
        command.setElementCadena(COMMAND_PARAMETER_PORT, org.rul.meapi.common.Utils.intToByte(port));
        setSpeedToCommand(command, speed);
        return command;
    }

    //Rotacion sentido inverso a las agujas del reloj
    public CommandSimple giroInverso(int speed){
        CommandSimple command = new CommandSimple("Giro directo motor", index, LENGTH_INSTRUCTION_DCMOTOR, org.rul.meapi.common.MeConstants.WRITEMODULE);
        initCommand(command);
        command.setElementCadena(COMMAND_PARAMETER_INDEX, org.rul.meapi.common.Utils.intToByte(command.getIndex()));
        command.setElementCadena(COMMAND_PARAMETER_PORT, org.rul.meapi.common.Utils.intToByte(port));
        setSpeedToCommand(command, speed * -1);
        return command;
    }

    //Rotacion quieto
    public CommandSimple stop(){
        CommandSimple command = new CommandSimple("Giro directo motor", index, LENGTH_INSTRUCTION_DCMOTOR, org.rul.meapi.common.MeConstants.WRITEMODULE);
        initCommand(command);
        command.setElementCadena(COMMAND_PARAMETER_INDEX, org.rul.meapi.common.Utils.intToByte(command.getIndex()));
        command.setElementCadena(COMMAND_PARAMETER_PORT, org.rul.meapi.common.Utils.intToByte(port));
        setSpeedToCommand(command, 0);
        return command;
    }

    private void setSpeedToCommand(CommandSimple command, int speed){
        this.speed = speed;
        command.setElementCadena(COMMAND_PARAMETER_SPEED_LOW, (byte) (speed & 0xff));
        command.setElementCadena(COMMAND_PARAMETER_SPEED_HIGH, (byte) ((speed >> 8) & 0xff));
    }
}
