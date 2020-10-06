package org.neshan.sample.starter.activity;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ToggleButton;

import com.carto.core.MapRange;
import com.carto.core.ScreenBounds;
import com.carto.core.ScreenPos;
import com.carto.styles.AnimationStyle;
import com.carto.styles.AnimationStyleBuilder;
import com.carto.styles.AnimationType;
import com.carto.styles.MarkerStyle;
import com.carto.styles.MarkerStyleBuilder;
import com.carto.utils.BitmapUtils;

import org.neshan.common.model.LatLng;
import org.neshan.common.model.LatLngBounds;
import org.neshan.mapsdk.MapView;
import org.neshan.mapsdk.model.Marker;
import org.neshan.sample.starter.R;
import org.neshan.sample.starter.database_helper.AssetDatabaseHelper;

import java.io.IOException;
import java.util.ArrayList;

public class DatabaseLayer extends AppCompatActivity {

    // map UI element
    MapView map;

    // our database points
    SQLiteDatabase pointsDB;
    ArrayList<Marker> markers = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // starting app in full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_database_layer);
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
        // do after 1 secend delay
        new Handler().postDelayed(() -> {
            // copy database.sqlite file from asset folder to /data/data/... and read points and add marker on map
            getDBPoints();
        }, 1000);
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


    // copy database.sqlite file from asset folder to /data/data/... and read points and add marker on map
    private void getDBPoints() {
        // we create an AssetDatabaseHelper object, create a new database in mobile storage
        // and copy database.sqlite file into the new created database
        // Then we open the database and return the SQLiteDatabase object
        AssetDatabaseHelper myDbHelper = new AssetDatabaseHelper(this);

        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            pointsDB = myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }


        // creating a cursor and query all rows of points table
        Cursor cursor = pointsDB.rawQuery("select * from points", null);

        //reading all points and adding a marker for each one
        if (cursor.moveToFirst()) {
            // variable for creating bound
            // min = south-west
            // max = north-east
            double minLat = Double.MAX_VALUE;
            double minLng = Double.MAX_VALUE;
            double maxLat = Double.MIN_VALUE;
            double maxLng = Double.MIN_VALUE;
            while (!cursor.isAfterLast()) {
                double lng = cursor.getDouble(cursor.getColumnIndex("lng"));
                double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
                Log.i("POINTS", "getDBPoints: " + lat + " " + lng);
                LatLng LatLng = new LatLng(lat, lng);

                // validating min and max
                minLat = Math.min(LatLng.getLatitude(), minLat);
                minLng = Math.min(LatLng.getLongitude(), minLng);
                maxLat = Math.max(LatLng.getLatitude(), maxLat);
                maxLng = Math.max(LatLng.getLongitude(), maxLng);

                markers.add(addMarker(LatLng));

                cursor.moveToNext();
            }

            map.moveToCameraBounds(
                    new LatLngBounds(new LatLng(minLat, minLng), new LatLng(maxLat, maxLng)),
                    new ScreenBounds(
                            new ScreenPos(0, 0),
                            new ScreenPos(map.getWidth(), map.getHeight())
                    ),
                    true, 0.25f);

            Log.i("BOUND", "getDBPoints: " + minLat + " " + minLng + "----" + maxLat + " " + maxLng);
        }
        cursor.close();
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

        // Adding marker to map!
        map.addMarker(marker);
        return marker;
    }

    public void toggleDatabaseLayer(View view) {
        ToggleButton toggleButton = (ToggleButton) view;
        if (toggleButton.isChecked())
            getDBPoints();
        else
            for (Marker marker : markers) {
                map.removeMarker(marker);
            }
    }
}
