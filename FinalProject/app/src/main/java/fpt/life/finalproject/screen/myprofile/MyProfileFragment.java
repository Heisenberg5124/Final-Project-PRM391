package fpt.life.finalproject.screen.myprofile;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fpt.life.finalproject.MainActivity;
import fpt.life.finalproject.R;
import fpt.life.finalproject.dto.MyProfile;
import fpt.life.finalproject.screen.Login.Login_Activity;
import fpt.life.finalproject.service.MyProfileService;

import static android.app.Activity.RESULT_OK;

public class MyProfileFragment extends Fragment {
    private EditText eTxtBirthday;
    private Button btnLogout;
    private Button btnEditImage;
    private View rootView;
    private MyProfile myProfile;
    private ImageView imageViewAvt;
    private MyProfileService myProfileService;
    private EditText eTxtName;
    private ChipGroup chipGroup;
    private RangeSlider ageRangeSlider;
    private RangeSlider distanceSlider;
    private TextView txtAgeRange;
    private TextView txtDistance;
    private Spinner spinnerShowMe;
    private Spinner spinnerGender;


    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_my_profile, container, false);
        myProfile = getArguments().getParcelable("myProfile");
        initComponent();
        initData();
        return rootView;
    }

    private void initComponent() {
        myProfileService = new MyProfileService(myProfile.getUid());
        imageViewAvt = (ImageView) rootView.findViewById(R.id.imageview_avt_myprofile);
        chipGroup = (ChipGroup) rootView.findViewById(R.id.chipgroup_hobbies_myprofile);
        //Button
        btnLogout = (Button) rootView.findViewById(R.id.btn_logout_profile);
        btnEditImage = (Button) rootView.findViewById(R.id.btn_edit_image);
        //txtDisplay
        txtAgeRange = rootView.findViewById(R.id.txt_age_range_profile);
        txtDistance = rootView.findViewById(R.id.txt_distance_profile);
        eTxtBirthday = (EditText) rootView.findViewById(R.id.birthday_picker);
        eTxtName = (EditText) rootView.findViewById(R.id.eTxtName);
        //Slider
        ageRangeSlider = rootView.findViewById(R.id.AgeRange);
        distanceSlider = rootView.findViewById(R.id.distance);
        //Spinner
        spinnerGender = rootView.findViewById(R.id.spinner_Gender);
        spinnerShowMe = rootView.findViewById(R.id.spinner_ShowMe);
    }

    private void initData() {
        inputBirthday();
        setSpinner();
        addGroupHobby(myProfile.getHobbies());
        setAgeRangeSlider(myProfile.getRangeAge().get("min"), myProfile.getRangeAge().get("max"));
        setDistanceSlider(myProfile.getRangeDistance());
        logOut(btnLogout);
        editImage(btnEditImage);
        setName();
        setAvt();
    }

    private void setAvt() {
        Picasso.get().load(myProfile.getListImage().get(0)).into(imageViewAvt);
    }

    private void editImage(Button button) {
        button.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chua lam", Toast.LENGTH_LONG).show();//TODO: Action Edit Image
        });
    }

    private String checkLengthName(String message) {
        if (message.length() > 14) return message.substring(0, 14) + "...";
        else return message;
    }

    private void setName() {
        eTxtName.setText(checkLengthName(myProfile.getName()));
        eTxtName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String currentName = checkLengthName(myProfile.getName());
                if (!eTxtName.getText().toString().equals("")) {
                    myProfile.setName(eTxtName.getText().toString());
                    myProfileService.updateField("name", eTxtName.getText().toString());
                    eTxtName.setText(checkLengthName(eTxtName.getText().toString()));
                    ((MainActivity) getActivity()).setCurrentUserName(myProfile.getName());
                } else {
                    eTxtName.setText(currentName);
                }
            } else {
                eTxtName.setText(myProfile.getName());
            }
        });
    }

    private void logOut(Button logout) {
        logout.setOnClickListener(v -> signOut());
    }

    private void setSpinner() {
        //Spinner Gender
        ArrayAdapter<CharSequence> adapterGender = ArrayAdapter.createFromResource(getContext(),
                R.array.gender, R.layout.custom_spinner_item);
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapterGender);
        setOnGenderSelect();
        //Spinner Show Me
        ArrayAdapter<CharSequence> adapterShowMe = ArrayAdapter.createFromResource(getContext(),
                R.array.showMe, R.layout.custom_spinner_item);
        adapterShowMe.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShowMe.setAdapter(adapterShowMe);
        setOnShowMeSelect();
    }

    private void setOnShowMeSelect() {
        if (myProfile.getShowMeGender().equalsIgnoreCase("Male"))
            spinnerShowMe.setSelection(0);
        else if (myProfile.getShowMeGender().equalsIgnoreCase("Female"))
            spinnerShowMe.setSelection(1);
        else spinnerShowMe.setSelection(2);
        spinnerShowMe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //TODO: Update performance
                myProfileService.updateField("showMeGender", parent.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setOnGenderSelect() {
        if (myProfile.getGender().equals("Male"))
            spinnerGender.setSelection(0);
        else spinnerGender.setSelection(1);
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //TODO: Update performance
                myProfileService.updateField("gender", parent.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void setAgeRangeSlider(float cmin, float cmax) {
        txtAgeRange.setText(String.format("%.0f - %.0f", cmin, cmax));
        ageRangeSlider.setValues(cmin, cmax);
        ageRangeSlider.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {
                List<Float> valuesRange = slider.getValues();
                myProfileService.updateRangeAge(Math.round(valuesRange.get(0)), Math.round(valuesRange.get(1)));
            }
        });
        ageRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> valuesRange = slider.getValues();
            txtAgeRange.setText(String.format("%.0f - %.0f", valuesRange.get(0), valuesRange.get(1)));
        });
    }


    @SuppressLint("DefaultLocale")
    private void setDistanceSlider(float cdistance) {
        txtDistance.setText(String.format("%.0f", cdistance));
        distanceSlider.setValues(cdistance);
        distanceSlider.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {
                List<Float> valuesRange = slider.getValues();
                myProfileService.updateDistance("rangeDistance", Math.round(valuesRange.get(0)));

            }
        });
        distanceSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> valuesRange = slider.getValues();
            txtDistance.setText(String.format("%.0f", valuesRange.get(0)));
        });
    }


    @SuppressLint("SetTextI18n")
    private void inputBirthday() {
        final Calendar c = Calendar.getInstance();
        c.setTime(myProfile.getBirthday());
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DATE);
        eTxtBirthday.setText(mDay + "/" + (mMonth) + "/" + mYear);
        eTxtBirthday.setOnClickListener(v -> {
            setDate(eTxtBirthday, mYear, mMonth, mDay);
        });
    }

    @SuppressLint("SetTextI18n")
    public void setDate(EditText tv, int mYear, int mMonth, int mDay) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    tv.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    myProfileService.updateBirthDay("birthday", myProfileService.dateToTimeStamp(year, monthOfYear, dayOfMonth));
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }

    private void addGroupHobby(ArrayList<String> hobbies) {
        chipGroup.removeAllViews();
        for (String s : hobbies) {
            Chip chip = new Chip(getContext());
            chip.setTextSize(18);
            chip.setText(s);
            chipGroup.addView(chip);
        }
        chipGroup.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditHobbiesActivity.class);
            intent.putExtra("hobbies", myProfile.getHobbies());
            startActivityForResult(intent, 1);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                myProfile.setHobbies((ArrayList<String>) data.getSerializableExtra("editedHobbies"));
                myProfileService.updateHobbies(myProfile.getHobbies());
                addGroupHobby(myProfile.getHobbies());
            }
        }

    }

    public void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(getActivity())
                .addOnCompleteListener(task -> {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getContext(), Login_Activity.class));
                    getActivity().finish();
                });
        // [END auth_fui_signout]
    }
}