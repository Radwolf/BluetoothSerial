package org.rul.meapi.common;

import org.junit.Before;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rgonzalez on 28/10/2016.
 */

public class Notas {
    static int[] tones = {65, 73, 82, 87, 98, 110, 123, 131, 147, 165, 175, 196, 220, 247, 262, 294, 330, 349,
            392, 440, 494, 523, 587, 658, 698, 784, 880, 988, 1047, 1175, 1319, 1397, 1568, 1760, 1976,
            2093, 2349, 2637, 2794, 3136, 3520, 3951, 4186};
    static String[] letraNotas = {"C", "D", "E", "F", "G", "A", "B"};
    static String[] numeroNotas = {"2", "3", "4", "5", "6", "7", "8"};

    public static final Map<String, Integer> TONO;
    static{
        Map<String, Integer> auxTonos = new HashMap<>();
        for(int i = 0; i < numeroNotas.length && auxTonos.size() < tones.length; i++){
            for(int j = 0; j < letraNotas.length && auxTonos.size() < tones.length; j++){
                auxTonos.put(letraNotas[j] + numeroNotas[i], tones[auxTonos.size()]);
            }
        }
        System.out.println(auxTonos.toString());
        TONO = Collections.unmodifiableMap(auxTonos);
    }
}
