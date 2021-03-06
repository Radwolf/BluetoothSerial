package org.rul.meapi.common;

/**
 * Created by rgonzalez on 20/05/2016.
 */
public class MeConstants {

    public static final int DEV_VERSION = 0;
    public static final int DEV_ULTRASOINIC = 1;
    public static final int DEV_TEMPERATURE = 2;
    public static final int DEV_LIGHTSENSOR = 3;
    public static final int DEV_POTENTIALMETER = 4;
    public static final int DEV_JOYSTICK = 5;
    public static final int DEV_GYRO = 6;
    public static final int DEV_SOUNDSENSOR = 7;
    public static final int DEV_RGBLED = 8;
    public static final int DEV_SEVSEG = 9;
    public static final int DEV_DCMOTOR= 10;
    public static final int DEV_SERVO= 11;
    public static final int DEV_ENCODER = 12;
    public static final int DEV_PIRMOTION = 15;
    public static final int DEV_INFRADRED = 16;
    public static final int DEV_LINEFOLLOWER = 17;
    // representacion binaria expresada en hexadecimal
    public static final int DEV_BUTTON = 18;
    public static final int DEV_LIMITSWITCH = 19;
    public static final int DEV_SHUTTER = 20;
    public static final int DEV_MATRIX_LED = 41;  //TODO: Como controlar el int to byte deberia de ser el dispositivo 29 en byte (41 en int)
    public static final int DEV_PINDIGITAL = 30;
    public static final int DEV_PINANALOG = 31;
    public static final int DEV_PINPWM = 32;
    public static final int DEV_PINANGLE = 33;
    public static final int DEV_BUZZER = 34;
    public static final int DEV_CAR_CONTROLLER = 40;
    public static final int DEV_GRIPPER_CONTROLLER = 41;

    public static final int SLOT_1 = 1;
    public static final int SLOT_2 = 2;

    public static final int READMODULE = 1;
    public static final int WRITEMODULE = 2;

    public static final int VERSION_INDEX = 0xfa;
    public static final int MSG_VALUECHANGED = 0x10;

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
}
