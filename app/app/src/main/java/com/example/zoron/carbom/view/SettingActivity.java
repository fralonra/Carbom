package com.example.zoron.carbom.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.zoron.carbom.R;

/**
 * Created by zoron on 17-3-23.
 */

public class SettingActivity extends AppCompatActivity {
    public static final String spFileName = "pref";
    public static final String spIndexSerialPort = "SerialPort";
    public static final String defaultSerialPort = "/dev/ttyS0";

    private SharedPreferences sharedPreferences;
    private String serialPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.e("ActionBar", e.toString());
        }

        sharedPreferences = getSharedPreferences(spFileName, MODE_PRIVATE);
        serialPort = sharedPreferences.getString(spIndexSerialPort, defaultSerialPort);

        setContentView(R.layout.activity_setting);

        ArrayAdapter<CharSequence> baudrateAdapter = ArrayAdapter.createFromResource(this,
                R.array.baudrates_value,android.R.layout.simple_spinner_item);
        baudrateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner serialPortList = (Spinner) findViewById(R.id.serialport_list);
        serialPortList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                serialPort = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(spIndexSerialPort, serialPort);
                editor.apply();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
