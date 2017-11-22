package no.skunkworks.theremidi.midi;

/**
 * Created by espen.skjaeran on 22/11/2017.
 */

public enum Note {
    C1((byte)60);

    private Note(byte val) { this.byteValue = val; }
    private byte byteValue;

    public byte getByteValue() {
        return byteValue;
    }
}
