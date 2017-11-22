package no.skunkworks.theremidi.midi;
/**
 * Created by espen.skjaeran on 22/11/2017.
 */

public class NoteOnEvent extends MidiEvent {

    private Note note;
    private byte velocity;

    public NoteOnEvent(Note note, byte velocity) {
        this.note = note;
        this.velocity = velocity;
    }

    @Override
    public byte[] encode(byte channel) {
        byte[] buffer = new byte[3];
        buffer[0] = (byte)(0x90 + (channel - 1)); // note on
        buffer[1] = note.getByteValue();
        buffer[2] = velocity;
        return buffer;
    }

    public String toString() {
        return "NoteOn - " + note + ", velocity " + velocity;
    }
}
