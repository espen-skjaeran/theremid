package no.skunkworks.theremidi.uiadapters;

import android.util.Log;
import android.widget.SeekBar;

import no.skunkworks.theremidi.MidiMux;
import no.skunkworks.theremidi.midi.PitchBendChange;

/**
 * Listening for seekbar events and emitting events to Midi.
 * Will know what events etc to send.
 */

public class SeekbarToBendAdapter implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "SeekbarToBendAdapter";
    private String inputName;
    private MidiMux midiMux;

    public SeekbarToBendAdapter(MidiMux mux, String name) {
        this.midiMux = mux;
        this.inputName = name;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        Log.d(TAG, "Got new seekbar level " + i);
        midiMux.sendEvent(new PitchBendChange((short) i));
    }

    public void onStartTrackingTouch(SeekBar var1) {}

    public void onStopTrackingTouch(SeekBar var1) {}
}
