package org.neshan.sample.starter.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.carto.graphics.Color;
import com.carto.styles.LineStyle;
import com.carto.styles.LineStyleBuilder;
import com.carto.styles.PolygonStyle;
import com.carto.styles.PolygonStyleBuilder;

import org.neshan.common.model.LatLng;
import org.neshan.mapsdk.MapView;
import org.neshan.mapsdk.model.Polygon;
import org.neshan.sample.starter.R;

import java.util.ArrayList;

public class DrawPolygon extends AppCompatActivity {

    // map UI element
    MapView map;
    private Polygon polygon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // starting app in full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_draw_polygon);
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

    // Drawing polygon on map
    public void drawPolygon(View view) {
        //remove polyline from map if exist
        if (polygon != null) {
            map.removePolygon(polygon);
        }
        // Adding some LatLng points to a latLngs
        ArrayList<LatLng> latLngs = new ArrayList<>();
        latLngs.add(new LatLng(35.762294, 51.325525));
        latLngs.add(new LatLng(35.756548, 51.323768));
        latLngs.add(new LatLng(35.755394, 51.328617));
        latLngs.add(new LatLng(35.760905, 51.330666));
        // Creating a polygon from list of latlngs. here we use getPolygonStyle() method to define polygon styles
        polygon = new Polygon(latLngs, getPolygonStyle());
        // adding the created polygon on map
        map.addPolygon(polygon);
        // focusing camera on first point of drawn polygon
        map.moveCamera(new LatLng(35.762294, 51.325525), 0.25f);
        map.setZoom(14, 0);
    }

    // In this method we create a PolygonStyleCreator and set its features.
    // One feature is its lineStyle, getLineStyle() method is used to get polygon's line style
    // By calling buildStyle() method on polygonStrCr, an object of type PolygonStyle is returned
    private PolygonStyle getPolygonStyle() {
        PolygonStyleBuilder polygonStCr = new PolygonStyleBuilder();
        polygonStCr.setLineStyle(getLineStyle());
        return polygonStCr.buildStyle();
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
