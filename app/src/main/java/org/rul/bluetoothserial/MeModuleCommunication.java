package org.rul.bluetoothserial;

import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by rgonzalez on 12/05/2016.
 */
public class MeModuleCommunication {


    public static final int COMMAND_PARAMETER_LENGTH = 2;
    public static final int COMMAND_PARAMETER_INDEX = 3;
    public static final int COMMAND_PARAMETER_ACTION = 4;
    public static final int COMMAND_PARAMETER_DEVICE = 5;



    public static final int LENGTH_INSTRUCTION_ULTRASOINIC = 4;
    public static final int LENGTH_INSTRUCTION_LIGHTSENSOR = 4;
    public static final int LENGTH_INSTRUCTION_BUZZER = 7;
    public static final int LENGTH_INSTRUCTION_RGBLED = 9;
    public static final int LENGTH_INSTRUCTION_DCMOTOR= 6;
    public static final int LENGTH_INSTRUCTION_INFRADRED = -1;  //Pendiente de debuggar MKBLOCK
    public static final int LENGTH_INSTRUCTION_LINEFOLLOWER = 4;
    public static final int LENGTH_INSTRUCTION_BUTTON = -1;  //Pendiente de debuggar MKBLOCK





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


}
