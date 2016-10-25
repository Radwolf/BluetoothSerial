package org.rul.meapi.device;

import org.rul.meapi.common.MeConstants;
import org.rul.meapi.common.Utils;
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

    public static final int COMMAND_PARAMETER_PORT = 6;
    public static final int COMMAND_PARAMETER_DESCONOCIDO_2 = 7;
    public static final int COMMAND_PARAMETER_POSITION_X = 8;
    public static final int COMMAND_PARAMETER_POSITION_Y = 9;

    public static final int LENGTH_INSTRUCTION_MATRIX_LED= 23;

    private int index;

    public MeMatrixLedDevice(String name, int index) {
        super(name);
        this.index = index;
    }

    protected void initCommand(CommandSimple command){
        super.initCommand(command);
        command.setElementCadena(COMMAND_PARAMETER_LENGTH, Utils.intToByte(LENGTH_INSTRUCTION_MATRIX_LED));
        command.setElementCadena(COMMAND_PARAMETER_DEVICE, Utils.intToByte(MeConstants.DEV_MATRIX_LED));
        command.setElementCadena(COMMAND_PARAMETER_ACTION, Utils.intToByte(command.getType()));
    }

    //Pinta una face creada previamente en una hoja de un documento google sheets
    public CommandSimple pintarFace(ByteBuffer cadenaFace, int x, int y){
        //TODO: Debugar el commando matrix led para confirmar el lenght de la cadena, y el parametro length y desconocidos
        CommandSimple command = new CommandSimple(String.format("Matrix led face"), index, LENGTH_INSTRUCTION_MATRIX_LED, MeConstants.WRITEMODULE);
        initCommand(command);
        command.setElementCadena(COMMAND_PARAMETER_INDEX, Utils.intToByte(command.getIndex()));
        command.setElementCadena(COMMAND_PARAMETER_PORT, Utils.intToByte(MeConstants.PORT_1));
        command.setElementCadena(COMMAND_PARAMETER_DESCONOCIDO_2, Utils.intToByte(2));
        command.setElementCadena(COMMAND_PARAMETER_POSITION_X, Utils.intToByte(x));
        command.setElementCadena(COMMAND_PARAMETER_POSITION_Y, Utils.intToByte(y));
        setMatrixFace(command, cadenaFace);
        return command;
    }

    private void setMatrixFace(CommandSimple command, ByteBuffer cadenaFace) {
        for(int i = 0; i < 16; i++){
            command.setElementCadena(COMMAND_PARAMETER_POSITION_Y + i + 1, cadenaFace.get(i));
        }
    }

}
