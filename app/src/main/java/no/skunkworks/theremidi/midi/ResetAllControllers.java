package no.skunkworks.theremidi.midi;

/**
 * Created by espen.skjaeran on 22/11/2017.
 */

public class ResetAllControllers extends MidiEvent {

    public ResetAllControllers() {}

    @Override
    public byte[] encode(byte channel) {
        byte[] buf = new byte[3];
        buf[0] = (byte) (0xB0 + channel);
        buf[1] = 121;
        buf[2] = 0;
        return buf;
    }
    public String toString() {
        return "Reset all controllers";
    }
}
