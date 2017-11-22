package no.skunkworks.theremidi.midi;

/**
 * Created by espen.skjaeran on 22/11/2017.
 */

public abstract class MidiEvent {

    /**
     * Encodes this event to a particular channel.
     * @param channel the midi channel (1-16) to send to.
     * @return the encoded on-the-wire message
     */
    public abstract byte[] encode(byte channel);

}
