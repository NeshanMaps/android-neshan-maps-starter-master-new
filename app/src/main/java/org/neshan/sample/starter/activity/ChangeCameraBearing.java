package org.neshan.sample.starter.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import org.neshan.common.model.LatLng;
import org.neshan.mapsdk.MapView;
import org.neshan.sample.starter.R;
import org.neshan.sample.starter.custom_view.CircularSeekBar;

public class ChangeCameraBearing extends AppCompatActivity {

    // map UI element
    private MapView map;
    // camera bearing control
    private CircularSeekBar bearingSeekBar;

    // variable that hold camera bearing
    float cameraBearing;

    boolean isCameraRotationEnable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // starting app in full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_change_camera_bearing);
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
        // connect bearing seek bar to camera
        bearingSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                // change camera bearing programmatically
                map.setBearing(progress, 0f);
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });

        map.setOnCameraMoveListener(() -> {
            // updating seek bar with new camera bearing value
            if (map.getBearing() < 0) {
                cameraBearing = (180 + map.getBearing()) + 180;
            } else {
                cameraBearing = map.getBearing();
            }
            // updating own ui element must run on ui thread not in map ui thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bearingSeekBar.setProgress(cameraBearing);
                }
            });
        });
    }

    // We use findViewByID for every element in our layout file here
    private void initViews() {
        map = findViewById(R.id.map);
        bearingSeekBar = findViewById(R.id.bearing_seek_bar);
    }

    // Initializing map
    private void initMap() {
        // Setting map focal position to a fixed position and setting camera zoom
        map.moveCamera(new LatLng(35.767234, 51.330743), 0);
        map.setZoom(14, 0);
    }

    public void toggleCameraRotation(View view) {
        ToggleButton toggleButton = (ToggleButton) view;
        isCameraRotationEnable = !isCameraRotationEnable;
        if (toggleButton.isChecked())
            map.getSettings().setMapRotationEnabled(true);
        else
            map.getSettings().setMapRotationEnabled(false);
    }
}
