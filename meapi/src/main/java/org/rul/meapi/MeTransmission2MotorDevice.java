package org.rul.meapi;

import org.rul.meapi.model.CommandSequence;
import org.rul.meapi.model.CommandTimmer;

import java.util.Calendar;

/**
 * Created by rgonzalez on 20/05/2016.
 */
public class MeTransmission2MotorDevice {

    MeMotorDevice motorRight;
    MeMotorDevice motorLeft;

    int speedForCell;
    long timeForCell;

    public MeTransmission2MotorDevice(int portRight, int portLeft) {
        motorRight = new MeMotorDevice("Motor right", portRight);
        motorLeft = new MeMotorDevice("Motor left", portLeft);
    }

    public CommandSequence runForward(int speed){
        CommandSequence commandSequence = new CommandSequence("Transmision run forward");
        commandSequence.addCommand(motorRight.giroDirecto(speed));
        commandSequence.addCommand(motorLeft.giroInverso(speed));
        return commandSequence;
    }

    public CommandSequence runBackward(int speed){
        CommandSequence commandSequence = new CommandSequence("Transmision run backward");
        commandSequence.addCommand(motorRight.giroInverso(speed));
        commandSequence.addCommand(motorLeft.giroDirecto(speed));
        return commandSequence;
    }

    public CommandSequence turnRight(int speed){
        CommandSequence commandSequence = new CommandSequence("Transmision turn right");
        commandSequence.addCommand(motorRight.giroInverso(speed));
        commandSequence.addCommand(motorLeft.giroDirecto(speed));
        return commandSequence;
    }

    public CommandSequence turnLeft(int speed){
        CommandSequence commandSequence = new CommandSequence("Transmision turn left");
        commandSequence.addCommand(motorRight.giroDirecto(speed));
        commandSequence.addCommand(motorLeft.giroInverso(speed));
        return commandSequence;
    }

    public CommandSequence stop(){
        CommandSequence commandSequence = new CommandSequence("Stop");
        commandSequence.addCommand(motorRight.stop());
        commandSequence.addCommand(motorLeft.stop());
        return commandSequence;
    }

    public CommandSequence runForwardCell(){
        CommandSequence commandSequence = new CommandSequence("Forward one cell");
        commandSequence.addCommand(motorRight.giroDirecto(speedForCell));
        commandSequence.addCommand(motorLeft.giroInverso(speedForCell));
        commandSequence.addCommand(new CommandTimmer("Tiempo de desplazamiento calibrado", MeConstants.WRITEMODULE, 3, timeForCell));
        commandSequence.addCommand(motorRight.stop());
        commandSequence.addCommand(motorLeft.stop());
        return commandSequence;
    }
}
