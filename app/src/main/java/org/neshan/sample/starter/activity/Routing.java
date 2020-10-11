package org.neshan.sample.starter.activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.carto.graphics.Color;
import com.carto.styles.AnimationStyle;
import com.carto.styles.AnimationStyleBuilder;
import com.carto.styles.AnimationType;
import com.carto.styles.LineStyle;
import com.carto.styles.LineStyleBuilder;
import com.carto.styles.MarkerStyle;
import com.carto.styles.MarkerStyleBuilder;
import com.carto.utils.BitmapUtils;

import org.neshan.common.model.LatLng;
import org.neshan.mapsdk.MapView;
import org.neshan.mapsdk.model.Marker;
import org.neshan.mapsdk.model.Polyline;
import org.neshan.sample.starter.R;
import org.neshan.common.utils.PolylineEncoding;
import org.neshan.servicessdk.direction.NeshanDirection;
import org.neshan.servicessdk.direction.model.DirectionStep;
import org.neshan.servicessdk.direction.model.NeshanDirectionResult;
import org.neshan.servicessdk.direction.model.Route;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Routing extends AppCompatActivity {
    // map UI element
    MapView map;

    // define two toggle button and connecting together for two type of routing
    ToggleButton overviewToggleButton;
    ToggleButton stepByStepToggleButton;

    // we save decoded Response of routing encoded string because we don't want request every time we clicked toggle buttons
    ArrayList<LatLng> routeOverviewPolylinePoints;
    ArrayList<LatLng> decodedStepByStepPath;

    // value for difference mapSetZoom
    boolean overview = false;

    // Marker that will be added on map
    Marker marker;
    // List of created markers
    ArrayList<Marker> markers = new ArrayList();
    // marker animation style
    AnimationStyle animSt;
    // drawn path of route
    private Polyline onMapPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // starting app in full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_routing);
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
        map.setOnMapLongClickListener(latLng -> {
            if (markers.size() < 2) {
                markers.add(addMarker(latLng));
                if (markers.size() == 2) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            overviewToggleButton.setChecked(true);
                            neshanRoutingApi();
                        }
                    });
                }
            } else {
                runOnUiThread(() -> Toast.makeText(Routing.this, "مسیریابی بین دو نقطه انجام میشود!", Toast.LENGTH_SHORT).show());
            }
        });
    }

    // We use findViewByID for every element in our layout file here
    private void initViews() {
        map = findViewById(R.id.map);


        // CheckChangeListener for Toggle buttons
        CompoundButton.OnCheckedChangeListener changeChecker = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
                // if any toggle button checked:
                if (isChecked) {
                    // if overview toggle button checked other toggle button is uncheck
                    if (toggleButton == overviewToggleButton) {
                        stepByStepToggleButton.setChecked(false);
                        overview = true;
                    }
                    if (toggleButton == stepByStepToggleButton) {
                        overviewToggleButton.setChecked(false);
                        overview = false;
                    }
                }
                if (!isChecked && onMapPolyline!=null) {
                    map.removePolyline(onMapPolyline);
                }
            }
        };

        // each toggle button has a checkChangeListener for uncheck other toggle button
        overviewToggleButton = findViewById(R.id.overviewToggleButton);
        overviewToggleButton.setOnCheckedChangeListener(changeChecker);

        stepByStepToggleButton = findViewById(R.id.stepByStepToggleButton);
        stepByStepToggleButton.setOnCheckedChangeListener(changeChecker);
    }

    // Initializing map
    private void initMap() {
        // Setting map focal position to a fixed position and setting camera zoom
        map.moveCamera(new LatLng(35.767234, 51.330743), 0);
        map.setZoom(14, 0);
    }

    private Marker addMarker(LatLng loc) {
        // Creating animation for marker. We should use an object of type AnimationStyleBuilder, set
        // all animation features on it and then call buildStyle() method that returns an object of type
        // AnimationStyle
        AnimationStyleBuilder animStBl = new AnimationStyleBuilder();
        animStBl.setFadeAnimationType(AnimationType.ANIMATION_TYPE_SMOOTHSTEP);
        animStBl.setSizeAnimationType(AnimationType.ANIMATION_TYPE_SPRING);
        animStBl.setPhaseInDuration(0.5f);
        animStBl.setPhaseOutDuration(0.5f);
        animSt = animStBl.buildStyle();

        // Creating marker style. We should use an object of type MarkerStyleBuilder, set all features on it
        // and then call buildStyle method on it. This method returns an object of type MarkerStyle
        MarkerStyleBuilder markStCr = new MarkerStyleBuilder();
        markStCr.setSize(30f);
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker)));
        // AnimationStyle object - that was created before - is used here
        markStCr.setAnimationStyle(animSt);
        MarkerStyle markSt = markStCr.buildStyle();

        // Creating marker
        marker = new Marker(loc, markSt);

        // Adding marker to markerLayer, or showing marker on map!
        map.addMarker(marker);
        return marker;
    }

    // request routing method from Neshan Server
    private void neshanRoutingApi() {
        new NeshanDirection.Builder("service.VNlPhrWb3wYRzEYmstQh3GrAXyhyaN55AqUSRR3V", markers.get(0).getLatLng(), markers.get(1).getLatLng())
                .build().call(new Callback<NeshanDirectionResult>() {
            @Override
            public void onResponse(Call<NeshanDirectionResult> call, Response<NeshanDirectionResult> response) {

                // two type of routing
                Route route = response.body().getRoutes().get(0);
                routeOverviewPolylinePoints = new ArrayList<>(PolylineEncoding.decode(route.getOverviewPolyline().getEncodedPolyline()));
                decodedStepByStepPath = new ArrayList<>();

                // decoding each segment of steps and putting to an array
                for (DirectionStep step : route.getLegs().get(0).getDirectionSteps()) {
                    decodedStepByStepPath.addAll(PolylineEncoding.decode(step.getEncodedPolyline()));
                }

                onMapPolyline = new Polyline(routeOverviewPolylinePoints, getLineStyle());
                //draw polyline between route points
                map.addPolyline(onMapPolyline);
                // focusing camera on first point of drawn line
                mapSetPosition(overview);
            }

            @Override
            public void onFailure(Call<NeshanDirectionResult> call, Throwable t) {

            }
        });
    }

    // for overview routing we zoom out and review hole route and for stepByStep routing we just zoom to first marker position
    private void mapSetPosition(boolean overview) {
        double centerFirstMarkerX = markers.get(0).getLatLng().getLatitude();
        double centerFirstMarkerY = markers.get(0).getLatLng().getLongitude();
        if (overview) {
            double centerFocalPositionX = (centerFirstMarkerX + markers.get(1).getLatLng().getLatitude()) / 2;
            double centerFocalPositionY = (centerFirstMarkerY + markers.get(1).getLatLng().getLongitude()) / 2;
            map.moveCamera(new LatLng(centerFocalPositionX, centerFocalPositionY), 0.5f);
            map.setZoom(14, 0.5f);
        } else {
            map.moveCamera(new LatLng(centerFirstMarkerX, centerFirstMarkerY), 0.5f);
            map.setZoom(18, 0.5f);
        }

    }

    // In this method we create a LineStyleCreator, set its features and call buildStyle() method
    // on it and return the LineStyle object (the same routine as crating a marker style)
    private LineStyle getLineStyle() {
        LineStyleBuilder lineStCr = new LineStyleBuilder();
        lineStCr.setColor(new Color((short) 2, (short) 119, (short) 189, (short) 190));
        lineStCr.setWidth(10f);
        lineStCr.setStretchFactor(0f);
        return lineStCr.buildStyle();
    }

    // call this function with clicking on toggle buttons and draw routing line depend on type of routing requested
    public void findRoute(View view) {
        if (markers.size() < 2) {
            Toast.makeText(this, "برای مسیریابی باید دو نقطه انتخاب شود", Toast.LENGTH_SHORT).show();
            overviewToggleButton.setChecked(false);
            stepByStepToggleButton.setChecked(false);
        } else if (overviewToggleButton.isChecked()) {
            map.removePolyline(onMapPolyline);
            onMapPolyline = new Polyline(routeOverviewPolylinePoints, getLineStyle());
            //draw polyline between route points
            map.addPolyline(onMapPolyline);
        } else if (stepByStepToggleButton.isChecked()) {
            map.removePolyline(onMapPolyline);
            onMapPolyline = new Polyline(decodedStepByStepPath, getLineStyle());
            //draw polyline between route points
            map.addPolyline(onMapPolyline);

        }
    }
}
