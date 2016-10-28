package org.rul.meapi.model;

import org.junit.Before;
import org.junit.Test;
import org.rul.meapi.common.MeConstants;
import org.rul.meapi.common.Notas;
import org.rul.meapi.common.Utils;
import org.rul.meapi.device.MeTransmission2MotorDevice;

import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Created by rgonzalez on 23/05/2016.
 */
public class MIDIPlayTest {

    enum Note {

        //REST, CA4, A4$, B4, C4, C4$, D4, D4$, E4, F4, F4$, G4, G4$, A5;
        REST, C2, D2, E2, F2, G2, A2, B2, C3, D3, E3, F3, G3, A3, B3, C4, D4, E4, F4, G4, A4, B4,
        C5, D5, E5, F5, G5, A5, B5, C6, D6, E6, F6, G6, A6, B6, C7, D7, E7, F7, G7, A7, B7, C8;
        public static final int SAMPLE_RATE = 16 * 1024; // ~16KHz
        public static final int SECONDS = 2;
        private byte[] sin = new byte[SECONDS * SAMPLE_RATE];

        Note() {
            int n = this.ordinal();
            if (n > 0) {
                double exp = ((double) n - 1) / 12d;
                double f = 440d * Math.pow(2d, exp);
                for (int i = 0; i < sin.length; i++) {
                    double period = (double)SAMPLE_RATE / f;
                    double angle = 2.0 * Math.PI * i / period;
                    sin[i] = (byte)(Math.sin(angle) * 127f);
                }
            }
        }

        public byte[] data() {
            return sin;
        }

    }

    @Before
    public void setup(){
    }

    @Test
    public void toneConversion() throws LineUnavailableException {
        int tone  = Notas.TONO.get("C4");
        System.out.println("Tone: " + tone);
        final AudioFormat af =
                new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, true);
        SourceDataLine line = AudioSystem.getSourceDataLine(af);
        line.open(af, Note.SAMPLE_RATE);
        line.start();
        //E2 G3  C4 B3 G3 D3 E3     C3 E3  F4 E4 C4 A3
/*        play(line, Note.valueOf("E2"), 500);
        play(line, Note.REST, 10);
        play(line, Note.valueOf("G3"), 500);
        play(line, Note.REST, 10);
        play(line, Note.valueOf("C4"), 500);
        play(line, Note.REST, 10);
        play(line, Note.valueOf("B3"), 500);
        play(line, Note.REST, 10);
        play(line, Note.valueOf("D3"), 500);
        play(line, Note.REST, 10);*/
        play(line, Note.valueOf("C4"), 500);
        play(line, Note.REST, 10);

        line.drain();
        line.close();
        //Tone: 262
        //toneLow = 06
        //toneHigh = 01
        byte toneLow = (byte) (tone & 0xFF);
        byte toneHigh = (byte) ((tone >> 8) & 0xFF);
        System.out.print(Utils.byteToHexString(toneLow) + ", " + Utils.byteToHexString(toneHigh));
    }

    @Test
    public void testMidi() throws LineUnavailableException {
        final AudioFormat af =
                new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, true);
        SourceDataLine line = AudioSystem.getSourceDataLine(af);
        line.open(af, Note.SAMPLE_RATE);
        line.start();

        for  (Note n : Note.values()) {
            play(line, n, 500);
            play(line, Note.REST, 10);
        }
        line.drain();
        line.close();
    }

    private static void play(SourceDataLine line, Note note, int ms) {
        ms = Math.min(ms, Note.SECONDS * 1000);
        int length = Note.SAMPLE_RATE * ms / 1000;
        int count = line.write(note.data(), 0, length);
    }
}
