package org.rul.meapi.model;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Rul on 22/05/2016.
 */
public class ValidateByteTest {

    @Test
    public void pruebasArrayByteTest(){
        ByteBuffer bb = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).put(new byte[]{0, 0, 0, 0, 0, 0, 0, 1});
        for(int i = 0; i < bb.limit(); i++){
            System.out.printf("%s ", bb.get(i));
        }
    }
}
