package org.neshan.sample.starter;

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
import org.neshan.mapsdk.model.Polyline;

public class DrawArc extends AppCompatActivity {

    // map UI element
    private MapView map;
    private Circle circle;
    private Polyline arc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // starting app in full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_draw_arc);
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
        map.moveCamera(new LatLng(35.701029742703604, 51.33399009376021), 0);
        map.setZoom(12, 0);
    }

    public void drawArc(View view) {
        if (arc == null) {
            arc = map.drawArc(new LatLng(35.701029742703604, 51.33399009376021), new LatLng(35.72488915365864, 51.38092935533464), getLineStyle());
        } else {
            map.removePolyline(arc);
            arc = null;
        }
    }

    private LineStyle getLineStyle() {
        LineStyleBuilder lineStyleBuilder = new LineStyleBuilder();
        lineStyleBuilder.setColor(new Color((short) 2, (short) 50, (short) 189, (short) 190));
        lineStyleBuilder.setWidth(5f);
        return lineStyleBuilder.buildStyle();
    }
}