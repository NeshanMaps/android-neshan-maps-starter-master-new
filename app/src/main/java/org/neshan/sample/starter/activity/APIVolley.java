package org.neshan.sample.starter.activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.carto.styles.AnimationStyle;
import com.carto.styles.AnimationStyleBuilder;
import com.carto.styles.AnimationType;
import com.carto.styles.MarkerStyle;
import com.carto.styles.MarkerStyleBuilder;
import com.carto.utils.BitmapUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONObject;
import org.neshan.common.model.LatLng;
import org.neshan.mapsdk.MapView;
import org.neshan.mapsdk.model.Marker;
import org.neshan.sample.starter.R;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class APIVolley extends AppCompatActivity {

    // map UI element
    private MapView map;

    // a bottomsheet to show address on
    private View reverseBottomSheetView;
    private BottomSheetBehavior reverseBottomSheetBehavior;

    //ui elements in bottom sheet
    private TextView addressTitle;
    private TextView addressDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // starting app in full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_api_volley);
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
            // addMarker adds a marker (pretty self explanatory :D) to the clicked location
            addMarker(latLng);

            //calling NeshanReverseAPI to get address of a location and showing it on a bottom sheet
            neshanReverseAPI(latLng);

        });
    }

    // We use findViewByID for every element in our layout file here
    private void initViews() {
        map = findViewById(R.id.map);

        // UI elements in bottom sheet
        addressTitle = findViewById(R.id.title);
        addressDetails = findViewById(R.id.details);

        reverseBottomSheetView = findViewById(R.id.reverse_bottom_sheet_include);
        reverseBottomSheetBehavior = BottomSheetBehavior.from(reverseBottomSheetView);
        // bottomsheet is collapsed at first
        reverseBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    // Initializing map
    private void initMap() {
        // Setting map focal position to a fixed position and setting camera zoom
        map.moveCamera(new LatLng(35.767234, 51.330743), 0);
        map.setZoom(14, 0);
    }

    // This method gets a LatLng as input and adds a marker on that position
    private void addMarker(LatLng loc) {

        // Creating animation for marker. We should use an object of type AnimationStyleBuilder, set
        // all animation features on it and then call buildStyle() method that returns an object of type
        // AnimationStyle
        AnimationStyleBuilder animStBl = new AnimationStyleBuilder();
        animStBl.setFadeAnimationType(AnimationType.ANIMATION_TYPE_SMOOTHSTEP);
        animStBl.setSizeAnimationType(AnimationType.ANIMATION_TYPE_SPRING);
        animStBl.setPhaseInDuration(0.5f);
        animStBl.setPhaseOutDuration(0.5f);
        AnimationStyle animSt = animStBl.buildStyle();

        // Creating marker style. We should use an object of type MarkerStyleBuilder, set all features on it
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
    }

    private void neshanReverseAPI(LatLng loc) {
        String requestURL = "https://api.neshan.org/v1/reverse?lat=" + loc.getLatitude() + "&lng=" + loc.getLongitude();
        final String latLngAddr = String.format("%.6f", loc.getLatitude()) + "," + String.format("%.6f", loc.getLongitude());


        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest reverseGeoSearchRequest = new StringRequest(
                Request.Method.GET,
                requestURL,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        String neighbourhood = new String(obj.getString("neighbourhood").getBytes(StandardCharsets.ISO_8859_1),"UTF-8");
                        String address = new String(obj.getString("address").getBytes(StandardCharsets.ISO_8859_1),"UTF-8");


                        // if server was able to return neighbourhood and address to us
                        if (!neighbourhood.equals("null") && !address.equals("null")) {
                            addressTitle.setText(neighbourhood);
                            addressDetails.setText(address);
                        } else {
                            addressTitle.setText("آدرس نامشخص");
                            addressDetails.setText(latLngAddr);
                        }

                    } catch (Exception e) {

                        addressTitle.setText("آدرس نامشخص");
                        addressDetails.setText(latLngAddr);
                    }
                }, error -> error.printStackTrace()) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                // TODO: replace "YOUR_API_KEY" with your api key
                params.put("Api-Key", "YOUR-API-KEY");
                return params;
            }
        };

        // Add the request to the queue
        requestQueue.add(reverseGeoSearchRequest);
    }
}
