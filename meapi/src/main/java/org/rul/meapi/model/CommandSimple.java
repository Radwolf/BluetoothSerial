package org.rul.meapi.model;

import org.rul.meapi.Utils;

/**
 * Created by rgonzalez on 20/05/2016.
 */
public class CommandSimple {

    private int[] cadena;
    private String name;
    private int index;
    private int secondsTimer;
    private int tipo;  //Escritura o Lectura

    public CommandSimple(String name, int lenght) {
        this.name = name;
        this.secondsTimer = 0;
        this.cadena = new int[lenght];
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

    public void setElementCadena(int position, int value){
        cadena[position] = value;
    }

    public boolean isCommandComplet(){
        boolean commandComplet = false;
        for(int i = 0; i < cadena.length; i++){
            commandComplet = !(cadena[i] == -1);
        }
        return commandComplet;
    }

    public String toString(byte[] msgCommand){
        return Utils.bytesToHexString(msgCommand);
    }

    public byte[] toByteArray(){
        final byte[] bytes = new byte[cadena.length];
        for(int i = 0; i < cadena.length; i++) {
            bytes[i] = (byte) cadena[i];
        }
        return bytes;
    }
}
