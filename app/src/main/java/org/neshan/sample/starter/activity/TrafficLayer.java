package org.neshan.sample.starter.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ToggleButton;

import org.neshan.common.model.LatLng;
import org.neshan.mapsdk.MapView;
import org.neshan.sample.starter.R;

public class TrafficLayer extends AppCompatActivity {

    // map UI element
    MapView map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // starting app in full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_traffic_layer);
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
    }

    // We use findViewByID for every element in our layout file here
    private void initViews(){
        map = findViewById(R.id.map);
    }


    // Initializing map
    private void initMap(){
        // Setting map focal position to a fixed position and setting camera zoom
        map.moveCamera(new LatLng(35.767234, 51.330743),0 );
        map.setZoom(14,0);
    }

    public void toggleTrafficLayer(View view) {
        ToggleButton toggleButton = (ToggleButton) view;
        if (toggleButton.isChecked())
            map.setTrafficEnabled(true);
        else
            map.setTrafficEnabled(false);
    }
}
