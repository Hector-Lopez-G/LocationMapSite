package com.hector.locationmapsite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.MapsInitializer;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.MarkerOptions;
import com.huawei.hms.maps.model.PointOfInterest;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.AddressDetail;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.CoordinateBounds;
import com.huawei.hms.site.api.model.Poi;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;
import com.huawei.hms.site.api.model.TextSearchRequest;
import com.huawei.hms.site.api.model.TextSearchResponse;

import java.util.Arrays;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapViewDemoActivity";
    // Huawei map.
    private HuaweiMap hMap;

    private MapView mMapView;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain a MapView instance.
        mMapView = findViewById(R.id.mapView);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        /*
        String apiKey = AGConnectServicesConfig.fromContext(this).getString("client/api_key");
        MapsInitializer.setApiKey(apiKey);*/
        mMapView.onCreate(mapViewBundle);
        // Obtain a map instance.
        mMapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(HuaweiMap map) {
        // Obtain a map instance from callback.
        Log.d(TAG, "onMapReady: ");
        hMap = map;
        hMap.getUiSettings().setCompassEnabled(true);
        hMap.getUiSettings().setAllGesturesEnabled(true);
        hMap.getUiSettings().setZoomControlsEnabled(true);
        hMap.getUiSettings().setCompassEnabled(true);
        hMap.getUiSettings().setMyLocationButtonEnabled(true);

        LatLng amsterdam = new LatLng(52.37, 4.90);
        hMap.addMarker(new MarkerOptions().position(amsterdam).title("Amsterdam"));
        hMap.animateCamera(CameraUpdateFactory.newLatLngZoom(amsterdam, 8));

        hMap.setOnPoiClickListener(new HuaweiMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest pointOfInterest) {
                Log.i(TAG, pointOfInterest.toString());
                Toast.makeText(getApplicationContext(), "Name: " + pointOfInterest.name + ", Lat: " + pointOfInterest.latLng.latitude + ", Long: " + pointOfInterest.latLng.longitude , Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}