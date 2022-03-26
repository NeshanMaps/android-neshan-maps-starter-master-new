package org.neshan.sample.starter.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.neshan.common.model.LatLng;
import org.neshan.mapsdk.MapView;
import org.neshan.sample.starter.R;
import org.neshan.sample.starter.model.address.NeshanAddress;
import org.neshan.sample.starter.network.RetrofitClientInstance;
import org.neshan.sample.starter.network.ReverseService;

import io.reactivex.subjects.PublishSubject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APIRetrofit extends AppCompatActivity {

    public static final ReverseService getDataService = RetrofitClientInstance.getRetrofitInstance().create(ReverseService.class);
    private final PublishSubject<LatLng> locationPublishSubject = PublishSubject.create();

    //ui elements
    private TextView addressTitle;
    private ProgressBar progressBar;
    private MapView map;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_api_retrofit);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // everything related to ui is initialized here
        initLayoutReferences();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void initLayoutReferences() {
        // Initializing views
        initViews();
        // Initializing mapView element
        initMap();

    }

    // We use findViewByID for every element in our layout file here
    private void initViews() {
        map = findViewById(R.id.map);
        addressTitle = findViewById(R.id.addressTitle);
        progressBar = findViewById(R.id.progressBar);
    }

    // Initializing map
    private void initMap() {
        // Setting map focal position to a fixed position and setting camera zoom
        map.moveCamera(new LatLng(35.767234, 51.330743), 0);
        map.setZoom(14, 0);

        map.setOnCameraMoveFinishedListener(i -> {
            runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));
            getReverseApi(map.getCameraTargetPosition());
        });
    }

    private void getReverseApi(LatLng currentLocation) {
        getDataService.getReverse(currentLocation.getLatitude(), currentLocation.getLongitude()).enqueue(new Callback<NeshanAddress>() {
            @Override
            public void onResponse(Call<NeshanAddress> call, Response<NeshanAddress> response) {
                String address = response.body().getAddress();
                if (address != null && !address.isEmpty()) {
                    addressTitle.setText(address);
                } else {
                    addressTitle.setText("معبر بی‌نام");
                }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<NeshanAddress> call, Throwable t) {
                addressTitle.setText("معبر بی‌نام");
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

}
