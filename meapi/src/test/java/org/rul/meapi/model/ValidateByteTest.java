package org.rul.meapi.model;

import org.junit.Ignore;
import org.junit.Test;
import org.rul.meapi.common.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Rul on 22/05/2016.
 */
public class ValidateByteTest {

    @Ignore
    @Test
    public void pruebasArrayByteTest(){
        ByteBuffer bb = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).put(new byte[]{0, 0, 0, 0, 0, 0, 0, 1});
        for(int i = 0; i < bb.limit(); i++){
            System.out.printf("%s ", bb.get(i));
        }
    }

    @Test
    public void pruebaIntToByte(){
        byte b = Utils.intToByte(-1);
        System.out.print(Utils.bytesToHexString(new byte[]{b}));
    }

    @Test
    public void pruebaIntArrayToHexString(){
        int[] faceInt = new int[]{0, 32, 68, 66, 33, 1, 1, 1, 1, 1, 1, 33, 66, 68, 32, 0};
        byte [] faceByte = new byte[faceInt.length];
        for(int i = 0; i < faceInt.length; i++){
            faceByte[i] = Utils.intToByte(faceInt[i]);
        }
        System.out.print(Utils.bytesToHexString(faceByte));
    }

    // 00 20 44 42 21 01 01 01 01 01 01 21 42 44 20 00
    // 00 20 44 42 21 01 01 01 01 01 01 21 42 44 20 00
}
