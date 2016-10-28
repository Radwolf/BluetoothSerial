package org.rul.meapi.device;

import org.rul.meapi.common.MeConstants;
import org.rul.meapi.common.Notas;
import org.rul.meapi.common.Utils;
import org.rul.meapi.model.CommandSimple;

/**
 * Created by rgonzalez on 20/05/2016.
 */
public class MeBuzzerDevice extends MeDevice {

    public static final int COMMAND_PARAMETER_LENGTH = 2;
    public static final int COMMAND_PARAMETER_INDEX = 3;
    public static final int COMMAND_PARAMETER_ACTION = 4;
    public static final int COMMAND_PARAMETER_DEVICE = 5;

    public static final int COMMAND_PARAMETER_TONE_LOW = 6;
    public static final int COMMAND_PARAMETER_TONE_HIGH = 7;
    public static final int COMMAND_PARAMETER_BEAT_LOW = 8;
    public static final int COMMAND_PARAMETER_BEAT_HIGH = 9;

    public static final int LENGTH_INSTRUCTION_BUZZER= 7;

    private int index;
    private static final byte BEAT_LOW = (byte) 0xfa;
    private static final byte BEAT_HIGH = (byte) 0x00;

    public MeBuzzerDevice(String name, int index) {
        super(name);
        this.index = index;
    }

    protected void initCommand(CommandSimple command){
        super.initCommand(command);
        command.setElementCadena(COMMAND_PARAMETER_LENGTH, Utils.intToByte(LENGTH_INSTRUCTION_BUZZER));
        command.setElementCadena(COMMAND_PARAMETER_DEVICE, Utils.intToByte(MeConstants.DEV_BUZZER));
        command.setElementCadena(COMMAND_PARAMETER_ACTION, Utils.intToByte(command.getType()));
    }

    //Rotacion sentido de las agujas del reloj
    public CommandSimple tocarTono(String nota, Integer beat){
        CommandSimple command = new CommandSimple("Tocamos la nota " + nota, index, LENGTH_INSTRUCTION_BUZZER, MeConstants.WRITEMODULE);
        initCommand(command);
        command.setElementCadena(COMMAND_PARAMETER_INDEX, Utils.intToByte(command.getIndex()));
        setTono(command, nota);
        if(beat == null) {
            command.setElementCadena(COMMAND_PARAMETER_BEAT_LOW, Utils.intToByte(BEAT_LOW));
            command.setElementCadena(COMMAND_PARAMETER_BEAT_HIGH, Utils.intToByte(BEAT_HIGH));
        }
        return command;
    }

    //Para de sonar
    public CommandSimple stop(){
        return null;
    }

    private void setTono(CommandSimple command, String nota){
        int tono = Notas.TONO.get(nota);
        command.setElementCadena(COMMAND_PARAMETER_TONE_LOW, (byte) (tono & 0xff));
        command.setElementCadena(COMMAND_PARAMETER_TONE_HIGH, (byte) ((tono >> 8) & 0xff));
    }
}
