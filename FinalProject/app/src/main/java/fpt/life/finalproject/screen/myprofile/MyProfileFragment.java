package fpt.life.finalproject.screen.myprofile;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fpt.life.finalproject.MainActivity;
import fpt.life.finalproject.R;
import fpt.life.finalproject.dto.MyProfile;
import fpt.life.finalproject.screen.Login.Login_Activity;
import fpt.life.finalproject.screen.Login.Profile_Activity;
import fpt.life.finalproject.service.MyProfileService;

public class MyProfileFragment extends Fragment {
    private EditText birthday;
    private Button logout;
    private Button editImage;
    private View rootView;
    private MyProfile myProfile;
    MyProfileService myProfileService = new MyProfileService();
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

        birthday = (EditText) rootView.findViewById(R.id.birthday_picker);
        logout = (Button) rootView.findViewById(R.id.btn_logout_profile);
        editImage = (Button) rootView.findViewById(R.id.btn_edit_image);
        myProfile = getArguments().getParcelable("myProfile");
        inputBirthday();
        setSpinner();
        addGroupHobby(addHobbies());
        setAgeRangeSlider(28,38);
        setDistanceSlider(20);
        logOut(logout);
        editImage(editImage);
        return rootView;
    }

    private void editImage(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Chua lam",Toast.LENGTH_LONG).show();//TODO: Action Edit Image
            }
        });
    }


    private void logOut(Button logout){
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Chua lam",Toast.LENGTH_LONG).show();//TODO: Action Logout
                signOut();
            }
        });
    }


    private void setSpinner(){
        Spinner spinnerGender = (Spinner) rootView.findViewById(R.id.spinner_Gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.gender, R.layout.custom_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);
        Spinner spinnerShowMe = (Spinner) rootView.findViewById(R.id.spinner_ShowMe);
        spinnerShowMe.setAdapter(adapter);
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
    private void setAgeRangeSlider(float cmin, float cmax){
        RangeSlider rangeSlider = rootView.findViewById(R.id.AgeRange);
        TextView textView = (TextView) rootView.findViewById(R.id.txt_age_range_profile);
        textView.setText(String.format("%.0f - %.0f",cmin,cmax));
        rangeSlider.setValues(cmin,cmax);
        rangeSlider.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {
                List<Float> valuesRange = slider.getValues();
                myProfileService.updateRangeAge(Math.round(valuesRange.get(0)),Math.round(valuesRange.get(1)));
            }
        });
        rangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                List<Float> valuesRange = slider.getValues();
                textView.setText(String.format("%.0f - %.0f",valuesRange.get(0),valuesRange.get(1)));
            }
        });
    }


    @SuppressLint("DefaultLocale")
    private void setDistanceSlider(float cdistance){
        RangeSlider rangeSlider = rootView.findViewById(R.id.distance);
        TextView textView = (TextView) rootView.findViewById(R.id.txt_distance_profile);
        textView.setText(String.format("%.0f",cdistance));
        rangeSlider.setValues(cdistance);
        rangeSlider.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {
                List<Float> valuesRange = slider.getValues();
                myProfileService.updateDistance("rangeDistance",Math.round(valuesRange.get(0)));

            }
        });
        rangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                List<Float> valuesRange = slider.getValues();
                textView.setText(String.format("%.0f",valuesRange.get(0)));
            }
        });
    }


    private void inputBirthday(){
        final Calendar c = Calendar.getInstance();
        //TODO: change to birthday of my profile
        c.setTime(myProfile.getBirthday());
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DATE); // current day
        birthday.setText(mDay + "/" + (mMonth) + "/" + mYear);
        birthday.setOnClickListener(v ->{
            setDate(birthday,mYear,mMonth,mDay);
        });
    }


    public void setDate(EditText tv,int mYear, int mMonth,int mDay){
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        tv.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        myProfileService.updateBirthDay("birthday",myProfileService.dateToTimeStamp(year,monthOfYear,dayOfMonth));
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }


    private ArrayList<String> addHobbies(){
        ArrayList<String> s = new ArrayList<>();
        s.add("Swim");
        s.add("Game");
        s.add("Dance");
        s.add("Book");
        s.add("Food");
        return s;
    }
    private ArrayList<String> addHobbiesChange(){
        ArrayList<String> s = new ArrayList<>();
        s.add("Drug");
        s.add("Drunk");
        s.add("DmQuan");
        s.add("Woman");
        s.add("Money");
        return s;
    }

//    private void mockData(){
//        MyProfile myProfile = new MyProfile("Z7sJqYLoCbhxGPU8dzX4VXehaZe2","Anh Quan","10/05/2021","Male","Female",addHobbies(),"Lorem ipsum dolor sit amet, consectetur adipiscing elit");
//    }

    private void addGroupHobby(ArrayList<String> hobbies){
        ChipGroup chipGroup = (ChipGroup) rootView.findViewById(R.id.chipgroup_hobbies_myprofile);
        for (String s : hobbies) {
            Chip chip = new Chip(getContext());
            chip.setTextSize(18);
            chip.setText(s);
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myProfileService.updateHobbies(addHobbiesChange());
                }
            });
            chipGroup.addView(chip);
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