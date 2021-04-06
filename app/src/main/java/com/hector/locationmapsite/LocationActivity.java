package com.hector.locationmapsite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.location.Location;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.framework.common.ContextCompat;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsResponse;
import com.huawei.hms.location.LocationSettingsStatusCodes;
import com.huawei.hms.location.SettingsClient;

import java.util.List;

public class LocationActivity extends AppCompatActivity {
    // Define a location provider client.
    private FusedLocationProviderClient fusedLocationProviderClient;
    // Define a device setting client.
    private SettingsClient settingsClient;
    LocationCallback mLocationCallback;
    LocationRequest mLocationRequest;

    String TAG = "LocationActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        // Create a fusedLocationProviderClient object.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Create a settingsClient object.
        settingsClient = LocationServices.getSettingsClient(this);

        mLocationRequest = new LocationRequest();
        // Set the interval for requesting location updates, in milliseconds.
        mLocationRequest.setInterval(10000);
        // Set the priority of the request.
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(checkLocationPermission()){
            requestLocationUpdatesWithCallback();
        }

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    List<Location> locations = locationResult.getLocations();
                    if (!locations.isEmpty()) {
                        for (Location location : locations) {
                            Log.i(TAG,"onLocationResult location[Longitude,Latitude,Accuracy]:" + location.getLongitude() + "," + location.getLatitude() + "," + location.getAccuracy());
                        }
                    }
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                if (locationAvailability != null) {
                    boolean flag = locationAvailability.isLocationAvailable();
                    Log.i(TAG, "onLocationAvailability isLocationAvailable:" + flag);
                }
            }
        };
    }

    Boolean checkLocationPermission(){
        boolean ACF = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        boolean ACL = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        return (ACF && ACL);
    }

    private void requestLocationUpdatesWithCallback() {
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();
            // Check device settings before requesting location updates.
            settingsClient.checkLocationSettings(locationSettingsRequest).addOnSuccessListener((OnSuccessListener) locationSettingsResponse -> {
                        Log.i(TAG, "check location settings success");
                        // Request location updates.
                        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper()).addOnSuccessListener((OnSuccessListener) aVoid -> Log.i(TAG, "requestLocationUpdatesWithCallback onSuccess"))
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.e(TAG, "requestLocationUpdatesWithCallback onFailure:" + e.getMessage());
                                    }
                                });
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Log.e(TAG, "checkLocationSetting onFailure:" + e.getMessage());
                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    /*try {
                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        rae.startResolutionForResult(RequestLocationUpdatesWithCallbackActivity.this, 0);
                                    } catch (IntentSender.SendIntentException sie) {
                                        Log.e(TAG, "PendingIntent unable to execute request.");
                                    }*/
                                    break;
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "requestLocationUpdatesWithCallback exception:" + e.getMessage());
        }
    }

    private void removeLocationUpdatesWithCallback() {
        try {
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
                    .addOnSuccessListener((OnSuccessListener) aVoid -> Log.i(TAG, "removeLocationUpdatesWithCallback onSuccess"))
                    .addOnFailureListener(e -> Log.e(TAG, "removeLocationUpdatesWithCallback onFailure:" + e.getMessage()));
        } catch (Exception e) {
            Log.e(TAG, "removeLocationUpdatesWithCallback exception:" + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: apply LOCATION PERMISSION successful");
                requestLocationUpdatesWithCallback();
            } else {
                Log.i(TAG, "onRequestPermissionsResult: apply LOCATION PERMISSSION  failed");
            }
        }

        if (requestCode == 2) {
            if (grantResults.length > 2 && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: apply ACCESS_BACKGROUND_LOCATION successful");
            } else {
                Log.i(TAG, "onRequestPermissionsResult: apply ACCESS_BACKGROUND_LOCATION  failed");
            }
        }
    }
}