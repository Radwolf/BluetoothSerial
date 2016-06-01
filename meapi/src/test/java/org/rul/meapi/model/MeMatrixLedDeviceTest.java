package org.rul.meapi.model;

import org.junit.Assert;
import org.junit.Test;
import org.rul.meapi.device.MeMatrixLedDevice;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by rgonzalez on 23/05/2016.
 */
public class MeMatrixLedDeviceTest {

    private static final ByteBuffer COMMAND_PINTAR_FACE_CORAZON =
            ByteBuffer.allocate(MeMatrixLedDevice.LENGTH_INSTRUCTION_MATRIX_LED + 9).order(ByteOrder.LITTLE_ENDIAN)
                .put(new byte[]{(byte) 0xff, (byte)0x55, (byte)0x17, 1, 2, 29, 1, 2, 0, 0,
                        0, 0, 0, 0, 48, 72, 68, 34,
                        68, 72, 48, 0, 0, 0, 0, 0});

//    @Test
//    public void pintarCorazon(){
//        MeMatrixLedDevice meMatrixLedDevice = new MeMatrixLedDevice("Face corazón", 1);
//        CommandSimple command = meMatrixLedDevice.pintarFace("Corazon");
//        Assert.assertEquals(command.getCadena().length, command.checkByteCadena(COMMAND_PINTAR_FACE_CORAZON) );
//        Assert.assertTrue(command.isCadenaValid(COMMAND_PINTAR_FACE_CORAZON));
//    }
}
