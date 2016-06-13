package org.rul.meapi.model;

import org.rul.meapi.common.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by rgonzalez on 20/05/2016.
 */
public class CommandSimple extends CommandAbstract{

    private ByteBuffer byteCadena;
    private ByteBuffer validateMask;

    public CommandSimple(String name, int index, int lenght, int tipo) {
        super(name, tipo, index);
        byteCadena = ByteBuffer.allocate(lenght + 3).order(ByteOrder.LITTLE_ENDIAN);
        validateMask = ByteBuffer.allocate(lenght + 3).order(ByteOrder.LITTLE_ENDIAN);
    }

    public String maskToString(){
        StringBuffer maskString = new StringBuffer();
        for(int i = 0; i < validateMask.limit(); i++){
            maskString.append(String.format("%s ", validateMask.get(i)));
        }
        return maskString.toString();
    }

    /*
     * Setea un valor en una posicion de la cadena, y rellena la mascara de validacion
     */
    public void setElementCadena(int position, byte value){
        validateMask.put(position, (byte) 1);
        byteCadena.put(position, value);
    }

    /*
     * Nos retorna la array de bytes de la cadena
     */
    public byte[] getCadena() {
        return byteCadena.array();
    }

    /*
     * Nos devuelve la representacion en string de la cadena de bytes
     */
    public String toString(){
        return Utils.bytesToHexString(byteCadena.array());
    }

    /*
     * Nos valida si se han seteado valores en todas las posiciones de la cadena del comando
     */
    public boolean isCommandComplet(){
        boolean commandComplet = true;
        for(int i = 0; i < validateMask.limit() && commandComplet; i++){
            commandComplet = (validateMask.get(i) == 1);
        }
        return commandComplet;
    }

    /*
     * Nos indica si dos cadenas de byte son iguales
     */
    public boolean isCadenaValid(ByteBuffer byteBufferCheck){
        byteBufferCheck.rewind();
        byteCadena.rewind();
        return byteBufferCheck.equals(byteCadena);
    }

    /*
     * Checkea si dos cadenas de bytes son identicas en cuanto contenido retorna la posición que
     * difiere o el tamaño de la cadena si no ha encontrado diferencia
     */
    public int checkByteCadena(ByteBuffer byteBufferCheck){
        int position;
        System.out.printf("Comprobación cadena del comando %s%n", this.getName());
        System.out.println(String.format("Cadena a comparar: %s", Utils.bytesToHexString(byteBufferCheck.array())));
        System.out.println(String.format("Cadena original: %s", Utils.bytesToHexString(byteCadena.array())));
        System.out.println();
        if(byteBufferCheck.limit() == byteCadena.limit()) {
            for (position = 0; position < byteBufferCheck.limit()
                    && byteCadena.get(position) == byteBufferCheck.get(position); position++) {
            }
        }else{
            position = -1;
        }
        return position;
    }
}
