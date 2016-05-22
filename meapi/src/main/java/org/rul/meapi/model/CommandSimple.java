package org.rul.meapi.model;

import org.rul.meapi.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by rgonzalez on 20/05/2016.
 */
public class CommandSimple {

    private int[] cadena;
    private String name;
    private int index;
    private int secondsTimer;
    private int tipo;  //Escritura o Lectura
    private ByteBuffer validateMask;

    public CommandSimple(String name, int lenght) {
        this.name = name;
        this.secondsTimer = 0;
        this.cadena = new int[lenght+3];
        validateMask = ByteBuffer.allocate(lenght + 3).order(ByteOrder.LITTLE_ENDIAN);
        initCadena();
    }

    private void initCadena() {
        for(int i = 0; i < cadena.length; i++){
            cadena[i] = -1;
        }
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setSecondsTimer(int secondsTimer) {
        this.secondsTimer = secondsTimer;
    }

    public ByteBuffer getValidateMask() {
        return validateMask;
    }

    public String maskToString(){
        StringBuffer maskString = new StringBuffer();
        for(int i = 0; i < validateMask.limit(); i++){
            maskString.append(String.format("%s ", validateMask.get(i)));
        }
        return maskString.toString();
    }
    public void setElementCadena(int position, int value){
        validateMask.put(position, (byte) 1);
        cadena[position] = value;
    }

    public boolean isCommandComplet(){
        boolean commandComplet = false;
        for(int i = 0; i < cadena.length; i++){
            commandComplet = !(cadena[i] == -1);
        }
        return commandComplet;
    }

    public String toString(){
        return Utils.bytesToHexString(this.toByteArray());
    }

    public byte[] toByteArray(){
        final byte[] bytes = new byte[cadena.length];
        for(int i = 0; i < cadena.length; i++) {
            bytes[i] = (byte) cadena[i];
        }
        return bytes;
    }
}
