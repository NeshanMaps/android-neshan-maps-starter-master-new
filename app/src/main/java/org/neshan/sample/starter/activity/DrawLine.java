package org.neshan.sample.starter.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.carto.graphics.Color;
import com.carto.styles.LineStyle;
import com.carto.styles.LineStyleBuilder;

import org.neshan.common.model.LatLng;
import org.neshan.mapsdk.MapView;
import org.neshan.mapsdk.model.Polyline;
import org.neshan.sample.starter.R;

import java.util.ArrayList;

public class DrawLine extends AppCompatActivity {

    // map UI element
    private MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // starting app in full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_draw_line);
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
    private void initViews() {
        map = findViewById(R.id.map);
    }

    // Initializing map
    private void initMap() {
        // Setting map focal position to a fixed position and setting camera zoom
        map.moveCamera(new LatLng(35.767234, 51.330743), 0);
        map.setZoom(14, 0);
    }

    // Drawing line on map
    public Polyline drawLine(View view) {
        // Adding some LatLng points to a latLngs
        ArrayList<LatLng> latLngs = new ArrayList<>();
        latLngs.add(new LatLng(35.769368, 51.327650));
        latLngs.add(new LatLng(35.756670, 51.323889));
        latLngs.add(new LatLng(35.746670, 51.383889));
        // Creating a line from LineGeom. here we use getLineStyle() method to define line styles
        Polyline polyline = new Polyline(latLngs, getLineStyle());
        // adding the created line to lineLayer, showing it on map
        map.addPolyline(polyline);
        // focusing camera on first point of drawn line
        map.moveCamera(new LatLng(35.769368, 51.327650), 0.25f);
        map.setZoom(14, 0);
        return polyline;
    }

    // In this method we create a LineStyleCreator, set its features and call buildStyle() method
    // on it and return the LineStyle object (the same routine as crating a marker style)
    private LineStyle getLineStyle() {
        LineStyleBuilder lineStCr = new LineStyleBuilder();
        lineStCr.setColor(new Color((short) 2, (short) 119, (short) 189, (short) 190));
        lineStCr.setWidth(12f);
        lineStCr.setStretchFactor(0f);
        return lineStCr.buildStyle();
    }
}
