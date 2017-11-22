package no.skunkworks.theremidi.midi;

/**
 * Pitch Bend Change.
 * This message is sent to indicate a change in the pitch bender (wheel or lever, typically).
 * The pitch bender is measured by a fourteen bit value. Center (no pitch change) is 2000H.
 * Sensitivity is a function of the receiver, but may be set using RPN 0.
 * (lllllll) are the least significant 7 bits. (mmmmmmm) are the most significant 7 bits.
 * 1110nnnn	0lllllll 0mmmmmmm
 */
public class PitchBendChange extends MidiEvent {

    private short pitch;
    public PitchBendChange(short pitch) {
        this.pitch = pitch;
    }


    @Override
    public byte[] encode(byte channel) {
        byte[] buffer = new byte[3];
        buffer[0] = (byte)(0xE0 + (channel - 1)); // note on
        //Scaling to 14 bit by shifting 8 >> 6
        int offsetpitch = 0x2000 + (pitch << 6);
        buffer[1] = (byte) (offsetpitch & 0x7F);
        buffer[2] = (byte) ((offsetpitch >> 7) & 0x7F);
        return buffer;
    }
    public String toString() {
        return "Pitchbend, pitch " + pitch;
    }
}
