package org.rul.meapi.model;

import org.junit.Assert;
import org.junit.Test;
import org.rul.meapi.common.MeConstants;
import org.rul.meapi.device.MeMotorDevice;
import org.rul.meapi.device.MeTransmission2MotorDevice;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by rgonzalez on 23/05/2016.
 */
public class MeTransmission2MotorDeviceTest {

    @Test
    public void deviceTransmission2MotorRunForward(){
        System.out.println("Transmission run forward");
        MeTransmission2MotorDevice transmission = new MeTransmission2MotorDevice(MeConstants.PORT_M1, MeConstants.PORT_M2);
        transmission.runForward(100).print();
    }

    @Test
    public void deviceTransmission2MotorRunBackward(){
        System.out.println("Transmission run backward");
        MeTransmission2MotorDevice transmission = new MeTransmission2MotorDevice(MeConstants.PORT_M1, MeConstants.PORT_M2);
        transmission.runBackward(100).print();
    }

    @Test
    public void deviceTransmission2MotorTurnRight(){
        System.out.println("Transmission run turn right");
        MeTransmission2MotorDevice transmission = new MeTransmission2MotorDevice(MeConstants.PORT_M1, MeConstants.PORT_M2);
        transmission.turnRight(100).print();
    }

    @Test
    public void deviceTransmission2MotorTurnLeft(){
        System.out.println("Transmission run turn left");
        MeTransmission2MotorDevice transmission = new MeTransmission2MotorDevice(MeConstants.PORT_M1, MeConstants.PORT_M2);
        transmission.turnLeft(100).print();
    }

    @Test
    public void deviceTransmission2MotorStop(){
        System.out.println("Transmission stop");
        MeTransmission2MotorDevice transmission = new MeTransmission2MotorDevice(MeConstants.PORT_M1, MeConstants.PORT_M2);
        transmission.stop().print();
    }

    @Test
    public void deviceTransmission2MotorRunForwardCell(){
        System.out.println("Transmission run forward cell");
        MeTransmission2MotorDevice transmission = new MeTransmission2MotorDevice(MeConstants.PORT_M1, MeConstants.PORT_M2);
        transmission.setSpeedForCell(100);
        transmission.setTimeForCell(10000);
        transmission.runForwardCell().print();
    }

    @Test
    public void deviceTransmission2MotorTurnLeft90Grados(){
        System.out.println("Transmission run turn left 90 grados");
        MeTransmission2MotorDevice transmission = new MeTransmission2MotorDevice(MeConstants.PORT_M1, MeConstants.PORT_M2);
        transmission.setSpeedForTurn(100);
        transmission.turnLeft90Grados().print();
    }

    @Test
    public void deviceTransmission2MotorTurnRight90Grados(){
        System.out.println("Transmission run turn right 90 grados");
        MeTransmission2MotorDevice transmission = new MeTransmission2MotorDevice(MeConstants.PORT_M1, MeConstants.PORT_M2);
        transmission.setSpeedForTurn(100);
        transmission.turnRigth90Grados().print();
    }
}
