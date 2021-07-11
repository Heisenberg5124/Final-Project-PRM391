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
import fpt.life.finalproject.service.LocationService;

public class RegisterLocationFragment extends Fragment {

    private final static int PERMISSION_LOCATION = 1000;

    private View view;

    private Button buttonLocation;

    private LocationService locationService;

    private String uid;

    public RegisterLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            RegisterLocationFragmentArgs args = RegisterLocationFragmentArgs.fromBundle(getArguments());
            uid = args.getUserUid();
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
        locationService = new LocationService(getContext(), uid);

        buttonLocation.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);
            } else {
                locationService.showLocation();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationService.showLocation();
            } else {
                Log.d("CheckLocation", "onRequestPermissionsResult: ");
            }
        }
    }
}