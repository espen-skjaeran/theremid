package no.skunkworks.theremidi.midi;

/**
 * Created by espen.skjaeran on 22/11/2017.
 */

public class AllNotesOff extends MidiEvent {

    public AllNotesOff() {}


    @Override
    public byte[] encode(byte channel) {
        byte[] buf = new byte[3];
        buf[0] = (byte) (0xB0 + channel);
        buf[1] = 123;
        buf[2] = 0;
        return buf;
    }
}
