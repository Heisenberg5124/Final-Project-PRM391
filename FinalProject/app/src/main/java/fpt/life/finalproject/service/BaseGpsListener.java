package fpt.life.finalproject.service;

import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public interface BaseGpsListener extends LocationListener, GpsStatus.Listener {

    void onLocationChanged(Location location);
    void onProviderDisabled(String provider);
    void onProviderEnabled(String provider);
    void onStatusChanged(String provider, int status, Bundle extras);
    void onGpsStatusChanged(int event);
}
