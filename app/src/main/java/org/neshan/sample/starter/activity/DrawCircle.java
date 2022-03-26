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
import org.neshan.mapsdk.model.Circle;
import org.neshan.sample.starter.R;

public class DrawCircle extends AppCompatActivity {

    // map UI element
    private MapView map;
    private Circle circle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // starting app in full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_draw_circle);
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

    private void initMap() {
        // Setting map focal position to a fixed position and setting camera zoom
        map.moveCamera(new LatLng(35.767234, 51.330743), 0);
        map.setZoom(14, 0);
    }

    public void drawCircle(View view) {
        //remove circle from map if exist
        if (circle != null) {
            map.removeCircle(circle);
        }

        circle = new Circle(new LatLng(35.762294, 51.325525), 100, new Color((short) 2, (short) 119, (short) 189, (short) 190), getLineStyle());


        map.addCircle(circle);

        // focusing camera on first point of drawn polygon
        map.moveCamera(new LatLng(35.762294, 51.325525), 0.25f);
        map.setZoom(14, 0);
    }

    private LineStyle getLineStyle() {
        LineStyleBuilder lineStyleBuilder = new LineStyleBuilder();
        lineStyleBuilder.setColor(new Color((short) 2, (short) 50, (short) 189, (short) 190));
        lineStyleBuilder.setWidth(5f);
        return lineStyleBuilder.buildStyle();
    }
}