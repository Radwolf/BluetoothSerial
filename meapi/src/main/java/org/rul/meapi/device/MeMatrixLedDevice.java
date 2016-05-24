package org.rul.meapi.device;

import org.rul.meapi.model.CommandSimple;
import org.rul.meapi.service.GoogleSheetsService;

import java.nio.ByteBuffer;

/**
 * Created by rgonzalez on 20/05/2016.
 */
public class MeMatrixLedDevice extends MeDevice {

    public static final int COMMAND_PARAMETER_LENGTH = 2;
    public static final int COMMAND_PARAMETER_INDEX = 3;
    public static final int COMMAND_PARAMETER_ACTION = 4;
    public static final int COMMAND_PARAMETER_DEVICE = 5;

    public static final int COMMAND_PARAMETER_DESCONOCIDO_1 = 6;
    public static final int COMMAND_PARAMETER_DESCONOCIDO_2 = 7;
    public static final int COMMAND_PARAMETER_DESCONOCIDO_3 = 8;
    public static final int COMMAND_PARAMETER_DESCONOCIDO_4 = 9;

    public static final int LENGTH_INSTRUCTION_MATRIX_LED= 17;

    private int index;

    public MeMatrixLedDevice(String name, int index) {
        super(name);
        this.index = index;
    }

    protected void initCommand(CommandSimple command){
        super.initCommand(command);
        command.setElementCadena(COMMAND_PARAMETER_LENGTH, org.rul.meapi.common.Utils.intToByte(LENGTH_INSTRUCTION_MATRIX_LED));
        command.setElementCadena(COMMAND_PARAMETER_DEVICE, org.rul.meapi.common.Utils.intToByte(org.rul.meapi.common.MeConstants.DEV_MATRIX_LED));
        command.setElementCadena(COMMAND_PARAMETER_ACTION, org.rul.meapi.common.Utils.intToByte(command.getType()));
    }

    //Pinta una face creada previamente en una hoja de un documento google sheets
    public CommandSimple pintarFace(String face){
        //TODO: Debugar el commando matrix led para confirmar el lenght de la cadena, y el parametro length y desconocidos
        CommandSimple command = new CommandSimple(String.format("Matrix led face %s", face), index, LENGTH_INSTRUCTION_MATRIX_LED + 6, org.rul.meapi.common.MeConstants.WRITEMODULE);
        initCommand(command);
        command.setElementCadena(COMMAND_PARAMETER_INDEX, org.rul.meapi.common.Utils.intToByte(command.getIndex()));
        command.setElementCadena(COMMAND_PARAMETER_DESCONOCIDO_1, org.rul.meapi.common.Utils.intToByte(1));
        command.setElementCadena(COMMAND_PARAMETER_DESCONOCIDO_2, org.rul.meapi.common.Utils.intToByte(2));
        command.setElementCadena(COMMAND_PARAMETER_DESCONOCIDO_3, org.rul.meapi.common.Utils.intToByte(0));
        command.setElementCadena(COMMAND_PARAMETER_DESCONOCIDO_4, org.rul.meapi.common.Utils.intToByte(0));
        setMatrixFace(command, face);
        return command;
    }

    private void setMatrixFace(CommandSimple command, String face) {
        ByteBuffer cadenaFace = GoogleSheetsService.getFaceSheets(face);
        for(int i = 0; i < 16; i++){
            command.setElementCadena(COMMAND_PARAMETER_DESCONOCIDO_4 + i + 1, cadenaFace.get(i));
        }
    }

}
