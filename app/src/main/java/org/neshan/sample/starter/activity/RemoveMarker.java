package org.neshan.sample.starter.activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.carto.styles.AnimationStyle;
import com.carto.styles.AnimationStyleBuilder;
import com.carto.styles.AnimationType;
import com.carto.styles.MarkerStyle;
import com.carto.styles.MarkerStyleBuilder;
import com.carto.utils.BitmapUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.neshan.common.model.LatLng;
import org.neshan.mapsdk.MapView;
import org.neshan.mapsdk.model.Marker;
import org.neshan.sample.starter.R;

public class RemoveMarker extends AppCompatActivity {

    // map UI element
    private MapView map;
    // Marker that will be added on map
    private Marker marker;
    // marker animation style
    private AnimationStyle animSt;
    // markerId bottom sheet
    private TextView marker_id;
    // remove marker button
    private Button remove_marker;
    // bottom sheet layout and behavior
    private View remove_marker_bottom_sheet;
    private BottomSheetBehavior bottomSheetBehavior;
    // save selected Marker for select and deselect function
    private Marker selectedMarker = null;
    // Tip Strings
    static String firstTipString = "<b>" + "قدم اول: " + "</b> " + "برای ایجاد پین جدید نگهدارید!";
    static String secondTipString = "<b>" + "قدم دوم: " + "</b> " + "برای حذف روی پین لمس کنید!";
    private int markerIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // starting app in full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_remove_marker);
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
        // map listener: Long Click -> add marker Single Click -> deselect marker
        map.setOnMapLongClickListener(new MapView.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng var1) {
                // check the bottom sheet expanded or collapsed
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {

                    if (selectedMarker == null) {
                        // if bottom sheet is expanded and no marker selected second tip is going up (for just one time)
                        collapseBottomSheet();
                        // delay for collapsing then expanding bottom sheet
                        remove_marker_bottom_sheet.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                expandBottomSheet();
                            }
                        }, 200);
                        remove_marker_bottom_sheet.post(new Runnable() {
                            @Override
                            public void run() {
                                marker_id.setText(Html.fromHtml(secondTipString));
                            }
                        });
                    } else {
                        // if bottom sheet is expanded and any marker selected deselect that marker by long tap
                        deselectMarker(selectedMarker);
                    }
                }
                // addMarker adds a marker (pretty self explanatory :D) to the clicked location
                addMarker(var1, "Marker " + ++markerIndex);
            }
        });

        map.setOnMapClickListener(new MapView.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng var1) {
                if (selectedMarker != null) {
                    // deselect marker when tap on map and a marker is selected
                    deselectMarker(selectedMarker);
                }
            }
        });

        // marker listener for select and deselect markers
        map.setOnMarkerClickListener(new MapView.OnMarkerClickListener() {
            @Override
            public void OnMarkerClicked(Marker marker) {
                if (selectedMarker != null) {
                    // deselect marker when tap on a marker and a marker is selected
                    deselectMarker(selectedMarker);
                } else {
                    // select marker when tap on a marker
                    selectMarker(marker);
                    remove_marker_bottom_sheet.post(new Runnable() {
                        @Override
                        public void run() {
                            marker_id.setText("از حدف پین " + marker.getTitle() + " اطمینان دارید؟");
                            remove_marker.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });

        // remove marker and deselect that marker
        remove_marker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedMarker != null) {
                    map.removeMarker(selectedMarker);
                    deselectMarker(selectedMarker);
                }
            }
        });

    }

    // We use findViewByID for every element in our layout file here
    private void initViews() {
        map = findViewById(R.id.map);
        marker_id = findViewById(R.id.marker_id);
        remove_marker = findViewById(R.id.remove_marker);
        // bottom sheet include tag and behavior
        remove_marker_bottom_sheet = findViewById(R.id.remove_marker_bottom_sheet_include);
        bottomSheetBehavior = BottomSheetBehavior.from(remove_marker_bottom_sheet);
//        bottomSheetBehavior.setHideable(true);
        remove_marker.setVisibility(View.GONE);
        marker_id.setText(Html.fromHtml(firstTipString));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        // bottom sheet callback deselect marker for when bottom sheet collapsed manually
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED && selectedMarker != null) {
                    deselectMarker(selectedMarker);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }

    // Initializing map
    private void initMap() {
        // Setting map focal position to a fixed position and setting camera zoom
        map.moveCamera(new LatLng(35.767234, 51.330743), 0);
        map.setZoom(14, 0);
    }

    // This method gets a LatLng as input and adds a marker on that position
    private void addMarker(LatLng loc, String title) {
        // If you want to have only one marker on map at a time, uncomment next line to delete all markers before adding a new marker
//        markerLayer.clear();

        // Creating animation for marker. We should use an object of type AnimationStyleBuilder, set
        // all animation features on it and then call buildStyle() method that returns an object of type
        // AnimationStyle
        AnimationStyleBuilder animStBl = new AnimationStyleBuilder();
        animStBl.setFadeAnimationType(AnimationType.ANIMATION_TYPE_SMOOTHSTEP);
        animStBl.setSizeAnimationType(AnimationType.ANIMATION_TYPE_SPRING);
        animStBl.setPhaseInDuration(0.5f);
        animStBl.setPhaseOutDuration(0.5f);
        animSt = animStBl.buildStyle();

        // Creating marker style. We should use an object of type MarkerStyleCreator, set all features on it
        // and then call buildStyle method on it. This method returns an object of type MarkerStyle
        MarkerStyleBuilder markStCr = new MarkerStyleBuilder();
        markStCr.setSize(30f);
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker_blue)));
        // AnimationStyle object - that was created before - is used here
        markStCr.setAnimationStyle(animSt);
        MarkerStyle markSt = markStCr.buildStyle();

        // Creating marker
        marker = new Marker(loc, markSt).setTitle(title);
        // Setting a metadata on marker, here we have an id for each marker

        // Adding marker to map!
        map.addMarker(marker);
    }

    // change selected marker color to blue
    private void changeMarkerToBlue(Marker redMarker) {
        // create new marker style
        MarkerStyleBuilder markStCr = new MarkerStyleBuilder();
        markStCr.setSize(30f);
        // Setting a new bitmap as marker
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker_blue)));
        markStCr.setAnimationStyle(animSt);
        MarkerStyle blueMarkSt = markStCr.buildStyle();

        // changing marker style using setStyle
        redMarker.setStyle(blueMarkSt);
    }

    // change deselected marker color to red
    private void changeMarkerToRed(Marker blueMarker) {
        // create new marker style
        MarkerStyleBuilder markStCr = new MarkerStyleBuilder();
        markStCr.setSize(30f);
        // Setting a new bitmap as marker
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker)));
        markStCr.setAnimationStyle(animSt);
        MarkerStyle redMarkSt = markStCr.buildStyle();

        // changing marker style using setStyle
        blueMarker.setStyle(redMarkSt);
    }

    // deselect marker and collapsing bottom sheet
    private void deselectMarker(final Marker deselectMarker) {
        collapseBottomSheet();
        changeMarkerToBlue(deselectMarker);
        selectedMarker = null;
    }

    // select marker and expanding bottom sheet
    private void selectMarker(final Marker selectMarker) {
        expandBottomSheet();
        changeMarkerToRed(selectMarker);
        selectedMarker = selectMarker;
    }

    private void collapseBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void expandBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    // customize back button for when a marker is selected
    @Override
    public void onBackPressed() {
        if (selectedMarker != null) {
            deselectMarker(selectedMarker);
        } else {
            super.onBackPressed();
        }
    }
}
