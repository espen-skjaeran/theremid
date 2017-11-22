package no.skunkworks.theremidi;

import android.content.Context;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiDeviceStatus;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import no.skunkworks.theremidi.midi.AllNotesOff;
import no.skunkworks.theremidi.midi.MidiEvent;
import no.skunkworks.theremidi.midi.ResetAllControllers;

/**
 * Main interface to midi stuff.
 * Gets a list of connected devices, revceives updates on new/disconnected devices,
 * sends events.
 * TODO: Broadcast 'connected' event to UIs.
 */

public class MidiMux extends MidiManager.DeviceCallback
                     implements MidiManager.OnDeviceOpenedListener{

    public static class MidiConnectionException extends Exception {
        public MidiConnectionException(String msg) { super(msg); }
    }

    public interface MidiConnectionListener {
        public void connected(String name, int port);
        public void disconnected(String name);
    }


    private static String TAG = "MidiMux";
    
    private MidiManager midiManager;
    private MidiDevice midiDevice;
    private MidiInputPort remoteInputPort;
    private int remoteInputPortNo;
    private Handler handler;
    private static MidiMux INSTANCE;
    private HashMap<String, MidiDeviceInfo> devices = new HashMap<>();
    private Set<MidiConnectionListener> listeners = new HashSet<>();

    public static synchronized MidiMux getInstance(Context context) {
       if(INSTANCE == null) {
           INSTANCE = new MidiMux(context);
       }
        return INSTANCE;
    }

    public static void shutdown() {
        Log.d(TAG, "Shutting down MIDI");
        
        INSTANCE.midiManager.unregisterDeviceCallback(INSTANCE);
        if(INSTANCE.remoteInputPort != null) try {
            INSTANCE.sendEvent(new AllNotesOff());
            INSTANCE.sendEvent(new ResetAllControllers());
            INSTANCE.remoteInputPort.close();
            INSTANCE.midiDevice.close();
        }
        catch(IOException io) {
            Log.w(TAG, "Trouble closing remote port: " + io);
        }
    }

    public MidiMux(Context context) {
        this.handler = new Handler();
        midiManager = (MidiManager)context.getSystemService(Context.MIDI_SERVICE);
        MidiDeviceInfo[] infos = midiManager.getDevices();
        for(MidiDeviceInfo info : infos) {
            if(info.getInputPortCount() > 0) {
                Log.d(TAG, "Got device: "+ info.getProperties());
                String name = info.getProperties().getString(MidiDeviceInfo.PROPERTY_PRODUCT);
                devices.put(name, info);
            }
        }
        midiManager.registerDeviceCallback(this, handler);
    }

    public void addListener(MidiConnectionListener listener) {
        listeners.add(listener);
    }

    public void removeListener(MidiConnectionListener listener) {
        listeners.remove(listener);
    }

    public List<String> getAvailableDevices() {
        devices.clear();
        ArrayList<String> out = new ArrayList<>();
        for(MidiDeviceInfo info : devices.values()) {

            if(info.getInputPortCount() > 0) {
                Log.d(TAG, "Got device: "+ info.getProperties() +
                " with ports " + Arrays.asList(info.getPorts()));
                String name = info.getProperties().getString(MidiDeviceInfo.PROPERTY_PRODUCT);
               devices.put(name, info);
               out.add(name);
            }
            else {
                Log.i(TAG, "Info has no input ports! " + info);
            }
        }
        return out;
    }

    public String getConnectedDevice() {
        return midiDevice == null ? null : midiDevice.getInfo().getProperties().getString(MidiDeviceInfo.PROPERTY_NAME);
    }

    public int getConnectedPort() {

        return remoteInputPort == null ? 0 : remoteInputPort.getPortNumber();
    }

    public void connectToDevice(String productId, int port) throws MidiConnectionException {
        MidiDeviceInfo info = devices.get(productId);
        if(info == null) {
            throw new MidiConnectionException(productId + " is not among available devices!");
        }
        if(port < 1 || port > 16) {
            throw new MidiConnectionException("Midi port " + port + " illegal");
        }
        midiManager.openDevice(info, this, handler);
        this.remoteInputPortNo = port;
    }


    public void sendEvent(MidiEvent evt) {
        byte[] encoded = evt.encode((byte) remoteInputPortNo);
        try {
            remoteInputPort.send(encoded, 0, encoded.length);
        } catch (IOException e) {
            Log.w(TAG, "Trouble sending event: " + e);
        }
    }

    @Override
    public void onDeviceAdded( MidiDeviceInfo info ) {
       Log.d(TAG, "Device Added! " + info);
       devices.remove(info.getProperties().getString(MidiDeviceInfo.PROPERTY_NAME));
    }

    @Override
    public void onDeviceRemoved( MidiDeviceInfo info ) {
        Log.d(TAG, "Device removed! " + info);
        String name = info.getProperties().getString(MidiDeviceInfo.PROPERTY_NAME);
        devices.put(name, info);
        if(name.equals(getConnectedDevice())) {
            for (MidiConnectionListener l : listeners) {
                l.disconnected(name);
            }
        }
    }

    @Override
    public void onDeviceStatusChanged(MidiDeviceStatus status) {
        Log.d(TAG,"Got status for device! " + status);
    }

    @Override
    public void onDeviceOpened(MidiDevice device) {
        midiDevice = device;
        Log.d(TAG, "Opened MIDI device! " + device);
        remoteInputPort =  midiDevice.openInputPort(remoteInputPortNo);
        if(remoteInputPort != null) {
            Log.i(TAG, "Got input port of device!" + midiDevice.getInfo());
            for (MidiConnectionListener l : listeners) {
                l.connected(getConnectedDevice(), getConnectedPort());
            }
        }
        else {
            Log.w(TAG, "Opening input port of device failed!" + midiDevice.getInfo());
        }
    }

}