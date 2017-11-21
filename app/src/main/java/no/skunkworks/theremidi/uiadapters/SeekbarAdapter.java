package no.skunkworks.theremidi.uiadapters;

import android.util.Log;
import android.widget.SeekBar;

/**
 * Listening for seekbar events and emitting events to Midi.
 * Will know what events etc to send.
 */

public class SeekbarAdapter implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "SeekbarAdapter";
    private String inputName;

    public SeekbarAdapter(String name) {
        this.inputName = name;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        Log.d(TAG, "Got new seekbar level " + i);
    }

    public void onStartTrackingTouch(SeekBar var1) {}

    public void onStopTrackingTouch(SeekBar var1) {}
}
