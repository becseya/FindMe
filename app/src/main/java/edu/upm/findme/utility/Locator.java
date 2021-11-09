package edu.upm.findme.utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class Locator extends LocationCallback implements MenuManager.SwitchStateChangeListener {

    FusedLocationProviderClient locationClient;
    Locator.LocationHandler handler;
    boolean running;

    public Locator(Context context, Locator.LocationHandler handler) {
        this.locationClient = LocationServices.getFusedLocationProviderClient(context);
        this.handler = handler;
        this.running = false;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onSwitchChange(boolean gpsIsOn) {
        this.running = gpsIsOn;

        if (gpsIsOn) {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(5000);
            locationClient.requestLocationUpdates(locationRequest, this, Looper.getMainLooper());
        } else {
            locationClient.removeLocationUpdates(this);
            handler.onNewLocation(null);
        }
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        if (locationResult != null)
            handler.onNewLocation(locationResult.getLastLocation());
    }

    public boolean isRunning() {
        return running;
    }

    public interface LocationHandler {
        // null means the service has stooped
        void onNewLocation(Location location);
    }
}
