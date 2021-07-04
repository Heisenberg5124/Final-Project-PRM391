package fpt.life.finalproject.screen.register.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import fpt.life.finalproject.R;
import fpt.life.finalproject.dto.register.RegistrationProfile;
import fpt.life.finalproject.util.ButtonUtil;

public class RegisterShowMeFragment extends Fragment
        implements ChipGroup.OnCheckedChangeListener {

    private View view;

    private ChipGroup chipGroupShowMe;
    private Button buttonContinue;

    private RegistrationProfile registrationProfile;
    private String showMe;

    public RegisterShowMeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            RegisterShowMeFragmentArgs args = RegisterShowMeFragmentArgs.fromBundle(getArguments());
            registrationProfile = args.getRegistrationProfile();
            Log.i("Check Arguments", "onViewCreated: " + registrationProfile.toString());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onClickButtonContinue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_register_show_me, container, false);
        initComponents();
        return view;
    }

    private void initComponents() {
        chipGroupShowMe = view.findViewById(R.id.chip_group_register_show_me);
        buttonContinue = view.findViewById(R.id.button_register_continue_show_me);

        chipGroupShowMe.setOnCheckedChangeListener(this);
    }

    private void onClickButtonContinue() {
        final NavController navController = Navigation.findNavController(view);

        buttonContinue.setOnClickListener(v -> {
            registrationProfile.setShowMeGender(showMe);

            RegisterShowMeFragmentDirections.ActionFragmentRegisterShowMeToFragmentRegisterHobbies action
                    = RegisterShowMeFragmentDirections.actionFragmentRegisterShowMeToFragmentRegisterHobbies(registrationProfile);
            navController.navigate(action);
        });
    }

    @Override
    public void onCheckedChanged(ChipGroup group, int checkedId) {
        Chip chip = group.findViewById(checkedId);
        boolean isSelected = ButtonUtil.isChipGroupSelected(checkedId);

        showMe = isSelected
                ? chip.getText().toString()
                : null;

        ButtonUtil buttonUtil = ButtonUtil.builder()
                .button(buttonContinue)
                .filled(isSelected)
                .build();
        buttonUtil.setButtonWhenFilled();
    }
}