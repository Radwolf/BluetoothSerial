package org.rul.meapi.model;

import org.junit.Assert;
import org.junit.Test;
import org.rul.meapi.MeConstants;
import org.rul.meapi.MeMotorDevice;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by rgonzalez on 23/05/2016.
 */
public class MeMotorDeviceTest {

    private static final ByteBuffer COMMAND_GIRO_M1_DIRECTO =
            ByteBuffer.allocate(MeMotorDevice.LENGTH_INSTRUCTION_DCMOTOR + 3).order(ByteOrder.LITTLE_ENDIAN)
                .put(new byte[]{(byte) 0xff, (byte)0x55, (byte)0x06, (byte)0x02, (byte)0x02, (byte)0x0a, (byte)0x09, (byte)0x64, (byte)0x00});
    //ff 55 06 60 02 0a 09 ff 00
    private static final ByteBuffer COMMAND_GIRO_M1_INVERSO =
            ByteBuffer.allocate(MeMotorDevice.LENGTH_INSTRUCTION_DCMOTOR + 3).order(ByteOrder.LITTLE_ENDIAN)
                    .put(new byte[]{(byte) 0xff, (byte)0x55, (byte)0x06, (byte)0x01, (byte)0x02, (byte)0x0a, (byte)0x09, (byte)0x9c, (byte)0xff});
    private static final ByteBuffer COMMAND_GIRO_M2_DIRECTO =
            ByteBuffer.allocate(MeMotorDevice.LENGTH_INSTRUCTION_DCMOTOR + 3).order(ByteOrder.LITTLE_ENDIAN)
                    .put(new byte[]{(byte) 0xff, (byte)0x55, (byte)0x06, (byte)0x02, (byte)0x02, (byte)0x0a, (byte)0x0a, (byte)0x64, (byte)0x00});
    //ff 55 06 60 02 0a 09 ff 00
    private static final ByteBuffer COMMAND_GIRO_M2_INVERSO =
            ByteBuffer.allocate(MeMotorDevice.LENGTH_INSTRUCTION_DCMOTOR + 3).order(ByteOrder.LITTLE_ENDIAN)
                    .put(new byte[]{(byte) 0xff, (byte)0x55, (byte)0x06, (byte)0x01, (byte)0x02, (byte)0x0a, (byte)0x0a, (byte)0x9c, (byte)0xff});

    @Test
    public void deviceMotor1GiroDirecto(){
        MeMotorDevice motor1 = new MeMotorDevice("Motor 1 giro directo", MeConstants.PORT_M1, 2);
        CommandSimple command = motor1.giroDirecto(100);
        Assert.assertEquals(command.getCadena().length, command.checkByteCadena(COMMAND_GIRO_M1_DIRECTO) );
        Assert.assertTrue(command.isCadenaValid(COMMAND_GIRO_M1_DIRECTO));
    }

    @Test
    public void deviceMotor1GiroInverso(){
        MeMotorDevice motor1 = new MeMotorDevice("Motor 1 giro inverso", MeConstants.PORT_M1, 1);
        CommandSimple command = motor1.giroInverso(100);
        Assert.assertEquals(command.getCadena().length, command.checkByteCadena(COMMAND_GIRO_M1_INVERSO) );
        Assert.assertTrue(command.isCadenaValid(COMMAND_GIRO_M1_INVERSO));
    }

    @Test
    public void deviceMotor2GiroDirecto(){
        MeMotorDevice motor2 = new MeMotorDevice("Motor 2 giro directo", MeConstants.PORT_M2, 2);
        CommandSimple command = motor2.giroDirecto(100);
        Assert.assertEquals(command.getCadena().length, command.checkByteCadena(COMMAND_GIRO_M2_DIRECTO) );
        Assert.assertTrue(command.isCadenaValid(COMMAND_GIRO_M2_DIRECTO));
    }

    @Test
    public void deviceMotor2GiroInverso(){
        MeMotorDevice motor2 = new MeMotorDevice("Motor 2 giro inverso", MeConstants.PORT_M2, 1);
        CommandSimple command = motor2.giroInverso(100);
        Assert.assertEquals(command.getCadena().length, command.checkByteCadena(COMMAND_GIRO_M2_INVERSO) );
        Assert.assertTrue(command.isCadenaValid(COMMAND_GIRO_M2_INVERSO));
    }

}
