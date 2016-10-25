package org.rul.meapi.model;

import junit.framework.Assert;

import org.junit.Test;
import org.rul.meapi.common.Utils;
import org.rul.meapi.service.GoogleSheetsService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by rgonzalez on 24/05/2016.
 */

/**
 * Para poder utilizar la api de drive desde android debemos pasarle este inputstream que tenemos en el directorio resources
 * InputStream in = GoogleSheetsService.class.getResourceAsStream("/client_secret_api.json");
 *
 * Para los test desde java sería este otro que lo tenemos en un directorio local o lo podemos conseguir desde la consola de google apis
 * InputStream in = new FileInputStream(System.getProperty("user.home").concat("/.credentials/sheets.googleapis.com-java-quickstart.json/client_secret_api_otro.json"));
 */
public class GoogleSheetsTest {

    @Test
    public void pruebaBasicaTest() throws FileNotFoundException {
        ByteBuffer faceSheet = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
        faceSheet.put(new byte[]{
                0, 32, 68, 66, 33, 1, 1, 1,
                1, 1, 1, 33, 66, 68, 32, 0});
        InputStream in = new FileInputStream(System.getProperty("user.home").concat("/.credentials/sheets.googleapis.com-java-quickstart.json/client_secret_api_otro.json"));
        ByteBuffer cadenaFace = GoogleSheetsService.getFaceSheets(in, "Cara :)");
        System.out.println(Utils.bytesToHexString(faceSheet.array()));
        System.out.println(Utils.bytesToHexString(cadenaFace.array()));
        int position;
        if(faceSheet.limit() == cadenaFace.limit()) {
            for (position = 0; position < faceSheet.limit()
                    && cadenaFace.get(position) == faceSheet.get(position); position++) {
            }
        }else{
            position = -1;
        }
        Assert.assertEquals(cadenaFace.limit(), position);
    }
}
