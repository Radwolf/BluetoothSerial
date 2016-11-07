package org.rul.meapi.model;

import org.junit.Test;
import org.rul.meapi.common.MeConstants;
import org.rul.meapi.device.MeTransmission2MotorDevice;

/**
 * Created by rgonzalez on 23/05/2016.
 */
public class MeMelodiesDeviceTest {

    @Test
    public void deviceMelodies(){
        System.out.println("Transmission run forward");
        MeTransmission2MotorDevice transmission = new MeTransmission2MotorDevice(MeConstants.PORT_M1, MeConstants.PORT_M2);
        transmission.runForward(100).print();
    }

}
