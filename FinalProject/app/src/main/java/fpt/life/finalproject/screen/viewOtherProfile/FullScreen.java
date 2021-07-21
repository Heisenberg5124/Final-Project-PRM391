package fpt.life.finalproject.screen.viewOtherProfile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import fpt.life.finalproject.R;

public class FullScreen extends AppCompatActivity {

    ViewPager viewPager;
    ArrayList<String> images;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        if (savedInstanceState==null){
            Intent i = getIntent();
            images = i.getStringArrayListExtra("images");
            images.remove(0);
            position = i.getIntExtra("position", 0);
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        FullSizeAdapter fullSizeAdapter = new FullSizeAdapter(this, images);
        viewPager.setAdapter(fullSizeAdapter);
        viewPager.setCurrentItem(position, true);
    }
}