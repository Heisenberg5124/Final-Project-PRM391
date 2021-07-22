package fpt.life.finalproject.screen.myprofile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.SearchView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fpt.life.finalproject.MainActivity;
import fpt.life.finalproject.R;
import fpt.life.finalproject.adapter.HobbyAdapter;
import fpt.life.finalproject.adapter.RecyclerItemSelectedListener;
import fpt.life.finalproject.model.Hobby;
import fpt.life.finalproject.util.ButtonUtil;

public class EditHobbiesActivity extends AppCompatActivity implements RecyclerItemSelectedListener {
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
    private final ArrayList<String> editHobbies = new ArrayList<>();

    private RecyclerView recyclerViewEditHobbies;
    private ChipGroup chipGroupEditHobbies;
    private SearchView searchViewEditHobbies;
    private Button buttonDone;
    private HobbyAdapter hobbyAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hobbies);

        initComponents();
        initRecyclerView();
        selectedChip();
        initSearchView();
        onClickButtonDone();
    }
    private void onClickButtonDone() {
        buttonDone.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("editedHobbies",editHobbies);
            setResult(RESULT_OK,intent);
            finish();
        });
    }
    private void selectedChip(){
        editHobbies.addAll((ArrayList<String>) this.getIntent().getSerializableExtra("hobbies"));
        ButtonUtil buttonUtil = ButtonUtil.builder().button(buttonDone).build();
        LayoutInflater inflater = LayoutInflater.from(this);
        String color = "#FD4C67";
        for (String stringEditHobbies : editHobbies) {
            Chip chip = new Chip(inflater.getContext());
            chip.setCloseIconVisible(true);
            chip.setCheckable(false);
            chip.setCloseIconResource(R.drawable.ic_close);
            chip.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor(color)));
            chip.setChipStrokeWidth(1.5f);
            chip.setText(stringEditHobbies);
            chipGroupEditHobbies.addView(chip);
            for (Hobby hobby : hobbies) {
                if (hobby.getContent().equals(stringEditHobbies)){
                    hobby.setSelected(true);
                    chip.setOnCloseIconClickListener(view -> {
                        hobby.setSelected(false);
                        hobbyAdapter.notifyDataSetChanged();

                        chipGroupEditHobbies.removeView(chip);
                        editHobbies.remove(hobby.getContent());
                        setButtonDone(buttonUtil);
                    });
                    hobby.setSelected(hobby.isSelected());
                }
            }
            hobbyAdapter.notifyDataSetChanged();
        }
    }
    private void initComponents() {
        createHobbies();
        recyclerViewEditHobbies = findViewById(R.id.recycler_view_edit_hobbies);
        chipGroupEditHobbies = findViewById(R.id.chip_group_edit_hobbies);
        searchViewEditHobbies = findViewById(R.id.search_view_edit_hobbies);
        buttonDone = findViewById(R.id.button_edit_done_hobbies);
    }

    private void createHobbies() {
        hobbies.clear();
        editHobbies.clear();
            for (String string : hobbyList) {
                Hobby hobby = Hobby.builder()
                        .content(string)
                        .isSelected(false)
                        .build();
                hobbies.add(hobby);
        }
    }
    private void initRecyclerView() {
        hobbyAdapter = new HobbyAdapter((ArrayList<Hobby>) hobbies, this);
        recyclerViewEditHobbies.setAdapter(hobbyAdapter);
        recyclerViewEditHobbies.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        RecyclerView.ItemDecoration itemDecoration
                = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerViewEditHobbies.addItemDecoration(itemDecoration);
    }
    private void initSearchView() {
        searchViewEditHobbies.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Hobby> filteredHobbyList = filter(newText);
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

    @Override
    public void onItemClick(Object object) {
        Hobby hobby = (Hobby) object;
        String color = "#FD4C67";
        ButtonUtil buttonUtil = ButtonUtil.builder().button(buttonDone).build();

        if (!hobby.isSelected()) {
            LayoutInflater inflater = LayoutInflater.from(this);
            Chip chip = new Chip(inflater.getContext());
            chip.setText(hobby.getContent());
            chip.setCloseIconVisible(true);
            chip.setCheckable(false);
            chip.setCloseIconResource(R.drawable.ic_close);
            chip.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor(color)));
            chip.setChipStrokeWidth(1.5f);

            chip.setOnCloseIconClickListener(view -> {
                hobby.setSelected(false);
                hobbyAdapter.notifyDataSetChanged();

                chipGroupEditHobbies.removeView(chip);
                editHobbies.remove(hobby.getContent());

                setButtonDone(buttonUtil);
            });

            chipGroupEditHobbies.addView(chip);

            hobby.setSelected(!hobby.isSelected());
            hobbyAdapter.notifyDataSetChanged();
            editHobbies.add(hobby.getContent());

            setButtonDone(buttonUtil);
        }
    }
    private void setButtonDone(ButtonUtil buttonUtil) {
        buttonUtil.setFilled(checkHobbiesSelected());
        buttonUtil.setButtonWhenFilled();
        buttonDone.setText(String.format("DONE %d/%d", editHobbies.size(), NUM_OF_HOBBIES_MAX));
    }

    private boolean checkHobbiesSelected() {
        int hobbiesNum = editHobbies.size();
        return hobbiesNum >= NUM_OF_HOBBIES_MIN && hobbiesNum <= NUM_OF_HOBBIES_MAX;
    }
}