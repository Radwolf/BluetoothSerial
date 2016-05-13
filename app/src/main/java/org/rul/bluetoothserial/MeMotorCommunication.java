package org.rul.bluetoothserial;

/**
 * Created by rgonzalez on 12/05/2016.
 */
public class MeMotorCommunication extends MeModuleCommunication {

    public static final int COMMAND_PARAMETER_PORT = 6;
    public static final int COMMAND_PARAMETER_SPEED_LOW = 7;
    public static final int COMMAND_PARAMETER_SPEED_HIGH = 8;

    public static final int SPEED_0 = 0;
    public static final int SPEED_1 = 50;
    public static final int SPEED_2 = 100;
    public static final int SPEED_3 = 255;

    private int index;
    private int port;
    private int speedLow;
    private int speedHigh;

    public MeMotorCommunication(String name, int port) {
        super(name, LENGTH_INSTRUCTION_DCMOTOR);
        this.port = port;
    }

    public void writeCommand(int index, int action, int speedLow, int speedHigh) {
        super.writeCommand();
        this.index = index;
        this.speedLow = speedLow;
        this.speedHigh = speedHigh;
        command[COMMAND_PARAMETER_PORT] = port;
        command[COMMAND_PARAMETER_INDEX] = index;
        command[COMMAND_PARAMETER_ACTION] = action;
        command[COMMAND_PARAMETER_DEVICE] = DEV_DCMOTOR;
        command[COMMAND_PARAMETER_SPEED_LOW] = speedLow;
        command[COMMAND_PARAMETER_SPEED_HIGH] = speedHigh;
    }

}
