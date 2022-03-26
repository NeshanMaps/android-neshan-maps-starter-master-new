package org.neshan.sample.starter.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import org.neshan.common.model.LatLng;
import org.neshan.mapsdk.MapView;
import org.neshan.sample.starter.R;

public class ChangeCameraTilt extends AppCompatActivity {

    // map UI element
    private MapView map;
    // camera tilt control
    private SeekBar tiltSeekBar;

    boolean isCameraTiltEnable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // starting app in full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_change_camera_tilt);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // everything related to ui is initialized here
        initLayoutReferences();
    }

    // Initializing layout references (views, map and map events)
    private void initLayoutReferences() {
        // Initializing views
        initViews();
        // Initializing mapView element
        initMap();
        // connect tilt seek bar to camera
        tiltSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // change camera tilt programmatically

                // because of we can not set min range for seek bar we take seek bar range 0-60
                // then add 30 in each read seek bar to convert it to neshan camera tilt range(30-90)
                // for reverse converting subtract 30 in each setting progress for seek bar
                map.setTilt(progress + 30, 0f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // sync map with tilt controller
        map.setOnCameraMoveListener(() -> {
            // because of we can not set min range for seek bar we take seek bar range 0-60
            // then add 30 in each read seek bar to convert it to neshan camera tilt range(30-90)
            // for reverse converting subtract 30 in each setting progress for seek bar

            // updating own ui element must run on ui thread not in map ui thread
            runOnUiThread(() -> tiltSeekBar.setProgress(Math.round(map.getTilt()) - 30));
        });
    }

    // We use findViewByID for every element in our layout file here
    private void initViews() {
        map = findViewById(R.id.map);
        tiltSeekBar = findViewById(R.id.tilt_seek_bar);
    }

    // Initializing map
    private void initMap() {
        // Setting map focal position to a fixed position and setting camera zoom
        map.moveCamera(new LatLng(35.767234, 51.330743), 0);
        map.setZoom(14, 0);
    }

    public void toggleCameraTilt(View view) {
        ToggleButton toggleButton = (ToggleButton) view;
        isCameraTiltEnable = !isCameraTiltEnable;
        if (toggleButton.isChecked()) {
            //set tilt range from 30 to 90 degrees
            map.getSettings().setMinTiltAngle(30);
            map.getSettings().setMaxTiltAngle(90);
        } else {
            //set tilt range to 1 degree (only current tilt degree)
            map.getSettings().setMinTiltAngle(map.getTilt());
            map.getSettings().setMaxTiltAngle(map.getTilt());
        }

    }
}
