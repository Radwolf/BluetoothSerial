package org.rul.bluetoothserial;

/**
 * Created by rgonzalez on 13/05/2016.
 */
public class MeMotorAPI {

    private int portMotorLeft;
    private int portMotorRight;
    private int stopMotor = MeMotorCommunication.SPEED_0;
    private int stopInverseMotor = MeMotorCommunication.SPEED_3;
    private int speedDefault;
    private int speedBackDefault;

    private MeMotorCommunication commMotorL;
    private MeMotorCommunication commMotorR;

    private byte[] msgLeft;
    private byte[] msgRight;

    public MeMotorAPI(int portMotorLeft, int portMotorRight, int speedDefault, int speedBackDefault) {
        this.portMotorLeft = portMotorLeft;
        this.portMotorRight = portMotorRight;
        this.speedDefault = speedDefault;
        this.speedBackDefault = speedBackDefault;
        commMotorL = new MeMotorCommunication("Motor Izquierdo", portMotorLeft);
        commMotorR = new MeMotorCommunication("Motor Derecho", portMotorRight);
    }

    public int getPortMotorLeft() {
        return portMotorLeft;
    }

    public void setPortMotorLeft(int portMotorLeft) {
        this.portMotorLeft = portMotorLeft;
    }

    public int getPortMotorRight() {
        return portMotorRight;
    }

    public void setPortMotorRight(int portMotorRight) {
        this.portMotorRight = portMotorRight;
    }

    public int getStopMotor() {
        return stopMotor;
    }

    public void setStopMotor(int stopMotor) {
        this.stopMotor = stopMotor;
    }

    public int getSpeedDefault() {
        return speedDefault;
    }

    public void setSpeedDefault(int speedDefault) {
        this.speedDefault = speedDefault;
    }

    public byte[] getMsgLeft() {
        return msgLeft;
    }

    public void setMsgLeft(byte[] msgLeft) {
        this.msgLeft = msgLeft;
    }

    public byte[] getMsgRight() {
        return msgRight;
    }

    public void setMsgRight(byte[] msgRight) {
        this.msgRight = msgRight;
    }

    public void runForward(){
        commMotorL.writeCommand(1, MeModuleCommunication.WRITEMODULE, speedDefault, stopInverseMotor);
        msgLeft = commMotorL.commandToByteArray();
        commMotorR.writeCommand(2, MeModuleCommunication.WRITEMODULE, speedBackDefault, stopMotor);
        msgRight = commMotorR.commandToByteArray();
    }

    public void stop(){
        commMotorL.writeCommand(1, MeModuleCommunication.WRITEMODULE, stopMotor, stopMotor);
        msgLeft = commMotorL.commandToByteArray();
        commMotorR.writeCommand(2, MeModuleCommunication.WRITEMODULE, stopMotor, stopMotor);
        msgRight = commMotorR.commandToByteArray();
    }

    public void runBackward(){
        commMotorL.writeCommand(1, MeModuleCommunication.WRITEMODULE, speedBackDefault, stopMotor);
        msgLeft = commMotorL.commandToByteArray();
        commMotorR.writeCommand(2, MeModuleCommunication.WRITEMODULE, speedDefault, stopInverseMotor);
        msgRight = commMotorR.commandToByteArray();
    }

    public void turnLeft(){
        commMotorL.writeCommand(1, MeModuleCommunication.WRITEMODULE, speedBackDefault, stopMotor);
        msgLeft = commMotorL.commandToByteArray();
        commMotorR.writeCommand(2, MeModuleCommunication.WRITEMODULE, speedBackDefault, stopMotor);
        msgRight = commMotorR.commandToByteArray();
    }

    public void turnRight(){
        commMotorL.writeCommand(1, MeModuleCommunication.WRITEMODULE, speedDefault, stopInverseMotor);
        msgLeft = commMotorL.commandToByteArray();
        commMotorR.writeCommand(2, MeModuleCommunication.WRITEMODULE, speedDefault, stopInverseMotor);
        msgRight = commMotorR.commandToByteArray();
    }

    public void runForwardCell(){
        runForward();
        //Time to speed
        stop();
    }

    public void turnLeftCell(){
        turnLeft();
        //Time 45º left
        stop();
    }

    public void turnRightCell(){
        turnRight();
        //Time 45º right
        stop();
    }
}
