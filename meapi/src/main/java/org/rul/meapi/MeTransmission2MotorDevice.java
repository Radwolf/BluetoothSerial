package org.rul.meapi;

import org.rul.meapi.model.CommandSequence;

/**
 * Created by rgonzalez on 20/05/2016.
 */
public class MeTransmission2MotorDevice {

    MeMotorDevice motorRight;
    MeMotorDevice motorLeft;

    public MeTransmission2MotorDevice(int portRight, int portLeft) {
        motorRight = new MeMotorDevice("Motor right", portRight);
        motorLeft = new MeMotorDevice("Motor left", portLeft);
    }

    public CommandSequence runForward(){
        CommandSequence commandSequence = new CommandSequence();
        commandSequence.addCommand(motorRight.giroDirecto((byte) 100));
        commandSequence.addCommand(motorLeft.giroInverso((byte) 156));
        return commandSequence;
    }


}
