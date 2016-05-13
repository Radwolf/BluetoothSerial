package org.rul.bluetoothserial;

import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by rgonzalez on 12/05/2016.
 */
public class MeModuleCommunication {

    public static final int COMMAND_PARAMETER_PREFIX1 = 0;
    public static final int COMMAND_PARAMETER_PREFIX2 = 1;
    public static final int COMMAND_PARAMETER_LENGTH = 2;
    public static final int COMMAND_PARAMETER_INDEX = 3;
    public static final int COMMAND_PARAMETER_ACTION = 4;
    public static final int COMMAND_PARAMETER_DEVICE = 5;

    public static final int DEV_VERSION = 0;
    public static final int DEV_ULTRASOINIC = 1;
    public static final int DEV_TEMPERATURE = 2;
    public static final int DEV_LIGHTSENSOR = 3;
    public static final int DEV_POTENTIALMETER = 4;
    public static final int DEV_JOYSTICK = 5;
    public static final int DEV_GYRO = 6;
    public static final int DEV_BUZZER = 7;
    public static final int DEV_RGBLED = 8;
    public static final int DEV_SEVSEG = 9;
    public static final int DEV_DCMOTOR= 10;
    public static final int DEV_SERVO= 11;
    public static final int DEV_ENCODER = 12;
    public static final int DEV_PIRMOTION = 15;
    public static final int DEV_INFRADRED = 16;
    public static final int DEV_LINEFOLLOWER = 17;
    public static final int DEV_BUTTON = 18;
    public static final int DEV_LIMITSWITCH = 19;
    public static final int DEV_SHUTTER = 20;
    public static final int DEV_PINDIGITAL = 30;
    public static final int DEV_PINANALOG = 31;
    public static final int DEV_PINPWM = 32;
    public static final int DEV_PINANGLE = 33;
    public static final int DEV_CAR_CONTROLLER = 40;
    public static final int DEV_GRIPPER_CONTROLLER = 41;

    public static final int LENGTH_INSTRUCTION_ULTRASOINIC = 4;
    public static final int LENGTH_INSTRUCTION_LIGHTSENSOR = 4;
    public static final int LENGTH_INSTRUCTION_BUZZER = 7;
    public static final int LENGTH_INSTRUCTION_RGBLED = 9;
    public static final int LENGTH_INSTRUCTION_DCMOTOR= 6;
    public static final int LENGTH_INSTRUCTION_INFRADRED = -1;  //Pendiente de debuggar MKBLOCK
    public static final int LENGTH_INSTRUCTION_LINEFOLLOWER = 4;
    public static final int LENGTH_INSTRUCTION_BUTTON = -1;  //Pendiente de debuggar MKBLOCK

    public static final int SLOT_1 = 1;
    public static final int SLOT_2 = 2;

    public static final int READMODULE = 1;
    public static final int WRITEMODULE = 2;

    public static final int PORT_NULL = 0;
    public static final int PORT_1 = 1;
    public static final int PORT_2 = 2;
    public static final int PORT_3 = 3;
    public static final int PORT_4 = 4;
    public static final int PORT_5 = 5;
    public static final int PORT_6 = 6;
    public static final int PORT_7 = 7;
    public static final int PORT_8 = 8;
    public static final int PORT_M1 = 9;
    public static final int PORT_M2 = 10;

    public static final int INSTRUCTION_PREFIX1 = 255;
    public static final int INSTRUCTION_PREFIX2 = 85;

    public String name;
    public int length;

    public int[] command;

    public MeModuleCommunication(String name, int length){
        this.name = name;
        this.length = length;
        this.command = new int[length + 3];
    }

    public void writeCommand(){
        command[COMMAND_PARAMETER_PREFIX1] = INSTRUCTION_PREFIX1;
        command[COMMAND_PARAMETER_PREFIX2] = INSTRUCTION_PREFIX2;
        command[COMMAND_PARAMETER_LENGTH] = length;
    }

    public String readCommand(byte[] msgCommand){
        return Utils.bytesToHexString(msgCommand);
    }

    public byte[] commandToByteArray(){
        final byte[] bytes = new byte[command.length];
        for(int i = 0; i < command.length; i++) {
            bytes[i] = (byte) command[i];
        }
        return bytes;
    }
}
