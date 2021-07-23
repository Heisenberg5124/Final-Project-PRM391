package fpt.life.finalproject.screen.register.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import fpt.life.finalproject.MainActivity;
import fpt.life.finalproject.R;
import fpt.life.finalproject.service.LocationService;
import fpt.life.finalproject.service.OnUpdateLocationFirebaseListener;
import fpt.life.finalproject.service.RegisterService;

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
        locationService = new LocationService(getActivity(), uid, new OnUpdateLocationFirebaseListener() {
            @Override
            public void onCompleteUpdateLocation() {

            }
        });

        buttonLocation.setOnClickListener(view -> {
            locationService.getLastKnownLocation();
            Context context = getContext();
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("uid", uid);
            context.startActivity(intent);
            ((Activity) context).finish();
        });
    }
}