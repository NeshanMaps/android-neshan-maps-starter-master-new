package org.neshan.sample.starter.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import org.neshan.common.model.LatLng;
import org.neshan.mapsdk.MapView;
import org.neshan.mapsdk.style.NeshanMapStyle;
import org.neshan.sample.starter.R;

public class ChangeStyle extends AppCompatActivity {

    // map UI element
    private MapView map;
    // save current map style
    @NeshanMapStyle
    private int mapStyle;
    // map style control
    private ImageView themePreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // starting app in full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_change_style);
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
        // Initializing theme preview
        validateThemePreview();
    }

    private void validateThemePreview() {
        switch (mapStyle) {
            case NeshanMapStyle.NESHAN:
                themePreview.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.map_style_standard_day, getTheme()));
                break;
            case NeshanMapStyle.STANDARD_DAY:
                themePreview.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.map_style_standard_night, getTheme()));
                break;
            case NeshanMapStyle.NESHAN_NIGHT:
                themePreview.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.map_style_1, getTheme()));
                break;
            case NeshanMapStyle.STYLE_1:
                themePreview.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.map_style_2, getTheme()));
                break;
            case NeshanMapStyle.STYLE_2:
                themePreview.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.map_style_3, getTheme()));
                break;
            case NeshanMapStyle.STYLE_3:
                themePreview.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.map_style_4, getTheme()));
                break;
            case NeshanMapStyle.STYLE_4:
                themePreview.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.map_style_5, getTheme()));
                break;
            case NeshanMapStyle.STYLE_5:
                themePreview.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.map_style_6, getTheme()));
                break;
            case NeshanMapStyle.STYLE_6:
                themePreview.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.map_style_neshan, getTheme()));
                break;
        }
    }

    // We use findViewByID for every element in our layout file here
    private void initViews() {
        map = findViewById(R.id.map);
        themePreview = findViewById(R.id.theme_preview);
    }

    // Initializing map
    private void initMap() {
        mapStyle = NeshanMapStyle.NESHAN;
        // Setting map focal position to a fixed position and setting camera zoom
        map.moveCamera(new LatLng(35.767234, 51.330743), 0);
        map.setZoom(14, 0);
        map.setTrafficEnabled(true);
    }

    public void changeStyle(View view) {
        switch (mapStyle) {
            case NeshanMapStyle.NESHAN:
                mapStyle = NeshanMapStyle.STANDARD_DAY;
                break;
            case NeshanMapStyle.STANDARD_DAY:
                mapStyle = NeshanMapStyle.NESHAN_NIGHT;
                break;
            case NeshanMapStyle.NESHAN_NIGHT:
                mapStyle = NeshanMapStyle.STYLE_1;
                break;
            case NeshanMapStyle.STYLE_1:
                mapStyle = NeshanMapStyle.STYLE_2;
                break;
            case NeshanMapStyle.STYLE_2:
                mapStyle = NeshanMapStyle.STYLE_3;
                break;
            case NeshanMapStyle.STYLE_3:
                mapStyle = NeshanMapStyle.STYLE_4;
                break;
            case NeshanMapStyle.STYLE_4:
                mapStyle = NeshanMapStyle.STYLE_5;
                break;
            case NeshanMapStyle.STYLE_5:
                mapStyle = NeshanMapStyle.STYLE_6;
                break;
            case NeshanMapStyle.STYLE_6:
                mapStyle = NeshanMapStyle.NESHAN;
                break;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                validateThemePreview();
            }
        });
        map.setMapStyle(mapStyle);
    }
}
