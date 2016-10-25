package org.rul.meapi.model;

import org.junit.Assert;
import org.junit.Test;
import org.rul.meapi.device.MeMatrixLedDevice;
import org.rul.meapi.service.GoogleSheetsService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by rgonzalez on 23/05/2016.
 */
public class MeMatrixLedDeviceTest {

    private static final ByteBuffer COMMAND_PINTAR_FACE_CARA =
            ByteBuffer.allocate(MeMatrixLedDevice.LENGTH_INSTRUCTION_MATRIX_LED + 3).order(ByteOrder.LITTLE_ENDIAN)
                .put(new byte[]{(byte) 0xff, (byte)0x55, (byte)0x17, 0, 2, 41, 1, 2, 0, 0,
                        0, 32, 68, 66, 33, 1, 1, 1,
                        1, 1, 1, 33, 66, 68, 32, 0});

    @Test
    public void pintarCorazon() throws FileNotFoundException {
        MeMatrixLedDevice meMatrixLedDevice = new MeMatrixLedDevice("Face corazón", 0);
        InputStream in = new FileInputStream(System.getProperty("user.home").concat("/.credentials/sheets.googleapis.com-java-quickstart.json/client_secret_api_otro.json"));
        ByteBuffer cadenaFace = GoogleSheetsService.getFaceSheets(in, "Cara :)");
        CommandSimple command = meMatrixLedDevice.pintarFace(cadenaFace, 0, 0);
        Assert.assertEquals(command.getCadena().length, command.checkByteCadena(COMMAND_PINTAR_FACE_CARA) );
        Assert.assertTrue(command.isCadenaValid(COMMAND_PINTAR_FACE_CARA));
    }
}
