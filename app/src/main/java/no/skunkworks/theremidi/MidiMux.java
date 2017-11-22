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
    private int midiChannel;
    private Handler handler;
    private static MidiMux INSTANCE;
    //TODO: Make List so we preserve order.
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
        INSTANCE.disconnect();
    }

    public MidiMux(Context context) {
        this.handler = new Handler();
        midiManager = (MidiManager)context.getSystemService(Context.MIDI_SERVICE);
        MidiDeviceInfo[] infos = midiManager.getDevices();
        for(MidiDeviceInfo info : infos) {
            Log.d(TAG, "Got device: "+ info.getProperties());
            String name = info.getProperties().getString(MidiDeviceInfo.PROPERTY_PRODUCT);
            devices.put(name, info);
        }
        midiManager.registerDeviceCallback(this, handler);
    }

    public void addListener(MidiConnectionListener listener) {
        listeners.add(listener);
    }

    public void removeListener(MidiConnectionListener listener) {
        listeners.remove(listener);
    }

    public List<MidiDeviceInfo> getAvailableMidiDevices() {
        return new ArrayList<MidiDeviceInfo> (devices.values());
    }

    public List<String> getAvailableDevices() {
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
    private void disconnect() {
        if(remoteInputPort != null) {
            sendEvent(new AllNotesOff());
            sendEvent(new ResetAllControllers());
            try {
                this.remoteInputPort.close();
                this.remoteInputPort = null;
            } catch (IOException e) {
               Log.e(TAG, "Failed closing input port: " + e);
            }
            if(midiDevice != null) try {
                midiDevice.close();
                midiDevice = null;
            } catch (IOException e) {
                Log.e(TAG, "Failed closing remote device: " + e);
            }
        }
    }

    public void connectToDevice(String productId, int channel) throws MidiConnectionException {
        disconnect();

        MidiDeviceInfo info = devices.get(productId);
        if(info == null) {
            throw new MidiConnectionException(productId + " is not among available devices!");
        }
        if(channel < 1 || channel > 16) {
            throw new MidiConnectionException("Midi channel " + channel + " illegal");
        }
        midiManager.openDevice(info, this, handler);
        this.midiChannel = channel;
    }


    public void sendEvent(MidiEvent evt) {
        byte[] encoded = evt.encode((byte) 1); // Confused ports vs channels remoteInputPortNo);
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
        int portno = 0;
        for(MidiDeviceInfo.PortInfo port : device.getInfo().getPorts()) {
            if(port.getType() == MidiDeviceInfo.PortInfo.TYPE_INPUT) {
                portno = port.getPortNumber();
            }
        }
        remoteInputPort =  midiDevice.openInputPort(portno);
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