package fpt.life.finalproject.screen.register.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fpt.life.finalproject.R;
import fpt.life.finalproject.adapter.HobbyAdapter;
import fpt.life.finalproject.adapter.RecyclerItemSelectedListener;
import fpt.life.finalproject.dto.register.RegistrationProfile;
import fpt.life.finalproject.model.Hobby;
import fpt.life.finalproject.util.ButtonUtil;

public class RegisterHobbiesFragment extends Fragment implements RecyclerItemSelectedListener {

    private View view;

    private static final int NUM_OF_HOBBIES_MIN = 1;
    private static final int NUM_OF_HOBBIES_MAX = 5;

    private final List<String> hobbyList = Arrays.asList(
            "Cycling", "Outdoors", "Walking", "Cooking", "Working out", "Athlete", "Craft Beer",
            "Writer", "Politics", "Climbing", "Foodie", "Art", "Karaoke", "Yoga", "Blogging",
            "Disney", "Surfing", "Soccer", "Dog lover", "Cat lover", "Movies", "Swimming",
            "Hiking", "Running", "Music", "Fashion", "Vlogging", "Astrology", "Coffee", "Instagram",
            "DIY", "Board Games", "Environmentalism", "Dancing", "Volunteering", "Trivia",
            "Reading", "Tea", "Language Exchange", "Shopping", "Wine", "Travel");
    private final List<Hobby> hobbies = new ArrayList<>();
    private final List<String> registerHobbies = new ArrayList<>();

    private RecyclerView recyclerViewRegisterHobbies;
    private ChipGroup chipGroupRegisterHobbies;
    private SearchView searchViewRegisterHobbies;
    private Button buttonContinue;

    private HobbyAdapter hobbyAdapter;

    private RegistrationProfile registrationProfile;

    public RegisterHobbiesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            RegisterHobbiesFragmentArgs args = RegisterHobbiesFragmentArgs.fromBundle(getArguments());
            registrationProfile = args.getRegistrationProfile();
            Log.i("Check Arguments", "onViewCreated: " + registrationProfile.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_register_hobbies, container, false);

        initComponents();
        initRecyclerView();
        initSearchView();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onClickButtonContinue();
    }

    private void initRecyclerView() {
        hobbyAdapter = new HobbyAdapter((ArrayList<Hobby>) hobbies, this);
        recyclerViewRegisterHobbies.setAdapter(hobbyAdapter);
        recyclerViewRegisterHobbies.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerView.ItemDecoration itemDecoration
                = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerViewRegisterHobbies.addItemDecoration(itemDecoration);
    }

    private void initSearchView() {
        searchViewRegisterHobbies.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Hobby> filteredHobbyList = filter(newText);
                //setAdapter((ArrayList<Hobby>) filteredHobbyList);
                hobbyAdapter.filterList((ArrayList<Hobby>) filteredHobbyList);
                return false;
            }
        });
    }

    private List<Hobby> filter(String text) {
        String lowerCaseQuery = text.toLowerCase();
        List<Hobby> filteredHobbyList = new ArrayList<>();
        for (Hobby hobby : hobbies) {
            String hobbyText = hobby.getContent().toLowerCase();
            if (hobbyText.contains(lowerCaseQuery))
                filteredHobbyList.add(hobby);
        }

        return filteredHobbyList;
    }

    private void initComponents() {
        createHobbies();
        recyclerViewRegisterHobbies = view.findViewById(R.id.recycler_view_register_hobbies);
        chipGroupRegisterHobbies = view.findViewById(R.id.chip_group_register_hobbies);
        searchViewRegisterHobbies = view.findViewById(R.id.search_view_register_hobbies);
        buttonContinue = view.findViewById(R.id.button_register_continue_hobbies);
    }

    private void createHobbies() {
        hobbies.clear();
        registerHobbies.clear();
        for (String string : hobbyList) {
            Hobby hobby = Hobby.builder()
                    .content(string)
                    .isSelected(false)
                    .build();
            hobbies.add(hobby);
        }
    }

    @Override
    public void onItemClick(Object object) {
        Hobby hobby = (Hobby) object;
        String color = "#FD4C67";
        ButtonUtil buttonUtil = ButtonUtil.builder().button(buttonContinue).build();

        if (!hobby.isSelected()) {
            Chip chip = new Chip(getContext());
            chip.setText(hobby.getContent());
            chip.setCloseIconVisible(true);
            chip.setCheckable(false);
            chip.setCloseIconResource(R.drawable.ic_close);
            chip.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor(color)));
            chip.setChipStrokeWidth(1.5f);

            chip.setOnCloseIconClickListener(view -> {
                hobby.setSelected(false);
                hobbyAdapter.notifyDataSetChanged();

                chipGroupRegisterHobbies.removeView(chip);
                registerHobbies.remove(hobby.getContent());

                setButtonContinue(buttonUtil);
            });

            chipGroupRegisterHobbies.addView(chip);

            hobby.setSelected(!hobby.isSelected());
            hobbyAdapter.notifyDataSetChanged();
            registerHobbies.add(hobby.getContent());

            setButtonContinue(buttonUtil);
        }
    }

    private void onClickButtonContinue() {
        final NavController navController = Navigation.findNavController(view);

        buttonContinue.setOnClickListener(v -> {
            registrationProfile.setHobbies(registerHobbies);

            RegisterHobbiesFragmentDirections.ActionFragmentRegisterHobbiesToFragmentRegisterPhotos action
                    = RegisterHobbiesFragmentDirections.actionFragmentRegisterHobbiesToFragmentRegisterPhotos(registrationProfile);
            navController.navigate(action);
        });
    }

    private void setButtonContinue(ButtonUtil buttonUtil) {
        buttonUtil.setFilled(checkHobbiesSelected());
        buttonUtil.setButtonWhenFilled();
        buttonContinue.setText(String.format("CONTINUE %d/%d", registerHobbies.size(), NUM_OF_HOBBIES_MAX));
    }

    private boolean checkHobbiesSelected() {
        int hobbiesNum = registerHobbies.size();
        return hobbiesNum >= NUM_OF_HOBBIES_MIN && hobbiesNum <= NUM_OF_HOBBIES_MAX;
    }
}