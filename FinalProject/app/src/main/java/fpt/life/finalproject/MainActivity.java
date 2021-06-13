package fpt.life.finalproject;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import fpt.life.finalproject.screen.homepage.HomepageFragment;
import fpt.life.finalproject.screen.matched.MatchedFragment;
import fpt.life.finalproject.screen.myprofile.MyProfileFragment;

public class MainActivity extends AppCompatActivity {

    ImageView profileImageView;
    ImageView matchedImageView;
    ImageView logoImageView;

    MyProfileFragment myProfileFragment;
    MatchedFragment matchedFragment;
    HomepageFragment homepageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView();
        initFragment();

        profileImageView.setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.frame_layout_main_fragment, myProfileFragment)
                    .commit();
            profileImageView.setColorFilter(Color.rgb(253, 76, 103));
            matchedImageView.setColorFilter(Color.rgb(87, 87, 87));
        });

        matchedImageView.setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.frame_layout_main_fragment, matchedFragment)
                    .commit();
            profileImageView.setColorFilter(Color.rgb(87, 87, 87));
            matchedImageView.setColorFilter(Color.rgb(253, 76, 103));
        });

        logoImageView.setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.frame_layout_main_fragment, homepageFragment)
                    .commit();
            profileImageView.setColorFilter(Color.rgb(87, 87, 87));
            matchedImageView.setColorFilter(Color.rgb(87, 87, 87));
        });
    }

    private void findView() {
        profileImageView = findViewById(R.id.image_view_profile);
        matchedImageView = findViewById(R.id.image_view_matched);
        logoImageView = findViewById(R.id.image_view_logo);
    }

    private void initFragment() {
        myProfileFragment = new MyProfileFragment();
        matchedFragment = new MatchedFragment();
        homepageFragment = new HomepageFragment();

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.frame_layout_main_fragment, homepageFragment)
                .commit();
    }
}