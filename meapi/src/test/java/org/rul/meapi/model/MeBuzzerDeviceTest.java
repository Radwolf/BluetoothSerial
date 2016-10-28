package org.rul.meapi.model;

import org.junit.Assert;
import org.junit.Test;
import org.rul.meapi.common.MeConstants;
import org.rul.meapi.device.MeBuzzerDevice;
import org.rul.meapi.device.MeMotorDevice;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by rgonzalez on 23/05/2016.
 */
public class MeBuzzerDeviceTest {

    private static final ByteBuffer COMMAND_PLAY_TONE_C4 =
            ByteBuffer.allocate(MeBuzzerDevice.LENGTH_INSTRUCTION_BUZZER + 3).order(ByteOrder.LITTLE_ENDIAN)
                .put(new byte[]{(byte) 0xff, (byte)0x55, (byte)0x07, (byte)0x00, (byte)0x02, (byte)0x22, (byte)0x06, (byte)0x01, (byte)0xfa, (byte)0x00});
    //ff 55 07 00 02 06 01 fa 00

    @Test
    public void deviceBuzzerPlayTone(){
        MeBuzzerDevice buzzer = new MeBuzzerDevice("Play tone C4", 0);
        CommandSimple command = buzzer.tocarTono("C4", null);
        Assert.assertEquals(command.getCadena().length, command.checkByteCadena(COMMAND_PLAY_TONE_C4) );
        Assert.assertTrue(command.isCadenaValid(COMMAND_PLAY_TONE_C4));
    }

}
