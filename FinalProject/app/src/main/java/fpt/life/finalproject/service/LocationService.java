package fpt.life.finalproject.service;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class LocationService {

    private DocumentReference documentReference;

    private Context context;
    private String uid;

    public LocationService(Context context, String uid) {
        this.context = context;
        this.uid = uid;
        this.documentReference = FirebaseFirestore.getInstance().collection("users")
                .document(uid);
    }

    public void showLocation() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("CheckLocation", "Loading location...");
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.d("CheckLocation", "CheckLocation: " + location.getLatitude() + "_" + location.getLongitude());
            documentReference.update("location", new GeoPoint(location.getLatitude(), location.getLongitude()))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("CheckLocation", "onComplete: " + location.getLatitude() + "_" + location.getLongitude());
                        }
                    });
        } else {
            Log.d("CheckLocation", "Please enable GPS");
        }
    }
}
