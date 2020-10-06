package org.neshan.sample.starter.activity;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ToggleButton;

import com.carto.core.ScreenBounds;
import com.carto.core.ScreenPos;
import com.carto.styles.AnimationStyle;
import com.carto.styles.AnimationStyleBuilder;
import com.carto.styles.AnimationType;
import com.carto.styles.MarkerStyle;
import com.carto.styles.MarkerStyleBuilder;
import com.carto.utils.BitmapUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.neshan.common.model.LatLng;
import org.neshan.common.model.LatLngBounds;
import org.neshan.mapsdk.MapView;
import org.neshan.mapsdk.model.Marker;
import org.neshan.sample.starter.task.DownloadTask;
import org.neshan.sample.starter.R;

import java.util.ArrayList;

public class OnlineLayer extends AppCompatActivity implements DownloadTask.Callback {

    // map UI element
    MapView map;
    ArrayList<Marker> markers=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // starting app in full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_online_layer);
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

        if (checkInternet()) {
            DownloadTask downloadTask = new DownloadTask(this);
            downloadTask.execute("https://api.neshan.org/points.geojson");
        }
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

    // Check for Internet connectivity.
    private Boolean checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null;
    }

    // This method gets a LatLng as input and adds a marker on that position
    private Marker addMarker(LatLng loc) {
        // Creating animation for marker. We should use an object of type AnimationStyleBuilder, set
        // all animation features on it and then call buildStyle() method that returns an object of type
        // AnimationStyle
        AnimationStyleBuilder animStBl = new AnimationStyleBuilder();
        animStBl.setFadeAnimationType(AnimationType.ANIMATION_TYPE_SMOOTHSTEP);
        animStBl.setSizeAnimationType(AnimationType.ANIMATION_TYPE_SPRING);
        animStBl.setPhaseInDuration(0.5f);
        animStBl.setPhaseOutDuration(0.5f);
        AnimationStyle animSt = animStBl.buildStyle();

        // Creating marker style. We should use an object of type MarkerStyleCreator, set all features on it
        // and then call buildStyle method on it. This method returns an object of type MarkerStyle
        MarkerStyleBuilder markStCr = new MarkerStyleBuilder();
        markStCr.setSize(30f);
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker)));
        // AnimationStyle object - that was created before - is used here
        markStCr.setAnimationStyle(animSt);
        MarkerStyle markSt = markStCr.buildStyle();

        // Creating marker
        Marker marker = new Marker(loc, markSt);

        // Adding marker to markerLayer, or showing marker on map!
        map.addMarker(marker);
        return marker;
    }

    @Override
    public void onJsonDownloaded(JSONObject jsonObject) {
        try {
            JSONArray features = jsonObject.getJSONArray("features");
            // variable for creating bound
            // min = south-west
            // max = north-east
            double minLat = Double.MAX_VALUE;
            double minLng = Double.MAX_VALUE;
            double maxLat = Double.MIN_VALUE;
            double maxLng = Double.MIN_VALUE;
            for (int i = 0; i < features.length(); i++) {
                JSONObject geometry = features.getJSONObject(i).getJSONObject("geometry");
                JSONArray coordinates = geometry.getJSONArray("coordinates");
                LatLng LatLng = new LatLng(coordinates.getDouble(1),coordinates.getDouble(0));

                // validating min and max
                minLat = Math.min(LatLng.getLatitude(), minLat);
                minLng = Math.min(LatLng.getLongitude(), minLng);
                maxLat = Math.max(LatLng.getLatitude(), maxLat);
                maxLng = Math.max(LatLng.getLongitude(), maxLng);

                markers.add(addMarker(LatLng));
            }
            map.moveToCameraBounds(
                    new LatLngBounds(new LatLng(minLat,minLng ), new LatLng(maxLat,maxLng )),
                    new ScreenBounds(
                            new ScreenPos(0,0),
                            new ScreenPos(map.getWidth(),map.getHeight())
                    ),
                    true, 0.25f);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void toggleOnlineLayer(View view) {
        ToggleButton toggleButton = (ToggleButton) view;
        if (toggleButton.isChecked()) {
            if (checkInternet()) {
                DownloadTask downloadTask = new DownloadTask(this);
                downloadTask.execute("https://api.neshan.org/points.geojson");
            }
        } else {
            for (Marker marker:markers) {
                map.removeMarker(marker);
            }
        }
    }
}
