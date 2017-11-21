package no.skunkworks.theremidi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main interface to midi stuff.
 * Gets a list of connected devices, revceives updates on new/disconnected devices,
 * sends events.
 */

public class MidiMux {

    public static final MidiMux INSTANCE = new MidiMux();

    public void event() {

    }


    public List<String> getConnectedDevices() {
        ArrayList<String> out = new ArrayList<>();
        out.add("Espensynth1");
        out.add("Epensynth2");
        return out;
    }
}
