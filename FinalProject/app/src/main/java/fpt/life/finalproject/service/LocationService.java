package fpt.life.finalproject.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.List;

import lombok.Data;
import lombok.SneakyThrows;
import pub.devrel.easypermissions.EasyPermissions;

@Data
public class LocationService {

    private final static int PERMISSION_LOCATION = 1000;

    private DocumentReference documentReference;

    private Context context;
    private String uid;
    private LocationManager locationManager;

    private FusedLocationProviderClient fusedLocationProviderClient;

    public LocationService(Context context, String uid) {
        this.context = context;
        this.uid = uid;
        this.documentReference = FirebaseFirestore.getInstance().collection("users")
                .document(uid);
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    private boolean hasLocationPermission() {
        return EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestLocationPermission() {
        EasyPermissions.requestPermissions((Activity) context,
                "This application cannot work without Location Permission",
                PERMISSION_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public boolean isProviderEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    public void getLastKnownLocation() {
        if (hasLocationPermission()) {
            if (isProviderEnabled()) {
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    Log.d("CheckLocation", "getLastKnownLocation: " + location.getLatitude() + " - " + location.getLongitude());
                    updateLocation(location);
                });
            } else {
                Log.d("CheckLocation", "getLastKnownLocation: " + "Please enable GPS");
                buildAlertMessageNoGps();
            }
        } else {
            requestLocationPermission();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void updateLocation(Location location) {
        if (location != null) {
            documentReference.update("location", new GeoPoint(location.getLatitude(), location.getLongitude()))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @SneakyThrows
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("CheckLocation", "onComplete: " + location.getLatitude() + "_" + location.getLongitude());
                            Log.d("CheckLocation", "onComplete: " + getCityFromLocation(location));
                        }
                    });
        }
    }

    @SneakyThrows
    public String getCityFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(context);
        List<Address> currentLocation = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        return currentLocation.get(0).getAdminArea();
    }
}
