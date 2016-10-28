package org.rul.meapi.device;

import org.rul.meapi.model.CommandSequence;
import org.rul.meapi.model.CommandTimmer;

/**
 * Created by rgonzalez on 20/05/2016.
 */
public class MeMelodiesDevice {

    MeBuzzerDevice buzzerDevice;

    public MeMelodiesDevice(int index) {
        buzzerDevice = new MeBuzzerDevice("Buzzer device", index);
    }

    public CommandSequence play(String[] notas){
        CommandSequence commandSequence = new CommandSequence("Buzzer para reproducir melodias");
        for (String nota: notas) {
            commandSequence.addCommand(buzzerDevice.tocarTono(nota, null));
        }
        commandSequence.addCommand(buzzerDevice.stop());
        return commandSequence;
    }


}
