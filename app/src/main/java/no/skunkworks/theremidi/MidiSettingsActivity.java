package no.skunkworks.theremidi;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MidiSettingsActivity extends Activity {

    private MidiMux midiMux;
    private static final String[] MIDI_PORTS = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                                                "11", "12", "13", "14", "15", "16"};
    private Spinner destinationSpinner;
    private Spinner midiPortSpinner;


    public MidiSettingsActivity() {
        this.midiMux = MidiMux.INSTANCE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_midi_settings);

        destinationSpinner = findViewById(R.id.destinationsSpinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getApplicationContext(),
                android.R.layout.simple_list_item_1,
                midiMux.getConnectedDevices());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        destinationSpinner.setAdapter(adapter);

        midiPortSpinner = findViewById(R.id.midiPortSpinner);
        ArrayAdapter<CharSequence> midiPortAdapter = new ArrayAdapter(getApplicationContext(),
                android.R.layout.simple_list_item_1,
                MIDI_PORTS);
        midiPortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        midiPortSpinner.setAdapter(midiPortAdapter);

        findViewById(R.id.midiSettingsOkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                okPressed();
            }
        });
    }
    private void okPressed() {
        //Byebye
        finish();
    }
}
