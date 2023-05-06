package org.neshan.sample.starter.activity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.carto.graphics.Color;
import com.carto.styles.TextStyle;
import com.carto.styles.TextStyleBuilder;

import org.neshan.common.model.LatLng;
import org.neshan.mapsdk.MapView;
import org.neshan.mapsdk.model.Label;
import org.neshan.sample.starter.R;


public class AddLabel extends AppCompatActivity {

    // map UI element
    private MapView map;
    private Label label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // starting app in full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_label);
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

        // when long clicked on map, a marker is added in clicked location
        map.setOnMapLongClickListener(latLng -> addLabel(latLng));
    }

    // We use findViewByID for every element in our layout file here
    private void initViews() {
        map = findViewById(R.id.map);
    }

    // Initializing map
    private void initMap() {
        // Setting map focal position to a fixed position and setting camera zoom
        map.moveCamera(new LatLng(35.767234, 51.330743), 0);
        map.setZoom(14, 0);
    }

    // This method gets a LatLng as input and adds a label on that position
    private void addLabel(LatLng loc) {
        if (label != null) {
            map.removeLabel(label);
        }
        // Creating text style. We should use an object of type TextStyleBuilder, set all features on it
        // and then call buildStyle method on it. This method returns an object of type TextStyle.
        TextStyleBuilder textStyleBuilder = new TextStyleBuilder();
        textStyleBuilder.setFontSize(15f);
        textStyleBuilder.setColor(new Color((short) 255, (short) 0, (short) 255, (short) 255));
        textStyleBuilder.setStrokeColor(new Color((short) 0, (short) 0, (short) 0, (short) 255));
        textStyleBuilder.setStrokeWidth(1);
        TextStyle textStyle = textStyleBuilder.buildStyle();

        // Creating label
        label = new Label(loc, textStyle, "مکان انتخاب شده");

        // Adding marker to labelLayer, or showing label on map!
        map.addLabel(label);
    }
}
