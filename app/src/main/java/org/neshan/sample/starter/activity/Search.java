package org.neshan.sample.starter.activity;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carto.core.ScreenBounds;
import com.carto.core.ScreenPos;
import com.carto.styles.MarkerStyle;
import com.carto.styles.MarkerStyleBuilder;
import com.carto.utils.BitmapUtils;

import org.neshan.common.model.LatLng;
import org.neshan.common.model.LatLngBounds;
import org.neshan.mapsdk.MapView;
import org.neshan.mapsdk.model.Marker;
import org.neshan.sample.starter.R;
import org.neshan.sample.starter.adapter.SearchAdapter;
import org.neshan.servicessdk.search.NeshanSearch;
import org.neshan.servicessdk.search.model.Item;
import org.neshan.servicessdk.search.model.Location;
import org.neshan.servicessdk.search.model.NeshanSearchResult;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Search extends AppCompatActivity implements SearchAdapter.OnSearchItemListener {

    private static final String TAG = "Search";
    private EditText editText;
    private RecyclerView recyclerView;
    private List<Item> items;
    private SearchAdapter adapter;

    // map UI element
    private MapView map;
    private Marker centerMarker;
    private ArrayList<Marker> markers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // everything related to ui is initialized here
        initLayoutReferences();
    }

    @Override
    public void onSeachItemClick(LatLng LatLng) {
        closeKeyBoard();
        clearMarkers();
        adapter.updateList(new ArrayList<Item>());
        map.moveCamera(LatLng, 0);
        map.setZoom(16f, 0);
        addMarker(LatLng, 30f);

    }

    // Initializing layout references (views, map and map events)
    private void initLayoutReferences() {
        // Initializing views
        initViews();
        // Initializing mapView element
        initMap();

        //listen for search text change
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                search(s.toString());
                Log.i(TAG, "afterTextChanged: " + s.toString());
            }
        });

    }

    // We use findViewByID for every element in our layout file here
    private void initViews() {
        map = findViewById(R.id.map);
        editText = findViewById(R.id.search_editText);
        recyclerView = findViewById(R.id.recyclerView);
        items = new ArrayList<>();
        adapter = new SearchAdapter(items, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void initMap() {
        // Setting map focal position to a fixed position and setting camera zoom
        LatLng LatLng = new LatLng(35.767234, 51.330743);
        map.moveCamera(LatLng, 0);
        map.setZoom(14f, 0);
        centerMarker = new Marker(LatLng, getCenterMarkerStyle());
        map.addMarker(centerMarker);
    }

    private void search(String term) {
        LatLng searchPosition = map.getCameraTargetPosition();
        updateCenterMarker(searchPosition);
        new NeshanSearch.Builder("YOUR-API-KEY")
                .setLocation(searchPosition)
                .setTerm(term)
                .build().call(new Callback<NeshanSearchResult>() {
                    @Override
                    public void onResponse(Call<NeshanSearchResult> call, Response<NeshanSearchResult> response) {
                        if (response.code() == 403) {
                            Toast.makeText(Search.this, "کلید دسترسی نامعتبر", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (response.body() != null) {
                            NeshanSearchResult result = response.body();
                            items = result.getItems();
                            adapter.updateList(items);
                        }
                    }

                    @Override
                    public void onFailure(Call<NeshanSearchResult> call, Throwable t) {
                        Log.i(TAG, "onFailure: " + t.getMessage());
                        Toast.makeText(Search.this, "ارتباط برقرار نشد!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateCenterMarker(LatLng LatLng) {
        centerMarker.setLatLng(LatLng);
    }

    private MarkerStyle getCenterMarkerStyle() {
        MarkerStyleBuilder markerStyleBuilder = new MarkerStyleBuilder();
        markerStyleBuilder.setSize(50);
        markerStyleBuilder.setBitmap
                (BitmapUtils.createBitmapFromAndroidBitmap
                        (BitmapFactory.decodeResource(getResources(), R.drawable.center_marker)));
        return markerStyleBuilder.buildStyle();
    }

    private Marker addMarker(LatLng LatLng, float size) {
        Marker marker = new Marker(LatLng, getMarkerStyle(size));
        map.addMarker(marker);
        markers.add(marker);
        return marker;
    }

    private MarkerStyle getMarkerStyle(float size) {
        MarkerStyleBuilder styleCreator = new MarkerStyleBuilder();
        styleCreator.setSize(size);
        styleCreator.setBitmap
                (BitmapUtils.createBitmapFromAndroidBitmap
                        (BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker)));
        return styleCreator.buildStyle();
    }

    private void closeKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void showSearchClick(View view) {
        closeKeyBoard();
        adapter.updateList(items);
        clearMarkers();
    }

    private void clearMarkers() {
        for (Marker marker : markers) {
            map.removeMarker(marker);
        }
        markers.clear();
    }

    public void showMarkersClick(View view) {
        adapter.updateList(new ArrayList<Item>());
        closeKeyBoard();
        clearMarkers();
        double minLat = Double.MAX_VALUE;
        double minLng = Double.MAX_VALUE;
        double maxLat = Double.MIN_VALUE;
        double maxLng = Double.MIN_VALUE;
        for (Item item : items) {
            Location location = item.getLocation();
            LatLng latLng = location.getLatLng();
            markers.add(addMarker(latLng, 15f));
            minLat = Math.min(latLng.getLatitude(), minLat);
            minLng = Math.min(latLng.getLongitude(), minLng);
            maxLat = Math.max(latLng.getLatitude(), maxLat);
            maxLng = Math.max(latLng.getLongitude(), maxLng);
        }

        if (items.size() > 0) {
            map.moveToCameraBounds(new LatLngBounds(new LatLng(minLat, minLng), new LatLng(maxLat, maxLng)),
                    new ScreenBounds(new ScreenPos(0, 0), new ScreenPos(map.getWidth(), map.getHeight())),
                    true, 0.5f);
        }
    }

}
