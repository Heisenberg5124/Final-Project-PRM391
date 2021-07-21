package fpt.life.finalproject.screen.register.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Date;

import fpt.life.finalproject.R;
import fpt.life.finalproject.dto.register.RegistrationProfile;
import fpt.life.finalproject.util.ButtonUtil;

public class RegisterBasicInformationFragment extends Fragment {

    private View view;

    private EditText editTextName;
    private EditText editTextBirthday;
    private EditText editTextBio;
    private TextView textViewWordCounter;
    private Button buttonContinue;

    private final Calendar calendar = Calendar.getInstance();

    private RegistrationProfile registrationProfile;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            textViewWordCounter.setText(String.format("%s/500", s.length()));
        }

        @Override
        public void afterTextChanged(Editable s) {
            ButtonUtil buttonUtil = ButtonUtil.builder()
                    .button(buttonContinue)
                    .filled(ButtonUtil.isEditTextFilled(editTextName, editTextBirthday, editTextBio))
                    .build();
            buttonUtil.setButtonWhenFilled();
        }
    };


    public RegisterBasicInformationFragment() {
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
        view = inflater.inflate(R.layout.fragment_register_basic_information, container, false);
        initComponents();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onClickButtonContinue();
    }

    private void initComponents() {
        editTextName = view.findViewById(R.id.edit_text_name);
        editTextBirthday = view.findViewById(R.id.edit_text_birthday);
        editTextBio = view.findViewById(R.id.edit_text_bio);
        textViewWordCounter = view.findViewById(R.id.text_view_word_counter);
        buttonContinue = view.findViewById(R.id.button_register_continue_basic_information);

        editTextBirthday.setOnClickListener(v -> {
            setDate(editTextBirthday);
        });

        addTextChangedListenerForEditText(editTextName, editTextBirthday, editTextBio);
    }

    private void setDate(EditText editText) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    String dateShow = String.format("%02d-%02d-%04d", dayOfMonth, month + 1, year);
                    editText.setText(dateShow);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }

    private void addTextChangedListenerForEditText(EditText ...editTexts) {
        for (EditText editText : editTexts)
            editText.addTextChangedListener(textWatcher);
    }

    private void onClickButtonContinue() {
        final NavController navController = Navigation.findNavController(view);

        buttonContinue.setOnClickListener(v -> {
            registrationProfile = RegistrationProfile.builder()
                    .uid(FirebaseAuth.getInstance().getUid())
                    .name(editTextName.getText().toString())
                    .birthday(editTextBirthday.getText().toString())
                    .bio(editTextBio.getText().toString())
                    .build();

            RegisterBasicInformationFragmentDirections.ActionFragmentRegisterBasicInformationToFragmentRegisterGender action
                    = RegisterBasicInformationFragmentDirections.actionFragmentRegisterBasicInformationToFragmentRegisterGender(registrationProfile);
            navController.navigate(action);
        });
    }
}