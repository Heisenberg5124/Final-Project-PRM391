package fpt.life.finalproject.screen.register.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import fpt.life.finalproject.R;
import fpt.life.finalproject.service.BaseGpsListener;

public class RegisterLocationFragment extends Fragment implements BaseGpsListener {

    private final static int PERMISSION_LOCATION = 1000;

    private View view;

    private Button buttonLocation;

    public RegisterLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            RegisterLocationFragmentArgs args = RegisterLocationFragmentArgs.fromBundle(getArguments());
            String uid = args.getUserUid();
            Log.i("Check Arguments", "onViewCreated: " + uid);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_register_location, container, false);

        initComponents();

        return view;
    }

    private void initComponents() {
        buttonLocation = view.findViewById(R.id.button_register_allow_location);

        buttonLocation.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);
            } else {
                showLocation();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showLocation();
            } else {
                Log.d("CheckLocation", "onRequestPermissionsResult: ");
            }
        }
    }

    private void showLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("CheckLocation", "Loading location...");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } else {
            Log.d("CheckLocation", "Please enable GPS");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("CheckLocation", "CheckLocation: " + location.getLatitude() + "_" + location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onGpsStatusChanged(int event) {

    }
}