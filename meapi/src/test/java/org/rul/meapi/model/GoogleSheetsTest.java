package org.rul.meapi.model;

import junit.framework.Assert;

import org.junit.Test;
import org.rul.meapi.common.Utils;
import org.rul.meapi.service.GoogleSheetsService;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by rgonzalez on 24/05/2016.
 */
public class GoogleSheetsTest {

    @Test
    public void pruebaBasicaTest(){
        ByteBuffer faceSheet = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
        faceSheet.put(new byte[]{
                0, 0, 0, 0, 48, 72, 68, 34,
                68, 72, 48, 0, 0, 0, 0, 0});
        ByteBuffer cadenaFace = GoogleSheetsService.getFaceSheets("Corazon");
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
