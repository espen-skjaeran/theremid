package no.skunkworks.theremidi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

public class MidiSettingsActivity extends Activity implements MidiMux.MidiConnectionListener {
    private static String TAG = "MidiSettingsActivity";

    private MidiMux midiMux;
    private static final String[] MIDI_CHANNELS = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                                                "11", "12", "13", "14", "15", "16"};
    private Spinner destinationSpinner;
    private Spinner midiPortSpinner;
    private ArrayAdapter<CharSequence> deviceListAdapter, midiPortAdapter;

    public MidiSettingsActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.midiMux = MidiMux.getInstance(this.getApplicationContext());
        this.midiMux.addListener(this);
        setContentView(R.layout.activity_midi_settings);

        List<String> deviceList = midiMux.getAvailableDevices();
        destinationSpinner = findViewById(R.id.destinationsSpinner);
        deviceListAdapter = new ArrayAdapter(getApplicationContext(),
                android.R.layout.simple_list_item_1,
                deviceList);

        deviceListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        destinationSpinner.setAdapter(deviceListAdapter);
        String connectedDevice = midiMux.getConnectedDevice();
        if(connectedDevice != null) {
            destinationSpinner.setSelection(deviceList.indexOf(connectedDevice));
        }

        midiPortSpinner = findViewById(R.id.midiPortSpinner);
        midiPortAdapter = new ArrayAdapter(getApplicationContext(),
                android.R.layout.simple_list_item_1,
                MIDI_CHANNELS);
        midiPortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        midiPortSpinner.setAdapter(midiPortAdapter);
        if(midiMux.getConnectedPort() > 0) {
            midiPortSpinner.setSelection(midiMux.getConnectedPort() - 1);
        }

        findViewById(R.id.midiSettingsOkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                okPressed();
            }
        });
        findViewById(R.id.midiSettingsCancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelPressed();
            }
        });
    }

    private void cancelPressed() {
        finish();
    }

    private void okPressed() {
        try {
            midiMux.connectToDevice((String) destinationSpinner.getSelectedItem(),
                    Integer.valueOf((String) midiPortSpinner.getSelectedItem()));
            finish();
        }
        catch(MidiMux.MidiConnectionException uh) {
            Log.w(TAG, "Failed to connect to " + destinationSpinner.getSelectedItem() + ": " + uh);
        }
    }

    @Override
    public void connected(String name, int port) {
        List<String> deviceList = midiMux.getAvailableDevices();
        deviceListAdapter.add(name);
        destinationSpinner.setSelection(deviceList.indexOf(name));

        midiPortSpinner.setSelection(port -1);
    }

    @Override
    public void disconnected(String name) {
        if(destinationSpinner.getSelectedItem().equals(name)) {
            destinationSpinner.setSelection(-1);
            midiPortSpinner.setSelection(-1);
        }
    }

}
