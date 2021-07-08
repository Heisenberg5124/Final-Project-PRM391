package fpt.life.finalproject.screen.register.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fpt.life.finalproject.R;

public class RegisterLocationFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_register_location, container, false);
    }
}