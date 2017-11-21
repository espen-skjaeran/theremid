package no.skunkworks.theremidi;

import android.content.Context;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Main interface to midi stuff.
 * Gets a list of connected devices, revceives updates on new/disconnected devices,
 * sends events.
 */

public class MidiMux extends MidiManager.DeviceCallback {
    private static String TAG = "MidiMux";
    
    private MidiManager midiManager;

    private static MidiMux INSTANCE;
    private HashMap<String, MidiDeviceInfo> devices = new HashMap<>();

    public static synchronized MidiMux getInstance(Context context) {
       if(INSTANCE == null) {
           INSTANCE = new MidiMux(context);
       }
        return INSTANCE;
    }

    public MidiMux(Context context) {
        midiManager = (MidiManager)context.getSystemService(Context.MIDI_SERVICE);

    }

    public List<String> getConnectedDevices() {
        devices.clear();
        ArrayList<String> out = new ArrayList<>();
        MidiDeviceInfo[] infos = midiManager.getDevices();
        for(MidiDeviceInfo info : infos) {
            if(info.getInputPortCount() > 0) {
                Log.d(TAG, "Got device: "+ info.getProperties());
                String name = info.getProperties().getString(MidiDeviceInfo.PROPERTY_NAME);
               devices.put(name, info);
               out.add(name);
            }
        }

        //Dummy
        //out.add("Espensynth1");
        //out.add("Epensynth2");
        return out;
    }

    @Override
    public void onDeviceAdded( MidiDeviceInfo info ) {
       Log.d(TAG, "Device Added! " + info);
       devices.remove(info.getProperties().getString(MidiDeviceInfo.PROPERTY_NAME));
    }

    @Override
    public void onDeviceRemoved( MidiDeviceInfo info ) {
        Log.d(TAG, "Device removed! " + info);
        devices.put(info.getProperties().getString(MidiDeviceInfo.PROPERTY_NAME), info);
    }

}